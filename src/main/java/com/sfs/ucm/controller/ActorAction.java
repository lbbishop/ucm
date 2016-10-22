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
import com.sfs.ucm.model.Actor;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectActorUpdated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

@Stateful
@ConversationScoped
@Named("actorAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ActorAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

	@Inject
	private Logger logger;

	@Inject
	private FacesContextMessage facesContextMessage;

	@Inject
	@ProjectSecurityInit
	private Event<Project> projectSecurityMarkingSrc;

	@Inject
	@ProjectActorUpdated
	private Event<Project> projectActorSrc;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private Actor actor;

	private List<Actor> actors;

	private Project project;

	private boolean selected;

	@Inject
	public void init() {
		this.actor = new Actor();
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
			logger.info("load called");

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
		this.actor = new Actor(ModelUtils.getNextIdentifier(this.actors));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {

		try {
			this.project.removeActor(this.actor);
			em.remove(this.actor);
			logger.info("{} deleted successfully", this.actor.getName());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.actor.getName());

			loadList();

			// update producers
			this.projectActorSrc.fire(project);
			this.projectEvent.fire(this.project);
			this.selected = false;
		}
		catch (Exception e) {
			logger.error("Error occurred deleting Actor: {}", e.getMessage());
			throw new UCMException("Error occurred deleting Actor: " + e.getMessage(), e);
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
				this.actor.setModifiedBy(authUser.getUsername());
				if (this.actor.getId() == null) {
					this.project.addActor(this.actor);
				}

				em.persist(this.project);

				logger.info("{} saved successfully", this.actor.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.actor.getArtifact());

				loadList();
				this.selected = true;

				// update producers
				this.projectActorSrc.fire(project);
				this.projectEvent.fire(this.project);
			}
		}
		catch (Exception e) {
			logger.error("Error occurred saving Actor: {}", e.getMessage());
			throw new UCMException("Error occurred saving Actor: " + e.getMessage(), e);
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
	 * Actors producer
	 * 
	 * @return List
	 */
	public List<Actor> getActors() {
		return this.actors;
	}

	/**
	 * get Actor
	 * 
	 * @return actor
	 */
	public Actor getActor() {
		return actor;
	}

	/**
	 * set Actor
	 * 
	 * @param actor
	 */
	public void setActor(Actor actor) {
		this.actor = actor;
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
	 * Validate actor
	 * <ul>
	 * <li>If new actor check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.actor.getId() == null) {
			if (this.actors.contains(this.actor)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.actor.getName());
				logger.error("{} already exists", this.actor.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * load resources
	 */
	private void loadList() throws UCMException {

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Actor> c = cb.createQuery(Actor.class);
			Root<Actor> obj = c.from(Actor.class);
			c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
			this.actors = em.createQuery(c).getResultList();
		}
		catch (Exception e) {
			logger.error("Error occurred loading Actor: {}", e.getMessage());
			throw new UCMException("Error occurred loading Actor: " + e.getMessage(), e);
		}
	}

}