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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.Actor;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.EnvironmentalFactors;
import com.sfs.ucm.model.Feature;
import com.sfs.ucm.model.ProductVision;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.Requirement;
import com.sfs.ucm.model.StakeholderRequest;
import com.sfs.ucm.model.TechnicalFactors;
import com.sfs.ucm.model.TestPlan;
import com.sfs.ucm.model.TestSet;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Project Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("projectAction")
public class ProjectAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private Project project;

	private List<Project> projects;

	private boolean selected;

	/**
	 * initialize method
	 */
	@Inject
	public void init() {
		this.project = new Project();
		this.selected = false;

		begin();

		// log assigned authUser roles TODO fix this
		// String authUserRole = identity.hasRole(authUser.getUsername(), UserRoleType.User.toString(), Literal.GROUPTYPE_SECURITY_LEVEL.toString()) ? "Y" :
		// "N";
		// String projectAdminRole = identity.hasRole(authUser.getUsername(), UserRoleType.ProjectAdmin.toString(), Literal.GROUPTYPE_SECURITY_LEVEL.toString())
		// ? "Y" : "N";
		// String adminRole = identity.hasRole(authUser.getUsername(), UserRoleType.Admin.toString(), Literal.GROUPTYPE_SECURITY_LEVEL.toString()) ? "Y" : "N";
		// logger.info("User {}: Roles = [User]{}, [ProjectAdmin]{}, [Admin]{}", identity.getUser(), authUserRole, projectAdminRole, adminRole);
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			loadList();
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Add action
	 */
	public void add() {
		this.project = new Project();

		// set the project vision
		this.project.setProductVision(new ProductVision());

		// set tech factors
		this.project.setTechnicalFactors(new TechnicalFactors());

		// set environmental factors
		this.project.setEnvironmentalFactors(new EnvironmentalFactors());

		// set the test plan
		this.project.setTestPlan(new TestPlan("Project Test Plan"));

		// add default System Actor
		this.project.addActor(new Actor(0, "System", Actor.SIMPLE, "Responds to Actor Actions"));
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
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			logger.info(this.project.toString());
			for (UseCase uc : this.project.getUseCases()) {
				logger.info(uc.toString());
			}

			// remove dependent objects in proper sequence
			Iterator<UseCase> useCaseIter = this.project.getUseCases().iterator();
			while (useCaseIter.hasNext()) {
				em.remove(useCaseIter.next());
			}

			Iterator<Requirement> requirementIter = this.project.getRequirements().iterator();
			while (requirementIter.hasNext()) {
				em.remove(requirementIter.next());
			}

			Iterator<Feature> featureIter = this.project.getFeatures().iterator();
			while (featureIter.hasNext()) {
				em.remove(featureIter.next());
			}

			Iterator<StakeholderRequest> stakeholderRequestIter = this.project.getStakeholderRequests().iterator();
			while (stakeholderRequestIter.hasNext()) {
				em.remove(stakeholderRequestIter.next());
			}

			// test sets
			Iterator<TestSet> testSetIter = this.project.getTestPlan().getTestSets().iterator();
			while (testSetIter.hasNext()) {
				em.remove(testSetIter.next());
			}

			// finally remove the project
			em.remove(this.project);

			this.facesContextMessage.infoMessage("{0} deleted successfully", this.project.getName());
			projectEvent.fire(this.project);
			this.projects = em.createQuery("from Project", Project.class).getResultList();

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
				this.project.setModifiedBy(authUser.getUsername());
				if (this.project.getId() == null) {
					this.projects.add(this.project);
				}
				em.persist(this.project);

				logger.info("saved {}", this.project.getName());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.project.getName());
				this.projects = em.createQuery("from Project", Project.class).getResultList();

				this.selected = true;

				// raise event
				projectEvent.fire(project);
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
	 * Projects producer
	 * 
	 * @return List
	 */
	public List<Project> getProjects() {
		return this.projects;
	}

	/**
	 * get Project
	 * 
	 * @return project
	 */
	public Project getProject() {
		return this.project;
	}

	/**
	 * set Project
	 * 
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
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
	 * Validate project
	 * <ul>
	 * <li>If new project check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.project.getId() == null) {
			if (this.projects.contains(this.project)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.project.getName());
				logger.error("{} already exists", this.project.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);

			}
		}
		return isvalid;
	}

	/**
	 * load projects
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> c = cb.createQuery(Project.class);
		Root<Project> obj = c.from(Project.class);
		c.select(obj).orderBy(cb.asc(obj.get("name")));
		this.projects = em.createQuery(c).getResultList();
	}

}