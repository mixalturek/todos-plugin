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
package org.jenkinsci.plugins.todos;

import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.io.Serializable;

import org.jenkinsci.plugins.todos.model.TodosReport;
import org.kohsuke.stapler.StaplerProxy;

/**
 * TODO: Description.
 * 
 * @author Michal Turek
 */
public class TodosBuildAction implements Action, Serializable, StaplerProxy {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	public static final String URL_NAME = "todosResult";

	private final AbstractBuild<?, ?> build;
	private final TodosResult result;

	public TodosBuildAction(AbstractBuild<?, ?> build, TodosResult result) {
		this.build = build;
		this.result = result;
	}

	public String getIconFileName() {
		return "/plugin/todos/icons/todos-24.png";
	}

	public String getDisplayName() {
		return "TODOs";
	}

	public String getUrlName() {
		return URL_NAME;
	}

	public String getSummary() {
		String retVal = "";

		if (result != null) {
			retVal = TodosReportSummary.createReportSummary(result.getReport(),
					getPreviousReport());
		}

		return retVal;
	}

	public String getDetails() {
		String retVal = "";

		if (result != null) {
			retVal = TodosReportSummary.createReportSummaryDetails(
					result.getReport(), getPreviousReport());
		}

		return retVal;
	}

	public TodosResult getResult() {
		return result;
	}

	private TodosReport getPreviousReport() {
		TodosResult previous = getPreviousResult();
		if (previous == null) {
			return null;
		} else {
			return previous.getReport();
		}
	}

	TodosResult getPreviousResult() {
		TodosBuildAction previousAction = getPreviousAction();
		TodosResult previousResult = null;

		if (previousAction != null) {
			previousResult = previousAction.getResult();
		}

		return previousResult;
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
		return result;
	}
}
