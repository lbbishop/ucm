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
package com.sfs.ucm.data;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.Stakeholder;
import com.sfs.ucm.util.ProjectStakeholderInit;
import com.sfs.ucm.util.ProjectStakeholderUpdated;

@Stateful
@SessionScoped
public class ProjectStakeholderProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@PersistenceContext
	private EntityManager em;

	private List<Stakeholder> stakeholders;

	private Project project;

	@Inject
	public void init() {

	}

	/**
	 * project stakeholder update event
	 * 
	 * @param project
	 */
	public void onProjectStakeholderInit(@Observes @ProjectStakeholderInit final Project project) {
		if (this.stakeholders == null || !project.equals(this.project)) {
			load(project);
		}
		this.project = project;
	}

	/**
	 * project stakeholder update event
	 * 
	 * @param project
	 */
	public void onProjectStakeholderUpdate(@Observes @ProjectStakeholderUpdated final Project project) {
		load(project);
	}

	/**
	 * Producer
	 * 
	 * @return List of project stakeholders
	 */
	@Produces
	@Named
	public List<Stakeholder> getProjectStakeholders() {
		return this.stakeholders;
	}

	/**
	 * Load resources
	 * 
	 * @param project
	 */
	private void load(final Project project) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Stakeholder> c = cb.createQuery(Stakeholder.class);
		Root<Stakeholder> obj = c.from(Stakeholder.class);
		c.select(obj).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("id")));
		this.stakeholders = em.createQuery(c).getResultList();
	}
}
