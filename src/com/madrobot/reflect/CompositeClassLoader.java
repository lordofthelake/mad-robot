package com.madrobot.reflect;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * ClassLoader that is composed of other classloaders. Each loader will be used to try to load the particular class, until
 * one of them succeeds. <b>Note:</b> The loaders will always be called in the REVERSE order they were added in.
 *
 *
 * <h1>Example</h1>
 * <pre><code>CompositeClassLoader loader = new CompositeClassLoader();
 * loader.add(MyClass.class.getClassLoader());
 * loader.add(new AnotherClassLoader());
 * &nbsp;
 * loader.loadClass("com.blah.ChickenPlucker");
 * </code></pre>
 *
 * <p>The above code will attempt to load a class from the following classloaders (in order):</p>
 *
 * <ul>
 *   <li>AnotherClassLoader (and all its parents)</li>
 *   <li>The classloader for MyClas (and all its parents)</li>
 *   <li>The thread's context classloader (and all its parents)</li>
 *   <li>The classloader for XMLWizard (and all its parents)</li>
 * </ul>
 * 
 * <p>The added classloaders are kept with weak references to allow an application container to reload classes.</p>
 *
 * @since 1.0.3
 */
public class CompositeClassLoader extends ClassLoader {

    private final List<WeakReference<Object>> classLoaders = new ArrayList<WeakReference<Object>>();
    private final ReferenceQueue queue = new ReferenceQueue();

    public CompositeClassLoader() {
        addInternal(Object.class.getClassLoader()); // bootstrap loader.
        addInternal(getClass().getClassLoader()); // whichever classloader loaded this jar.
    }

    /**
     * Add a loader to the n
     * @param classLoader
     */
    public synchronized void add(ClassLoader classLoader) {
        cleanup();
        if (classLoader != null) {
            addInternal(classLoader);
        }
    }

    private void addInternal(ClassLoader classLoader) {
        WeakReference refClassLoader = null;
        for (Iterator iterator = classLoaders.iterator(); iterator.hasNext();) {
            WeakReference ref = (WeakReference) iterator.next();
            ClassLoader cl = (ClassLoader)ref.get();
            if (cl == null) {
                iterator.remove();
            } else if (cl == classLoader) {
                iterator.remove();
                refClassLoader = ref;
            }
        }
        classLoaders.add(0, refClassLoader != null ? refClassLoader : new WeakReference(classLoader, queue));
    }

    private void cleanup() {
        WeakReference ref;
        while ((ref = (WeakReference)queue.poll()) != null)
        {
            classLoaders.remove(ref);
        }
    }

    @Override
	public Class loadClass(String name) throws ClassNotFoundException {
        List copy = new ArrayList(classLoaders.size()) {

            @Override
			public boolean add(Object ref) {
                Object classLoader = ((WeakReference)ref).get();
                if (classLoader != null) {
                    return super.add(classLoader);
                }
                return false;
            }

            @Override
			public boolean addAll(Collection c) {
                boolean result = false;
                for(Iterator iter = c.iterator(); iter.hasNext(); ) {
                    result |= add(iter.next());
                }
                return result;
            }
            
        };
        synchronized(this) {
            cleanup();
            copy.addAll(classLoaders);
        }
        
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        for (Iterator iterator = copy.iterator(); iterator.hasNext();) {
            ClassLoader classLoader = (ClassLoader) iterator.next();
            if (classLoader == contextClassLoader) {
                contextClassLoader = null;
            }
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException notFound) {
                // ok.. try another one
            }
        }
        
        // One last try - the context class loader associated with the current thread. Often used in j2ee servers.
        // Note: The contextClassLoader cannot be added to the classLoaders list up front as the thread that constructs
        // XMLWizard is potentially different to thread that uses it.
        if (contextClassLoader != null) {
            return contextClassLoader.loadClass(name);
        } else {
            throw new ClassNotFoundException(name);
        }
    }
}
