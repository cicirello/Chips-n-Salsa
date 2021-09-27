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

/**
 * Implements the common scheduling cost function known as 
 * makespan.  The makespan of a schedule is equal to the
 * completion time of the last job in the schedule.  If the problem
 * doesn't have release dates and setup times, then minimizing
 * makespan is trivial since all possible permutations of the
 * jobs has a makespan simply equal to the sum of the process times.
 * If the problem has sequence-dependent setups, then minimizing
 * makespan is NP-Hard and is roughly equivalent to the wandering
 * salesperson problem.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.15.2020
 */
public final class MinimizeMakespan implements SingleMachineSchedulingProblem {
	
	private final SingleMachineSchedulingProblemData instanceData;
	private int lowerBound;
	
	/**
	 * Constructs a single machine scheduling problem for minimizing 
	 * makespan.
	 *
	 * @param instanceData An encapsulation of the job characteristics,
	 * such as processing times, etc.
	 */
	public MinimizeMakespan(SingleMachineSchedulingProblemData instanceData) {
		this.instanceData = instanceData;
		int n = instanceData.numberOfJobs();
		lowerBound = 0;
		for (int i = 0; i < n; i++) {
			lowerBound += instanceData.getProcessingTime(i);
		}
	}
	
	@Override
	public SingleMachineSchedulingProblemData getInstanceData() {
		return instanceData;
	}
	
	@Override
	public int cost(Permutation candidate) {
		int[] c = instanceData.getCompletionTimes(candidate);
		return c[candidate.get(candidate.length()-1)];
	}
	
	@Override
	public int value(Permutation candidate) {
		return cost(candidate);
	}
	
	@Override
	public int minCost() {
		return lowerBound;
	}
}