package com.madrobot.net.client.oauth.signature;

import java.io.IOException;
import java.io.Serializable;

import android.util.Base64;

import com.madrobot.net.client.oauth.HttpParameters;
import com.madrobot.net.client.oauth.HttpRequest;
import com.madrobot.net.client.oauth.OAuthMessageSignerException;

public abstract class OAuthMessageSigner implements Serializable {

	private static final long serialVersionUID = 4445779788786131202L;

	private String consumerSecret;

	private String tokenSecret;

	public OAuthMessageSigner() {
	}

	protected String base64Encode(byte[] b) {
		return new String(Base64.encode(b,Base64.DEFAULT));
	}

	protected byte[] decodeBase64(String s) {
		return Base64.decode(s.getBytes(), Base64.DEFAULT);
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public abstract String getSignatureMethod();

	public String getTokenSecret() {
		return tokenSecret;
	}

	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public abstract String sign(HttpRequest request, HttpParameters requestParameters)
			throws OAuthMessageSignerException;
}
