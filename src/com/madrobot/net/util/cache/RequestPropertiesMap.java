package com.madrobot.net.util.cache;

import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides case-insensitive access to the request properties of a
 * {@link URLConnection} via the {@link Map} interface.
 * <p>
 * The method {@link URLConnection#getRequestProperties()} cannot be called
 * after a connection is established because it is mutable, but
 * {@link URLConnection#getRequestProperty(String)} can be. This class provides
 * the same interface as the object returned by
 * {@link URLConnection#getRequestProperties()}, but calls through to
 * {@link URLConnection#getRequestProperty(String)} so that the request
 * properties can be accessed after a connection has been established.
 * <p>
 * The {@link Map} returned by {@link URLConnection#getRequestProperties()} on
 * Android is case-sensitive, while
 * {@link URLConnection#getRequestProperty(String)} is not.
 */
class RequestPropertiesMap implements Map<String, List<String>> {

    private final URLConnection mConnection;

    public RequestPropertiesMap(URLConnection connection) {
        mConnection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public boolean containsKey(Object key) {
        if (key instanceof String) {
            String field = (String) key;
            return mConnection.getRequestProperty(field) != null;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Set<Entry<String, List<String>>> entrySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public List<String> get(Object key) {
        if (key instanceof String) {
            String field = (String) key;
            String value = mConnection.getRequestProperty(field);
            return value != null ? Collections.singletonList(value) : null;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public List<String> put(String key, List<String> value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void putAll(Map<? extends String, ? extends List<String>> value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public List<String> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public int size() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Collection<List<String>> values() {
        throw new UnsupportedOperationException();
    }
}
