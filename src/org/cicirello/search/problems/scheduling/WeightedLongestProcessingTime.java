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
 * This is an implementation of the weighted longest process time
 * heuristic.  This heuristic is usually defined as choose the job j
 * with <b>smallest</b> value of: h(j) = w[j] / p[j],
 * where w[j] is the <b>earliness</b> weight of job j, and p[j] is its processing time.
 * However, our constructive heuristic implementation, as well as our stochastic
 * sampling implementations that rely on constructive heuristics assume that
 * larger heuristic values are preferred.  The stochastic sampling algorithms
 * also assume that the heuristic's valuations are positive.
 * So we redefine it as follows:
 * h(j) = S - w[j] / p[j], where as before w[j] is the <b>earliness</b> weight of 
 * job j, and p[j] is its processing time.  S is computed as: 
 * S = {@link #MIN_H} - min(w[j] / p[j]).
 * The {@link #MIN_H}
 * is a small non-zero value.  In this way, we shift all of the h(j) values
 * by a constant amount such that all h(j) values are positive.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 10.12.2020
 */
public class WeightedLongestProcessingTime extends SchedulingHeuristic {
	
	private final double[] h;
	
	/**
	 * Constructs a WeightedLongestProcessingTime heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 */
	public WeightedLongestProcessingTime(SingleMachineSchedulingProblem problem) {
		super(problem);
		// pre-compute h and cache results.
		h = new double[data.numberOfJobs()];
		double minimum = 0;
		for (int i = 0; i < h.length; i++) {
			h[i] = -data.getEarlyWeight(i) / (double)data.getProcessingTime(i);
			if (h[i] < minimum) minimum = h[i];
		}
		double shift = MIN_H - minimum;
		for (int i = 0; i < h.length; i++) {
			h[i] += shift;
		}
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		return h[element];
	}
}




