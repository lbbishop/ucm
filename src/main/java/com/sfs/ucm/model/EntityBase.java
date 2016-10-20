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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.PersistenceContextListener;

/**
 * Entity base class. Must have persistent subclass.
 * <p>
 * Contains all common entity properties.
 * 
 * @author lbbishop
 * 
 */
@Audited
@MappedSuperclass
@EntityListeners(PersistenceContextListener.class)
public class EntityBase implements Serializable {

	protected static final long serialVersionUID = 1L;

	@Column(name = "identifier", nullable = true)
	protected Integer identifier;

	@NotNull(message = "ModifiedBy is required")
	@Size(max = 25)
	@Column(name = "modified_by", length = 25, nullable = false)
	protected String modifiedBy;

	@NotNull
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date", nullable = false)
	protected java.util.Date modifiedDate;

	// JPA optimistic locking field
	@Version
	@Column(name = "object_version")
	protected int objectVersion;

	/**
	 * Default constructor
	 */
	public EntityBase() {
		super();
	}

	/**
	 * @return the identifier
	 */
	public Integer getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(Integer identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the objectVersion
	 */
	public int getObjectVersion() {
		return objectVersion;
	}

	/**
	 * @param objectVersion
	 *            the objectVersion to set
	 */
	public void setObjectVersion(int objectVersion) {
		this.objectVersion = objectVersion;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy
	 *            the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public java.util.Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            the modifiedDate to set
	 */
	public void setModifiedDate(java.util.Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
