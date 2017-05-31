package com.sangupta.gather;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Gather is the main query class that a callee deals with. Once the query is
 * built up they can fire it on a collection of objects. For example,
 * <code>Gather query = Gather.where("name").is("sangupta").and("age").lessThan(40);</code>
 * is a valid query to be fired on a collection of objects.
 * 
 * @author sangupta
 *
 */
public class Gather {
	
	final List<GatherCriteria> criteria = new ArrayList<>();
	
	/**
	 * The field name over which the query clause will fire
	 */
	private String key;
	
	/**
	 * The default sibling join method for clauses
	 */
	private GatherSiblingJoin siblingJoin = GatherSiblingJoin.OR;
	
	private Gather(String key) {
		this.key = key;
	}

	public static Gather where(String name) {
		return new Gather(name);
	}
	
	public Gather and(String key) {
		if(this.key != null) {
			throw new IllegalArgumentException("Add a comparison condition to previous key first");
		}
		
		this.key = key;
		this.siblingJoin = GatherSiblingJoin.AND;
		return this;
	}

	public Gather or(String key) {
		if(this.key != null) {
			throw new IllegalArgumentException("Add a comparison condition to previous key first");
		}
		
		this.key = key;
		this.siblingJoin = GatherSiblingJoin.OR;
		return this;
	}

	/**
	 * Check if the attribute value is equivalent to the given value.
	 * 
	 * @param value
	 * @return
	 */
	public Gather is(Object value) {
		if(this.key == null) {
			throw new IllegalArgumentException("Operation needs a key to work upon");
		}
		
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.Equals, value, this.siblingJoin));
		this.key = null;
		return this;
	}
	
	/**
	 * Check if the attribute value is <code>null</code>.
	 * 
	 * @return
	 */
	public Gather isNull() {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.IsNull, null, this.siblingJoin));
		return this;
	}
	
	/**
	 * Check if the attribute value equals to the given value ignoring case.
	 * 
	 * @param value
	 * @return
	 */
	public Gather isIgnoreCase(String value) {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.EqualsIgnoreCase, value, this.siblingJoin));
		this.key = null;
		return this;
	}
	
	/**
	 * Check if the attribute value matches the given value as a wildcard pattern.
	 * 
	 * @param pattern
	 * @return
	 */
	public Gather like(String pattern) {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.WildcardMatch, pattern, this.siblingJoin));
		this.key = null;
		return this;
	}
	
	/**
	 * Check if the attribute value matches the given value as a regular-expression match.
	 * 
	 * @param pattern
	 * @return
	 */
	public Gather regex(String pattern) {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.RegexMatch, pattern, this.siblingJoin));
		this.key = null;
		return this;
	}
	
	public Gather greaterThan(Object value) {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.GreaterThan, value, this.siblingJoin));
		this.key = null;
		return this;
	}
	
	/**
	 * Execute the query over the given collection of objects.
	 * 
	 * @param collection
	 * @param classOfT
	 * @return
	 */
	public <T> List<T> execute(Collection<T> collection) {
		return GatherExecutor.getResults(collection, this);
	}
}