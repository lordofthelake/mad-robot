package com.madrobot.net.client.oauth;


import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * A multi-map of HTTP request parameters. Each key references a
 * {@link SortedSet} of parameters collected from the request during message
 * signing. Parameter values are sorted as per {@linkplain http
 * ://oauth.net/core/1.0a/#anchor13}. Every key/value pair will be
 * percent-encoded upon insertion. This class has special semantics tailored to
 * being useful for message signing; it's not a general purpose collection class
 * to handle request parameters.
 * 
 */
@SuppressWarnings("serial")
public class HttpParameters implements Map<String, SortedSet<String>>, Serializable {

    private TreeMap<String, SortedSet<String>> wrappedMap = new TreeMap<String, SortedSet<String>>();

    @Override
	public void clear() {
        wrappedMap.clear();
    }

    @Override
	public boolean containsKey(Object key) {
        return wrappedMap.containsKey(key);
    }

    @Override
	public boolean containsValue(Object value) {
        for (Set<String> values : wrappedMap.values()) {
            if (values.contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
	public Set<java.util.Map.Entry<String, SortedSet<String>>> entrySet() {
        return wrappedMap.entrySet();
    }

    @Override
	public SortedSet<String> get(Object key) {
        return wrappedMap.get(key);
    }

    public String getAsHeaderElement(String key) {
        String value = getFirst(key);
        if (value == null) {
            return null;
        }
        return key + "=\"" + value + "\"";
    }

    /**
     * Concatenates all values for the given key to a list of key/value pairs
     * suitable for use in a URL query string.
     * 
     * @param key
     *        the parameter name
     * @return the query string
     */
    public String getAsQueryString(Object key) {
        StringBuilder sb = new StringBuilder();
        key = OAuth.percentEncode((String) key);
        Set<String> values = wrappedMap.get(key);
        if (values == null) {
            return key + "=";
        }
        Iterator<String> iter = values.iterator();
        while (iter.hasNext()) {
            sb.append(key + "=" + iter.next());
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * Convenience method for {@link #getFirst(key, false)}.
     * 
     * @param key
     *        the parameter name (must be percent encoded if it contains unsafe
     *        characters!)
     * @return the first value found for this parameter
     */
    public String getFirst(Object key) {
        return getFirst(key, false);
    }

    /**
     * Returns the first value from the set of all values for the given
     * parameter name. If the key passed to this method contains special
     * characters, you MUST first percent encode it using
     * {@link OAuth#percentEncode(String)}, otherwise the lookup will fail
     * (that's because upon storing values in this map, keys get
     * percent-encoded).
     * 
     * @param key
     *        the parameter name (must be percent encoded if it contains unsafe
     *        characters!)
     * @param percentDecode
     *        whether the value being retrieved should be percent decoded
     * @return the first value found for this parameter
     */
    public String getFirst(Object key, boolean percentDecode) {
        SortedSet<String> values = wrappedMap.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        String value = values.first();
        return percentDecode ? OAuth.percentDecode(value) : value;
    }

    @Override
	public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    @Override
	public Set<String> keySet() {
        return wrappedMap.keySet();
    }

    @Override
	public SortedSet<String> put(String key, SortedSet<String> value) {
        return wrappedMap.put(key, value);
    }

    public SortedSet<String> put(String key, SortedSet<String> values, boolean percentEncode) {
        if (percentEncode) {
            remove(key);
            for (String v : values) {
                put(key, v, true);
            }
            return get(key);
        } else {
            return wrappedMap.put(key, values);
        }
    }

    /**
     * Convenience method to add a single value for the parameter specified by
     * 'key'.
     * 
     * @param key
     *        the parameter name
     * @param value
     *        the parameter value
     * @return the value
     */
    public String put(String key, String value) {
        return put(key, value, false);
    }

    /**
     * Convenience method to add a single value for the parameter specified by
     * 'key'.
     * 
     * @param key
     *        the parameter name
     * @param value
     *        the parameter value
     * @param percentEncode
     *        whether key and value should be percent encoded before being
     *        inserted into the map
     * @return the value
     */
    public String put(String key, String value, boolean percentEncode) {
        SortedSet<String> values = wrappedMap.get(key);
        if (values == null) {
            values = new TreeSet<String>();
            wrappedMap.put(percentEncode ? OAuth.percentEncode(key) : key, values);
        }
        if (value != null) {
            value = percentEncode ? OAuth.percentEncode(value) : value;
            values.add(value);
        }

        return value;
    }

    @Override
	public void putAll(Map<? extends String, ? extends SortedSet<String>> m) {
        wrappedMap.putAll(m);
    }

    public void putAll(Map<? extends String, ? extends SortedSet<String>> m, boolean percentEncode) {
        if (percentEncode) {
            for (String key : m.keySet()) {
                put(key, m.get(key), true);
            }
        } else {
            wrappedMap.putAll(m);
        }
    }

    public void putAll(String[] keyValuePairs, boolean percentEncode) {
        for (int i = 0; i < keyValuePairs.length - 1; i += 2) {
            this.put(keyValuePairs[i], keyValuePairs[i + 1], percentEncode);
        }
    }

    /**
     * Convenience method to merge a Map<String, List<String>>.
     * 
     * @param m
     *        the map
     */
    public void putMap(Map<String, List<String>> m) {
        for (String key : m.keySet()) {
            SortedSet<String> vals = get(key);
            if (vals == null) {
                vals = new TreeSet<String>();
                put(key, vals);
            }
            vals.addAll(m.get(key));
        }
    }

    /**
     * Convenience method to allow for storing null values. {@link #put} doesn't
     * allow null values, because that would be ambiguous.
     * 
     * @param key
     *        the parameter name
     * @param nullString
     *        can be anything, but probably... null?
     * @return null
     */
    public String putNull(String key, String nullString) {
        return put(key, nullString);
    }

    @Override
	public SortedSet<String> remove(Object key) {
        return wrappedMap.remove(key);
    }

    @Override
	public int size() {
        int count = 0;
        for (String key : wrappedMap.keySet()) {
            count += wrappedMap.get(key).size();
        }
        return count;
    }

    @Override
	public Collection<SortedSet<String>> values() {
        return wrappedMap.values();
    }
}
