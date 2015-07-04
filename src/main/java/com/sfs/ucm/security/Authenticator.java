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

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.service.AuthUserService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

@Named
@RequestScoped
public class Authenticator implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private FacesContextMessage facesContextMessage;

	@Inject
	@Service
	private AuthUserService authUserService;

	@Inject
	@Authenticated
	private Event<AuthUser> authUserSrc;

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
		if (request.getSession().getAttribute("AuthUser") == null) {
			try {

				// Retrieve the Principal
				Principal principal = request.getUserPrincipal();
				logger.info("Remote user {}", principal.getName());

				// configure the user
				configure(request);

				// Display a message based on the User role
				if (request.isUserInRole("Admin")) {
					logger.info("Welcome {} (Administrator)", principal.getName());
				}

				outcome = Literal.NAV_SUCCESS.toString();

				return outcome;
			}
			catch (Exception e) {
				logger.info("Authenticator error occurred: {}", e.getMessage());
				this.facesContextMessage.fatalMessage("An Error Occurred: Login failed");
				outcome = Literal.NAV_FAILURE.toString();
			}
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
	private void configure(HttpServletRequest request) {

		// find or persist user
		String username = request.getRemoteUser();
		AuthUser authUser = this.authUserService.findUserByName(username);

		if (authUser == null) {

			authUser = new AuthUser(username, username, username);
			this.authUserService.store(authUser);

			// place authUser in session attribute AuthUser
			request.getSession().setAttribute("AuthUser", authUser);

			// fire update events
			authUserSrc.fire(authUser);
		}

	}
}
