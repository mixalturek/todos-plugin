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
import hudson.util.StackedAreaRenderer2;

import org.jfree.data.category.CategoryDataset;

/**
 * TODO: Description.
 * 
 * @author Michal Turek
 */
public class TodosAreaRenderer extends StackedAreaRenderer2 {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/** Base URL of the graph links. */
	private final String url;

	/**
	 * Creates a new instance of <code>AbstractAreaRenderer</code>.
	 * 
	 * @param url
	 *            base URL of the graph links
	 * @param toolTipProvider
	 *            tooltip provider for the clickable map
	 */
	public TodosAreaRenderer(String url) {
		super();
		this.url = "/" + url + "/";
	}

	@Override
	public String generateURL(CategoryDataset dataset, int row, int column) {
		return getLabel(dataset, column).build.getNumber() + url;
	}

	/**
	 * Returns the build label at the specified column.
	 * 
	 * @param dataset
	 *            data set of values
	 * @param column
	 *            the column
	 * @return the label of the column
	 */
	private NumberOnlyBuildLabel getLabel(CategoryDataset dataset, int column) {
		return (NumberOnlyBuildLabel) dataset.getColumnKey(column);
	}
}
