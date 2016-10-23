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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;

@Entity
@Table(name = "help")
public class Help extends EntityBase implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "Keyword is required")
	@Size(max = 30)
	@Column(name = "keyword", length = 30, nullable = false)
	private String keyword;

	@NotNull(message = "Content is required")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "content", columnDefinition = "CLOB")
	private String content;

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public Help() {
		super();
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param keyword
	 */
	public Help(String keyword) {
		super();
		init();
		this.keyword = keyword;
	}

	/**
	 * Constructor
	 * 
	 * @param keyword
	 * 
	 * @param content
	 */
	public Help(String keyword, String content) {
		super();
		init();
		this.keyword = keyword;
		this.content = content;
	}

	/**
	 * init method
	 */
	private void init() {
		this.keyword = "";
		this.content = "";
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
		if (keyword != null) {
			this.keyword = keyword.trim();
		}
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Short form
	 * 
	 * @return the helpContent
	 */
	public String getContentAbbrv() {
		String content = null;
		if (StringUtils.isNotEmpty(this.content)) {
			content = StringUtils.abbreviate(this.content, 100);
		}
		return content;
	}

	/**
	 * @param helpContent
	 *            the helpContent to set. Add CSS style class to paragraph, unordered lists, and ordered lists.
	 * 
	 */
	public void setContent(String content) {

		this.content = content;
		// if (StringUtils.isNotEmpty(this.content)) {
		// this.content = StringUtils.replace(this.content, "<p>", "<p class=\"tooltip-inline\">");
		// this.content = StringUtils.replace(this.content, "<ul>", "<ul class=\"tooltip-inline\">");
		// this.content = StringUtils.replace(this.content, "<ol>", "<ol class=\"tooltip-inline\">");
		// this.content = StringUtils.replace(this.content, "<div>", "<div class=\"tooltip-inline\">");
		// }
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
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
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
		Help other = (Help) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		}
		else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Help [id=");
		builder.append(id);
		builder.append(", keyword=");
		builder.append(keyword);
		builder.append(", content=");
		builder.append(StringUtils.abbreviate(content, 60));
		builder.append("]");
		return builder.toString();
	}

}
