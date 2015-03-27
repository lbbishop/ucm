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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.sfs.ucm.data.Literal;

/**
 * UseCase Flow
 * 
 * @author lbbishop
 * 
 */
@Entity
@Indexed
@Audited
@Table(name = "flow")
public class Flow extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Name is required")
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@NotNull
	@Column(name = "basic_flow_flag", nullable = false)
	private Boolean basicFlowFlag;

	@Column(name = "start_step", nullable = true)
	private Short startStep;

	@Column(name = "end_step", nullable = true)
	private Short endStep;

	@ManyToOne(optional = true)
	@JoinColumn(name = "usecase_id")
	private UseCase useCase;

	// flow steps
	@OneToMany(mappedBy = "flow", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<FlowStep> flowSteps;

	// subflows
	@OneToMany(mappedBy = "flow", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Subflow> subflows;

	/**
	 * Default constructor
	 */
	public Flow() {
		super();
		init();
	}

	/**
	 * Data constructor
	 */
	public Flow(Boolean basicFlowFlag) {
		super();
		init();
		this.basicFlowFlag = basicFlowFlag;
	}

	/**
	 * Constructor
	 * 
	 * @param startStep
	 */
	public Flow(Short startStep) {
		super();
		init();
		this.startStep = startStep;
	}

	/**
	 * class init method
	 */
	private void init() {
		this.basicFlowFlag = false;
		this.flowSteps = new ArrayList<FlowStep>();
		this.subflows = new ArrayList<Subflow>();
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
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		if (name != null) {
			this.name = name.trim();
		}
	}

	/**
	 * Add flow step
	 * 
	 * @param flowStep
	 */
	public void addFlowStep(FlowStep flowStep) {
		flowStep.setFlow(this);
		this.flowSteps.add(flowStep);
	}

	/**
	 * remove flow step
	 * 
	 * @param flowStep
	 */
	public void removeFlowStep(FlowStep flowStep) {
		flowStep.setFlow(null);
		this.flowSteps.remove(flowStep);
	}

	/**
	 * @return the flowSteps
	 */
	public List<FlowStep> getFlowSteps() {
		return flowSteps;
	}

	/**
	 * @return the startStep
	 */
	public Short getStartStep() {
		return startStep;
	}

	/**
	 * @return the endStep
	 */
	public Short getEndStep() {
		return endStep;
	}

	/**
	 * @return the useCase
	 */
	public UseCase getUseCase() {
		return useCase;
	}

	/**
	 * @param useCase
	 *            the useCase to set
	 */
	public void setUseCase(UseCase useCase) {
		this.useCase = useCase;
	}

	/**
	 * @return the basicFlowFlag
	 */
	public Boolean getBasicFlowFlag() {
		return basicFlowFlag;
	}

	/**
	 * @param basicFlowFlag
	 *            the basicFlowFlag to set
	 */
	public void setBasicFlowFlag(Boolean basicFlowFlag) {
		this.basicFlowFlag = basicFlowFlag;
	}

	/**
	 * @param startStep
	 *            the startStep to set
	 */
	public void setStartStep(Short startStep) {
		this.startStep = startStep;
	}

	/**
	 * @param endStep
	 *            the endStep to set
	 */
	public void setEndStep(Short endStep) {
		this.endStep = endStep;
	}

	/**
	 * @param flowSteps
	 *            the flowSteps to set
	 */
	public void setFlowSteps(List<FlowStep> flowSteps) {
		this.flowSteps = flowSteps;
	}

	/**
	 * @return the subflows
	 */
	public List<Subflow> getSubflows() {
		return subflows;
	}

	/**
	 * @param subflow
	 *            the subflow to add
	 */
	public void addSubflow(Subflow subflow) {
		subflow.setFlow(this);
		this.subflows.add(subflow);
	}

	/**
	 * @param subflow
	 *            the subflow to remove
	 */
	public void removeSubflow(Subflow subflow) {
		subflow.setFlow(null);
		this.subflows.remove(subflow);
	}
	
	/**
	 * Number of flow steps
	 * @return number of steps
	 */
	public int getNumSteps() {
		return this.flowSteps.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Flow [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", startStep=");
		builder.append(startStep);
		builder.append(", endStep=");
		builder.append(endStep);
		builder.append(", flowSteps=");
		builder.append(flowSteps);
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Flow other = (Flow) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
