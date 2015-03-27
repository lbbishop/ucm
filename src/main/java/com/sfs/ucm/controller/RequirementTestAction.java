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

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.Requirement;
import com.sfs.ucm.model.RequirementTest;
import com.sfs.ucm.model.TestPlan;
import com.sfs.ucm.model.TestSet;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.TestPlanUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * RequirementTest Test Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("requirementTestAction")
public class RequirementTestAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	@TestPlanUpdated
	Event<TestPlan> testPlanEventSrc;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private RequirementTest requirementTest;

	private List<RequirementTest> requirementTests;

	private TestSet testSet;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.requirementTest = new RequirementTest();
		this.selected = false;

		begin();
	}

	/**
	 * Controller resource loader
	 */
	public void load() {
		try {
			this.testSet = em.find(TestSet.class, id);

			loadList();

			// update tests
			updateTests();

			// update producers
			this.projectSecurityMarkingSrc.fire(this.testSet.getTestPlan().getProject());

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.testSet.getTestPlan().getProject());
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
	public void add() {
		this.requirementTest = new RequirementTest(ModelUtils.getNextIdentifier(this.requirementTests));
	}

	/**
	 * Clear test results
	 */
	public void clearTestResults() {

		for (RequirementTest requirementTest : this.requirementTests) {
			requirementTest.setTestResultType(null);
		}

		// refresh list
		loadList();

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
	 * Action: remove object
	 */
	public void remove() {
		try {
			this.testSet.removeRequirementTest(this.requirementTest);
			em.remove(this.requirementTest);
			logger.info("deleted {}", this.requirementTest.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.requirementTest.getArtifact());

			// refresh list
			loadList();

			// update listeners
			this.testPlanEventSrc.fire(this.testSet.getTestPlan());

			this.selected = false;
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * save action
	 */
	public void save() {
		try {
			this.requirementTest.setModifiedBy(authUser.getUsername());
			if (this.requirementTest.getId() == null) {
				this.testSet.addRequirementTest(this.requirementTest);
			}

			em.persist(this.testSet);
			logger.info("saved {}", this.requirementTest.getArtifact());
			this.facesContextMessage.infoMessage("{0} saved successfully", this.requirementTest.getArtifact());

			// refresh list
			loadList();

			// update listeners
			this.testPlanEventSrc.fire(this.testSet.getTestPlan());

			this.selected = true;
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * RequirementTests producer
	 * 
	 * @return List
	 */
	public List<RequirementTest> getRequirementTests() {
		return this.requirementTests;
	}

	/**
	 * get RequirementTest
	 * 
	 * @return requirementTest
	 */
	public RequirementTest getRequirementTest() {
		return requirementTest;
	}

	/**
	 * set RequirementTest
	 * 
	 * @param requirementTest
	 */
	public void setRequirementTest(RequirementTest requirementTest) {
		this.requirementTest = requirementTest;
	}

	/**
	 * @return the testSet
	 */
	public TestSet getTestSet() {
		return testSet;
	}

	/**
	 * @param testSet
	 *            the testSet to set
	 */
	public void setTestSet(TestSet testSet) {
		this.testSet = testSet;
	}

	/**
	 * @param requirementTests
	 *            the requirementTests to set
	 */
	public void setRequirementTests(List<RequirementTest> requirementTests) {
		this.requirementTests = requirementTests;
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
	 * Update tests
	 * <p>
	 * Creates new tests
	 */
	private void updateTests() {

		logger.debug("updateTests called");

		// requirement tests
		List<Requirement> requirements = findRequirements(this.testSet.getTestPlan().getProject());

		for (Requirement requirement : requirements) {
			if (!checkForDuplicate(requirement)) {
				RequirementTest requirementTest = new RequirementTest(requirement.getIdentifier(), requirement);
				this.testSet.addRequirementTest(requirementTest);
				em.persist(this.testSet);
			}
		}

		// refresh list
		loadList();

	}

	/**
	 * Check for duplicate artifact
	 * 
	 * @param requirement
	 * @return flag true if validation is successful
	 */
	private boolean checkForDuplicate(Requirement requirement) {
		boolean duplicate = false;
		for (RequirementTest requirementTest : this.requirementTests) {
			if (requirement.equals(requirementTest.getRequirement())) {
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}

	/**
	 * load requirementTests
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RequirementTest> c = cb.createQuery(RequirementTest.class);
		Root<RequirementTest> obj = c.from(RequirementTest.class);
		c.select(obj).where(cb.equal(obj.get("testSet"), this.testSet)).orderBy(cb.asc(obj.get("id")));
		this.requirementTests = em.createQuery(c).getResultList();
	}

	/**
	 * Find project requirements
	 * 
	 * @param project
	 * @return List of Requirement
	 */
	private List<Requirement> findRequirements(Project project) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Requirement> c = cb.createQuery(Requirement.class);
		Root<Requirement> obj = c.from(Requirement.class);
		c.select(obj).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("id")));
		List<Requirement> requirements = em.createQuery(c).getResultList();

		return requirements;
	}

}