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

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthRole;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Administrator Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("adminAction")
public class AdminAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	private AuthUser projectManager;

	@Inject
	public void init() {
		begin();
	}

	/**
	 * close action return outcome
	 */
	public String close() {
		String outcome = Literal.NAV_HOME.toString();
		end();
		return outcome;
	}

	/**
	 * saveProjectManager action
	 * 
	 * @throws UCMException
	 */
	public void onProjectManagerChange() throws UCMException {
		try {
			AuthUser pm = em.find(AuthUser.class, this.projectManager.getId());
			pm.addAuthRole(new AuthRole(this.projectManager.getUsername(), Literal.ROLE_MANAGER.toString(), Literal.ROLEGROUP_MANAGERS.toString()));
			em.persist(pm);

			logger.info("User {} given role of Manager", pm);
			this.facesContextMessage.infoMessage("User {0} given role of Project Manager", pm.getName());
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * @return the projectManager
	 */
	public AuthUser getProjectManager() {
		return projectManager;
	}

	/**
	 * @param projectManager
	 *            the projectManager to set
	 */
	public void setProjectManager(AuthUser projectManager) {
		this.projectManager = projectManager;
	}

}