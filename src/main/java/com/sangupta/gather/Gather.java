/**
 *
 * gather: SQL queries for Java collections
 * Copyright (c) 2017, Sandeep Gupta
 *
 * https://sangupta.com/projects/gather
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.sangupta.gather;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

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
	
	private boolean inverse = false;
	
	// ***************************************
	// STATIC METHODS FOLLOW
	// ***************************************
	
	public static Gather where(String name) {
		return new Gather(name);
	}
	
	public static Number min(String key) {
		return null;
	}
	
	public static Number max(String key) {
		return null;
	}
	
	public static Double average(String key) {
		return null;
	}
	
	// ***************************************
	// INSTANCE METHODS FOLLOW
	// ***************************************
	
	private Gather(String key) {
		this.key = key;
	}

	public Gather and(String key) {
		if(this.key != null) {
			throw new IllegalArgumentException("Add a comparison condition to previous key first");
		}
		
		this.key = key;
		this.siblingJoin = GatherSiblingJoin.AND;
		return this;
	}
	
	public Gather not() {
		if(this.key == null) {
			throw new IllegalArgumentException("Define a key first");
		}
		
		this.inverse = true;
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
		
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.Equals, value, this.siblingJoin, this.inverse));
		this.key = null;
		return this;
	}
	
	/**
	 * Check if the attribute value is <code>null</code>.
	 * 
	 * @return
	 */
	public Gather isNull() {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.IsNull, null, this.siblingJoin, this.inverse));
		return this;
	}
	
	/**
	 * Check if the attribute value equals to the given value ignoring case.
	 * 
	 * @param value
	 * @return
	 */
	public Gather isIgnoreCase(String value) {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.EqualsIgnoreCase, value, this.siblingJoin, this.inverse));
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
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.WildcardMatch, pattern, this.siblingJoin, this.inverse));
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
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.RegexMatch, pattern, this.siblingJoin, this.inverse));
		this.key = null;
		return this;
	}
	
	/**
	 * Check if the attribute value matches the given value as a regular-expression match.
	 * 
	 * @param pattern
	 * @return
	 */
	public Gather regex(Pattern pattern) {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.RegexMatch, pattern, this.siblingJoin, this.inverse));
		this.key = null;
		return this;
	}
	
	public Gather greaterThan(Object value) {
		this.criteria.add(new GatherCriteria(this.key, GatherOperation.GreaterThan, value, this.siblingJoin, this.inverse));
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
	public <T> List<T> find(Collection<T> collection) {
		return GatherExecutor.getResults(collection, this);
	}
	
}
