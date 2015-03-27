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
package com.sfs.ucm.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.Resource;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Resource Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("resourceAction")
public class ResourceAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	@ProjectUpdated
	Event<Project> projectEvent;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private Resource resource;

	private List<Resource> resources;

	private List<Resource> filteredResources;

	private Project project;

	private UploadedFile file;

	private boolean selected;

	private StreamedContent resourceFile;

	@Inject
	public void init() {
		this.resource = new Resource();
		this.selected = false;

		begin();
	}

	/**
	 * Begin work unit
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			this.project = em.find(Project.class, id);
			loadList();

			// update producer
			this.projectSecurityMarkingSrc.fire(this.project);

			editable = this.accessManager.hasPermission("projectMember", "Edit", this.project);
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Close action
	 * <p>
	 * End work unit and navigate home
	 * 
	 * @return outcome
	 */
	public String close() {
		String outcome = Literal.NAV_HOME.toString();
		end();
		return outcome;
	}

	/**
	 * Add action
	 * 
	 */
	public void add() {
		this.resource = new Resource(ModelUtils.getNextIdentifier(this.resources));
	}

	/**
	 * File Upload Handler
	 * 
	 * @param event
	 */
	public void handleFileUpload(FileUploadEvent event) {

		// extract filename
		File file = new File(event.getFile().getFileName());

		logger.info("Uploading file: {}, type: {}", file.getName(), event.getFile().getContentType());
		this.resource.setName(file.getName());
		this.resource.setPath(file.getName());
		this.resource.setContents(event.getFile().getContents());
		this.resource.setContentType(event.getFile().getContentType());

		this.facesContextMessage.infoMessage("Uploaded file {0}", event.getFile().getFileName());

	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	public void remove() throws UCMException {
		try {
			this.project.removeResource(this.resource);
			em.remove(this.resource);
			logger.info("deleted {}", this.resource.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.resource.getArtifact());
			loadList();
			projectEvent.fire(project);
			this.selected = false;
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * save action
	 * 
	 * @throws UCMException
	 */
	public void save() throws UCMException {
		try {
			if (validate()) {
				this.resource.setModifiedBy(authUser.getUsername());
				if (this.resource.getId() == null) {
					this.project.addResource(this.resource);
				}

				em.persist(this.project);
				projectEvent.fire(project);
				logger.info("saved {}", this.resource.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.resource.getArtifact());

				loadList();
				this.selected = true;
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	public void upload() {
		logger.info("upload {}", file);
		if (file != null) {
			this.facesContextMessage.infoMessage("Uploaded file {0}", file.getFileName());
		}
	}

	/**
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		this.selected = true;

		if (this.resource.getContents() != null && this.resource.getContents().length > 0) {
			InputStream stream = new ByteArrayInputStream(resource.getContents());
			this.resourceFile = new DefaultStreamedContent(stream, this.resource.getContentType(), this.resource.getName());
		}
	}

	/**
	 * @return the file
	 */
	public UploadedFile getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(UploadedFile file) {
		this.file = file;
	}

	/**
	 * Resources producer
	 * 
	 * @return List
	 */
	public List<Resource> getResources() {
		return this.resources;
	}

	/**
	 * get Resource
	 * 
	 * @return resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * set Resource
	 * 
	 * @param resource
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @return the resourceFile
	 */
	public StreamedContent getResourceFile() {
		return resourceFile;
	}

	/**
	 * @param resourceFile
	 *            the resourceFile to set
	 */
	public void setResourceFile(StreamedContent resourceFile) {
		this.resourceFile = resourceFile;
	}

	/**
	 * @return the filteredResources
	 */
	public List<Resource> getFilteredResources() {
		return filteredResources;
	}

	/**
	 * @param filteredResources
	 *            the filteredResources to set
	 */
	public void setFilteredResources(List<Resource> filteredResources) {
		this.filteredResources = filteredResources;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Validate resource
	 * <ul>
	 * <li>If new resource check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.resource.getId() == null) {
			if (this.resources.contains(this.resource)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.resource.getName());
				logger.error("{} already exists", this.resource);
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * Load resource
	 */
	private void loadList() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Resource> c = cb.createQuery(Resource.class);
		Root<Resource> obj = c.from(Resource.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.resources = em.createQuery(c).getResultList();
	}

}