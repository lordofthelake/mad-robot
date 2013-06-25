
package com.madrobot.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


/**
 * A HashMap implementation with weak references values and by default for the key. WHen the
 * value is garbage collected, the key will also vanish from the map.
 * 
 * @since 1.4
 */
public class WeakCache extends AbstractMap {

    private interface Visitor {
        Object visit(Object element);
    }

    private final Map map;

    /**
     * Construct a WeakCache with weak keys.
     * 
     * @param map the map to use
     * @since 1.4
     */
    public WeakCache() {
        this(new WeakHashMap());
    }

    /**
     * Construct a WeakCache.
     * 
     * @param map the map to use
     * @since 1.4
     */
    public WeakCache(Map map) {
        this.map = map;
    }

    @Override
	public void clear() {
        map.clear();
    }

    @Override
	public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
	public boolean containsValue(final Object value) {
        Boolean result = (Boolean)iterate(new Visitor() {

            @Override
			public Object visit(Object element) {
                return element.equals(value) ? Boolean.TRUE : null;
            }

        }, 0);
        return result == Boolean.TRUE;
    }

    protected Reference createReference(Object value) {
        return new WeakReference(value);
    }

    @Override
	public Set entrySet() {
        final Set set = new HashSet();
        if (map.size() != 0) {
            iterate(new Visitor() {

                @Override
				public Object visit(Object element) {
                    final Map.Entry entry = (Map.Entry)element;
                    set.add(new Map.Entry() {

                        @Override
						public Object getKey() {
                            return entry.getKey();
                        }

                        @Override
						public Object getValue() {
                            return ((Reference)entry.getValue()).get();
                        }

                        @Override
						public Object setValue(Object value) {
                            return entry.setValue(createReference(value));
                        }

                    });
                    return null;
                }

            }, 2);
        }
        return set;
    }

    @Override
	public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
	public Object get(Object key) {
        Reference reference = (Reference)map.get(key);
        return reference != null ? reference.get() : null;
    }

    @Override
	public int hashCode() {
        return map.hashCode();
    }

    private Object iterate(Visitor visitor, int type) {
        Object result = null;
        for (Iterator iter = map.entrySet().iterator(); result == null && iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            Reference reference = (Reference)entry.getValue();
            Object element = reference.get();
            if (element == null) {
                iter.remove();
                continue;
            }
            switch (type) {
            case 0:
                result = visitor.visit(element);
                break;
            case 1:
                result = visitor.visit(entry.getKey());
                break;
            case 2:
                result = visitor.visit(entry);
                break;
            }

        }
        return result;
    }

    @Override
	public Set keySet() {
        return map.keySet();
    }

    @Override
	public Object put(Object key, Object value) {
        Reference ref = (Reference)map.put(key, createReference(value));
        return ref == null ? null : ref.get();
    }

    @Override
	public Object remove(Object key) {
        Reference ref = (Reference)map.remove(key);
        return ref == null ? null : ref.get();
    }

    @Override
	public int size() {
        if (map.size() == 0) {
            return 0;
        }
        final int i[] = new int[1];
        i[0] = 0;
        iterate(new Visitor() {

            @Override
			public Object visit(Object element) {
                ++i[0];
                return null;
            }

        }, 0);
        return i[0];
    }

    @Override
	public String toString() {
        return map.toString();
    }

    @Override
	public Collection values() {
        final Collection collection = new ArrayList();
        if (map.size() != 0) {
            iterate(new Visitor() {

                @Override
				public Object visit(Object element) {
                    collection.add(element);
                    return null;
                }

            }, 0);
        }
        return collection;
    }
}
