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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;

@Stateless
@Named("searchManager")
public class SearchManager implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager em;

	@Inject
	private Logger logger;


	@Inject
	public void init() {

	}

	/**
	 * Rebuild search index
	 */
	public void rebuildIndex() {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
		
		try {
			fullTextEntityManager.createIndexer().startAndWait();
		}
		catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
}
