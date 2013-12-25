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

import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jenkinsci.plugins.todos.model.TodosPatternStatistics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleInsets;

/**
 * Build a trend chart.
 * 
 * @author Michal Turek
 */
public class TodosChartBuilder implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/**
	 * Build a trend chart from the provided data.
	 * 
	 * @param action
	 *            the build action
	 * @return the trend chart
	 */
	public static JFreeChart buildChart(TodosBuildAction action) {
		String strComments = Messages.Todos_ReportSummary_Comments();

		JFreeChart chart = ChartFactory.createStackedAreaChart(null, null,
				strComments, buildDataset(action), PlotOrientation.VERTICAL,
				true, false, true);

		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setForegroundAlpha(0.8f);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);

		CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
		plot.setDomainAxis(domainAxis);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// Crop extra space around the graph
		plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

		TodosAreaRenderer renderer = new TodosAreaRenderer(action.getUrlName());
		plot.setRenderer(renderer);

		return chart;
	}

	/**
	 * Build a data set that will be shown.
	 * 
	 * @param lastAction
	 *            the last build action
	 * @return the data set
	 */
	private static CategoryDataset buildDataset(TodosBuildAction lastAction) {
		DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

		List<TodosBuildAction> allValidActions = new LinkedList<TodosBuildAction>();
		Set<String> allPatterns = new HashSet<String>();

		getActionsAndPatterns(lastAction, allValidActions, allPatterns);

		for (TodosBuildAction action : allValidActions) {
			Set<String> remainingPatterns = new HashSet<String>(allPatterns);
			NumberOnlyBuildLabel buildLabel = new NumberOnlyBuildLabel(
					action.getBuild());

			for (TodosPatternStatistics statistics : action.getStatistics()
					.getPatternStatistics()) {
				builder.add(statistics.getNumOccurrences(),
						statistics.getPattern(), buildLabel);
				remainingPatterns.remove(statistics.getPattern());
			}

			for (String pattern : remainingPatterns) {
				builder.add(0, pattern, buildLabel);
			}
		}

		return builder.build();
	}

	/**
	 * Get all valid actions and all patterns defined through all builds.
	 * 
	 * @param lastAction
	 *            the last action
	 * @param outAllValidActions
	 *            all valid actions with statistics defined, output parameter
	 * @param outAllPatterns
	 *            all patterns defined through all builds
	 */
	private static void getActionsAndPatterns(TodosBuildAction lastAction,
			List<TodosBuildAction> outAllValidActions,
			Set<String> outAllPatterns) {
		outAllValidActions.clear();
		outAllPatterns.clear();

		TodosBuildAction action = lastAction;

		while (action != null) {
			if (action.getStatistics() != null) {
				for (TodosPatternStatistics statistics : action.getStatistics()
						.getPatternStatistics()) {
					outAllPatterns.add(statistics.getPattern());
				}

				outAllValidActions.add(action);
			}

			action = action.getPreviousAction();
		}
	}
}
