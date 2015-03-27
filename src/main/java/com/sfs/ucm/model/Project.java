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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.ProjectStatusType;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "project_status_type", nullable = true)
	private ProjectStatusType projectStatusType;

	@NotNull
	@Column(name = "visible_to_public", nullable = false)
	private Boolean visibleToPublic;

	@Size(max = 255)
	@Column(name = "approved_by", length = 255, nullable = true)
	private String approvedBy;

	@OneToOne
	@JoinColumn(name = "defaultrelease_id", nullable = true)
	private ProductRelease defaultRelease;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Actor> actors;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<ProjectMember> projectMembers;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<ProjectPackage> projectPackages;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<DesignConstraint> designConstraints;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Feature> features;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Setting> settings;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<GlossaryTerm> glossaryTerms;

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
	private List<Requirement> requirements;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Risk> risks;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<UserStory> userStories;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Estimate> estimates;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Stakeholder> stakeholders;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<StakeholderRequest> stakeholderRequests;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<UseCase> useCases;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<ProductRelease> productReleases;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<BusinessProcess> businessProcesses;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Iteration> iterations;

	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Resource> resources;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Task> tasks;

	@IndexedEmbedded
	@OneToMany(mappedBy = "project", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Note> notes;

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
		this.projectStatusType = ProjectStatusType.Proposed;
	}

	/**
	 * class init method
	 */
	private void init() {
		this.projectStatusType = ProjectStatusType.Proposed;
		this.visibleToPublic = false;

		this.projectMembers = new ArrayList<ProjectMember>();
		this.projectPackages = new ArrayList<ProjectPackage>();
		this.actors = new ArrayList<Actor>();
		this.designConstraints = new ArrayList<DesignConstraint>();
		this.features = new ArrayList<Feature>();
		this.glossaryTerms = new ArrayList<GlossaryTerm>();
		this.requirements = new ArrayList<Requirement>();
		this.productReleases = new ArrayList<ProductRelease>();
		this.risks = new ArrayList<Risk>();
		this.userStories = new ArrayList<UserStory>();
		this.estimates = new ArrayList<Estimate>();
		this.stakeholders = new ArrayList<Stakeholder>();
		this.stakeholderRequests = new ArrayList<StakeholderRequest>();
		this.useCases = new ArrayList<UseCase>();
		this.businessProcesses = new ArrayList<BusinessProcess>();
		this.iterations = new ArrayList<Iteration>();
		this.tasks = new ArrayList<Task>();
		this.resources = new ArrayList<Resource>();
		this.settings = new ArrayList<Setting>();
		this.open = true;
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
	public List<ProjectMember> getProjectMembers() {
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
	public List<ProjectPackage> getProjectPackages() {
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
	 * Add Requirement to Project
	 * 
	 * @param Requirement
	 *            the Requirement to add
	 */
	public void addRequirement(Requirement requirement) {
		requirement.setProject(this);
		this.requirements.add(requirement);
	}

	/**
	 * Remove Requirement
	 * 
	 * @param Requirement
	 *            the Requirement
	 */
	public void removeRequirement(Requirement requirement) {
		requirement.setProject(null);
		this.requirements.remove(requirement);
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
	public List<Setting> getSettings() {
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
	 * @return the userStories
	 */
	public List<UserStory> getUserStories() {
		return userStories;
	}

	/**
	 * @param userStory
	 *            the userStory to add
	 */
	public void addUserStory(UserStory userStory) {
		userStory.setProject(this);
		this.userStories.add(userStory);
	}

	/**
	 * @param userStory
	 *            the userStory to remove
	 */
	public void removeUserStory(UserStory userStory) {
		userStory.setProject(null);
		this.userStories.remove(userStory);
	}

	/**
	 * @return the risks
	 */
	public List<Risk> getRisks() {
		return risks;
	}

	/**
	 * @return the features
	 */
	public List<Feature> getFeatures() {
		return features;
	}

	/**
	 * @return the requirements
	 */
	public List<Requirement> getRequirements() {
		return requirements;
	}

	/**
	 * @return the stakeholders
	 */
	public List<Stakeholder> getStakeholders() {
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
	public List<StakeholderRequest> getStakeholderRequests() {
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
	public List<DesignConstraint> getDesignConstraints() {
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
	 * @return the defaultRelease
	 */
	public ProductRelease getDefaultRelease() {
		return defaultRelease;
	}

	/**
	 * @param defaultRelease
	 *            the defaultRelease to set
	 */
	public void setDefaultRelease(ProductRelease defaultRelease) {
		this.defaultRelease = defaultRelease;
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
	public List<Actor> getActors() {
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
	 * @return the businessProcesses
	 */
	public List<BusinessProcess> getBusinessProcesses() {
		return businessProcesses;
	}

	/**
	 * Add business process
	 * 
	 * @param businessProcess
	 */
	public void addBusinessProcess(BusinessProcess businessProcess) {
		businessProcess.setProject(this);
		this.businessProcesses.add(businessProcess);
	}

	/**
	 * Remove business process
	 * 
	 * @param businessProcess
	 */
	public void removeBusinessProcess(BusinessProcess businessProcess) {
		businessProcess.setProject(null);
		this.businessProcesses.remove(businessProcess);
	}

	/**
	 * Add Iteration
	 * 
	 * @param iteration
	 */
	public void addIteration(Iteration iteration) {
		iteration.setProject(this);
		this.iterations.add(iteration);
	}

	/**
	 * Remove Iteration
	 * 
	 * @param iteration
	 */
	public void removeIteration(Iteration iteration) {
		iteration.setProject(null);
		this.iterations.remove(iteration);
	}

	/**
	 * @return the iterations
	 */
	public List<Iteration> getIterations() {
		return iterations;
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
	 * Add task
	 * 
	 * @param task
	 */
	public void addTask(Task task) {
		task.setProject(this);
		this.tasks.add(task);
	}

	/**
	 * Remove task
	 * 
	 * @param task
	 */
	public void removeTask(Task task) {
		task.setProject(null);
		this.tasks.remove(task);
	}

	/**
	 * @return the tasks
	 */
	public List<Task> getTasks() {
		return tasks;
	}

	/**
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return resources;
	}

	/**
	 * Add a model resource
	 * 
	 * @param resource
	 */
	public void addResource(Resource resource) {
		resource.setProject(this);
		this.resources.add(resource);
	}

	/**
	 * Remove a model resource
	 * 
	 * @param resource
	 */
	public void removeResource(Resource resource) {
		resource.setProject(null);
		this.resources.remove(resource);
	}

	/**
	 * @return the productReleases
	 */
	public List<ProductRelease> getProductReleases() {
		return productReleases;
	}

	/**
	 * Add Product Release
	 * 
	 * @param ProductRelease
	 */
	public void addProductRelease(ProductRelease productRelease) {
		productRelease.setProject(this);
		this.productReleases.add(productRelease);
	}

	/**
	 * Remove Product Release
	 * 
	 * @param ProductRelease
	 */
	public void removeProductRelease(ProductRelease productRelease) {
		productRelease.setProject(null);
		this.productReleases.remove(productRelease);
	}

	/**
	 * @return the useCases
	 */
	public List<UseCase> getUseCases() {
		return useCases;
	}

	/**
	 * @return the estimates
	 */
	public List<Estimate> getEstimates() {
		return estimates;
	}

	/**
	 * @param estimate
	 *            the estimate to add
	 */
	public void addEstimate(Estimate estimate) {
		estimate.setProject(this);
		this.estimates.add(estimate);
	}

	/**
	 * @param estimate
	 *            the estimate to remove
	 */
	public void removeEstimate(Estimate estimate) {
		estimate.setProject(null);
		this.estimates.remove(estimate);
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
	 * @return the projectStatusType
	 */
	public ProjectStatusType getProjectStatusType() {
		return projectStatusType;
	}

	/**
	 * @param projectStatusType
	 *            the projectStatusType to set
	 */
	public void setProjectStatusType(ProjectStatusType projectStatusType) {
		this.projectStatusType = projectStatusType;
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
	 * @return the approvedBy
	 */
	public String getApprovedBy() {
		return approvedBy;
	}

	/**
	 * @param approvedBy
	 *            the approvedBy to set
	 */
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	/**
	 * @return the glossary
	 */
	public List<GlossaryTerm> getGlossaryTerms() {
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
	 * @return the notes
	 */
	public List<Note> getNotes() {
		return notes;
	}

	/**
	 * @param note
	 *            the note to add
	 */
	public void addNote(Note note) {
		note.setProject(this);
		this.notes.add(note);
	}

	/**
	 * @param note
	 *            the note to remove
	 */
	public void removeNote(Note note) {
		note.setProject(null);
		this.notes.remove(note);
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
		builder.append(", projectStatusType=");
		builder.append(projectStatusType);
		builder.append(", approvedBy=");
		builder.append(approvedBy);
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
