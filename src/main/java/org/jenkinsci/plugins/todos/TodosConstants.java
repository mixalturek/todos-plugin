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

/**
 * Various constants.
 * 
 * @author Michal Turek
 */
public class TodosConstants {
	/** The plugin name. */
	public static final String PLUGIN_NAME = "TODOs";

	/** String for build log to identify the messages from this plugin. */
	public static final String PLUGIN_LOG_PREFIX = "[TODOs Plugin]";

	/** String specifying an error message. */
	public static final String ERROR = "ERROR";

	/** String specifying a warning message. */
	public static final String WARNING = "WARNING";

	/** Subdirectory of build results directory where source files are stored. */
	public static final String BUILD_SUBDIR = "todos-plugin";

	/** Results page URL. */
	public static final String RESULTS_URL = "todosResult";

	/** 24x24 px icon. */
	public static final String ICON_24PX = "/plugin/todos/icons/todos-24.png";

	/** 48x48 px icon. */
	public static final String ICON_48PX = "/plugin/todos/icons/todos-48.png";

	/** Chart width. */
	public static final int CHART_WIDTH = 500;

	/** Chart height. */
	public static final int CHART_HEIGHT = 200;

	/** Default pattern for file. */
	public static final String DEFAULT_FILE_SEARCH_PATTERN = "**/todos.xml";
}
