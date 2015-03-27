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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;

/**
 * 
 * Project setting
 * 
 * @author lbbishop
 * 
 */
@Audited
@Entity
@Table(name = "setting")
public class Setting extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Size(max = 32)
	@Column(name = "keyword", length = 32, nullable = false)
	private String keyword;

	@Size(max = 32)
	@Column(name = "strval", length = 32, nullable = true)
	private String strval;

	@Column(name = "intval", nullable = true)
	private Integer intval;

	@Type(type = "org.hibernate.type.YesNoType")
	@Column(name = "blnval", nullable = true)
	private Boolean blnval;

	@ManyToOne(optional = false)
	@JoinColumn(name = "project_id")
	private Project project;

	/**
	 * Default constructor
	 */
	public Setting() {
		super();
		init();
	}

	/**
	 * constructor
	 * 
	 * @param keyword
	 */
	public Setting(String keyword) {
		super();
		init();
		this.keyword = keyword;
	}

	/**
	 * Data constructor
	 * 
	 * @param keyword
	 * @param strval
	 */
	public Setting(String keyword, String strval) {
		super();
		this.keyword = keyword;
		this.strval = strval;
	}

	/**
	 * Data constructor
	 * 
	 * @param keyword
	 * @param intval
	 */
	public Setting(String keyword, Integer intval) {
		super();
		this.keyword = keyword;
		this.intval = intval;
	}

	/**
	 * Data constructor
	 * 
	 * @param keyword
	 * @param blnval
	 */
	public Setting(String keyword, Boolean blnval) {
		super();
		this.keyword = keyword;
		this.blnval = blnval;
	}

	/**
	 * class init method
	 */
	private void init() {
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
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword
	 *            the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @return the strval
	 */
	public String getStrval() {
		return strval;
	}

	/**
	 * @param strval
	 *            the strval to set
	 */
	public void setStrval(String strval) {
		this.strval = strval;
	}

	/**
	 * @return the intval
	 */
	public Integer getIntval() {
		return intval;
	}

	/**
	 * @param intval
	 *            the intval to set
	 */
	public void setIntval(Integer intval) {
		this.intval = intval;
	}

	/**
	 * @return the blnval
	 */
	public Boolean getBlnval() {
		return blnval;
	}

	/**
	 * @param blnval
	 *            the blnval to set
	 */
	public void setBlnval(Boolean blnval) {
		this.blnval = blnval;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		Setting other = (Setting) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		}
		else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Setting [id=");
		builder.append(id);
		builder.append(", keyword=");
		builder.append(keyword);
		builder.append(", strval=");
		builder.append(strval);
		builder.append(", intval=");
		builder.append(intval);
		builder.append(", blnval=");
		builder.append(blnval);
		builder.append("]");
		return builder.toString();
	}

}
