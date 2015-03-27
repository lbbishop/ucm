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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.TestResultType;
import com.sfs.ucm.util.ModelUtils;

/**
 * UseCaseRule Test
 * 
 * @author lbbishop
 * 
 */
@Table(name = "usecaseruletest")
@Entity
@Audited
public class UseCaseRuleTest extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@OneToOne
	@JoinColumn(name = "usecaserule_id", nullable=true)
	private UseCaseRule useCaseRule;

	@ManyToOne
	@JoinColumn(name = "testset_id")
	private TestSet testSet;

	@Enumerated(EnumType.STRING)
	@Column(name = "testresult_type", nullable = true)
	private TestResultType testResultType;

	/**
	 * Constructor
	 */
	public UseCaseRuleTest() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param identifier
	 */
	public UseCaseRuleTest(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
	}

	/**
	 * Constructor
	 * 
	 * @param identifier
	 * @param useCaseRule
	 */
	public UseCaseRuleTest(int identifier, UseCaseRule useCaseRule) {
		super();
		this.identifier = Integer.valueOf(identifier);
		this.useCaseRule = useCaseRule;
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
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {		
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_USECASERULETEST.toString(), this.identifier);
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
	 * @return the useCaseRule
	 */
	public UseCaseRule getUseCaseRule() {
		return useCaseRule;
	}

	/**
	 * @param useCaseRule
	 *            the useCaseRule to set
	 */
	public void setUseCaseRule(UseCaseRule useCaseRule) {
		this.useCaseRule = useCaseRule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UseCaseRuleTest [id=");
		builder.append(id);
		builder.append(", useCaseRule=");
		builder.append(useCaseRule);
		builder.append(", testResultType=");
		builder.append(testResultType);
		builder.append("]");
		return builder.toString();
	}

}
