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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ConversationScoped;
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
import com.sfs.ucm.model.Actor;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Flow;
import com.sfs.ucm.model.FlowStep;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.TestCase;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * AlternativeFlow Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("alternativeFlowAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AlternativeFlowAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	static final Comparator<FlowStep> FLOWSTEP_ORDER = new Comparator<FlowStep>() {
		public int compare(FlowStep f1, FlowStep f2) {
			return f1.getStepNumber().compareTo(f2.getStepNumber());
		}
	};

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
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	@Service
	private ProjectService projectService;

	private UseCase useCase;

	private Flow alternativeFlow;

	private Project project;

	private boolean selected;

	private Long flowId;

	private FlowStep alternativeFlowStep;

	private boolean alternativeStepSelected;

	/**
	 * Controller resource loader
	 */
	@Inject
	public void init() {

		this.alternativeFlow = new Flow();
		this.selected = false;
		this.alternativeStepSelected = false;

		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			loadUseCase();

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Load flow using flowId as request parameter
	 * 
	 * @throws UCMException
	 */
	public void loadFlow() throws UCMException {
		try {
			logger.debug("loadFlow flowId = {}", this.flowId);
			this.alternativeFlow = em.find(Flow.class, this.flowId);

			// resort steps
			Collections.sort(this.alternativeFlow.getFlowSteps(), FLOWSTEP_ORDER);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Save action
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save() throws UCMException {
		try {
			em.persist(this.alternativeFlow);
			logger.info("Saved Alternative Flow {}", this.alternativeFlow.getName());
			this.facesContextMessage.infoMessage("{0} saved successfully", this.alternativeFlow.getName());
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Remove alternative flow
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			// remove any parent test classes
			List<TestCase> testCases = findTestCases(this.alternativeFlow);
			Iterator<TestCase> testCaseIter = testCases.iterator();
			while (testCaseIter.hasNext()) {
				em.remove(testCaseIter.next());
			}

			this.useCase.removeAlternativeFlow(this.alternativeFlow);
			em.remove(this.alternativeFlow);

			logger.info("Deleted Alternative Flow {}", this.alternativeFlow.getName());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.alternativeFlow.getName());
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
	 * save alternative flow action
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void saveAlternativeFlow() throws UCMException {
		try {
			if (validate()) {

				if (this.alternativeFlowStep.getId() == null) {
					this.alternativeFlowStep.setModifiedBy(authUser.getUsername());
					this.alternativeFlow.addFlowStep(this.alternativeFlowStep);
				}

				em.persist(this.alternativeFlow);
				logger.info("Saved {}", this.alternativeFlow.getName());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.alternativeFlow.getName());

				// resort steps
				Collections.sort(this.alternativeFlow.getFlowSteps(), FLOWSTEP_ORDER);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Add alternative flow step
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addFlowStep() throws UCMException {
		try {
			Integer stepNumber = this.alternativeFlow.getFlowSteps().size() + 1;

			this.alternativeFlowStep = new FlowStep(stepNumber.shortValue());

			if (stepNumber.shortValue() % 2 == 0) {
				// test for previous actor
			}
			else {
				// system is actor for odd numbered steps
				Actor system = this.projectService.findSystemActor(this.project);
				this.alternativeFlowStep.setActor(system);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Insert an alternative flow step
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void insertFlowStep() throws UCMException {
		try {
			logger.debug("inserting flow step before: {}", this.alternativeFlowStep.getStepNumber());
			this.alternativeFlowStep = new FlowStep(this.alternativeFlowStep.getStepNumber());

			// renumber steps
			short startStep = this.alternativeFlowStep.getStepNumber();

			// increment start step
			startStep++;

			// renumber from this starting step
			renumberFlowSteps(startStep);

			// resort steps
			Collections.sort(this.alternativeFlow.getFlowSteps(), FLOWSTEP_ORDER);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Action: remove alternative flow step
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeFlowStep() throws UCMException {
		try {
			this.alternativeFlow.removeFlowStep(this.alternativeFlowStep);
			em.remove(this.alternativeFlowStep);
			logger.info("Deleted Step {}", this.alternativeFlowStep.getStepNumber());
			this.facesContextMessage.infoMessage("{0} step deleted successfully", this.alternativeFlowStep.getStepNumber());

			// renumber steps
			renumberAllFlowSteps();

			// persist renumbering changes
			em.persist(this.useCase);

			this.alternativeStepSelected = false;
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * @return the alternativeStepSelected
	 */
	public boolean isAlternativeStepSelected() {
		return alternativeStepSelected;
	}

	/**
	 * @param alternativeStepSelected
	 *            the alternativeStepSelected to set
	 */
	public void setAlternativeStepSelected(boolean alternativeStepSelected) {
		this.alternativeStepSelected = alternativeStepSelected;
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
	 * Flow Step Row selection event
	 * 
	 * @param event
	 */
	public void onFlowStepRowSelect(SelectEvent event) {
		this.alternativeStepSelected = true;
	}

	/**
	 * @return the alternativeFlowStep
	 */
	public FlowStep getAlternativeFlowStep() {
		return alternativeFlowStep;
	}

	/**
	 * @param alternativeFlowStep
	 *            the alternativeFlowStep to set
	 */
	public void setAlternativeFlowStep(FlowStep alternativeFlowStep) {
		this.alternativeFlowStep = alternativeFlowStep;
	}

	/**
	 * get UseCase
	 * 
	 * @return useCase
	 */
	public UseCase getUseCase() {
		return useCase;
	}

	/**
	 * set UseCase
	 * 
	 * @param useCase
	 */
	public void setUseCase(UseCase useCase) {
		this.useCase = useCase;
	}

	/**
	 * @return the alternativeFlow
	 */
	public Flow getAlternativeFlow() {
		return alternativeFlow;
	}

	/**
	 * @param alternativeFlow
	 *            the alternativeFlow to set
	 */
	public void setAlternativeFlow(Flow alternativeFlow) {
		this.alternativeFlow = alternativeFlow;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
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
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @return the flowId
	 */
	public Long getFlowId() {
		return flowId;
	}

	/**
	 * @param flowId
	 *            the flowId to set
	 */
	public void setFlowId(Long flowId) {
		this.flowId = flowId;
	}

	/**
	 * Validate alternative flow
	 * <ul>
	 * <li></li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;

		return isvalid;
	}

	/**
	 * load resources
	 */
	private void loadUseCase() {
		this.useCase = em.find(UseCase.class, this.id);
		this.project = this.useCase.getProject();
	}

	/**
	 * helper method renumbers alternative flow steps
	 * 
	 * @param starting
	 *            step
	 */
	private void renumberFlowSteps(final short startStep) {
		logger.debug("startStep = {}", startStep);
		short stepNumber = startStep;
		for (int i = stepNumber - 2; i < this.alternativeFlow.getFlowSteps().size(); i++) {
			FlowStep flowStep = this.alternativeFlow.getFlowSteps().get(i);
			flowStep.setStepNumber(stepNumber);
			logger.debug("renumbered {}", flowStep);
			stepNumber++;
		}
	}

	/**
	 * Helper method to renumber all alternative flow steps
	 */
	private void renumberAllFlowSteps() {
		for (int i = 0; i < this.alternativeFlow.getFlowSteps().size(); i++) {
			FlowStep flowStep = this.alternativeFlow.getFlowSteps().get(i);
			flowStep.setStepNumber((short) (i + 1));
			logger.debug("renumbered {}", flowStep);
		}
	}

	/**
	 * Find testcases associated with this alternative flow
	 * <p>
	 * Return all testcases where flow name is contained in the testcase name.
	 * 
	 * @param alternativeFlow
	 * @return List of TestCase
	 */
	private List<TestCase> findTestCases(Flow alternativeFlow) {
		List<TestCase> testCases = new ArrayList<TestCase>();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TestCase> c = cb.createQuery(TestCase.class);
		Root<TestCase> obj = c.from(TestCase.class);
		c.select(obj);
		List<TestCase> list = em.createQuery(c).getResultList();

		for (TestCase testCase : list) {

			if (testCase.getName().indexOf(alternativeFlow.getName()) != -1) {
				testCases.add(testCase);
			}
		}

		return testCases;
	}

}