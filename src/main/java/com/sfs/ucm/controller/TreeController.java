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

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;

import com.sfs.ucm.data.ModelNode;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Iteration;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.model.TestCase;
import com.sfs.ucm.model.TestPlan;
import com.sfs.ucm.model.TestSet;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.service.ProjectService;
import com.sfs.ucm.util.Authenticated;
import com.sfs.ucm.util.ProjectUpdated;
import com.sfs.ucm.util.Service;
import com.sfs.ucm.util.TestPlanUpdated;

@SessionScoped
@Named(value = "treeController")
public class TreeController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private Logger logger;

	private AuthUser authUser;

	private TreeNode root;

	private TreeNode testPlanNode;

	private TreeNode selectedNode;

	@Inject
	@ProjectUpdated
	private Event<Project> projectEvent;

	@Inject
	@Service
	private ProjectService projectService;

	private String uri;

	@PostConstruct
	public void init() {
		this.uri = "/project";
		logger.info("treeController: init called");
	}

	/**
	 * Authenticated user observer
	 * 
	 * @param authUser
	 * @throws UCMException
	 */
	public void onAuthenticatedUserChange(@Observes @Authenticated final AuthUser authUser) throws UCMException {
		logger.info("onAuthenticatedUserChange {}", authUser);
		this.authUser = authUser;

		// load projects for user
		loadTreeNodes();
	}

	/**
	 * Project updated observer
	 * 
	 * @param project
	 * @throws UCMException
	 */
	public void onProjectUpdate(@Observes @ProjectUpdated Project project) throws UCMException {
		logger.info("onProjectUpdate called");

		// update tree nodes
		loadTreeNodes();
	}

	/**
	 * TestPlan updated observer
	 * 
	 * @param project
	 * @throws UCMException
	 */
	public void onTestPlanUpdate(@Observes @TestPlanUpdated TestPlan testPlan) throws UCMException {
		logger.info("onTestPlanUpdate called");

		// update tree nodes
		loadTreeNodes();
	}

	/**
	 * Load tree nodes
	 * 
	 * @throws UCMException
	 */
	@SuppressWarnings("unused")
	public void loadTreeNodes() throws UCMException {
		try {
			this.root = new DefaultTreeNode("root", null);
			logger.info("loadTreeNodes root {}", this.root);

			// Projects
			TreeNode projectsRoot = new DefaultTreeNode(new ModelNode("Projects", 0L, "projects"), this.root);
			logger.info("loadTreeNodes projectsRoot {}", projectsRoot);
			projectsRoot.setExpanded(true);

			List<ModelNode> projectNodes = this.projectService.findMemberProjects(authUser);

			for (ModelNode project : projectNodes) {
				Project theProject = em.find(Project.class, project.getId());
				logger.info("loaded project {}", theProject);

				// verify user is a project member of the project or has manager role
				ProjectMember theProjectMember = this.projectService.findProjectMember(project.getId(), this.authUser);

				// create a transient project member until one is created
				if (theProjectMember == null) {
					theProjectMember = new ProjectMember();
				}

				TreeNode projectNode = new DefaultTreeNode("project", new ModelNode(project.getName(), project.getId(), "project", theProject.getOpen()), projectsRoot);

				projectNode.setExpanded(true);
				logger.info("PM {}", theProjectMember);

				// project node collapse state
				if (theProjectMember.getProjectOpen()) {
					projectNode.setExpanded(true);
				}
				else {
					projectNode.setExpanded(false);
				}

				// Product Vision
				TreeNode productVision = new DefaultTreeNode("document", new ModelNode("Product Vision", project.getId(), "productvision"), projectNode);


				// Project members
				TreeNode projectMembersNode = new DefaultTreeNode(new ModelNode("Project Members", project.getId(), "members"), projectNode);


				// Stakeholders
				TreeNode stakeholdersRoot = new DefaultTreeNode(new ModelNode("Stakeholders", project.getId(), "stakeholders"), projectNode);

				// StakeholderRequests
				TreeNode stakeholderRequestsRoot = new DefaultTreeNode(new ModelNode("Stakeholder Requests", project.getId(), "stakeholderrequests"), projectNode);

				// Risks
				TreeNode risksRoot = new DefaultTreeNode(new ModelNode("Risks", project.getId(), "risks"), projectNode);

				// Constraints
				TreeNode constraintsRoot = new DefaultTreeNode(new ModelNode("Design Constraints", project.getId(), "constraints"), projectNode);

				// Glossary Terms
				TreeNode glossaryRoot = new DefaultTreeNode(new ModelNode("Glossary", project.getId(), "glossary"), projectNode);

				// Packages
				TreeNode packagesNode = new DefaultTreeNode(new ModelNode("Packages", project.getId(), "packages"), projectNode);

				// Features
				TreeNode featuresRoot = new DefaultTreeNode(new ModelNode("Features", project.getId(), "features"), projectNode);

				// Resources
				TreeNode resourceModelNode = new DefaultTreeNode(new ModelNode("Resources", project.getId(), "resources"), projectNode);

				// Supplementary Requirement Specifications
				TreeNode requirementsRoot = new DefaultTreeNode(new ModelNode("Requirement Specifications", project.getId(), "requirements"), projectNode);
				requirementsRoot.setExpanded(!theProjectMember.getRequirementsCollapsed());

				// Requirement Business Rules
				TreeNode requirementRules = new DefaultTreeNode(new ModelNode("Business Rules", project.getId(), "requirementrules"), requirementsRoot);

				// Project Actors
				TreeNode actorsRoot = new DefaultTreeNode(new ModelNode("Actors", project.getId(), "actors"), projectNode);

				// Project use cases and flows
				TreeNode useCasesRoot = new DefaultTreeNode("useCases", new ModelNode("Use Cases", project.getId(), "usecases"), projectNode);
				useCasesRoot.setExpanded(!theProjectMember.getUseCasesCollapsed());
				
				List<UseCase> useCases = em.createQuery("from UseCase uc where uc.project.id = :id order by uc.name asc", UseCase.class).setParameter("id", project.getId()).getResultList();
				for (UseCase useCase : useCases) {
					TreeNode basicFlowNode = new DefaultTreeNode("document", new ModelNode(useCase.getIdentifierName(), useCase.getId(), "basicflow"), useCasesRoot);
					basicFlowNode.setExpanded(false);
					TreeNode alternativesFlowNode = new DefaultTreeNode(new ModelNode("Alternative Flows", useCase.getId(), "alternativeflows"), basicFlowNode);
				}

				// UseCase Business Rules
				TreeNode useCaseRules = new DefaultTreeNode(new ModelNode("Business Rules", project.getId(), "usecaserules"), useCasesRoot);


				// Test Plan
				this.testPlanNode = new DefaultTreeNode("document", new ModelNode("Test Plan", project.getId(), "testplan"), projectNode);
				this.testPlanNode.setExpanded(!theProjectMember.getTestPlanCollapsed());
				TestPlan testPlan = theProject.getTestPlan();

				// UnitTests node
				TreeNode unitTestRoot = new DefaultTreeNode(new ModelNode("Unit Tests", testPlan.getId(), "unittests"), this.testPlanNode);

				// TestSets node
				TreeNode testSetsRoot = new DefaultTreeNode("testSets", new ModelNode("Test Sets", testPlan.getId(), "testsets"), this.testPlanNode);
				testSetsRoot.setExpanded(!theProjectMember.getTestSetsCollapsed());

				List<TestSet> testSets = em.createQuery("from TestSet ts where ts.testPlan.id = :testPlanId", TestSet.class).setParameter("testPlanId", testPlan.getId()).getResultList();
				for (TestSet testSet : testSets) {
					TreeNode testSetNode = new DefaultTreeNode(new ModelNode(testSet.getIdentifierName(), testSet.getId(), "testset"), testSetsRoot);

					// requirement tests
					TreeNode reqTestsNode = new DefaultTreeNode(new ModelNode("Requirement Tests", testSet.getId(), "requirementtests"), testSetNode);

					// requirement rule tests
					TreeNode reqRuleTestsNode = new DefaultTreeNode(new ModelNode("Requirement Rule Tests", testSet.getId(), "requirementruletests"), testSetNode);

					// test cases
					TreeNode testCasesNode = new DefaultTreeNode(new ModelNode("Test Cases", testSet.getId(), "testcases"), testSetNode);

					List<TestCase> testCases = em.createQuery("from TestCase tc where tc.testSet.id = :id order by tc.identifier asc", TestCase.class).setParameter("id", testSet.getId())
							.getResultList();
					for (TestCase testCase : testCases) {
						TreeNode testCaseNode = new DefaultTreeNode("document", new ModelNode(testCase.getIdentifierName(), testCase.getId(), "testcase"), testCasesNode);
						testCaseNode.setExpanded(false);
					}
				}

				// Project Tasks
				TreeNode tasksNode = new DefaultTreeNode(new ModelNode("Tasks", project.getId(), "tasks"), projectNode);

	

	
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}
	}

	/**
	 * Open or close selected project
	 */
	public void openCloseProject() throws UCMException {

		ModelNode node = (ModelNode) this.selectedNode.getData();

		try {
			if (!this.selectedNode.isExpanded()) {
				this.projectService.updateProjectState(node.getId(), authUser, true);
			}
			else {
				this.projectService.updateProjectState(node.getId(), authUser, false);
			}
		}
		catch (Exception e) {
			throw new UCMException(e);
		}

		// update tree nodes
		loadTreeNodes();

	}

	/**
	 * @return the selectedNode
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * @param selectedNode
	 *            the selectedNode to set
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
		// ModelNode node = (ModelNode) this.selectedNode.getData();
	}

	/**
	 * Node selection handler
	 * 
	 * @param event
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		// ModelNode modelNode = (ModelNode) event.getTreeNode().getData();
	}

	/**
	 * Node expansion handler
	 * 
	 * @param event
	 */
	public void onNodeExpand(NodeExpandEvent event) {

		ModelNode modelNode = (ModelNode) event.getTreeNode().getData();

		try {
			if ("Use Cases".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), false);
			}
			else if ("Test Plan".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), false);
			}
			else if ("Requirement Specifications".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), false);
			}
			else if ("Project Management".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), false);
			}
			else if ("Project Estimation".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), false);
			}
			else if ("Test Sets".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), false);
			}
		}
		catch (UCMException e) {
			logger.error("Error calling projectService.updateProjectMemberState {}", e.getMessage());
		}
	}

	/**
	 * Node collapse handler
	 * 
	 * @param event
	 */
	public void onNodeCollapse(NodeCollapseEvent event) {

		ModelNode modelNode = (ModelNode) event.getTreeNode().getData();

		try {
			if ("Use Cases".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), true);
			}
			else if ("Test Plan".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), true);
			}
			else if ("Requirement Specifications".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), true);
			}
			else if ("Project Management".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), true);
			}
			else if ("Project Estimation".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), true);
			}
			else if ("Test Sets".equals(modelNode.getName())) {
				TreeNode node = event.getTreeNode().getParent();
				ModelNode projectNode = (ModelNode) node.getData();
				this.projectService.updateProjectMemberState(projectNode.getId(), authUser, modelNode.getName(), true);
			}
		}
		catch (UCMException e) {
			logger.error("Error calling projectService.updateProjectMemberState {}", e.getMessage());
		}
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Return the root node
	 * 
	 * @return TreeNode
	 */
	public TreeNode getRoot() {
		return root;
	}

}
