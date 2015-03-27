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
import com.sfs.ucm.model.RequirementRule;
import com.sfs.ucm.model.RequirementRuleTest;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Requirement Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("requirementRuleAction")
public class RequirementRuleAction extends ActionBase implements Serializable {

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
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private RequirementRule requirementRule;

	private List<RequirementRule> requirementRules;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.requirementRule = new RequirementRule();
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
			this.project = em.find(Project.class, id);

			loadList();

			// update producers
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
			this.requirementRule.setModifiedBy(authUser.getUsername());

			em.persist(this.requirementRule);
			logger.info("Saved {}", this.requirementRule.getArtifact());
			this.facesContextMessage.infoMessage("{0} saved successfully", this.requirementRule.getArtifact());
			this.selected = true;
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
	 * @return the requirementRules
	 */
	public List<RequirementRule> getRequirementRules() {
		return requirementRules;
	}

	/**
	 * @param requirementRules
	 *            the requirementRules to set
	 */
	public void setRequirementRules(List<RequirementRule> requirementRules) {
		this.requirementRules = requirementRules;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			// remove any parent test classes
			List<RequirementRuleTest> list = findRequirementRuleTests(this.requirementRule);
			Iterator<RequirementRuleTest> iter = list.iterator();
			while (iter.hasNext()) {
				em.remove(iter.next());
			}

			this.requirementRule.getRequirement().removeRequirementRule(this.requirementRule);
			em.remove(this.requirementRule);
			logger.info("deleted {}", this.requirementRule.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.requirementRule.getArtifact());

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
	 * @return the requirementRule
	 */
	public RequirementRule getRequirementRule() {
		return requirementRule;
	}

	/**
	 * @param requirementRule
	 *            the requirementRule to set
	 */
	public void setRequirementRule(RequirementRule requirementRule) {
		this.requirementRule = requirementRule;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
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
	 * load requirement rules
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RequirementRule> c = cb.createQuery(RequirementRule.class);
		Root<RequirementRule> obj = c.from(RequirementRule.class);
		c.select(obj).where(cb.equal(obj.get("requirement").get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.requirementRules = em.createQuery(c).getResultList();
	}

	/**
	 * find tests associated with this artifact
	 */
	private List<RequirementRuleTest> findRequirementRuleTests(RequirementRule requirementRule) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RequirementRuleTest> c = cb.createQuery(RequirementRuleTest.class);
		Root<RequirementRuleTest> obj = c.from(RequirementRuleTest.class);
		c.select(obj).where(cb.equal(obj.get("requirementRule"), requirementRule));
		return em.createQuery(c).getResultList();
	}

}