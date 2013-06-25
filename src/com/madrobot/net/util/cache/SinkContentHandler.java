package com.madrobot.net.util.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.ResponseCache;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A {@link ContentHandler} that consumes the content of a {@link URLConnection}
 * so that it may be captured by a {@link ResponseCache}.
 * <p>
 * If the {@link URLConnection} is providing cached data, the
 * {@link ContentHandler} does nothing.
 */
class SinkContentHandler extends ContentHandler {
    private static final int BUFFER_SIZE = 4096;

    private static List<String> getVia(URLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> via = headerFields.get("via");
        if (via == null) {
            via = Collections.emptyList();
        }
        return via;
    }

    static boolean isViaLocalhost(URLConnection connection) {
        List<String> via = getVia(connection);
        return via.contains("1.0 localhost") || via.contains("1.1 localhost");
    }

    /**
     * Consumes the entire {@link InputStream}.
     */
    private static void skipAll(InputStream input) throws IOException {
        // Read into a buffer to reduce method invocation overhead.
        //
        // Don't use skip() because it's not supported by
        // Android's current HttpURLConnection implementation.
        // Even if skip() was supported, the implementation would
        // probably allocate a buffer internally anyway.
        //
        // It is important to allocate a new buffer for each invocation
        // because this ContentHandler may be used by multiple threads,
        // and HttpURLConnection may read from the buffer when writing
        // to the ResponseCache.
        byte[] buffer = new byte[BUFFER_SIZE];
        while (-1 != input.read(buffer)) {
        }
    }

    @Override
    public Void getContent(URLConnection connection) throws IOException {
        InputStream input = connection.getInputStream();
        if (isViaLocalhost(connection)) {
            // The content is already cached locally
            input.close();
            return null;
        } else {
            // Read the InputStream fully to populate the ResponseCache
            try {
                skipAll(input);
            } finally {
                input.close();
            }
            return null;
        }
    }
}
