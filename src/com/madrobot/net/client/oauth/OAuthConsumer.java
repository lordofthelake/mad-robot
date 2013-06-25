package com.madrobot.net.client.oauth;

import java.io.Serializable;

import com.madrobot.net.client.oauth.signature.AuthorizationHeaderSigningStrategy;
import com.madrobot.net.client.oauth.signature.HmacSha1MessageSigner;
import com.madrobot.net.client.oauth.signature.OAuthMessageSigner;
import com.madrobot.net.client.oauth.signature.PlainTextMessageSigner;
import com.madrobot.net.client.oauth.signature.QueryStringSigningStrategy;
import com.madrobot.net.client.oauth.signature.SigningStrategy;

/**
 * <p>
 * Exposes a simple interface to sign HTTP requests using a given OAuth token and secret. Refer to {@link OAuthProvider}
 * how to retrieve a valid token and token secret.
 * </p>
 * <p>
 * HTTP messages are signed as follows:
 * <p>
 * 
 * <pre>
 * // exchange the arguments with the actual token/secret pair
 * OAuthConsumer consumer = new DefaultOAuthConsumer(&quot;1234&quot;, &quot;5678&quot;);
 * 
 * URL url = new URL(&quot;http://example.com/protected.xml&quot;);
 * HttpURLConnection request = (HttpURLConnection) url.openConnection();
 * 
 * consumer.sign(request);
 * 
 * request.connect();
 * </pre>
 * 
 * </p>
 * </p>
 * 
 */
public interface OAuthConsumer extends Serializable {

	public String getConsumerKey();

	public String getConsumerSecret();

	/**
	 * Returns all parameters collected from the HTTP request during message signing (this means the return value may be
	 * NULL before a call to {@link #sign}), plus all required OAuth parameters that were added because the request
	 * didn't contain them beforehand. In other words, this is the exact set of parameters that were used for creating
	 * the message signature.
	 * 
	 * @return the request parameters used for message signing
	 */
	public HttpParameters getRequestParameters();

	public String getToken();

	public String getTokenSecret();

	/**
	 * Allows you to add parameters (typically OAuth parameters such as oauth_callback or oauth_verifier) which will go
	 * directly into the signer, i.e. you don't have to put them into the request first. The consumer's
	 * {@link SigningStrategy} will then take care of writing them to the correct part of the request before it is sent.
	 * Note that these parameters are expected to already be percent encoded -- they will be simply merged as-is.
	 * 
	 * @param additionalParameters
	 *            the parameters
	 */
	public void setAdditionalParameters(HttpParameters additionalParameters);

	/**
	 * Sets the message signer that should be used to generate the OAuth signature.
	 * 
	 * @param messageSigner
	 *            the signer
	 * @see HmacSha1MessageSigner
	 * @see PlainTextMessageSigner
	 */
	public void setMessageSigner(OAuthMessageSigner messageSigner);

	/**
	 * <p>
	 * Causes the consumer to always include the oauth_token parameter to be sent, even if blank. If you're seeing 401s
	 * during calls to {@link OAuthProvider#retrieveRequestToken}, try setting this to true.
	 * </p>
	 * 
	 * @param enable
	 *            true or false
	 */
	public void setSendEmptyTokens(boolean enable);

	/**
	 * Defines which strategy should be used to write a signature to an HTTP request.
	 * 
	 * @param signingStrategy
	 *            the strategy
	 * @see AuthorizationHeaderSigningStrategy
	 * @see QueryStringSigningStrategy
	 */
	public void setSigningStrategy(SigningStrategy signingStrategy);

	/**
	 * Sets the OAuth token and token secret used for message signing.
	 * 
	 * @param token
	 *            the token
	 * @param tokenSecret
	 *            the token secret
	 */
	public void setTokenWithSecret(String token, String tokenSecret);

	/**
	 * Signs the given HTTP request by writing an OAuth signature (and other required OAuth parameters) to it. Where
	 * these parameters are written depends on the current {@link SigningStrategy}.
	 * 
	 * @param request
	 *            the request to sign
	 * @return the request object passed as an argument
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public HttpRequest sign(HttpRequest request) throws OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException;

	/**
	 * <p>
	 * Signs the given HTTP request by writing an OAuth signature (and other required OAuth parameters) to it. Where
	 * these parameters are written depends on the current {@link SigningStrategy}.
	 * </p>
	 * This method accepts HTTP library specific request objects; the consumer implementation must ensure that only
	 * those request types are passed which it supports.
	 * 
	 * @param request
	 *            the request to sign
	 * @return the request object passed as an argument
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public HttpRequest sign(Object request) throws OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException;

	/**
	 * <p>
	 * "Signs" the given URL by appending all OAuth parameters to it which are required for message signing. The assumed
	 * HTTP method is GET. Essentially, this is equivalent to signing an HTTP GET request, but it can be useful if your
	 * application requires clickable links to protected resources, i.e. when your application does not have access to
	 * the actual request that is being sent.
	 * </p>
	 * 
	 * @param url
	 *            the input URL. May have query parameters.
	 * @return the input URL, with all necessary OAuth parameters attached as a query string. Existing query parameters
	 *         are preserved.
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public String sign(String url) throws OAuthMessageSignerException, OAuthExpectationFailedException,
			OAuthCommunicationException;
}
