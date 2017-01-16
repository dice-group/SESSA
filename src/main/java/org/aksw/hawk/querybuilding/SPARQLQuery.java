package org.aksw.hawk.querybuilding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.hawk.datastructures.Answer;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.PathBlock;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.util.FmtUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SPARQLQuery implements Cloneable, Serializable {

	private static final long serialVersionUID = 6652694466896208327L;
	// prune by lemma for verbs
	private static HashSet<String> stopwords = Sets.newHashSet("of", "is", "and", "in", "name", "was", "did", "person", "location", "organization");
	public List<String> constraintTriples = Lists.newArrayList();
	public List<String> queryPatternTriples = Lists.newArrayList();
	public Set<String> filter = Sets.newHashSet();
	public PrefixMapping pm = null;
	public Map<String, Set<String>> textMapFromVariableToSingleFuzzyToken = Maps.newHashMap();
	public Map<String, Set<String>> textMapFromVariableToCombinedNNExactMatchToken = Maps.newHashMap();
	private boolean isASKQuery = false;
	private int limit = 1;

	public SPARQLQuery(String initialConstraint) {
		constraintTriples.add(initialConstraint);
	}
	public SPARQLQuery(Query lggQuery) {
		addPrefix(lggQuery.getPrefixMapping());
		addQueryPattern(lggQuery.getQueryPattern());		
	}
	/**
	 * only for clone()
	 */
	protected SPARQLQuery() {
	}

	public void isASKQuery(boolean isASKQuery) {
		this.isASKQuery = isASKQuery;
	}

	public void addConstraint(String constraint) {
		constraintTriples.add(constraint);
	}

	public void addFilterOverAbstractsContraint(String variable, String label) {
		fuzzyToken(variable, label);
		exactToken(variable, label);

	}
	/**
	 * for lgg
	 */
	public void addPrefix(PrefixMapping prefixMapping){
		this.pm = prefixMapping;		
	}
	/**
	 * for lgg
	 */	
	public PrefixMapping getPrefix(){
		return pm;
	}
	/**
	 * for lgg
	 */
	public void addQueryPattern(Element queryPattern){
		ElementGroup el = (ElementGroup) queryPattern;
		List<Element> list = el.getElements();
		if(list.size()!=1) {
			//return null;
		}
		Element ele = list.get(0);
		String queryPatternstr = null;
			PathBlock pb = ((ElementPathBlock) ele).getPattern();
			for (TriplePath tp : pb){
					
			Triple triple= tp.asTriple();
			Node predicate = triple.getPredicate();
			String predicateStr = FmtUtils.stringForNode(predicate);

			Node object = triple.getObject();
			String objectStr = FmtUtils.stringForNode(object);
			
			queryPatternstr = "?proj "+predicateStr+" "+objectStr+".";
			this.queryPatternTriples.add(queryPatternstr);
			}		
	}
	public void addQueryPattern(List<String> queryPatternTriples){
		for (String queryPattern : queryPatternTriples) {
		this.queryPatternTriples.add(queryPattern);
		}
	}
	public List<String> getQueryPattern(){
		return queryPatternTriples;
	}

	private void exactToken(String variable, String label) {
		// ?s text:query (<http://dbpedia.org/ontology/abstract> 'Mandela
		// anti-apartheid activist').

		// to search in a string with whitespaces like "Nobel Prize"
		if (textMapFromVariableToCombinedNNExactMatchToken.containsKey(variable)) {
			Set<String> set = textMapFromVariableToCombinedNNExactMatchToken.get(variable);
			set.add(label);
			textMapFromVariableToCombinedNNExactMatchToken.put(variable, set);
		} else {
			textMapFromVariableToCombinedNNExactMatchToken.put(variable, Sets.newHashSet(label));
		}
	}

	private void fuzzyToken(String variable, String label) {
		// ?s text:query (<http://dbpedia.org/ontology/abstract> 'Mandela
		// anti-apartheid activist').
		String[] separatedLabel = label.split("[ \\-]");
		// to search in a string with whitespaces like "Nobel Prize"
		if (textMapFromVariableToSingleFuzzyToken.containsKey(variable)) {
			Set<String> set = textMapFromVariableToSingleFuzzyToken.get(variable);
			for (String item : separatedLabel) {
				if (!item.isEmpty()) {
					set.add(item);
				}
			}
			textMapFromVariableToSingleFuzzyToken.put(variable, set);
		} else {
			Set<String> set = Sets.newHashSet();
			for (String item : separatedLabel) {
				if (!item.isEmpty()) {
					set.add(item);
				}
			}
			textMapFromVariableToSingleFuzzyToken.put(variable, set);
		}
	}

	public boolean constraintsContains(String target) {
		for (String c : constraintTriples) {
			if (c.contains(target)) {
				return true;
			}
		}
		return false;
	}

	public void addFilter(String string) {
		filter.add(string);		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		SPARQLQuery q = new SPARQLQuery();
		q.isASKQuery(isASKQuery);
		q.constraintTriples = Lists.newArrayList();
//		q.pm = this.pm;
//		q.queryPatternTriples = Lists.newArrayList();
//		
//		for (String queryPattern : this.queryPatternTriples) {
//			q.queryPatternTriples.add(queryPattern);
//		}
		for (String constraint : this.constraintTriples) {
			q.constraintTriples.add(constraint);
		}
		q.filter = Sets.newHashSet();
		for (String key : this.filter) {
			q.filter.add(key);
		}
		q.textMapFromVariableToSingleFuzzyToken = Maps.newHashMap();
		for (String key : this.textMapFromVariableToSingleFuzzyToken.keySet()) {
			Set<String> list = Sets.newHashSet(this.textMapFromVariableToSingleFuzzyToken.get(key));
			q.textMapFromVariableToSingleFuzzyToken.put(key, list);
		}
		q.textMapFromVariableToCombinedNNExactMatchToken = Maps.newHashMap();
		for (String key : this.textMapFromVariableToCombinedNNExactMatchToken.keySet()) {
			Set<String> list = Sets.newHashSet(this.textMapFromVariableToCombinedNNExactMatchToken.get(key));
			q.textMapFromVariableToCombinedNNExactMatchToken.put(key, list);
		}
		return q;
	}

	@Override
	public String toString() {
		return generateQueryStringWithExactMatch();
	}

	public Set<String> generateQueries() {
		Set<String> set = Sets.newHashSet();
		if (!textMapFromVariableToSingleFuzzyToken.isEmpty()) {
			String fuzzyQuery = generateQueryStringWithFuzzy();
			set.add(fuzzyQuery);
		}
		if (!textMapFromVariableToCombinedNNExactMatchToken.isEmpty()) {
			String exactQuery = generateQueryStringWithExactMatch();
			set.add(exactQuery);
		}

		return set;
	}

	private String generateQueryStringWithExactMatch() {
		StringBuilder sb = new StringBuilder();

		
		//Prefix from Lgg
		if(pm != null){
			for (String prefix : pm.getNsPrefixMap().keySet()) {
				sb.append("PREFIX ");
				sb.append(prefix);
				sb.append(": ");
				sb.append(FmtUtils.stringForURI(pm.getNsPrefixURI(prefix), null, null));
				sb.append('\n');
			}
		}
		sb.append("PREFIX text:<http://jena.apache.org/text#> \n");
		
		if (isASKQuery) {
			sb.append("ASK {\n ");
		} else {
			sb.append("SELECT DISTINCT ?proj WHERE {\n ");
		}
		for (String variable : textMapFromVariableToCombinedNNExactMatchToken.keySet()) {
			// ?s text:query (<http://dbpedia.org/ontology/abstract> 'Mandela
			// anti-apartheid activist').
			ArrayList<String> list = Lists.newArrayList(textMapFromVariableToCombinedNNExactMatchToken.get(variable));
			if (!list.isEmpty()) {
				sb.append(variable + " text:query (<http://dbpedia.org/ontology/abstract> '");
				StringBuilder fulltext = new StringBuilder();
				for (int i = 0; i < list.size(); i++) {
					// TODO photographer does not match photographers in index
					// temporary solution is a a hack with ~ for fuzzy
					if (i > 0 && fulltext.length() > 0) {
						fulltext.append(" AND ");
					}
					fulltext.append("\"" + list.get(i) + "\"");
				}
				sb.append(fulltext.toString());
				// return 100 uris from text index
				// TODO decrease that number by introducing a ranking factor
				sb.append("' " + 1000 + "). \n");
			}
		}
		//QueryPattern from Lgg
		if(queryPatternTriples != null){
			for (String queryPattern : queryPatternTriples) {
				sb.append(queryPattern + " \n");
			}
		}
		
		for (String constraint : constraintTriples) {
			sb.append(constraint + " \n");
		}
		for (String filterString : filter) {
			sb.append("FILTER (" + filterString + ").\n ");
		}
		sb.append("}\n");
		if (!isASKQuery) {
			sb.append("LIMIT " + limit);
		}
		return sb.toString();
	}
	
	
	
	

	private String generateQueryStringWithFuzzy() {
		StringBuilder sb = new StringBuilder();

		
		
		//Prefix from Lgg
		if(pm != null){
			for (String prefix : pm.getNsPrefixMap().keySet()) {
				sb.append("PREFIX ");
				sb.append(prefix);
				sb.append(": ");
				sb.append(FmtUtils.stringForURI(pm.getNsPrefixURI(prefix), null, null));
				sb.append('\n');
			}
		}
		sb.append("PREFIX text:<http://jena.apache.org/text#> \n");
		
		
		if (isASKQuery) {
			sb.append("ASK {\n ");
		} else {
			sb.append("SELECT DISTINCT ?proj WHERE {\n ");
		}
		for (String variable : textMapFromVariableToSingleFuzzyToken.keySet()) {
			// ?s text:query (<http://dbpedia.org/ontology/abstract> 'Mandela
			// anti-apartheid activist').
			ArrayList<String> list = Lists.newArrayList(textMapFromVariableToSingleFuzzyToken.get(variable));
			// Stopwords introduced to prevent Lucene from doing quatsch
			list.removeAll(stopwords);
			if (!list.isEmpty()) {
				sb.append(variable + " text:query (<http://dbpedia.org/ontology/abstract> '");
				StringBuilder fulltext = new StringBuilder();
				for (int i = 0; i < list.size(); i++) {
					// TODO photographer does not match photographers in index
					// temporary solution is a a hack with ~ for fuzzy
					if (i > 0 && fulltext.length() > 0) {
						fulltext.append(" AND ");
					}
					if (isInteger(list.get(i))) {
						fulltext.append(list.get(i));
					} else {
						fulltext.append(list.get(i) + "~1");
					}
				}
				sb.append(fulltext.toString());
				// return 100 uris from text index
				// TODO decrease that number by introducing a ranking factor
				sb.append("' " + 1000 + "). \n");
			}
		}
		//QueryPattern from Lgg
		if(queryPatternTriples != null){
			for (String queryPattern : queryPatternTriples) {
				sb.append(queryPattern + " \n");
			}
		}
		for (String constraint : constraintTriples) {
			sb.append(constraint + " \n");
		}
		for (String filterString : filter) {
			sb.append("FILTER (" + filterString + ").\n ");
		}
		sb.append("}\n");
		if (!isASKQuery) {
			sb.append("LIMIT " + limit);
		}
		return sb.toString();
	}

	// taken from
	// http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java
	private boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}

	public void setLimit(int cardinality) {
		this.limit = cardinality;

	}

	public Answer toAnswer() {
		Answer answer = new Answer();
		try {
			SPARQLQuery tmpQuery = (SPARQLQuery) this.clone();
			answer.query = tmpQuery;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return answer;
	}

}