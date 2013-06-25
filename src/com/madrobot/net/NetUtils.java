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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class NetUtils {
	private final static String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/**
	 * Decode the IP Address represented as an integer
	 * @param ip String representation of the IP address.
	 * @return
	 */
	public static String decodeIPAddress(int ip) {
		int[] num = new int[4];

		num[0] = (ip & 0xff000000) >> 24;
		num[1] = (ip & 0x00ff0000) >> 16;
		num[2] = (ip & 0x0000ff00) >> 8;
		num[3] = ip & 0x000000ff;

		StringBuilder builder=new StringBuilder();
		builder.append(num[0]).append('.').append(num[1]).append('.').append(num[2]).append('.').append(num[3]);

		return builder.toString();
	}

	/**
	 * Enable Http Response cache. Works only on Ice-cream sandwich.
	 * 
	 * @param cacheDir
	 *            Directory to be used as a response cache
	 * @param cacheSize
	 *            Size of the cache
	 */
	public static void enableHttpResponseCache(String cacheDir, long cacheSize) {
		try {
			File httpCacheDir = new File(cacheDir, "http");
			Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class)
					.invoke(null, httpCacheDir, cacheSize);
		} catch (Exception e) {
		}
	}

	/**
	 * Encodes an IP as an int
	 * 
	 * @param ip
	 * @return the encoded IP
	 */
	public static int encode(Inet4Address ip) {
		int i = 0;
		byte[] b = ip.getAddress();
		i |= b[0] << 24;
		i |= b[1] << 16;
		i |= b[2] << 8;
		i |= b[3] << 0;

		return i;
	}

	/**
	 * Encode the given IP address segments
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return the encoded ip
	 */
	public static int encodeIPAddress(int a, int b, int c, int d) {
		int ip = 0;
		ip |= a << 24;
		ip |= b << 16;
		ip |= c << 8;
		ip |= d;
		return ip;
	}

	/**
	 * Finds this computer's global IP address
	 * 
	 * @param url
	 *            the url to find the global IP
	 * @return The global IP address, or null if a problem occurred
	 */
	public static Inet4Address getGlobalAddress(String url) {
		try {
			URLConnection uc = new URL(url).openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			return (Inet4Address) InetAddress.getByName(br.readLine());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Finds a local, non-loopback, IPv4 address
	 * 
	 * @return The first non-loopback IPv4 address found, or <code>null</code> if no such addresses found
	 * @throws SocketException
	 *             If there was a problem querying the network interfaces
	 */
	public static InetAddress getLocalAddress() throws SocketException {
		Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
		while (ifaces.hasMoreElements()) {
			NetworkInterface iface = ifaces.nextElement();
			Enumeration<InetAddress> addresses = iface.getInetAddresses();

			while (addresses.hasMoreElements()) {
				InetAddress addr = addresses.nextElement();
				if ((addr instanceof Inet4Address) && !addr.isLoopbackAddress()) {
					return addr;
				}
			}
		}
		return null;
	}

	private static boolean isMaskValue(String component, int size) {
		try {
			int value = Integer.parseInt(component);

			return value >= 0 && value <= size;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isValidEmailAddress(String address) {
		Pattern pattern = Pattern.compile(emailPattern);
		Matcher matcher = pattern.matcher(address);
		return matcher.matches();
	}

	/**
	 * Validate the given IPv4 address.
	 * 
	 * @param address
	 *            the IP address as a String.
	 * 
	 * @return true if a valid IPv4 address, false otherwise
	 */
	public static boolean isValidIPv4(String address) {
		if (address.length() == 0) {
			return false;
		}

		int octet;
		int octets = 0;

		String temp = address + ".";

		int pos;
		int start = 0;
		while (start < temp.length() && (pos = temp.indexOf('.', start)) > start) {
			if (octets == 4) {
				return false;
			}
			try {
				octet = Integer.parseInt(temp.substring(start, pos));
			} catch (NumberFormatException ex) {
				return false;
			}
			if (octet < 0 || octet > 255) {
				return false;
			}
			start = pos + 1;
			octets++;
		}

		return octets == 4;
	}

	public static boolean isValidIPv4WithNetmask(String address) {
		int index = address.indexOf("/");
		String mask = address.substring(index + 1);

		return (index > 0) && isValidIPv4(address.substring(0, index)) && (isValidIPv4(mask) || isMaskValue(mask, 32));
	}

	/**
	 * Validate the given IPv6 address.
	 * 
	 * @param address
	 *            the IP address as a String.
	 * 
	 * @return true if a valid IPv4 address, false otherwise
	 */
	public static boolean isValidIPv6(String address) {
		if (address.length() == 0) {
			return false;
		}

		int octet;
		int octets = 0;

		String temp = address + ":";
		boolean doubleColonFound = false;
		int pos;
		int start = 0;
		while (start < temp.length() && (pos = temp.indexOf(':', start)) >= start) {
			if (octets == 8) {
				return false;
			}

			if (start != pos) {
				String value = temp.substring(start, pos);

				if (pos == (temp.length() - 1) && value.indexOf('.') > 0) {
					if (!isValidIPv4(value)) {
						return false;
					}

					octets++; // add an extra one as address covers 2 words.
				} else {
					try {
						octet = Integer.parseInt(temp.substring(start, pos), 16);
					} catch (NumberFormatException ex) {
						return false;
					}
					if (octet < 0 || octet > 0xffff) {
						return false;
					}
				}
			} else {
				if (pos != 1 && pos != temp.length() - 1 && doubleColonFound) {
					return false;
				}
				doubleColonFound = true;
			}
			start = pos + 1;
			octets++;
		}

		return octets == 8 || doubleColonFound;
	}

	public static boolean isValidIPv6WithNetmask(String address) {
		int index = address.indexOf("/");
		String mask = address.substring(index + 1);

		return (index > 0)
				&& (isValidIPv6(address.substring(0, index)) && (isValidIPv6(mask) || isMaskValue(mask, 128)));
	}

	/**
	 * Makes the platform trust all SSL connections.
	 * <p>
	 * Warning: This should be used for development purposes only
	 * </p>
	 */
	public static void trustAllSecureConnections() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}
}
