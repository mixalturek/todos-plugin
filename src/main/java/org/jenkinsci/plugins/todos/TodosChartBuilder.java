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

import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;

import java.awt.Color;
import java.io.Serializable;
import java.util.Map;

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
	 * Build a trend chart from provided data.
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

		// crop extra space around the graph
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

		TodosBuildAction action = lastAction;

		do {
			TodosResult result = action.getResult();

			if (result != null) {
				NumberOnlyBuildLabel buildLabel = new NumberOnlyBuildLabel(
						action.getBuild());

				for (Map.Entry<String, Integer> entry : result.getReport()
						.getPatternsToCountMapping().entrySet()) {
					builder.add(entry.getValue(), entry.getKey(), buildLabel);
				}
			}

			action = action.getPreviousAction();
		} while (action != null);

		return builder.build();
	}
}
