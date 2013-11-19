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
 * Report that stores all comments that were found.
 * 
 * @author Michal Turek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = "http://todos.sourceforge.net", name = "comments")
public class TodosReport implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/** All comments that were found. */
	@XmlElement(namespace = "http://todos.sourceforge.net", name = "comment", type = TodosComment.class)
	private final List<TodosComment> comments;

	/** The version of the file format. */
	@XmlAttribute
	private final String version;

	/**
	 * Constructor initializing an empty instance.
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
	 * Get number of comments containing a pattern.
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the number of comments with the specified pattern
	 */
	public int getCommentsWithPatternCount(String pattern) {
		if (pattern == null) {
			return 0;
		}

		int num = 0;

		for (TodosComment comment : comments) {
			if (pattern.equals(comment.getPattern())) {
				++num;
			}
		}

		return num;
	}

	/**
	 * Get mapping of patterns to their counts in the comments.
	 * 
	 * @return the mapping; the key is pattern, the value is the number of its
	 *         occurrences in the comments
	 */
	public Map<String, Integer> getPatternsToCountMapping() {
		Map<String, Integer> results = new HashMap<String, Integer>();

		for (TodosComment comment : getComments()) {
			Integer num = results.get(comment.getPattern());

			if (num == null) {
				results.put(comment.getPattern(), 1);
			} else {
				results.put(comment.getPattern(), num + 1);
			}
		}

		return results;
	}

	/**
	 * Get files containing at least one comment.
	 * 
	 * @return the files containing at least one comment
	 */
	public Set<String> getFiles() {
		Set<String> files = new HashSet<String>();

		for (TodosComment comment : comments) {
			files.add(comment.getFile());
		}

		return files;
	}

	/**
	 * Get files containing at least one comment with the specified pattern.
	 * 
	 * @param pattern
	 *            the pattern
	 * 
	 * @return the files containing at least one comment with the specified
	 *         pattern
	 */
	public Set<String> getFilesWithPattern(String pattern) {
		Set<String> files = new HashSet<String>();

		if (pattern == null) {
			return files;
		}

		for (TodosComment comment : comments) {
			if (pattern.equals(comment.getPattern())) {
				files.add(comment.getFile());
			}
		}

		return files;
	}
}
