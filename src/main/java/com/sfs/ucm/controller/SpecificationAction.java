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
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Feature;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.Requirement;
import com.sfs.ucm.model.RequirementRule;
import com.sfs.ucm.model.RequirementRuleTest;
import com.sfs.ucm.model.RequirementTest;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUserInit;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Supplementary Specification Requirement Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("specificationAction")
public class SpecificationAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	
	@Inject
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	@ProjectUserInit
	private Event<Project> projectUserSrc;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	private Requirement requirement;

	private RequirementRule requirementRule;

	private List<Requirement> requirements;
	
	private List<Feature> features;

	private Project project;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.requirement = new Requirement();
		this.requirementRule = new RequirementRule();
		this.selected = false;
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			// begin work unit
			begin();
			
			this.project = em.find(Project.class, id);

			loadList();
			loadFeatures(this.project);

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
	 * 
	 * @return outcome
	 */
	public void add() {
		this.requirement = new Requirement(ModelUtils.getNextIdentifier(this.requirements));
	}

	/**
	 * Add business rule action
	 * 
	 * @return outcome
	 */
	public void addBusinessRule() {
		Long cnt = getRequirementRuleCount();
		this.requirementRule = new RequirementRule(cnt.intValue() + 1);
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
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			// remove any requirement rule tests
			for (RequirementRule requirementRule : this.requirement.getRequirementRules()) {
				List<RequirementRuleTest> requirementRuleTests = findRequirementRuleTests(requirementRule);
				Iterator<RequirementRuleTest> requirementRuleTestIter = requirementRuleTests.iterator();
				while (requirementRuleTestIter.hasNext()) {
					em.remove(requirementRuleTestIter.next());
				}
			}

			// remove any parent test classes
			List<RequirementTest> requirementTests = findRequirementTests(this.requirement);
			Iterator<RequirementTest> requirementTestIter = requirementTests.iterator();
			while (requirementTestIter.hasNext()) {
				em.remove(requirementTestIter.next());
			}

			this.project.removeRequirement(this.requirement);
			em.remove(this.requirement);
			logger.info("deleted {}", this.requirement.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.requirement.getArtifact());

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
	 */
	public void save() throws UCMException {
		try {
			if (validate()) {
				this.requirement.setModifiedBy(authUser.getUsername());
				if (this.requirement.getId() == null) {
					this.project.addRequirement(this.requirement);
				}

				em.persist(this.project);
				
				logger.info("saved {}", this.requirement.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.requirement.getArtifact());

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
	 * save business rule action
	 * 
	 * @throws UCMException
	 */
	public void saveBusinessRule() {
		try {
			if (validate()) {
				this.requirementRule.setModifiedBy(authUser.getUsername());
				if (this.requirementRule.getId() == null) {
					this.requirement.addRequirementRule(this.requirementRule);
				}
				em.persist(this.requirement);
				logger.info("Saved {}", this.requirementRule.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.requirementRule.getArtifact());
				this.selected = true;
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * @return the features
	 */
	public List<Feature> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	/**
	 * Requirements producer
	 * 
	 * @return List
	 */
	public List<Requirement> getRequirements() {
		return this.requirements;
	}

	/**
	 * get Requirement
	 * 
	 * @return requirement
	 */
	public Requirement getRequirement() {
		return requirement;
	}

	/**
	 * set Requirement
	 * 
	 * @param requirement
	 */
	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
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
	 * Feature value change
	 * <p>
	 * Add this Requirement to the selected Feature
	 * 
	 * @param e
	 */
	public void featureValueChange(ValueChangeEvent e) {
		Feature feature = (Feature) e.getNewValue();
		feature.addRequirement(this.requirement);
	}

	/**
	 * Validate requirement
	 * <ul>
	 * <li>If new requirement check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.requirement.getId() == null) {
			if (this.requirements.contains(this.requirement)) {
				this.facesContextMessage.errorMessage("{0} already exists", StringUtils.abbreviate(this.requirement.getName(), 25));
				logger.error("{} already exists", this.requirement.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * load requirements
	 */
	private void loadList() throws UCMException {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Requirement> c = cb.createQuery(Requirement.class);
		Root<Requirement> obj = c.from(Requirement.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.requirements = em.createQuery(c).getResultList();

	}

	/**
	 * find tests associated with this artifact
	 */
	private List<RequirementTest> findRequirementTests(Requirement requirement) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RequirementTest> c = cb.createQuery(RequirementTest.class);
		Root<RequirementTest> obj = c.from(RequirementTest.class);
		c.select(obj).where(cb.equal(obj.get("requirement"), requirement));
		return em.createQuery(c).getResultList();
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

	/**
	 * Get count of requirement rules
	 * 
	 * @return count
	 */
	private Long getRequirementRuleCount() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		c.select(cb.count(c.from(RequirementRule.class)));
		return em.createQuery(c).getSingleResult();
	}
	
	/**
	 * Load resources
	 * 
	 * @param project
	 */
	private void loadFeatures(final Project project) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Feature> c = cb.createQuery(Feature.class);
		Root<Feature> obj = c.from(Feature.class);
		c.select(obj);
		c.where(cb.equal(obj.get("project"), project));
		c.orderBy(cb.asc(obj.get("id")));
		this.features = em.createQuery(c).getResultList();
	}

}