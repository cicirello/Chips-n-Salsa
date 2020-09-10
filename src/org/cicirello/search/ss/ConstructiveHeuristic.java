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

package org.cicirello.search.ss;

import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * <p>Classes implementing this interface are used
 * as constructive heuristics for constructing heuristic solutions
 * to optimization problems, as well as for
 * certain stochastic sampling search algorithms.</p>
 *
 * @param <T> The type of Partial object for which this 
 * ConstructiveHeuristic guides construction, which is 
 * assumed to be an object that is a sequence of integers (e.g., vector of integers,
 * permutation, or some other indexable type that stores integers).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.4.2020
 */
public interface ConstructiveHeuristic<T extends Copyable<T>> {
	
	/**
	 * Heuristically evaluates the possible addition of an element to the
	 * end of a Partial.  Higher evaluations imply that the element is
	 * a better choice for the next element to add.  For example, if you evaluate
	 * two elements, x and y, with h, and h returns a higher value for y than for x,
	 * then this means that y is believed to be the better choice according to the 
	 * heuristic. Implementations of this interface must ensure that h always 
	 * returns a positive result. This is because stochastic sampling algorithms
	 * such as HBSS and VBSS assume that the constructive heuristic returns only
	 * positive values.
	 * @param p The current state of the Partial
	 * @param element The element under consideration for adding to the Partial
	 * @param incEval An IncrementalEvaluation of p.  This method assumes that incEval
	 * is of the same runtime type as the object returned by {@link #createIncrementalEvaluation}.
	 * @return The heuristic evaluation of the hypothetical addition of element to the end
	 * of p. The higher the evaluation, the more important the heuristic believes that
	 * element should be added next.  The intention is to compare the value returned
	 * with the heuristic evaluations of other elements.  Individual results in isolation
	 * are not necessarily meaningful.
	 * @throws ClassCastException if incEval is not of the same runtime type as the
	 * objects returned by the {@link #createIncrementalEvaluation} method of the
	 * class implementing this interface
	 */
	double h(Partial<T> p, int element, IncrementalEvaluation<T> incEval);
	
	/**
	 * <p>Creates an IncrementalEvaluation object corresponding to an initially
	 * empty Partial for use in incrementally constructing a solution
	 * to the problem for which this heuristic is designed. The object returned 
	 * incrementally computes any data associated with a Partial as
	 * needed by the {@link #h} method.  The {@link #h} method will assume that 
	 * it will be given an object of the specific runtime type returned by this
	 * method.  It is unsafe to pass IncrementalEvaluation objects created by
	 * one heuristic to the {@link #h} method of another.</p>
	 *
	 * <p>The default implementation simply returns null, which is appropriate for
	 * heuristics that won't benefit from incrementally computing heuristic information.</p>
	 *
	 * @return An IncrementalEvaluation for an empty Partial 
	 * to be used for incrementally computing any data required by the {@link #h} method.
	 */
	default IncrementalEvaluation<T> createIncrementalEvaluation() {
		return null;
	}
	
	/**
	 * Creates an empty Partial solution, which will be incrementally
	 * transformed into a complete solution of a specified length.
	 * @param n the desired length of the final complete solution.
	 * @return an empty Partial solution
	 */
	Partial<T> createPartial(int n);
	
	/**
	 * Gets the required length of complete solutions 
	 * to the problem instance for which this constructive heuristic
	 * is configured.
	 * @return length of solutions to the problem instance for which this heuristic
	 * is configured
	 */
	int completeLength();
	
	/**
	 * Gets a reference to the instance of the optimization problem 
	 * that is the subject of this heuristic.
	 * @return the instance of the optimization problem 
	 * that is the subject of this heuristic.
	 */
	Problem<T> getProblem();
}