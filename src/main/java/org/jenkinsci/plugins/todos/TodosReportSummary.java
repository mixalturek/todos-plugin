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
import java.util.HashSet;
import java.util.Set;

import org.jenkinsci.plugins.todos.model.TodosPatternStatistics;
import org.jenkinsci.plugins.todos.model.TodosReportStatistics;

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
			String commentsDiff = "";
			String filesDiff = "";

			if (previous != null) {
				commentsDiff = getDifference(current.getNumComments(),
						previous.getNumComments());
				filesDiff = getDifference(current.getNumFiles(),
						previous.getNumFiles());
			}

			builder.append(Messages.comments_in_files(current.getNumComments(),
					commentsDiff, current.getNumFiles(), filesDiff));
			builder.append(" ");
			builder.append(Messages
					.Todos_ReportSummary_ShowDetails(TodosConstants.RESULTS_URL));
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
			for (String pattern : getAllPatterns(current, previous)) {
				String commentsDiff = "";
				String filesDiff = "";

				TodosPatternStatistics currentPatternstatistics = current
						.getPatternStatistics(pattern);

				if (previous != null) {
					TodosPatternStatistics previousPatternstatistics = previous
							.getPatternStatistics(pattern);

					commentsDiff = getDifference(
							currentPatternstatistics.getNumOccurrences(),
							previousPatternstatistics.getNumOccurrences());
					filesDiff = getDifference(
							currentPatternstatistics.getNumFiles(),
							previousPatternstatistics.getNumFiles());
				}

				builder.append("<li><pre style=\"display: inline;\">");
				builder.append(currentPatternstatistics.getPatternHtml());
				builder.append("</pre>: ");
				builder.append(Messages.comments_in_files(
						currentPatternstatistics.getNumOccurrences(),
						commentsDiff, currentPatternstatistics.getNumFiles(),
						filesDiff));
				builder.append(".</li>");
			}
		}

		return builder.toString();
	}

	/**
	 * Get the formatted difference of two integers.
	 * 
	 * @param current
	 *            current value
	 * @param previous
	 *            previous value
	 */
	private static String getDifference(int current, int previous) {
		int difference = current - previous;

		if (difference == 0) {
			return "";
		}

		// Minus sign is part of the difference variable (negative number)
		return ((difference > 0) ? " (+" : " (") + String.valueOf(difference)
				+ ")";
	}

	/**
	 * Get all patterns that are defined in both data sets.
	 * 
	 * @param current
	 *            the current statistics
	 * @param previous
	 *            the previous statistics
	 * @return the patterns
	 */
	private static Set<String> getAllPatterns(TodosReportStatistics current,
			TodosReportStatistics previous) {
		Set<String> patterns = new HashSet<String>();

		if (current != null) {
			for (TodosPatternStatistics statistics : current
					.getPatternStatistics()) {
				patterns.add(statistics.getPattern());
			}
		}

		if (previous != null) {
			for (TodosPatternStatistics statistics : previous
					.getPatternStatistics()) {
				patterns.add(statistics.getPattern());
			}
		}

		return patterns;
	}
}
