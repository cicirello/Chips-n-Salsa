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
import org.cicirello.search.ss.PartialPermutation;
import org.cicirello.search.ss.IncrementalEvaluation;

/**
 * This is an implementation of the earliest due date heuristic.
 * If used deterministically, this heuristic always chooses the
 * job that has the earliest due date.  It is usually defined as:
 * h(j) = 1 / d[j], where d[j] is the due date of job j.
 * However, this class defines it as: h(j) = 1 / (1 + d[j]) in order
 * to safely handle the case of a job due at the start of the schedule
 * (i.e., d[j] = 0).  This mild variation doesn't affect a schedule
 * constructed deterministically with the heuristic, as the order of 
 * the jobs remains the same. This implementation bounds the minimum
 * value of h at {@link #MIN_H} in support of stochastic sampling search
 * algorithms, which assume positive heuristic values (i.e., h=0 would
 * cause such algorithms technical problems).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.24.2020
 */
public final class EarliestDueDate extends SchedulingHeuristic {
	
	private final double[] h;
	
	/**
	 * Constructs an EarliestDueDate heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @throws IllegalArgumentException if problem.hasDueDates() returns false.
	 */
	public EarliestDueDate(SingleMachineSchedulingProblem problem) {
		super(problem);
		if (!data.hasDueDates()) {
			throw new IllegalArgumentException("This heuristic requires due dates.");
		}
		// This heuristic is static (i.e., doesn't depend on job sequence) so 
		// pre-compute and cache results.
		h = new double[data.numberOfJobs()];
		for (int i = 0; i < h.length; i++) {
			h[i] = 1.0 / (1.0 + data.getDueDate(i));
			if (h[i] < MIN_H) h[i] = MIN_H;
		}
	}
	
	@Override
	public double h(PartialPermutation p, int element, IncrementalEvaluation incEval) {
		return h[element];
	}
}
