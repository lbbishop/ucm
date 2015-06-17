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

import javax.persistence.Basic;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.TestResultType;
import com.sfs.ucm.util.ModelUtils;

/**
 * TestCase
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "testcase")
public class TestCase extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Name is required")
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@OneToOne
	@JoinColumn(name = "usecase_id", nullable = true)
	private UseCase useCase;

	@Size(max = 255)
	@Column(name = "description", length = 255, nullable = true)
	private String description;

	@Lob
	@Column(name = "notes", columnDefinition = "TEXT NULL", nullable = true)
	private String notes;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "test_date", nullable = true)
	private java.util.Date testDate;

	@Lob
	@Column(name = "input_data", columnDefinition = "TEXT NULL", nullable = true)
	private String inputData;

	@OneToOne
	@JoinColumn(name = "tester_id", nullable = true)
	private ProjectMember tester;

	@OneToMany(mappedBy = "testCase", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<TestCaseStep> testCaseSteps;

	@Enumerated(EnumType.STRING)
	@Column(name = "testresult_type", nullable = true)
	private TestResultType testResultType;

	@Type(type = "org.hibernate.type.YesNoType")
	@Column(name = "processed", nullable = true)
	private Boolean processed;

	@ManyToOne(optional = false)
	@JoinColumn(name = "testset_id")
	private TestSet testSet;

	/**
	 * Constructor
	 * 
	 */
	public TestCase() {
		super();
		init();
	}

	/**
	 * Data Constructor
	 * <p>
	 * sets processed flag
	 * 
	 * @param identifier
	 * 
	 */
	public TestCase(int identifier) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		this.processed = true;
	}

	/**
	 * Resource initialization
	 */
	private void init() {
		this.testCaseSteps = new ArrayList<TestCaseStep>();
		this.tester = new ProjectMember();
		this.testResultType = TestResultType.Unknown;
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
	 * @return the testCaseSteps
	 */
	public List<TestCaseStep> getTestCaseSteps() {
		return testCaseSteps;
	}

	/**
	 * 
	 * @return number of test case steps
	 */
	public int getNumTestCaseSteps() {
		return this.testCaseSteps.size();
	}

	/**
	 * @return the testSet
	 */
	public TestSet getTestSet() {
		return testSet;
	}

	/**
	 * @param testSet
	 *            the testSet to set
	 */
	public void setTestSet(TestSet testSet) {
		this.testSet = testSet;
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
	 * Return identifier name string of the form UC1: name
	 * 
	 * @return identifier name
	 */
	public String getIdentifierNameAbbrv() {
		return getArtifact() + ": " + StringUtils.abbreviate(this.name, 40);
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
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the inputData
	 */
	public String getInputData() {
		return inputData;
	}

	/**
	 * @param inputData
	 *            the inputData to set
	 */
	public void setInputData(String inputData) {
		this.inputData = inputData;
	}

	/**
	 * @return the tester
	 */
	public ProjectMember getTester() {
		return tester;
	}

	/**
	 * @param tester
	 *            the tester to set
	 */
	public void setTester(ProjectMember tester) {
		this.tester = tester;
	}

	/**
	 * Add test case step to test case
	 * 
	 * @param testCaseStep
	 *            the testCaseStep to add
	 */
	public void addTestCaseStep(TestCaseStep testCaseStep) {
		testCaseStep.setTestCase(this);
		this.testCaseSteps.add(testCaseStep);
	}

	/**
	 * Remove test case step to test case
	 * 
	 * @param testCaseStep
	 *            the testCaseStep to remove
	 */
	public void removeTestCaseStep(TestCaseStep testCaseStep) {
		testCaseStep.setTestCase(null);
		this.testCaseSteps.remove(testCaseStep);
	}

	/**
	 * @return the testResultType
	 */
	public TestResultType getTestResultType() {
		return testResultType;
	}

	/**
	 * @param testResultType
	 *            the testResultType to set
	 */
	public void setTestResultType(TestResultType testResultType) {
		this.testResultType = testResultType;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_TESTCASE.toString(), this.identifier);
	}

	/**
	 * Return identifier name string of the form SC1: name
	 * 
	 * @return identifier name
	 */
	public String getIdentifierName() {
		return getArtifact() + ": " + this.name;
	}

	/**
	 * @return the useCase
	 */
	public UseCase getUseCase() {
		return useCase;
	}

	/**
	 * @param useCase
	 *            the useCase to set
	 */
	public void setUseCase(UseCase useCase) {
		this.useCase = useCase;
	}

	/**
	 * @return the testDate
	 */
	public java.util.Date getTestDate() {
		return testDate;
	}

	/**
	 * @param testDate
	 *            the testDate to set
	 */
	public void setTestDate(java.util.Date testDate) {
		this.testDate = testDate;
	}

	/**
	 * @return the processed
	 */
	public Boolean getProcessed() {
		return processed;
	}

	/**
	 * @param processed
	 *            the processed to set
	 */
	public void setProcessed(Boolean processed) {
		this.processed = processed;
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
		TestCase other = (TestCase) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
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
		builder.append("TestCase [id=");
		builder.append(id);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", notes=");
		builder.append(notes);
		builder.append(", inputData=");
		builder.append(inputData);
		builder.append(", tester=");
		builder.append(tester);
		builder.append(", testResult=");
		builder.append(testResultType);
		builder.append("]");
		return builder.toString();
	}

}
