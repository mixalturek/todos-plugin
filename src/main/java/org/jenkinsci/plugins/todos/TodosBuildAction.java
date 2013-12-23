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
 * Build action for interaction with the user.
 * 
 * @author Michal Turek
 */
public class TodosBuildAction implements Action, Serializable, StaplerProxy {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/** The build that this object is associated to. */
	private final AbstractBuild<?, ?> build;

	/** Report statistics for this build. */
	private final TodosReportStatistics statistics;

	/**
	 * Constructor.
	 * 
	 * @param build
	 *            the build that this object is associated to
	 * @param statistics
	 *            report statistics for this build
	 */
	public TodosBuildAction(AbstractBuild<?, ?> build,
			TodosReportStatistics statistics) {
		this.build = build;
		this.statistics = statistics;
	}

	/**
	 * Get the icon.
	 * 
	 * @see hudson.model.Action#getIconFileName()
	 */
	public String getIconFileName() {
		return TodosConstants.ICON_24PX;
	}

	/**
	 * Get the large icon.
	 * 
	 * @return the icon
	 */
	public String getIconFileNameLarge() {
		return TodosConstants.ICON_48PX;
	}

	/**
	 * Get the display name.
	 * 
	 * @see hudson.model.Action#getDisplayName()
	 */
	public String getDisplayName() {
		return TodosConstants.PLUGIN_NAME;
	}

	/**
	 * Get the URL for results.
	 * 
	 * @see hudson.model.Action#getUrlName()
	 */
	public String getUrlName() {
		return TodosConstants.RESULTS_URL;
	}

	/**
	 * Get the report summary in a HTML form.
	 * 
	 * @return the summary or empty string
	 */
	public String getSummary() {
		if (statistics == null) {
			return "";
		}

		return TodosReportSummary.createReportSummary(statistics,
				getPreviousStatistics());
	}

	/**
	 * Get the report summary details in a HTML form.
	 * 
	 * @return the summary details or empty string
	 */
	public String getSummaryDetails() {
		if (statistics == null) {
			return "";
		}

		return TodosReportSummary.createReportSummaryDetails(statistics,
				getPreviousStatistics());
	}

	/**
	 * Get the statistics.
	 * 
	 * @return the statistics or null
	 */
	public TodosReportStatistics getStatistics() {
		return statistics;
	}

	/**
	 * Get statistics of a previous build.
	 * 
	 * @return the statistics or null
	 */
	private TodosReportStatistics getPreviousStatistics() {
		TodosBuildAction previousAction = getPreviousAction();

		if (previousAction == null) {
			return null;
		}

		return previousAction.getStatistics();
	}

	/**
	 * Get the action of the previous build.
	 * 
	 * @return the action or null
	 */
	TodosBuildAction getPreviousAction() {
		if (build != null) {
			AbstractBuild<?, ?> previousBuild = build.getPreviousBuild();

			if (previousBuild != null) {
				return previousBuild.getAction(TodosBuildAction.class);
			}
		}

		return null;
	}

	/**
	 * Get the associated build.
	 * 
	 * @return the build or null
	 */
	AbstractBuild<?, ?> getBuild() {
		return build;
	}

	/**
	 * Get the object that is responsible for processing web requests.
	 * 
	 * @see org.kohsuke.stapler.StaplerProxy#getTarget()
	 */
	public Object getTarget() {
		return new TodosResult(build);
	}
}
