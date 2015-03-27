package com.sfs.ucm.service;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import com.sfs.ucm.util.Service;

@Service
@Stateful
public class PersistenceService {
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	/**
	 * Persist a transient entity
	 * @param entity
	 */
	public <T> void persist(final T entity) {
		em.persist(entity);
	}

	/**
	 * Delete a persisted entity
	 * @param entity
	 */
	public <T> void delete(final T entity) {
		em.remove(entity);
	}

	/**
	 * Find entity by id
	 * @param type
	 * @param id
	 * @return entity
	 */
	public <T> T findById(final Class<T> type, final Long id) {
		return (T) em.find(type, id);
	}

	/**
	 * Update a persisted entity
	 * @param entity
	 */
	public <T> void update(final T entity) {
		em.merge(entity);
	}
}
