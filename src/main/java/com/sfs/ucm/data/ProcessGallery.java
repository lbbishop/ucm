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
package com.sfs.ucm.data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Software process diagrams
 * 
 * @author lbbishop
 * 
 */
@ApplicationScoped
@Named(value="processGallery")
public class ProcessGallery {

	private List<ProcessDiagram> processDiagrams;

	@PostConstruct
	public void init() {
		this.processDiagrams = new ArrayList<ProcessDiagram>();

		for (int i = 1; i <= 2; i++) {
			this.processDiagrams.add(new ProcessDiagram("process" + i + ".png", "Captor user tasks"));
		}
	}

	/**
	 * @return the processDiagrams
	 */
	public List<ProcessDiagram> getProcessDiagrams() {
		return processDiagrams;
	}

}
