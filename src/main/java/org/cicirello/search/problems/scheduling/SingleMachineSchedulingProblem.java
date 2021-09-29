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

import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.permutations.Permutation;

/**
 * Implement this interface to define a single machine scheduling problem.
 * A class that implements this interface should implement the cost function
 * for the problem (i.e., the function we are optimizing) and must utilize
 * a class implementing the {@link SingleMachineSchedulingProblemData} interface
 * to represent the data describing the instance, such as processing times, due dates,
 * etc.  You may have a single class that implements both interfaces, if desired,
 * in which case, the {@link #getInstanceData} could just return the this reference.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 7.15.2020
 */
public interface SingleMachineSchedulingProblem extends IntegerCostOptimizationProblem<Permutation> {
	
	/**
	 * Gets an object that encapsulates the data describing the
	 * scheduling problem instance, such as number of jobs, and the
	 * characteristics of the jobs, such as processing times, setup
	 * times, due dates, weights, etc.
	 * @return an encapsulation of the data describing the scheduling problem instance
	 */
	SingleMachineSchedulingProblemData getInstanceData();
}