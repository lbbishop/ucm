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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

/**
 * TestPlan
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "testplan")
public class TestPlan extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Description is required")
	@Lob
	@Column(name = "description", columnDefinition = "TEXT", nullable = false)
	private String description;

	@ManyToOne
	private Project project;

	@OneToMany(mappedBy = "testPlan", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<TestSet> testSets;

	@OneToMany(mappedBy = "testPlan", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<UnitTest> unitTests;

	/**
	 * Default constructor
	 */
	public TestPlan() {
		super();
		init();
	}

	/**
	 * Data constructor
	 */
	public TestPlan(String description) {
		super();
		init();
		this.description = description;
	}

	/**
	 * class init method
	 */
	private void init() {
		this.testSets = new HashSet<TestSet>();
		this.unitTests = new HashSet<UnitTest>();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * Add test set to test plan
	 * 
	 * @param testSet
	 *            the testSet to add
	 */
	public void addTestSet(TestSet testSet) {
		testSet.setTestPlan(this);
		this.testSets.add(testSet);
	}

	/**
	 * Remove test set
	 * 
	 * @param testSet
	 *            the testSet to remove
	 */
	public void removeTestSet(TestSet testSet) {
		testSet.setTestPlan(null);
		this.testSets.remove(testSet);
	}

	/**
	 * @return the testSets
	 */
	public Collection<TestSet> getTestSets() {
		return testSets;
	}

	/**
	 * @return the unitTests
	 */
	public Collection<UnitTest> getUnitTests() {
		return unitTests;
	}

	/**
	 * Add unit test to test plan
	 * 
	 * @param unitTest
	 *            the unitTest to add
	 */
	public void addUnitTest(UnitTest unitTest) {
		unitTest.setTestPlan(this);
		this.unitTests.add(unitTest);
	}

	/**
	 * Remove unit test
	 * 
	 * @param unitTest
	 *            the unitTest to remove
	 */
	public void removeUnitTest(UnitTest unitTest) {
		unitTest.setTestPlan(null);
		this.unitTests.remove(unitTest);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TestPlan [id=");
		builder.append(id);
		builder.append(", description=");
		builder.append(StringUtils.abbreviate(description, 80));
		builder.append("]");
		return builder.toString();
	}

}
