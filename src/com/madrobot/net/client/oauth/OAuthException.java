package com.madrobot.net.client.oauth;

@SuppressWarnings("serial")
public abstract class OAuthException extends Exception {

    public OAuthException(String message) {
        super(message);
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuthException(Throwable cause) {
        super(cause);
    }
}
