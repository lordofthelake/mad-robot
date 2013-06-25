package com.madrobot.net.client.oauth.signature;


import com.madrobot.net.client.oauth.HttpParameters;
import com.madrobot.net.client.oauth.HttpRequest;
import com.madrobot.net.client.oauth.OAuth;
import com.madrobot.net.client.oauth.OAuthMessageSignerException;

@SuppressWarnings("serial")
public class PlainTextMessageSigner extends OAuthMessageSigner {

    @Override
    public String getSignatureMethod() {
        return "PLAINTEXT";
    }

    @Override
    public String sign(HttpRequest request, HttpParameters requestParams)
            throws OAuthMessageSignerException {
        return OAuth.percentEncode(getConsumerSecret()) + '&'
                + OAuth.percentEncode(getTokenSecret());
    }
}
