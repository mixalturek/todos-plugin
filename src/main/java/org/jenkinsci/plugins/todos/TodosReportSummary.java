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

import org.jenkinsci.plugins.todos.model.TodosReport;

/**
 * TODO: Description.
 * 
 * @author Michal Turek
 */
public class TodosReportSummary implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	public static String createReportSummary(TodosReport report,
			TodosReport previous) {
		StringBuilder builder = new StringBuilder();

		if ((report != null) && (previous != null)) {

			String strLines = Messages.Todos_ReportSummary_Lines();
			String strFiles = Messages.Todos_ReportSummary_Files();
			String strAnd = Messages.Todos_ReportSummary_and();
			String strLanguages = Messages.Todos_ReportSummary_Languages();

			builder.append("<a href=\"" + TodosBuildAction.URL_NAME + "\">");
			/*
			// TODO: Uncomment and solve errors
			builder.append(report.getLineCountString());
			printDifference(report.getLineCount(), previous.getLineCount(),
					builder);

			builder.append(" " + strLines + "</a> in ");
			builder.append(report.getFileCountString());
			printDifference(report.getFileCount(), previous.getFileCount(),
					builder);

			builder.append(" " + strFiles + " " + strAnd + " ");
			builder.append(report.getLanguageCountString());
			printDifference(report.getLanguageCount(),
					previous.getLanguageCount(), builder);
			*/

			builder.append("report.getLineCountString()");
			printDifference(0, 42, builder);

			builder.append(" " + strLines + "</a> in ");
			builder.append("report.getFileCountString()");
			printDifference(0, 42, builder);

			builder.append(" " + strFiles + " " + strAnd + " ");
			builder.append("report.getLanguageCountString()");
			printDifference(0, 42, builder);

			builder.append(" " + strLanguages + ".");
		}

		return builder.toString();
	}

	public static String createReportSummaryDetails(TodosReport report,
			TodosReport previous) {

		StringBuilder builder = new StringBuilder();

		if ((report != null) && (previous != null)) {

			/*
			for (Language language : report.getLanguages()) {

				Language previousLanguage = null;
				previousLanguage = previous.getLanguage(language.getName());

				appendLanguageDetails(language, previousLanguage, builder);
			}
			*/
		}

		return builder.toString();
	}

	/*
	private static void appendLanguageDetails(Language language,
			Language previous, StringBuilder builder) {

		String strLines = Messages.Todos_ReportSummary_Lines();
		String strFiles = Messages.Todos_ReportSummary_Files();

		builder.append("<li>");
		builder.append("<a href=\"");
		builder.append(TodosBuildAction.URL_NAME);
		builder.append("/languageResult/");
		builder.append(language.getName());
		builder.append("\">");
		builder.append(language.getName());
		builder.append("</a> : ");
		builder.append(language.getLineCountString());
		if (previous != null) {
			printDifference(language.getLineCount(), previous.getLineCount(),
					builder);
		}
		// TODO: localization
		builder.append(" " + strLines + " in ");
		builder.append(language.getFileCountString());
		if (previous != null) {
			printDifference(language.getFileCount(), previous.getFileCount(),
					builder);
		}
		builder.append(" " + strFiles + ".</li>");
	}
	*/

	private static void printDifference(int current, int previous,
			StringBuilder builder) {
		int difference = current - previous;

		if (difference > 0) {
			builder.append(" (+");
			builder.append(grouping(difference));
			builder.append(")");
		} else if (difference == 0) {
			// do nothing
		} else {
			builder.append(" ("); // minus sign is part of the difference
									// variable (negative number)
			builder.append(grouping(difference));
			builder.append(")");
		}
	}

	public static String grouping(int value) {
		// TODO: Is it correct?
		return String.format("%,d", value);
	}
}
