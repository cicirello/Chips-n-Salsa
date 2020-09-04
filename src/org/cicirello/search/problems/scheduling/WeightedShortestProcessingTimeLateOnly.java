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

package org.cicirello.search.problems.scheduling;

import org.cicirello.permutations.Permutation;
import org.cicirello.search.ss.IncrementalEvaluation;
import org.cicirello.search.ss.Partial;

/**
 * This is an implementation of the weighted shortest process time
 * heuristic.  
 * There are two variations of the weighted shortest processing time
 * heuristic.  The basic form (implemented in {@link WeightedShortestProcessingTime})
 * is defined as: 
 * h(j) = w[j] / p[j],
 * where w[j] is the weight of job j, and p[j] is its processing time.
 * However, for some scheduling cost functions, performance is improved
 * if the heuristic is modified as follows.  If the job j is already late
 * (i.e., its due date d[j] &lt; T, where T is the current time), then
 * its heuristic value is: h(j) = w[j] / p[j], just like in the basic 
 * version.  Otherwise, if the due date hasn't passed yet, then h(j) = 0.
 * This implementation alters this definition slightly as:
 * If job j is already late, then h(j) = max( {@link #MIN_H}, w[j] / p[j]), 
 * where {@link #MIN_H}
 * is a small non-zero value.  If the job j is not yet late, then
 * h(j) = {@link #MIN_H}.  For deterministic construction of a 
 * schedule, this adjustment is unnecessary.  However, for stochastic sampling
 * algorithms it is important for the heuristic to return positive values.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.4.2020
 */
public final class WeightedShortestProcessingTimeLateOnly extends WeightedShortestProcessingTime {
	
	/**
	 * Constructs an WeightedShortestProcessingTimeLateOnly heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @throws IllegalArgumentException if problem.hasDueDates() returns false.
	 */
	public WeightedShortestProcessingTimeLateOnly(SingleMachineSchedulingProblem problem) {
		super(problem);
		if (!data.hasDueDates()) {
			throw new IllegalArgumentException("This heuristic requires due dates.");
		}
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		if (((IncrementalTimeCalculator)incEval).slack(element, p) >= 0) {
			return MIN_H;
		} 
		return super.h(p, element, incEval);
	}
	
	@Override
	public IncrementalEvaluation<Permutation> createIncrementalEvaluation() {
		return new IncrementalTimeCalculator();
	}
	
}