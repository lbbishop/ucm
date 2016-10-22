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
import com.sfs.ucm.model.GlossaryTerm;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * GlossaryTerm Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("glossaryTermAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class GlossaryTermAction extends ActionBase implements Serializable {

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

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private GlossaryTerm glossaryTerm;

	private List<GlossaryTerm> glossaryTerms;

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
	 * Load resources
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.project = em.find(Project.class, id);

			// update producers
			this.projectSecurityMarkingSrc.fire(this.project);

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);

			loadList();
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
		this.glossaryTerm = new GlossaryTerm(ModelUtils.getNextIdentifier(this.glossaryTerms));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.project.removeGlossaryTerm(this.glossaryTerm);
			em.remove(this.glossaryTerm);

			logger.info("deleted {}", this.glossaryTerm.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.glossaryTerm.getArtifact());

			// refresh list
			loadList();

			// fire update event
			projectEvent.fire(project);
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
				this.glossaryTerm.setModifiedBy(authUser.getUsername());
				if (this.glossaryTerm.getId() == null) {
					this.project.addGlossaryTerm(this.glossaryTerm);
				}
				em.persist(this.project);

				logger.info("saved {}", this.glossaryTerm.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.glossaryTerm.getArtifact());

				// refresh list
				loadList();

				// fire update event
				projectEvent.fire(project);

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
	 * GlossaryTerms producer
	 * 
	 * @return List
	 */
	public List<GlossaryTerm> getGlossaryTerms() {
		return this.glossaryTerms;
	}

	/**
	 * get GlossaryTerm
	 * 
	 * @return glossaryTerm
	 */
	public GlossaryTerm getGlossaryTerm() {
		return glossaryTerm;
	}

	/**
	 * set GlossaryTerm
	 * 
	 * @param glossaryTerm
	 */
	public void setGlossaryTerm(GlossaryTerm glossaryTerm) {
		this.glossaryTerm = glossaryTerm;
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
	 * Validate glossaryTerm
	 * <ul>
	 * <li>If new glossaryTerm check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.glossaryTerm.getId() == null) {
			if (this.glossaryTerms.contains(this.glossaryTerm)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.glossaryTerm.getTerm());
				logger.error("{} already exists", this.glossaryTerm.getTerm());
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
		CriteriaQuery<GlossaryTerm> c = cb.createQuery(GlossaryTerm.class);
		Root<GlossaryTerm> obj = c.from(GlossaryTerm.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.glossaryTerms = em.createQuery(c).getResultList();
	}

}