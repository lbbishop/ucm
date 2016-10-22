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
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectPackage;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectPackageUpdated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Package Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("projectPackageAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ProjectPackageAction extends ActionBase implements Serializable {

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
	@ProjectPackageUpdated
	Event<Project> projectPackageSrc;

	@Inject
	private AccessManager accessManager;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	private boolean editable;

	private ProjectPackage projectPackage;

	private List<ProjectPackage> projectPackages;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
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
			this.project = em.find(Project.class, id);
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
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.projectPackage = new ProjectPackage(ModelUtils.getNextIdentifier(this.projectPackages));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.project.removeProjectPackage(projectPackage);
			em.remove(this.projectPackage);
			logger.info("deleted {}", this.projectPackage.getName());
			this.facesContextMessage.infoMessage("{0} deleted successfully", StringUtils.abbreviate(this.projectPackage.getName(), 25));

			// refresh list
			loadList();

			projectPackageSrc.fire(this.project);
			projectEvent.fire(this.project);
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
				if (this.projectPackage.getId() == null) {
					this.project.addProjectPackage(this.projectPackage);
				}

				try {
					em.persist(this.project);
				}
				catch (javax.ejb.EJBException e) {
					if (e.getCausedByException().equals(OptimisticLockException.class)) {
						logger.error("OptimisticLockException {}", e.getMessage());
						this.facesContextMessage.warningMessage("Another user is modifying this Artifact");
					}
					throw new UCMException(e);
				}

				logger.info("saved {}", this.projectPackage.getName());
				this.facesContextMessage.infoMessage("{0} saved successfully", StringUtils.abbreviate(this.projectPackage.getName(), 25));

				// update producers
				projectEvent.fire(this.project);
				projectPackageSrc.fire(this.project);

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
	 * ProjectPackages producer
	 * 
	 * @return List
	 */
	public List<ProjectPackage> getProjectPackages() {
		return this.projectPackages;
	}

	/**
	 * get ProjectPackage
	 * 
	 * @return projectPackage
	 */
	public ProjectPackage getProjectPackage() {
		return projectPackage;
	}

	/**
	 * set Package
	 * 
	 * @param projectPackage
	 */
	public void setProjectPackage(ProjectPackage projectPackage) {
		this.projectPackage = projectPackage;
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
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Validate projectPackage
	 * <ul>
	 * <li>If new projectPackage check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.projectPackage.getId() == null) {
			if (this.projectPackages.contains(this.projectPackage)) {
				this.facesContextMessage.errorMessage("{0} already exists", StringUtils.abbreviate(this.projectPackage.getName(), 25));
				logger.error("{} already exists", this.projectPackage.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * Load project packages
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectPackage> c = cb.createQuery(ProjectPackage.class);
		Root<ProjectPackage> obj = c.from(ProjectPackage.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.projectPackages = em.createQuery(c).getResultList();
	}

}