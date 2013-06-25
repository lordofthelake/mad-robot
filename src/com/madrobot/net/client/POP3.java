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
package com.madrobot.net.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Vector;

import com.madrobot.net.client.command.POP3Command;
import com.madrobot.net.client.command.ProtocolCommandListener;
import com.madrobot.net.client.command.ProtocolCommandSupport;

/***
 * The POP3 class is not meant to be used by itself and is provided
 * only so that you may easily implement your own POP3 client if
 * you so desire. If you have no need to perform your own implementation,
 * you should use {@link com.madrobot.net.client.POP3Client}.
 * <p>
 * Rather than list it separately for each method, we mention here that every
 * method communicating with the server and throwing an IOException can also
 * throw a {@link com.madrobot.net.client.MalformedServerReplyException} ,
 * which is a subclass of IOException. A MalformedServerReplyException will be
 * thrown when the reply received from the server deviates enough from the
 * protocol specification that it cannot be interpreted in a useful manner
 * despite attempts to be as lenient as possible.
 * <p>
 * <p>
 * 
 * @see POP3Client
 * @see com.madrobot.net.client.MalformedServerReplyException
 ***/

public class POP3 extends SocketClient {
	// We have to ensure that the protocol communication is in ASCII
	// but we use ISO-8859-1 just in case 8-bit characters cross
	// the wire.
	private static final String __DEFAULT_ENCODING = "ISO-8859-1";
	static final String _ERROR = "-ERR";
	static final String _OK = "+OK";
	/*** A constant representing the POP3 authorization state. ***/
	public static final int AUTHORIZATION_STATE = 0;
	/*** The default POP3 port. Set to 110 according to RFC 1288. ***/
	public static final int DEFAULT_PORT = 110;

	/***
	 * A constant representing the state where the client is not yet connected
	 * to a POP3 server.
	 ***/
	public static final int DISCONNECTED_STATE = -1;
	/*** A constant representing the POP3 transaction state. ***/
	public static final int TRANSACTION_STATE = 1;

	/*** A constant representing the POP3 update state. ***/
	public static final int UPDATE_STATE = 2;

	private StringBuffer __commandBuffer;
	private int __popState;
	private BufferedWriter __writer;

	/***
	 * A ProtocolCommandSupport object used to manage the registering of
	 * ProtocolCommandListeners and te firing of ProtocolCommandEvents.
	 ***/
	protected ProtocolCommandSupport _commandSupport_;
	String _lastReplyLine;
	BufferedReader _reader;
	int _replyCode;

	Vector<String> _replyLines;

	/***
	 * The default POP3Client constructor. Initializes the state
	 * to <code>DISCONNECTED_STATE</code>.
	 ***/
	public POP3() {
		setDefaultPort(DEFAULT_PORT);
		__commandBuffer = new StringBuffer();
		__popState = DISCONNECTED_STATE;
		_reader = null;
		__writer = null;
		_replyLines = new Vector<String>();
		_commandSupport_ = new ProtocolCommandSupport(this);
	}

	private void __getReply() throws IOException {
		String line;

		_replyLines.setSize(0);
		line = _reader.readLine();

		if(line == null){
			throw new EOFException("Connection closed without indication.");
		}

		if(line.startsWith(_OK)){
			_replyCode = POP3Reply.OK;
		} else if(line.startsWith(_ERROR)){
			_replyCode = POP3Reply.ERROR;
		} else{
			throw new MalformedServerReplyException("Received invalid POP3 protocol response from server.");
		}

		_replyLines.addElement(line);
		_lastReplyLine = line;

		if(_commandSupport_.getListenerCount() > 0){
			_commandSupport_.fireReplyReceived(_replyCode, getReplyString());
		}
	}

	/***
	 * Performs connection initialization and sets state to
	 * <code> AUTHORIZATION_STATE </code>.
	 ***/
	@Override
	protected void _connectAction_() throws IOException {
		super._connectAction_();
		_reader = new BufferedReader(new InputStreamReader(_input_, __DEFAULT_ENCODING));
		__writer = new BufferedWriter(new OutputStreamWriter(_output_, __DEFAULT_ENCODING));
		__getReply();
		setState(AUTHORIZATION_STATE);
	}

	/***
	 * Adds a ProtocolCommandListener. Delegates this task to
	 * {@link #_commandSupport_ _commandSupport_ }.
	 * <p>
	 * 
	 * @param listener
	 *            The ProtocolCommandListener to add.
	 ***/
	public void addProtocolCommandListener(ProtocolCommandListener listener) {
		_commandSupport_.addProtocolCommandListener(listener);
	}

	/***
	 * Disconnects the client from the server, and sets the state to
	 * <code> DISCONNECTED_STATE </code>. The reply text information
	 * from the last issued command is voided to allow garbage collection
	 * of the memory used to store that information.
	 * <p>
	 * 
	 * @exception IOException
	 *                If there is an error in disconnecting.
	 ***/
	@Override
	public void disconnect() throws IOException {
		super.disconnect();
		_reader = null;
		__writer = null;
		_lastReplyLine = null;
		_replyLines.setSize(0);
		setState(DISCONNECTED_STATE);
	}

	/***
	 * Retrieves the additional lines of a multi-line server reply.
	 ***/
	public void getAdditionalReply() throws IOException {
		String line;

		line = _reader.readLine();
		while(line != null){
			_replyLines.addElement(line);
			if(line.equals(".")){
				break;
			}
			line = _reader.readLine();
		}
	}

	/***
	 * Returns the reply to the last command sent to the server.
	 * The value is a single string containing all the reply lines including
	 * newlines. If the reply is a single line, but its format ndicates it
	 * should be a multiline reply, then you must call
	 * {@link #getAdditionalReply getAdditionalReply() } to
	 * fetch the rest of the reply, and then call <code>getReplyString</code>
	 * again. You only have to worry about this if you are implementing
	 * your own client using the {@link #sendCommand sendCommand } methods.
	 * <p>
	 * 
	 * @return The last server response.
	 ***/
	public String getReplyString() {
		Enumeration<String> en;
		StringBuilder buffer = new StringBuilder(256);

		en = _replyLines.elements();
		while(en.hasMoreElements()){
			buffer.append(en.nextElement());
			buffer.append(SocketClient.NETASCII_EOL);
		}

		return buffer.toString();
	}

	/***
	 * Returns an array of lines received as a reply to the last command
	 * sent to the server. The lines have end of lines truncated. If
	 * the reply is a single line, but its format ndicates it should be
	 * a multiline reply, then you must call {@link #getAdditionalReply
	 * getAdditionalReply() } to
	 * fetch the rest of the reply, and then call <code>getReplyStrings</code>
	 * again. You only have to worry about this if you are implementing
	 * your own client using the {@link #sendCommand sendCommand } methods.
	 * <p>
	 * 
	 * @return The last server response.
	 ***/
	public String[] getReplyStrings() {
		String[] lines;
		lines = new String[_replyLines.size()];
		_replyLines.copyInto(lines);
		return lines;
	}

	/***
	 * Returns the current POP3 client state.
	 * <p>
	 * 
	 * @return The current POP3 client state.
	 ***/
	public int getState() {
		return __popState;
	}

	/***
	 * Removes a ProtocolCommandListener. Delegates this task to
	 * {@link #_commandSupport_ _commandSupport_ }.
	 * <p>
	 * 
	 * @param listener
	 *            The ProtocolCommandListener to remove.
	 ***/
	public void removeProtocolCommandistener(ProtocolCommandListener listener) {
		_commandSupport_.removeProtocolCommandListener(listener);
	}

	/***
	 * Sends a command with no arguments to the server and returns the
	 * reply code.
	 * <p>
	 * 
	 * @param command
	 *            The POP3 command to send
	 *            (one of the POP3Command constants).
	 * @return The server reply code (either POP3Reply.OK or POP3Reply.ERROR).
	 ***/
	public int sendCommand(int command) throws IOException {
		return sendCommand(POP3Command.getCommand(command), null);
	}

	/***
	 * Sends a command an arguments to the server and returns the reply code.
	 * <p>
	 * 
	 * @param command
	 *            The POP3 command to send
	 *            (one of the POP3Command constants).
	 * @param args
	 *            The command arguments.
	 * @return The server reply code (either POP3Reply.OK or POP3Reply.ERROR).
	 ***/
	public int sendCommand(int command, String args) throws IOException {
		return sendCommand(POP3Command.getCommand(command), args);
	}

	/***
	 * Sends a command with no arguments to the server and returns the
	 * reply code.
	 * <p>
	 * 
	 * @param command
	 *            The POP3 command to send.
	 * @return The server reply code (either POP3Reply.OK or POP3Reply.ERROR).
	 ***/
	public int sendCommand(String command) throws IOException {
		return sendCommand(command, null);
	}

	/***
	 * Sends a command an arguments to the server and returns the reply code.
	 * <p>
	 * 
	 * @param command
	 *            The POP3 command to send.
	 * @param args
	 *            The command arguments.
	 * @return The server reply code (either POP3Reply.OK or POP3Reply.ERROR).
	 ***/
	public int sendCommand(String command, String args) throws IOException {
		String message;

		__commandBuffer.setLength(0);
		__commandBuffer.append(command);

		if(args != null){
			__commandBuffer.append(' ');
			__commandBuffer.append(args);
		}
		__commandBuffer.append(SocketClient.NETASCII_EOL);

		__writer.write(message = __commandBuffer.toString());
		__writer.flush();

		if(_commandSupport_.getListenerCount() > 0){
			_commandSupport_.fireCommandSent(command, message);
		}

		__getReply();
		return _replyCode;
	}

	/***
	 * Sets POP3 client state. This must be one of the <code>_STATE</code>
	 * constants.
	 * <p>
	 * 
	 * @param state
	 *            The new state.
	 ***/
	public void setState(int state) {
		__popState = state;
	}

}
