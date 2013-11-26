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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Statistics for a report. The class is thread safe after it is constructed.
 * 
 * @author Michal Turek
 */
public class TodosReportStatistics implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0L;

	/** Paths of source files that were used for parsing. */
	private final transient Set<String> sourceFiles;

	/** Total number of comments that were found. */
	private int totalComments;

	/** Total number of files containing comments. */
	private final int totalFiles;

	/** Statistics of one concrete pattern. */
	private final Map<String, PatternStatistics> patternStatistics = new HashMap<String, PatternStatistics>();

	/**
	 * Helper constructor for empty instance.
	 */
	public TodosReportStatistics() {
		this(new TodosReport(), new HashSet<String>());
	}

	/**
	 * Constructor.
	 * 
	 * @param report
	 *            the source report
	 * @param sourceFiles
	 *            the files from which the report was created
	 */
	public TodosReportStatistics(TodosReport report, Set<String> sourceFiles) {
		this.sourceFiles = Collections.unmodifiableSet(sourceFiles);

		Set<String> filesWithComments = new HashSet<String>();

		for (TodosComment comment : report.getComments()) {
			++totalComments;
			filesWithComments.add(comment.getFile());

			PatternStatistics storedPattern = patternStatistics.get(comment
					.getPattern());

			if (storedPattern == null) {
				storedPattern = new PatternStatistics();
				storedPattern.patternName = comment.getPattern();
				storedPattern.numOccurrences = 1;
				storedPattern.filesWithComment.add(comment.getFile());
				patternStatistics.put(comment.getPattern(), storedPattern);
			} else {
				++storedPattern.numOccurrences;
				storedPattern.filesWithComment.add(comment.getFile());
			}
		}

		totalFiles = filesWithComments.size();

		for (Map.Entry<String, PatternStatistics> entry : patternStatistics
				.entrySet()) {
			entry.getValue().numFiles = entry.getValue().filesWithComment
					.size();

			// Release the memory
			entry.getValue().filesWithComment = null;
		}
	}

	public Set<String> getSourceFiles() {
		// It is already an unmodifiable list
		return sourceFiles;
	}

	public int getTotalComments() {
		return totalComments;
	}

	public int getTotalFiles() {
		return totalFiles;
	}

	public Collection<PatternStatistics> getPatternStatistics() {
		// There are no public methods to update the values
		return patternStatistics.values();
	}

	/**
	 * Get statistics for a concrete pattern.
	 * 
	 * @param patternName
	 *            the pattern name
	 * @return the statistics of the pattern; if no statistics for such pattern
	 *         name are defined a new object initialized with zeros will be
	 *         returned
	 */
	public PatternStatistics getPatternStatistics(String patternName) {
		PatternStatistics pattern = patternStatistics.get(patternName);

		if (pattern == null) {
			pattern = new PatternStatistics();
			pattern.patternName = patternName;
			pattern.numOccurrences = 0;
			pattern.numFiles = 0;
		}

		return pattern;
	}

	/**
	 * Statistic for one pattern.
	 * 
	 * @author Michal Turek
	 */
	public static class PatternStatistics implements Serializable {
		/** Serial version UID. */
		private static final long serialVersionUID = 0L;

		/** The pattern name. */
		private String patternName;

		/** Number of occurrences of a pattern. */
		private int numOccurrences;

		/** Number of files with a pattern. */
		private int numFiles;

		/** Helper structure to compute number of files with this pattern. */
		private transient Set<String> filesWithComment = new HashSet<String>();

		public String getPatternName() {
			return patternName;
		}

		public int getNumOccurrences() {
			return numOccurrences;
		}

		public int getNumFiles() {
			return numFiles;
		}
	}
}
