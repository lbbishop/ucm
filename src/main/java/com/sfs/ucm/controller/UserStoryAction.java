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
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.UserStory;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * UserStory Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("userStoryAction")
public class UserStoryAction extends ActionBase implements Serializable {

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
	private AccessManager accessManager;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	private boolean editable;

	private UserStory userStory;

	private List<UserStory> userStories;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.userStory = new UserStory();
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

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);
			this.projectSecurityMarkingSrc.fire(this.project);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Add action
	 */
	public void add() {
		this.userStory = new UserStory(ModelUtils.getNextIdentifier(this.userStories));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.project.removeUserStory(this.userStory);
			UserStory obj = em.find(UserStory.class, this.userStory.getId());
			em.remove(obj);
			logger.info("deleted {}", this.userStory.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.userStory.getArtifact());

			// refresh list
			loadList();

			this.selected = false;
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * save action @
	 */
	public void save() throws UCMException {
		try {
			if (validate()) {
				this.userStory.setModifiedBy(authUser.getUsername());
				if (this.userStory.getId() == null) {
					this.project.addUserStory(this.userStory);
				}
				em.persist(this.userStory);
				logger.info("saved {}", this.userStory.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.userStory.getArtifact());

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
	 * @return the userStories
	 */
	public List<UserStory> getUserStories() {
		return userStories;
	}

	/**
	 * @param userStories
	 *            the userStories to set
	 */
	public void setUserStories(List<UserStory> userStories) {
		this.userStories = userStories;
	}

	/**
	 * get UserStory
	 * 
	 * @return userStory
	 */
	public UserStory getUserStory() {
		return userStory;
	}

	/**
	 * set UserStory
	 * 
	 * @param userStory
	 */
	public void setUserStory(UserStory userStory) {
		this.userStory = userStory;
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
	 * Validate userStory
	 * <ul>
	 * <li>If new userStory check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.userStory.getId() == null) {
			if (this.userStories.contains(this.userStory)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.userStory.getName());
				logger.error("{} already exists", this.userStory.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}
		return isvalid;
	}

	/**
	 * Load UserStorys list
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserStory> c = cb.createQuery(UserStory.class);
		Root<UserStory> obj = c.from(UserStory.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.userStories = em.createQuery(c).getResultList();
	}

}