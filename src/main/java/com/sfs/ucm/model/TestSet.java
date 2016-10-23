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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * TestSet
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "testset")
public class TestSet extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Name is required")
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@NotNull(message = "Tester is required")
	@OneToOne
	@JoinColumn(name = "tester_id", nullable = false)
	private ProjectMember tester;

	@ManyToOne(optional = false)
	@JoinColumn(name = "testplan_id")
	private TestPlan testPlan;

	@OneToMany(mappedBy = "testSet", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<SpecificationTest> specificationTests;

	@OneToMany(mappedBy = "testSet", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	@NotAudited
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private Collection<TestCase> testCases;

	@OneToMany(mappedBy = "testSet", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<SpecificationRuleTest> specificationRuleTests;

	@OneToMany(mappedBy = "testSet", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<UseCaseRuleTest> useCaseRuleTests;

	/**
	 * Default constructor
	 */
	public TestSet() {
		super();
		init();
	}

	/**
	 * Identifier constructor
	 */
	public TestSet(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param identifier
	 * @param tester
	 */
	public TestSet(int identifier, ProjectMember tester) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		this.tester = tester;

	}

	/**
	 * Constructor
	 * 
	 * @param identifier
	 * @param name
	 * @param tester
	 */
	public TestSet(int identifier, String name, ProjectMember tester) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		this.name = name;
		this.tester = tester;

	}

	/**
	 * class init method
	 */
	private void init() {
		this.tester = new ProjectMember();
		this.specificationTests = new HashSet<SpecificationTest>();
		this.testCases = new HashSet<TestCase>();
		this.specificationRuleTests = new HashSet<SpecificationRuleTest>();
		this.useCaseRuleTests = new HashSet<UseCaseRuleTest>();
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
	 * @return the testPlan
	 */
	public TestPlan getTestPlan() {
		return testPlan;
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
	 * @param testPlan
	 *            the testPlan to set
	 */
	public void setTestPlan(TestPlan testPlan) {
		this.testPlan = testPlan;
	}

	/**
	 * Add test case
	 * 
	 * @param testCase
	 *            the testCase to add
	 */
	public void addTestCase(TestCase testCase) {
		testCase.setTestSet(this);
		this.testCases.add(testCase);
	}

	/**
	 * Remove test case
	 * 
	 * @param testCase
	 *            the testCase to remove
	 */
	public void removeTestCase(TestCase testCase) {
		testCase.setTestSet(null);
		this.testCases.remove(testCase);
	}

	/**
	 * @return the testCases
	 */
	public Collection<TestCase> getTestCases() {
		return testCases;
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
	 * @return the specificationRuleTests
	 */
	public Collection<SpecificationRuleTest> getSpecificationRuleTests() {
		return specificationRuleTests;
	}

	/**
	 * Add specification rule test
	 * 
	 * @param specificationRuleTest
	 *            the specificationRuleTests to add
	 */
	public void addSpecificationRuleTest(SpecificationRuleTest specificationRuleTest) {
		specificationRuleTest.setTestSet(this);
		this.specificationRuleTests.add(specificationRuleTest);
	}

	/**
	 * Remove specification rule test
	 * 
	 * @param specificationRuleTest
	 *            the specificationRuleTests to remove
	 */
	public void removeSpecificationRuleTest(SpecificationRuleTest specificationRuleTest) {
		specificationRuleTest.setTestSet(null);
		this.specificationRuleTests.remove(specificationRuleTest);
	}

	/**
	 * @return the useCaseRuleTests
	 */
	public Collection<UseCaseRuleTest> getUseCaseRuleTests() {
		return useCaseRuleTests;
	}

	/**
	 * Add use case rule test
	 * 
	 * @param useCaseRuleTest
	 *            the useCaseRuleTest to add
	 */
	public void addUseCaseRuleTest(UseCaseRuleTest useCaseRuleTest) {
		useCaseRuleTest.setTestSet(this);
		this.useCaseRuleTests.add(useCaseRuleTest);
	}

	/**
	 * Remove use case rule test
	 * 
	 * @param useCaseRuleTest
	 *            the useCaseRuleTest to remove
	 */
	public void removeUseCaseRuleTest(UseCaseRuleTest useCaseRuleTest) {
		useCaseRuleTest.setTestSet(null);
		this.useCaseRuleTests.remove(useCaseRuleTest);
	}

	/**
	 * @return the specificationTests
	 */
	public Collection<SpecificationTest> getSpecificationTests() {
		return specificationTests;
	}

	/**
	 * Add specification test
	 * 
	 * @param specificationTest
	 *            the specificationTests to add
	 */
	public void addSpecificationTest(SpecificationTest specificationTest) {
		specificationTest.setTestSet(this);
		this.specificationTests.add(specificationTest);
	}

	/**
	 * Remove specification test
	 * 
	 * @param specificationTest
	 *            the specificationTests to remove
	 */
	public void removeSpecificationTest(SpecificationTest specificationTest) {
		specificationTest.setTestSet(null);
		this.specificationTests.remove(specificationTest);
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_TESTSET.toString(), this.identifier);
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
		TestSet other = (TestSet) obj;
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
		builder.append("TestSet [id=");
		builder.append(id);
		builder.append(", tester=");
		builder.append(tester);
		builder.append("]");
		return builder.toString();
	}

}
