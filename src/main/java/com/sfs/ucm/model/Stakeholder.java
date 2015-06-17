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
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * Stakeholder
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name="stakeholder")
public class Stakeholder extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@NotNull(message = "Role is required")
	@Size(max = 100)
	@Column(name = "role", length = 100, nullable = false)
	private String role;

	@NotNull(message = "Representative is required")
	@Size(max = 80)
	@Column(name = "representative", length = 80, nullable = false)
	private String representative;

	@Size(max = 255)
	@Column(name = "responsibilities", length = 255, nullable = true)
	private String responsibilities;

	@Size(max = 255)
	@Column(name = "involvement", length = 255, nullable = false)
	private String involvement;

	@Size(max = 255)
	@Column(name = "comments", length = 255, nullable = true)
	private String comments;

	@Size(max = 255)
	@Column(name = "deliverables", length = 255, nullable = true)
	private String deliverables;

	@OneToOne(optional=true, cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, fetch=FetchType.LAZY)
	@JoinColumn(name = "survivaltest_id", nullable = true)
	private SurvivalTest survivalTest;

	@ManyToOne
	private Project project;

	@OneToMany(mappedBy = "stakeholder")
	private Collection<StakeholderRequest> stakeholderRequests;

	/**
	 * Default constructor
	 */
	public Stakeholder() {
		super();
		init();
	}

	/**
	 * constructor
	 */
	public Stakeholder(int identifier) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
	}

	/**
	 * init method
	 */
	private void init() {
		this.stakeholderRequests = new HashSet<StakeholderRequest>();
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
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role) {
		this.role = role.trim();
	}

	/**
	 * @return the representative
	 */
	public String getRepresentative() {
		return representative;
	}

	/**
	 * @param representative
	 *            the representative to set
	 */
	public void setRepresentative(String representative) {
		this.representative = representative.trim();
	}

	/**
	 * @return the involvement
	 */
	public String getInvolvement() {
		return involvement;
	}

	/**
	 * @param involvement
	 *            the involvement to set
	 */
	public void setInvolvement(String involvement) {
		this.involvement = involvement;
	}

	/**
	 * @return the responsibilities
	 */
	public String getResponsibilities() {
		return responsibilities;
	}

	/**
	 * @param responsibilities
	 *            the responsibilities to set
	 */
	public void setResponsibilities(String responsibilities) {
		this.responsibilities = responsibilities.trim();
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
	 * @return the survivalTest
	 */
	public SurvivalTest getSurvivalTest() {
		return survivalTest;
	}

	/**
	 * @param survivalTest
	 *            the survivalTest to set
	 */
	public void setSurvivalTest(SurvivalTest survivalTest) {
		this.survivalTest = survivalTest;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the deliverables
	 */
	public String getDeliverables() {
		return deliverables;
	}

	/**
	 * @param deliverables
	 *            the deliverables to set
	 */
	public void setDeliverables(String deliverables) {
		this.deliverables = deliverables;
	}

	/**
	 * @return the stakeholderRequests
	 */
	public Collection<StakeholderRequest> getStakeholderRequests() {
		return stakeholderRequests;
	}

	/**
	 * @param the
	 *            stakeholderRequest to add
	 */
	public void addStakeholderRequest(StakeholderRequest stakeholderRequest) {
		stakeholderRequest.setStakeholder(this);
		this.stakeholderRequests.add(stakeholderRequest);
	}
	
	/**
	 * @param the
	 *            stakeholderRequest to remove
	 */
	public void removeStakeholderRequest(StakeholderRequest stakeholderRequest) {
		stakeholderRequest.setStakeholder(null);
		this.stakeholderRequests.remove(stakeholderRequest);
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_STAKEHOLDER.toString(), this.identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Stakeholder [id=");
		builder.append(id);
		builder.append(", role=");
		builder.append(role);
		builder.append(", representative=");
		builder.append(representative);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		Stakeholder other = (Stakeholder) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		}
		else
			if (!role.equals(other.role))
				return false;
		return true;
	}

}
