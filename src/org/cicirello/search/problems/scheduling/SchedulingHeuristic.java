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
import org.cicirello.search.ss.ConstructiveHeuristic;
import org.cicirello.search.problems.Problem;

/**
 * This class serves as an abstract base class for the
 * scheduling heuristics of the library, handling common
 * functionality such as maintaining the scheduling problem
 * instance.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.24.2020
 */
public abstract class SchedulingHeuristic implements ConstructiveHeuristic {
	
	/** 
	 * The instance of the scheduling problem that is the target
	 * of the heuristic. 
	 */
	protected final SingleMachineSchedulingProblem problem;
	
	/** 
	 * The instance data of the scheduling problem that is the target
	 * of the heuristic. 
	 */
	protected final SingleMachineSchedulingProblemData data;
	
	/**
	 * Initializes the abstract base class for scheduling heuristics.
	 * @param problem The instance of a scheduling problem that is the target
	 * of the heuristic.
	 */
	public SchedulingHeuristic(SingleMachineSchedulingProblem problem) {
		this.problem = problem;
		data = problem.getInstanceData();
	}
	
	@Override
	public Problem<Permutation> getProblem() {
		return problem;
	}
	
	@Override
	public int completePermutationLength() {
		return data.numberOfJobs();
	}
}