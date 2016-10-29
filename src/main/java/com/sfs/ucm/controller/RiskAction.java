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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.Risk;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.ActiveProject;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Risk Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("riskAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class RiskAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

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
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private Risk risk;

	private List<Risk> risks;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.risk = new Risk();
		this.selected = false;

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
			loadList();

			// update producers
			this.projectSecurityMarkingSrc.fire(this.project);

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}

	}

	/**
	 * Add action
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.risk = new Risk(ModelUtils.getNextIdentifier(this.risks));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {

		try {
			this.project.removeRisk(this.risk);
			Risk obj = em.find(Risk.class, this.risk.getId());
			em.remove(obj);
			logger.info("deleted {}", this.risk.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.risk.getArtifact());

			// refresh list
			loadList();

			this.selected = false;

		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * save action
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save() throws UCMException {

		try {
			if (validate()) {
				this.risk.setModifiedBy(authUser.getUsername());
				if (this.risk.getId() == null) {
					this.project.addRisk(this.risk);
				}
				em.persist(this.risk);

				logger.info("saved {}", this.risk.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.risk.getArtifact());

				// refresh list
				loadList();

				this.selected = true;
			}
		}
		catch (Exception e) {
			logger.error("Error occurred saving Risk. {}", e.getMessage());
			throw new UCMException("Error occurred saving Risk. " + e.getMessage());
		}

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
	 * View risklist action
	 * 
	 * @return outcome
	 */
	public String viewRisks() {
		return "/risklists";
	}

	/**
	 * Risks producer
	 * 
	 * @return List
	 */
	public List<Risk> getRisks() {
		return this.risks;
	}

	/**
	 * get Risk
	 * 
	 * @return risk
	 */
	public Risk getRisk() {
		return risk;
	}

	/**
	 * set Risk
	 * 
	 * @param risk
	 */
	public void setRisk(Risk risk) {
		this.risk = risk;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
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
	 * Validate risk
	 * <ul>
	 * <li>If new risk check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.risk.getId() == null) {
			if (this.risks.contains(this.risk)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.risk.getName());
				logger.error("{} already exists", this.risk.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}
		return isvalid;
	}

	/**
	 * Load Risks list
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Risk> c = cb.createQuery(Risk.class);
		Root<Risk> obj = c.from(Risk.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.risks = em.createQuery(c).getResultList();
	}

}