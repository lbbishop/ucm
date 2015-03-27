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
import java.util.ArrayList;
import java.util.List;

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

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.bean.TestResult;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.TestResultType;
import com.sfs.ucm.data.TestType;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.RequirementTest;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.service.TestService;
import com.sfs.ucm.service.UseCaseService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.Service;

/**
 * Test Coverage Report Controller
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("testCoverageAction")
public class TestCoverageAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@Service
	private UseCaseService useCaseService;

	@Inject
	@Service
	private TestService testService;

	@Inject
	@Service
	private ProjectService projectService;

	private List<TestResult> testCaseResults;

	private List<TestResult> requirementTestResults;

	private Project project;

	private List<Project> projects;

	private boolean selected;
	
	private int testCaseResultCount;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {

		this.selected = false;
		this.testCaseResults = new ArrayList<TestResult>();

		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			// load projects for selection
			loadProjects();
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
	 * @return the testCaseResults
	 */
	public List<TestResult> getTestCaseResults() {
		return testCaseResults;
	}

	/**
	 * @param testCaseResults
	 *            the testCaseResults to set
	 */
	public void setTestCaseResults(List<TestResult> testCaseResults) {
		this.testCaseResults = testCaseResults;
	}

	/**
	 * @return the requirementTestResults
	 */
	public List<TestResult> getRequirementTestResults() {
		return requirementTestResults;
	}

	/**
	 * @param requirementTestResults
	 *            the requirementTestResults to set
	 */
	public void setRequirementTestResults(List<TestResult> requirementTestResults) {
		this.requirementTestResults = requirementTestResults;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the projects
	 */
	public List<Project> getProjects() {
		return projects;
	}

	/**
	 * @return the testCaseResultCount
	 */
	public int getTestCaseResultCount() {
		return testCaseResultCount;
	}

	/**
	 * Project change event handler
	 * 
	 * @param event
	 */
	public void onProjectChange(ValueChangeEvent event) {
		this.project = (Project) event.getNewValue();

		// load test coverage results
		loadList(this.project);

		// update producers
		this.projectSecurityMarkingSrc.fire(this.project);
	}

	/**
	 * test coverage results
	 */
	private void loadList(final Project project) {

		List<TestResultType> passFail = new ArrayList<TestResultType>();
		passFail.add(TestResultType.Failed);
		passFail.add(TestResultType.Passed);
		passFail.add(TestResultType.Unknown);

		// RequirementTests
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TestResult> c = cb.createQuery(TestResult.class);
			Root<RequirementTest> obj = c.from(RequirementTest.class);
			c.select(cb.construct(TestResult.class, obj.get("identifier"), cb.literal(TestType.Requirement.toString()), obj.get("testSet").get("name"), obj.get("requirement").get("name"),
					obj.get("testSet").get("tester").get("authUser").get("name"), obj.get("modifiedDate"), obj.get("testResultType")));
			c.where(cb.equal(obj.get("testSet").get("testPlan").get("project"), project), obj.get("testResultType").in(passFail)).orderBy(cb.asc(obj.get("identifier")));
			this.requirementTestResults = em.createQuery(c).getResultList();

		}

		Long naltflows = this.useCaseService.getAlternativeFlowCountByProductRelease(authUser, project);
		Long nbasicflows = this.useCaseService.getBasicFlowCountByProductRelease(authUser, project);
		Long cnt = nbasicflows + naltflows;

		logger.info("Generating {} test coverage report with {} test cases", this.project.getName(), cnt);

		// loop thru each testset selecting most recent pass/fail test and adding it to the test results
		for (int i = 1; i <= cnt; i++) {
			TestResult mostRecentTestResult = this.testService.findMostRecentTestResultByIdentifier(i, project, passFail);
			if (mostRecentTestResult != null) {
				testCaseResults.add(mostRecentTestResult);
			}
		}
		
		this.testCaseResultCount = testCaseResults.size();
	}

	/**
	 * load projects
	 */
	private void loadProjects() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> c = cb.createQuery(Project.class);
		Root<Project> obj = c.from(Project.class);
		c.select(obj).orderBy(cb.asc(obj.get("name")));
		this.projects = em.createQuery(c).getResultList();
	}

}