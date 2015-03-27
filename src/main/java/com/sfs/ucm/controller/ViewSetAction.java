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

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
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
import com.sfs.ucm.model.ProductRelease;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.model.ViewSet;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectTaskUpdated;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Use ViewSet Actions
 * 
 * @author lbbishop
 */
@Stateful
@ViewScoped
@Named("viewSetAction")
public class ViewSetAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authenticatedUser;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	@ProjectTaskUpdated
	Event<Project> projectTaskSrc;

	private AuthUser authUser;

	private List<ProductRelease> productReleases;

	private List<Project> memberProjects;

	private ViewSet viewSet;

	private List<ViewSet> viewSets;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@PostConstruct
	public void init() {
		this.viewSet = new ViewSet();

		// begin work unit
		begin();

		this.authUser = em.find(AuthUser.class, authenticatedUser.getId());

		this.selected = false;

		logger.info("init called");
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			loadList();

			// find member projects
			findMemberProjects(this.authUser);
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
	public void add() {
		this.viewSet = new ViewSet(ModelUtils.getNextIdentifier(this.viewSets));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.authUser.removeViewSet(this.viewSet);
			em.remove(this.viewSet);
			logger.info("deleted {}", this.viewSet.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.viewSet.getArtifact());

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
	public void save() throws UCMException {
		try {
			if (validate()) {

				this.viewSet.setModifiedBy(authUser.getUsername());
				if (this.viewSet.getId() == null) {
					this.authUser.addViewSet(this.viewSet);
				}

				em.persist(this.viewSet);
				logger.info("saved {}", this.viewSet.getName());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.viewSet.getArtifact());

				// refresh list
				loadList();

				Project project = this.projectService.findProjectById(this.viewSet.getProject().getId());

				// update producer
				this.projectTaskSrc.fire(project);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Active flag Change event handler
	 * <p>
	 * If active flag value, reset other project viewset active flags
	 * 
	 * @throws UCMException
	 */
	public void onActiveValueChange(ValueChangeEvent event) throws UCMException {

		try {
			Boolean active = (Boolean) event.getNewValue();
			if (active) {

				// clear active flag
				for (ViewSet theViewSet : this.viewSets) {
					if (theViewSet.getProject().equals(this.viewSet.getProject())) {
						theViewSet.setActive(false);
					}
				}

				this.viewSet.setActive(true);
			}
		}
		catch (Exception e) {
			logger.error("Error occurred changing active flag {}", e.getMessage());
			throw new UCMException(e);
		}

	}

	/**
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		this.viewSet = (ViewSet) event.getObject();

		// update product releases list
		updateProductReleases(this.viewSet.getProject());

		this.selected = true;
	}

	/**
	 * ViewSets producer
	 * 
	 * @return List
	 */
	public List<ViewSet> getViewSets() {
		return this.viewSets;
	}

	/**
	 * @param viewSets
	 *            the viewSets to set
	 */
	public void setViewSets(List<ViewSet> viewSets) {
		this.viewSets = viewSets;
	}

	/**
	 * Project value change listener
	 * <p>
	 * Update list of product releases for this new project selection
	 * 
	 * @param event
	 */
	public void projectValueChange(ValueChangeEvent event) {
		Project project = (Project) event.getNewValue();

		if (project != null) {
			updateProductReleases(project);
		}
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
	 * @return the productReleases
	 */
	public List<ProductRelease> getProductReleases() {
		return productReleases;
	}

	/**
	 * get ViewSet
	 * 
	 * @return viewSet
	 */
	public ViewSet getViewSet() {
		return viewSet;
	}

	/**
	 * set ViewSet
	 * 
	 * @param viewSet
	 */
	public void setViewSet(ViewSet viewSet) {
		this.viewSet = viewSet;
	}

	/**
	 * @return the memberProjects
	 */
	public List<Project> getMemberProjects() {
		return memberProjects;
	}

	/**
	 * Validate viewSet
	 * <ul>
	 * <li>If new viewSet check for duplicate</li>
	 * <li>sets viewset active if no viewsets exist</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.viewSet.getId() == null) {
			if (this.viewSets.contains(this.viewSet)) {
				this.facesContextMessage.infoMessage("{0} already exists", this.viewSet.getName());
				logger.error("{} already exists", this.viewSet);
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		if (this.viewSets.size() == 0) {
			this.viewSet.setActive(true);
		}

		return isvalid;
	}

	/**
	 * Load viewsets
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ViewSet> c = cb.createQuery(ViewSet.class);
		Root<ViewSet> obj = c.from(ViewSet.class);
		c.select(obj).where(cb.equal(obj.get("authUser"), this.authUser)).orderBy(cb.asc(obj.get("id")));
		this.viewSets = em.createQuery(c).getResultList();
	}

	/**
	 * update productRelease
	 * 
	 * @param project
	 */
	public void updateProductReleases(final Project project) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductRelease> c = cb.createQuery(ProductRelease.class);
		Root<ProductRelease> obj = c.from(ProductRelease.class);
		c.select(obj).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("version")));
		this.productReleases = em.createQuery(c).getResultList();
	}

	/**
	 * Find member projects
	 * 
	 * @param authUser
	 */
	private void findMemberProjects(AuthUser authUser) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> c = cb.createQuery(Project.class);
		Root<ProjectMember> obj = c.from(ProjectMember.class);
		c.select(obj.<Project> get("project")).where(cb.equal(obj.get("authUser"), authUser)).orderBy(cb.asc(obj.get("id")));
		this.memberProjects = em.createQuery(c).getResultList();

	}

}