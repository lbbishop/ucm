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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.sfs.ucm.model.Project;
import com.sfs.ucm.util.ProjectSecurityInit;

@Stateful
@SessionScoped
public class ProjectSecurityMarkingProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	private List<SelectItem> securityMarkingItems;

	private String projectSecurityMarking;

	@Inject
	public void init() {
		load();
	}

	/**
	 * project security event
	 * 
	 * @param project
	 */
	public void onProjectSecurityInit(@Observes @ProjectSecurityInit final Project project) {
		findSecurityMarking(project);
	}

	/**
	 * @return the Security Marking Items
	 */
	@Produces
	@Named
	public List<SelectItem> getSecurityMarkingItems() {
		return securityMarkingItems;
	}

	/**
	 * @return the project security marking
	 */
	@Produces
	@Named
	public String getProjectSecurityMarking() {
		return this.projectSecurityMarking;
	}

	/**
	 * Load resources
	 */
	private void load() {
		securityMarkingItems = new ArrayList<SelectItem>();
		this.securityMarkingItems.add(new SelectItem("Unlimited Access", "Unlimited Access"));
		this.securityMarkingItems.add(new SelectItem("OUO", "Official Use Only"));
		this.securityMarkingItems.add(new SelectItem("UCI", "Unclassified Controlled Information"));
		this.securityMarkingItems.add(new SelectItem("UCNI", "Unclassified Controlled Nuclear Information"));
		this.securityMarkingItems.add(new SelectItem("OUO Exemption 3", "OUO Exemption 3 - Statutory Exemption"));
		this.securityMarkingItems.add(new SelectItem("OUO Exemption 4", "OUO Exemption 4 - Commercial/Proprietary"));
		this.securityMarkingItems.add(new SelectItem("OUO Exemption 5", "OUO Exemption 5 - Privileged Information"));
		this.securityMarkingItems.add(new SelectItem("OUO Exemption 6", "OUO Exemption 6 - Personal Privacy"));
		this.securityMarkingItems.add(new SelectItem("OUO Exemption 7", "OUO Exemption 7 - Law Enforcement"));
	}

	/**
	 * Find security marking.
	 * 
	 * @param project
	 * @return label
	 */
	private void findSecurityMarking(Project project) {
		this.projectSecurityMarking = null;
		ListIterator<SelectItem> iter = securityMarkingItems.listIterator();
		while (iter.hasNext()) {
			SelectItem item = iter.next();
			if (item.getValue().equals(project.getSecurityMarking())) {
				this.projectSecurityMarking = item.getLabel();
				break;
			}
		}
	}
}
