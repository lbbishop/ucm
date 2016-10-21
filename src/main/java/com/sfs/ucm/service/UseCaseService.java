package com.sfs.ucm.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.model.Flow;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.UseCase;
import com.sfs.ucm.util.Service;

/**
 * Use Case Service
 * 
 * @author lbbisho
 * 
 */
@Service
@Stateless
public class UseCaseService {

	@Inject
	private EntityManager em;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	private Logger logger;

	/**
	 * Get count of basic flows
	 * 
	 * @param authUser
	 * @param project
	 * @return count
	 * @throws UCMException
	 */
	public Long getBasicFlowCount(final AuthUser authUser, final Project project) throws UCMException {
		Long cnt = 0L;
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> c = cb.createQuery(Long.class);
			Root<UseCase> obj = c.from(UseCase.class);
			c.select(cb.count(obj)).where(cb.equal(obj.get("project"), project));
			cnt = em.createQuery(c).getSingleResult();
		}
		catch (Exception e) {
			logger.error("Error occurred in getBasicFlowCount: {}", e.getMessage());
			throw new UCMException(e);
		}
		return cnt;
	}

	/**
	 * Get count of alternative flows
	 * 
	 * @param authUser
	 * @param project
	 * @return count
	 * @throws UCMException
	 */
	public Long getAlternativeFlowCount(final AuthUser authUser, final Project project) throws UCMException {
		Long cnt = 0L;
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> c = cb.createQuery(Long.class);
			Root<Flow> obj = c.from(Flow.class);
			c.select(cb.count(obj)).where(cb.equal(obj.get("useCase").get("project"), project));
			cnt = em.createQuery(c).getSingleResult();
		}
		catch (Exception e) {
			logger.error("Error occurred in getAlternativeFlowCount: {}", e.getMessage());
			throw new UCMException(e);
		}
		return cnt;
	}
	
	/**
	 * Find use case alternative flows
	 * 
	 * @param useCase
	 * @return List of Alternative Flows
	 * @throws UCMException
	 */
	public List<Flow> findUseCaseAlternativeFlows(final UseCase useCase) throws UCMException {
		List<Flow> list = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Flow> c = cb.createQuery(Flow.class);
			Root<Flow> obj = c.from(Flow.class);
			c.select(obj).where(cb.equal(obj.get("useCase"), useCase));
			list = em.createQuery(c).getResultList();
		}
		catch (Exception e) {
			logger.error("Error occurred in findUseCaseAlternativeFlows: {}", e.getMessage());
			throw new UCMException(e);
		}
		return list;
	}
}
