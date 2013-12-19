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

import java.io.Serializable;

import org.jenkinsci.plugins.todos.model.TodosReportStatistics;
import org.jenkinsci.plugins.todos.model.TodosReportStatistics.PatternStatistics;

/**
 * Display the report summary and top-level details.
 * 
 * @author Michal Turek
 */
public class TodosReportSummary implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/**
	 * Generate the report summary.
	 * 
	 * @param current
	 *            current report statistics
	 * @param previous
	 *            previous report statistics
	 * @return a string with the summary
	 */
	public static String createReportSummary(TodosReportStatistics current,
			TodosReportStatistics previous) {
		StringBuilder builder = new StringBuilder();

		if (current != null) {
			builder.append("<a href=\"");
			builder.append(TodosBuildAction.URL_NAME);
			builder.append("\">");
			builder.append(current.getTotalComments());

			if (previous != null) {
				printDifference(current.getTotalComments(),
						previous.getTotalComments(), builder);
			}

			builder.append(" ");
			builder.append(Messages.Todos_ReportSummary_Comments());
			builder.append("</a> ");
			builder.append(Messages.Todos_ReportSummary_in());
			builder.append(" ");
			builder.append(current.getTotalFiles());

			if (previous != null) {
				printDifference(current.getTotalFiles(),
						previous.getTotalFiles(), builder);
			}

			builder.append(" ");
			builder.append(Messages.Todos_ReportSummary_Files());
			builder.append(".");
		}

		return builder.toString();
	}

	/**
	 * Build summary details.
	 * 
	 * @param current
	 *            current report statistics
	 * @param previous
	 *            previous report statistics
	 * @return a string with the summary details
	 */
	public static String createReportSummaryDetails(
			TodosReportStatistics current, TodosReportStatistics previous) {
		StringBuilder builder = new StringBuilder();

		if (current != null) {
			for (PatternStatistics statistics : current.getPatternStatistics()) {
				builder.append("<li>");
				builder.append(HtmlUtils.encodeText(
						statistics.getPatternName(), true));
				builder.append(": ");
				builder.append(statistics.getNumOccurrences());

				if (previous != null) {
					printDifference(statistics.getNumOccurrences(), previous
							.getPatternStatistics(statistics.getPatternName())
							.getNumOccurrences(), builder);
				}

				builder.append(" ");
				builder.append(Messages.Todos_ReportSummary_Comments());
				builder.append(" ");
				builder.append(Messages.Todos_ReportSummary_in());
				builder.append(" ");
				builder.append(current.getPatternStatistics(
						statistics.getPatternName()).getNumFiles());

				if (previous != null) {
					printDifference(
							current.getPatternStatistics(
									statistics.getPatternName()).getNumFiles(),
							previous.getPatternStatistics(
									statistics.getPatternName()).getNumFiles(),
							builder);
				}

				builder.append(" ");
				builder.append(Messages.Todos_ReportSummary_Files());
				builder.append(".</li>");
			}
		}

		return builder.toString();
	}

	/**
	 * Print the formatted difference of two integers.
	 * 
	 * @param current
	 *            current value
	 * @param previous
	 *            previous value
	 * @param builder
	 *            string builder for output
	 */
	private static void printDifference(int current, int previous,
			StringBuilder builder) {
		int difference = current - previous;

		if (difference == 0) {
			return;
		}

		// Minus sign is part of the difference variable (negative number)
		builder.append((difference > 0) ? " (+" : " (");
		builder.append(difference);
		builder.append(")");
	}
}
