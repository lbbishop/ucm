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
import com.sfs.ucm.model.Flow;
import com.sfs.ucm.model.FlowStepRule;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * UseCaseRule Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("useCaseRuleAction")
public class UseCaseRuleAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectEvent;

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

	private FlowStepRule flowStepRule;

	private List<FlowStepRule> flowStepRules;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.flowStepRule = new FlowStepRule();
		this.selected = false;

		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() {
		try {
			this.project = em.find(Project.class, id);

			loadList();

			// update producer
			this.projectSecurityMarkingSrc.fire(this.project);

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
	 * save business rule action
	 * 
	 * @throws UCMException
	 */
	public void save() throws UCMException {
		try {
			this.flowStepRule.setModifiedBy(authUser.getUsername());

			em.persist(this.flowStepRule);
			logger.info("Saved {}", this.flowStepRule.getArtifact());
			this.facesContextMessage.infoMessage("{0} saved successfully", this.flowStepRule.getArtifact());
			this.selected = true;
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {

			this.flowStepRule.getFlowStep().removeFlowStepRule(this.flowStepRule);
			em.remove(this.flowStepRule);
			logger.info("deleted {}", this.flowStepRule.getArtifact());
			this.facesContextMessage.infoMessage("{0} saved successfully", this.flowStepRule.getArtifact());

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
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
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
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Return use case business rules list
	 * 
	 * @return List
	 */
	public List<FlowStepRule> getFlowStepRules() {
		return this.flowStepRules;
	}

	/**
	 * get FlowStepRule
	 * 
	 * @return flowStepRule
	 */
	public FlowStepRule getFlowStepRule() {
		return flowStepRule;
	}

	/**
	 * set FlowStepRule
	 * 
	 * @param flowStepRule
	 */
	public void setFlowStepRule(FlowStepRule flowStepRule) {
		this.flowStepRule = flowStepRule;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Load use cases
	 */
	private void loadList() {

		List<UseCase> useCases = null;
		this.flowStepRules = new ArrayList<FlowStepRule>();

		// add basic flow step rules
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UseCase> c = cb.createQuery(UseCase.class);
			Root<UseCase> obj = c.from(UseCase.class);
			c.select(obj).where(cb.equal(obj.get("project"), this.project));
			useCases = em.createQuery(c).getResultList();
		}

		{
			for (UseCase useCase : useCases) {
				Flow basicFlow = useCase.getBasicFlow();

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<FlowStepRule> c = cb.createQuery(FlowStepRule.class);
				Root<FlowStepRule> obj = c.from(FlowStepRule.class);
				c.select(obj).where(cb.equal(obj.get("flowStep").get("flow"), basicFlow)).orderBy(cb.asc(obj.get("id")));
				this.flowStepRules.addAll(em.createQuery(c).getResultList());
			}
		}

		// add alternative flow step rules
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FlowStepRule> c = cb.createQuery(FlowStepRule.class);
			Root<FlowStepRule> obj = c.from(FlowStepRule.class);
			c.select(obj).where(cb.equal(obj.get("flowStep").get("flow").get("useCase").get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
			this.flowStepRules.addAll(em.createQuery(c).getResultList());
		}
	}

}