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
import com.sfs.ucm.model.Specification;
import com.sfs.ucm.model.SpecificationTest;
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
 * Specification Test Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("specificationTestAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SpecificationTestAction extends ActionBase implements Serializable {

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

	private SpecificationTest specificationTest;

	private List<SpecificationTest> specificationTests;

	private TestSet testSet;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.specificationTest = new SpecificationTest();
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.specificationTest = new SpecificationTest(ModelUtils.getNextIdentifier(this.specificationTests));
	}

	/**
	 * Clear test results
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void clearTestResults() {

		for (SpecificationTest specificationTest : this.specificationTests) {
			specificationTest.setTestResultType(null);
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() {
		try {
			this.testSet.removeSpecificationTest(this.specificationTest);
			em.remove(this.specificationTest);
			logger.info("deleted {}", this.specificationTest.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.specificationTest.getArtifact());

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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save() {
		try {
			this.specificationTest.setModifiedBy(authUser.getUsername());
			if (this.specificationTest.getId() == null) {
				this.testSet.addSpecificationTest(this.specificationTest);
			}

			em.persist(this.testSet);
			logger.info("saved {}", this.specificationTest.getArtifact());
			this.facesContextMessage.infoMessage("{0} saved successfully", this.specificationTest.getArtifact());

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
	 * SpecificationTests producer
	 * 
	 * @return List
	 */
	public List<SpecificationTest> getSpecificationTests() {
		return this.specificationTests;
	}

	/**
	 * get SpecificationTest
	 * 
	 * @return specificationTest
	 */
	public SpecificationTest getSpecificationTest() {
		return specificationTest;
	}

	/**
	 * set SpecificationTest
	 * 
	 * @param specificationTest
	 */
	public void setSpecificationTest(SpecificationTest specificationTest) {
		this.specificationTest = specificationTest;
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
	 * @param specificationTests
	 *            the specificationTests to set
	 */
	public void setSpecificationTests(List<SpecificationTest> specificationTests) {
		this.specificationTests = specificationTests;
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

		// specification tests
		List<Specification> specifications = findSpecifications(this.testSet.getTestPlan().getProject());

		for (Specification specification : specifications) {
			if (!checkForDuplicate(specification)) {
				SpecificationTest specificationTest = new SpecificationTest(specification.getIdentifier(), specification);
				this.testSet.addSpecificationTest(specificationTest);
				em.persist(this.testSet);
			}
		}

		// refresh list
		loadList();

	}

	/**
	 * Check for duplicate artifact
	 * 
	 * @param specification
	 * @return flag true if validation is successful
	 */
	private boolean checkForDuplicate(Specification specification) {
		boolean duplicate = false;
		for (SpecificationTest specificationTest : this.specificationTests) {
			if (specification.equals(specificationTest.getSpecification())) {
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}

	/**
	 * load specificationTests
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SpecificationTest> c = cb.createQuery(SpecificationTest.class);
		Root<SpecificationTest> obj = c.from(SpecificationTest.class);
		c.select(obj).where(cb.equal(obj.get("testSet"), this.testSet)).orderBy(cb.asc(obj.get("id")));
		this.specificationTests = em.createQuery(c).getResultList();
	}

	/**
	 * Find project specifications
	 * 
	 * @param project
	 * @return List of Specification
	 */
	private List<Specification> findSpecifications(Project project) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Specification> c = cb.createQuery(Specification.class);
		Root<Specification> obj = c.from(Specification.class);
		c.select(obj).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("id")));
		List<Specification> specifications = em.createQuery(c).getResultList();

		return specifications;
	}

}