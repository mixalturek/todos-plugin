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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Statistics of a report. The class is thread safe.
 * 
 * @author Michal Turek
 */
public class TodosReportStatistics implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0L;

	/** Statistics per patterns. */
	private final List<TodosPatternStatistics> patternStatistics;

	/**
	 * Helper constructor to create an empty instance.
	 */
	public TodosReportStatistics() {
		this(new ArrayList<TodosPatternStatistics>());
	}

	/**
	 * Constructor.
	 * 
	 * @param patternStatistics
	 *            statistics per patterns
	 */
	public TodosReportStatistics(List<TodosPatternStatistics> patternStatistics) {
		this.patternStatistics = new ArrayList<TodosPatternStatistics>(
				patternStatistics);
	}

	/**
	 * Get number of comments that were found.
	 * 
	 * @return the number of comments
	 */
	public int getNumComments() {
		int numComments = 0;

		for (TodosPatternStatistics statistics : patternStatistics) {
			numComments += statistics.getNumOccurrences();
		}

		return numComments;
	}

	/**
	 * Get number of files containing the comments.
	 * 
	 * @return the number of files
	 */
	public int getNumFiles() {
		int numFiles = 0;

		for (TodosPatternStatistics statistics : patternStatistics) {
			numFiles += statistics.getNumFiles();
		}

		return numFiles;
	}

	/**
	 * Get statistics of all patterns.
	 * 
	 * @return unmodifiable list with statistics
	 */
	public List<TodosPatternStatistics> getPatternStatistics() {
		return Collections.unmodifiableList(patternStatistics);
	}

	/**
	 * Get statistics of a concrete pattern.
	 * 
	 * @param pattern
	 *            the pattern name
	 * @return the statistics of the pattern; if no statistics for such pattern
	 *         is defined a new object initialized with zeros will be returned
	 */
	public TodosPatternStatistics getPatternStatistics(String pattern) {
		for (TodosPatternStatistics statistics : patternStatistics) {
			if (statistics.getPattern().equals(pattern)) {
				return statistics;
			}
		}

		return new TodosPatternStatistics(pattern, 0, 0);
	}
}
