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
 * This is an implementation of the shortest process time
 * heuristic, adjusted to include setup time.  
 * This heuristic is defined as: h(j) = 1 / (p[j] + s[i][j]),
 * where p[j] is job j's processing time, and s[i][j] is job j's
 * setup time if it follows job i on the machine.
 * Additionally, the heuristic returns max( {@link #MIN_H}, h(j)),
 * where {@link #MIN_H} is a small non-zero value.  This adjustment is to handle
 * unusually long processing and setup times.  For deterministic construction of a 
 * schedule, this adjustment is unnecessary.  However, for stochastic sampling
 * algorithms it is important for the heuristic to return non-zero values.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 9.4.2020
 */
public class ShortestProcessingPlusSetupTime extends SchedulingHeuristic {
	
	/**
	 * Constructs an ShortestProcessingPlusSetupTime heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 */
	public ShortestProcessingPlusSetupTime(SingleMachineSchedulingProblem problem) {
		super(problem);
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		double denominator = data.getProcessingTime(element);
		if (HAS_SETUPS) {
			denominator += (p.size()==0 ? data.getSetupTime(element) 
							: data.getSetupTime(p.getLast(), element));
		}
		double value = 1.0 / denominator;
		return value <= MIN_H ? MIN_H : value;
	}
}