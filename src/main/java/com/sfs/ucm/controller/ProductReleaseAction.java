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
import javax.enterprise.event.Event;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
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
import com.sfs.ucm.model.ProductRelease;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProductReleaseUpdated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * ProductRelease Actions
 * 
 * @author lbbishop
 */
@Stateful
@ViewScoped
@Named("productReleaseAction")
public class ProductReleaseAction implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Long id;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectEventSrc;

	@Inject
	@ProductReleaseUpdated
	Event<Project> productReleaseSrc;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private ProductRelease productRelease;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private List<ProductRelease> productReleases;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.selected = false;
		this.productRelease = new ProductRelease();
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
		return outcome;
	}

	/**
	 * Add action
	 */
	public void add() {
		this.productRelease = new ProductRelease(ModelUtils.getNextIdentifier(this.productReleases));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.project.removeProductRelease(this.productRelease);
			em.remove(this.productRelease);
			this.facesContextMessage.infoMessage("{0} deleted successfully", StringUtils.abbreviate(this.productRelease.getVersion(), 25));
			logger.info("deleted {}", this.productRelease);

			// refresh list
			loadList();

			// update producers
			projectEventSrc.fire(project);
			this.productReleaseSrc.fire(this.project);
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
				this.productRelease.setModifiedBy(authUser.getUsername());
				if (this.productRelease.getId() == null) {
					this.project.addProductRelease(this.productRelease);
				}
				em.persist(this.project);

				logger.info("saved {}", this.productRelease.getVersion());
				this.facesContextMessage.infoMessage("{0} saved successfully", StringUtils.abbreviate(this.productRelease.getVersion(), 25));

				// refresh list
				loadList();
				this.selected = true;

				// update producers
				projectEventSrc.fire(project);
				this.productReleaseSrc.fire(this.project);

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
	 * ProductReleases
	 * 
	 * @return List
	 */
	public List<ProductRelease> getProductReleases() {
		return this.productReleases;
	}

	/**
	 * get ProductRelease
	 * 
	 * @return productRelease
	 */
	public ProductRelease getProductRelease() {
		return productRelease;
	}

	/**
	 * set ProductRelease
	 * 
	 * @param productRelease
	 */
	public void setProductRelease(ProductRelease productRelease) {
		this.productRelease = productRelease;
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
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Validate productRelease
	 * <ul>
	 * <li>If new productRelease check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.productRelease.getId() == null) {
			if (this.productReleases.contains(this.productRelease)) {
				this.facesContextMessage.errorMessage("{0} already exists", StringUtils.abbreviate(this.productRelease.getVersion(), 25));
				logger.error("{} already exists", this.productRelease.getVersion());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * load product releases
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductRelease> c = cb.createQuery(ProductRelease.class);
		Root<ProductRelease> obj = c.from(ProductRelease.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.productReleases = em.createQuery(c).getResultList();
	}

}