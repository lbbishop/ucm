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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
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

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * StakeholderRequest
 * 
 * @author lbbishop
 * 
 */
@Entity
@Indexed
@Audited
@Table(name="stakeholderrequest")
public class StakeholderRequest extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Name is required")
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Description is required")
	@Lob
	@Column(name = "description", columnDefinition = "TEXT", nullable = false)
	private String description;

	@OneToOne
	@JoinColumn(name = "stakeholder_id", nullable=true)
	private Stakeholder stakeholder;

	@ManyToOne
	private Project project;

	@OneToMany(mappedBy = "stakeholderRequest", cascade = { CascadeType.PERSIST })
	private Collection<Feature> features;

	/**
	 * Default constructor
	 */
	public StakeholderRequest() {
		super();
		init();
	}

	/**
	 * identifier constructor
	 */
	public StakeholderRequest(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
		init();
	}

	/**
	 * class init method
	 */
	private void init() {
		this.features = new HashSet<Feature>();
		this.stakeholder = new Stakeholder();
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the description
	 */
	public String getDescriptionAbbrv() {
		return StringUtils.abbreviate(description, 80);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the stakeholder
	 */
	public Stakeholder getStakeholder() {
		return stakeholder;
	}

	/**
	 * @param stakeholder
	 *            the stakeholder to set
	 */
	public void setStakeholder(Stakeholder stakeholder) {
		this.stakeholder = stakeholder;
	}

	/**
	 * @return the features
	 */
	public Collection<Feature> getFeatures() {
		return features;
	}

	/**
	 * @param feature
	 *            add feature
	 */
	public void addFeature(Feature feature) {
		feature.setStakeholderRequest(this);
		this.features.add(feature);
	}

	/**
	 * @param feature
	 *            feature to remove
	 */
	public void removeFeature(Feature feature) {
		feature.setStakeholderRequest(null);
		this.features.remove(feature);
	}

	/**
	 * @return the name abbreviated
	 */
	public String getNameAbbrv() {
		return StringUtils.abbreviate(name, 20);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_STAKEHOLDERREQUEST.toString(), this.identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StakeholderRequest [id=");
		builder.append(id);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", stakeholder=");
		builder.append(stakeholder);
		builder.append(", project=");
		builder.append(project);
		builder.append(", features=");
		builder.append(features);
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
		StakeholderRequest other = (StakeholderRequest) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
