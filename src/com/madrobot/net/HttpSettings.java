package com.madrobot.net;

public class HttpSettings {

	/**
	 * Default HTTP payload buffer size 4Kb
	 */

	/**
	 * Represents the HTTP GET, POST and DELETE
	 */
	public enum HttpMethod {
		/**
		 * Http DELETE method
		 */
		HTTP_DELETE,
		/**
		 * Http GET method
		 */
		HTTP_GET,
		/**
		 * Http POST method
		 */
		HTTP_POST;
	}

	public static final String HTTP_EXPECT_CONTINUE_PARAM = "http.protocol.expect-continue";
	public static final String HTTP_SINGLE_COOKIE_PARAM="http.protocol.single-cookie-header";
	/**
	 * HTTP session socket time-out key
	 */
	public static final String HTTP_SOCKET_TIME_OUT_PARAM = "http.socket.timeout";
	private int defaultBufferSize = 1042 * 4;

	private boolean expectContinue = true;
	
	private HttpMethod httpMethod = HttpMethod.HTTP_GET;

	
	private boolean isSingleCookieHeader=false;

	/**
	 * HTTP session timeout. Default is 30 seconds
	 */
	private int socketTimeout = 30000;;

	public int getDefaultBufferSize() {
		return defaultBufferSize;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public boolean isExpectContinue() {
		return expectContinue;
	}

	public boolean isSingleCookieHeader() {
		return isSingleCookieHeader;
	}

	/**
	 * default: 4kb
	 */
	public void setDefaultBufferSize(int defaultBufferSize) {
		this.defaultBufferSize = defaultBufferSize;
	}

	/**
	 * default :true
	 * 
	 * @param expectContinue
	 */
	public void setExpectContinue(boolean expectContinue) {
		this.expectContinue = expectContinue;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	
	public void setSingleCookieHeader(boolean isSingleCookieHeader) {
		this.isSingleCookieHeader = isSingleCookieHeader;
	}

	/**
	 * default: 30 secs
	 * 
	 * @param timeout
	 */
	public void setSocketTimeout(int timeout) {
		this.socketTimeout = timeout;
	}

}
