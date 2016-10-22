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

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;

/**
 * Project Manager
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("projectManager")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ProjectManager extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectEvent;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	private Logger logger;

	private Project project;

	private Long id;

	private Long totalFlows;

	private Long totalTestCases;

	private Long totalTestSets;

	/**
	 * initialize method
	 */
	@Inject
	public void init() {

		// begin work unit
		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.project = em.find(Project.class, this.id);
			logger.info("load: project {}", this.project);

			// get totals for project attributes
			this.totalFlows = (Long) em.createQuery("select count(*) from Flow f where f.useCase.project = :project").setParameter("project", this.project).getSingleResult();

			this.totalTestSets = (Long) em.createQuery("select count(*) from TestSet ts where ts.testPlan.project = :project").setParameter("project", this.project).getSingleResult();

			this.totalTestCases = (Long) em.createQuery("select count(*) from TestCase tc where tc.testSet.testPlan.project = :project").setParameter("project", this.project).getSingleResult();

			this.projectSecurityMarkingSrc.fire(this.project);
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
	 * get Project
	 * 
	 * @return project
	 */
	public Project getProject() {
		return project;
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
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the totalFlows
	 */
	public Long getTotalFlows() {
		return totalFlows;
	}

	/**
	 * @return the totalTestCases
	 */
	public Long getTotalTestCases() {
		return totalTestCases;
	}

	/**
	 * @return the totalTestSets
	 */
	public Long getTotalTestSets() {
		return totalTestSets;
	}

}