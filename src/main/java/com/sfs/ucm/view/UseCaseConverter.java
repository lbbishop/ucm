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
package com.sfs.ucm.view;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import com.sfs.ucm.model.UseCase;

/**
 * UseCase Converter
 * 
 * @author lbbishop
 * 
 */
@Named
@RequestScoped
public class UseCaseConverter implements Converter, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		UseCase obj = null;
		if (!value.trim().equals("")) {
			Long id = Long.valueOf(value);
			obj = em.find(UseCase.class, id);
		}
		return obj;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String sval = "";
		if (value != null && !value.equals("")) {
			UseCase obj = (UseCase) value;
			if (obj != null && obj.getId() != null) {
				sval = obj.getId().toString();
			}
		}
		return sval;
	}
}
