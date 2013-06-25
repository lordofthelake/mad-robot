package com.madrobot.net.client.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;


class UrlStringRequestAdapter implements HttpRequest {

    private String url;

    public UrlStringRequestAdapter(String url) {
        this.url = url;
    }

    @Override
	public Map<String, String> getAllHeaders() {
        return Collections.emptyMap();
    }

    @Override
	public String getContentType() {
        return null;
    }

    @Override
	public String getHeader(String name) {
        return null;
    }

    @Override
	public InputStream getMessagePayload() throws IOException {
        return null;
    }

    @Override
	public String getMethod() {
        return "GET";
    }

    @Override
	public String getRequestUrl() {
        return url;
    }

    @Override
	public void setHeader(String name, String value) {
    }

    @Override
	public void setRequestUrl(String url) {
        this.url = url;
    }

    @Override
	public Object unwrap() {
        return url;
    }
}
