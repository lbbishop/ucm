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
import java.util.Set;

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

import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Feature;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectFeatureInit;
import com.sfs.ucm.util.ProjectFeatureUpdated;
import com.sfs.ucm.util.Service;

@Stateful
@SessionScoped
public class ProjectFeatureProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager em;

	private List<Feature> features;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private Project project;

	@Inject
	public void init() {
	}

	/**
	 * project feature update event
	 * 
	 * @param project
	 */
	public void onProjectFeatureInit(@Observes @ProjectFeatureInit final Project project) {
		if (this.features == null || !project.equals(this.project)) {
			load(project);
		}
		this.project = project;
	}

	/**
	 * project feature update event
	 * 
	 * @param project
	 */
	public void onProjectFeatureUpdate(@Observes @ProjectFeatureUpdated final Project project) {
		load(project);
	}

	/**
	 * Producer
	 * 
	 * @return List of project features
	 */
	@Produces
	@Named
	public List<Feature> getProjectFeatures() {
		return this.features;
	}

	/**
	 * Load resources
	 * 
	 * @param project
	 */
	private void load(final Project project) {

		Set<String> versions = this.projectService.findActiveProductReleaseVersions(this.authUser, project);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Feature> c = cb.createQuery(Feature.class);
		Root<Feature> obj = c.from(Feature.class);
		c.select(obj);
		c.where(cb.equal(obj.get("project"), project), obj.get("productRelease").get("version").in(versions));
		c.orderBy(cb.asc(obj.get("id")));
		this.features = em.createQuery(c).getResultList();
	}

}
