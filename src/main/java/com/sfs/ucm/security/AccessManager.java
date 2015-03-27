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
package com.sfs.ucm.security;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.util.Authenticated;

/**
 * Access Security Manager
 * 
 * @author lbbishop
 */
@RequestScoped
@Named("accessManager")
public class AccessManager implements Serializable {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	public void init() {

	}

	/**
	 * Check permission
	 * 
	 * @param roleName
	 * @param action
	 * @param project
	 * @return permission flag
	 */
	public boolean hasPermission(String roleName, String action, Project project) {

		boolean hasPermission = false;

		if ("projectMember".equals(roleName) && "Edit".equals(action)) {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProjectMember> c = cb.createQuery(ProjectMember.class);
			Root<ProjectMember> obj = c.from(ProjectMember.class);
			c.select(obj).where(cb.equal(obj.get("project"), project), cb.equal(obj.get("authUser"), this.authUser),cb.notEqual(obj.get("primaryRole"), Literal.MEMBERROLE_GUEST.toString()));
			List<ProjectMember> list = em.createQuery(c).getResultList();

			hasPermission = (list.size() > 0);
		}
		return hasPermission;
	}

}