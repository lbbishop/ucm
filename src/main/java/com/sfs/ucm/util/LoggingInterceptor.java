//*****************************************************************************
//
//
//					   Sandia National Laboratories (SNL)														
//
//
//*****************************************************************************
package com.sfs.ucm.util;

import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.MDC;

/**
 * CDI logging interceptor: adds userid to Logback MDC context. Use %X{userid} in logback pattern definition.
 * 
 * @author lbbisho
 */
@Interceptor
@Log
public class LoggingInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String NULL_USER = "null user";

	@AroundInvoke
	public Object logMethodEntry(InvocationContext ctx) throws Exception {

		// add userid to MDC
		MDC.put("userid", getSessionUsername());

		return ctx.proceed();
	}

	/**
	 * Determine what the username should be based on the session context.
	 *
	 * @return the username
	 */
	private String getSessionUsername() {

		String username = NULL_USER;
		try {
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			   Object request = context.getRequest();
			    if (request instanceof HttpServletRequest) {
			        username = (String)((HttpServletRequest) request).getSession().getAttribute("USERID");
			    }
		}
		catch (Exception e) {
			// handle exception
		}
		return username;
	}
}
