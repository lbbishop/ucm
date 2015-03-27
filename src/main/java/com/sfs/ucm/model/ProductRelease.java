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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import com.sfs.ucm.data.BaselineType;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * ProductRelease
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name="productrelease")
public class ProductRelease extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Version is required")
	@Size(max = 16)
	@Column(name = "version", length = 16, nullable = false)
	private String version;

	@Size(max = 255)
	@Column(name = "description", length = 255, nullable = true)
	private String description;

	@NotNull(message = "Baseline type is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "baseline_type", nullable = false)
	private BaselineType baselineType;

	@Size(max = 40)
	@Column(name = "digital_signature", length = 30, nullable = true)
	private String digitalSignature;

	@Lob
	@Column(name = "change_log", columnDefinition = "TEXT NULL", nullable = true)
	private String changeLog;

	@Size(max = 25)
	@Column(name = "external_id", length = 25, nullable = true)
	private String externalId;

	@ManyToOne
	private Project project;

	/**
	 * Default constructor
	 */
	public ProductRelease() {
		super();
	}

	/**
	 * constructor
	 */
	public ProductRelease(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
	}

	public ProductRelease(String version) {
		super();
		this.version = version;
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
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		if (version != null) {
			this.version = version.trim();
		}
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the description abbreviated
	 */
	public String getDescriptionAbbrv() {
		return StringUtils.abbreviate(this.description, 80);
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
	 * @return the baselineType
	 */
	public BaselineType getBaselineType() {
		return baselineType;
	}

	/**
	 * @param baselineType
	 *            the baselineType to set
	 */
	public void setBaselineType(BaselineType baselineType) {
		this.baselineType = baselineType;
	}

	/**
	 * @return the changeLog
	 */
	public String getChangeLog() {
		return changeLog;
	}

	/**
	 * @param changeLog
	 *            the changeLog to set
	 */
	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId
	 *            the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the digitalSignature
	 */
	public String getDigitalSignature() {
		return digitalSignature;
	}

	/**
	 * @param digitalSignature
	 *            the digitalSignature to set
	 */
	public void setDigitalSignature(String digitalSignature) {
		this.digitalSignature = digitalSignature;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_PRODUCTRELEASE.toString(), this.identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductRelease [id=");
		builder.append(id);
		builder.append(", version=");
		builder.append(version);
		builder.append(", description=");
		builder.append(description);
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
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		ProductRelease other = (ProductRelease) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		}
		else if (!version.equals(other.version))
			return false;
		return true;
	}

}
