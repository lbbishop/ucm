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

import com.sfs.ucm.bean.TestResult;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.TestType;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.RequirementTest;
import com.sfs.ucm.model.TestCase;
import com.sfs.ucm.model.TestSet;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectSecurityInit;

/**
 * Test Set Report Controller
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("testSetReporter")
public class TestSetReporter extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private List<TestResult> testCaseResults;

	private List<TestResult> requirementTestResults;

	private TestSet testSet;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.testSet = em.find(TestSet.class, this.id);

			// load testset summary
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
	 * testset results
	 */
	private void loadList() {

		// RequirementTests
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TestResult> c = cb.createQuery(TestResult.class);
			Root<RequirementTest> obj = c.from(RequirementTest.class);
			c.select(cb.construct(TestResult.class, obj.get("identifier"), cb.literal(TestType.Requirement.toString()), obj.get("testSet").get("name"), obj.get("requirement").get("name"),
					obj.get("testSet").get("tester").get("authUser").get("name"), obj.get("modifiedDate"), obj.get("testResultType")));
			c.where(cb.equal(obj.get("testSet").get("id"), this.id)).orderBy(cb.asc(obj.get("identifier")));
			this.requirementTestResults = em.createQuery(c).getResultList();
		}

		// TestCases
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TestResult> c = cb.createQuery(TestResult.class);
			Root<TestCase> obj = c.from(TestCase.class);
			c.select(cb.construct(TestResult.class, obj.get("identifier"), cb.literal(TestType.TestCase.toString()), obj.get("testSet").get("name"), obj.get("name"), obj.get("testSet").get("tester")
					.get("authUser").get("name"), obj.get("testDate"), obj.get("testResultType")));
			c.where(cb.equal(obj.get("testSet").get("id"), this.id)).orderBy(cb.asc(obj.get("identifier")));
			this.testCaseResults = em.createQuery(c).getResultList();
		}

	}

}