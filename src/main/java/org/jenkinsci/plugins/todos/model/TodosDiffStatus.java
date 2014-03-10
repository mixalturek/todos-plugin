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
package org.jenkinsci.plugins.todos.model;

import org.jenkinsci.plugins.todos.Messages;

/**
 * Status of comparison of two reports.
 * 
 * Implementation note: The declaration order of the constants is significant,
 * it's used in sorting.
 * 
 * @author Michal Turek
 * 
 * @see TodosReport#diffReports(TodosReport)
 */
public enum TodosDiffStatus {
	/** The comment is present only in the current report. */
	NEW {
		@Override
		public String getCss() {
			return "new";
		}

		@Override
		public String getText() {
			return Messages.Todos_DiffStatus_New();
		}
	},

	/** The comment is present only in the previous report. */
	SOLVED {
		@Override
		public String getCss() {
			return "solved";
		}

		@Override
		public String getText() {
			return Messages.Todos_DiffStatus_Solved();
		}
	},

	/** The comment is present in both the current and the previous report. */
	UNCHANGED {
		@Override
		public String getCss() {
			return "unchanged";
		}

		@Override
		public String getText() {
			return Messages.Todos_DiffStatus_Unchanged();
		}
	};

	/**
	 * Get CSS class.
	 * 
	 * @return the class
	 */
	public abstract String getCss();

	/**
	 * Get localized text.
	 * 
	 * @return the localized text
	 */
	public abstract String getText();
}
