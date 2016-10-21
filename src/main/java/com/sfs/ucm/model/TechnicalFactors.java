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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Constants;

/**
 * Project estimation technical factors
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "techfactors")
public class TechnicalFactors extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// technical factors
	@NotNull(message = "TF1 is required")
	@Column(nullable = false)
	private Integer tf1;

	@NotNull(message = "TF2 is required")
	@Column(nullable = false)
	private Integer tf2;

	@NotNull(message = "TF3 is required")
	@Column(nullable = false)
	private Integer tf3;

	@NotNull(message = "TF4 is required")
	@Column(nullable = false)
	private Integer tf4;

	@NotNull(message = "TF5 is required")
	@Column(nullable = false)
	private Integer tf5;

	@NotNull(message = "TF6 is required")
	@Column(nullable = false)
	private Integer tf6;

	@NotNull(message = "TF7 is required")
	@Column(nullable = false)
	private Integer tf7;

	@NotNull(message = "TF8 is required")
	@Column(nullable = false)
	private Integer tf8;

	@NotNull(message = "TF9 is required")
	@Column(nullable = false)
	private Integer tf9;

	@NotNull(message = "TF10 is required")
	@Column(nullable = false)
	private Integer tf10;

	@NotNull(message = "TF11 is required")
	@Column(nullable = false)
	private Integer tf11;

	@NotNull(message = "TF12 is required")
	@Column(nullable = false)
	private Integer tf12;

	@NotNull(message = "TF13 is required")
	@Column(nullable = false)
	private Integer tf13;

	@OneToOne(mappedBy = "technicalFactors")
	private Project project;

	/**
	 * Default constructor
	 */
	public TechnicalFactors() {
		super();
		init();
	}

	/**
	 * init method
	 */
	private void init() {
		this.tf1 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf2 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf3 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf4 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf5 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf6 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf7 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf8 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf9 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf10 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf11 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf12 = Constants.TCF_COMPLEXITY_IRRELEVANT;
		this.tf13 = Constants.TCF_COMPLEXITY_IRRELEVANT;

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
	 * @return the tf1
	 */
	public Integer getTf1() {
		return tf1;
	}

	/**
	 * @param tf1
	 *            the tf1 to set
	 */
	public void setTf1(Integer tf1) {
		this.tf1 = tf1;
	}

	/**
	 * @return the tf2
	 */
	public Integer getTf2() {
		return tf2;
	}

	/**
	 * @param tf2
	 *            the tf2 to set
	 */
	public void setTf2(Integer tf2) {
		this.tf2 = tf2;
	}

	/**
	 * @return the tf3
	 */
	public Integer getTf3() {
		return tf3;
	}

	/**
	 * @param tf3
	 *            the tf3 to set
	 */
	public void setTf3(Integer tf3) {
		this.tf3 = tf3;
	}

	/**
	 * @return the tf4
	 */
	public Integer getTf4() {
		return tf4;
	}

	/**
	 * @param tf4
	 *            the tf4 to set
	 */
	public void setTf4(Integer tf4) {
		this.tf4 = tf4;
	}

	/**
	 * @return the tf5
	 */
	public Integer getTf5() {
		return tf5;
	}

	/**
	 * @param tf5
	 *            the tf5 to set
	 */
	public void setTf5(Integer tf5) {
		this.tf5 = tf5;
	}

	/**
	 * @return the tf6
	 */
	public Integer getTf6() {
		return tf6;
	}

	/**
	 * @param tf6
	 *            the tf6 to set
	 */
	public void setTf6(Integer tf6) {
		this.tf6 = tf6;
	}

	/**
	 * @return the tf7
	 */
	public Integer getTf7() {
		return tf7;
	}

	/**
	 * @param tf7
	 *            the tf7 to set
	 */
	public void setTf7(Integer tf7) {
		this.tf7 = tf7;
	}

	/**
	 * @return the tf8
	 */
	public Integer getTf8() {
		return tf8;
	}

	/**
	 * @param tf8
	 *            the tf8 to set
	 */
	public void setTf8(Integer tf8) {
		this.tf8 = tf8;
	}

	/**
	 * @return the tf9
	 */
	public Integer getTf9() {
		return tf9;
	}

	/**
	 * @param tf9
	 *            the tf9 to set
	 */
	public void setTf9(Integer tf9) {
		this.tf9 = tf9;
	}

	/**
	 * @return the tf10
	 */
	public Integer getTf10() {
		return tf10;
	}

	/**
	 * @param tf10
	 *            the tf10 to set
	 */
	public void setTf10(Integer tf10) {
		this.tf10 = tf10;
	}

	/**
	 * @return the tf11
	 */
	public Integer getTf11() {
		return tf11;
	}

	/**
	 * @param tf11
	 *            the tf11 to set
	 */
	public void setTf11(Integer tf11) {
		this.tf11 = tf11;
	}

	/**
	 * @return the tf12
	 */
	public Integer getTf12() {
		return tf12;
	}

	/**
	 * @param tf12
	 *            the tf12 to set
	 */
	public void setTf12(Integer tf12) {
		this.tf12 = tf12;
	}

	/**
	 * @return the tf13
	 */
	public Integer getTf13() {
		return tf13;
	}

	/**
	 * @param tf13
	 *            the tf13 to set
	 */
	public void setTf13(Integer tf13) {
		this.tf13 = tf13;
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
		builder.append("TechnicalFactors [id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}

}
