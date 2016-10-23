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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.sfs.ucm.data.Constants;
import com.sfs.ucm.data.RuleType;

/**
 * BusinessRule base class
 * 
 * @author lbbisho
 * 
 */
@Entity
@Indexed
@Audited
@Inheritance
@DiscriminatorColumn(name = "business_rule_type")
@Table(name = "businessrule")
public class BusinessRule extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Name is required")
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	protected String name;

	@NotNull(message = "Rule Type is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "rule_type", nullable = true)
	protected RuleType ruleType;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "When condition is required")
	@Lob
	@Column(name = "when", columnDefinition = "CLOB", nullable = false)
	protected String when;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Then outcome is required")
	@Lob
	@Column(name = "then", columnDefinition = "CLOB", nullable = false)
	protected String then;

	/**
	 * Default constructor
	 */
	public BusinessRule() {
		super();
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param rule
	 * @param implementation
	 */
	public BusinessRule(String when, String then) {
		super();
		init();
		this.when = when;
		this.then = then;
	}

	/**
	 * init method
	 */
	private void init() {
		this.ruleType = RuleType.Generic;
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
	 * @return the when condition abbreviated
	 */
	public String getWhenAbbrv() {
		return org.apache.commons.lang.StringUtils.abbreviate(this.when, Constants.ABBRV_DESC_LEN);
	}

	/**
	 * @return the then condition abbreviated
	 */
	public String getThenAbbrv() {
		return org.apache.commons.lang.StringUtils.abbreviate(this.then, Constants.ABBRV_DESC_LEN);
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
	 * @return the ruleType
	 */
	public RuleType getRuleType() {
		return ruleType;
	}

	/**
	 * @param ruleType
	 *            the ruleType to set
	 */
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	/**
	 * @return the when
	 */
	public String getWhen() {
		return when;
	}

	/**
	 * @param when the when to set
	 */
	public void setWhen(String when) {
		this.when = when;
	}

	/**
	 * @return the then
	 */
	public String getThen() {
		return then;
	}

	/**
	 * @param then the then to set
	 */
	public void setThen(String then) {
		this.then = then;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BusinessRule [id=");
		builder.append(id);
		builder.append(", identifier=");
		builder.append(identifier);
		builder.append(", name=");
		builder.append(name);
		builder.append(", when=");
		builder.append(getWhenAbbrv());
		builder.append(", then=");
		builder.append(getThenAbbrv());
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
		BusinessRule other = (BusinessRule) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
