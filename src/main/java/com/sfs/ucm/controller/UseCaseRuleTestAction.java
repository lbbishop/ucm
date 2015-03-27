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
import com.sfs.ucm.model.TestPlan;
import com.sfs.ucm.model.TestSet;
import com.sfs.ucm.model.UseCaseRule;
import com.sfs.ucm.model.UseCaseRuleTest;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.TestPlanUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * UseCaseRuleTest Test Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("useCaseRuleTestAction")
public class UseCaseRuleTestAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

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

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	private boolean editable;

	private UseCaseRuleTest useCaseRuleTest;

	private List<UseCaseRuleTest> useCaseRuleTests;

	private TestSet testSet;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.useCaseRuleTest = new UseCaseRuleTest();
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
			this.testSet = em.find(TestSet.class, id);

			loadList();

			// update tests
			updateTests();

			// update producer
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
		this.useCaseRuleTest = new UseCaseRuleTest(ModelUtils.getNextIdentifier(this.useCaseRuleTests));
	}

	/**
	 * Clear test results
	 * 
	 * @throws UCMException
	 */
	public void clearTestResults() throws UCMException {
		try {
			for (UseCaseRuleTest useCaseRuleTest : this.useCaseRuleTests) {
				useCaseRuleTest.setTestResultType(null);
			}

			// refresh list
			loadList();
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
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.testSet.removeUseCaseRuleTest(this.useCaseRuleTest);
			em.remove(this.useCaseRuleTest);
			logger.info("deleted {}", this.useCaseRuleTest.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.useCaseRuleTest.getArtifact());

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
	 * 
	 * @throws UCMException
	 */
	public void save() throws UCMException {
		try {
			this.useCaseRuleTest.setModifiedBy(authUser.getUsername());
			if (this.useCaseRuleTest.getId() == null) {
				this.testSet.addUseCaseRuleTest(this.useCaseRuleTest);
			}

			em.persist(this.testSet);
			logger.info("saved {}", this.useCaseRuleTest.getArtifact());
			this.facesContextMessage.infoMessage("{0} saved successfully", this.useCaseRuleTest.getArtifact());

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
	 * UseCaseRuleTests producer
	 * 
	 * @return List
	 */
	public List<UseCaseRuleTest> getUseCaseRuleTests() {
		return this.useCaseRuleTests;
	}

	/**
	 * get UseCaseRuleTest
	 * 
	 * @return useCaseRuleTest
	 */
	public UseCaseRuleTest getUseCaseRuleTest() {
		return useCaseRuleTest;
	}

	/**
	 * set UseCaseRuleTest
	 * 
	 * @param useCaseRuleTest
	 */
	public void setUseCaseRuleTest(UseCaseRuleTest useCaseRuleTest) {
		this.useCaseRuleTest = useCaseRuleTest;
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
	 * @param useCaseRuleTests
	 *            the useCaseRuleTests to set
	 */
	public void setUseCaseRuleTests(List<UseCaseRuleTest> useCaseRuleTests) {
		this.useCaseRuleTests = useCaseRuleTests;
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
	 * update tests
	 */
	private void updateTests() {

		// add new use case rule tests
		List<UseCaseRule> useCaseRules = findUseCaseRules(this.testSet.getTestPlan().getProject());

		for (UseCaseRule useCaseRule : useCaseRules) {
			if (!checkForDuplicate(useCaseRule)) {
				UseCaseRuleTest useCaseRuleTest = new UseCaseRuleTest(useCaseRule.getIdentifier(), useCaseRule);
				this.testSet.addUseCaseRuleTest(useCaseRuleTest);
				em.persist(this.testSet);
			}
		}

		// refresh list
		loadList();

	}

	/**
	 * Check for duplicate
	 * 
	 * @param useCaseRule
	 * @return flag true duplicate
	 */
	private boolean checkForDuplicate(UseCaseRule useCaseRule) {
		boolean duplicate = false;
		for (UseCaseRuleTest useCaseRuleTest : this.useCaseRuleTests) {
			if (useCaseRule.equals(useCaseRuleTest.getUseCaseRule())) {
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}

	/**
	 * load useCaseRuleTests
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UseCaseRuleTest> c = cb.createQuery(UseCaseRuleTest.class);
		Root<UseCaseRuleTest> obj = c.from(UseCaseRuleTest.class);
		c.select(obj).where(cb.equal(obj.get("testSet"), this.testSet)).orderBy(cb.asc(obj.get("id")));
		this.useCaseRuleTests = em.createQuery(c).getResultList();
	}

	/**
	 * Find UseCaseRules
	 * 
	 * @param project
	 * @return List of UseCaseRule
	 */
	private List<UseCaseRule> findUseCaseRules(Project project) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UseCaseRule> c = cb.createQuery(UseCaseRule.class);
		Root<UseCaseRule> obj = c.from(UseCaseRule.class);
		c.select(obj).where(cb.equal(obj.get("useCase").get("project"), project)).orderBy(cb.asc(obj.get("id")));
		List<UseCaseRule> useCaseRules = em.createQuery(c).getResultList();

		return useCaseRules;
	}

}