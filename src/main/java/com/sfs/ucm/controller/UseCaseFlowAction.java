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

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.Actor;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Flow;
import com.sfs.ucm.model.FlowStep;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.model.UseCaseRule;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.service.UseCaseService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * UseCaseFlow Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("useCaseFlowAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UseCaseFlowAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	static final Comparator<FlowStep> FLOWSTEP_ORDER = new Comparator<FlowStep>() {
		public int compare(FlowStep f1, FlowStep f2) {
			return f1.getStepNumber().compareTo(f2.getStepNumber());
		}
	};

	private Long id;

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

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	@Service
	private UseCaseService useCaseService;

	private boolean editable;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	private UseCase useCase;

	private UseCaseRule useCaseRule;

	private FlowStep basicFlowStep;

	private Flow basicFlow;

	private Flow alternativeFlow;

	private Flow selectedAlternativeFlow;

	private List<Flow> alternativeFlows;

	private Project project;

	private boolean basicStepSelected;

	/**
	 * Controller resource loader
	 */
	@Inject
	public void init() {

		begin();

		this.basicFlowStep = new FlowStep();
		this.basicStepSelected = false;
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			loadUseCase();

			loadAlternativeFlows();

			// update project actor producer
			this.projectSecurityMarkingSrc.fire(this.project);

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * basic flow steps producer
	 * 
	 * @return List
	 */
	public List<FlowStep> getBasicFlowSteps() {
		return this.useCase.getBasicFlow().getFlowSteps();
	}

	/**
	 * Add basic flow step
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addFlowStep() throws UCMException {
		try {
			Integer stepNumber = this.useCase.getBasicFlow().getFlowSteps().size() + 1;
			this.basicFlowStep = new FlowStep(stepNumber.shortValue());

			if (stepNumber.intValue() % 2 == 0) {
				// test for previous actor
			}
			else {
				// system is actor for odd numbered steps
				Actor system = this.projectService.findSystemActor(this.project);
				this.basicFlowStep.setActor(system);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Insert a basic flow step
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void insertFlowStep() throws UCMException {
		try {
			logger.debug("inserting flow step before: {}", this.basicFlowStep.getStepNumber());
			this.basicFlowStep = new FlowStep(this.basicFlowStep.getStepNumber());

			// renumber steps
			short startStep = this.basicFlowStep.getStepNumber();

			// increment start step
			startStep++;

			// renumber from this starting step
			renumberFlowSteps(startStep);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Action: remove basic flow step
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeFlowStep() throws UCMException {
		try {
			this.basicFlow.removeFlowStep(this.basicFlowStep);
			em.remove(this.basicFlowStep);
			logger.info("Deleted Step {}", this.basicFlowStep.getStepNumber());
			this.facesContextMessage.infoMessage("Step {0} deleted successfully", this.basicFlowStep.getStepNumber());

			// renumber steps
			renumberAllFlowSteps();

			// persist renumbering changes

			// force testcase update
			Timestamp now = new Timestamp(System.currentTimeMillis());
			this.useCase.setModifiedDate(now);

			em.persist(this.useCase);

			this.basicStepSelected = false;
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * save basic flow action
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void saveBasicFlow() throws UCMException {
		try {
			this.basicFlowStep.setModifiedBy(this.authUser.getUsername());
			if (validate()) {
				if (this.basicFlowStep.getId() == null) {
					this.useCase.getBasicFlow().addFlowStep(this.basicFlowStep);
				}

				// force testcase update
				Timestamp now = new Timestamp(System.currentTimeMillis());
				this.useCase.setModifiedDate(now);

				em.persist(this.useCase);
				logger.info("Saved Basic Flow");
				this.facesContextMessage.infoMessage("Basic Flow saved successfully");

				this.basicStepSelected = true;

				// resort steps
				Collections.sort(this.useCase.getBasicFlow().getFlowSteps(), FLOWSTEP_ORDER);

			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Action: add AlternativeFlow
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addAlternativeFlow() {

		logger.info("addAlternativeFlow called");

		// construct alternative setting starting step as the current basic flow step
		this.alternativeFlow = new Flow(this.basicFlowStep.getStepNumber());

	}

	/**
	 * save alternative flow action
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void saveAlternativeFlow() throws UCMException {
		try {
			if (validateAlternativeFlow()) {

				this.alternativeFlow.setModifiedBy(authUser.getUsername());
				if (this.alternativeFlow.getId() == null) {
					this.useCase.addAlternativeFlow(this.alternativeFlow);
				}

				em.persist(this.alternativeFlow);

				// force testcase update
				Timestamp now = new Timestamp(System.currentTimeMillis());
				this.useCase.setModifiedDate(now);

				em.persist(this.useCase);

				logger.info("Saved {}", this.alternativeFlow.getName());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.alternativeFlow.getName());

				// refresh list of alternative flows
				loadAlternativeFlows();
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Handler
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateAlternativeFlowName() throws UCMException {
		try {
			if (this.alternativeFlow.getId() == null) {
				this.useCase.addAlternativeFlow(alternativeFlow);
			}

			logger.info("saving alt flow {}", this.alternativeFlow);

			// force testcase update
			Timestamp now = new Timestamp(System.currentTimeMillis());
			this.useCase.setModifiedDate(now);

			em.persist(this.useCase);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Basic Flow Row selection event
	 * 
	 * @param event
	 */
	public void onBasicFlowRowSelect(SelectEvent event) {
		this.basicStepSelected = true;
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
	 * @return the basicFlow
	 */
	public Flow getBasicFlow() {
		return basicFlow;
	}

	/**
	 * @param basicFlow
	 *            the basicFlow to set
	 */
	public void setBasicFlow(Flow basicFlow) {
		this.basicFlow = basicFlow;
	}

	/**
	 * @return the useCaseRule
	 */
	public UseCaseRule getUseCaseRule() {
		return useCaseRule;
	}

	/**
	 * @param useCaseRule
	 *            the useCaseRule to set
	 */
	public void setUseCaseRule(UseCaseRule useCaseRule) {
		this.useCaseRule = useCaseRule;
	}

	/**
	 * @return the selectedBasicFlowStep
	 */
	public FlowStep getBasicFlowStep() {
		return basicFlowStep;
	}

	/**
	 * @param basicFlowStep
	 *            the basicFlowStep to set
	 */
	public void setBasicFlowStep(FlowStep basicFlowStep) {
		this.basicFlowStep = basicFlowStep;
	}

	/**
	 * @return the basicStepSelected
	 */
	public boolean isBasicStepSelected() {
		return basicStepSelected;
	}

	/**
	 * @param basicStepSelected
	 *            the basicStepSelected to set
	 */
	public void setBasicStepSelected(boolean basicStepSelected) {
		this.basicStepSelected = basicStepSelected;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @return the selectedAlternativeFlow
	 */
	public Flow getSelectedAlternativeFlow() {
		return selectedAlternativeFlow;
	}

	/**
	 * @param selectedAlternativeFlow
	 *            the selectedAlternativeFlow to set
	 */
	public void setSelectedAlternativeFlow(Flow selectedAlternativeFlow) {
		this.selectedAlternativeFlow = selectedAlternativeFlow;
	}

	/**
	 * @return the alternativeFlows
	 */
	public List<Flow> getAlternativeFlows() {
		return alternativeFlows;
	}

	/**
	 * @param alternativeFlows
	 *            the alternativeFlows to set
	 */
	public void setAlternativeFlows(List<Flow> alternativeFlows) {
		this.alternativeFlows = alternativeFlows;
	}

	/**
	 * Validate useCase
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

	// **************** private methods *****************

	/**
	 * Validate Alternative Flow
	 * <ul>
	 * <li>Check for duplicate alternative flow</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validateAlternativeFlow() {
		boolean isvalid = true;

		if (this.alternativeFlow.getId() == null) {
			if (checkForDuplicateAlternateFlow(this.useCase, this.alternativeFlow.getName())) {
				this.facesContextMessage.errorMessage("{0} already exists", StringUtils.abbreviate(this.alternativeFlow.getName(), 25));
				logger.error("{} already exists", this.alternativeFlow.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		// verify alternative flow end step is a valid basic step number
		if (this.alternativeFlow.getEndStep() != null && this.alternativeFlow.getEndStep() != 0) {
			if (this.alternativeFlow.getEndStep().shortValue() < this.alternativeFlow.getStartStep().shortValue()) {
				this.facesContextMessage.errorMessage("EndStep must be greater than or equal to BeginStep");
				logger.error("EndStep must be greater than or equal to BeginStep");
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * check for duplicate alternative flow
	 */
	private boolean checkForDuplicateAlternateFlow(final UseCase useCase, final String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Flow> c = cb.createQuery(Flow.class);
		Root<Flow> obj = c.from(Flow.class);
		c.select(obj).where(cb.equal(obj.<String> get("name"), name), cb.equal(obj.get("useCase"), useCase));
		List<Flow> list = em.createQuery(c).getResultList();
		return (list.size() > 0);
	}

	/**
	 * helper method renumbers basic flow steps
	 * 
	 * @param starting
	 *            step
	 */
	private void renumberFlowSteps(final short startStep) {
		logger.info("startStep = {}", startStep);
		short stepNumber = startStep;
		for (int i = stepNumber - 2; i < this.basicFlow.getFlowSteps().size(); i++) {
			FlowStep flowStep = this.basicFlow.getFlowSteps().get(i);
			flowStep.setStepNumber(stepNumber);
			logger.info("renumbered {}", flowStep);
			stepNumber++;
		}
	}

	/**
	 * helper method to renumber all basic flow steps
	 */
	private void renumberAllFlowSteps() {
		for (int i = 0; i < this.basicFlow.getFlowSteps().size(); i++) {
			FlowStep flowStep = this.basicFlow.getFlowSteps().get(i);
			flowStep.setStepNumber((short) (i + 1));
			logger.info("renumbered {}", flowStep);
		}
	}

	/**
	 * load resources
	 */
	private void loadUseCase() {
		this.useCase = em.find(UseCase.class, this.id);
		this.project = this.useCase.getProject();
		this.basicFlow = this.useCase.getBasicFlow();

		Collections.sort(this.basicFlow.getFlowSteps(), FLOWSTEP_ORDER);
	}

	/**
	 * load alternative flows
	 */
	private void loadAlternativeFlows() {
		this.alternativeFlows = this.useCaseService.findUseCaseAlternativeFlows(this.useCase);
	}

}