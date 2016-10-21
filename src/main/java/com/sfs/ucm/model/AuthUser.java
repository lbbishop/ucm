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
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * Authenticated User
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "authuser")
public class AuthUser extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Size(min = 3, max = 16)
	@NotNull(message = "user name is required")
	@Column(name = "user_name", nullable = false)
	@Pattern(regexp = "^\\w*$", message = "not a valid username")
	private String username;

	@NotNull(message = "name is required")
	@Column(name = "name", length = 40, nullable = false)
	@Size(max = 40)
	@Pattern(regexp = "[A-Za-z ]*", message = "must contain only letters and spaces")
	private String name;

	@Size(max = 255)
	@Column(name = "email", length = 255, nullable = true)
	private String email;

	@Column(name = "phone_number")
	private String phoneNumber;

	@OneToMany(mappedBy = "authUser", cascade = { CascadeType.ALL })
	private Collection<AuthRole> authRoles;

	@OneToMany(mappedBy = "authUser", cascade = { CascadeType.ALL })
	private Collection<Preference> preferences;

	@Transient
	private boolean loggedIn;

	@Transient
	private Project selectedProject;

	@Transient
	private TestSet selectedTestSet;

	/**
	 * Default constructor
	 */
	public AuthUser() {
		super();
		init();
	}

	/**
	 * Default constructor
	 */
	public AuthUser(int identifier) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
	}

	/**
	 * constructor
	 * 
	 * @param userName
	 */
	public AuthUser(String username) {
		super();
		init();
		this.username = username;
	}

	/**
	 * constructor
	 * 
	 * @param username
	 * @param password
	 * @param name
	 */
	public AuthUser(String username, String name) {
		super();
		init();
		this.username = username;
		this.name = name;
	}

	/**
	 * class init method
	 */
	private void init() {
		this.loggedIn = false;
		this.authRoles = new HashSet<AuthRole>();
		this.preferences = new HashSet<Preference>();
	}

	/**
	 * display user roles as delimited string
	 * 
	 * @return string with delimited roles
	 */
	public String getUserRoles() {
		Collection<String> roles = new HashSet<String>();
		for (AuthRole authRole : this.authRoles) {
			roles.add(authRole.getRoleName());
		}
		return StringUtils.join(roles, ",");
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
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
		this.name = name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_USER.toString(), this.identifier);
	}

	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * @param loggedIn
	 *            the loggedIn to set
	 */
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	/**
	 * @param role
	 *            the role to add
	 */
	public void addAuthRole(AuthRole authRole) {
		authRole.setAuthUser(this);
		this.authRoles.add(authRole);
	}

	/**
	 * @param role
	 *            the roles to remove
	 */
	public void removeAuthRole(AuthRole authRole) {
		authRole.setAuthUser(null);
		this.authRoles.remove(authRole);
	}

	/**
	 * Does user have role
	 * 
	 * @param role
	 * @return boolean
	 */
	public boolean hasRole(String role) {
		boolean userHasRole = false;
		for (AuthRole authRole : this.authRoles) {
			if (authRole.getRoleName().equals(role)) {
				userHasRole = true;
				break;
			}
		}
		return userHasRole;
	}

	/**
	 * @return the authRoles
	 */
	public Collection<AuthRole> getAuthRoles() {
		return authRoles;
	}

	/**
	 * @return the preferences
	 */
	public Collection<Preference> getPreferences() {
		return preferences;
	}

	/**
	 * @param preference
	 *            the preference to add
	 */
	public void addPreference(Preference preference) {
		preference.setAuthUser(this);
		this.preferences.add(preference);
	}

	/**
	 * @param preference
	 *            the preferences to remove
	 */
	public void removePreference(Preference preference) {
		preference.setAuthUser(null);
		this.preferences.remove(preference);
	}

	// ================= preference mappings ==================

	/**
	 * @return the selectedProject
	 */
	public Project getSelectedProject() {
		return selectedProject;
	}

	/**
	 * @param selectedProject
	 *            the selectedProject to set
	 */
	public void setSelectedProject(Project selectedProject) {
		this.selectedProject = selectedProject;
	}

	/**
	 * @return the selectedTestSet
	 */
	public TestSet getSelectedTestSet() {
		return selectedTestSet;
	}

	/**
	 * @param selectedTestSet
	 *            the selectedTestSet to set
	 */
	public void setSelectedTestSet(TestSet selectedTestSet) {
		this.selectedTestSet = selectedTestSet;
	}

	/**
	 * check for a preference setting
	 * 
	 * @param keyword
	 * @return true if found
	 */
	public boolean isPreferenceSet(String keyword) {
		boolean prefset = false;
		for (Preference preference : this.preferences) {
			if (preference.getKeyword().equals(keyword)) {
				prefset = true;
				break;
			}
		}
		return prefset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthUser [id=");
		builder.append(id);
		builder.append(", username=");
		builder.append(username);
		builder.append(", name=");
		builder.append(name);
		builder.append(", email=");
		builder.append(email);
		builder.append(", phoneNumber=");
		builder.append(phoneNumber);
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
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		AuthUser other = (AuthUser) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		}
		else if (!username.equals(other.username))
			return false;
		return true;
	}

}
