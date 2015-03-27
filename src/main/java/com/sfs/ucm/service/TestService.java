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

import com.sfs.ucm.bean.TestResult;
import com.sfs.ucm.data.TestResultType;
import com.sfs.ucm.data.TestType;
import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.model.Project;
import com.sfs.ucm.model.TestCase;
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
public class TestService {

	@Inject
	private EntityManager em;

	@Inject
	@Service
	private ProjectService projectService;

	@Inject
	private Logger logger;

	/**
	 * Find the most recent test result
	 * 
	 * @param identifier
	 * @param project
	 * @param passFail
	 * @return TestResult null if not found
	 * @throws UCMException
	 */
	public TestResult findMostRecentTestResultByIdentifier(final int identifier, final Project project, final List<TestResultType> passFail) throws UCMException {

		TestResult testResult = null;

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TestResult> c = cb.createQuery(TestResult.class);
			Root<TestCase> obj = c.from(TestCase.class);
			c.select(cb.construct(TestResult.class, obj.get("identifier"), cb.literal(TestType.TestCase.toString()), obj.get("testSet").get("name"), obj.get("name"), obj.get("testSet").get("tester")
					.get("authUser").get("name"), obj.get("testDate"), obj.get("testResultType")));
			c.where(cb.equal(obj.get("testSet").get("testPlan").get("project"), project), cb.equal(obj.get("identifier"), identifier), obj.get("testResultType").in(passFail)).orderBy(
					cb.desc(obj.get("testDate")));
			List<TestResult> list = em.createQuery(c).getResultList();

			// use the first (most recent test result)
			Iterator<TestResult> iter = list.iterator();
			if (iter.hasNext()) {
				testResult = iter.next();
			}
		}
		catch (Exception e) {
			logger.error("findMostRecentTestResultByIdentifier: Error occurred {}", e.getMessage());
			throw new UCMException(e);
		}

		return testResult;
	}

	/**
	 * Delete test case associated with use case
	 * 
	 * @param useCase
	 * @throws UCMException
	 */
	public void deleteTestCase(final UseCase useCase) throws UCMException {

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TestCase> c = cb.createQuery(TestCase.class);
			Root<TestCase> obj = c.from(TestCase.class);
			c.select(obj).where(cb.equal(obj.get("useCase"), useCase));
			List<TestCase> list = em.createQuery(c).getResultList();

			Iterator<TestCase> iter = list.iterator();
			if (iter.hasNext()) {
				TestCase testCase = iter.next();
				em.remove(testCase);
				em.flush();
			}
		}
		catch (Exception e) {
			logger.error("deleteTestCase error {}", e.getMessage());
			throw new UCMException(e.getMessage());
		}
	}

}
