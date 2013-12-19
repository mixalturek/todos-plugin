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

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

import org.jenkinsci.plugins.todos.model.TodosParser;
import org.jenkinsci.plugins.todos.model.TodosReportStatistics;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Jenkins publisher for TODOs.
 * 
 * @author Michal Turek
 */
public class TodosPublisher extends Recorder implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/** Default pattern for file. */
	private static final String DEFAULT_PATTERN = "**/todos.xml";

	/** Actual pattern for file. */
	private final String pattern;

	@DataBoundConstructor
	public TodosPublisher(String pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return new TodosProjectAction(project);
	}

	protected boolean canContinue(final Result result) {
		return result != Result.ABORTED && result != Result.FAILURE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) {
		FilePath workspace = build.getWorkspace();
		PrintStream logger = listener.getLogger();
		TodosParser parser = new TodosParser(getRealPattern(), logger);
		TodosReportStatistics statistics = null;

		try {
			if (this.canContinue(build.getResult())) {
				statistics = workspace.act(parser);
			} else {
				// generate an empty report
				// TODO: Replace this empty report with the last valid one?
				statistics = new TodosReportStatistics();
			}
		} catch (IOException e) {
			e.printStackTrace(logger);
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace(logger);
			return false;
		}

		TodosResult result = new TodosResult(statistics, build);
		build.addAction(new TodosBuildAction(build, result));

		try {
			copyFilesToBuildDirectory(statistics.getSourceFiles(),
					build.getRootDir(), launcher.getChannel());
		} catch (IOException e) {
			e.printStackTrace(logger);
		} catch (InterruptedException e) {
			e.printStackTrace(logger);
		}

		return true;
	}

	/**
	 * Copy files to a build results directory. The copy of a file will be
	 * stored in TodosConstants.BUILD_SUBDIR subdirectory and a hashcode of its
	 * absolute path will be used in its name to distinguish files with the same
	 * names from different directories.
	 * 
	 * @param sourceFiles
	 *            the files to copy
	 * @param rootDir
	 *            the root directory where build results are stored.
	 * @param channel
	 *            the communication channel
	 * @throws IOException
	 *             if something fails
	 * @throws InterruptedException
	 *             if something fails
	 * 
	 * @see TodosConstants#BUILD_SUBDIR
	 */
	private void copyFilesToBuildDirectory(List<File> sourceFiles,
			File rootDir, VirtualChannel channel) throws IOException,
			InterruptedException {
		File destDir = new File(rootDir, TodosConstants.BUILD_SUBDIR);

		if (!destDir.exists() && !destDir.mkdir()) {
			throw new IOException(
					"Create directory for copy of workspace files failed: "
							+ destDir.getAbsolutePath());
		}

		for (File sourceFile : sourceFiles) {
			File masterFile = new File(destDir, Integer.toHexString(sourceFile
					.hashCode()) + "_" + sourceFile.getName());

			if (!masterFile.exists()) {
				FileOutputStream outputStream = new FileOutputStream(masterFile);
				new FilePath(channel, sourceFile.getAbsolutePath())
						.copyTo(outputStream);
			}
		}
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	private String getRealPattern() {
		if (this.getPattern() == null || this.getPattern().length() == 0) {
			return DEFAULT_PATTERN;
		} else {
			return this.getPattern();
		}
	}

	public String getPattern() {
		return pattern;
	}
}
