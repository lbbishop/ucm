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

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.util.Authenticated;

@SessionScoped
public class AuthenticatedUserProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	private AuthUser authUser;

	@Inject
	public void init() {
		this.authUser = new AuthUser();
	}

	/**
	 * user change event
	 * 
	 * @param user
	 */
	public void onAuthenticatedUserChange(@Observes @Authenticated final AuthUser authUser) {
		logger.info("onAuthenticatedUserChange {}", authUser);
		this.authUser = authUser;
	}

	@Produces
	@Authenticated
	@Named
	public AuthUser getAuthUser() {
		return this.authUser;
	}
}
