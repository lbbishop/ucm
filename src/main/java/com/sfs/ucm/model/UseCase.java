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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.StatusType;
import com.sfs.ucm.util.ModelUtils;

/**
 * UseCase
 * 
 * @author lbbishop
 * 
 */
@Entity
@Indexed
@Audited
@Table(name="usecase")
public class UseCase extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	// use case complexity weights
	public static final int SIMPLE = 5;
	public static final int AVERAGE = 10;
	public static final int COMPLEX = 15;

	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Name is required")
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@NotNull(message = "Primary Actor is required")
	@OneToOne
	@JoinColumn(name = "primaryactor_id", nullable = false)
	private Actor primaryActor;

	@NotNull(message = "Objective is required")
	@Size(max = 255)
	@Column(name = "objective", length = 255, nullable = false)
	private String objective;

	@NotNull(message = "Status is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "status_type", nullable = false)
	private StatusType statusType;

	@NotNull(message = "Complexity is required")
	@Column(name = "complexity", nullable = false)
	private Integer complexity;

	@NotNull(message = "Product Release is required")
	@OneToOne
	@JoinColumn(name = "productrelease_id", nullable = false)
	private ProductRelease productRelease;

	@OneToOne
	@JoinColumn(name = "projectpackage_id", nullable = true)
	private ProjectPackage projectPackage;

	@Size(max = 80)
	@Column(name = "preconditions", length = 80, nullable = true)
	private String preconditions;

	@Size(max = 80)
	@Column(name = "postconditions", length = 80, nullable = true)
	private String postconditions;

	@Size(max = 80)
	@Column(name = "trigger_event", length = 80, nullable = true)
	private String triggerEvent;

	@Lob
	@Column(name = "comments", columnDefinition = "TEXT NULL", nullable = true)
	private String comments;

	@Column(name = "percent_complete", nullable = true)
	private Long percentComplete;

	@ManyToOne
	private Project project;

	@Lob
	@Column(name = "issues", columnDefinition = "TEXT NULL", nullable = true)
	private String issues;

	@ManyToOne
	private Feature feature;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(name = "basicflow_id", nullable = true)
	private Flow basicFlow;
	
	@OneToOne
	@JoinColumn(name = "extendedflow_id", nullable = true)
	private Flow extendedFlow;

	@OneToMany(mappedBy = "useCase", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Flow> alternativeFlows;

	@IndexedEmbedded
	@OneToMany(mappedBy = "useCase", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<UseCaseRule> useCaseRules;

	@ManyToOne
	private Iteration iteration;

	@Transient
	private String complexityLabel;

	/**
	 * Default constructor
	 */
	public UseCase() {
		super();
		init();
	}

	/**
	 * Identifier constructor
	 */
	public UseCase(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
		init();
	}

	/**
	 * class init method
	 */
	private void init() {
		this.statusType = StatusType.New;
		this.basicFlow = new Flow(true);
		this.alternativeFlows = new ArrayList<Flow>();
		this.useCaseRules = new ArrayList<UseCaseRule>();

	}

	/**
	 * PrePersist method
	 */
	@PrePersist
	public void prePersist() {
		if (this.modifiedBy == null) {
			this.modifiedBy = Literal.APPNAME.toString();
		}
		this.modifiedDate = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * PreUpdate method
	 */
	@PreUpdate
	public void preUpdate() {
		if (this.modifiedBy == null) {
			this.modifiedBy = Literal.APPNAME.toString();
		}
		this.modifiedDate = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Post load
	 */
	@PostLoad
	public void postLoad() {
		updateComplexityLabel();
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
	 * @return the objective
	 */
	public String getObjectiveAbbrv() {
		return StringUtils.abbreviate(objective, 50);
	}

	/**
	 * @param objective
	 *            the objective to set
	 */
	public void setObjective(String objective) {
		this.objective = objective;
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
	 * @return the complexity
	 */
	public Integer getComplexity() {
		return complexity;
	}

	/**
	 * @return the complexityLabel
	 */
	public String getComplexityLabel() {
		return complexityLabel;
	}

	/**
	 * @param complexityLabel
	 *            the complexityLabel to set
	 */
	public void setComplexityLabel(String complexityLabel) {
		this.complexityLabel = complexityLabel;
	}

	/**
	 * String representation of complexity weight value
	 * 
	 * @return string
	 */
	public String complexityLabel() {
		return complexityLabel;
	}

	/**
	 * @param complexity
	 *            the complexity to set
	 */
	public void setComplexity(Integer complexity) {
		this.complexity = complexity;
		updateComplexityLabel();
	}

	/**
	 * @return the preconditions
	 */
	public String getPreconditions() {
		return preconditions;
	}

	/**
	 * @param preconditions
	 *            the preconditions to set
	 */
	public void setPreconditions(String preconditions) {
		this.preconditions = preconditions;
	}

	/**
	 * @return the postconditions
	 */
	public String getPostconditions() {
		return postconditions;
	}

	/**
	 * @param postconditions
	 *            the postconditions to set
	 */
	public void setPostconditions(String postconditions) {
		this.postconditions = postconditions;
	}

	/**
	 * @return the triggerEvent
	 */
	public String getTriggerEvent() {
		return triggerEvent;
	}

	/**
	 * @param triggerEvent
	 *            the triggerEvent to set
	 */
	public void setTriggerEvent(String triggerEvent) {
		this.triggerEvent = triggerEvent;
	}

	/**
	 * @return the primaryActor
	 */
	public Actor getPrimaryActor() {
		return primaryActor;
	}

	/**
	 * @param primaryActor
	 *            the primaryActor to set
	 */
	public void setPrimaryActor(Actor primaryActor) {
		this.primaryActor = primaryActor;
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
	 * @return the percentComplete
	 */
	public Long getPercentComplete() {
		return percentComplete;
	}

	/**
	 * @param percentComplete
	 *            the percentComplete to set
	 */
	public void setPercentComplete(Long percentComplete) {
		this.percentComplete = percentComplete;
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
	 * @return the issues
	 */
	public String getIssues() {
		return issues;
	}

	/**
	 * @param issues
	 *            the issues to set
	 */
	public void setIssues(String issues) {
		this.issues = issues;
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
	 * @return the basicFlow
	 */
	public Flow getBasicFlow() {
		return basicFlow;
	}

	/**
	 * @param basicFlow
	 *            the basicFlow to set
	 */
	public void setBasicFlow(Flow basicFlow) {
		this.basicFlow = basicFlow;
	}

	/**
	 * @return the alternativeFlows
	 */
	public List<Flow> getAlternativeFlows() {
		return alternativeFlows;
	}

	/**
	 * Add flow
	 * 
	 * @param flow
	 */
	public void addAlternativeFlow(Flow flow) {
		flow.setUseCase(this);
		this.alternativeFlows.add(flow);
	}
	
	/**
	 * Remove flow
	 * 
	 * @param flow
	 */
	public void removeAlternativeFlow(Flow flow) {
		flow.setUseCase(null);
		this.alternativeFlows.remove(flow);
	}

	/**
	 * @return the useCaseRules
	 */
	public List<UseCaseRule> getUseCaseRules() {
		return useCaseRules;
	}

	/**
	 * Add business rule
	 * 
	 * @param useCaseRule
	 *            the UseCaseRule to add
	 */
	public void addUseCaseRule(UseCaseRule useCaseRule) {
		useCaseRule.setUseCase(this);
		this.useCaseRules.add(useCaseRule);
	}
	
	/**
	 * Remove business rule
	 * 
	 * @param useCaseRule
	 *            the UseCaseRule to remove
	 */
	public void removeUseCaseRule(UseCaseRule useCaseRule) {
		useCaseRule.setUseCase(null);
		this.useCaseRules.remove(useCaseRule);
	}
	
	/**
	 * Get number of flows (basic flow + alternative flows)
	 * @return total number of flows
	 */
	public int getNumFlows() {
		return this.alternativeFlows.size() + 1;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_USECASE.toString(), this.identifier);
	}

	/**
	 * Return identifier name string of the form UC1: name
	 * 
	 * @return identifier name
	 */
	public String getIdentifierNameAbbrv() {
		return getArtifact() + ": " + StringUtils.abbreviate(this.name, 25);
	}

	/**
	 * Return identifier name string of the form UC1: name
	 * 
	 * @return identifier name
	 */
	public String getIdentifierName() {
		return getArtifact() + ": " + this.name;
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
	 * @return the productRelease
	 */
	public ProductRelease getProductRelease() {
		return productRelease;
	}

	/**
	 * @param productRelease
	 *            the productRelease to set
	 */
	public void setProductRelease(ProductRelease productRelease) {
		this.productRelease = productRelease;
	}

	/**
	 * @return the extendedFlow
	 */
	public Flow getExtendedFlow() {
		return extendedFlow;
	}

	/**
	 * @param extendedFlow the extendedFlow to set
	 */
	public void setExtendedFlow(Flow extendedFlow) {
		this.extendedFlow = extendedFlow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UseCase [id=");
		builder.append(id);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", name=");
		builder.append(name);
		builder.append(", objective=");
		builder.append(objective);
		builder.append(", preconditions=");
		builder.append(preconditions);
		builder.append(", postconditions=");
		builder.append(postconditions);
		builder.append(", triggerEvent=");
		builder.append(triggerEvent);
		builder.append(", statusType=");
		builder.append(statusType);
		builder.append(", comments=");
		builder.append(comments);
		builder.append(", percentComplete=");
		builder.append(percentComplete);
		builder.append(", primaryActor=");
		builder.append(primaryActor);
		builder.append(", project=");
		builder.append(project);
		builder.append(", feature=");
		builder.append(feature);
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
		UseCase other = (UseCase) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * helper method
	 */
	private void updateComplexityLabel() {
		if (this.complexity != null) {
			switch (this.complexity) {
			case AVERAGE:
				complexityLabel = Literal.COMPLEXITY_AVERAGE.toString();
				break;
			case SIMPLE:
				complexityLabel = Literal.COMPLEXITY_SIMPLE.toString();
				break;
			case COMPLEX:
				complexityLabel = Literal.COMPLEXITY_COMPLEX.toString();
				break;
			}
		}
	}

}
