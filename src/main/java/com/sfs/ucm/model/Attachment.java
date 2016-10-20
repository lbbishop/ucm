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
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * Represents a generic Attachment
 * 
 * @author lbbishop
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

@Table(name = "attachment")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorOptions(force = true)
@DiscriminatorColumn(name = "attachment_type", discriminatorType = DiscriminatorType.STRING)
public class Attachment extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@XmlElement
	@NotNull(message = "Filename is required")
	@Size(max = 255)
	@Column(name = "filename", length = 255, nullable = false)
	protected String filename;

	@XmlElement
	@Size(max = 255)
	@Column(name = "description", length = 255, nullable = true)
	protected String description;

	@XmlElement
	@Lob
	@Column(name = "contents", nullable = false)
	protected byte[] contents;

	@XmlElement
	@Size(max = 255)
	@Column(name = "content_type", length = 255, nullable = true)
	protected String contentType;
	
	@XmlElement
	@Column(name = "version", nullable = false)
	protected Integer version;

	@XmlElement
	@Column(name = "contains_controlled_info", nullable = true)
	protected Boolean containsControlledInfo;

	/**
	 * Default constructor
	 */
	public Attachment() {
		super();
		init();
	}

	/**
	 * Data constructor
	 * 
	 * @param filename
	 * @param contents
	 */
	public Attachment(String filename, byte[] contents) {
		super();
		init();
		this.filename = filename;
		this.contents = contents;
	}

	/**
	 * Copy constructor
	 * 
	 * @param modifiedBy
	 * @param rhs
	 */
	public Attachment(Attachment rhs) {
		super();
		init();
		this.filename = rhs.filename;
		this.description = rhs.description;
		this.contents = Arrays.copyOf(rhs.contents, rhs.contents.length);
		this.contentType = rhs.contentType;
	}

	/**
	 * Init method
	 */
	private void init() {
		this.containsControlledInfo = false;
		this.version = 1;
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
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
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
	 * @return the content
	 */
	public byte[] getContents() {
		return contents;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(byte[] contents) {
		this.contents = contents;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(byte[] contents) {
		this.contents = contents;
	}

	/**
	 * @return the containsControlledInfo
	 */
	public Boolean getContainsControlledInfo() {
		return containsControlledInfo;
	}

	/**
	 * @param containsControlledInfo
	 *            the containsControlledInfo to set
	 */
	public void setContainsControlledInfo(Boolean containsControlledInfo) {
		this.containsControlledInfo = containsControlledInfo;
	}

	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Attachment [id=");
		builder.append(id);
		builder.append(", filename=");
		builder.append(filename);
		builder.append(", description=");
		builder.append(description);
		builder.append(", contentType=");
		builder.append(contentType);
		builder.append(", version=");
		builder.append(version);
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
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
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
		Attachment other = (Attachment) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		}
		else if (!filename.equals(other.filename))
			return false;
		return true;
	}

}
