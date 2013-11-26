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

import hudson.model.AbstractBuild;

import java.io.Serializable;

import org.jenkinsci.plugins.todos.model.TodosReportStatistics;

/**
 * TODO: Description.
 * 
 * @author Michal Turek
 */
public class TodosResult implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	private final TodosReportStatistics statistics;
	private final AbstractBuild<?, ?> owner;

	public TodosResult(TodosReportStatistics statistics,
			AbstractBuild<?, ?> owner) {
		this.statistics = statistics;
		this.owner = owner;
	}

	public TodosReportStatistics getStatistics() {
		return statistics;
	}

	public AbstractBuild<?, ?> getOwner() {
		return owner;
	}

	/*
	public TodosResult getLanguageResult(String language) {
		TodosReport filtered = new TodosReport(report, new LanguageFileFilter(
				language));
		return new BreadCrumbResult(filtered, owner, language);
	}

	public TodosResult getFolderResult(String jumbledFolder) {
		String folder = jumbledFolder.replace("|",
				System.getProperty("file.separator"));
		TodosReport filtered = new TodosReport(report, new FolderFileFilter(
				folder));
		return new BreadCrumbResult(filtered, owner, folder);
	}

	private static class LanguageFileFilter implements FileFilter, Serializable {
		private final String language;

		public LanguageFileFilter(String language) {
			this.language = language;
		}

		public boolean include(File file) {
			return file.getLanguage().equals(language);
		}
	}

	private static class FolderFileFilter implements FileFilter, Serializable {
		private final String folder;

		public FolderFileFilter(String folder) {
			this.folder = folder;
		}

		public boolean include(File file) {
			String separator = System.getProperty("file.separator");

			int index = file.getName().lastIndexOf(separator);
			String fileFolder = file.getName().substring(0, index);

			return folder.equals(fileFolder);
		}
	}

	private static class BreadCrumbResult extends TodosResult implements
			ModelObject, Serializable {
		private String displayName = null;

		public BreadCrumbResult(TodosReport report, AbstractBuild<?, ?> owner,
				String displayName) {
			super(report, owner);
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}
	*/
}
