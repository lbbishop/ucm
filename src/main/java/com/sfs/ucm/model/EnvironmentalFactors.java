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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Constants;
import com.sfs.ucm.data.Literal;

/**
 * Project estimation environmental factors
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name="envfactors")
public class EnvironmentalFactors extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// environmental factors
	@NotNull(message = "EF1 is required")
	@Column(nullable = false)
	private Integer ef1;

	@NotNull(message = "EF2 is required")
	@Column(nullable = false)
	private Integer ef2;

	@NotNull(message = "EF3 is required")
	@Column(nullable = false)
	private Integer ef3;

	@NotNull(message = "EF4 is required")
	@Column(nullable = false)
	private Integer ef4;

	@NotNull(message = "EF5 is required")
	@Column(nullable = false)
	private Integer ef5;

	@NotNull(message = "EF6 is required")
	@Column(nullable = false)
	private Integer ef6;

	@NotNull(message = "EF7 is required")
	@Column(nullable = false)
	private Integer ef7;

	@NotNull(message = "EF8 is required")
	@Column(nullable = false)
	private Integer ef8;

	@OneToOne(mappedBy = "environmentalFactors")
	private Project project;

	/**
	 * Default constructor
	 */
	public EnvironmentalFactors() {
		super();
		init();
	}

	/**
	 * init method
	 */
	private void init() {

		this.ef1 = Constants.ECF_IMPACT_NOIMPACT;
		this.ef2 = Constants.ECF_IMPACT_NOIMPACT;
		this.ef3 = Constants.ECF_IMPACT_NOIMPACT;
		this.ef4 = Constants.ECF_IMPACT_NOIMPACT;
		this.ef5 = Constants.ECF_IMPACT_NOIMPACT;
		this.ef6 = Constants.ECF_IMPACT_NOIMPACT;
		this.ef7 = Constants.ECF_IMPACT_NOIMPACT;
		this.ef8 = Constants.ECF_IMPACT_NOIMPACT;

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
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the ef1
	 */
	public Integer getEf1() {
		return ef1;
	}

	/**
	 * @param ef1
	 *            the ef1 to set
	 */
	public void setEf1(Integer ef1) {
		this.ef1 = ef1;
	}

	/**
	 * @return the ef2
	 */
	public Integer getEf2() {
		return ef2;
	}

	/**
	 * @param ef2
	 *            the ef2 to set
	 */
	public void setEf2(Integer ef2) {
		this.ef2 = ef2;
	}

	/**
	 * @return the ef3
	 */
	public Integer getEf3() {
		return ef3;
	}

	/**
	 * @param ef3
	 *            the ef3 to set
	 */
	public void setEf3(Integer ef3) {
		this.ef3 = ef3;
	}

	/**
	 * @return the ef4
	 */
	public Integer getEf4() {
		return ef4;
	}

	/**
	 * @param ef4
	 *            the ef4 to set
	 */
	public void setEf4(Integer ef4) {
		this.ef4 = ef4;
	}

	/**
	 * @return the ef5
	 */
	public Integer getEf5() {
		return ef5;
	}

	/**
	 * @param ef5
	 *            the ef5 to set
	 */
	public void setEf5(Integer ef5) {
		this.ef5 = ef5;
	}

	/**
	 * @return the ef6
	 */
	public Integer getEf6() {
		return ef6;
	}

	/**
	 * @param ef6
	 *            the ef6 to set
	 */
	public void setEf6(Integer ef6) {
		this.ef6 = ef6;
	}

	/**
	 * @return the ef7
	 */
	public Integer getEf7() {
		return ef7;
	}

	/**
	 * @param ef7
	 *            the ef7 to set
	 */
	public void setEf7(Integer ef7) {
		this.ef7 = ef7;
	}

	/**
	 * @return the ef8
	 */
	public Integer getEf8() {
		return ef8;
	}

	/**
	 * @param ef8
	 *            the ef8 to set
	 */
	public void setEf8(Integer ef8) {
		this.ef8 = ef8;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnvironmentalFactors [id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

}
