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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Report that stores all comments that were found. The class is thread safe.
 * 
 * @author Michal Turek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "http://todos.sourceforge.net", name = "comments")
public class TodosReport {
	/** All comments that were found. */
	@XmlElement(namespace = "http://todos.sourceforge.net", name = "comment", type = TodosComment.class)
	private final List<TodosComment> comments;

	/** The version of the file format. */
	@XmlAttribute
	private final String version;

	/**
	 * Helper constructor to create an empty instance.
	 */
	public TodosReport() {
		this(Collections.<TodosComment> emptyList(), "");
	}

	/**
	 * Constructor initializing members.
	 * 
	 * @param comments
	 *            all comments that were found
	 * @param version
	 *            the version of the file format if loaded from a file
	 */
	public TodosReport(List<TodosComment> comments) {
		this(comments, "");
	}

	/**
	 * Constructor initializing members.
	 * 
	 * @param comments
	 *            all comments that were found
	 * @param version
	 *            the version of the file format if loaded from a file
	 */
	private TodosReport(List<TodosComment> comments, String version) {
		this.comments = new ArrayList<TodosComment>(comments);
		this.version = version;
	}

	/**
	 * Get all comments.
	 * 
	 * @return the comments
	 */
	public List<TodosComment> getComments() {
		return Collections.unmodifiableList(comments);
	}

	/**
	 * Concatenate two reports.
	 * 
	 * @param report
	 *            the report to concatenate with this one
	 * @return a new report that contains items from this instance followed by
	 *         items from the instance passed in the parameter
	 */
	public TodosReport concatenate(TodosReport report) {
		String version = (!this.version.isEmpty()) ? this.version
				: report.version;

		List<TodosComment> tmpList = new ArrayList<TodosComment>(comments);
		tmpList.addAll(report.getComments());
		return new TodosReport(tmpList, version);
	}

	/**
	 * Get total number of comments.
	 * 
	 * @return the number of comments
	 */
	public int getCommentsCount() {
		return comments.size();
	}

	/**
	 * Get the statistics for this report.
	 * 
	 * @param sourceFiles
	 *            the list of files from which the original report was created
	 * @return the statistics
	 */
	public TodosReportStatistics getStatistics(List<File> sourceFiles) {
		Set<String> filesWithComments = new HashSet<String>();
		Map<String, PatternStatistics> patternStatistics = new HashMap<String, PatternStatistics>();

		for (TodosComment comment : comments) {
			filesWithComments.add(comment.getFile());

			PatternStatistics storedPattern = patternStatistics.get(comment
					.getPattern());

			if (storedPattern == null) {
				storedPattern = new PatternStatistics(comment.getPattern(),
						comment.getFile());
				patternStatistics.put(comment.getPattern(), storedPattern);
			} else {
				storedPattern.increment(comment.getFile());
			}
		}

		return convertStatistics(patternStatistics, sourceFiles);
	}

	/**
	 * Convert statistics from internal to the final one.
	 * 
	 * @param sourceStatistics
	 *            the source statistics
	 * @param sourceFiles
	 *            the list of files from which the original report was created
	 * @return the statistics
	 */
	private TodosReportStatistics convertStatistics(
			Map<String, PatternStatistics> sourceStatistics,
			List<File> sourceFiles) {
		List<TodosPatternStatistics> statistics = new ArrayList<TodosPatternStatistics>(
				sourceStatistics.size());

		for (Map.Entry<String, PatternStatistics> entry : sourceStatistics
				.entrySet()) {
			statistics.add(new TodosPatternStatistics(entry.getValue().pattern,
					entry.getValue().numOccurrences,
					entry.getValue().filesWithComment.size()));
		}

		return new TodosReportStatistics(statistics, sourceFiles);
	}

	/**
	 * Helper structure to compute statistics of a pattern. For internal use
	 * only.
	 * 
	 * @author Michal Turek
	 */
	private static class PatternStatistics {
		/** The pattern name. */
		public String pattern;

		/** Number of occurrences of a pattern. */
		public int numOccurrences;

		/** Helper structure to compute number of files with this pattern. */
		public Set<String> filesWithComment;

		/**
		 * Constructor initializing members.
		 * 
		 * @param pattern
		 *            the pattern name
		 * @param file
		 *            the file in which the comment was found
		 */
		public PatternStatistics(String pattern, String file) {
			this.pattern = pattern;
			this.numOccurrences = 1;
			this.filesWithComment = new HashSet<String>();
			filesWithComment.add(file);
		}

		/**
		 * New occurrence was found, increment the counters.
		 * 
		 * @param file
		 *            the file in which the comment was found
		 */
		public void increment(String file) {
			++numOccurrences;
			filesWithComment.add(file);
		}
	}
}
