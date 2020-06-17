/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
 *
 * This file is part of Chips-n-Salsa (https://chips-n-salsa.cicirello.org/).
 * 
 * Chips-n-Salsa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Chips-n-Salsa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.cicirello.search.operators;

/**
 * <p>Defines an interface for iterating over all of the mutants (i.e., neighbors)
 * of a candidate solution to a problem.  MutationIterators are used in 
 * combination with {@link IterableMutationOperator} objects.</p>
 *
 * <p>Example 1: Here is an example of its use.  In this first example,
 * we iterate over all neighbors.  At the completion of this block, the
 * state of x will be as of the most recent call to <code>setSavepoint()</code>
 * or its original state if that method was never called.</p>
 *
 * <pre><code>
 * T x = some object of type T.
 * IterableMutationOperator&lt;T&gt; mutation = ....
 * MutationIterator iter = mutation.iterator(x);
 * while (iter.hasNext()) {
 *     iter.nextMutant();
 *     if (new state of x is one we'd like to be able to revert to) {
 *          iter.setSavepoint();
 *     } 
 * }
 * // This next statement rolls x back to the last savepoint.
 * iter.rollback(); 
 * </code></pre>
 *
 *<p>Example 2: In this next example,
 * we iterate over neighbors only until we find one we like.</p>
 *
 * <pre><code>
 * T x = some object of type T.
 * IterableMutationOperator&lt;T&gt; mutation = ....
 * MutationIterator iter = mutation.iterator(x);
 * boolean foundOneToKeep = false;
 * while (iter.hasNext()) {
 *     iter.nextMutant();
 *     if (new state of x is one we'd like to keep) {
 *          foundOneToKeep = true;
 *          break;
 *     } 
 * }
 * if (!foundOneToKeep) iter.rollback(); 
 * </code></pre>
 *
 * @since 1.0
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 10.4.2019
 */
public interface MutationIterator {
	
	/**
	 * Checks whether there are any additional neighbors
	 * of the candidate solution.
	 * @return true if there are additional neighbors that can
	 * be iterated over via the {@link #nextMutant} method, provided
	 * the {@link #rollback} method has not been called.
	 */
	boolean hasNext();
	
	/**
	 * Mutates the candidate solution into its next neighbor.
	 * @throws IllegalStateException if there are no additional neighbors to iterate over
	 * or if the {@link #rollback} method was called.  You should use the {@link #hasNext}
	 * method to check first.
	 */
	void nextMutant();
	
	/**
	 * Records internally within the MutationIterator the current neighbor/mutant,
	 * enabling reverting back to this neighbor when the {@link #rollback}
	 * method is called.  This does not affect the order of neighbors returned by future
	 * calls to the {@link #nextMutant} method.
	 */
	void setSavepoint();
	
	/**
	 * Reverts the candidate solution to its state as of the most recent call to the
	 * {@link #setSavepoint} method, or its original state if that method has
	 * not been called.  Upon calling the rollback method, all future calls
	 * to the {@link #nextMutant} method will throw an IllegalStateException.
	 * If rollback is not called, then the candidate solution's state will be
	 * as of the most recent call to {@link #nextMutant}.
	 */
	void rollback();
}