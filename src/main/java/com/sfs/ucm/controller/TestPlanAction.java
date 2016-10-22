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
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.TestPlan;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * TestPlan Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("testPlanAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TestPlanAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectEvent;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	private TestPlan testPlan;

	private Project project;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.project = em.find(Project.class, id);
			this.testPlan = this.project.getTestPlan();

			// update producers
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
	 * apply action
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void apply() throws UCMException {
		try {
			logger.info("saving: {}", this.testPlan);

			this.testPlan.setModifiedBy(authUser.getUsername());

			em.persist(this.project);
			projectEvent.fire(project);

			this.facesContextMessage.infoMessage("TestPlan saved successfully");
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * okay action
	 * 
	 * @return outcome
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String okay() throws UCMException {

		String outcome = Literal.NAV_HOME.toString();
		try {
			logger.info("saving: {}", this.testPlan);

			this.testPlan.setModifiedBy(authUser.getUsername());

			em.persist(this.project);
			projectEvent.fire(project);

			this.facesContextMessage.infoMessage("TestPlan saved successfully");
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
		return outcome;
	}

	/**
	 * get TestPlan
	 * 
	 * @return testPlan
	 */
	public TestPlan getTestPlan() {
		return testPlan;
	}

	/**
	 * set TestPlan
	 * 
	 * @param testPlan
	 */
	public void setTestPlan(TestPlan testPlan) {
		this.testPlan = testPlan;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

}