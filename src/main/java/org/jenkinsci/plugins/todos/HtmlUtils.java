/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Michal Turek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.todos;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * HTML related utilities.
 * 
 * @author Michal Turek
 */
public class HtmlUtils {
	/**
	 * Encode text so it can be passed to a HTML page. Replace special HTML
	 * characters by HTML entities.
	 * 
	 * @param text
	 *            the input text
	 * @param preserveSpaces
	 *            replace spaces by &nbsp; to let all of them visible
	 * @return the encoded string
	 */
	public static String encodeText(String text, boolean preserveSpaces) {
		String result = text;
		result = result.replace("&", "&amp;");
		result = result.replace("<", "&lt;");
		result = result.replace(">", "&gt;");
		result = result.replace("\"", "&quot;");
		result = result.replace("'", "&apos;");

		if (preserveSpaces) {
			result = result.replace(" ", "&nbsp;");
		}

		return result;
	}

	/**
	 * Encode string so it can be used as URL.
	 * 
	 * Note: UTF-8 encoding will be used if possible, otherwise platform default
	 * charset will be used.
	 * 
	 * @param url
	 *            the input string
	 * @return the string encoded as URL.
	 * @see URLEncoder#encode(String, String)
	 */
	public static String encodeUrl(String url) {
		try {
			// UTF-8 should be always defined
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			try {
				return URLEncoder.encode(url, Charset.defaultCharset().name());
			} catch (UnsupportedEncodingException e1) {
				return url;
			}
		}
	}
}
