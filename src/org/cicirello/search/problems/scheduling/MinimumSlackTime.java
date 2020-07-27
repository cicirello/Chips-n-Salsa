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
 * <p>This is an implementation of the minimum slack time (MST)
 * heuristic.  The slack S(j) of job j is defined
 * as S(j) = d[j] - T - p[j] - s[j], where d[j] is the job's due date, T is the
 * current time, and s[j] is any setup time of the job (for problems with
 * setup times).  The MST heuristic chooses to schedule the job
 * with the least slack.  We need to define the heuristic h(j) such that
 * h(j) &gt; 0 in order to support stochastic sampling search algorithms.
 * Below we derive the rule that we use.</p>
 *
 * <p>First, note that since the heuristic is used to compare jobs
 * relative to each other, and since during a specific decision making
 * scenario, the value of T is the same when computing slack for all jobs
 * (it is just the current time), the job that has minimum S(j), also
 * has minimum W(j) = d[j] - p[j] - s[j].  This is equivalent to the
 * job that has maximum value of X(j) = -W(j) = p[j] + s[j] - d[j].
 * X(j) can be negative or zero.  So we will add a constant d[max],
 * to derive our chosen definition of the MST heuristic: 
 * h(j) = p[j] + s[j] - d[j] + d[max].  This will always be positive,
 * since if j is the job with max due date, h(j) simplifies to p[j] + s[j],
 * which must be positive since processing times are always positive.
 * And the job that has maximum h(j) has minimum S(j), using this
 * definition is equivalent to the MST heuristic.  We do not dynamically
 * update d[max] as jobs are scheduled.</p>
 *
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.27.2020
 */
public final class MinimumSlackTime extends SchedulingHeuristic {
	
	/**
	 * Constructs an MinimumSlackTime heuristic.
	 * @param problem The instance of a scheduling problem that is
	 * the target of the heuristic.
	 * @throws IllegalArgumentException if problem.hasDueDates() returns false.
	 */
	public MinimumSlackTime(SingleMachineSchedulingProblem problem) {
		super(problem);
		if (!data.hasDueDates()) {
			throw new IllegalArgumentException("This heuristic requires due dates.");
		}
	}
	
	@Override
	public double h(PartialPermutation p, int element, IncrementalEvaluation incEval) {
		double value = ((WithDMax)incEval).DMAX + data.getProcessingTime(element) - data.getDueDate(element);
		if (HAS_SETUPS) {
			value += p.size()==0 ? data.getSetupTime(element) 
					: data.getSetupTime(p.getLast(), element);
		}
		return value;
	}
	
	@Override
	public IncrementalEvaluation createIncrementalEvaluation() {
		return new WithDMax();
	}
	
	/*
	 * package-private rather than private to enable test case access
	 */
	private class WithDMax implements IncrementalEvaluation {
		
		private final int DMAX;
		
		public WithDMax() {
			int max = data.getDueDate(0);
			final int n = data.numberOfJobs();
			for (int i = 1; i < n; i++) {
				if (data.getDueDate(i) > max) {
					max = data.getDueDate(i);
				}
			}
			DMAX = max;
		}
		
		@Override
		public void extend(PartialPermutation p, int element) { }
	}
}