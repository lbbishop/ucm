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

import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.util.ProjectUserInit;
import com.sfs.ucm.util.ProjectUserUpdated;

@Stateful
@SessionScoped
public class ProjectUserProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@PersistenceContext
	private EntityManager em;

	private List<AuthUser> projectUsers;

	private Project project;

	@Inject
	public void init() {
	}

	/**
	 * project member update event
	 * 
	 * @param project
	 */
	public void onProjectUserInit(@Observes @ProjectUserInit final Project project) {
		if (this.projectUsers == null || !project.equals(this.project)) {
			load(project);
		}
		this.project = project;
	}

	/**
	 * project member update event
	 * 
	 * @param project
	 */
	public void onProjectUserUpdate(@Observes @ProjectUserUpdated final Project project) {
		load(project);
	}

	/**
	 * Producer
	 * 
	 * @return List of project users
	 */
	@Produces
	@Named
	public List<AuthUser> getProjectUsers() {
		return this.projectUsers;
	}

	/**
	 * Load resources
	 * 
	 * @param project
	 */
	private void load(final Project project) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AuthUser> c = cb.createQuery(AuthUser.class);
		Root<ProjectMember> obj = c.from(ProjectMember.class);
		c.select(obj.<AuthUser> get("authUser")).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("authUser").get("name")));
		this.projectUsers = em.createQuery(c).getResultList();
	}
}
