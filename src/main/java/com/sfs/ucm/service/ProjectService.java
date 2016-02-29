package com.sfs.ucm.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.sfs.ucm.data.ModelNode;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.Actor;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.ProductRelease;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
import com.sfs.ucm.model.ViewSet;
import com.sfs.ucm.util.Service;

/**
 * Project Service
 * 
 * @author lbbisho
 * 
 */
@Service
@Stateless
public class ProjectService {

	@Inject
	private EntityManager em;

	@Inject
	private Logger logger;

	public List<ModelNode> findMemberProjects(final AuthUser authUser) throws UCMException {
		List<ModelNode> list = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ModelNode> cq = cb.createQuery(ModelNode.class);
			Root<ProjectMember> pm = cq.from(ProjectMember.class);
			cq.select(cb.construct(ModelNode.class, pm.get("project").get("name"), pm.get("project").get("id"), pm.get("project").get("description"))).orderBy(cb.asc(pm.get("project").get("id")));
			cq.where(cb.equal(pm.get("authUser"), authUser));
			list = em.createQuery(cq).getResultList();
		}
		catch (Exception e) {
			logger.error("Error occurred in findMemberProjects: {}", e.getMessage());
			throw new UCMException(e);
		}
		return list;
	}
	
	/**
	 * is user is a project member
	 * 
	 * @param authUser
	 * @param theProject
	 * @return flag true if user is project member
	 */
	public boolean userIsProjectMember(final AuthUser authUser, final Project theProject) {
		boolean userIsMember = false;

		if (authUser != null) {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProjectMember> c = cb.createQuery(ProjectMember.class);
			Root<ProjectMember> obj = c.from(ProjectMember.class);
			c.select(obj).where(cb.equal(obj.get("project"), theProject), cb.equal(obj.get("authUser"), authUser));
			List<ProjectMember> list = em.createQuery(c).getResultList();

			userIsMember = (list.size() > 0);
			logger.info("useris Member {}", userIsMember);
		}
		return userIsMember;
	}

	/**
	 * Update the project member state
	 * 
	 * @param projectId
	 * @param node
	 * @param state
	 */
	public void updateProjectMemberState(final Long projectId, final AuthUser authUser, final String node, final boolean state) throws UCMException {

		logger.debug("updateProjectMemberState {} {}", node, state);
		try {
			ProjectMember projectMember = findProjectMember(projectId, authUser);

			if (projectMember != null) {
				if ("Test Plan".equals(node)) {
					projectMember.setTestPlanCollapsed(state);
				}
				else if ("Use Cases".equals(node)) {
					projectMember.setUseCasesCollapsed(state);
				}
				else if ("Requirement Specifications".equals(node)) {
					projectMember.setRequirementsCollapsed(state);
				}
				else if ("Project Management".equals(node)) {
					projectMember.setProjectManagementCollapsed(state);
				}
				else if ("Project Estimation".equals(node)) {
					projectMember.setProjectEstimationCollapsed(state);
				}
				else if ("Test Sets".equals(node)) {
					projectMember.setTestSetsCollapsed(state);
				}

				em.persist(projectMember);
			}
		}
		catch (Exception e) {
			logger.error("Error occurred persisting projectMember {}", e.getMessage());
			throw new UCMException(e.getMessage());
		}
	}

	/**
	 * Update project open/close state
	 * 
	 * @param projectId
	 * @param authUser
	 * @param state
	 */
	public void updateProjectState(final Long projectId, final AuthUser authUser, final boolean state) {

		try {
			ProjectMember projectMember = findProjectMember(projectId, authUser);
			if (projectMember != null) {
				projectMember.setProjectOpen(state);
				em.persist(projectMember);
			}
		}
		catch (UCMException e) {
			logger.error("Error occurred persisting projectMember {}", e.getMessage());
		}

	}

	/**
	 * Find the System actor for project
	 * 
	 * @param project
	 * @return System actor or null if not found
	 * @throws UCMException
	 */
	public Actor findSystemActor(final Project project) throws UCMException {
		Actor actor = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Actor> c = cb.createQuery(Actor.class);
			Root<Actor> obj = c.from(Actor.class);
			c.select(obj).where(cb.equal(obj.get("project"), project), cb.equal(obj.get("name"), "System"));
			List<Actor> list = em.createQuery(c).getResultList();

			Iterator<Actor> iter = list.iterator();
			if (iter.hasNext()) {
				actor = iter.next();
			}
		}
		catch (Exception e) {
			logger.error("findProjectActor error {}", e.getMessage());
			throw new UCMException(e.getMessage());
		}
		return actor;
	}

	/**
	 * Find the Project Member
	 * 
	 * @param project
	 * @param authUser
	 * @return System actor or null if not found
	 * @throws UCMException
	 */
	public ProjectMember findProjectMember(final Project project, final AuthUser authUser) throws UCMException {
		ProjectMember projectMember = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProjectMember> c = cb.createQuery(ProjectMember.class);
			Root<ProjectMember> obj = c.from(ProjectMember.class);
			c.select(obj).where(cb.equal(obj.get("project"), project), cb.equal(obj.get("authUser"), authUser));
			List<ProjectMember> list = em.createQuery(c).getResultList();

			Iterator<ProjectMember> iter = list.iterator();
			if (iter.hasNext()) {
				projectMember = iter.next();
			}
		}
		catch (Exception e) {
			logger.error("findProjectMember error {}", e.getMessage());
			throw new UCMException(e.getMessage());
		}
		return projectMember;
	}

	/**
	 * Find the Project Member
	 * 
	 * @param projectId
	 * @param authUser
	 * @return projectMember or null if not found
	 * @throws UCMException
	 */
	public ProjectMember findProjectMember(final Long projectId, final AuthUser authUser) throws UCMException {
		ProjectMember projectMember = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProjectMember> c = cb.createQuery(ProjectMember.class);
			Root<ProjectMember> obj = c.from(ProjectMember.class);
			c.select(obj).where(cb.equal(obj.get("project").get("id"), projectId), cb.equal(obj.get("authUser"), authUser));
			List<ProjectMember> list = em.createQuery(c).getResultList();

			Iterator<ProjectMember> iter = list.iterator();
			if (iter.hasNext()) {
				projectMember = iter.next();
			}
		}
		catch (Exception e) {
			logger.error("findProjectMember error {}", e.getMessage());
			throw new UCMException(e.getMessage());
		}
		return projectMember;
	}

	/**
	 * Find the Project by identifier, eager fetches Product Releases
	 * 
	 * @param projectId
	 * @return Project or null if not found
	 * @throws UCMException
	 */
	public Project findProjectById(final Long projectId) throws UCMException {
		Project project = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Project> c = cb.createQuery(Project.class);
			Root<Project> obj = c.from(Project.class);
			obj.fetch("productReleases");
			c.select(obj).where(cb.equal(obj.get("id"), projectId));
			List<Project> list = em.createQuery(c).getResultList();

			Iterator<Project> iter = list.iterator();
			if (iter.hasNext()) {
				project = iter.next();
			}
		}
		catch (Exception e) {
			logger.error("findProjectById error {}", e.getMessage());
			throw new UCMException(e.getMessage());
		}
		return project;
	}

	/**
	 * Find list of project product release versions that are included in authUser active viewset product release
	 * 
	 * @param authUser
	 * @apram project
	 * @return versions if no viewset versions found, versions is populated with project product release versions
	 * @throws UCMException
	 */
	public Set<String> findActiveProductReleaseVersions(final AuthUser authUser, final Project project) throws UCMException {

		Set<String> versions = new HashSet<String>();

		// obtain active viewset product release of authUser
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ViewSet> c = cb.createQuery(ViewSet.class);
		Root<ViewSet> obj = c.from(ViewSet.class);
		c.select(obj).where(cb.equal(obj.get("authUser"), authUser), cb.isTrue(obj.<Boolean> get("active"))).orderBy(cb.asc(obj.get("id")));
		List<ViewSet> viewSets = em.createQuery(c).getResultList();
		Iterator<ViewSet> iter = viewSets.iterator();
		if (iter.hasNext()) {
			ViewSet viewSet = iter.next();
			String version = viewSet.getProductRelease().getVersion();

			// construct set of release versions in project that evaluate lexicographically less than or equal to the viewset release version
			for (ProductRelease productRelease : project.getProductReleases()) {
				if (productRelease.getVersion().compareTo(version) <= 0) {
					versions.add(productRelease.getVersion());
				}
			}
		}
		else {
			for (ProductRelease productRelease : project.getProductReleases()) {
				versions.add(productRelease.getVersion());
			}
		}

		// return the set of versions
		logger.debug("version set: {}", versions);

		return versions;
	}

}
