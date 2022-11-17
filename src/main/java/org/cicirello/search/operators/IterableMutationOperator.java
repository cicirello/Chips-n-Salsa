/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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
 * Implement the IterableMutationOperator interface to define a mutation operator that enables
 * iterating systematically over the neighbors of a candidate solution, like one would do in a hill
 * climber.
 *
 * <p>Example 1: Here is an example of its use. In this first example, we iterate over all
 * neighbors. At the completion of this block, the state of x will be as of the most recent call to
 * <code>setSavepoint()</code> or its original state if that method was never called.
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
 * <p>Example 2: In this next example, we iterate over neighbors only until we find one we like.
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
 * @param <T> The type of object used to represent candidate solutions to the problem.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface IterableMutationOperator<T> extends MutationOperator<T> {

  /**
   * Creates and returns a {@link MutationIterator} that can be used to systematically iterate over
   * all of the direct neighbors (i.e., a single mutation step away) of a candidate solution, as one
   * might do in a hill climber.
   *
   * @param c The candidate solution subject to the mutation. Calling methods of the {@link
   *     MutationIterator} that is returned changes the state of that candidate solution. See the
   *     documentation of those methods for details of how such changes may occur.
   * @return A MutationIterator for iterating over the direct neighbors of a candidate solution.
   */
  MutationIterator iterator(T c);

  @Override
  IterableMutationOperator<T> split();
}
