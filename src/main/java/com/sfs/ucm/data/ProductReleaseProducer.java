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

import com.sfs.ucm.model.ProductRelease;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.util.ProductReleaseInit;
import com.sfs.ucm.util.ProductReleaseUpdated;

/**
 * Project Product Release Producer
 * 
 * @author lbbishop
 * 
 */
@Stateful
@SessionScoped
public class ProductReleaseProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@PersistenceContext
	private EntityManager em;

	private List<ProductRelease> productReleases;

	private Project project;

	@Inject
	public void init() {

	}

	/**
	 * productRelease init event
	 * 
	 * @param project
	 */
	public void onProductReleaseInit(@Observes @ProductReleaseInit final Project project) {

		if (this.productReleases == null || !project.equals(this.project)) {
			load(project);
		}

		this.project = project;
	}

	/**
	 * productRelease update event
	 * 
	 * @param project
	 */
	public void onProductReleaseUpdate(@Observes @ProductReleaseUpdated final Project project) {
		load(project);
	}

	/**
	 * Producer
	 * 
	 * @return List of productReleases
	 */
	@Produces
	@Named
	public List<ProductRelease> getProductReleases() {
		return this.productReleases;
	}

	/**
	 * Load resources
	 * 
	 * @param project
	 */
	private void load(final Project project) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductRelease> c = cb.createQuery(ProductRelease.class);
		Root<ProductRelease> obj = c.from(ProductRelease.class);
		c.select(obj).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("version")));
		this.productReleases = em.createQuery(c).getResultList();
	}
}
