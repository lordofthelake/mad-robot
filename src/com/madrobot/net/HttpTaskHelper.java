/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * Implementation of a Http based task.
 * <p>
 * Sample Usage:<br/>
 * 
 * <pre>
 * HttpTaskHelper httpTask = new HttpTaskHelper(new URI(&quot;http://www.google.com&quot;));
 * // add request parameters (if any).
 * List&lt;NameValuePair&gt; requestParameter = new ArrayList&lt;NameValuePair&gt;();
 * NameValuePair param = new BasicNameValuePair(&quot;q&quot;, &quot;Android library&quot;);
 * requestParameter.add(param);
 * // set the parameters
 * httpTask.setRequestParameter(requestParameter);
 * //set the http method. The default is Http GET.
 * httpTask.getHttpSettings().setHttpMethod(HttpTaskHelper.HttpMethod.HTTP_GET);
 * 
 * 
 * </pre>
 * 
 * </p>
 * 
 */
public class HttpTaskHelper {

	private static final String TAG = "MadRobot";

	

	

	private HttpClient httpClient;

	public HttpResponse httpResponse;

	private HttpSettings httpSettings=new HttpSettings();

	private Map<String, String> requestHeader;
	private List<NameValuePair> requestParameter;
	private URI requestUrl;
	private Map<String, String> responseHeader;
	/**
	 * 
	 * Parameterized constructor to initialize the request details.
	 * 
	 * @param requestUrl
	 *            request url
	 */
	public HttpTaskHelper(URI requestUrl) {
		this.requestUrl = requestUrl;
	}

	/**
	 * Access point to add the query parameter to the request url
	 * 
	 * @throws URISyntaxException
	 *             if request url syntax is wrong
	 */
	private void addQueryParameter() throws URISyntaxException {
		this.requestUrl = URIUtils.createURI(this.requestUrl.getScheme(), this.requestUrl.getAuthority(),
				-1, this.requestUrl.getPath(), URLEncodedUtils.format(requestParameter, HTTP.UTF_8), null);
	}

	/**
	 * Add the response header into response
	 * 
	 * @param responseHeader
	 *            - http headers
	 */
	private void addResponseHeaders(Header[] responseHeader) {
		for(Header header : responseHeader){
			getResponseHeader().put(header.getName(), header.getValue());

		}
	}

	/**
	 * Cancel the request after the {@link HttpTaskHelper#execute()} method is
	 * called
	 */
	public void cancel() {
		if(getHttpClient() != null){
			getHttpClient().getConnectionManager().shutdown();
		}
	}

	/**
	 * 
	 * Get the http response from {@link HttpTaskHelper#getHttpMethod()}
	 * 
	 * @return HttpEntity - the response to the request. This is always a final
	 *         response, never an intermediate response with an 1xx status code.
	 * 
	 * @throws IOException
	 *             - in case of a problem or the connection was aborted
	 * 
	 * @throws URISyntaxException
	 *             in case of request url syntax error
	 */
	public HttpEntity execute() throws IOException, URISyntaxException {

		switch(httpSettings.getHttpMethod()) {
			case HTTP_GET:
				return handleHttpGet();
			case HTTP_POST:
				return handleHttpPost();
			case HTTP_DELETE:
				return handleHttpDelete();
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Return the http client object with http session time out values
	 * 
	 * @return HttpClient
	 * 
	 * @see {@link HttpTaskHelper.HTTP_SOCKET_TIME_OUT_PARAM}
	 */
	private HttpClient getHttpClient() {
		if(httpClient == null){
			httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(HttpSettings.HTTP_SOCKET_TIME_OUT_PARAM, httpSettings.getSocketTimeout());
			httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
			httpClient.getParams().setParameter(HttpSettings.HTTP_SINGLE_COOKIE_PARAM,httpSettings.isSingleCookieHeader());
			httpClient.getParams().setParameter(HttpSettings.HTTP_EXPECT_CONTINUE_PARAM, httpSettings.isExpectContinue());
		}
		return httpClient;
	}

	public HttpSettings getHttpSettings() {
		return httpSettings;
	}

	/**
	 * Private access point to get th http request headers
	 * 
	 * @return http request headers
	 */
	private Map<String, String> getRequestHeader() {
		return requestHeader;
	}

	// ////////////////////////////////////////////////////////////////////
	// Private Method's

	

	/**
	 * 
	 * Private access point to get the http request parameter
	 * 
	 * @return return http request parameter as name value pairs
	 */
	private List<NameValuePair> getRequestParameter() {
		return requestParameter;
	}

	/**
	 * Access point to get the response headers
	 * 
	 * @return http response headers
	 */
	public Map<String, String> getResponseHeader() {
		if(responseHeader == null){
			responseHeader = new HashMap<String, String>();
		}
		return responseHeader;
	}

	/**
	 * 
	 * Hold the responsibility of initialize and sending the http delete request
	 * to the requestURL and return the http response object
	 * 
	 * @return returns the HttpEntity object
	 * 
	 * @throws IOException
	 *             - in case of a problem or the connection was aborted
	 * 
	 * @throws URISyntaxException
	 *             in case of request url syntax error
	 */
	private HttpEntity handleHttpDelete() throws IOException, URISyntaxException {

		if(hasRequestParameter()){
			addQueryParameter();
		}
		Log.d(TAG, "Request URL:[GET] " + requestUrl);
		HttpDelete httpDelete = new HttpDelete(requestUrl);
		this.initRequest(httpDelete);
		httpResponse = getHttpClient().execute(httpDelete);
		return httpResponse.getEntity();
	}

	/**
	 * Hold the responsibility of sending the http get request to the requestURL
	 * and return the http response object
	 * 
	 * 
	 * @return returns the HttpEntity object
	 * 
	 * @throws IOException
	 *             - in case of a problem or the connection was aborted
	 * 
	 * @throws URISyntaxException
	 *             in case of request url syntax error
	 */
	private HttpEntity handleHttpGet() throws IOException, URISyntaxException {

		if(hasRequestParameter()){
			addQueryParameter();
		}
		Log.d(TAG, "Request URL:[GET] " + requestUrl);

		HttpGet httpGet = new HttpGet(requestUrl);
		this.initRequest(httpGet);
		httpResponse = getHttpClient().execute(httpGet);
		return httpResponse.getEntity();
	}

	/**
	 * Hold the responsibility of initialize and sending the http post request
	 * to the requestURL and return the http resposne object
	 * 
	 * @return returns the HttpEntity object
	 * 
	 * @throws IOException
	 *             - in case of a problem or the connection was aborted
	 * 
	 * @throws URISyntaxException
	 *             in case of request url syntax error
	 */
	private HttpEntity handleHttpPost() throws IOException, URISyntaxException {

		HttpPost httpPost = new HttpPost(requestUrl);
		this.initRequest(httpPost);
		try{
			if(hasRequestParameter()){
				httpPost.setEntity(new UrlEncodedFormEntity(getRequestParameter(), "UTF-8"));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		Log.d(TAG, "Request URL:[POST] " + requestUrl);
		httpClient = getHttpClient();
		httpResponse = httpClient.execute(httpPost);
		addResponseHeaders(httpResponse.getAllHeaders());
		return httpResponse.getEntity();
	}

	/**
	 * Return the decision result for adding the headers
	 * 
	 * @return true - if the request header is empty otherwise false
	 */
	private boolean hasRequestHeader() {
		return getRequestHeader() != null ? true : false;
	}

	/**
	 * Return the decision result for adding the request parameters
	 * 
	 * @return true - if the request parameter is empty otherwise false
	 */
	private boolean hasRequestParameter() {
		return getRequestParameter() != null ? true : false;
	}

	/**
	 * Access point to init the http request
	 * 
	 * @param httpRequestBase
	 *            Represents the http base
	 */
	private void initRequest(HttpRequestBase httpRequestBase) {
		if(hasRequestHeader()){
			setDefaultRequestHeaders(httpRequestBase);
			setRequestHeaders(httpRequestBase);
		} else{
			setDefaultRequestHeaders(httpRequestBase);
		}
	}

	/**
	 * Add the default headers if request headers is empty
	 * 
	 * @param httpRequestBase
	 *            - base object for http methods
	 */
	private void setDefaultRequestHeaders(HttpRequestBase httpRequestBase) {
		// httpRequestBase.setHeader("Accept", "*/*");
				
	}

	public void setHttpSettings(HttpSettings httpSettings) {
		this.httpSettings = httpSettings;
	}

	/**
	 * Access point to set the http request headers
	 * 
	 * @param requestHeader
	 *            http request headers
	 */
	public void setRequestHeader(Map<String, String> requestHeader) {
		this.requestHeader = requestHeader;
	}

	/**
	 * Add the request header into request
	 * 
	 * @param httpRequestBase
	 *            - base object for http methods
	 */
	private void setRequestHeaders(HttpRequestBase httpRequestBase) {
		for(String headerName : requestHeader.keySet()){
			String headerValue = requestHeader.get(headerName);
			httpRequestBase.setHeader(headerName, headerValue);
		}
	}

	/**
	 * Access point to set the http request parameter
	 * 
	 * @param requestParameter
	 *            http request parameter
	 */
	public void setRequestParameter(List<NameValuePair> requestParameter) {
		this.requestParameter = requestParameter;
	}
}
