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
package com.sfs.ucm.exception;

import javax.ejb.ApplicationException;

/**
 * @author lbbisho
 * 
 *         Application runtime exception with EJB 3 transaction rollback
 * 
 */
@ApplicationException(rollback=true)
public class UCMException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with error message.
	 * 
	 * @param message
	 *            the error message associated with the exception
	 */
	public UCMException(String message) {
		super(message);
	}

	/**
	 * Constructor with exception
	 * 
	 * @param throwable
	 *            the throwable exception
	 */
	public UCMException(Throwable throwable) {
		super(throwable);
	}
	
	/**
	 * Constructor with error message and root cause.
	 * 
	 * @param message
	 *            user supplied message
	 *            @param throwable cause
	 */
	public UCMException(String message, Throwable cause) {
		super(message, cause);
	}

}
