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
package com.sfs.ucm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.util.Service;


/**
 * Mail Service
 * 
 * @author lbbisho
 * 
 */
@Named
@Service
public class LogViewService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	public List<String> getDirectoryList(final String path) throws UCMException {

		List<String> filenames = new ArrayList<String>();

		File theFile = new File(path);
		if (theFile != null) {
			if (theFile.isDirectory()) {
				File[] files = theFile.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						if (files[i].getName().indexOf(".log") != -1) {
							filenames.add(files[i].getName());
						}
					}
				}
				else {
					logger.error(" [ACCESS DENIED]");
				}
			}
		}

		return filenames;

	}

	/**
	 * Read log file and return as list of lines
	 * 
	 * @param filepath
	 * @return list of lines
	 * @throws UCMException
	 */
	public List<String> readLogFile(final String filepath) throws UCMException {

		List<String> lines = new ArrayList<String>();

		try {
			// use buffering
			InputStream fin = new FileInputStream(filepath);
			lines = IOUtils.readLines(fin);

			fin.close();
		}
		catch (IOException e) {
			logger.error("Cannot read logfile. {}", e.getMessage());
			throw new UCMException(e);
		}

		return lines;
	}
}
