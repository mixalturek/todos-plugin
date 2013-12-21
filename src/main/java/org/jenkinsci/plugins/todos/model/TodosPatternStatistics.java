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

import java.io.Serializable;

import org.jenkinsci.plugins.todos.HtmlUtils;

/**
 * Statistic of one pattern. The class is thread safe.
 * 
 * @author Michal Turek
 */
public class TodosPatternStatistics implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0L;

	/** The pattern name. */
	private final String pattern;

	/** Total number of comments matching this pattern. */
	private final int numOccurrences;

	/** Number of files containing this pattern. */
	private final int numFiles;

	/**
	 * Constructor initializing members.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param numOccurrences
	 *            number of comments matching this pattern
	 * @param numFiles
	 *            number of files containing this pattern
	 */
	public TodosPatternStatistics(String pattern, int numOccurrences,
			int numFiles) {
		this.pattern = pattern;
		this.numOccurrences = numOccurrences;
		this.numFiles = numFiles;
	}

	/**
	 * Get the pattern name.
	 * 
	 * @return the name
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Get the pattern name.
	 * 
	 * @return the HTML encoded name
	 */
	public String getPatternHtml() {
		return HtmlUtils.encodeText(pattern, true);
	}

	/**
	 * Get number of comments matching this pattern.
	 * 
	 * @return the number of occurrences
	 */
	public int getNumOccurrences() {
		return numOccurrences;
	}

	/**
	 * Get number of files containing this pattern.
	 * 
	 * @return the number of files
	 */
	public int getNumFiles() {
		return numFiles;
	}
}
