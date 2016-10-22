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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.TestPlan;
import com.sfs.ucm.model.UnitTest;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * UnitTest Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("unitTestAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UnitTestAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	private TestPlan testPlan;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private UnitTest unitTest;

	private List<UnitTest> unitTests;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.unitTest = new UnitTest();
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
			loadList();

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
	 * Add action
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.unitTest = new UnitTest(ModelUtils.getNextIdentifier(this.unitTests));
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.testPlan.removeUnitTest(this.unitTest);
			em.remove(this.unitTest);
			logger.info("deleted {}", this.unitTest.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.unitTest.getArtifact());

			// refresh list
			loadList();
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
				this.unitTest.setModifiedBy(authUser.getUsername());
				if (this.unitTest.getId() == null) {
					this.testPlan.addUnitTest(this.unitTest);
				}
				em.persist(this.testPlan);
				logger.info("saved {}", this.unitTest.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.unitTest.getArtifact());

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
	 * UnitTests producer
	 * 
	 * @return List
	 */
	public List<UnitTest> getUnitTests() {
		return this.unitTests;
	}

	/**
	 * get UnitTest
	 * 
	 * @return unitTest
	 */
	public UnitTest getUnitTest() {
		return unitTest;
	}

	/**
	 * set UnitTest
	 * 
	 * @param unitTest
	 */
	public void setUnitTest(UnitTest unitTest) {
		this.unitTest = unitTest;
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
	 * @return the testPlan
	 */
	public TestPlan getTestPlan() {
		return testPlan;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Validate unitTest
	 * <ul>
	 * <li>If new unitTest check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.unitTest.getId() == null) {
			if (this.unitTests.contains(this.unitTest)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.unitTest.getName());
				logger.error("{} already exists", this.unitTest);
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * Load unit tests
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitTest> c = cb.createQuery(UnitTest.class);
		Root<UnitTest> obj = c.from(UnitTest.class);
		c.select(obj).where(cb.equal(obj.get("testPlan"), this.testPlan)).orderBy(cb.asc(obj.get("id")));
		this.unitTests = em.createQuery(c).getResultList();
	}

}