package com.madrobot.net.client.oauth;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 * This implementation uses the Apache Commons {@link HttpClient} 4.x HTTP
 * implementation to fetch OAuth tokens from a service provider. Android users
 * should use this provider implementation in favor of the default one, since
 * the latter is known to cause problems with Android's Apache Harmony
 * underpinnings.
 * 
 */
public class CommonsHttpOAuthProvider extends AbstractOAuthProvider {

    private static final long serialVersionUID = 1L;

    private transient HttpClient httpClient;

    public CommonsHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
            String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.httpClient = new DefaultHttpClient();
    }

    public CommonsHttpOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
            String authorizationWebsiteUrl, HttpClient httpClient) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
        this.httpClient = httpClient;
    }

    @Override
    protected void closeConnection(HttpRequest request, com.madrobot.net.client.oauth.HttpResponse response)
            throws Exception {
        if (response != null) {
            HttpEntity entity = ((HttpResponse) response.unwrap()).getEntity();
            if (entity != null) {
                try {
                    // free the connection
                    entity.consumeContent();
                } catch (IOException e) {
                    // this means HTTP keep-alive is not possible
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected HttpRequest createRequest(String endpointUrl) throws Exception {
        HttpPost request = new HttpPost(endpointUrl);
        return new HttpRequestAdapter(request);
    }

    @Override
    protected com.madrobot.net.client.oauth.HttpResponse sendRequest(HttpRequest request) throws Exception {
        HttpResponse response = httpClient.execute((HttpUriRequest) request.unwrap());
        return new HttpResponseAdapter(response);
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
