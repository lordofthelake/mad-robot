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
package com.madrobot.log;

import java.io.IOException;
import java.util.Date;

import android.util.Log;


final class LoggerStrategyHTMLFile extends LoggerStrategyLogFile {

	LoggerStrategyHTMLFile(String directory, String fileName) {
		super(directory, fileName);
		initHTMLLogger();
	}

	private void initHTMLLogger() {
		if (writer != null) {
			try {
				writer
						.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
				writer.write("<html>\n");
				writer.write("<head>");
				writer.write("<title></title>");
				writer.write("<script type=\"text/javascript\">");
				writer.flush();
				writer.write("function filter (phrase, _id){\n");
				writer
						.write("var words = phrase.value.toLowerCase().split(\" \");\n");
				writer.write("var table = document.getElementById(_id);\n");
				writer.write("var ele;\n");
				writer.write("for (var r = 1; r < table.rows.length; r++){\n");
				writer
						.write("ele = table.rows[r].innerHTML.replace(/<[^>]+>/g,\"\");\n");
				writer.write("var displayStyle = 'none';\n");
				writer.write("for (var i = 0; i < words.length; i++) {\n");
				writer.write("if (ele.toLowerCase().indexOf(words[i])>=0)\n");
				writer.write("displayStyle = '';\n");
				writer.write("else {\n");
				writer.write("displayStyle = 'none';\n");
				writer.write("break;\n");
				writer.write("}\n");
				writer.write("}\n");
				writer.write("table.rows[r].style.display = displayStyle;\n");
				writer.write("}\n");
				writer.write("}\n");
				writer.flush();
				writer.write("</script>\n");
				writer
						.write("<form><b>Search:</b><input name=\"filt\"onkeyup=\"filter(this, 'sf')\" type=\"text\"></form>");
				writer
						.write("<table id=\"sf\" WIDTH=\"100%\" border=\"0\" cellspacing=\"5\" cellpadding=\"5\"");
				writer
						.write("<tr><th><b>Date</b></th><th><b>Level</b></th><th><b>Tag</b></th><th><b>Message</b></th></tr>");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	 void shutdown() {
		if (writer != null) {
			try {
				writer.write("</table></body></html>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.shutdown();
	}

	@Override
	 void write(int level, String tag, String message) {

		if (writer != null) {
			String color = null;
			switch (level) {
			case Log.INFO:
				color = "#007F00";
				break;
			case Log.DEBUG:
				color = "#00007F";
				break;
			case Log.VERBOSE:
				color = "#000000";
				break;
			case Log.ERROR:
				color = "#ff0000";
				break;
			case Log.WARN:
				color = "#FF7F00";
				break;
			case Log.ASSERT:
				color = "#000000";
				break;
			}
			StringBuffer buf = new StringBuffer();
			String rowFont = "<td><font color=\"" + color + "\">";
			String endFont = "</font></td>";
			buf.append("<tr>");
			buf.append(rowFont);
			buf.append(formatDate(new Date()));
			buf.append(endFont);
			buf.append(rowFont);
			buf.append(levelToString(level));
			buf.append(endFont);

			buf.append(rowFont);
			buf.append(tag);
			buf.append(endFont);
			buf.append(rowFont);
			buf.append("<p>" + message + "</p>");
			buf.append(endFont);
			buf.append("</tr>");
			try {
				writer.append(buf);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.println(level, tag, message);
	}
}
