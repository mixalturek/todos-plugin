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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.jenkinsci.plugins.todos.TodosConstants;
import org.xml.sax.SAXException;

/**
 * Parse XML formatted TODOs reports.
 * 
 * @author Michal Turek
 */
public class TodosParser implements
		FilePath.FileCallable<TodosReportStatistics> {
	/** Serial version UID. */
	private static final long serialVersionUID = 0;

	/** Pattern for searching the input files. */
	private final String filePattern;

	/** Logger for output messages to build log. */
	private final PrintStream logger;

	/**
	 * Constructructor initializing members.
	 * 
	 * @param filePattern
	 *            pattern for searching the input files
	 * @param logger
	 *            logger for output messages to build log
	 */
	public TodosParser(String filePattern, PrintStream logger) {
		this.filePattern = filePattern;
		this.logger = logger;
	}

	/**
	 * Invoke the parsing.
	 * 
	 * @see hudson.FilePath.FileCallable#invoke(java.io.File,
	 *      hudson.remoting.VirtualChannel)
	 */
	public TodosReportStatistics invoke(File workspace, VirtualChannel channel)
			throws IOException {
		String[] files = findFiles(workspace, filePattern);
		TodosReport report = new TodosReport();

		if (files.length == 0) {
			logger.format("%s No file if matching the input pattern: %s %s\n",
					TodosConstants.WARNING, filePattern,
					TodosConstants.JENKINS_TODOS_PLUGIN);
		}

		Set<File> paths = new HashSet<File>();

		for (String filename : files) {
			try {
				File inputFile = new File(workspace, filename);
				report = report.concatenate(parse(inputFile, logger));
				paths.add(inputFile);
			} catch (SAXException e) {
				throw new IOException("File parsing failed: " + filename + ", "
						+ findExceptionMessage(e) + " "
						+ TodosConstants.JENKINS_TODOS_PLUGIN, e);
			} catch (JAXBException e) {
				throw new IOException("File parsing failed: " + filename + ", "
						+ findExceptionMessage(e) + " "
						+ TodosConstants.JENKINS_TODOS_PLUGIN, e);
			}
		}

		return report.getStatistics(new ArrayList<File>(paths));
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
				report = report.concatenate(parse(file, null));
			} catch (SAXException e) {
				// Silently ignore, it's still a possibility that other files
				// can be parsed successfully
			} catch (JAXBException e) {
				// Silently ignore, it's still a possibility that other files
				// can be parsed successfully
			}
		}

		return report;
	}

	/**
	 * Helper method to parse one input file.
	 * 
	 * @param file
	 *            the file to be parsed
	 * @param logger
	 *            optional logger to report errors, can be null
	 * @return the content of the parsed file in form of a report
	 * @throws SAXException
	 *             if a XML related error occur
	 * @throws JAXBException
	 *             if a XML related error occur
	 */
	private static TodosReport parse(File file, PrintStream logger)
			throws SAXException, JAXBException {
		if (!file.exists()) {
			if (logger != null) {
				logger.format("%s File does not exist: %s %s\n",
						TodosConstants.WARNING, file.getAbsolutePath(),
						TodosConstants.JENKINS_TODOS_PLUGIN);
			}
			return new TodosReport();
		} else if (!file.isFile() || !file.canRead()) {
			if (logger != null) {
				logger.format(
						"%s File is not readable, check permissions: %s %s\n",
						TodosConstants.WARNING, file.getAbsolutePath(),
						TodosConstants.JENKINS_TODOS_PLUGIN);
			}
			return new TodosReport();
		}

		if (logger != null) {
			logger.format("Processing file: %s %s\n", file.getAbsolutePath(),
					TodosConstants.JENKINS_TODOS_PLUGIN);
		}

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
	 */
	private String[] findFiles(File workspace, String pattern) {
		try {
			FileSet fileSet = new FileSet();
			Project antProject = new Project();
			fileSet.setProject(antProject);
			fileSet.setDir(workspace);
			fileSet.setIncludes(pattern);

			return fileSet.getDirectoryScanner(antProject).getIncludedFiles();
		} catch (BuildException e) {
			logger.format(
					"%s Exception occurred while searching files mathing input pattern: %s %s\n",
					TodosConstants.WARNING, pattern,
					TodosConstants.JENKINS_TODOS_PLUGIN);
			e.printStackTrace(logger);
			return new String[0];
		}
	}

	/**
	 * Helper method to retrieve a message from an exception or its causes.
	 * 
	 * @param exception
	 *            the root exception to search a message in
	 * @return the message if found, otherwise an empty string
	 */
	private String findExceptionMessage(Throwable exception) {
		String message = "";
		Throwable cause = exception;

		while (cause.getMessage() == null && cause.getCause() != null) {
			cause = cause.getCause();
		}

		if (cause != null) {
			message = cause.getMessage();
		}

		return message;
	}
}
