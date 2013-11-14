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
import hudson.model.AbstractProject;
import hudson.util.ChartUtil;

import java.io.IOException;
import java.io.Serializable;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * TODO: Description.
 * 
 * @author Michal Turek
 */
public class TodosProjectAction implements Action, Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	public static final String URL_NAME = "todosResult";

	public static final int CHART_WIDTH = 500;
	public static final int CHART_HEIGHT = 200;

	public AbstractProject<?, ?> project;

	public TodosProjectAction(final AbstractProject<?, ?> project) {
		this.project = project;
	}

	public String getIconFileName() {
		return "/plugin/todos/icons/todos-24.png";
	}

	public String getDisplayName() {
		return Messages.Todos_ProjectAction_Name();
	}

	public String getUrlName() {
		return URL_NAME;
	}

	/**
	 * 
	 * Redirects the index page to the last result.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doIndex(final StaplerRequest request,
			final StaplerResponse response) throws IOException {
		AbstractBuild<?, ?> build = getLastFinishedBuild();
		if (build != null) {
			response.sendRedirect2(String.format("../%d/%s", build.getNumber(),
					TodosBuildAction.URL_NAME));
		}
	}

	/**
	 * Returns the last finished build.
	 * 
	 * @return the last finished build or <code>null</code> if there is no such
	 *         build
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

	public final boolean hasValidResults() {
		AbstractBuild<?, ?> build = getLastFinishedBuild();

		if (build != null) {
			TodosBuildAction resultAction = build
					.getAction(TodosBuildAction.class);

			if (resultAction != null) {
				int nbr_results = 0;

				do {
					TodosResult result = resultAction.getResult();

					if (result != null) {
						nbr_results++;

						if (nbr_results > 1) {
							return true;
						}
					}

					resultAction = resultAction.getPreviousAction();

				} while (resultAction != null);
			}
		}

		return false;
	}

	/**
	 * Display the trend map. Delegates to the the associated
	 * {@link ResultAction}.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error
	 */
	public void doTrendMap(final StaplerRequest request,
			final StaplerResponse response) throws IOException {
		AbstractBuild<?, ?> lastBuild = this.getLastFinishedBuild();
		TodosBuildAction lastAction = lastBuild
				.getAction(TodosBuildAction.class);

		ChartUtil.generateClickableMap(request, response,
				TodosChartBuilder.buildChart(lastAction), CHART_WIDTH,
				CHART_HEIGHT);
	}

	/**
	 * Display the trend graph. Delegates to the the associated
	 * {@link ResultAction}.
	 * 
	 * @param request
	 *            Stapler request
	 * @param response
	 *            Stapler response
	 * @throws IOException
	 *             in case of an error in
	 *             {@link ResultAction#doGraph(StaplerRequest, StaplerResponse, int)}
	 */
	public void doTrend(final StaplerRequest request,
			final StaplerResponse response) throws IOException {
		AbstractBuild<?, ?> lastBuild = this.getLastFinishedBuild();
		TodosBuildAction lastAction = lastBuild
				.getAction(TodosBuildAction.class);

		ChartUtil.generateGraph(request, response,
				TodosChartBuilder.buildChart(lastAction), CHART_WIDTH,
				CHART_HEIGHT);
	}
}
