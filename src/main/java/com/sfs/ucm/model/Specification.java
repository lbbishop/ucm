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
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.sfs.ucm.data.Constants;
import com.sfs.ucm.data.DifficultyType;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.QualityType;
import com.sfs.ucm.util.ModelUtils;

/**
 * Non-functional (Supplemental) Specification Specification
 * 
 * @author lbbishop
 * 
 */
@Table(name = "specification")
@Entity
@Indexed
@Audited
public class Specification extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
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
	@Column(name = "description", columnDefinition = "CLOB", nullable = false)
	private String description;

	@NotNull(message = "Quality Type is required")
	@Column(name = "quality_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private QualityType qualityType;

	@OneToOne
	@JoinColumn(name = "projectpackage_id", nullable = true)
	private ProjectPackage projectPackage;

	@Enumerated(EnumType.STRING)
	@Column(name = "difficulty_type", nullable = true)
	private DifficultyType difficultyType;

	@Lob
	@Column(name = "issues", columnDefinition = "CLOB", nullable = true)
	private String issues;

	@ManyToOne
	private Project project;

	@ManyToOne
	private Feature feature;

	@IndexedEmbedded
	@OneToMany(mappedBy = "specification", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<SpecificationRule> specificationRules;
	
	@IndexedEmbedded
	@OneToMany(mappedBy = "specification", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<SpecificationAttachment> attachments;

	/**
	 * Default constructor
	 */
	public Specification() {
		super();
		init();
	}

	/**
	 * Identifier constructor
	 */
	public Specification(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
		init();
	}

	/**
	 * class init method
	 */
	private void init() {
		this.specificationRules = new HashSet<SpecificationRule>();
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
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature
	 *            the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	/**
	 * Add business rule
	 * 
	 * @param useCaseRule
	 *            the UseCaseRule to add
	 */
	public void addSpecificationRule(SpecificationRule specificationRule) {
		specificationRule.setSpecification(this);
		this.specificationRules.add(specificationRule);
	}

	/**
	 * Remove business rule
	 * 
	 * @param useCaseRule
	 *            the UseCaseRule to remove
	 */
	public void removeSpecificationRule(SpecificationRule specificationRule) {
		specificationRule.setSpecification(null);
		this.specificationRules.remove(specificationRule);
	}

	/**
	 * @return the specificationRules
	 */
	public Collection<SpecificationRule> getSpecificationRules() {
		return specificationRules;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_SPECIFICATION.toString(), this.identifier);
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
	 * @return the qualityType
	 */
	public QualityType getQualityType() {
		return qualityType;
	}

	/**
	 * @param qualityType
	 *            the qualityType to set
	 */
	public void setQualityType(QualityType qualityType) {
		this.qualityType = qualityType;
	}

	/**
	 * @return the difficultyType
	 */
	public DifficultyType getDifficultyType() {
		return difficultyType;
	}

	/**
	 * @param difficultyType
	 *            the difficultyType to set
	 */
	public void setDifficultyType(DifficultyType difficultyType) {
		this.difficultyType = difficultyType;
	}

	/**
	 * @return the projectPackage
	 */
	public ProjectPackage getProjectPackage() {
		return projectPackage;
	}

	/**
	 * @param projectPackage
	 *            the projectPackage to set
	 */
	public void setProjectPackage(ProjectPackage projectPackage) {
		this.projectPackage = projectPackage;
	}

	/**
	 * @return the issues
	 */
	public String getSpecifications() {
		return issues;
	}

	/**
	 * @param issues
	 *            the issues to set
	 */
	public void setSpecifications(String issues) {
		this.issues = issues;
	}
	
	/**
	 * @return the attachments
	 */
	public Collection<SpecificationAttachment> getAttachments() {
		return attachments;
	}

	/**
	 * Add attachment
	 * 
	 * @param attachment
	 *            the attachment to add
	 */
	public void addAttachment(SpecificationAttachment attachment) {
		attachment.setSpecification(this);
		this.attachments.add(attachment);
	}

	/**
	 * Remove attachment
	 * 
	 * @param attachment
	 *            the attachment to remove
	 */
	public void removeAttachment(SpecificationAttachment attachment) {
		attachment.setSpecification(null);
		this.attachments.remove(attachment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(", id=");
		builder.append(id);
		builder.append("Specification [identifier=");
		builder.append(identifier);
		builder.append(", description=");
		builder.append(description);
		builder.append(", qualityType=");
		builder.append(qualityType);
		builder.append(", difficultyType=");
		builder.append(difficultyType);
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
		Specification other = (Specification) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
