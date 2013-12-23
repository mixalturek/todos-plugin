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

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

/**
 * Plugin descriptor.
 * 
 * @author Michal Turek
 */
@Extension
public class TodosDescriptor extends BuildStepDescriptor<Publisher> {
	/**
	 * Constructor.
	 */
	public TodosDescriptor() {
		super(TodosPublisher.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isApplicable(Class<? extends AbstractProject> jobType) {
		// The plugin is applicable for all job types
		return true;
	}

	@Override
	public String getDisplayName() {
		return Messages.Todos_Publisher_Name();
	}
}
