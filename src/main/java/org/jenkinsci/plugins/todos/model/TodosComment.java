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
package org.jenkinsci.plugins.todos.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Single comment that was found.
 * 
 * @author Michal Turek
 */
public class TodosComment {
	/** The pattern using which this comment was found. */
	@XmlAttribute
	private final String pattern;

	/** The input file where the comment was found. */
	@XmlAttribute
	private final String file;

	/** The position in the file, line number. */
	@XmlAttribute
	private final int line;

	/** The matching line and optionally several lines after it. */
	@XmlValue
	private final String text;

	/**
	 * Constructor initializing members.
	 * 
	 * @param pattern
	 *            the pattern using which this comment was found
	 * @param file
	 *            the input file where the comment was found
	 * @param line
	 *            the position in the file, line number
	 * @param text
	 *            the matching line and optionally several lines after it
	 */
	public TodosComment(String pattern, String file, int line, String text) {
		this.pattern = pattern;
		this.file = file;
		this.line = line;
		this.text = text;
	}

	/**
	 * This constructor is required by JAXB.
	 */
	@SuppressWarnings("unused")
	private TodosComment() {
		this(null, null, 0, null);
	}

	public String getPattern() {
		return pattern;
	}

	public String getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}

	public String getText() {
		return text;
	}
}
