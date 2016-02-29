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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.indexes.IndexReaderAccessor;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;

import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.service.SearchService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Search Actions
 * 
 * @author lbbishop
 */
@SessionScoped
@Named("searchAction")
public class SearchAction implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int MAX_RESULTS = 20;

	@Inject
	private FacesContextMessage facesContextMessage;

	@Inject
	private EntityManager em;

	@Inject
	@Service
	private SearchService searchService;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private String searchField;

	private List<Project> projects;

	@Inject
	public void init() {
	}

	/**
	 * Auto-complete analyzing suggester
	 * 
	 * @param searchString
	 * @return List of suggested search terms found
	 * @throws UCMException
	 */
	public List<String> suggester(final String searchString) throws UCMException {
		Set<String> suggestionSet = new HashSet<String>();
		IndexReaderAccessor ira = null;
		IndexReader reader = null;
		try {
			// create native Lucene query using the query DSL
			FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
			SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();
			ira = searchFactory.getIndexReaderAccessor();
			reader = ira.open(Project.class);
			IndexSearcher searcher = new IndexSearcher(reader);

			Analyzer analyzer = new StandardAnalyzer();

			suggestionSet.addAll(this.searchService.findSuggestionTokens(searcher, "name", searchString, analyzer));
			suggestionSet.addAll(this.searchService.findSuggestionTokens(searcher, "description", searchString, analyzer));

			// searcher.close();

		}
		catch (Exception e) {
			throw new UCMException(e);
		}
		finally {
			ira.close(reader);
		}

		return new ArrayList<String>(suggestionSet);
	}

	/**
	 * Full text search
	 * 
	 * @throws UCMException
	 */
	@SuppressWarnings("unchecked")
	public void fullTextSearch() throws UCMException {
		try {

			if (StringUtils.isNotEmpty(this.searchField)) {
				FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

				// create native Lucene query using the query DSL
				QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Project.class).get();

				Query query = qb.keyword().onFields("name", "description").matching(this.searchField).createQuery();

				// wrap Lucene query in a javax.persistence.Query
				javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query, Project.class);

				// execute search
				this.projects = persistenceQuery.setMaxResults(MAX_RESULTS).getResultList();
				logger.info("fullTextSearch: search field:[{}] results size {}", this.searchField, this.projects.size());

			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * @return the searchField
	 */
	public String getSearchField() {
		return searchField;
	}

	/**
	 * @param searchField
	 *            the searchField to set
	 */
	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

}