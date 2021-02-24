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
 * <p>This is an implementation of the shortest process time
 * heuristic, adjusted to include setup time.  
 * This heuristic is defined as: h(j) = 1 / (p[j] + s[i][j]),
 * where p[j] is job j's processing time, and s[i][j] is job j's
 * setup time if it follows job i on the machine.
 * Additionally, the heuristic returns max( {@link #MIN_H}, h(j)),
 * where {@link #MIN_H} is a small non-zero value.  This adjustment is to handle
 * unusually long processing and setup times.  For deterministic construction of a 
 * schedule, this adjustment is unnecessary.  However, for stochastic sampling
 * algorithms it is important for the heuristic to return non-zero values.</p>
 *
 * <p>In this version, the heuristic is precomputed 
 * for all pairs of jobs (e.g., for evaluating job
 * j for each possible preceding job). This may 
 * speed up stochastic sampling search when many iterations
 * are executed (won't need to recompute the same heuristic
 * values repeatedly). However, for large problems, the O(n<sup>2</sup>)
 * space, where n is the number of jobs may be prohibitive.
 * For a version that doesn't precompute the heuristic, see
 * the {@link ShortestProcessingPlusSetupTime} class, 
 * which requires only O(1) space.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 2.22.2021
 */
public final class ShortestProcessingPlusSetupTimePrecompute extends SchedulingHeuristic {
	
	private final double[][] h; 
	
	/**
	 * Constructs an ShortestProcessingPlusSetupTimePrecompute heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 */
	public ShortestProcessingPlusSetupTimePrecompute(SingleMachineSchedulingProblem problem) {
		super(problem);
		final int n = data.numberOfJobs();
		h = new double[n][n];
		if (HAS_SETUPS) {
			for (int i = 0; i < n; i++) {
				h[i][i] = Math.max(MIN_H, 1.0 / (data.getProcessingTime(i) + data.getSetupTime(i)));
				for (int j = i+1; j < n; j++) {
					h[i][j] = Math.max(MIN_H, 1.0 / (data.getProcessingTime(j) + data.getSetupTime(i, j)));
					h[j][i] = Math.max(MIN_H, 1.0 / (data.getProcessingTime(i) + data.getSetupTime(j, i)));
				}
			}
		} else {
			for (int i = 0; i < n; i++) {
				final double H = Math.max(MIN_H, 1.0 / (data.getProcessingTime(i)));
				for (int j = 0; j < n; j++) {
					h[j][i] = H;
				}
			}
		}
	}
	
	@Override
	public double h(Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
		return p.size()==0 ? h[element][element] : h[p.getLast()][element];
	}
}