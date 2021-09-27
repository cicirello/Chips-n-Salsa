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
import org.cicirello.search.ss.Partial;
import org.cicirello.search.ss.IncrementalEvaluation;

/**
 * This is an implementation of the weighted shortest process time
 * heuristic.  This heuristic is usually defined as: h(j) = w[j] / p[j],
 * where w[j] is the weight of job j, and p[j] is its processing time.
 * This implementation alters this definition slightly as:
 * h(j) = max( {@link #MIN_H}, w[j] / p[j]), where {@link #MIN_H}
 * is a small non-zero value.  This is to deal with the possibility of
 * a job with weight w[j] = 0.  For deterministic construction of a 
 * schedule, this adjustment is unnecessary.  However, for stochastic sampling
 * algorithms it is important for the heuristic to return positive values.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.4.2020
 */
public class WeightedShortestProcessingTime extends SchedulingHeuristic {
	
	private final double[] h;
	
	/**
	 * Constructs an WeightedShortestProcessingTime heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 */
	public WeightedShortestProcessingTime(SingleMachineSchedulingProblem problem) {
		super(problem);
		// pre-compute h and cache results.
		h = new double[data.numberOfJobs()];
		for (int i = 0; i < h.length; i++) {
			h[i] = data.getWeight(i);
			if (h[i] < MIN_H) h[i] = MIN_H;
			else {
				h[i] /= data.getProcessingTime(i);
				if (h[i] < MIN_H) h[i] = MIN_H;
			}
		}
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		return h[element];
	}
}