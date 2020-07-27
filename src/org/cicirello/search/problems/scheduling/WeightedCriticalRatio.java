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
import org.cicirello.search.ss.PartialPermutation;

/**
 * <p>This is an implementation of a variation of the weighted critical ratio
 * heuristic.  The usual definition of this heuristic is:
 * h(j) = (w[j]/p[j])(1/(1+S(j)/p[j])),
 * where w[j] is the weight of job j, p[j] is its processing time,
 * and S(j) is a calculation of the slack of job j where slack S(j)
 * is d[j] - T - p[j] - s[j].  The d[j] is the job's due date, T is the
 * current time, and s[j] is any setup time of the job (for problems with
 * setup times).</p>  
 *
 * <p>Historically, this heuristic has been criticized for
 * allowing negative evaluations (i.e., slack S(j) is negative for jobs
 * completing late).  Additionally, this library's use of constructive
 * heuristics is for stochastic sampling, for which we require positive
 * heuristic values.  Therefore, we have altered the definition as 
 * follows: h(j) = (w[j]/p[j])(1/(1+max(0,S(j))/p[j])).</p>
 *
 * <p>Furthermore, the constant {@link #MIN_H} defines the minimum value
 * the heuristic will return, preventing h(j)=0 in support of stochastic
 * sampling algorithms for which h(j)=0 is problematic.  This implementation 
 * returns max( {@link #MIN_H}, h(j)), where {@link #MIN_H}
 * is a small non-zero value.</p>  
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.27.2020
 */
public final class WeightedCriticalRatio extends WeightedShortestProcessingTime {
	
	/**
	 * Constructs an WeightedCriticalRatio heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @throws IllegalArgumentException if problem.hasDueDates() returns false.
	 */
	public WeightedCriticalRatio(SingleMachineSchedulingProblem problem) {
		super(problem);
		if (!data.hasDueDates()) {
			throw new IllegalArgumentException("This heuristic requires due dates.");
		}
	}
	
	@Override
	public double h(PartialPermutation p, int element, IncrementalEvaluation incEval) {
		double value = super.h(p, element, incEval);
		if (value > MIN_H) {
			double s = ((IncrementalTimeCalculator)incEval).slack(element, p);
			if (s > 0) {
				value /= (1.0 + s / data.getProcessingTime(element));
				return value <= MIN_H ? MIN_H : value;
			}
		}		
		return value;
	}
	
	@Override
	public IncrementalEvaluation createIncrementalEvaluation() {
		return new IncrementalTimeCalculator();
	}
	
}