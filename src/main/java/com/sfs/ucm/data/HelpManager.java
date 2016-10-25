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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.sfs.ucm.model.Help;
import com.sfs.ucm.util.HelpUpdated;

/**
 * Help Content manager
 * 
 * @author lbbisho
 * 
 */
@ApplicationScoped
@Named("helpManager")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class HelpManager implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private Logger logger;

	private Map<String, String> helpMap = null;

	@Inject
	public void init() {
		this.helpMap = new HashMap<String, String>();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Help> c = cb.createQuery(Help.class);
		Root<Help> obj = c.from(Help.class);
		c.select(obj).orderBy(cb.asc(obj.get("keyword")));
		List<Help> list = em.createQuery(c).getResultList();
		if (logger.isDebugEnabled()) {
			logger.debug("Loading {} Help entries", list.size());
		}
		for (Help help : list) {
			this.helpMap.put(help.getKeyword(), help.getContent());
		}
	}

	/**
	 * Help change observer
	 * 
	 * @param help
	 */
	public void onHelpContentChange(@Observes @HelpUpdated final Help help) {
		logger.info("updating Help keyword {}", help.getKeyword());
		this.helpMap.put(help.getKeyword(), help.getContent());
	}

	/**
	 * Retrieve help using key
	 * 
	 * @param key
	 * @return value
	 */
	public String getHelp(String keyword) {
		String value = "";

		if (this.helpMap.containsKey(keyword)) {
			value = this.helpMap.get(keyword);
		}
		return value;
	}

}