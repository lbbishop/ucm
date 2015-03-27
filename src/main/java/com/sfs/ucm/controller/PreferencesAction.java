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
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * User Preferences Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("preferencesAction")
public class PreferencesAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	protected Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@Authenticated
	private Event<AuthUser> authUserEventSrc;

	private AuthUser user;

	@Inject
	public void init() {
		begin();
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.user = em.find(AuthUser.class, authUser.getId());
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * close action return outcome
	 * 
	 * @throws UCMException
	 */
	public String close() throws UCMException {
		String outcome = Literal.NAV_HOME.toString();
		end();
		return outcome;
	}

	/**
	 * saveTheme action
	 * 
	 * @throws UCMException
	 */
	public void saveTheme() throws UCMException {
		try {
			if (validate()) {
				this.authUser.setModifiedBy(this.user.getUsername());
				em.persist(this.user);
				logger.info("Updated user interface theme for {}", this.user);
				this.facesContextMessage.infoMessage("Updated user interface theme for {0}", this.user.getName());

				// set theme for session authUser
				this.authUser.setTheme(this.user.getTheme());
				authUserEventSrc.fire(this.authUser);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * saveTooltipsDisplayMode action
	 * 
	 * @throws UCMException
	 */
	public void saveTooltipsDisplayMode() throws UCMException {
		try {
			if (validate()) {
				this.authUser.setModifiedBy(this.authUser.getUsername());
				em.persist(this.user);
				logger.info("Updated tooltips display mode for {}", this.user);
				this.facesContextMessage.infoMessage("Updated tooltips display mode for {0}", this.user.getName());

				// set theme for session authUser
				this.authUser.setTooltipDisplayed(this.user.isTooltipDisplayed());
				authUserEventSrc.fire(this.authUser);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Validate
	 * <ul>
	 * <li>If new viewSet check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;

		return isvalid;
	}

	/**
	 * @return the user
	 */
	public AuthUser getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(AuthUser user) {
		this.user = user;
	}

}