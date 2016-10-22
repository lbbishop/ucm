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
import com.sfs.ucm.model.Feature;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Feature Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("featureAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class FeatureAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private Feature feature;

	private List<Feature> features;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.feature = new Feature();
		this.selected = false;
	}

	/**
	 * Load resources
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			// begin work unit
			begin();
			
			this.project = em.find(Project.class, id);

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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.feature = new Feature(ModelUtils.getNextIdentifier(this.features));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.project.removeFeature(this.feature);
			em.remove(this.feature);
			logger.info("deleted {}", this.feature.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.feature.getArtifact());

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
				this.feature.setModifiedBy(authUser.getUsername());
				if (this.feature.getId() == null) {
					this.project.addFeature(this.feature);
				}
				em.persist(this.project);

				logger.info("saved {}", this.feature.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.feature.getArtifact());

				// refresh list
				loadList();

				this.selected = true;
			}
		}
		catch (Exception e) {
			String msg = "Error occurred saving Feature: " + e.getMessage();
			logger.error(msg);
			throw new UCMException(msg);
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
	 * Get features
	 * 
	 * @return List
	 */
	public List<Feature> getFeatures() {
		return this.features;
	}

	/**
	 * get Feature
	 * 
	 * @return feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * set Feature
	 * 
	 * @param feature
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
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
	 * Validate feature
	 * <ul>
	 * <li>If new feature check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.feature.getId() == null) {
			if (this.features.contains(this.feature)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.feature.getName());
				logger.error("{} already exists", this.feature.getName());
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
	private void loadList() throws UCMException {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Feature> c = cb.createQuery(Feature.class);
		Root<Feature> obj = c.from(Feature.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.features = em.createQuery(c).getResultList();

	}

}