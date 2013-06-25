package com.madrobot.net.client.oauth;

import java.io.IOException;
import java.io.InputStream;

public interface HttpResponse {

    InputStream getContent() throws IOException;

    String getReasonPhrase() throws Exception;

    int getStatusCode() throws IOException;

    /**
     * Returns the underlying response object, in case you need to work on it
     * directly.
     * 
     * @return the wrapped response object
     */
    Object unwrap();
}
