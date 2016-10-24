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

import com.sfs.ucm.data.Constants;
import com.sfs.ucm.data.DifficultyType;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.PriorityType;
import com.sfs.ucm.data.RiskLevelType;
import com.sfs.ucm.data.StabilityType;
import com.sfs.ucm.util.ModelUtils;

/**
 * Feature
 * 
 * @author lbbishop
 * 
 */
@Entity
@Indexed
@Audited
@Table(name = "feature")
public class Feature extends EntityBase implements Serializable {

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
	@Column(name = "description", columnDefinition = "CLOB", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "stability_type", nullable = true)
	private StabilityType stabilityType;

	@Enumerated(EnumType.STRING)
	@Column(name = "difficulty_type", nullable = true)
	private DifficultyType difficultyType;

	@Enumerated(EnumType.STRING)
	@Column(name = "risk_level_type", nullable = true)
	private RiskLevelType riskLevelType;

	@Enumerated(EnumType.STRING)
	@Column(name = "priority_type", nullable = true)
	private PriorityType priorityType;

	@ManyToOne
	@JoinColumn(name = "stakeholderrequest_id", nullable = true)
	private StakeholderRequest stakeholderRequest;

	@ManyToOne
	private Project project;

	@OneToMany(mappedBy = "feature", cascade = { CascadeType.PERSIST })
	private Collection<Specification> specifications;

	@OneToMany(mappedBy = "feature", cascade = { CascadeType.PERSIST })
	private Collection<UseCase> useCases;

	/**
	 * Default constructor
	 */
	public Feature() {
		super();
		init();
	}

	/**
	 * Identifier constructor
	 */
	public Feature(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
		init();
	}

	/**
	 * class init method
	 */
	private void init() {
		this.specifications = new HashSet<Specification>();
		this.useCases = new HashSet<UseCase>();
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
	 * @return the stabilityType
	 */
	public StabilityType getStabilityType() {
		return stabilityType;
	}

	/**
	 * @param stabilityType
	 *            the stabilityType to set
	 */
	public void setStabilityType(StabilityType stabilityType) {
		this.stabilityType = stabilityType;
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
	 * @return the riskLevelType
	 */
	public RiskLevelType getRiskLevelType() {
		return riskLevelType;
	}

	/**
	 * @param riskLevelType
	 *            the riskLevelType to set
	 */
	public void setRiskLevelType(RiskLevelType riskLevelType) {
		this.riskLevelType = riskLevelType;
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
	 * @return the stakeholderRequest
	 */
	public StakeholderRequest getStakeholderRequest() {
		return stakeholderRequest;
	}

	/**
	 * @param stakeholderRequest
	 *            the stakeholderRequest to set
	 */
	public void setStakeholderRequest(StakeholderRequest stakeholderRequest) {
		this.stakeholderRequest = stakeholderRequest;
	}

	/**
	 * add specification
	 * 
	 * @param specification
	 */
	public void addSpecification(Specification specification) {
		specification.setFeature(this);
		this.specifications.add(specification);
	}

	/**
	 * remove specification
	 * 
	 * @param specification
	 */
	public void removeSpecification(Specification specification) {
		specification.setFeature(null);
		this.specifications.remove(specification);
	}

	/**
	 * Add UseCase
	 * 
	 * @param useCase
	 *            the useCase to add
	 */
	public void addUseCase(UseCase useCase) {
		useCase.setFeature(this);
		this.useCases.add(useCase);
	}

	/**
	 * Remove UseCase
	 * 
	 * @param useCase
	 *            the useCase to add
	 */
	public void removeUseCase(UseCase useCase) {
		useCase.setFeature(null);
		this.useCases.remove(useCase);
	}

	/**
	 * @return the specifications
	 */
	public Collection<Specification> getSpecifications() {
		return specifications;
	}

	/**
	 * @return the useCases
	 */
	public Collection<UseCase> getUseCases() {
		return useCases;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_FEATURE.toString(), this.identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Feature [id=");
		builder.append(id);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", difficultyType=");
		builder.append(difficultyType);
		builder.append(", riskLevelType=");
		builder.append(riskLevelType);
		builder.append(", priorityType=");
		builder.append(priorityType);
		builder.append(", project=");
		builder.append(project);
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
		Feature other = (Feature) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
