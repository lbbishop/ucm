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
package com.sfs.ucm.bean;

import java.util.Date;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.TestResultType;
import com.sfs.ucm.data.TestType;
import com.sfs.ucm.util.ModelUtils;

/**
 * Represents a test coverage report record
 * 
 * @author lbbishop
 * 
 */
public class TestResult {

	private Integer identifier;

	private String artifact;

	private String testType;

	private String testSet;

	private String testName;

	private String tester;

	private Date testDate;

	private TestResultType testResultType;

	/**
	 * Data Constructor
	 * 
	 * @param identifier
	 * @param Object
	 *            object
	 * @param testName
	 * @param tester
	 * @param testDate
	 * @param testResultType
	 */
	public TestResult(Integer identifier, String testType, String testSet, String testName, String tester, Date testDate, TestResultType testResultType) {
		super();
		this.identifier = identifier;
		this.testType = testType;
		this.testSet = testSet;
		this.testName = testName;
		this.tester = tester;
		this.testDate = testDate;
		this.testResultType = testResultType;

		// artifact
		if (testType.equals(TestType.Requirement.toString())) {
			this.artifact = ModelUtils.buildArtifactIdentifier(Literal.PREFIX_SPECIFICATIONTEST.toString(), this.identifier);
		}
		else if (testType.equals(TestType.RequirementRule.toString())) {
			this.artifact = ModelUtils.buildArtifactIdentifier(Literal.PREFIX_SPECIFICATIONRULETEST.toString(), this.identifier);
		}
		else if (testType.equals(TestType.TestCase.toString())) {
			this.artifact = ModelUtils.buildArtifactIdentifier(Literal.PREFIX_TESTCASE.toString(), this.identifier);
		}
		else if (testType.equals(TestType.UseCaseRule.toString())) {
			this.artifact = ModelUtils.buildArtifactIdentifier(Literal.PREFIX_USECASERULE.toString(), this.identifier);
		}
	}

	/**
	 * @return the identifier
	 */
	public Integer getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(Integer identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the testType
	 */
	public String getTestType() {
		return testType;
	}

	/**
	 * @param testType
	 *            the testType to set
	 */
	public void setTestType(String testType) {
		this.testType = testType;
	}

	/**
	 * @return the testName
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * @param testName
	 *            the testName to set
	 */
	public void setTestName(String testName) {
		this.testName = testName;
	}

	/**
	 * @return the tester
	 */
	public String getTester() {
		return tester;
	}

	/**
	 * @param tester
	 *            the tester to set
	 */
	public void setTester(String tester) {
		this.tester = tester;
	}

	/**
	 * @return the testDate
	 */
	public Date getTestDate() {
		return testDate;
	}

	/**
	 * @param testDate
	 *            the testDate to set
	 */
	public void setTestDate(Date testDate) {
		this.testDate = testDate;
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
	 * @return the artifact
	 */
	public String getArtifact() {
		return artifact;
	}

	/**
	 * @param artifact
	 *            the artifact to set
	 */
	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}

	/**
	 * @return the testSet
	 */
	public String getTestSet() {
		return testSet;
	}

	/**
	 * @param testSet
	 *            the testSet to set
	 */
	public void setTestSet(String testSet) {
		this.testSet = testSet;
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
		result = prime * result + ((testName == null) ? 0 : testName.hashCode());
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
		TestResult other = (TestResult) obj;
		if (testName == null) {
			if (other.testName != null)
				return false;
		}
		else if (!testName.equals(other.testName))
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
		builder.append("TestResult [identifier=");
		builder.append(identifier);
		builder.append(", testType=");
		builder.append(testType);
		builder.append(", testName=");
		builder.append(testName);
		builder.append(", tester=");
		builder.append(tester);
		builder.append(", testDate=");
		builder.append(testDate);
		builder.append(", testResultType=");
		builder.append(testResultType);
		builder.append("]");
		return builder.toString();
	}

}
