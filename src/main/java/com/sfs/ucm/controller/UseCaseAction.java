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

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ConversationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.Actor;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Feature;
import com.sfs.ucm.model.Flow;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectPackage;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.model.UseCaseAttachment;
import com.sfs.ucm.model.UseCaseRule;
import com.sfs.ucm.model.UseCaseRuleTest;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.service.TestService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * UseCase Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("useCaseAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UseCaseAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	private List<Feature> features;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	@Service
	private TestService testService;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	private UseCase useCase;

	private List<UseCase> useCases;

	private Project project;

	private boolean selected;

	private List<Flow> extendedFlows;

	private boolean renamed;

	private List<Actor> actors;

	private List<ProjectPackage> projectPackages;

	private StreamedContent attachmentFile;

	private UseCaseAttachment attachment;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.useCase = new UseCase();
		this.selected = false;
		this.renamed = false;
	}

	/**
	 * Controller resource loader
	 * 
	 * @throws UCMException
	 */
	public void load() throws UCMException {
		try {
			// begin work unit
			begin();

			this.project = em.find(Project.class, id);

			loadList();
			loadFeatures(this.project);
			loadActors(this.project);
			loadProjectPackages(this.project);

			// get list of basic flows as candidate extended flows
			this.extendedFlows = findExtendedFlows(this.project);

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
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.useCase = new UseCase(ModelUtils.getNextIdentifier(this.useCases));
	}

	/**
	 * use case name change handler
	 * <p>
	 * On change delete associated test case to remove dangling test case
	 * 
	 * @param event
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void onNameChange(ValueChangeEvent event) throws UCMException {
		String name = (String) event.getNewValue();
		this.useCase.getBasicFlow().setName(name);

		// use case name has changed so delete associated test case
		this.renamed = true;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			// remove any associated usecase rule tests
			for (UseCaseRule useCaseRule : this.useCase.getUseCaseRules()) {
				List<UseCaseRuleTest> useCaseRuleTests = findUseCaseRuleTests(useCaseRule);
				Iterator<UseCaseRuleTest> useCaseRuleTestIter = useCaseRuleTests.iterator();
				while (useCaseRuleTestIter.hasNext()) {
					em.remove(useCaseRuleTestIter.next());
				}
			}

			// remove associated test case
			this.testService.deleteTestCase(this.useCase);

			// remove the artifact
			this.project.removeUseCase(this.useCase);
			em.remove(this.useCase);
			em.flush();

			logger.info("Deleted {}", this.useCase.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.useCase.getArtifact());

			// refresh list
			loadList();

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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save() throws UCMException {
		try {
			if (validate()) {
				this.useCase.setModifiedBy(authUser.getUsername());
				if (this.useCase.getId() == null) {
					this.project.addUseCase(this.useCase);
				}

				em.persist(this.project);
				logger.info("saved {}", this.useCase.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.useCase.getArtifact());

				// renamed use case will become stale so remove it now
				if (this.renamed) {
					this.testService.deleteTestCase(this.useCase);
					this.renamed = false;
				}

				// refresh list
				loadList();
				this.selected = false;
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}
	
	/**
	 * File Upload Handler
	 * 
	 * @param event
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void handleFileUpload(FileUploadEvent event) {

		// extract filename
		File file = new File(event.getFile().getFileName());

		logger.info("Uploading file: {}, type: {}", file.getName(), event.getFile().getContentType());
		this.attachment.setFilename(file.getName());
		this.attachment.setVersion(1);
		this.attachment.setContents(event.getFile().getContents());
		this.attachment.setContentType(event.getFile().getContentType());

		this.facesContextMessage.infoMessage("Uploaded file {0}", event.getFile().getFileName());

	}

	/**
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		this.useCase = (UseCase) event.getObject();
		this.selected = true;
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
	 * UseCases producer
	 * 
	 * @return List
	 */
	public List<UseCase> getUseCases() {
		return this.useCases;
	}

	/**
	 * get UseCase
	 * 
	 * @return useCase
	 */
	public UseCase getUseCase() {
		return useCase;
	}

	/**
	 * set UseCase
	 * 
	 * @param useCase
	 */
	public void setUseCase(UseCase useCase) {
		this.useCase = useCase;
	}

	/**
	 * @return the features
	 */
	public List<Feature> getFeatures() {
		return features;
	}

	/**
	 * @param features
	 *            the features to set
	 */
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @return the extendedFlows
	 */
	public List<Flow> getExtendedFlows() {
		return extendedFlows;
	}

	/**
	 * Feature value change
	 * <p>
	 * Add this useCase to the selected Feature
	 * 
	 * @param e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void featureValueChange(ValueChangeEvent e) {
		Feature feature = (Feature) e.getNewValue();
		feature.addUseCase(this.useCase);
	}

	/**
	 * @return the actors
	 */
	public List<Actor> getActors() {
		return actors;
	}

	/**
	 * @return the projectPackages
	 */
	public List<ProjectPackage> getProjectPackages() {
		return projectPackages;
	}

	/**
	 * @return the attachmentFile
	 */
	public StreamedContent getAttachmentFile() {
		return attachmentFile;
	}

	/**
	 * @param attachmentFile
	 *            the attachmentFile to set
	 */
	public void setAttachmentFile(StreamedContent attachmentFile) {
		this.attachmentFile = attachmentFile;
	}

	/**
	 * @return the attachment
	 */
	public UseCaseAttachment getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment
	 *            the attachment to set
	 */
	public void setAttachment(UseCaseAttachment attachment) {
		this.attachment = attachment;
	}

	// ================= private methods =====================
	/**
	 * Validate useCase
	 * <ul>
	 * <li>If new useCase check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.useCase.getId() == null) {
			if (this.useCases.contains(this.useCase)) {
				this.facesContextMessage.errorMessage("{0} already exists", this.useCase.getName());
				logger.error("{} already exists", this.useCase);
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * Load use cases
	 * 
	 * @throws UCMException
	 */
	private void loadList() throws UCMException {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UseCase> c = cb.createQuery(UseCase.class);
		Root<UseCase> obj = c.from(UseCase.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("name")));
		this.useCases = em.createQuery(c).getResultList();
	}

	/**
	 * find tests associated with this artifact
	 */
	private List<UseCaseRuleTest> findUseCaseRuleTests(UseCaseRule useCaseRule) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UseCaseRuleTest> c = cb.createQuery(UseCaseRuleTest.class);
		Root<UseCaseRuleTest> obj = c.from(UseCaseRuleTest.class);
		c.select(obj).where(cb.equal(obj.get("useCaseRule"), useCaseRule));
		return em.createQuery(c).getResultList();
	}

	/**
	 * Find Basic Flows to be used as available Extended Flows
	 * 
	 * @param project
	 *            the current project
	 * @return List of Basic Flows
	 */
	private List<Flow> findExtendedFlows(final Project project) {
		List<Flow> list = null;

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Flow> c = cb.createQuery(Flow.class);
		Root<Flow> obj = c.from(Flow.class);
		Path<Boolean> basicFlow = obj.get("basicFlowFlag");
		c.select(obj).where(cb.isTrue(basicFlow)).orderBy(cb.asc(obj.get("name")));
		list = em.createQuery(c).getResultList();

		return list;
	}

	/**
	 * Load resources
	 * 
	 * @param project
	 */
	private void loadFeatures(final Project project) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Feature> c = cb.createQuery(Feature.class);
		Root<Feature> obj = c.from(Feature.class);
		c.select(obj);
		c.where(cb.equal(obj.get("project"), project));
		c.orderBy(cb.asc(obj.get("id")));
		this.features = em.createQuery(c).getResultList();
	}

	/**
	 * load resources
	 * 
	 * @param project
	 */
	private void loadActors(final Project project) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Actor> c = cb.createQuery(Actor.class);
		Root<Actor> obj = c.from(Actor.class);
		c.select(obj).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("name")));
		this.actors = em.createQuery(c).getResultList();
	}

	/**
	 * load resources
	 * 
	 * @param project
	 */
	private void loadProjectPackages(Project project) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProjectPackage> c = cb.createQuery(ProjectPackage.class);
		Root<ProjectPackage> obj = c.from(ProjectPackage.class);
		c.select(obj).where(cb.equal(obj.get("project"), project)).orderBy(cb.asc(obj.get("name")));
		this.projectPackages = em.createQuery(c).getResultList();
	}
}