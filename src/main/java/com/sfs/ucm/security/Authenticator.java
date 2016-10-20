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

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

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

	public void login() {

		try {
			// if remote user is not null then process
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			String principal = context.getRemoteUser();
			logger.info("Incoming principal: {} ", principal);
			if (StringUtils.isNotBlank(principal)) {

				// get http request object
				HttpServletRequest request = (HttpServletRequest) context.getRequest();

				// configure the user
				AuthUser authUser = configure(principal, request);

				// raise update event
				this.authUserSrc.fire(authUser);
			}
		}
		catch (Exception e) {
			String msg = "Login Failure: " + e.getMessage();
			logger.error(msg);
			facesContextMessage.fatalMessage(null, msg);
		}
	}

	/**
	 * logout action
	 */
	public void logout() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (session != null) {
			session.setAttribute("USERID", null);
			session.invalidate();
		}
	}

	/**
	 * configure the authenticated user
	 * 
	 * @return AuthUser
	 */
	private AuthUser configure(final String username, final HttpServletRequest request) {

		// find or persist user
		AuthUser authUser = this.authUserService.findUserByName(username);

		if (authUser == null) {

			authUser = new AuthUser(username, username, username);
			this.authUserService.store(authUser);

		}

		// put userid in session scope context
		request.getSession().setAttribute("USERID", username);

		return authUser;

	}
}
