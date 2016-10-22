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
import com.sfs.ucm.model.Stakeholder;
import com.sfs.ucm.model.SurvivalTest;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectStakeholderUpdated;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Stakeholder Actions
 * 
 * @author lbbishop
 * 
 **/
@Stateful
@ConversationScoped
@Named("stakeholderAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class StakeholderAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

	@Inject
	@ProjectStakeholderUpdated
	private Event<Project> projectStakeholderSrc;

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

	private Stakeholder stakeholder;

	private List<Stakeholder> stakeholders;

	private Project project;

	private boolean selected;

	private SurvivalTest survivalTest;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
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

			// update events
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
	 * Add action
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.stakeholder = new Stakeholder(ModelUtils.getNextIdentifier(this.stakeholders));
	}

	/**
	 * initialize survival test
	 */
	public void initSurvivalTest() {
		this.survivalTest = new SurvivalTest();
	}

	/**
	 * save survival test
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void saveSurvivalTest() {
		this.stakeholder.setSurvivalTest(this.survivalTest);
	}

	/**
	 * @return the survivalTest
	 */
	public SurvivalTest getSurvivalTest() {
		return survivalTest;
	}

	/**
	 * @param survivalTest
	 *            the survivalTest to set
	 */
	public void setSurvivalTest(SurvivalTest survivalTest) {
		this.survivalTest = survivalTest;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			this.project.removeStakeholder(this.stakeholder);
			em.remove(this.stakeholder);
			logger.info("deleted {}", this.stakeholder.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.stakeholder.getArtifact());

			// refresh list
			loadList();

			// update producers
			this.projectEvent.fire(this.project);
			this.projectStakeholderSrc.fire(this.project);

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
				this.stakeholder.setModifiedBy(authUser.getUsername());
				if (this.stakeholder.getId() == null) {
					this.project.addStakeholder(this.stakeholder);
				}

				em.persist(this.project);
				projectEvent.fire(project);

				this.facesContextMessage.infoMessage("{0} saved successfully", this.stakeholder.getArtifact());
				logger.info("saved: {}", this.stakeholder.getArtifact());

				this.selected = true;
				loadList();
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
	 * Stakeholders producer
	 * 
	 * @return List
	 */
	public List<Stakeholder> getStakeholders() {
		return this.stakeholders;
	}

	/**
	 * get Stakeholder
	 * 
	 * @return stakeholder
	 */
	public Stakeholder getStakeholder() {
		return stakeholder;
	}

	/**
	 * set Stakeholder
	 * 
	 * @param stakeholder
	 */
	public void setStakeholder(Stakeholder stakeholder) {
		this.stakeholder = stakeholder;
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
	 * Validate stakeholder
	 * <ul>
	 * <li>If new stakeholder check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.stakeholder.getId() == null) {
			if (this.stakeholders.contains(this.stakeholder)) {
				this.facesContextMessage.infoMessage("{0} already exists", this.stakeholder.getRole());
				logger.error("{} already exists", this.stakeholder);
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * load stakeholders
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Stakeholder> c = cb.createQuery(Stakeholder.class);
		Root<Stakeholder> obj = c.from(Stakeholder.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.stakeholders = em.createQuery(c).getResultList();
	}

}