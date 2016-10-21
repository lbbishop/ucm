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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.sfs.ucm.data.TestResultType;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Flow;
import com.sfs.ucm.model.FlowStep;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.model.TestCase;
import com.sfs.ucm.model.TestCaseStep;
import com.sfs.ucm.model.TestPlan;
import com.sfs.ucm.model.TestSet;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.util.TestPlanUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * TestCase Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("testCaseAction")
public class TestCaseAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private TestSet testSet;

	private TestPlan testPlan;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@TestPlanUpdated
	Event<TestPlan> testPlanEvent;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private TestCase testCase;

	private List<TestCase> testCases;

	private List<TestCase> filteredTestCases;

	private List<UseCase> useCases;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.testCase = new TestCase();
		this.useCases = new ArrayList<UseCase>();
		this.selected = false;

		begin();
	}

	/**
	 * Controller resource loaders
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.testSet = em.find(TestSet.class, id);
			this.testPlan = testSet.getTestPlan();
			loadList();

			// update producers
			this.projectSecurityMarkingSrc.fire(this.testPlan.getProject());

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.testPlan.getProject());
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
		this.testCase = (TestCase) event.getObject();
		this.selected = true;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.testSet.removeTestCase(this.testCase);
			em.remove(this.testCase);
			logger.info("deleted {}", this.testCase);
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.testCase.getArtifact());

			// refresh list
			loadList();
			testPlanEvent.fire(this.testPlan);

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
				this.testCase.setModifiedBy(authUser.getUsername());
				if (this.testCase.getId() == null) {
					this.testSet.addTestCase(this.testCase);
				}

				em.persist(this.testSet);
				testPlanEvent.fire(this.testPlan);
				logger.info("saved: {}", this.testCase.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.testCase.getArtifact());

				// refresh list
				loadList();
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * TestCases producer
	 * 
	 * @return List
	 */
	public List<TestCase> getTestCases() {
		return this.testCases;
	}

	/**
	 * get TestCase
	 * 
	 * @return testCase
	 */
	public TestCase getTestCase() {
		return testCase;
	}

	/**
	 * set TestCase
	 * 
	 * @param testCase
	 */
	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
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
	 * @return the filteredTestCases
	 */
	public List<TestCase> getFilteredTestCases() {
		return filteredTestCases;
	}

	/**
	 * @param filteredTestCases
	 *            the filteredTestCases to set
	 */
	public void setFilteredTestCases(List<TestCase> filteredTestCases) {
		this.filteredTestCases = filteredTestCases;
	}

	/**
	 * Update TestCases action
	 * <p>
	 * Update procedure
	 * <ul>
	 * <li>alternative flow name = basic flow name; alternative flow name</li>
	 * </ul>
	 * 
	 * @throws UCMException
	 */
	public void updateTestCases() throws UCMException {

		try {
			
			// load test cases
			loadUseCases();

			for (UseCase useCase : this.useCases) {

				// ========== BasicFlow Test Cases ==========
				TestCase testCase = findBasicFlowTestCase(this.testSet.getTester(), useCase);
				if (testCase == null) {
					testCase = new TestCase(ModelUtils.getNextIdentifier(this.testSet.getTestCases()));

					// generate the flow
					genBasicTestCaseFlow(useCase, testCase);

					this.testSet.addTestCase(testCase);

					em.persist(this.testSet);
					logger.info("Added BasicFlow TestCase {}", testCase.getName());
				}
				else {

					// test case exists and is out of date
					if (useCase.getModifiedDate().after(testCase.getModifiedDate())) {
						String conditions = testCase.getInputData();
						int identifier = testCase.getIdentifier().intValue();

						this.testSet.removeTestCase(testCase);
						em.remove(testCase);
						em.flush();

						testCase = new TestCase(identifier);
						testCase.setInputData(conditions);

						// generate the flow
						genBasicTestCaseFlow(useCase, testCase);

						this.testSet.addTestCase(testCase);

						em.persist(this.testSet);
						logger.info("Updated BasicFlow TestCase {}", testCase.getName());
					}
				}

				// ========== AlternativeFlow Test Cases ==========
				// retrieve use case alternative flows
				List<Flow> alternativeFlows = findAlternativeFlows(useCase);

				logger.info("Processing {} alternative flows for UseCase: {}", alternativeFlows.size(), useCase.getIdentifierName());

				// loop thru alternative flows and generate
				for (Flow alternativeFlow : alternativeFlows) {

					testCase = findAlternativeFlowTestCase(this.testSet.getTester(), alternativeFlow);
					if (testCase == null) {

						// construct test case
						testCase = new TestCase(ModelUtils.getNextIdentifier(this.testSet.getTestCases()));

						// generate new alternative test case flow
						genAlternativeTestCaseFlow(useCase, testCase, alternativeFlow);

						this.testSet.addTestCase(testCase);

						em.persist(this.testSet);
					}
					else {

						// test case exists and is out of date with respect to use case or alternative flow
						if (useCase.getModifiedDate().after(testCase.getModifiedDate()) || alternativeFlow.getModifiedDate().after(testCase.getModifiedDate())) {
							String conditions = testCase.getInputData();
							int identifier = testCase.getIdentifier().intValue();

							this.testSet.removeTestCase(testCase);
							em.remove(testCase);
							em.flush();

							testCase = new TestCase(identifier);
							testCase.setInputData(conditions);

							// generate new alternative test case flow
							genAlternativeTestCaseFlow(useCase, testCase, alternativeFlow);

							this.testSet.addTestCase(testCase);

							em.persist(this.testSet);
							logger.info("Updated AlternativeFlow TestCase {}", testCase.getName());
						}
					}
				}
			}

			// reload test cases
			loadList();
			
			// reload test cases
			loadList();

			// reorder identifiers
			for (int i = 0; i < this.testCases.size(); i++) {
				TestCase tc = this.testCases.get(i);
				tc.setIdentifier(i + 1);
				em.persist(tc);
			}

			// fire update event
			this.testPlanEvent.fire(this.testPlan);
			this.facesContextMessage.infoMessage("Successfully synchronized test cases for TestSet {0}", this.testSet.getName());
		}
		catch (Exception e) {
			logger.error("Error occurred updating test cases {}", e.getMessage());
			throw new UCMException(e);
		}
	}

	/**
	 * Clear all testcase results
	 */
	public void clearAllTestResults() {

		for (TestCase testCase : this.testCases) {
			testCase.setTestResultType(null);

			for (TestCaseStep testCaseStep : testCase.getTestCaseSteps()) {
				testCaseStep.setTestResultType(null);
			}
		}

		// refresh list
		loadList();

	}

	/**
	 * Clear selected testcase results
	 */
	public void clearTestResults() {

		testCase.setTestResultType(TestResultType.Unknown);

		for (TestCaseStep testCaseStep : testCase.getTestCaseSteps()) {
			testCaseStep.setTestResultType(null);
		}

		// refresh list
		loadList();

	}

	/**
	 * Get number of list items
	 * 
	 * @return number of items
	 */
	public int getNumItems() {
		return this.testCases.size();
	}

	/**
	 * Generate Basic TestCase Flow
	 * 
	 * @param useCase
	 * @param testCase
	 */
	private void genBasicTestCaseFlow(UseCase useCase, TestCase testCase) {

		// if usecase name differs from basic flow name then prefix with usecase name
		String name = useCase.getBasicFlow().getName().trim();
		if (!useCase.getName().trim().equals(useCase.getBasicFlow().getName().trim())) {
			name = useCase.getName().trim() + ":" + useCase.getBasicFlow().getName().trim();
		}
		testCase.setName(name);
		testCase.setDescription(useCase.getObjective());
		testCase.setTester(this.testSet.getTester());
		testCase.setUseCase(useCase);

		// add flow steps
		for (FlowStep flowStep : useCase.getBasicFlow().getFlowSteps()) {
			TestCaseStep testCaseStep = new TestCaseStep(flowStep.getStepNumber(), flowStep);
			testCase.addTestCaseStep(testCaseStep);
		}

		// add optional extended flow steps
		if (useCase.getExtendedFlow() != null) {
			short stepnum = (short)testCase.getTestCaseSteps().size();
			stepnum++;
			
			// log message
			logger.info("Adding {} extended flow steps using flow {}", useCase.getExtendedFlow().getFlowSteps().size(), useCase.getExtendedFlow().getName());
			for (FlowStep extendedFlowStep : useCase.getExtendedFlow().getFlowSteps()) {
				TestCaseStep testCaseStep = new TestCaseStep(stepnum, extendedFlowStep.getActor(), extendedFlowStep.getActionDescription());
				testCase.addTestCaseStep(testCaseStep);
				stepnum++;
			}			
		}

	}

	/**
	 * Generate alternative test case flow
	 * 
	 * @param useCase
	 * @param testCase
	 * @param alternativeFlow
	 */
	private void genAlternativeTestCaseFlow(UseCase useCase, TestCase testCase, Flow alternativeFlow) {

		// assign begin and end steps for flow
		Short beginStep = alternativeFlow.getStartStep().shortValue();
		Short endStep = alternativeFlow.getEndStep().shortValue();

		// name = usecase name:alternative flow name
		String name = useCase.getName().trim() + ":" + alternativeFlow.getName().trim();
		logger.debug("Adding TestCase using alternative flow: [{}], TestCase name: {}", name);

		testCase.setName(name);
		testCase.setDescription(useCase.getObjective());
		testCase.setTester(this.testSet.getTester());
		testCase.setUseCase(useCase);

		// add basic flow steps up beginStep
		short stepnum = 1;
		for (FlowStep basicFlowStep : useCase.getBasicFlow().getFlowSteps()) {
			if (basicFlowStep.getStepNumber() < beginStep) {
				TestCaseStep testCaseStep = new TestCaseStep(stepnum, basicFlowStep.getActor(), basicFlowStep.getActionDescription());
				testCase.addTestCaseStep(testCaseStep);
				stepnum++;
			}
		}

		// loop thru alternative flow steps
		for (FlowStep flowStep : alternativeFlow.getFlowSteps()) {
			// add alternative flow steps
			TestCaseStep testCaseStep = new TestCaseStep(stepnum, flowStep);
			testCase.addTestCaseStep(testCaseStep);
			stepnum++;
		}

		// finish test case flow with basic flow steps if end step is set
		if (endStep != null && endStep != 0) {
			for (FlowStep basicFlowStep : useCase.getBasicFlow().getFlowSteps()) {
				if (basicFlowStep.getStepNumber() > endStep) {
					TestCaseStep testCaseStep = new TestCaseStep(stepnum, basicFlowStep.getActor(), basicFlowStep.getActionDescription());
					testCase.addTestCaseStep(testCaseStep);
					stepnum++;
				}
			}
		}
	}

	/**
	 * Find TestCase associated with Basic Flow UseCase
	 * 
	 * @param tester
	 * @param UseCase
	 * @return TestCase the TestCase found or null if not found
	 */
	private TestCase findBasicFlowTestCase(final ProjectMember tester, final UseCase useCase) {
		TestCase testCase = null;

		String name = "%" + useCase.getBasicFlow().getName().trim() + "%";
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestCase> c = cb.createQuery(TestCase.class);
		Root<TestCase> obj = c.from(TestCase.class);
		c.select(obj).where(cb.equal(obj.get("tester"), tester), cb.like(obj.<String> get("name"), name));
		List<TestCase> list = em.createQuery(c).getResultList();

		Iterator<TestCase> iter = list.iterator();
		if (iter.hasNext()) {
			testCase = iter.next();
		}
		return testCase;
	}

	/**
	 * Find TestCase associated with AlternativeFlow
	 * 
	 * @param tester
	 * @param alternativeFlow
	 * @return TestCase the TestCase found or null if not found
	 */
	private TestCase findAlternativeFlowTestCase(final ProjectMember tester, final Flow alternativeFlow) {
		TestCase testCase = null;

		String name = "%" + alternativeFlow.getName().trim() + "%";
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestCase> c = cb.createQuery(TestCase.class);
		Root<TestCase> obj = c.from(TestCase.class);
		c.select(obj).where(cb.equal(obj.get("tester"), tester), cb.like(obj.<String> get("name"), name));
		List<TestCase> list = em.createQuery(c).getResultList();

		logger.debug("findAlternativeFlowTestCase list size {}", list.size());

		Iterator<TestCase> iter = list.iterator();
		if (iter.hasNext()) {
			testCase = iter.next();
		}
		return testCase;
	}

	/**
	 * Find Alternative Flows associated with use case
	 * 
	 * @param UseCase
	 * @return List of alternative flows
	 */
	private List<Flow> findAlternativeFlows(final UseCase useCase) {
		List<Flow> list = null;

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Flow> c = cb.createQuery(Flow.class);
		Root<Flow> obj = c.from(Flow.class);
		c.select(obj).where(cb.equal(obj.get("useCase"), useCase));
		list = em.createQuery(c).getResultList();

		return list;
	}

	/**
	 * Validate testCase
	 * <ul>
	 * <li>If new testCase check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.testCase.getId() == null) {
			if (this.testCases.contains(this.testCase)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.testCase.getName());
				logger.error("{} already exists", this.testCase.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}
		return isvalid;
	}

	/**
	 * Load test cases for active viewset product releases
	 * <p>
	 * order by test case name
	 */
	private void loadList() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestCase> c = cb.createQuery(TestCase.class);
		Root<TestCase> obj = c.from(TestCase.class);
		c.select(obj).where(cb.equal(obj.get("testSet"), this.testSet)).orderBy(cb.asc(obj.get("name")));
		this.testCases = em.createQuery(c).getResultList();
	}

	/**
	 * Load all use cases with Implemented status associated with this project test plan for active viewset product releases
	 */
	private void loadUseCases() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UseCase> c = cb.createQuery(UseCase.class);
		Root<UseCase> obj = c.from(UseCase.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.testPlan.getProject())).orderBy(cb.asc(obj.get("id")));
		this.useCases = em.createQuery(c).getResultList();
	}

}