package com.madrobot.net.client.oauth;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpURLConnectionRequestAdapter implements HttpRequest {

    protected HttpURLConnection connection;

    public HttpURLConnectionRequestAdapter(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
	public Map<String, String> getAllHeaders() {
        Map<String, List<String>> origHeaders = connection.getRequestProperties();
        Map<String, String> headers = new HashMap<String, String>(origHeaders.size());
        for (String name : origHeaders.keySet()) {
            List<String> values = origHeaders.get(name);
            if (!values.isEmpty()) {
                headers.put(name, values.get(0));
            }
        }
        return headers;
    }

    @Override
	public String getContentType() {
        return connection.getRequestProperty("Content-Type");
    }

    @Override
	public String getHeader(String name) {
        return connection.getRequestProperty(name);
    }

    @Override
	public InputStream getMessagePayload() throws IOException {
        return null;
    }

    @Override
	public String getMethod() {
        return connection.getRequestMethod();
    }

    @Override
	public String getRequestUrl() {
        return connection.getURL().toExternalForm();
    }

    @Override
	public void setHeader(String name, String value) {
        connection.setRequestProperty(name, value);
    }

    @Override
	public void setRequestUrl(String url) {
        // can't do
    }

    @Override
	public HttpURLConnection unwrap() {
        return connection;
    }
}
