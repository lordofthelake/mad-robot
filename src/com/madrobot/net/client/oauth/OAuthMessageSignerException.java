package com.madrobot.net.client.oauth;

@SuppressWarnings("serial")
public class OAuthMessageSignerException extends OAuthException {

    public OAuthMessageSignerException(Exception cause) {
        super(cause);
    }

    public OAuthMessageSignerException(String message) {
        super(message);
    }

}
