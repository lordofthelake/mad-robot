package com.madrobot.net.client.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;


public class HttpRequestAdapter implements HttpRequest {

    private HttpEntity entity;

    private HttpUriRequest request;

    public HttpRequestAdapter(HttpUriRequest request) {
        this.request = request;
        if (request instanceof HttpEntityEnclosingRequest) {
            entity = ((HttpEntityEnclosingRequest) request).getEntity();
        }
    }

    @Override
	public Map<String, String> getAllHeaders() {
        Header[] origHeaders = request.getAllHeaders();
        HashMap<String, String> headers = new HashMap<String, String>();
        for (Header h : origHeaders) {
            headers.put(h.getName(), h.getValue());
        }
        return headers;
    }

    @Override
	public String getContentType() {
        if (entity == null) {
            return null;
        }
        Header header = entity.getContentType();
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    @Override
	public String getHeader(String name) {
        Header header = request.getFirstHeader(name);
        if (header == null) {
            return null;
        }
        return header.getValue();
    }

    @Override
	public InputStream getMessagePayload() throws IOException {
        if (entity == null) {
            return null;
        }
        return entity.getContent();
    }

    @Override
	public String getMethod() {
        return request.getRequestLine().getMethod();
    }

    @Override
	public String getRequestUrl() {
        return request.getURI().toString();
    }

    @Override
	public void setHeader(String name, String value) {
        request.setHeader(name, value);
    }

    @Override
	public void setRequestUrl(String url) {
        throw new RuntimeException(new UnsupportedOperationException());
    }

    @Override
	public Object unwrap() {
        return request;
    }
}
