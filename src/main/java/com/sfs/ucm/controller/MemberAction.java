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
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.UserUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * ProjectMember Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("memberAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MemberAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEventSrc;

	@Inject
	@UserUpdated
	private Event<AuthUser> userSrc;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private ProjectMember projectMember;

	@Inject
	@ProjectSecurityInit
	private Event<Project> projectSecurityMarkingSrc;

	private List<ProjectMember> projectMembers;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.projectMember = new ProjectMember();
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

			// update producers
			this.userSrc.fire(this.authUser);
			this.projectSecurityMarkingSrc.fire(this.project);
			loadList();

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
		this.projectMember = new ProjectMember(ModelUtils.getNextIdentifier(this.projectMembers));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.project.removeProjectMember(this.projectMember);
			em.remove(this.projectMember);
			logger.info("deleted {}", this.projectMember.getId());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.projectMember.getArtifact());

			// refresh list
			loadList();

			// fire update events
			this.projectEventSrc.fire(project);
			
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
				this.projectMember.setModifiedBy(authUser.getUsername());
				if (this.projectMember.getId() == null) {
					this.project.addProjectMember(this.projectMember);
				}
				em.persist(this.project);
				
				// fire update events
				this.projectEventSrc.fire(project);
				
				this.facesContextMessage.infoMessage("messages", "{0} saved successfully", this.projectMember.getArtifact());
				logger.info("saved: {}", this.projectMember.getArtifact());

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
	 * member authUser value change
	 * <p>
	 * Set this member authUser to selected authUser
	 * 
	 * @param e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void authUserValueChange(ValueChangeEvent e) {
		AuthUser selectedUser = (AuthUser) e.getNewValue();
		this.projectMember.setAuthUser(selectedUser);
	}

	/**
	 * ProjectMembers producer
	 * 
	 * @return List
	 */
	public List<ProjectMember> getProjectMembers() {
		return this.projectMembers;
	}

	/**
	 * get ProjectMember
	 * 
	 * @return projectMember
	 */
	public ProjectMember getProjectMember() {
		return projectMember;
	}

	/**
	 * set ProjectMember
	 * 
	 * @param projectMember
	 */
	public void setProjectMember(ProjectMember projectMember) {
		this.projectMember = projectMember;
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
	 * Validate projectMember
	 * <ul>
	 * <li>If new projectMember check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.projectMember.getId() == null) {
			for (ProjectMember theProjectMember : this.projectMembers) {
				if (this.projectMember.getAuthUser().getName().equals(theProjectMember.getAuthUser().getName())) {
					this.facesContextMessage.errorMessage("dialogMessages", "{0} already exists", this.projectMember.getAuthUser().getName());
					logger.error("{} already exists", this.projectMember.getAuthUser().getName());
					isvalid = false;
					RequestContext requestContext = RequestContext.getCurrentInstance();
					requestContext.addCallbackParam("validationFailed", !isvalid);
					break;
				}
			}
		}

		return isvalid;
	}

	/**
	 * Load members
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectMember> c = cb.createQuery(ProjectMember.class);
		Root<ProjectMember> obj = c.from(ProjectMember.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.projectMembers = em.createQuery(c).getResultList();
	}

}