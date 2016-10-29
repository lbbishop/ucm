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

import java.io.Serializable;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.ActiveProject;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Dashboard Actions
 * 
 * @author lbbishop
 */
@ViewScoped
@Named("dashboardAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class DashboardAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@Inject
	@ActiveProject
	private Event<Project> activeProjectSrc;

	@Inject
	private Logger logger;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private Project project;

	private List<Project> projects;

	private boolean selected;

	/**
	 * initialize method
	 */
	@Inject
	public void init() {
		this.selected = false;
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			loadList();

			// TODO fix
			if (this.projects.size() > 0) {
				this.project = this.projects.get(0);

				this.activeProjectSrc.fire(this.project);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Close action
	 * <p>
	 * End work unit and navigate home
	 * 
	 * @return outcome
	 */
	public String close() {
		String outcome = Literal.NAV_HOME.toString();
		end();
		return outcome;
	}

	/**
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		this.selected = true;
	}

	/**
	 * Projects producer
	 * 
	 * @return List
	 */
	public List<Project> getProjects() {
		return this.projects;
	}

	/**
	 * get Project
	 * 
	 * @return project
	 */
	public Project getProject() {
		return this.project;
	}

	/**
	 * set Project
	 * 
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * load user projects
	 */
	private void loadList() {
		logger.info("Using authUser {}", this.authUser);
		this.projects = this.projectService.findMemberProjects(this.authUser);
	}

}