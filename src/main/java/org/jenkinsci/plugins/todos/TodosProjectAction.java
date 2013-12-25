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
import hudson.model.AbstractProject;
import hudson.util.ChartUtil;

import java.io.IOException;
import java.io.Serializable;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Project action for interaction with the user.
 * 
 * @author Michal Turek
 */
public class TodosProjectAction implements Action, Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/** The associated project. */
	public AbstractProject<?, ?> project;

	/**
	 * Constructor.
	 * 
	 * @param project
	 *            the associated project
	 */
	public TodosProjectAction(AbstractProject<?, ?> project) {
		this.project = project;
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
	 * Redirects the index page to the last result.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doIndex(StaplerRequest request, StaplerResponse response)
			throws IOException {
		AbstractBuild<?, ?> build = getLastFinishedBuild();

		if (build == null) {
			// Click to the link in menu on the job page before the first build
			response.sendRedirect2("..");
			return;
		}

		response.sendRedirect2(String.format("../%d/%s", build.getNumber(),
				TodosConstants.RESULTS_URL));
	}

	/**
	 * Returns the last finished build.
	 * 
	 * @return the last finished build or null if there is no such build
	 */
	public AbstractBuild<?, ?> getLastFinishedBuild() {
		AbstractBuild<?, ?> lastBuild = project.getLastBuild();

		while (lastBuild != null
				&& (lastBuild.isBuilding() || lastBuild
						.getAction(TodosBuildAction.class) == null)) {
			lastBuild = lastBuild.getPreviousBuild();
		}

		return lastBuild;
	}

	/**
	 * Check that the project has valid build results.
	 * 
	 * @return true if the project has at least two valid results, otherwise
	 *         false
	 */
	public final boolean hasValidResults() {
		AbstractBuild<?, ?> build = getLastFinishedBuild();

		if (build == null) {
			return false;
		}

		TodosBuildAction action = build.getAction(TodosBuildAction.class);

		int numResults = 0;

		while (action != null) {
			if (action.getStatistics() != null) {
				++numResults;

				if (numResults > 1) {
					return true;
				}
			}

			action = action.getPreviousAction();
		}

		return false;
	}

	/**
	 * Display the trend map.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doTrendMap(StaplerRequest request, StaplerResponse response)
			throws IOException {
		AbstractBuild<?, ?> lastBuild = this.getLastFinishedBuild();

		if (lastBuild == null) {
			return;
		}

		TodosBuildAction lastAction = lastBuild
				.getAction(TodosBuildAction.class);

		if (lastAction == null) {
			return;
		}

		ChartUtil.generateClickableMap(request, response,
				TodosChartBuilder.buildChart(lastAction),
				TodosConstants.CHART_WIDTH, TodosConstants.CHART_HEIGHT);
	}

	/**
	 * Display the trend graph.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doTrend(StaplerRequest request, StaplerResponse response)
			throws IOException {
		AbstractBuild<?, ?> lastBuild = this.getLastFinishedBuild();

		if (lastBuild == null) {
			return;
		}

		TodosBuildAction lastAction = lastBuild
				.getAction(TodosBuildAction.class);

		if (lastAction == null) {
			return;
		}

		ChartUtil.generateGraph(request, response,
				TodosChartBuilder.buildChart(lastAction),
				TodosConstants.CHART_WIDTH, TodosConstants.CHART_HEIGHT);
	}
}
