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
import com.sfs.ucm.model.Estimate;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProductReleaseInit;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.ProjectUserInit;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Estimate Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("estimateAction")
public class EstimateAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectEventSrc;

	@Inject
	@ProductReleaseInit
	Event<Project> productReleaseSrc;

	@Inject
	@ProjectUserInit
	Event<Project> projectUserSrc;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private Estimate estimate;

	private List<Estimate> estimates;

	private Project project;

	private boolean selected;

	private AuthUser projectUser;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.estimate = new Estimate();
		this.selected = false;

		begin();
	}

	/**
	 * Load resources
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.project = em.find(Project.class, id);

			// update producers
			this.productReleaseSrc.fire(this.project);
			this.projectUserSrc.fire(this.project);
			this.projectSecurityMarkingSrc.fire(this.project);

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);

			loadList();
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Add action
	 */
	public void add() {
		this.estimate = new Estimate(ModelUtils.getNextIdentifier(this.estimates));

	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.project.removeEstimate(this.estimate);
			em.remove(this.estimate);
			logger.info("deleted {}", this.estimate.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.estimate.getArtifact());

			// refresh list
			loadList();

			// raise project updated event
			projectEventSrc.fire(project);

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
	public void save() throws UCMException {
		try {
			if (validate()) {
				this.estimate.setModifiedBy(authUser.getUsername());
				if (this.estimate.getId() == null) {
					this.project.addEstimate(this.estimate);
				}
				em.persist(this.project);

				logger.info("saved {}", this.estimate.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.estimate.getArtifact());

				// refresh list
				loadList();

				// raise project updated event
				this.projectEventSrc.fire(project);

				this.selected = true;
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
	 * Get estimates
	 * 
	 * @return List
	 */
	public List<Estimate> getEstimates() {
		return this.estimates;
	}

	/**
	 * get Estimate
	 * 
	 * @return estimate
	 */
	public Estimate getEstimate() {
		return estimate;
	}

	/**
	 * set Estimate
	 * 
	 * @param estimate
	 */
	public void setEstimate(Estimate estimate) {
		this.estimate = estimate;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
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
	 * @return the projectUser
	 */
	public AuthUser getProjectUser() {
		return projectUser;
	}

	/**
	 * @param projectUser
	 *            the projectUser to set
	 */
	public void setProjectUser(AuthUser projectUser) {
		this.projectUser = projectUser;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Validate estimate
	 * <ul>
	 * <li>If new estimate check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.estimate.getId() == null) {
			if (this.estimates.contains(this.estimate)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.estimate.getName());
				logger.error("{} already exists", this.estimate.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * load list
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Estimate> c = cb.createQuery(Estimate.class);
		Root<Estimate> obj = c.from(Estimate.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.estimates = em.createQuery(c).getResultList();
	}

}