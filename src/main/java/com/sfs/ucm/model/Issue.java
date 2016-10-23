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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.sfs.ucm.data.Constants;
import com.sfs.ucm.data.IssueType;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.PriorityType;
import com.sfs.ucm.data.StatusType;
import com.sfs.ucm.util.ModelUtils;

/**
 * Issue
 * 
 * @author lbbishop
 * 
 */
@Entity
@Indexed
@Audited
@Table(name = "task")
public class Issue extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Title is required")
	@Size(max = 100)
	@Column(name = "title", length = 100, nullable = false)
	private String title;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Description is required")
	@Lob
	@Column(name = "description", columnDefinition = "TEXT", nullable = false)
	private String description;

	@NotNull(message = "Status is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "status_type", nullable = false)
	private StatusType statusType;

	@NotNull(message = "IssueType is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "issue_type", nullable = false)
	private IssueType issueType;

	@NotNull(message = "Priority is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "priority_type", nullable = false)
	private PriorityType priorityType;

	@DecimalMin("0.00")
	@Digits(integer=3, fraction=2)
	@Column(name = "estimated_effort", nullable = true)
	private Double estimatedEffort;

	@DecimalMin("0.00")
	@Digits(integer=3, fraction=2)
	@Column(name = "actual_effort", nullable = true)
	private Double actualEffort;
	
	@OneToOne
	@JoinColumn(name = "authuser_id", nullable = true)
	private ProjectMember assignee;

	@Column(name = "send_notification", nullable = false)
	private Boolean sendNotification;


	@ManyToOne
	private Project project;

	/**
	 * Default constructor
	 */
	public Issue() {
		super();
		init();
	}

	/**
	 * Iteration constructor
	 */
	public Issue(int identifier) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		
	}

	/**
	 * class init method
	 */
	private void init() {
		this.sendNotification = false;
		this.assignee = new ProjectMember();
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the abbreviated title
	 */
	public String getTitleAbbrv() {
		return StringUtils.abbreviate(this.title, Constants.ABBRV_NAME_LEN);
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		if (title != null) {
			this.title = title.trim();
		}
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the description
	 */
	public String getDescriptionAbbrv() {
		return StringUtils.abbreviate(this.description, Constants.ABBRV_DESC_LEN);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the priorityType
	 */
	public PriorityType getPriorityType() {
		return priorityType;
	}

	/**
	 * @param priorityType
	 *            the priorityType to set
	 */
	public void setPriorityType(PriorityType priorityType) {
		this.priorityType = priorityType;
	}

	/**
	 * @return the estimatedEffort
	 */
	public Double getEstimatedEffort() {
		return estimatedEffort;
	}

	/**
	 * @param estimatedEffort
	 *            the estimatedEffort to set
	 */
	public void setEstimatedEffort(Double estimatedEffort) {
		this.estimatedEffort = estimatedEffort;
	}

	/**
	 * @return the issueType
	 */
	public IssueType getIssueType() {
		return issueType;
	}

	/**
	 * @param issueType
	 *            the issueType to set
	 */
	public void setIssueType(IssueType issueType) {
		this.issueType = issueType;
	}

	/**
	 * @return the assignee
	 */
	public ProjectMember getAssignee() {
		return assignee;
	}

	/**
	 * @param assignee
	 *            the assignee to set
	 */
	public void setAssignee(ProjectMember assignee) {
		this.assignee = assignee;
	}

	/**
	 * @return the sendNotification
	 */
	public Boolean getSendNotification() {
		return sendNotification;
	}

	/**
	 * @param sendNotification
	 *            the sendNotification to set
	 */
	public void setSendNotification(Boolean sendNotification) {
		this.sendNotification = sendNotification;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_ISSUE.toString(), this.identifier);
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
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Issue other = (Issue) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
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
		builder.append("Issue	 [id=");
		builder.append(id);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", statusType=");
		builder.append(statusType);
		builder.append(", issueType=");
		builder.append(issueType);
		builder.append(", priorityType=");
		builder.append(priorityType);
		builder.append(", estimatedEffort=");
		builder.append(estimatedEffort);
		builder.append(", assignee=");
		builder.append(assignee);
		builder.append(", sendNotification=");
		builder.append(sendNotification);
		builder.append("]");
		return builder.toString();
	}

}
