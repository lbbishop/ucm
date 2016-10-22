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

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.BusinessRule;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.ProjectUpdated;

/**
 * BusinessRule Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("businessRuleAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class BusinessRulesAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectEvent;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private BusinessRule businessRule;

	private List<BusinessRule> businessRules;

	private Project project;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.businessRule = new BusinessRule();

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
	 * BusinessRules producer
	 * 
	 * @return List
	 */
	public List<BusinessRule> getBusinessRules() {
		return this.businessRules;
	}

	/**
	 * get BusinessRule
	 * 
	 * @return businessRule
	 */
	public BusinessRule getBusinessRule() {
		return businessRule;
	}

	/**
	 * set BusinessRule
	 * 
	 * @param businessRule
	 */
	public void setBusinessRule(BusinessRule businessRule) {
		this.businessRule = businessRule;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Load business rules
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BusinessRule> c = cb.createQuery(BusinessRule.class);
		Root<BusinessRule> obj = c.from(BusinessRule.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.businessRules = em.createQuery(c).getResultList();
	}

}