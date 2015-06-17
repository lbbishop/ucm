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
package com.sfs.ucm.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;

/**
 * FlowStep
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "flowstep")
public class FlowStep extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "step_number", nullable = false)
	private Short stepNumber;

	@NotNull
	@OneToOne
	@JoinColumn(name = "actor_id", nullable = false)
	private Actor actor;

	@NotNull(message = "Action Description is required")
	@Column(name = "action_description", columnDefinition = "TEXT")
	private String actionDescription;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "resource_id", nullable = true)
	private Resource resource;

	@OneToMany(mappedBy = "flowStep", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<FlowStepRule> flowStepRules;

	@ManyToOne(optional = false)
	private Flow flow;

	/**
	 * Default constructor
	 */
	public FlowStep() {
		super();
		init();
	}

	/**
	 * Data constructor
	 * 
	 * @param stepNumber
	 */
	public FlowStep(Short stepNumber) {
		super();
		init();
		this.stepNumber = stepNumber;
	}
	
	private void init() {
		this.flowStepRules = new HashSet<FlowStepRule>();
	}

	/**
	 * PrePersist method
	 */
	@PrePersist
	public void prePersist() {
		if (this.modifiedBy == null) {
			this.modifiedBy = Literal.APPNAME.toString();
		}
		this.modifiedDate = new Date();
	}

	/**
	 * PreUpdate method
	 */
	@PreUpdate
	public void preUpdate() {
		if (this.modifiedBy == null) {
			this.modifiedBy = Literal.APPNAME.toString();
		}
		this.modifiedDate = new Date();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the flow
	 */
	public Flow getFlow() {
		return flow;
	}

	/**
	 * @param flow
	 *            the flow to set
	 */
	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	/**
	 * @return the actor
	 */
	public Actor getActor() {
		return actor;
	}

	/**
	 * @param actor
	 *            the actor to set
	 */
	public void setActor(Actor actor) {
		this.actor = actor;
	}

	/**
	 * @return the actionDescription
	 */
	public String getActionDescription() {
		return actionDescription;
	}

	/**
	 * @return the actionDescriptionAbbrv
	 */
	public String getActionDescriptionAbbrv() {
		return StringUtils.abbreviate(actionDescription, 80);
	}

	/**
	 * @param actionDescription
	 *            the actionDescription to set
	 */
	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy
	 *            the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public java.util.Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            the modifiedDate to set
	 */
	public void setModifiedDate(java.util.Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * @return the stepNumber
	 */
	public Short getStepNumber() {
		return stepNumber;
	}

	/**
	 * @param stepNumber
	 *            the stepNumber to set
	 */
	public void setStepNumber(Short stepNumber) {
		this.stepNumber = stepNumber;
	}

	/**
	 * @return the flowStepRules
	 */
	public Collection<FlowStepRule> getFlowStepRules() {
		return flowStepRules;
	}

	/**
	 * Add business rule
	 * 
	 * @param flowStepRule
	 *            the FlowStepRule to add
	 */
	public void addFlowStepRule(FlowStepRule flowStepRule) {
		flowStepRule.setFlowStep(this);
		this.flowStepRules.add(flowStepRule);
	}

	/**
	 * Remove business rule
	 * 
	 * @param flowStepRule
	 *            the FlowStepRule to remove
	 */
	public void removeFlowStepRule(FlowStepRule flowStepRule) {
		flowStepRule.setFlowStep(null);
		this.flowStepRules.remove(flowStepRule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FlowStep [id=");
		builder.append(id);
		builder.append(", stepNumber=");
		builder.append(stepNumber);
		builder.append(", actionDescription=");
		builder.append(actionDescription);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + stepNumber;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlowStep other = (FlowStep) obj;
		if (stepNumber != other.stepNumber)
			return false;
		return true;
	}

}
