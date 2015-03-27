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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import com.sfs.ucm.data.DevelopmentPhaseType;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.StatusType;
import com.sfs.ucm.util.ModelUtils;

/**
 * Iteration
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name="iteration")
public class Iteration extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Objective is required")
	@Lob
	@Column(name = "objective", columnDefinition = "TEXT", nullable = false)
	private String objective;

	@Lob
	@Column(name = "assessment", columnDefinition = "TEXT  NULL", nullable = true)
	private String assessment;

	@Lob
	@Column(columnDefinition = "TEXT  NULL", nullable = true)
	private String planning;

	@NotNull(message = "Development Phase is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "development_phase_type", nullable = false)
	private DevelopmentPhaseType developmentPhaseType;

	@NotNull
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "begin_date", nullable = false)
	private java.util.Date beginDate;

	@NotNull
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", nullable = false)
	private java.util.Date endDate;
	
	@NotNull(message = "Status is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "status_type", nullable = false)
	private StatusType statusType;

	@OneToMany(mappedBy = "iteration")
	private List<UseCase> useCases;

	@OneToMany(mappedBy = "iteration", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Activity> activities;

	@ManyToOne
	private Project project;

	/**
	 * Default constructor
	 */
	public Iteration() {
		super();
		init();
	}

	/**
	 * Iteration constructor
	 */
	public Iteration(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
		init();
	}

	/**
	 * class init method
	 */
	private void init() {
		this.useCases = new ArrayList<UseCase>();
		this.activities = new ArrayList<Activity>();
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
	 * @return the objective
	 */
	public String getObjective() {
		return objective;
	}

	/**
	 * @return the abbreviated objective
	 */
	public String getObjectiveAbbrv() {
		return StringUtils.abbreviate(objective, 80);
	}

	/**
	 * @param objective
	 *            the objective to set
	 */
	public void setObjective(String objective) {
		this.objective = objective;
	}

	/**
	 * @return the beginDate
	 */
	public java.util.Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @return the developmentPhaseType
	 */
	public DevelopmentPhaseType getDevelopmentPhaseType() {
		return developmentPhaseType;
	}

	/**
	 * @param developmentPhaseType
	 *            the developmentPhaseType to set
	 */
	public void setDevelopmentPhaseType(DevelopmentPhaseType developmentPhaseType) {
		this.developmentPhaseType = developmentPhaseType;
	}

	/**
	 * @param beginDate
	 *            the beginDate to set
	 */
	public void setBeginDate(java.util.Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return the endDate
	 */
	public java.util.Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_ITERATION.toString(), this.identifier);
	}

	/**
	 * @return the assessment
	 */
	public String getAssessment() {
		return assessment;
	}

	/**
	 * @param assessment
	 *            the assessment to set
	 */
	public void setAssessment(String assessment) {
		this.assessment = assessment;
	}

	/**
	 * @return the planning
	 */
	public String getPlanning() {
		return planning;
	}

	/**
	 * @param planning
	 *            the planning to set
	 */
	public void setPlanning(String planning) {
		this.planning = planning;
	}

	/**
	 * @return the statusType
	 */
	public StatusType getStatusType() {
		return statusType;
	}

	/**
	 * @param statusType the statusType to set
	 */
	public void setStatusType(StatusType statusType) {
		this.statusType = statusType;
	}

	/**
	 * @return the useCases
	 */
	public List<UseCase> getUseCases() {
		return useCases;
	}

	/**
	 * @param useCase
	 *            the useCase to add
	 */
	public void addUseCase(UseCase useCase) {
		useCase.setIteration(this);
		useCases.add(useCase);
	}
	
	/**
	 * @param useCase
	 *            the useCase to remove
	 */
	public void removeUseCase(UseCase useCase) {
		useCase.setIteration(null);
		useCases.remove(useCase);
	}

	/**
	 * @return the activities
	 */
	public List<Activity> getActivities() {
		return activities;
	}

	/**
	 * @param activity
	 *            the activity to add
	 */
	public void addActivity(Activity activity) {
		activity.setIteration(this);
		this.activities.add(activity);
	}
	
	/**
	 * @param activity
	 *            the activity to remove
	 */
	public void removeActivity(Activity activity) {
		activity.setIteration(null);
		this.activities.remove(activity);
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
		result = prime * result + ((objective == null) ? 0 : objective.hashCode());
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
		Iteration other = (Iteration) obj;
		if (objective == null) {
			if (other.objective != null)
				return false;
		}
		else if (!objective.equals(other.objective))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Iteration [id=");
		builder.append(id);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", phase=");
		builder.append(developmentPhaseType);
		builder.append(", beginDate=");
		builder.append(beginDate);
		builder.append(", endDate=");
		builder.append(endDate);
		builder.append("]");
		return builder.toString();
	}

}
