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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import com.sfs.ucm.data.Constants;

/**
 * Project entity
 * 
 * @author lbbishop
 * 
 */
@Entity
@Indexed
@Audited
@Table(name = "project")
@XmlRootElement
public class Project extends EntityBase implements Serializable {

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

	@Size(max = 255)
	@Column(name = "url", length = 255, nullable = true)
	private String url;

	@NotNull(message = "Security Marking is required")
	@Size(max = 50)
	@Column(name = "security_marking", length = 50, nullable = false)
	private String securityMarking;

	@NotNull
	@Column(name = "visible_to_public", nullable = false)
	private Boolean visibleToPublic;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<Actor> actors;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<ProjectMember> projectMembers;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<ProjectPackage> projectPackages;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<DesignConstraint> designConstraints;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<Feature> features;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<Setting> settings;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<GlossaryTerm> glossaryTerms;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(name = "productvision_id")
	private ProductVision productVision;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(name = "techfactors_id")
	private TechnicalFactors technicalFactors;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(name = "envfactors_id")
	private EnvironmentalFactors environmentalFactors;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<Specification> specifications;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<Risk> risks;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<Stakeholder> stakeholders;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<StakeholderRequest> stakeholderRequests;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<UseCase> useCases;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<Issue> issues;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@JoinColumn(name = "testplan_id")
	private TestPlan testPlan;

	@Column(nullable = false)
	private Boolean open;

	/**
	 * Default constructor
	 */
	public Project() {
		super();
		init();
	}

	/**
	 * @param name
	 * @param description
	 */
	public Project(String name, String description) {
		super();
		init();
		this.name = name;
		this.description = description;
	}

	/**
	 * class init method
	 */
	private void init() {
		this.visibleToPublic = false;

		this.projectMembers = new HashSet<ProjectMember>();
		this.projectPackages = new HashSet<ProjectPackage>();
		this.actors = new HashSet<Actor>();
		this.designConstraints = new HashSet<DesignConstraint>();
		this.features = new HashSet<Feature>();
		this.glossaryTerms = new HashSet<GlossaryTerm>();
		this.specifications = new HashSet<Specification>();
		this.risks = new HashSet<Risk>();
		this.stakeholders = new HashSet<Stakeholder>();
		this.stakeholderRequests = new HashSet<StakeholderRequest>();
		this.useCases = new HashSet<UseCase>();
		this.issues = new HashSet<Issue>();
		this.settings = new HashSet<Setting>();
		this.open = true;
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
	 * Add Feature to Project
	 * 
	 * @param feature
	 *            the feature to add
	 */
	public void addFeature(Feature feature) {
		feature.setProject(this);
		this.features.add(feature);
	}

	/**
	 * Remove Feature
	 * 
	 * @param feature
	 *            the feature to remove
	 */
	public void removeFeature(Feature feature) {
		feature.setProject(null);
		this.features.remove(feature);
	}

	/**
	 * @return the projectMembers
	 */
	public Collection<ProjectMember> getProjectMembers() {
		return projectMembers;
	}

	/**
	 * Add ProjectMember to Project
	 * 
	 * @param projectMember
	 *            the project member to add
	 */
	public void addProjectMember(ProjectMember projectMember) {
		projectMember.setProject(this);
		this.projectMembers.add(projectMember);
	}

	/**
	 * Remove ProjectMember
	 * 
	 * @param projectMember
	 *            the project member to remove
	 */
	public void removeProjectMember(ProjectMember projectMember) {
		projectMember.setProject(null);
		this.projectMembers.remove(projectMember);
	}

	/**
	 * @return the projectPackage
	 */
	public Collection<ProjectPackage> getProjectPackages() {
		return projectPackages;
	}

	/**
	 * Add ProjectPackage to Project
	 * 
	 * @param projectPackage
	 *            the project package to add
	 */
	public void addProjectPackage(ProjectPackage projectPackage) {
		projectPackage.setProject(this);
		this.projectPackages.add(projectPackage);
	}

	/**
	 * Remove ProjectPackage
	 * 
	 * @param projectPackage
	 *            the project package to remove
	 */
	public void removeProjectPackage(ProjectPackage projectPackage) {
		projectPackage.setProject(null);
		this.projectPackages.remove(projectPackage);
	}

	/**
	 * Add Risk to Project
	 * 
	 * @param risk
	 *            the risk to add
	 */
	public void addRisk(Risk risk) {
		risk.setProject(this);
		this.risks.add(risk);
	}

	/**
	 * Remove Risk
	 * 
	 * @param risk
	 *            the risk to remove
	 */
	public void removeRisk(Risk risk) {
		risk.setProject(null);
		this.risks.remove(risk);
	}

	/**
	 * Add Specification to Project
	 * 
	 * @param Specification
	 *            the Specification to add
	 */
	public void addSpecification(Specification specification) {
		specification.setProject(this);
		this.specifications.add(specification);
	}

	/**
	 * Remove Specification
	 * 
	 * @param Specification
	 *            the Specification
	 */
	public void removeSpecification(Specification specification) {
		specification.setProject(null);
		this.specifications.remove(specification);
	}

	/**
	 * Add GlossaryTerm to Project
	 * 
	 * @param GlossaryTerm
	 *            the GlossaryTerm to add
	 */
	public void addGlossaryTerm(GlossaryTerm glossaryTerm) {
		glossaryTerm.setProject(this);
		this.glossaryTerms.add(glossaryTerm);
	}

	/**
	 * Add Constraint to Project
	 * 
	 * @param constraint
	 *            the constraint to add
	 */
	public void addDesignConstraint(DesignConstraint designConstraint) {
		designConstraint.setProject(this);
		this.designConstraints.add(designConstraint);
	}

	/**
	 * Remove Constraint
	 * 
	 * @param constraint
	 *            the constraint to add
	 */
	public void removeDesignConstraint(DesignConstraint designConstraint) {
		designConstraint.setProject(null);
		this.designConstraints.remove(designConstraint);
	}

	/**
	 * Add UseCase to Project
	 * 
	 * @param useCase
	 *            the useCase to add
	 */
	public void addUseCase(UseCase useCase) {
		useCase.setProject(this);
		this.useCases.add(useCase);
	}

	/**
	 * Remove UseCase
	 * 
	 * @param useCase
	 *            the useCase to remove
	 */
	public void removeUseCase(UseCase useCase) {
		useCase.setProject(null);
		this.useCases.remove(useCase);
	}

	/**
	 * @return the settings
	 */
	public Collection<Setting> getSettings() {
		return settings;
	}

	/**
	 * Add a setting
	 * 
	 * @param setting
	 *            the setting to add
	 */
	public void addSettings(Setting setting) {
		setting.setProject(this);
		this.settings.add(setting);
	}

	/**
	 * Remove a setting
	 * 
	 * @param setting
	 *            the setting to remove
	 */
	public void removeSettings(Setting setting) {
		setting.setProject(null);
		this.settings.remove(setting);
	}

	/**
	 * @return the risks
	 */
	public Collection<Risk> getRisks() {
		return risks;
	}

	/**
	 * @return the features
	 */
	public Collection<Feature> getFeatures() {
		return features;
	}

	/**
	 * @return the specifications
	 */
	public Collection<Specification> getSpecifications() {
		return specifications;
	}

	/**
	 * @return the stakeholders
	 */
	public Collection<Stakeholder> getStakeholders() {
		return stakeholders;
	}

	/**
	 * Add Stakeholder to Project
	 * 
	 * @param stakeholder
	 *            the stakeholder to add
	 */
	public void addStakeholder(Stakeholder stakeholder) {
		stakeholder.setProject(this);
		this.stakeholders.add(stakeholder);
	}

	/**
	 * Remove Stakeholder
	 * 
	 * @param stakeholder
	 *            the stakeholder to remove
	 */
	public void removeStakeholder(Stakeholder stakeholder) {
		stakeholder.setProject(null);
		this.stakeholders.remove(stakeholder);
	}

	/**
	 * @return the stakeholderRequests
	 */
	public Collection<StakeholderRequest> getStakeholderRequests() {
		return stakeholderRequests;
	}

	/**
	 * Add StakeholderRequest to Project
	 * 
	 * @param stakeholderRequest
	 *            the stakeholderRequest to add
	 */
	public void addStakeholderRequest(StakeholderRequest stakeholderRequest) {
		stakeholderRequest.setProject(this);
		this.stakeholderRequests.add(stakeholderRequest);
	}

	/**
	 * Remove StakeholderRequest
	 * 
	 * @param stakeholderRequest
	 *            the stakeholderRequest to remove
	 */
	public void removeStakeholderRequest(StakeholderRequest stakeholderRequest) {
		stakeholderRequest.setProject(null);
		this.stakeholderRequests.remove(stakeholderRequest);
	}

	// /**
	// * @return the environment
	// */
	// public Environment getEnvironment() {
	// return environment;
	// }
	//
	// /**
	// * @param environment
	// * the environment to set
	// */
	// public void setEnvironment(Environment environment) {
	// this.environment = environment;
	// }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the designConstraints
	 */
	public Collection<DesignConstraint> getDesignConstraints() {
		return designConstraints;
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
	 * @return the description abbreviated
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
	 * @return the productVision
	 */
	public ProductVision getProductVision() {
		return productVision;
	}

	/**
	 * @param productVision
	 *            the productVision to set
	 */
	public void setProductVision(ProductVision productVision) {
		this.productVision = productVision;
		this.productVision.setProject(this);
	}

	/**
	 * @return the technicalFactors
	 */
	public TechnicalFactors getTechnicalFactors() {
		return technicalFactors;
	}

	/**
	 * @param technicalFactors
	 *            the technicalFactors to set
	 */
	public void setTechnicalFactors(TechnicalFactors technicalFactors) {
		this.technicalFactors = technicalFactors;
		this.technicalFactors.setProject(this);
	}

	/**
	 * @return the environmentalFactors
	 */
	public EnvironmentalFactors getEnvironmentalFactors() {
		return environmentalFactors;
	}

	/**
	 * @param environmentalFactors
	 *            the environmentalFactors to set
	 */
	public void setEnvironmentalFactors(EnvironmentalFactors environmentalFactors) {
		this.environmentalFactors = environmentalFactors;
		this.environmentalFactors.setProject(this);
	}

	/**
	 * @return the actors
	 */
	public Collection<Actor> getActors() {
		return actors;
	}

	/**
	 * Add Actor to Project
	 * 
	 * @param actor
	 *            the actor to add
	 */
	public void addActor(Actor actor) {
		actor.setProject(this);
		this.actors.add(actor);
	}

	/**
	 * Remove Actor
	 * 
	 * @param actor
	 *            the actor to remove
	 */
	public void removeActor(Actor actor) {
		actor.setProject(null);
		this.actors.remove(actor);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Add issue
	 * 
	 * @param issue
	 */
	public void addIssue(Issue issue) {
		issue.setProject(this);
		this.issues.add(issue);
	}

	/**
	 * Remove issue
	 * 
	 * @param issue
	 */
	public void removeIssue(Issue issue) {
		issue.setProject(null);
		this.issues.remove(issue);
	}

	/**
	 * @return the issues
	 */
	public Collection<Issue> getIssues() {
		return issues;
	}

	/**
	 * @return the useCases
	 */
	public Collection<UseCase> getUseCases() {
		return useCases;
	}

	/**
	 * @return the securityMarking
	 */
	public String getSecurityMarking() {
		return securityMarking;
	}

	/**
	 * @param securityMarking
	 *            the securityMarking to set
	 */
	public void setSecurityMarking(String securityMarking) {
		this.securityMarking = securityMarking;
	}

	/**
	 * @return the visibleToPublic
	 */
	public Boolean getVisibleToPublic() {
		return visibleToPublic;
	}

	/**
	 * @param visibleToPublic
	 *            the visibleToPublic to set
	 */
	public void setVisibleToPublic(Boolean visibleToPublic) {
		this.visibleToPublic = visibleToPublic;
	}

	/**
	 * @return the glossary
	 */
	public Collection<GlossaryTerm> getGlossaryTerms() {
		return glossaryTerms;
	}

	/**
	 * Remove GlossaryTerm
	 * 
	 * @param glossaryTerm
	 */
	public void removeGlossaryTerm(GlossaryTerm glossaryTerm) {
		glossaryTerm.setProject(null);
		this.glossaryTerms.remove(glossaryTerm);
	}

	/**
	 * @return the open
	 */
	public Boolean getOpen() {
		return open;
	}

	/**
	 * @param open
	 *            the open to set
	 */
	public void setOpen(Boolean open) {
		this.open = open;
	}

	/**
	 * @return the testPlan
	 */
	public TestPlan getTestPlan() {
		return testPlan;
	}

	/**
	 * @param testPlan
	 *            the testPlan to set
	 */
	public void setTestPlan(TestPlan testPlan) {
		this.testPlan = testPlan;
		this.testPlan.setProject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Project [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", securityMarking=");
		builder.append(securityMarking);
		builder.append(", description=");
		builder.append(description);
		builder.append(", modifiedBy=");
		builder.append(modifiedBy);
		builder.append(", modifiedDate=");
		builder.append(modifiedDate);
		builder.append(", objectVersion=");
		builder.append(objectVersion);
		builder.append(", testPlan=");
		builder.append(testPlan);
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
		Project other = (Project) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
