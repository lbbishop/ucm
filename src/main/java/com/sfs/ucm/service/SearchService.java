package com.sfs.ucm.service;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.IndexSearcher;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;

import com.sfs.ucm.exception.UCMException;
import com.sfs.ucm.util.Service;

/**
 * Full text search Service
 * 
 * @author lbbisho
 * 
 */
@Service
@Stateless
public class SearchService {

	@Inject
	private EntityManager em;

	@Inject
	private Logger logger;

	/**
	 * Re-index search terms
	 * 
	 */
	public void rebuildSearchIndex() throws UCMException {

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

		try {
			fullTextEntityManager.createIndexer().startAndWait();
		}
		catch (InterruptedException e) {
			logger.error("Build of search index failed: {}", e.getMessage());
			throw new UCMException(e.getMessage());
		}
	}

	/**
	 * Find and extract auto-complete suggestion terms
	 * 
	 * @param indexSearcher
	 * @param field
	 * @param searchString
	 * @param analyzer
	 * @return suggestion set
	 * @throws SwcException
	 */
	public Set<String> findSuggestionTokens(final IndexSearcher indexSearcher, final String field, final String searchString, final Analyzer analyzer) throws UCMException {

		Set<String> suggestionSet = new HashSet<String>();
//		try {
//
//			// tokenize the search string selecting last token to analyze
//
//			// lucene wildcard search does not invoke the analyzer
//			String qstr = searchString.trim().concat("*");
//
//			if (!qstr.startsWith("?") && !qstr.startsWith("*")) {
//				QueryParser queryParser = new QueryParser(Version.LUCENE_35, field, analyzer);
//				queryParser.setDefaultOperator(Operator.AND);
//				Query query = queryParser.parse(qstr);
//
//				TopDocs topDocs = indexSearcher.search(query, null, 20);
//
//				// log message
//				if (topDocs.scoreDocs.length > 0) {
//					Object[] params = new Object[4];
//					params[0] = searchString;
//					params[1] = field;
//					params[2] = qstr;
//					params[3] = topDocs.scoreDocs.length;
//					logger.info("findSuggestionTokens: searchString:[{}]field:[{}]qstr:[{}]hits:[{}]", params);
//				}
//
//				for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
//					Document doc = indexSearcher.doc(scoreDoc.doc);
//					String docvalue = doc.get(field);
//
//					if (docvalue != null) {
//						StringTokenizer st = new StringTokenizer(docvalue, ", .;:\"");
//						while (st.hasMoreTokens()) {
//							String token = st.nextToken();
//							if (token.toLowerCase().startsWith(searchString)) {
//								// we have a suggestion token
//								suggestionSet.add(token);
//							}
//						}
//
//						// apply StandardAnalyzer to suggestion tokens
//						Iterator<String> iter = suggestionSet.iterator();
//						while (iter.hasNext()) {
//							String token = iter.next();
//							query = new QueryParser(field, analyzer).parse(token);
//							topDocs = indexSearcher.search(query, null, 20);
//
//							if (topDocs.scoreDocs.length == 0) {
//								iter.remove();
//							}
//						}
//					}
//				}
//
//			}
//		}
//		catch (ParseException e) {
//			throw new UCMException(e);
//		}
//		catch (IOException e) {
//			throw new UCMException(e);
//		}

		return suggestionSet;

	}

}
