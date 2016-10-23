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
import javax.enterprise.event.Event;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Feature;
import com.sfs.ucm.model.IssueAttachment;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.Specification;
import com.sfs.ucm.model.SpecificationRule;
import com.sfs.ucm.model.SpecificationRuleTest;
import com.sfs.ucm.model.SpecificationTest;
import com.sfs.ucm.security.AccessManager;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ModelUtils;
import com.sfs.ucm.util.ProjectSecurityInit;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.view.FacesContextMessage;

/**
 * Supplementary Specification Specification Actions
 * 
 * @author lbbishop
 */
@Stateful
@ConversationScoped
@Named("specificationAction")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SpecificationAction extends ActionBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContextMessage facesContextMessage;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private AccessManager accessManager;

	private boolean editable;

	@Inject
	private Logger logger;

	@Inject
	@Authenticated
	private AuthUser authUser;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	@ProjectSecurityInit
	Event<Project> projectSecurityMarkingSrc;

	private Specification specification;

	private SpecificationRule specificationRule;

	private List<Specification> specifications;

	private List<Feature> features;

	private Project project;

	private boolean selected;

	private StreamedContent attachmentFile;

	private IssueAttachment attachment;

	/**
	 * Controller initialization
	 */
	@Inject
	public void init() {
		this.specification = new Specification();
		this.specificationRule = new SpecificationRule();
		this.selected = false;
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
	 * @return outcome
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add() {
		this.specification = new Specification(ModelUtils.getNextIdentifier(this.specifications));
	}

	/**
	 * Add business rule action
	 * 
	 * @return outcome
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addBusinessRule() {
		Long cnt = getSpecificationRuleCount();
		this.specificationRule = new SpecificationRule(cnt.intValue() + 1);
	}

	/**
	 * Row selection event
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		this.selected = true;
	}

	/**
	 * Action: remove object
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove() throws UCMException {
		try {
			// remove any specification rule tests
			for (SpecificationRule specificationRule : this.specification.getSpecificationRules()) {
				List<SpecificationRuleTest> specificationRuleTests = findSpecificationRuleTests(specificationRule);
				Iterator<SpecificationRuleTest> specificationRuleTestIter = specificationRuleTests.iterator();
				while (specificationRuleTestIter.hasNext()) {
					em.remove(specificationRuleTestIter.next());
				}
			}

			// remove any parent test classes
			List<SpecificationTest> specificationTests = findSpecificationTests(this.specification);
			Iterator<SpecificationTest> specificationTestIter = specificationTests.iterator();
			while (specificationTestIter.hasNext()) {
				em.remove(specificationTestIter.next());
			}

			this.project.removeSpecification(this.specification);
			em.remove(this.specification);
			logger.info("deleted {}", this.specification.getArtifact());
			this.facesContextMessage.infoMessage("{0} deleted successfully", this.specification.getArtifact());

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
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void save() throws UCMException {
		try {
			if (validate()) {
				this.specification.setModifiedBy(authUser.getUsername());
				if (this.specification.getId() == null) {
					this.project.addSpecification(this.specification);
				}

				em.persist(this.project);

				logger.info("saved {}", this.specification.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.specification.getArtifact());

				// refresh list
				loadList();
				this.selected = true;
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * save business rule action
	 * 
	 * @throws UCMException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void saveBusinessRule() {
		try {
			if (validate()) {
				this.specificationRule.setModifiedBy(authUser.getUsername());
				if (this.specificationRule.getId() == null) {
					this.specification.addSpecificationRule(this.specificationRule);
				}
				em.persist(this.specification);
				logger.info("Saved {}", this.specificationRule.getArtifact());
				this.facesContextMessage.infoMessage("{0} saved successfully", this.specificationRule.getArtifact());
				this.selected = true;
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
	 * Feature value change
	 * <p>
	 * Add this Specification to the selected Feature
	 * 
	 * @param e
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void featureValueChange(ValueChangeEvent e) {
		Feature feature = (Feature) e.getNewValue();
		feature.addSpecification(this.specification);
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
	 * Specifications producer
	 * 
	 * @return List
	 */
	public List<Specification> getSpecifications() {
		return this.specifications;
	}

	/**
	 * get Specification
	 * 
	 * @return specification
	 */
	public Specification getSpecification() {
		return specification;
	}

	/**
	 * set Specification
	 * 
	 * @param specification
	 */
	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	/**
	 * @return the specificationRule
	 */
	public SpecificationRule getSpecificationRule() {
		return specificationRule;
	}

	/**
	 * @param specificationRule
	 *            the specificationRule to set
	 */
	public void setSpecificationRule(SpecificationRule specificationRule) {
		this.specificationRule = specificationRule;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
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
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	// ===================== private methods ==========================

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
	public IssueAttachment getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment
	 *            the attachment to set
	 */
	public void setAttachment(IssueAttachment attachment) {
		this.attachment = attachment;
	}

	/**
	 * Validate specification
	 * <ul>
	 * <li>If new specification check for duplicate</li>
	 * </ul>
	 * 
	 * @return flag true if validation is successful
	 */
	private boolean validate() {
		boolean isvalid = true;
		if (this.specification.getId() == null) {
			if (this.specifications.contains(this.specification)) {
				this.facesContextMessage.errorMessage("{0} already exists", StringUtils.abbreviate(this.specification.getName(), 25));
				logger.error("{} already exists", this.specification.getName());
				isvalid = false;
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.addCallbackParam("validationFailed", !isvalid);
			}
		}

		return isvalid;
	}

	/**
	 * load specifications
	 */
	private void loadList() throws UCMException {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Specification> c = cb.createQuery(Specification.class);
		Root<Specification> obj = c.from(Specification.class);
		c.select(obj).where(cb.equal(obj.get("project"), this.project)).orderBy(cb.asc(obj.get("id")));
		this.specifications = em.createQuery(c).getResultList();

	}

	/**
	 * find tests associated with this artifact
	 */
	private List<SpecificationTest> findSpecificationTests(Specification specification) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SpecificationTest> c = cb.createQuery(SpecificationTest.class);
		Root<SpecificationTest> obj = c.from(SpecificationTest.class);
		c.select(obj).where(cb.equal(obj.get("specification"), specification));
		return em.createQuery(c).getResultList();
	}

	/**
	 * find tests associated with this artifact
	 */
	private List<SpecificationRuleTest> findSpecificationRuleTests(SpecificationRule specificationRule) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SpecificationRuleTest> c = cb.createQuery(SpecificationRuleTest.class);
		Root<SpecificationRuleTest> obj = c.from(SpecificationRuleTest.class);
		c.select(obj).where(cb.equal(obj.get("specificationRule"), specificationRule));
		return em.createQuery(c).getResultList();
	}

	/**
	 * Get count of specification rules
	 * 
	 * @return count
	 */
	private Long getSpecificationRuleCount() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		c.select(cb.count(c.from(SpecificationRule.class)));
		return em.createQuery(c).getSingleResult();
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

}