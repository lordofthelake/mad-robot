package com.madrobot.net.client.oauth;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


public class HttpURLConnectionResponseAdapter implements HttpResponse {

    private HttpURLConnection connection;

    public HttpURLConnectionResponseAdapter(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
	public InputStream getContent() throws IOException {
        return connection.getInputStream();
    }

    @Override
	public String getReasonPhrase() throws Exception {
        return connection.getResponseMessage();
    }

    @Override
	public int getStatusCode() throws IOException {
        return connection.getResponseCode();
    }

    @Override
	public Object unwrap() {
        return connection;
    }
}
