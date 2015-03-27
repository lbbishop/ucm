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
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.TestResultType;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.TestCase;
import com.sfs.ucm.model.TestCaseStep;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * TestCase Flow Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("testCaseFlowAction")
public class TestCaseFlowAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private boolean editable;

	private TestCase testCase;

	private boolean selected;

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
			this.testCase = em.find(TestCase.class, id);

			// resort steps
			Collections.sort(testCase.getTestCaseSteps(), TESTCASESTEP_ORDER);
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
	 * save action
	 * <p>
	 * Update TestCase result based on test step results
	 * 
	 * @throws UCMException
	 */
	public void save() throws UCMException {
		try {
			this.testCase.setModifiedBy(authUser.getUsername());
			for (TestCaseStep tcs : this.testCase.getTestCaseSteps()) {
				if (tcs.getTestResultType() != null) {

					this.testCase.setTestResultType(TestResultType.Passed);

					if (tcs.getTestResultType().equals(TestResultType.Failed)) {
						this.testCase.setTestResultType(TestResultType.Failed);
						break;
					}
				}
				else {
					this.testCase.setTestResultType(TestResultType.Unknown);
				}
			}
			
			Timestamp now = new Timestamp(System.currentTimeMillis());
			this.testCase.setTestDate(now);

			em.persist(this.testCase);

			logger.info("saved: {}", this.testCase.getArtifact());
			this.facesContextMessage.infoMessage("{0} saved successfully", this.testCase.getArtifact());
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
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

	static final Comparator<TestCaseStep> TESTCASESTEP_ORDER = new Comparator<TestCaseStep>() {
		public int compare(TestCaseStep f1, TestCaseStep f2) {
			return f1.getStepNumber().compareTo(f2.getStepNumber());
		}
	};

}