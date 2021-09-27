/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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
 * <p>This class implements a variation the weighted shortest process time
 * heuristic, but adjusted to incorporate setups times for problems with
 * sequence-dependent setups.  
 * The original version of the heuristic can be found in the
 * {@link WeightedShortestProcessingTime} class, 
 * and is defined as: h(j) = w[j] / p[j],
 * where w[j] is the weight of job j, and p[j] is its processing time.</p>
 *
 * <p>We modify this to incorporate setup times by instead defining
 * the heuristic as: h(j) = w[j] / (p[j] + s[i][j]), where
 * s[i][j] is the setup time required by job j if it immediately follows
 * job i on the machine, where job i is the preceding job.</p>
 *
 * <p>Furthermore, this implementation returns: max( {@link #MIN_H}, h(j)), 
 * where {@link #MIN_H}
 * is a small non-zero value.  This is to deal with the possibility of
 * a job with weight w[j] = 0, or especially high processing and setup times
 * relative to weight.  For deterministic construction of a 
 * schedule, this adjustment is unnecessary.  However, for stochastic sampling
 * algorithms it is important for the heuristic to return non-zero values.</p>
 *
 * <p>The heuristic values are computed each time the
 * {@link #h} method is called. Therefore, for many
 * iterations of stochastic sampling, the same 
 * heuristic values may be computed repeatedly. If your
 * problem instance is small enough to be able to afford
 * the extra memory, you might consider instead using
 * the {@link WeightedShortestProcessingPlusSetupTimePrecompute} class, which
 * implements the same heuristic, but it precomputes
 * a table of heuristic values upon constructing the
 * heuristic object.</p>
 
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 2.22.2021
 */
public class WeightedShortestProcessingPlusSetupTime extends SchedulingHeuristic {
	
	/**
	 * Constructs an WeightedShortestProcessingPlusSetupTime heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 */
	public WeightedShortestProcessingPlusSetupTime(SingleMachineSchedulingProblem problem) {
		super(problem);
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		double value = data.getWeight(element);
		if (value < MIN_H) return MIN_H;
		double denominator = data.getProcessingTime(element);
		if (HAS_SETUPS) {
			denominator += (p.size()==0 ? data.getSetupTime(element) 
							: data.getSetupTime(p.getLast(), element));
		}
		value /= denominator;
		return value <= MIN_H ? MIN_H : value;
	}
}