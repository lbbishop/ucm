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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.StatusType;
import com.sfs.ucm.util.ModelUtils;

/**
 * Activity
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "activity")
public class Activity extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Project Member is required")
	@OneToOne
	@JoinColumn(name = "authuser_id", nullable = false)
	private AuthUser authUser;

	@NotNull(message = "Task is required")
	@OneToOne
	@JoinColumn(name = "task_id", nullable = false)
	private Task task;

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull(message = "Activity begin date is required")
	@Column(name = "begin_date", nullable = false)
	private java.util.Date beginDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date", nullable = true)
	private java.util.Date endDate;

	@NotNull(message = "Status is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "status_type", nullable = false)
	private StatusType statusType;

	@ManyToOne(optional = false)
	private Iteration iteration;

	/**
	 * Default constructor
	 */
	public Activity() {
		super();
		init();
	}

	/**
	 * identifier constructor
	 * 
	 * @param identifier
	 * @param user
	 */
	public Activity(int identifier, AuthUser authUser) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		this.authUser = authUser;
	}

	/**
	 * class init method
	 */
	private void init() {

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
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_ITERATIONACTIVITY.toString(), this.identifier);
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
	 * @return the beginDate
	 */
	public java.util.Date getBeginDate() {
		return beginDate;
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
	 * Get task duration in hours:minutes
	 * 
	 * @return difference of end date and begin date
	 */
	public String getTaskDurationInHours() {
		String durationInHours = null;
		if (this.endDate != null) {
		Long duration = this.endDate.getTime() - this.beginDate.getTime();
		Long totalMinutes = duration / (1000 * 60);
		Long hours = totalMinutes / 60;
		Long minutes = totalMinutes % 60;

		durationInHours = hours.toString() + ":" + minutes.toString();
		}
		return durationInHours;
	}

	/**
	 * @return the authUser
	 */
	public AuthUser getAuthUser() {
		return authUser;
	}

	/**
	 * @param authUser
	 *            the authUser to set
	 */
	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}

	/**
	 * @return the statusType
	 */
	public StatusType getStatusType() {
		return statusType;
	}

	/**
	 * @param statusType
	 *            the statusType to set
	 */
	public void setStatusType(StatusType statusType) {
		this.statusType = statusType;
	}

	/**
	 * @return the iteration
	 */
	public Iteration getIteration() {
		return iteration;
	}

	/**
	 * @param iteration
	 *            the iteration to set
	 */
	public void setIteration(Iteration iteration) {
		this.iteration = iteration;
	}

	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(Task task) {
		this.task = task;
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
		result = prime * result + ((task == null) ? 0 : task.hashCode());
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
		Activity other = (Activity) obj;
		if (task == null) {
			if (other.task != null)
				return false;
		}
		else if (!task.equals(other.task))
			return false;
		return true;
	}

}
