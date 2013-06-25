package com.madrobot.net.client.oauth.signature;


import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.madrobot.net.client.oauth.HttpParameters;
import com.madrobot.net.client.oauth.HttpRequest;
import com.madrobot.net.client.oauth.OAuth;
import com.madrobot.net.client.oauth.OAuthMessageSignerException;

@SuppressWarnings("serial")
public class HmacSha1MessageSigner extends OAuthMessageSigner {

	private static final String MAC_NAME = "HmacSHA1";

	@Override
    public String getSignatureMethod() {
        return "HMAC-SHA1";
    }

    @Override
    public String sign(HttpRequest request, HttpParameters requestParams)
            throws OAuthMessageSignerException {
        try {
            String keyString = OAuth.percentEncode(getConsumerSecret()) + '&'
                    + OAuth.percentEncode(getTokenSecret());
            byte[] keyBytes = keyString.getBytes(OAuth.ENCODING);

            SecretKey key = new SecretKeySpec(keyBytes, MAC_NAME);
            Mac mac = Mac.getInstance(MAC_NAME);
            mac.init(key);

            String sbs = new SignatureBaseString(request, requestParams).generate();
            OAuth.debugOut("SBS", sbs);
            byte[] text = sbs.getBytes(OAuth.ENCODING);

            return base64Encode(mac.doFinal(text)).trim();
        } catch (GeneralSecurityException e) {
            throw new OAuthMessageSignerException(e);
        } catch (UnsupportedEncodingException e) {
            throw new OAuthMessageSignerException(e);
        }
    }
}
