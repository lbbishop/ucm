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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
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
import com.sfs.ucm.data.StatusType;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.model.Task;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProductReleaseInit;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectTaskUpdated;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.ProjectUserInit;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Task Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("taskAction")
public class TaskAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	@ProductReleaseInit
	private Event<Project> productReleaseSrc;

	@Inject
	@ProjectUserInit
	private Event<Project> projectUserSrc;

	@Inject
	@ProjectSecurityInit
	private Event<Project> projectSecurityMarkingSrc;

	@Inject
	@ProjectTaskUpdated
	private Event<Project> projectTaskSrc;

	@Inject
	@Service
	private ProjectService projectService;

	private Task task;

	private List<Task> tasks;

	private List<Task> filteredTasks;

	private Project project;

	private AuthUser projectUser;

	private boolean selected;

	private double totalHours;

	private int newTaskCount;

	@Inject
	public void init() {
		this.task = new Task();
		this.selected = false;
		this.editable = true;

		// begin work unit
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

			// load resources
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
	 */
	public void add() {
		this.task = new Task(ModelUtils.getNextIdentifier(this.tasks));
		this.task.setStatusType(StatusType.New);
		this.projectUser = null;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.project.removeTask(this.task);
			em.remove(this.task);
			logger.info("Deleted {}", this.task.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.task.getArtifact());
			loadList();

			this.selected = false;

			// set total estimated hours for new tasks
			this.totalHours = calcTotalHours();

			// new task count
			this.newTaskCount = newTaskCount();

			// update producers
			this.projectEvent.fire(this.project);
			this.projectTaskSrc.fire(this.project);
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
				this.task.setModifiedBy(authUser.getUsername());
				if (this.task.getId() == null) {
					this.project.addTask(this.task);
				}

				em.persist(this.project);
				projectEvent.fire(project);
				logger.info("Saved {}", this.task.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.task.getArtifact());

				// set total estimated hours for new tasks
				this.totalHours = calcTotalHours();

				// new task count
				this.newTaskCount = newTaskCount();

				loadList();
				this.selected = true;

				// update producers
				this.projectEvent.fire(this.project);
				this.projectTaskSrc.fire(this.project);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * AssignedTo change event handler
	 * 
	 * @param event
	 */
	public void onAssignedToChange(ValueChangeEvent event) {
		try {
			AuthUser user = (AuthUser) event.getNewValue();
			ProjectMember projectMember = findProjectMember(user, this.project);
			this.task.setAssignee(projectMember);
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
		this.projectUser = this.task.getAssignee().getAuthUser();
	}

	/**
	 * Assessor
	 * 
	 * @return List
	 */
	public List<Task> getTasks() {
		return this.tasks;
	}

	/**
	 * @param tasks
	 *            the tasks to set
	 */
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	/**
	 * get Task
	 * 
	 * @return task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * set Task
	 * 
	 * @param task
	 */
	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
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
	 * @return the totalHours
	 */
	public double getTotalHours() {
		return totalHours;
	}

	/**
	 * @return the newTaskCount
	 */
	public int getNewTaskCount() {
		return newTaskCount;
	}

	/**
	 * @return the filteredTasks
	 */
	public List<Task> getFilteredTasks() {
		return filteredTasks;
	}

	/**
	 * @param filteredTasks
	 *            the filteredTasks to set
	 */
	public void setFilteredTasks(List<Task> filteredTasks) {
		this.filteredTasks = filteredTasks;
	}

	/**
	 * Validate task
	 * <ul>
	 * <li>If new task check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		logger.info("validate task {}", this.task);
		if (this.task.getId() == null) {
			if (this.tasks.contains(this.task)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.task.getName());
				logger.error("{} already exists", this.task.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}
		return isvalid;
	}

	/**
	 * Load Task list
	 * 
	 * @throws UCM
	 *             Exception
	 */
	private void loadList() throws UCMException {

		Set<String> versions = this.projectService.findActiveProductReleaseVersions(this.authUser, this.project);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Task> c = cb.createQuery(Task.class);
		Root<Task> obj = c.from(Task.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project), obj.get("productRelease").get("version").in(versions)).orderBy(cb.asc(obj.get("id")));
		this.tasks = em.createQuery(c).getResultList();

		// set total estimated hours for new tasks
		this.totalHours = calcTotalHours();

		// new task count
		this.newTaskCount = newTaskCount();
	}

	/**
	 * Find Project Member
	 * 
	 * @param authUser
	 * @param project
	 * @return ProjectMember or null if not found
	 */
	private ProjectMember findProjectMember(AuthUser authUser, Project project) {

		List<ProjectMember> list = null;
		ProjectMember projectMember = null;

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectMember> c = cb.createQuery(ProjectMember.class);
		Root<ProjectMember> obj = c.from(ProjectMember.class);
		c.select(obj).where(cb.equal(obj.get("project"), project), cb.equal(obj.get("authUser"), authUser));
		list = em.createQuery(c).getResultList();
		logger.info("list size {}", list.size());

		Iterator<ProjectMember> iter = list.iterator();
		if (iter.hasNext()) {
			projectMember = iter.next();
			logger.info("pm = {}", projectMember);
		}

		return projectMember;
	}

	/**
	 * Helper method to calculate total estimated hours for new tasks
	 * 
	 * @return total hours
	 */
	private double calcTotalHours() {

		double hours = 0.0;
		for (Task task : this.tasks) {
			if (task.getStatusType().equals(StatusType.New)) {
				if (task.getEstimatedEffort() != null) {
					hours += task.getEstimatedEffort().doubleValue();
				}
			}
		}
		return hours;
	}

	/**
	 * new task count
	 * 
	 * @return count
	 */
	private int newTaskCount() {

		int cnt = 0;
		for (Task task : this.tasks) {
			if (task.getStatusType().equals(StatusType.New)) {
				cnt++;
			}
		}
		return cnt;
	}

}