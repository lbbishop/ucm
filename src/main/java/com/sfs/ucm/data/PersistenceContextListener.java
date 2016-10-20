//*****************************************************************************
//
//
//					Sandia National Laboratories (SNL)										
//
//
//*****************************************************************************
package com.sfs.ucm.data;

import java.sql.Timestamp;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.sfs.ucm.model.EntityBase;

/**
 * Persistence entity listener
 * 
 * @author lbbisho
 *
 */
public class PersistenceContextListener {

	@PostConstruct
	public void init() {

	}

	/**
	 * PrePersist listener
	 * <p>
	 * Works in a web context and non-web context. If web context is not available, application name is used for modifiedBy property.
	 * 
	 * @param o
	 */
	@PrePersist
	public void prePresist(Object o) {

		String userid = Literal.APPNAME.toString();
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			if (request != null) {
				String tmpstr = (String) request.getSession().getAttribute("USERID");
				if (StringUtils.isNotBlank(tmpstr)) {
					userid = tmpstr;
				}
			}
		}

		if (o instanceof EntityBase) {
			EntityBase base = (EntityBase) o;
			final Timestamp now = new Timestamp(System.currentTimeMillis());
			base.setModifiedBy(userid);
			base.setModifiedDate(now);			
		}
	}

	/**
	 * PreUpdate listener
	 * <p>
	 * Works in a web context and non-web context. If web context is not available, application name is used for modifiedBy property.
	 * 
	 * @param o
	 */
	@PreUpdate
	public void preUpdate(Object o) {

		String userid = Literal.APPNAME.toString();
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			if (request != null) {
				String tmpstr = (String) request.getSession().getAttribute("USERID");
				if (StringUtils.isNotBlank(tmpstr)) {
					userid = tmpstr;
				}
			}
		}

		if (o instanceof EntityBase) {
			EntityBase base = (EntityBase) o;
			final Timestamp now = new Timestamp(System.currentTimeMillis());
			base.setModifiedBy(userid);
			base.setModifiedDate(now);	
		}
	}
}
