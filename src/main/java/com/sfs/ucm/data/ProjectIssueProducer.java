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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.Issue;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectIssueInit;
import com.sfs.ucm.util.ProjectIssueUpdated;
import com.sfs.ucm.util.Service;

@Stateful
@SessionScoped
public class ProjectIssueProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private EntityManager em;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@Service
	private ProjectService projectService;

	private List<Issue> issues;

	private Project project;

	@Inject
	public void init() {

	}

	/**
	 * Project task update handler
	 * <p>
	 * Select by project and StatusType.New
	 * 
	 * @param project
	 */
	public void onProjectIssueInit(@Observes @ProjectIssueInit final Project project) throws UCMException {
		if (this.projectIssues == null || !project.equals(this.project)) {
			load(project);
		}
		this.project = project;
	}

	/**
	 * Project task update handler
	 * <p>
	 * Select by project and StatusType.New
	 * 
	 * @param project
	 */
	public void onProjectIssueUpdate(@Observes @ProjectIssueUpdated final Project project) throws UCMException {
		load(project);
	}

	/**
	 * Producer
	 * 
	 * @return List of project issue
	 */
	@Produces
	@Named
	public List<Issue> getIssues() {
		return this.issues;
	}

	/**
	 * Load resources
	 * 
	 * @param project
	 * @throws UCMException
	 */
	private void load(final Project project) throws UCMException {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Issue> c = cb.createQuery(Issue.class);
		Root<Issue> obj = c.from(Issue.class);
		c.select(obj).where(cb.equal(obj.get("project"), project), cb.equal(obj.get("statusType"), StatusType.New)).orderBy(cb.asc(obj.get("id")));
		this.issues = em.createQuery(c).getResultList();
	}
}
