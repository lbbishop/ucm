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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * Specification Rule
 * 
 * @author lbbishop
 * 
 */
@Entity
@Indexed
@Audited
@DiscriminatorValue("specification_rule")
public class SpecificationRule extends AbstractBusinessRule implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "specification_id")
	private Specification specification;

	/**
	 * Constructor
	 */
	public SpecificationRule() {
		super();
	}

	/**
	 * Constructor
	 */
	public SpecificationRule(int identifier) {
		super();
		this.identifier = Integer.valueOf(identifier);
	}
	
	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_SPECIFICATIONRULE.toString(), this.identifier);
	}

	/**
	 * @return the specification
	 */
	public Specification getSpecification() {
		return specification;
	}

	/**
	 * @param specification
	 *            the specification to set
	 */
	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

}
