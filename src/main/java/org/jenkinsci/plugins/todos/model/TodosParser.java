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
 * Parse XML formatted TODOs reports.
 * 
 * @author Michal Turek
 */
public class TodosParser implements FilePath.FileCallable<TodosReport> {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/** Pattern for searching the input files. */
	private final String filePattern;

	/**
	 * Constructor initializing members.
	 * 
	 * @param filePattern
	 *            pattern for searching the input files
	 */
	public TodosParser(String filePattern) {
		this.filePattern = filePattern;
	}

	/**
	 * Invoke the parsing.
	 * 
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File,
	 *      hudson.remoting.VirtualChannel)
	 */
	public TodosReport invoke(File workspace, VirtualChannel channel)
			throws IOException {
		String[] files = findFiles(workspace, filePattern);
		TodosReport report = new TodosReport();

		for (String filename : files) {
			try {
				File inputFile = new File(workspace, filename);
				report = report.concatenate(parse(inputFile), inputFile);
			} catch (SAXException e) {
				throw new IOException("XML parsing failed: " + filename + ", "
						+ findExceptionMessage(e), e);
			} catch (JAXBException e) {
				throw new IOException("XML parsing failed: " + filename + ", "
						+ findExceptionMessage(e), e);
			}
		}

		return report;
	}

	/**
	 * Parse a list of input files. All errors are silently ignored.
	 * 
	 * @param files
	 *            the files
	 * @return the content of the parsed files in form of a report
	 */
	public static TodosReport parseFiles(File[] files) {
		TodosReport report = new TodosReport();

		for (File file : files) {
			try {
				report = report.concatenate(parse(file));
			} catch (SAXException e) {
				// Silently ignore, there is still a possibility that other
				// files can be parsed successfully
			} catch (JAXBException e) {
				// Silently ignore, there is still a possibility that other
				// files can be parsed successfully
			} catch (IOException e) {
				// Silently ignore, there is still a possibility that other
				// files can be parsed successfully
			}
		}

		return report;
	}

	/**
	 * Parse one input file.
	 * 
	 * @param file
	 *            the file to be parsed
	 * @return the content of the parsed file in form of a report
	 * @throws SAXException
	 *             if a XML related error occurs
	 * @throws JAXBException
	 *             if a XML related error occurs
	 * @throws IOException
	 *             if an IO related error occurs
	 */
	private static TodosReport parse(File file) throws SAXException,
			JAXBException, IOException {
		if (!file.exists()) {
			throw new IOException("File does not exist: "
					+ file.getAbsolutePath());
		} else if (!file.isFile() || !file.canRead()) {
			throw new IOException("File is not readable, check permissions: "
					+ file.getAbsolutePath());
		}

		// The constant is not available in this version of Java
		String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
		SchemaFactory sf = SchemaFactory
				.newInstance(/* XMLConstants. */W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf
				.newSchema(TodosParser.class.getResource("todos.xsd"));

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
	 * @throws IOException
	 *             if something fails
	 */
	private String[] findFiles(File workspace, String pattern)
			throws IOException {
		try {
			FileSet fileSet = new FileSet();
			Project antProject = new Project();
			fileSet.setProject(antProject);
			fileSet.setDir(workspace);
			fileSet.setIncludes(pattern);

			return fileSet.getDirectoryScanner(antProject).getIncludedFiles();
		} catch (BuildException e) {
			throw new IOException(
					"Searching files mathing input pattern failed: " + pattern,
					e);
		}
	}

	/**
	 * Helper method to retrieve a message from an exception or its causes.
	 * 
	 * @param exception
	 *            the root exception to search a message in
	 * @return the first non empty message if found, otherwise an empty string
	 */
	private String findExceptionMessage(Throwable exception) {
		Throwable cause = exception;

		while (cause != null) {
			if (cause.getMessage() != null && !cause.getMessage().isEmpty()) {
				return cause.getMessage();
			}

			cause = cause.getCause();
		}

		return "";
	}
}
