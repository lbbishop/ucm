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

import java.util.Iterator;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {

	private static Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);
	private ExceptionHandler wrapped;

	public CustomExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {
		@SuppressWarnings("rawtypes")
		Iterator iterator = getUnhandledExceptionQueuedEvents().iterator();

		while (iterator.hasNext()) {
			ExceptionQueuedEvent event = (ExceptionQueuedEvent) iterator.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

			Throwable throwable = context.getException();

			FacesContext fc = FacesContext.getCurrentInstance();
			try {
				if (throwable.getMessage() != null) {

					fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, throwable.getMessage(), null));
					System.err.println("class: " + throwable.toString() + " message: " + throwable.getMessage());

					if (throwable.getCause() != null) {
						fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, throwable.getCause().getMessage(), null));
						System.err.println("cause: " + throwable.getCause().getMessage());
					}
					String outcome = calcExceptionOutcome(fc, throwable);
					NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();
					navigationHandler.handleNavigation(fc, null, outcome);

					fc.renderResponse();
				}
			}
			finally {
				iterator.remove();
			}
		}

		// Let the parent handle the rest
		getWrapped().handle();
	}

	/**
	 * Helper method to determine outcome and log messages.
	 * <p>
	 * Rules:
	 * <ul>
	 * <li>If exception is a WELD NonexistentConversationException, the error logged and outcome is the expired session page.</li>
	 * <li>If exception is a JSF ViewExpiredException, outcome is the expired session page with no error logged.</li>
	 * <li>All other errors cause navigation to error page with exception message logged to system logger and FacesContext message queue.</li>
	 * </ul>
	 * 
	 * @param FacesContext
	 *            the JSF FacesContext
	 * @param throwable
	 *            the handled exception
	 * @return outcome the navigation outcome (null if no navigation is to be performed)
	 */
	private String calcExceptionOutcome(final FacesContext fc, final Throwable throwable) {
		String outcome = null;

		if (throwable instanceof NonexistentConversationException) {
			outcome = "/expired.jsf?nocid=true";
		}
		else if (throwable instanceof ViewExpiredException) {
			outcome = "/expired.jsf?faces-redirect=true";
		}
		else {

			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, throwable.getMessage(), null));
			logger.error("Class: {} Message:{}", throwable.toString(), throwable.getMessage());

			if (throwable.getCause() != null) {
				fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, throwable.getCause().getMessage(), null));
				logger.error("Cause: {}", throwable.getCause().getMessage());
			}

			outcome = "/error.jsf?nocid=true";
		}
		return outcome;
	}

}