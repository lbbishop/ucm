package com.sfs.ucm.service;

import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.AuthUser;
import com.sfs.ucm.util.Service;

@Service
@Stateless
public class AuthUserService {

	@Inject
	private Logger logger;

	@PersistenceContext
	private EntityManager em;

	/**
	 * Find all AuthUsers
	 * 
	 * @return List of AuthUser
	 * @throws UCMException
	 */
	public List<AuthUser> findAllAuthUsers() throws UCMException {
		List<AuthUser> list = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<AuthUser> cq = cb.createQuery(AuthUser.class);
			Root<AuthUser> r = cq.from(AuthUser.class);
			r.fetch("authRoles");
			cq.select(r);
			list = em.createQuery(cq).getResultList();

		}
		catch (Exception e) {
			logger.error("Error occurred in findAllAuthUsers: {}", e.getMessage());
			throw new UCMException("Error occurred in findAllAuthUsers: " + e.getMessage(), e);
		}
		return list;
	}

	/**
	 * Find AuthUser by name
	 * 
	 * @param username
	 * @return AuthUser null if not found
	 * @throws UCMException
	 */
	public AuthUser findUserByName(final String username) throws UCMException {
		AuthUser authUser = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<AuthUser> cq = cb.createQuery(AuthUser.class);
			Root<AuthUser> r = cq.from(AuthUser.class);
			//r.fetch("authRoles");
			cq.select(r).where(cb.equal(r.get("username"), username));
			List<AuthUser> list = em.createQuery(cq).getResultList();
			Iterator<AuthUser> iter = list.iterator();
			if (iter.hasNext()) {
				authUser = iter.next();
			}
		}
		catch (Exception e) {
			logger.error("Error occurred in findUserByName: {}", e.getMessage());
			throw new UCMException("Error occurred in findUserByName: " + e.getMessage(), e);
		}

		return authUser;
	}

	/**
	 * Persist authUser
	 * 
	 * @param authUser
	 * @throws UCMException
	 */
	public void store(AuthUser authUser) throws UCMException {
			em.persist(authUser);
	}
}
