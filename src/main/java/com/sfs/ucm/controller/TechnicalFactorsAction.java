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
import com.sfs.ucm.model.TechnicalFactors;
import com.sfs.ucm.util.ActiveProject;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Project Technical Factors Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("technicalFactorsAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TechnicalFactorsAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ActiveProject
	private Project activeProject;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private TechnicalFactors technicalFactors;

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
			logger.info("Using active project {}", this.activeProject);
			this.project = em.find(Project.class, this.activeProject.getId());
			this.technicalFactors = this.project.getTechnicalFactors();
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
			if (validate()) {
				this.technicalFactors.setModifiedBy(authUser.getUsername());

				em.persist(this.project);
				
				logger.info("saved: {}", this.technicalFactors);
				this.facesContextMessage.infoMessage("Technical Factors saved successfully");
			}
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
			if (validate()) {
				this.technicalFactors.setModifiedBy(authUser.getUsername());

				em.persist(this.project);
			
				logger.info("saved {}", this.technicalFactors);
				this.facesContextMessage.infoMessage("Technical Factors saved successfully");

			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
		return outcome;
	}

	/**
	 * @return the technicalFactors
	 */
	public TechnicalFactors getTechnicalFactors() {
		return technicalFactors;
	}

	/**
	 * @param technicalFactors
	 *            the technicalFactors to set
	 */
	public void setTechnicalFactors(TechnicalFactors technicalFactors) {
		this.technicalFactors = technicalFactors;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Validate factors
	 * <ul>
	 * <li>If new factors check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;

		return isvalid;
	}

}