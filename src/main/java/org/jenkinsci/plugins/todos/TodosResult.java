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

import java.io.File;

import org.jenkinsci.plugins.todos.model.TodosParser;
import org.jenkinsci.plugins.todos.model.TodosReport;

/**
 * Result object, that is responsible for processing web requests.
 * 
 * @author Michal Turek
 */
public class TodosResult {
	/** The build of this report. */
	private final AbstractBuild<?, ?> build;

	/**
	 * Constructor.
	 * 
	 * @param build
	 *            the build of this report
	 */
	public TodosResult(AbstractBuild<?, ?> build) {
		this.build = build;
	}

	/**
	 * Get the build.
	 * 
	 * @return the build
	 */
	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	/**
	 * Get diff between current and previous reports.
	 * 
	 * @return the report containing the diff
	 */
	public TodosReport getReportDiff() {
		return getCurrentReport().diffReports(getPreviousReport());
	}

	/**
	 * Get the current report.
	 * 
	 * @return the report
	 */
	private TodosReport getCurrentReport() {
		if (build == null) {
			return new TodosReport();
		}

		File destDir = new File(build.getRootDir(), TodosConstants.BUILD_SUBDIR);

		if (!destDir.exists()) {
			return new TodosReport();
		}

		return TodosParser.parseFiles(destDir.listFiles());
	}

	/**
	 * Get the previous valid report.
	 * 
	 * @return the report, empty report or null
	 */
	private TodosReport getPreviousReport() {
		if (build == null) {
			return new TodosReport();
		}

		AbstractBuild<?, ?> previousBuild = build.getPreviousBuild();

		while (previousBuild != null) {
			File destDir = new File(previousBuild.getRootDir(),
					TodosConstants.BUILD_SUBDIR);

			if (destDir.exists()) {
				return TodosParser.parseFiles(destDir.listFiles());
			}

			previousBuild = previousBuild.getPreviousBuild();
		}

		return null;
	}
}
