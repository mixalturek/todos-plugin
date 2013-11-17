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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class TodosReport {
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
		List<TodosComment> tmpList = new ArrayList<TodosComment>(comments);
		tmpList.addAll(report.getComments());
		return new TodosReport(tmpList);
	}
}
