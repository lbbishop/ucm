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
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthRole;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Preference;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.UserUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * User Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("userAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UserAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authenticatedUser;

	@Inject
	@UserUpdated
	private Event<AuthUser> authUserSrc;

	private AuthUser authUser;

	private List<AuthUser> authUsers;

	private boolean selected;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.authUser = new AuthUser();
		this.selected = false;

		begin();

	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			loadList();
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Close action
	 * <p>
	 * End work unit and navigate home
	 * 
	 * @return outcome
	 */
	public String close() {
		String outcome = Literal.NAV_HOME.toString();
		end();
		return outcome;
	}

	/**
	 * Add action
	 * 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.authUser = new AuthUser(ModelUtils.getNextIdentifier(this.authUsers));

		// set default user preferences
		this.authUser.addPreference(new Preference(Literal.PREF_DISPLAYTOOLTIPS.toString(), true));

	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			em.remove(this.authUser);
			logger.info("deleted {}", this.authUser.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.authUser.getArtifact());

			// refresh list
			loadList();
			this.selected = false;

			// fire change event
			this.authUserSrc.fire(this.authUser);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * save action
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save() throws UCMException {
		try {
			if (validate()) {
				this.authUser.setModifiedBy(this.authenticatedUser.getUsername());
				if (this.authUser.getId() == null) {
					this.authUser.addAuthRole(new AuthRole(this.authUser.getUsername(), Literal.ROLE_USER.toString(), Literal.ROLEGROUP_USERS.toString()));
					this.authUsers.add(this.authUser);
				}

				em.persist(this.authUser);
				logger.info("saved {}", this.authUser.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.authUser.getArtifact());

				// refresh list
				loadList();

				// fire change event
				this.authUserSrc.fire(this.authUser);

				this.selected = true;
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		this.selected = true;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Users producer
	 * 
	 * @return List
	 */
	public List<AuthUser> getAuthUsers() {
		return this.authUsers;
	}

	/**
	 * @param authUsers
	 *            the authUsers to set
	 */
	public void setAuthUsers(List<AuthUser> authUsers) {
		this.authUsers = authUsers;
	}

	/**
	 * get User
	 * 
	 * @return authUser
	 */
	public AuthUser getAuthUser() {
		return authUser;
	}

	/**
	 * set User
	 * 
	 * @param authUser
	 */
	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}

	/**
	 * Validate authUser
	 * <ul>
	 * <li>If new authUser check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.authUser.getId() == null) {
			for (AuthUser theUser : this.authUsers) {
				if (this.authUser.getName().equals(theUser.getName())) {
					this.facesContextMessage.errorMessage("{0} already exists", this.authUser.getName());
					logger.error("validate: authUser {} already exists", this.authUser);
					isvalid = false;
					break;
				}
			}
		}

		return isvalid;
	}

	/**
	 * load authUsers
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AuthUser> c = cb.createQuery(AuthUser.class);
		Root<AuthUser> obj = c.from(AuthUser.class);
		c.select(obj).orderBy(cb.asc(obj.get("name")));
		this.authUsers = em.createQuery(c).getResultList();
	}

}