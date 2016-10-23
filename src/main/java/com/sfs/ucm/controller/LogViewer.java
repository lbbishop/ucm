/*
 * South Face Software
 * Copyright 2012, South Face Software, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.sfs.ucm.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.service.LogViewService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.Log;
import com.sfs.ucm.util.Service;



/**
 * File log viewer
 * <p>
 * Manages file log viewer actions
 * </p>
 */
@Named("logViewer")
@ViewScoped
public class LogViewer implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "wddrPU")
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@Service
	private LogViewService logViewService;

	private List<String> logFiles;

	private List<String> lines;

	private String selectedLogFile;

	/**
	 * init method
	 */
	@Inject
	public void init() {

	}

	/**
	 * Resource loader
	 */
	@Log
	public void load() throws UCMException {
		try {
			// get directory list
			this.logFiles = this.logViewService.getDirectoryList(buildLogDirectory());

			// display current log file by default
			this.selectedLogFile = Literal.LOG_FILE.toString();
			loadLogfile();
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Log file change handler
	 * 
	 * @param event
	 */
	public void onLogFileChange(ValueChangeEvent event) {
		this.selectedLogFile = (String) event.getNewValue();
		loadLogfile();
	}

	/**
	 * Refresh action
	 * 
	 * @throws PreException
	 */
	public void refresh() throws UCMException {
		if (StringUtils.isNotBlank(this.selectedLogFile)) {
			loadLogfile();
		}
	}

	/**
	 * Close action
	 * 
	 * @return outcome
	 */
	public String close() {
		String outcome = Literal.NAV_HOME.toString();
		return outcome;
	}

	/**
	 * @return the logFiles
	 */
	public List<String> getLogFiles() {
		return logFiles;
	}

	/**
	 * @param logFiles
	 *            the logFiles to set
	 */
	public void setLogFiles(List<String> logFiles) {
		this.logFiles = logFiles;
	}

	/**
	 * @return the lines
	 */
	public List<String> getLines() {
		return lines;
	}

	/**
	 * @param lines
	 *            the lines to set
	 */
	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	/**
	 * @return the selectedLogFile
	 */
	public String getSelectedLogFile() {
		return selectedLogFile;
	}

	/**
	 * @param selectedLogFile
	 *            the selectedLogFile to set
	 */
	public void setSelectedLogFile(String selectedLogFile) {
		this.selectedLogFile = selectedLogFile;
	}

	/**
	 * Helper method to load log file
	 * 
	 * @throws UCMException
	 */
	@Log
	private void loadLogfile() throws UCMException {
		try {

			if (this.selectedLogFile != null) {
				String filepath = buildLogDirectory() + "/" + this.selectedLogFile.trim();

				logger.info("Using logger file {}", filepath);
				this.lines = this.logViewService.readLogFile(filepath);
			}
		}
		catch (UCMException e) {
			logger.error("Error occurred reading log file. {}", e.getMessage());
		}
	}

	/**
	 * Build logger directory path
	 * 
	 * @return logger directory path
	 * @throws UCMException
	 */
	private String buildLogDirectory() throws UCMException {
		String logdir = null;
		try {
			// Read properties file.
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader().getResourceAsStream("logback.properties"));
			logdir = properties.getProperty(Literal.LOG_DIR.toString());
		}
		catch (FileNotFoundException e) {
			throw new UCMException(e);
		}
		catch (IOException e) {
			throw new UCMException(e);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}

		return logdir;
	}

}