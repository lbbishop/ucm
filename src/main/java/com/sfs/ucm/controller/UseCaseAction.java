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
import java.util.Iterator;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Feature;
import com.sfs.ucm.model.Flow;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.model.UseCaseRule;
import com.sfs.ucm.model.UseCaseRuleTest;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.service.TestService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProductReleaseInit;
import com.sfs.ucm.util.ProjectActorInit;
import com.sfs.ucm.util.ProjectFeatureInit;
import com.sfs.ucm.util.ProjectPackageInit;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.ProjectUserInit;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * UseCase Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("useCaseAction")
public class UseCaseAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

	@Inject
	@ProjectPackageInit
	private Event<Project> projectPackageSrc;

	@Inject
	@ProjectActorInit
	private Event<Project> projectActorSrc;

	@Inject
	@ProjectFeatureInit
	private Event<Project> projectFeatureSrc;

	@Inject
	@ProjectUserInit
	private Event<Project> projectUserSrc;

	@Inject
	@ProjectSecurityInit
	private Event<Project> projectSecurityMarkingSrc;

	@Inject
	@ProductReleaseInit
	private Event<Project> productReleaseSrc;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	@Service
	private TestService testService;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private UseCase useCase;

	private List<UseCase> useCases;

	private Project project;

	private boolean selected;

	private List<Flow> extendedFlows;

	private boolean renamed;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.useCase = new UseCase();
		this.selected = false;
		this.renamed = false;

		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.project = em.find(Project.class, id);

			// update event listeners
			this.projectPackageSrc.fire(this.project);
			this.projectActorSrc.fire(this.project);
			this.projectUserSrc.fire(this.project);
			this.productReleaseSrc.fire(this.project);
			this.projectFeatureSrc.fire(this.project);
			this.projectSecurityMarkingSrc.fire(this.project);

			loadList();

			// get list of basic flows as candidate extended flows
			this.extendedFlows = findExtendedFlows(this.project);

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);
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
	public void add() {
		this.useCase = new UseCase(ModelUtils.getNextIdentifier(this.useCases));
	}

	/**
	 * use case name change handler
	 * <p>
	 * On change delete associated test case to remove dangling test case
	 * 
	 * @param event
	 */
	public void onNameChange(ValueChangeEvent event) throws UCMException {
		String name = (String) event.getNewValue();
		this.useCase.getBasicFlow().setName(name);

		// use case name has changed so delete associated test case
		this.renamed = true;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			// remove any associated usecase rule tests
			for (UseCaseRule useCaseRule : this.useCase.getUseCaseRules()) {
				List<UseCaseRuleTest> useCaseRuleTests = findUseCaseRuleTests(useCaseRule);
				Iterator<UseCaseRuleTest> useCaseRuleTestIter = useCaseRuleTests.iterator();
				while (useCaseRuleTestIter.hasNext()) {
					em.remove(useCaseRuleTestIter.next());
				}
			}
			
			// remove associated test case
			this.testService.deleteTestCase(this.useCase);

			// remove the artifact
			this.project.removeUseCase(this.useCase);
			em.remove(this.useCase);
			em.flush();

			logger.info("Deleted {}", this.useCase.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.useCase.getArtifact());

			// refresh list
			loadList();

			projectEvent.fire(project);
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
				this.useCase.setModifiedBy(authUser.getUsername());
				if (this.useCase.getId() == null) {
					this.project.addUseCase(this.useCase);
				}

				em.persist(this.project);
				logger.info("saved {}", this.useCase.getArtifact());
				projectEvent.fire(project);
				this.facesContextMessage.infoMessage("{0} saved successfully", this.useCase.getArtifact());
				
				// renamed use case will become stale so remove it now
				if (this.renamed) {
					this.testService.deleteTestCase(this.useCase);
					this.renamed = false;
				}

				// refresh list
				loadList();
				this.selected = false;
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
		this.useCase = (UseCase) event.getObject();
		this.selected = true;
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
	 * UseCases producer
	 * 
	 * @return List
	 */
	public List<UseCase> getUseCases() {
		return this.useCases;
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
	 * @return the extendedFlows
	 */
	public List<Flow> getExtendedFlows() {
		return extendedFlows;
	}

	/**
	 * Feature value change
	 * <p>
	 * Add this useCase to the selected Feature
	 * 
	 * @param e
	 */
	public void featureValueChange(ValueChangeEvent e) {
		Feature feature = (Feature) e.getNewValue();
		feature.addUseCase(this.useCase);
	}

	/**
	 * Validate useCase
	 * <ul>
	 * <li>If new useCase check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.useCase.getId() == null) {
			if (this.useCases.contains(this.useCase)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.useCase.getName());
				logger.error("{} already exists", this.useCase);
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * Load use cases
	 * 
	 * @throws UCMException
	 */
	private void loadList() throws UCMException {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UseCase> c = cb.createQuery(UseCase.class);
		Root<UseCase> obj = c.from(UseCase.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("name")));
		this.useCases = em.createQuery(c).getResultList();
	}

	/**
	 * find tests associated with this artifact
	 */
	private List<UseCaseRuleTest> findUseCaseRuleTests(UseCaseRule useCaseRule) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UseCaseRuleTest> c = cb.createQuery(UseCaseRuleTest.class);
		Root<UseCaseRuleTest> obj = c.from(UseCaseRuleTest.class);
		c.select(obj).where(cb.equal(obj.get("useCaseRule"), useCaseRule));
		return em.createQuery(c).getResultList();
	}

	/**
	 * Find Basic Flows to be used as available Extended Flows
	 * 
	 * @param project
	 *            the current project
	 * @return List of Basic Flows
	 */
	private List<Flow> findExtendedFlows(final Project project) {
		List<Flow> list = null;

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Flow> c = cb.createQuery(Flow.class);
		Root<Flow> obj = c.from(Flow.class);
		Path<Boolean> basicFlow = obj.get("basicFlowFlag");
		c.select(obj).where(cb.isTrue(basicFlow)).orderBy(cb.asc(obj.get("name")));
		list = em.createQuery(c).getResultList();

		return list;
	}

}