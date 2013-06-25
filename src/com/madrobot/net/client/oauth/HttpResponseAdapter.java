package com.madrobot.net.client.oauth;

import java.io.IOException;
import java.io.InputStream;


public class HttpResponseAdapter implements HttpResponse {

    private org.apache.http.HttpResponse response;

    public HttpResponseAdapter(org.apache.http.HttpResponse response) {
        this.response = response;
    }

    @Override
	public InputStream getContent() throws IOException {
        return response.getEntity().getContent();
    }

    @Override
	public String getReasonPhrase() throws Exception {
        return response.getStatusLine().getReasonPhrase();
    }

    @Override
	public int getStatusCode() throws IOException {
        return response.getStatusLine().getStatusCode();
    }

    @Override
	public Object unwrap() {
        return response;
    }
}
