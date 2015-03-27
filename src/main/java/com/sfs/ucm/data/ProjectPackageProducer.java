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
import com.sfs.ucm.model.ProjectPackage;
import com.sfs.ucm.util.ProjectPackageInit;
import com.sfs.ucm.util.ProjectPackageUpdated;

@Stateful
@SessionScoped
public class ProjectPackageProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@PersistenceContext
	private EntityManager em;

	private List<ProjectPackage> projectPackages;

	private Project project;

	@Inject
	public void init() {

	}

	/**
	 * project package event
	 * 
	 * @param project
	 */
	public void onProjectPackageInit(@Observes @ProjectPackageInit final Project project) {
		if (projectPackages == null || !project.equals(this.project)) {
			load(project);
		}

		// update the current project
		this.project = project;
	}

	/**
	 * project package event
	 * 
	 * @param project
	 */
	public void onProjectPackageUpdate(@Observes @ProjectPackageUpdated final Project project) {
		load(project);
	}

	/**
	 * Producer
	 * 
	 * @return List of ProjectPackage
	 */
	@Produces
	@Named
	public List<ProjectPackage> getProjectPackages() {
		return this.projectPackages;
	}

	/**
	 * load resources
	 * @param project
	 */
	private void load(Project project) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectPackage> c = cb.createQuery(ProjectPackage.class);
		Root<ProjectPackage> obj = c.from(ProjectPackage.class);
		c.select(obj).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("name")));
		this.projectPackages = em.createQuery(c).getResultList();
	}
}
