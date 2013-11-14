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
package org.jenkinsci.plugins.todos.model;

import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.SAXException;

/**
 * TODO: Description.
 * 
 * @author Michal Turek
 */
public class TodosParser implements FilePath.FileCallable<TodosReport> {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	private final String filePattern;

	public TodosParser(String filePattern) {
		this.filePattern = filePattern;
	}

	public TodosReport invoke(File workspace, VirtualChannel channel)
			throws IOException {
		String[] files = findFiles(workspace, filePattern);
		TodosReport report = new TodosReport();

		for (String fileName : files) {
			try {
				report = report.concatenate(parse(workspace, fileName));
			} catch (SAXException e) {
				throw new IOException("Parsing failed", e);
			} catch (JAXBException e) {
				throw new IOException("Parsing failed", e);
			}
		}

		// TODO: Simplify names if needed.
		// report.simplifyNames();
		return report;
	}

	private TodosReport parse(File workspace, String fileName)
			throws SAXException, JAXBException {
		File file = new File(workspace, fileName);

		String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
		SchemaFactory sf = SchemaFactory
				.newInstance(/* XMLConstants. */W3C_XML_SCHEMA_NS_URI);
		// TODO: Check that access to XSD work
		Schema schema = sf.newSchema(this.getClass().getResource("todos.xsd"));

		JAXBContext context = JAXBContext.newInstance(TodosReport.class);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		return (TodosReport) unmarshaller.unmarshal(file);
	}

	/**
	 * Returns an array with the filenames of the specified file pattern that
	 * have been found in the workspace.
	 * 
	 * @param workspace
	 *            root directory of the workspace
	 * @return the filenames of all found files
	 */
	private String[] findFiles(File workspace, String pattern) {
		try {
			FileSet fileSet = new FileSet();
			Project antProject = new Project();
			fileSet.setProject(antProject);
			fileSet.setDir(workspace);
			fileSet.setIncludes(pattern);

			return fileSet.getDirectoryScanner(antProject).getIncludedFiles();
		} catch (BuildException exception) {
			return new String[0];
		}
	}
}
