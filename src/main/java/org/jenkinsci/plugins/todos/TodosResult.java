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
package org.jenkinsci.plugins.todos;

import hudson.model.AbstractBuild;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.jenkinsci.plugins.todos.model.TodosComment;
import org.jenkinsci.plugins.todos.model.TodosReport;
import org.jenkinsci.plugins.todos.model.TodosReportStatistics;

/**
 * TODO: Description.
 * 
 * @author Michal Turek
 */
public class TodosResult implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	private final TodosReportStatistics statistics;
	private final AbstractBuild<?, ?> owner;

	public TodosResult(TodosReportStatistics statistics,
			AbstractBuild<?, ?> owner) {
		this.statistics = statistics;
		this.owner = owner;
	}

	public TodosReportStatistics getStatistics() {
		return statistics;
	}

	public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	public TodosReport getReport() {
		// TODO: Return real data
		List<TodosComment> comments = new LinkedList<TodosComment>();
		comments.add(new TodosComment("PATTERN", "FILE", -1, "TEXT"));
		comments.add(new TodosComment("TODO", "a.java", 42, "// TODO: aa"));
		comments.add(new TodosComment("TODO", "a.java", 4, "// TODO: aa"));
		comments.add(new TodosComment("TODO", "a.java", 6, "// TODO: aa"));
		comments.add(new TodosComment("FIXME", "b.java", 5, "// FIXME bb"));
		comments.add(new TodosComment("NOTE", "c.java", 5, "// NOTE bb"));
		comments.add(new TodosComment("OPEN", "d.java", 5, "// OPEN bb"));
		comments.add(new TodosComment("FIXME", "bb.java", 5, "// FIXME bb"));
		comments.add(new TodosComment("FIXME", "bb.java", 55, "// FIXME bb"));
		comments.add(new TodosComment("FIXME", "bb.java", 56, "// FIXME bb"));
		comments.add(new TodosComment("FIXME", "bb.java", 54, "// FIXME bb"));
		comments.add(new TodosComment("FIXME", "bb.java", 555, "// FIXME bb"));

		return new TodosReport(comments);
	}
}
