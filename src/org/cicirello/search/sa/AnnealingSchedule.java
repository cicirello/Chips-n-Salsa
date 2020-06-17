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
 
package org.cicirello.search.sa;

import org.cicirello.search.concurrent.Splittable;

/**
 * This interface specifies the required functionality for implementations of
 * annealing schedules.  Classes that implement this interface are in charge of
 * managing simulated annealing's temperature parameter.  Adaptive annealing schedules
 * may have additional state data to maintain.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 10.4.2019
 */
public interface AnnealingSchedule extends Splittable<AnnealingSchedule> {
	
	/**
	 * Perform any initialization necessary for the annealing schedule 
	 * at to the start of a run
	 * of simulated annealing.  This includes initializing the temperature parameter.  
	 * This method is called once by implementations
	 * of simulated annealing at the start of the run.  Implementations of
	 * simulated annealing that perform reannealing will also call this
	 * once at the start of each reanneal.
	 *
	 * @param maxEvals The maximum length of the run of simulated annealing
	 * about to start.  Some annealing schedules depend upon prior knowledge
	 * of run length.  For those annealing schedules that don't depend upon
	 * run length, this parameter is ignored.
	 */
	void init(int maxEvals);
	
	/**
	 * Determine whether or not to accept a neighboring solution based on its
	 * cost and the current cost, both passed as parameters.  Lower cost indicates
	 * better solution.  This method must also update the temperature and any other
	 * state data related to the annealing schedule.
	 * @param neighborCost The cost of the neighboring solution under consideration.
	 * @param currentCost The cost of the current solution.
	 * @return true if simulated annealing should accept the neighbor, and false otherwise.
	 */
	boolean accept(double neighborCost, double currentCost);
}