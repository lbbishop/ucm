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
import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Preference;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.view.FacesContextMessage;

@Stateful
@Named
@RequestScoped
public class Authenticator implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private EntityManager em;

	@Inject
	private FacesContextMessage facesContextMessage;

	@Inject
	@Authenticated
	private Event<AuthUser> loginEventSrc;

	private String username;
	private String password;

	public Authenticator() {

	}

	/**
	 * login
	 * 
	 * @return outcome
	 */
	public String login() {

		String outcome = null;

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		try {

			request.login(username, password);

			// Retrieve the Principal
			Principal principal = request.getUserPrincipal();
			//this.username = "lbbisho";

			// configure the user
			configure();

			// Display a message based on the User role
			String message = null;
			if (request.isUserInRole("Admin")) {
				message = "Welcome " + principal.getName() + " (Administrator)";
			}
			else if (request.isUserInRole("Manager")) {
				message = "Welcome " + principal.getName() + " (Manager)";
			}
			else if (request.isUserInRole("User")) {
				message = "Welcome " + principal.getName() + " (User)";
			}
			else if (request.isUserInRole("Guest")) {
				message = "Welcome " + principal.getName() + " (Guest)";
			}
			logger.info(message);

			outcome = Literal.NAV_SUCCESS.toString();

			return outcome;
		}
		catch (ServletException e) {

			logger.info("Authenticator error occurred: {}", e.getMessage());

			this.facesContextMessage.fatalMessage("An Error Occurred: Login failed");
			outcome = Literal.NAV_FAILURE.toString();

		}

		return outcome;
	}

	/**
	 * logout action
	 */
	public void logout() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (session != null) {
			session.invalidate();
		}
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "/logout");
	}

	/**
	 * configure the authenticated user
	 */
	private void configure() {

		// query user
		List<AuthUser> list = em.createQuery("from AuthUser user left join fetch user.authRoles where user.username = :username", AuthUser.class).setParameter("username", username).getResultList();
		Iterator<AuthUser> iter = list.iterator();
		if (iter.hasNext()) {
			AuthUser user = iter.next();
			user.setLoggedIn(true);

			// store default theme
			if (!user.isPreferenceSet(Literal.PREF_THEME.toString())) {
				user.addPreference(new Preference(Literal.PREF_THEME.toString(), Literal.THEME_DEFAULT.toString()));
				em.persist(user);
			}

			// fire update events
			loginEventSrc.fire(user);
		}

	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
