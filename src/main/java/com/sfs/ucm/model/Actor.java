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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.sfs.ucm.data.Constants;
import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * Actor
 * 
 * @author lbbishop
 * 
 */
@Entity
@Indexed
@Audited
@Table(name = "actor")
public class Actor extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	// actor complexity weights
	public static final int SIMPLE = 1;
	public static final int AVERAGE = 2;
	public static final int COMPLEX = 3;

	@Id
	@DocumentId
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
	@NotNull(message = "Name is required")
	@Size(max = 100)
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@NotNull(message = "Complexity is required")
	@Column(name = "complexity", nullable = false)
	private Integer complexity;

	@NotNull(message = "Responsibilities is required")
	@Lob
	@Column(name = "responsibilities", columnDefinition = "TEXT", nullable = false)
	private String responsibilities;

	@Lob
	@Column(name = "needs", columnDefinition = "TEXT NULL", nullable = true)
	private String needs;

	@ManyToOne
	private Project project;

	@Transient
	private String complexityLabel;

	/**
	 * Default constructor
	 */
	public Actor() {
		super();
	}

	/**
	 * Identifier constructor
	 */
	public Actor(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
	}

	/**
	 * @param name
	 * @param responsibilities
	 */
	public Actor(String name, String responsibilities) {
		super();
		this.name = name;
		this.responsibilities = responsibilities;
	}

	/**
	 * @param identifier
	 * @param name
	 * @param complexity
	 * @param responsibilities
	 */
	public Actor(Integer identifier, String name, Integer complexity, String responsibilities) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.complexity = complexity;
		this.responsibilities = responsibilities;
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
	 * PostLoad
	 */
	@PostLoad
	public void PostLoad() {
		updateComplexityLabel();
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_ACTOR.toString(), this.identifier);
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
	 * @return the complexity
	 */
	public Integer getComplexity() {
		return complexity;
	}

	/**
	 * @param complexity
	 *            the complexity to set
	 */
	public void setComplexity(Integer complexity) {
		this.complexity = complexity;
		updateComplexityLabel();
	}

	/**
	 * Complexity string
	 * 
	 * @return string representation of complexity weight
	 */
	public String getComplexityLabel() {
		return this.complexityLabel;
	}

	/**
	 * @return the responsibilities
	 */
	public String getResponsibilities() {
		return responsibilities;
	}

	/**
	 * @return the responsibilities
	 */
	public String getResponsibilitiesAbbrv() {
		return StringUtils.abbreviate(responsibilities, Constants.ABBRV_DESC_LEN);
	}

	/**
	 * @param responsibilities
	 *            the responsibilities to set
	 */
	public void setResponsibilities(String responsibilities) {
		this.responsibilities = responsibilities;
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
	 * @return the needs
	 */
	public String getNeeds() {
		return needs;
	}

	/**
	 * @return the needs
	 */
	public String getNeedsAbbrv() {
		return StringUtils.abbreviate(needs, Constants.ABBRV_DESC_LEN);
	}

	/**
	 * @param needs
	 *            the needs to set
	 */
	public void setNeeds(String needs) {
		this.needs = needs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Actor [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", responsibilities=");
		builder.append(responsibilities);
		builder.append(", needs=");
		builder.append(needs);
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
		Actor other = (Actor) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * Helper method
	 */
	private void updateComplexityLabel() {
		if (this.complexity != null) {
			switch (this.complexity) {
			case AVERAGE:
				this.complexityLabel = Literal.COMPLEXITY_AVERAGE.toString();
				break;
			case COMPLEX:
				this.complexityLabel = Literal.COMPLEXITY_COMPLEX.toString();
				break;
			case SIMPLE:
				this.complexityLabel = Literal.COMPLEXITY_SIMPLE.toString();
				break;
			}
		}
	}

}
