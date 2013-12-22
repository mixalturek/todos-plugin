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

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.io.Serializable;

import org.jenkinsci.plugins.todos.model.TodosReportStatistics;
import org.kohsuke.stapler.StaplerProxy;

/**
 * TODO: Description.
 * 
 * @author Michal Turek
 */
public class TodosBuildAction implements Action, Serializable, StaplerProxy {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	private final AbstractBuild<?, ?> build;
	private final TodosReportStatistics statistics;

	public TodosBuildAction(AbstractBuild<?, ?> build,
			TodosReportStatistics statistics) {
		this.build = build;
		this.statistics = statistics;
	}

	public String getIconFileName() {
		return "/plugin/todos/icons/todos-24.png";
	}

	public String getDisplayName() {
		return "TODOs";
	}

	public String getUrlName() {
		return TodosConstants.RESULTS_URL;
	}

	public String getSummary() {
		String retVal = "";

		if (statistics != null) {
			retVal = TodosReportSummary.createReportSummary(statistics,
					getPreviousStatistics());
		}

		return retVal;
	}

	public String getDetails() {
		String retVal = "";

		if (statistics != null) {
			retVal = TodosReportSummary.createReportSummaryDetails(statistics,
					getPreviousStatistics());
		}

		return retVal;
	}

	public TodosReportStatistics getStatistics() {
		return statistics;
	}

	TodosReportStatistics getPreviousStatistics() {
		TodosBuildAction previousAction = getPreviousAction();
		TodosReportStatistics previousStatistics = null;

		if (previousAction != null) {
			previousStatistics = previousAction.getStatistics();
		}

		return previousStatistics;
	}

	TodosBuildAction getPreviousAction() {
		if (build != null) {
			AbstractBuild<?, ?> previousBuild = build.getPreviousBuild();

			if (previousBuild != null) {
				return previousBuild.getAction(TodosBuildAction.class);
			}
		}

		return null;
	}

	AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public Object getTarget() {
		return new TodosResult(build);
	}
}
