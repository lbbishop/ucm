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
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.TestResultType;
import com.sfs.ucm.util.ModelUtils;

/**
 * Non-functional (Supplemental) Requirement test
 * 
 * @author lbbishop
 * 
 */
@Table(name = "requirementtest")
@Entity
@Audited
public class RequirementTest extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Size(max = 1000)
	@Column(name = "input", length = 1000, nullable = true)
	private String input;

	@Enumerated(EnumType.STRING)
	@Column(name = "testresult_type", nullable = true)
	private TestResultType testResultType;

	@Lob
	@Column(name = "notes", columnDefinition = "TEXT NULL", nullable = true)
	private String notes;

	@OneToOne
	@JoinColumn(name = "requirement_id", nullable = true)
	private Requirement requirement;

	@ManyToOne
	private TestSet testSet;

	/**
	 * Default constructor
	 */
	public RequirementTest() {
		super();
		init();
	}

	/**
	 * Constructor
	 */
	public RequirementTest(int identifier, Requirement requirement) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		this.requirement = requirement;

	}

	/**
	 * Identifier constructor
	 */
	public RequirementTest(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
		init();
	}

	/**
	 * class init method
	 */
	private void init() {

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
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_REQUIREMENTTEST.toString(), this.identifier);
	}

	/**
	 * @return the input
	 */
	public String getInput() {
		return input;
	}

	/**
	 * @param input
	 *            the input to set
	 */
	public void setInput(String input) {
		this.input = input;
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
	 * @return the requirement
	 */
	public Requirement getRequirement() {
		return requirement;
	}

	/**
	 * @param requirement
	 *            the requirement to set
	 */
	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
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

}
