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

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.StatusType;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Issue;
import com.sfs.ucm.model.IssueAttachment;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Issue Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("issueAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class IssueAction extends ActionBase implements Serializable {

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
	@Service
	private ProjectService projectService;

	private Issue issue;

	private List<Issue> issues;

	private List<Issue> filteredIssues;

	private Project project;

	private AuthUser projectUser;

	private boolean selected;

	private double totalHours;

	private int newIssueCount;

	private List<AuthUser> projectUsers;

	private StreamedContent attachmentFile;

	private IssueAttachment attachment;

	@Inject
	public void init() {
		this.issue = new Issue();
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

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);

			// load resources
			loadList();
			loadProjectUsers(this.project);
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
		this.issue = new Issue(ModelUtils.getNextIdentifier(this.issues));
		this.issue.setStatusType(StatusType.Open);
		this.projectUser = null;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.project.removeIssue(this.issue);
			em.remove(this.issue);
			logger.info("Deleted {}", this.issue.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.issue.getArtifact());
			loadList();

			this.selected = false;

			// set total estimated hours for new issues
			this.totalHours = calcTotalHours();

			// new issue count
			this.newIssueCount = newIssueCount();
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
				this.issue.setModifiedBy(authUser.getUsername());
				if (this.issue.getId() == null) {
					this.project.addIssue(this.issue);
				}

				em.persist(this.project);
				projectEvent.fire(project);
				logger.info("Saved {}", this.issue.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.issue.getArtifact());

				// set total estimated hours for new issues
				this.totalHours = calcTotalHours();

				// new issue count
				this.newIssueCount = newIssueCount();

				loadList();
				this.selected = true;
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void onAssignedToChange(ValueChangeEvent event) {
		try {
			AuthUser user = (AuthUser) event.getNewValue();
			ProjectMember projectMember = findProjectMember(user, this.project);
			this.issue.setAssignee(projectMember);
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
		this.projectUser = this.issue.getAssignee().getAuthUser();
	}

	/**
	 * File Upload Handler
	 * 
	 * @param event
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void handleFileUpload(FileUploadEvent event) {

		// extract filename
		File file = new File(event.getFile().getFileName());

		logger.info("Uploading file: {}, type: {}", file.getName(), event.getFile().getContentType());
		this.attachment.setFilename(file.getName());
		this.attachment.setVersion(1);
		this.attachment.setContents(event.getFile().getContents());
		this.attachment.setContentType(event.getFile().getContentType());

		this.facesContextMessage.infoMessage("Uploaded file {0}", event.getFile().getFileName());

	}

	// =================== accessors ===========================
	/**
	 * Assessor
	 * 
	 * @return List
	 */
	public List<Issue> getIssues() {
		return this.issues;
	}

	/**
	 * @param issues
	 *            the issues to set
	 */
	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	/**
	 * get Issue
	 * 
	 * @return issue
	 */
	public Issue getIssue() {
		return issue;
	}

	/**
	 * set Issue
	 * 
	 * @param issue
	 */
	public void setIssue(Issue issue) {
		this.issue = issue;
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
	 * @return the newIssueCount
	 */
	public int getNewIssueCount() {
		return newIssueCount;
	}

	/**
	 * @return the filteredIssues
	 */
	public List<Issue> getFilteredIssues() {
		return filteredIssues;
	}

	/**
	 * @param filteredIssues
	 *            the filteredIssues to set
	 */
	public void setFilteredIssues(List<Issue> filteredIssues) {
		this.filteredIssues = filteredIssues;
	}

	/**
	 * @return the projectUsers
	 */
	public List<AuthUser> getProjectUsers() {
		return projectUsers;
	}

	/**
	 * @return the attachment
	 */
	public IssueAttachment getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment
	 *            the attachment to set
	 */
	public void setAttachment(IssueAttachment attachment) {
		this.attachment = attachment;
	}

	/**
	 * @return the attachmentFile
	 */
	public StreamedContent getAttachmentFile() {
		return attachmentFile;
	}

	/**
	 * @param attachmentFile
	 *            the attachmentFile to set
	 */
	public void setAttachmentFile(StreamedContent attachmentFile) {
		this.attachmentFile = attachmentFile;
	}

	// ================= private methods =======================
	/**
	 * Validate issue
	 * <ul>
	 * <li>If new issue check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		logger.info("validate issue {}", this.issue);
		if (this.issue.getId() == null) {
			if (this.issues.contains(this.issue)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.issue.getTitle());
				logger.error("{} already exists", this.issue.getTitle());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}
		return isvalid;
	}

	/**
	 * Load Issue list
	 * 
	 * @throws UCM
	 *             Exception
	 */
	private void loadList() throws UCMException {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Issue> c = cb.createQuery(Issue.class);
		Root<Issue> obj = c.from(Issue.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.issues = em.createQuery(c).getResultList();

		// set total estimated hours for new issues
		this.totalHours = calcTotalHours();

		// new issue count
		this.newIssueCount = newIssueCount();
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
	 * Helper method to calculate total estimated hours for open issues
	 * 
	 * @return total hours
	 */
	private double calcTotalHours() {

		double hours = 0.0;
		for (Issue issue : this.issues) {
			if (issue.getStatusType().equals(StatusType.Open)) {
				if (issue.getEstimatedEffort() != null) {
					hours += issue.getEstimatedEffort().doubleValue();
				}
			}
		}
		return hours;
	}

	/**
	 * new issue count
	 * 
	 * @return count
	 */
	private int newIssueCount() {

		int cnt = 0;
		for (Issue issue : this.issues) {
			if (issue.getStatusType().equals(StatusType.Open)) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * Load resources
	 * 
	 * @param project
	 */
	private void loadProjectUsers(final Project project) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AuthUser> c = cb.createQuery(AuthUser.class);
		Root<ProjectMember> obj = c.from(ProjectMember.class);
		c.select(obj.<AuthUser> get("authUser")).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("authUser").get("name")));
		this.projectUsers = em.createQuery(c).getResultList();
	}

}