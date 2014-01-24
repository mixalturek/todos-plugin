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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
public class TodosReport implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 1L;

	/** All comments that were found. */
	@XmlElement(namespace = "http://todos.sourceforge.net", name = "comment", type = TodosComment.class)
	private final List<TodosComment> comments;

	/** The version of the file format. */
	@XmlAttribute
	private final String version;

	/** The list of files from which the original report was created. */
	private final List<SlaveFile> sourceFiles;

	/**
	 * Helper constructor to create an empty instance.
	 */
	public TodosReport() {
		this(Collections.<TodosComment> emptyList(), Collections
				.<SlaveFile> emptyList(), "");
	}

	/**
	 * Constructor initializing members.
	 * 
	 * @param comments
	 *            all comments that were found
	 */
	public TodosReport(List<TodosComment> comments) {
		this(comments, Collections.<SlaveFile> emptyList(), "");
	}

	/**
	 * Constructor initializing members.
	 * 
	 * @param comments
	 *            all comments that were found
	 * @param sourceFiles
	 *            the list of files from which the original report was created
	 * @param version
	 *            the version of the file format if loaded from a file
	 */
	private TodosReport(List<TodosComment> comments,
			List<SlaveFile> sourceFiles, String version) {
		this.comments = new ArrayList<TodosComment>(comments);
		this.sourceFiles = new ArrayList<SlaveFile>(sourceFiles);
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
	 * @param inputFile
	 *            the file from which the original report was created
	 * @return a new report that contains items from this instance followed by
	 *         items from the instance passed in the parameter
	 */
	public TodosReport concatenate(TodosReport report, File inputFile) {
		String version = (!this.version.isEmpty()) ? this.version
				: report.version;

		List<TodosComment> commentsList = new ArrayList<TodosComment>(comments);
		commentsList.addAll(report.getComments());

		List<SlaveFile> filesList = new ArrayList<SlaveFile>(sourceFiles);
		filesList.add(new SlaveFile(inputFile));

		return new TodosReport(commentsList, filesList, version);
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
		return concatenate(report, new File(""));
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
	 * Get list of files from which the original report was created.
	 * 
	 * @return unmodifiable list with files
	 */
	public List<SlaveFile> getSourceFiles() {
		return Collections.unmodifiableList(sourceFiles);
	}

	/**
	 * Get the statistics for this report.
	 * 
	 * @return the statistics
	 */
	public TodosReportStatistics getStatistics() {
		Set<String> filesWithComments = new HashSet<String>();
		Map<String, PatternStatistics> patternStatistics = new HashMap<String, PatternStatistics>();

		for (TodosComment comment : comments) {
			filesWithComments.add(comment.getFile());

			PatternStatistics storedPattern = patternStatistics.get(comment
					.getPattern());

			if (storedPattern == null) {
				patternStatistics.put(
						comment.getPattern(),
						new PatternStatistics(comment.getPattern(), comment
								.getFile()));
			} else {
				storedPattern.increment(comment.getFile());
			}
		}

		return convertStatistics(patternStatistics);
	}

	/**
	 * Convert statistics from internal to the final one.
	 * 
	 * @param sourceStatistics
	 *            the source statistics
	 * @return the statistics
	 */
	private TodosReportStatistics convertStatistics(
			Map<String, PatternStatistics> sourceStatistics) {
		List<TodosPatternStatistics> statistics = new ArrayList<TodosPatternStatistics>(
				sourceStatistics.size());

		for (Map.Entry<String, PatternStatistics> entry : sourceStatistics
				.entrySet()) {
			statistics.add(new TodosPatternStatistics(entry.getValue().pattern,
					entry.getValue().numOccurrences,
					entry.getValue().filesWithComment.size()));
		}

		return new TodosReportStatistics(statistics);
	}

	/**
	 * Diff two reports.
	 * 
	 * @param previous
	 *            the previous/older report
	 * @return the newly generated report with the results
	 */
	public TodosReport diffReports(TodosReport previousReport) {
		if (previousReport == null) {
			return this;
		}

		List<TodosComment> current = comments;
		List<TodosComment> previous = new LinkedList<TodosComment>(
				previousReport.getComments());
		List<TodosComment> results = new LinkedList<TodosComment>();

		Iterator<TodosComment> it = current.iterator();

		// Exact match
		while (it.hasNext()) {
			TodosComment comment = it.next();

			if (findAndRemoveComment(previous, comment, true)) {
				results.add(new TodosComment(comment, TodosDiffStatus.UNCHANGED));
				it.remove();
			}
		}

		// Inexact match of the rest
		for (TodosComment comment : current) {
			if (findAndRemoveComment(previous, comment, false)) {
				results.add(new TodosComment(comment, TodosDiffStatus.UNCHANGED));
			} else {
				results.add(new TodosComment(comment, TodosDiffStatus.NEW));
			}
		}

		// Deleted comments
		for (TodosComment comment : previous) {
			results.add(new TodosComment(comment, TodosDiffStatus.SOLVED));
		}

		// Sort according to diff status
		Collections.sort(results, new Comparator<TodosComment>() {
			public int compare(TodosComment o1, TodosComment o2) {
				return o1.getDiffStatus().ordinal()
						- o2.getDiffStatus().ordinal();
			}
		});

		return new TodosReport(results);
	}

	/**
	 * Search a comment in a list of comments. Remove the comment from the list
	 * on match.
	 * 
	 * @param comments
	 *            the list of comments
	 * @param search
	 *            the comment to search
	 * @param exactMatch
	 *            if true check file, line and source code, if false check only
	 *            file and source code
	 * @return true if the comment was found and removed from the list
	 */
	private boolean findAndRemoveComment(List<TodosComment> comments,
			TodosComment search, boolean exactMatch) {
		Iterator<TodosComment> it = comments.iterator();

		if (exactMatch) {
			while (it.hasNext()) {
				TodosComment comment = it.next();

				if (comment.getLine() == search.getLine()
						&& comment.getFile().equals(search.getFile())
						&& comment.getSourceCode().equals(
								search.getSourceCode())) {
					it.remove();
					return true;
				}
			}
		} else {
			while (it.hasNext()) {
				TodosComment comment = it.next();

				if (comment.getFile().equals(search.getFile())
						&& comment.getSourceCode().equals(
								search.getSourceCode())) {
					it.remove();
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get number of new comments, compared to the previous report.
	 * 
	 * @return the number of comments
	 */
	public int getNewCommentsCount() {
		int num = 0;

		for (TodosComment comment : comments) {
			if (comment.getDiffStatus() == TodosDiffStatus.NEW) {
				++num;
			}
		}

		return num;
	}

	/**
	 * Get number of solved comments, compared to the previous report.
	 * 
	 * @return the number of comments
	 */
	public int getSolvedCommentsCount() {
		int num = 0;

		for (TodosComment comment : comments) {
			if (comment.getDiffStatus() == TodosDiffStatus.SOLVED) {
				++num;
			}
		}

		return num;
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

	/**
	 * Helper class to store a file name and an absolute path relative to the
	 * slave machine.
	 * 
	 * @author Michal Turek
	 */
	public static class SlaveFile implements Serializable {
		/** Serial version UID. */
		private static final long serialVersionUID = 0L;

		/** The file name. */
		private final String name;

		/** The absolute path to the file. */
		private final String absolutePath;

		/**
		 * Constructor.
		 * 
		 * @param file
		 *            the file in the file system
		 */
		public SlaveFile(File file) {
			this.name = file.getName();
			this.absolutePath = file.getAbsolutePath();
		}

		/**
		 * Get the file name.
		 * 
		 * @return the file name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the absolute path to the file.
		 * 
		 * @return the absolute path
		 */
		public String getAbsolutePath() {
			return absolutePath;
		}
	}
}
