package com.madrobot.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Wrapper around java.text.SimpleDateFormat that can
 * be called by multiple threads concurrently.
 * <p>SimpleDateFormat has a high overhead in creating
 * and is not thread safe. To make best use of resources,
 * the ThreadSafeSimpleDateFormat provides a dynamically
 * sizing pool of instances, each of which will only
 * be called by a single thread at a time.</p>
 * <p>The pool has a maximum capacity, to limit overhead.
 * If all instances in the pool are in use and another is
 * required, it shall block until one becomes available.</p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class ThreadSafeSimpleDateFormat {

    private final String formatString;
    private final Pool pool;
    private final TimeZone timeZone;

    public ThreadSafeSimpleDateFormat(String format, TimeZone timeZone, int initialPoolSize, int maxPoolSize, final boolean lenient) {
        formatString = format;
        this.timeZone = timeZone;
        pool = new Pool(initialPoolSize, maxPoolSize, new Pool.Factory() {
            @Override
			public Object newInstance() {
                SimpleDateFormat dateFormat = new SimpleDateFormat(formatString, Locale.ENGLISH);
                dateFormat.setLenient(lenient);
                return dateFormat;
            }
            
        });
    }

    private DateFormat fetchFromPool() {
        DateFormat format = (DateFormat)pool.fetchFromPool();
        TimeZone tz = timeZone != null ? timeZone : TimeZone.getDefault();
        if (!tz.equals(format.getTimeZone())) {
            format.setTimeZone(tz);
        }
        return format;
    }

    public String format(Date date) {
        DateFormat format = fetchFromPool();
        try {
            return format.format(date);
        } finally {
            pool.putInPool(format);
        }
    }

    public Date parse(String date) throws ParseException {
        DateFormat format = fetchFromPool();
        try {
            return format.parse(date);
        } finally {
            pool.putInPool(format);
        }
    }

    @Override
	public String toString() {
        return formatString;
    }
}
