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
import com.sfs.ucm.model.DesignConstraint;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.ActiveProject;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * DesignConstraint Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("constraintAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ConstraintAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	private FacesContextMessage facesContextMessage;

	@Inject
	@ActiveProject
	private Project activeProject;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private DesignConstraint designConstraint;

	private List<DesignConstraint> designConstraints;

	private Project project;

	private boolean selected;

	@Inject
	public void init() {
		this.selected = false;
		begin();
	}

	/**
	 * load resources
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
	 * Add action
	 * 
	 * @return outcome
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.designConstraint = new DesignConstraint(ModelUtils.getNextIdentifier(this.designConstraints));
		this.selected = true;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.project.removeDesignConstraint(this.designConstraint);
			em.remove(this.designConstraint);
			logger.info("deleted {}", this.designConstraint.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.designConstraint.getArtifact());

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
				this.designConstraint.setModifiedBy(authUser.getUsername());
				if (this.designConstraint.getId() == null) {
					this.project.addDesignConstraint(this.designConstraint);
				}

				em.persist(this.project);

				logger.info("saved {}", this.designConstraint.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.designConstraint.getArtifact());

				// refresh list
				loadList();

				this.selected = true;

			}
		}
		catch (Exception e) {
			throw new UCMException(e);
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
	 * DesignConstraints producer
	 * 
	 * @return List
	 */
	public List<DesignConstraint> getDesignConstraints() {
		return this.designConstraints;
	}

	/**
	 * get DesignConstraint
	 * 
	 * @return designConstraint
	 */
	public DesignConstraint getDesignConstraint() {
		return designConstraint;
	}

	/**
	 * set DesignConstraint
	 * 
	 * @param designConstraint
	 */
	public void setDesignConstraint(DesignConstraint designConstraint) {
		this.designConstraint = designConstraint;
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
	 * list count
	 * 
	 * @return list count
	 */
	public int getItemCount() {
		return this.designConstraints.size();
	}

	/**
	 * Validate designConstraint
	 * <ul>
	 * <li>If new designConstraint check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.designConstraint.getId() == null) {
			if (this.designConstraints.contains(this.designConstraint)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.designConstraint.getName());
				logger.error("{} already exists", this.designConstraint.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * Load list
	 */
	private void loadList() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DesignConstraint> c = cb.createQuery(DesignConstraint.class);
		Root<DesignConstraint> obj = c.from(DesignConstraint.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.designConstraints = em.createQuery(c).getResultList();

	}

}