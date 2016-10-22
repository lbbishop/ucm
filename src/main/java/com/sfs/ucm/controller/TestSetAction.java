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

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.model.TestPlan;
import com.sfs.ucm.model.TestSet;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectMemberUpdated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.ProjectUserInit;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * TestSet Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("testSetAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TestSetAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private TestPlan testPlan;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectEventSrc;

	@Inject
	@ProjectMemberUpdated
	Event<Project> projectMemberEventSrc;

	@Inject
	@ProjectUserInit
	Event<Project> projectUserSrc;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	private Logger logger;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private TestSet testSet;

	private List<TestSet> testSets;

	private boolean selected;

	private AuthUser user;

	private Project project;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.testSet = new TestSet();
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
			this.testPlan = em.find(TestPlan.class, id);
			this.project = this.testPlan.getProject();
			loadList();

			// update producers
			this.projectUserSrc.fire(this.project);
			this.projectSecurityMarkingSrc.fire(this.project);
			this.projectMemberEventSrc.fire(this.project);

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
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		this.selected = true;
	}

	/**
	 * Add action
	 * 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.testSet = new TestSet(ModelUtils.getNextIdentifier(this.testSets));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.testPlan.removeTestSet(this.testSet);
			em.remove(this.testSet);
			logger.info("deleted {}", this.testSet.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.testSet.getArtifact());

			// refresh list
			loadList();
			projectEventSrc.fire(this.project);

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
				this.testSet.setModifiedBy(authUser.getUsername());
				if (this.testSet.getId() == null) {
					this.testPlan.addTestSet(this.testSet);
				}

				em.persist(this.testPlan);
				projectEventSrc.fire(project);
				logger.info("saved {}", this.testSet.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.testSet.getArtifact());

				// refresh list
				loadList();
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Value change handler
	 * 
	 * @param event
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void onUserChange(ValueChangeEvent event) throws UCMException {
		try {
			AuthUser user = (AuthUser) event.getNewValue();

			ProjectMember projectMember = findProjectMember(user, this.project);
			this.testSet.setTester(projectMember);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * TestSets producer
	 * 
	 * @return List
	 */
	public List<TestSet> getTestSets() {
		return this.testSets;
	}

	/**
	 * get TestSet
	 * 
	 * @return testSet
	 */
	public TestSet getTestSet() {
		return testSet;
	}

	/**
	 * set TestSet
	 * 
	 * @param testSet
	 */
	public void setTestSet(TestSet testSet) {
		this.testSet = testSet;
	}

	/**
	 * @return the testPlan
	 */
	public TestPlan getTestPlan() {
		return testPlan;
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
	 * @return the user
	 */
	public AuthUser getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(AuthUser user) {
		this.user = user;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Validate testSet
	 * <ul>
	 * <li>If new testSet check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;

		return isvalid;
	}

	/**
	 * Load test sets
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestSet> c = cb.createQuery(TestSet.class);
		Root<TestSet> obj = c.from(TestSet.class);
		c.select(obj).where(cb.equal(obj.get("testPlan"), this.testPlan)).orderBy(cb.asc(obj.get("id")));
		this.testSets = em.createQuery(c).getResultList();
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

}