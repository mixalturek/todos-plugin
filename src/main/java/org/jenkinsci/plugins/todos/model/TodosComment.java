/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Michal Turek
 * This file is part of TODOs Plugin (Jenkins CI).
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

import java.io.File;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.jenkinsci.plugins.todos.HtmlUtils;

/**
 * Single comment that was found.
 * 
 * @author Michal Turek
 */
public class TodosComment implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

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
	private final String sourceCode;

	/**
	 * Constructor initializing members.
	 * 
	 * @param pattern
	 *            the pattern using which this comment was found
	 * @param file
	 *            the input file where the comment was found
	 * @param line
	 *            the position in the file, line number
	 * @param sourceCode
	 *            the matching line and optionally several lines after it
	 */
	public TodosComment(String pattern, String file, int line, String sourceCode) {
		this.pattern = pattern;
		this.file = file;
		this.line = line;
		this.sourceCode = sourceCode;
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

	public String getPatternHtml() {
		return HtmlUtils.encodeText(pattern, true);
	}

	public String getFile() {
		return file;
	}

	public String getFileHtml() {
		return HtmlUtils.encodeText(file, true);
	}

	/**
	 * Helper method that returns the path with space after each slash to allow
	 * line wrap of long paths in a HTML table.
	 * 
	 * That's not a bug, that's a feature!
	 * 
	 * @return the original path with spaces around each slash
	 */
	public String getFileHtmlExtraSpaces() {
		return HtmlUtils.encodeText(file, true).replace(File.separator,
				"&nbsp;" + File.separator + " ");
	}

	public int getLine() {
		return line;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public String getSourceCodeHtml() {
		return HtmlUtils.encodeText(sourceCode, false);
	}
}
