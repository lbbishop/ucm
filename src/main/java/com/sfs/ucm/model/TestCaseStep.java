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
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.TestResultType;

/**
 * TestCaseStep
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name="testcasestep")
public class TestCaseStep extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "step_number")
	private Short stepNumber;

	@NotNull
	@OneToOne
	@JoinColumn(name = "actor_id", nullable = false)
	private Actor actor;

	@Lob
	@Column(name = "action_description", columnDefinition = "TEXT")
	private String actionDescription;

	@Column(name = "expected_results", columnDefinition = "TEXT NULL", nullable = true)
	private String expectedResults;

	@Column(name = "notes", columnDefinition = "TEXT NULL", nullable = true)
	private String notes;

	@Enumerated(EnumType.STRING)
	@Column(name = "testresult_type", nullable = true)
	private TestResultType testResultType;

	@ManyToOne(optional = false)
	@JoinColumn(name = "testcase_id")
	private TestCase testCase;

	/**
	 * Default constructor
	 */
	public TestCaseStep() {
		super();
		init();
	}

	/**
	 * Constructor
	 * @param stepNumber
	 * @param actorName
	 * @param actionDescription
	 */
	public TestCaseStep(short stepNumber, Actor actor, String actionDescription) {
		super();		
		init();
		this.stepNumber = stepNumber;
		this.actor = actor;
		this.actionDescription = actionDescription;
	}
	
	
	/**
	 * Constructor
	 * @param stepNumber
	 * @param flowstep
	 */
	public TestCaseStep(short stepNumber, FlowStep flowStep) {
		super();		
		init();
		this.stepNumber = stepNumber;
		this.actor = flowStep.getActor();
		this.actionDescription = flowStep.getActionDescription();
		
		// embed business rules into action description
		for (FlowStepRule flowStepRule : flowStep.getFlowStepRules()) {
			this.actionDescription += "<br/>";
			this.actionDescription += "<br/>";
			this.actionDescription += "<b>Business Rule: " + flowStepRule.getArtifact() + "</b><br/>";
			this.actionDescription += flowStepRule.getRule();
		}
	}
	
	private void init() {
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
	 * @return the stepNumber
	 */
	public Short getStepNumber() {
		return stepNumber;
	}

	/**
	 * @param stepNumber
	 *            the stepNumber to set
	 */
	public void setStepNumber(Short stepNumber) {
		this.stepNumber = stepNumber;
	}

	/**
	 * @return the actionDescription
	 */
	public String getActionDescription() {
		return actionDescription;
	}

	/**
	 * @param actionDescription
	 *            the actionDescription to set
	 */
	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}

	/**
	 * @return the actor
	 */
	public Actor getActor() {
		return actor;
	}

	/**
	 * @param actor
	 *            the actor to set
	 */
	public void setActor(Actor actor) {
		this.actor = actor;
	}

	/**
	 * @return the testCase
	 */
	public TestCase getTestCase() {
		return testCase;
	}

	/**
	 * @param testCase
	 *            the testCase to set
	 */
	public void setTestCase(TestCase testCase) {
		this.testCase = testCase;
	}

	/**
	 * @return the expectedResults
	 */
	public String getExpectedResults() {
		return expectedResults;
	}

	/**
	 * @param expectedResults
	 *            the expectedResults to set
	 */
	public void setExpectedResults(String expectedResults) {
		this.expectedResults = expectedResults;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TestCaseStep [stepNumber=");
		builder.append(stepNumber);
		builder.append(", actor=");
		builder.append(actor);
		builder.append(", actionDescription=");
		builder.append(actionDescription);
		builder.append(", expectedResults=");
		builder.append(expectedResults);
		builder.append(", notes=");
		builder.append(notes);
		builder.append(", testResult=");
		builder.append(testResultType);
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
		result = prime * result + stepNumber;
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
		TestCaseStep other = (TestCaseStep) obj;
		if (stepNumber != other.stepNumber)
			return false;
		return true;
	}

}
