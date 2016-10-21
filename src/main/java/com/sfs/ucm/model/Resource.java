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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Constants;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.data.ModelResourceType;
import com.sfs.ucm.util.ModelUtils;

/**
 * Resource
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "resource")
public class Resource extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Name is required")
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@NotNull(message = "Model Resource Type is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "model_resource_type", nullable = false)
	private ModelResourceType modelResourceType;

	@NotNull(message = "Path is required")
	@Size(max = 255)
	@Column(name = "path", length = 255, nullable = false)
	private String path;

	@Size(max = 255)
	@Column(name = "description", length = 255, nullable = true)
	private String description;

	@Lob
	@Column(name = "contents", columnDefinition = "LONGBLOB NULL", nullable = true)
	private byte[] contents;

	@Size(max = 255)
	@Column(name = "content_type", length = 255, nullable = true)
	private String contentType;

	@ManyToOne
	private Project project;

	@Transient
	private boolean externalResource;

	/**
	 * Constructor
	 */
	public Resource() {
		super();
		init();

	}

	/**
	 * Identifier constructor
	 */
	public Resource(int identifier) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
	}

	/**
	 * init method
	 */
	private void init() {

	}

	/**
	 * Post load callback
	 */
	@PostLoad
	public void postLoad() {

		this.externalResource = false;
		if (this.contents == null || this.contents.length == 0) {
			this.externalResource = true;
		}
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
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the path
	 */
	public String getPathAbbrv() {
		return StringUtils.abbreviate(path, Constants.ABBRV_DESC_LEN);
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
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
	public void setContents(byte[] contents) {
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
	 * @return the modelResourceType
	 */
	public ModelResourceType getModelResourceType() {
		return modelResourceType;
	}

	/**
	 * @param modelResourceType
	 *            the modelResourceType to set
	 */
	public void setModelResourceType(ModelResourceType modelResourceType) {
		this.modelResourceType = modelResourceType;
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
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_MODELRESOURCE.toString(), this.identifier);
	}

	/**
	 * @return the externalResource
	 */
	public boolean isExternalResource() {
		return externalResource;
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
		Resource other = (Resource) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
