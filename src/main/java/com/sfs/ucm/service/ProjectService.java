package com.sfs.ucm.service;

import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.Actor;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.ProjectMember;
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

	/**
	 * Find all project members
	 * 
	 * @return projectMembers
	 * @throws UCMException
	 */
	public List<ProjectMember> findAllProjectMembers() throws UCMException {
		List<ProjectMember> list = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProjectMember> c = cb.createQuery(ProjectMember.class);
			Root<ProjectMember> obj = c.from(ProjectMember.class);
			c.select(obj).orderBy(cb.asc(obj.get("id")));
			list = em.createQuery(c).getResultList();
		}
		catch (Exception e) {
			logger.error("Error occurred in findAllProjectMembers: {}", e.getMessage());
			throw new UCMException(e);
		}
		return list;
	}

	/**
	 * Find member projects
	 * 
	 * @param authUser
	 * @return
	 * @throws UCMException
	 */
	public List<Project> findMemberProjects(final AuthUser authUser) throws UCMException {
		List<Project> list = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Project> cq = cb.createQuery(Project.class);
			Root<ProjectMember> obj = cq.from(ProjectMember.class);
			cq.select(obj.get("project")).orderBy(cb.asc(obj.get("project").get("name")));
			cq.where(cb.equal(obj.get("authUser"), authUser));
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

}
