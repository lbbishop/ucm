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

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {
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
					String outcome = calcExceptionOutcome(throwable);
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
	 * Helper method to determine outcome
	 * 
	 * @param throwable
	 * @return outcome
	 */
	private String calcExceptionOutcome(Throwable throwable) {
		String outcome = null;

		if (throwable.toString().indexOf("NonexistentConversationException") != -1) {
			outcome = "/viewexpired.jsf?faces-redirect=true";
		}
		else if (throwable.toString().indexOf("ViewExpiredException") != -1) {
			outcome = "/viewexpired.jsf?faces-redirect=true";
		}
		else {
			outcome = "/error.jsf?nocid=true";
		}
		return outcome;
	}
}