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

import java.io.Serializable;
import java.util.Map;

import org.jenkinsci.plugins.todos.model.TodosReport;

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
	 * @param report
	 *            current report
	 * @param previous
	 *            previous report
	 * @return a string with the summary
	 */
	public static String createReportSummary(TodosReport report,
			TodosReport previous) {
		StringBuilder builder = new StringBuilder();

		if (report != null) {
			builder.append("<a href=\"");
			builder.append(TodosBuildAction.URL_NAME);
			builder.append("\">");
			builder.append(report.getCommentsCount());

			if (previous != null) {
				printDifference(report.getCommentsCount(),
						previous.getCommentsCount(), builder);
			}

			builder.append(" ");
			builder.append(Messages.Todos_ReportSummary_Comments());
			builder.append("</a> ");
			builder.append(Messages.Todos_ReportSummary_in());
			builder.append(" ");
			builder.append(report.getFiles().size());

			if (previous != null) {
				printDifference(report.getFiles().size(), previous.getFiles()
						.size(), builder);
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
	 * @param report
	 *            current report
	 * @param previous
	 *            previous report
	 * @return a string with the summary details
	 */
	public static String createReportSummaryDetails(TodosReport report,
			TodosReport previous) {
		StringBuilder builder = new StringBuilder();

		if (report != null) {
			for (Map.Entry<String, Integer> entry : report
					.getPatternsToCountMapping().entrySet()) {
				builder.append("<li>");
				builder.append("<a href=\"");
				builder.append(TodosBuildAction.URL_NAME);
				// TODO: constants, was /languageResult/
				builder.append("/patternResult/");
				builder.append(HtmlUtils.encodeUrl(entry.getKey()));
				builder.append("\">");
				builder.append(HtmlUtils.encodeText(entry.getKey(), true));
				builder.append("</a>: ");
				builder.append(entry.getValue());

				if (previous != null) {
					printDifference(
							entry.getValue(),
							previous.getCommentsWithPatternCount(entry.getKey()),
							builder);
				}

				builder.append(" ");
				builder.append(Messages.Todos_ReportSummary_Comments());
				builder.append(" ");
				builder.append(Messages.Todos_ReportSummary_in());
				builder.append(" ");
				builder.append(report.getFilesWithPattern(entry.getKey())
						.size());

				if (previous != null) {
					printDifference(
							report.getFilesWithPattern(entry.getKey()).size(),
							previous.getFilesWithPattern(entry.getKey()).size(),
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
