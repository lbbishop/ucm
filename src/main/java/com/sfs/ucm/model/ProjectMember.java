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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * ProjectMember
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "projectmember")
public class ProjectMember extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "User is required")
	@OneToOne
	@JoinColumn(name = "authuser_id", nullable = false)
	private AuthUser authUser;

	@NotNull(message = "Primary Role is required")
	@Size(max = 30)
	@Column(name = "primary_role", length = 30, nullable = false)
	private String primaryRole;

	@Column(name = "project_open", nullable = true)
	private Boolean projectOpen;

	@Column(name = "project_collapsed", nullable = true)
	private Boolean projectCollapsed;

	@Column(name = "requirements_collapsed", nullable = true)
	private Boolean requirementsCollapsed;

	@Column(name = "usecases_collapsed", nullable = true)
	private Boolean useCasesCollapsed;

	@Column(name = "testplan_collapsed", nullable = true)
	private Boolean testPlanCollapsed;

	@Column(name = "testsets_collapsed", nullable = true)
	private Boolean testSetsCollapsed;

	@Column(name = "testcases_collapsed", nullable = true)
	private Boolean testCasesCollapsed;

	@Column(name = "projectmanagement_collapsed", nullable = true)
	private Boolean projectManagementCollapsed;

	@Column(name = "iteration_collapsed", nullable = true)
	private Boolean iterationCollapsed;

	@Column(name = "projectestimation_collapsed", nullable = true)
	private Boolean projectEstimationCollapsed;

	@ManyToOne
	private Project project;

	/**
	 * Default constructor
	 */
	public ProjectMember() {
		super();
		init();
	}

	/**
	 * Identifier constructor
	 */
	public ProjectMember(int identifier) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);

	}

	public ProjectMember(int identifier, AuthUser authUser) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		this.authUser = authUser;

	}

	/**
	 * class init method
	 */
	private void init() {
		this.authUser = new AuthUser();
		this.projectOpen = true;
		this.projectCollapsed = false;
		this.iterationCollapsed = true;
		this.projectEstimationCollapsed = true;
		this.projectManagementCollapsed = true;
		this.requirementsCollapsed = true;
		this.useCasesCollapsed = true;
		this.testCasesCollapsed = true;
		this.testSetsCollapsed = true;
		this.testPlanCollapsed = true;
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
	 * Post load
	 */
	@PostLoad
	public void postLoad() {
		if (this.projectOpen == null) {
			this.projectOpen = true;
		}
		if (this.projectCollapsed == null) {
			this.projectCollapsed = false;
		}
		
		if (this.requirementsCollapsed == null) {
			this.requirementsCollapsed = false;
		}
		if (this.projectEstimationCollapsed == null) {
			this.projectEstimationCollapsed = true;
		}
		if (this.testCasesCollapsed == null) {
			this.testCasesCollapsed = true;
		}
		if (this.testPlanCollapsed == null) {
			this.testPlanCollapsed = true;
		}
		if (this.testSetsCollapsed == null) {
			this.testSetsCollapsed = true;
		}
		if (this.useCasesCollapsed == null) {
			this.useCasesCollapsed = false;
		}
		if (this.projectManagementCollapsed == null) {
			this.projectManagementCollapsed = true;
		}
		if (this.iterationCollapsed == null) {
			this.iterationCollapsed = true;
		}
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
	 * @return the primaryRole
	 */
	public String getPrimaryRole() {
		return primaryRole;
	}

	/**
	 * @param primaryRole
	 *            the primaryRole to set
	 */
	public void setPrimaryRole(String primaryRole) {
		this.primaryRole = primaryRole;
	}

	/**
	 * @return the projectOpen
	 */
	public Boolean getProjectOpen() {
		return projectOpen;
	}

	/**
	 * @param projectOpen
	 *            the projectOpen to set
	 */
	public void setProjectOpen(Boolean projectOpen) {
		this.projectOpen = projectOpen;
	}

	/**
	 * @return the projectCollapsed
	 */
	public Boolean getProjectCollapsed() {
		return projectCollapsed;
	}

	/**
	 * @param projectCollapsed
	 *            the projectCollapsed to set
	 */
	public void setProjectCollapsed(Boolean projectCollapsed) {
		this.projectCollapsed = projectCollapsed;
	}

	/**
	 * @return the requirementsCollapsed
	 */
	public Boolean getRequirementsCollapsed() {
		return requirementsCollapsed;
	}

	/**
	 * @param requirementsCollapsed
	 *            the requirementsCollapsed to set
	 */
	public void setRequirementsCollapsed(Boolean requirementsCollapsed) {
		this.requirementsCollapsed = requirementsCollapsed;
	}

	/**
	 * @return the useCasesCollapsed
	 */
	public Boolean getUseCasesCollapsed() {
		return useCasesCollapsed;
	}

	/**
	 * @param useCasesCollapsed
	 *            the useCasesCollapsed to set
	 */
	public void setUseCasesCollapsed(Boolean useCasesCollapsed) {
		this.useCasesCollapsed = useCasesCollapsed;
	}

	/**
	 * @return the testPlanCollapsed
	 */
	public Boolean getTestPlanCollapsed() {
		return testPlanCollapsed;
	}

	/**
	 * @param testPlanCollapsed
	 *            the testPlanCollapsed to set
	 */
	public void setTestPlanCollapsed(Boolean testPlanCollapsed) {
		this.testPlanCollapsed = testPlanCollapsed;
	}

	/**
	 * @return the testSetsCollapsed
	 */
	public Boolean getTestSetsCollapsed() {
		return testSetsCollapsed;
	}

	/**
	 * @param testSetsCollapsed
	 *            the testSetsCollapsed to set
	 */
	public void setTestSetsCollapsed(Boolean testSetsCollapsed) {
		this.testSetsCollapsed = testSetsCollapsed;
	}

	/**
	 * @return the testCasesCollapsed
	 */
	public Boolean getTestCasesCollapsed() {
		return testCasesCollapsed;
	}

	/**
	 * @param testCasesCollapsed
	 *            the testCasesCollapsed to set
	 */
	public void setTestCasesCollapsed(Boolean testCasesCollapsed) {
		this.testCasesCollapsed = testCasesCollapsed;
	}

	/**
	 * @return the projectManagementCollapsed
	 */
	public Boolean getProjectManagementCollapsed() {
		return projectManagementCollapsed;
	}

	/**
	 * @param projectManagementCollapsed
	 *            the projectManagementCollapsed to set
	 */
	public void setProjectManagementCollapsed(Boolean projectManagementCollapsed) {
		this.projectManagementCollapsed = projectManagementCollapsed;
	}

	/**
	 * @return the iterationCollapsed
	 */
	public Boolean getIterationCollapsed() {
		return iterationCollapsed;
	}

	/**
	 * @param iterationCollapsed
	 *            the iterationCollapsed to set
	 */
	public void setIterationCollapsed(Boolean iterationCollapsed) {
		this.iterationCollapsed = iterationCollapsed;
	}

	/**
	 * @return the projectEstimationCollapsed
	 */
	public Boolean getProjectEstimationCollapsed() {
		return projectEstimationCollapsed;
	}

	/**
	 * @param projectEstimationCollapsed
	 *            the projectEstimationCollapsed to set
	 */
	public void setProjectEstimationCollapsed(Boolean projectEstimationCollapsed) {
		this.projectEstimationCollapsed = projectEstimationCollapsed;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_PROJECTMEMBER.toString(), this.identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectMember [id=");
		builder.append(id);
		builder.append(", authUser=");
		builder.append(authUser);
		builder.append(", primaryRole=");
		builder.append(primaryRole);
		builder.append(", projectOpen=");
		builder.append(projectOpen);
		builder.append(", projectCollapsed=");
		builder.append(projectCollapsed);
		builder.append(", requirementsCollapsed=");
		builder.append(requirementsCollapsed);
		builder.append(", useCasesCollapsed=");
		builder.append(useCasesCollapsed);
		builder.append(", testPlanCollapsed=");
		builder.append(testPlanCollapsed);
		builder.append(", testSetsCollapsed=");
		builder.append(testSetsCollapsed);
		builder.append(", testCasesCollapsed=");
		builder.append(testCasesCollapsed);
		builder.append(", projectManagementCollapsed=");
		builder.append(projectManagementCollapsed);
		builder.append(", iterationCollapsed=");
		builder.append(iterationCollapsed);
		builder.append(", projectEstimationCollapsed=");
		builder.append(projectEstimationCollapsed);
		builder.append("]");
		return builder.toString();
	}

}
