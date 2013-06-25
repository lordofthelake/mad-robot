package com.madrobot.net.client.oauth;


/**
 * Supports signing HTTP requests of type {@link org.apache.http.HttpRequest}.
 * 
 */
public class CommonsHttpOAuthConsumer extends AbstractOAuthConsumer {

    private static final long serialVersionUID = 1L;

    public CommonsHttpOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof org.apache.http.HttpRequest)) {
            throw new IllegalArgumentException(
                    "This consumer expects requests of type "
                            + org.apache.http.HttpRequest.class.getCanonicalName());
        }

        return new HttpRequestAdapter((org.apache.http.client.methods.HttpUriRequest) request);
    }

}
