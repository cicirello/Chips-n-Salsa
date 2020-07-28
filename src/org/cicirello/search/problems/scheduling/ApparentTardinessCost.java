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
 * <p>This is an implementation of the Apparent Tardiness Cost (ATC)
 * heuristic.   
 * ATC is defined as:
 * h(j) = (w[j]/p[j]) exp( -max(0,S(j)) / (k p&#772;) ),
 * where w[j] is the weight of job j, p[j] is its processing time,
 * and S(j) is a calculation of the slack of job j where slack S(j)
 * is d[j] - T - p[j] - s[j].  The d[j] is the job's due date, T is the
 * current time, and s[j] is any setup time of the job (for problems with
 * setup times).  The k is a parameter that can be tuned based on 
 * problem instance characteristics, and p&#772; is the average processing
 * time of remaining unscheduled jobs.</p>  
 *
 * <p>The constant {@link #MIN_H} defines the minimum value
 * the heuristic will return, preventing h(j)=0 in support of stochastic
 * sampling algorithms for which h(j)=0 is problematic.  This implementation 
 * returns max( {@link #MIN_H}, h(j)), where {@link #MIN_H}
 * is a small non-zero value.</p>  
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.27.2020
 */
public final class ApparentTardinessCost extends WeightedShortestProcessingTime {
	
	private final double k;
	
	/**
	 * Constructs an ApparentTardinessCost heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @param k A parameter to the heuristic, which must be positive.  Typical good
	 * values are in the interval [1.0, 4.0] but it is not limited to that interval.
	 * @throws IllegalArgumentException if problem.hasDueDates() returns false.
	 * @throws IllegalArgumentException if k &le; 0.0.
	 */
	public ApparentTardinessCost(SingleMachineSchedulingProblem problem, double k) {
		super(problem);
		if (!data.hasDueDates()) {
			throw new IllegalArgumentException("This heuristic requires due dates.");
		}
		if (k <= 0.0) throw new IllegalArgumentException("k must be positive");
		this.k = k;
	}
	
	/**
	 * Constructs an ApparentTardinessCost heuristic.  Uses a default of k=2.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @throws IllegalArgumentException if problem.hasDueDates() returns false.
	 */
	public ApparentTardinessCost(SingleMachineSchedulingProblem problem) {
		this(problem, 2.0);
	}
	
	@Override
	public double h(PartialPermutation p, int element, IncrementalEvaluation incEval) {
		double value = super.h(p, element, incEval);
		if (value > MIN_H) {
			double s = ((IncrementalAverageProcessingCalculator)incEval).slack(element, p);
			if (s > 0) {
				double correction = Math.exp(-s / 
					(k * 
					  ((IncrementalAverageProcessingCalculator)incEval).averageProcessingTime())
				);
				if (correction <= 0.0) return MIN_H;
				value *= correction;
				return value <= MIN_H ? MIN_H : value;
			}
		}		
		return value;
	}
	
	@Override
	public IncrementalEvaluation createIncrementalEvaluation() {
		return new IncrementalAverageProcessingCalculator();
	}
	
}