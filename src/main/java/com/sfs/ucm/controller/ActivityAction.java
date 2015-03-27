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
import com.sfs.ucm.data.StatusType;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.Activity;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Iteration;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectTaskInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Activity Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("activityAction")
public class ActivityAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectSrc;

	@Inject
	@ProjectTaskInit
	Event<Project> projectTaskSrc;

	@Inject
	private FacesContextMessage facesContextMessage;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private Activity activity;

	private List<Activity> activities;

	private Project project;

	private Iteration iteration;

	private boolean selected;

	@Inject
	public void init() {
		this.selected = false;
		this.activity = new Activity();

		// begin work unit
		begin();
	}

	/**
	 * load resources
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.iteration = em.find(Iteration.class, this.id);
			this.project = iteration.getProject();

			loadList();

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);

			// update producer events
			this.projectTaskSrc.fire(this.project);
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
	 * Add action
	 */
	public void add() {
		this.activity = new Activity(ModelUtils.getNextIdentifier(this.activities), authUser);
		this.activity.setStatusType(StatusType.New);
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.iteration.removeActivity(this.activity);
			em.remove(this.activity);
			logger.info("deleted {}", this.activity.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.activity.getArtifact());

			// refresh list
			loadList();

			// fire update event
			projectSrc.fire(project);
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
				this.activity.setModifiedBy(authUser.getUsername());
				if (this.activity.getId() == null) {
					this.iteration.addActivity(this.activity);
					logger.info("added activity {}", iteration.getActivities().size());
				}

				em.persist(this.iteration);

				logger.info("saved {}", this.activity.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.activity.getArtifact());

				// refresh list
				loadList();

				// fire update event
				projectSrc.fire(project);

				this.selected = true;
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Activities producer
	 * 
	 * @return List
	 */
	public List<Activity> getActivities() {
		return this.activities;
	}

	/**
	 * get Activity
	 * 
	 * @return activity
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * set Activity
	 * 
	 * @param activity
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * @return the iteration
	 */
	public Iteration getIteration() {
		return iteration;
	}

	/**
	 * @param iteration
	 *            the iteration to set
	 */
	public void setIteration(Iteration iteration) {
		this.iteration = iteration;
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
	 * Validate activity
	 * <ul>
	 * <li>If new activity check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.activity.getId() == null) {
			if (this.activities.contains(this.activity)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.activity.getTask().getName());
				logger.error("{} already exists: ", this.activity.getTask().getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		// verify begin date is earlier than enddate
		if (this.activity.getEndDate() != null) {
			if (this.activity.getBeginDate().after(this.activity.getEndDate())) {
				this.facesContextMessage.errorMessage("Task begin date must occur before end date");
				logger.error("Task end date occurs before begin date");
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
		CriteriaQuery<Activity> c = cb.createQuery(Activity.class);
		Root<Activity> obj = c.from(Activity.class);
		c.select(obj).where(cb.equal(obj.get("iteration"), this.iteration)).orderBy(cb.asc(obj.get("id")));
		this.activities = em.createQuery(c).getResultList();
	}

}