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
 
package org.cicirello.search.restarts;

import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * This class is used for implementing multistart metaheuristics.  It can be used to
 * restart any class that implements the {@link Metaheuristic} interface, and requires
 * specification of either a {@link RestartSchedule} for the purpose of specifying run lengths
 * for the restarts, or a run length if all runs are to be of the same length.
 * A multistart metaheuristic returns the best result from among all of the restarts.
 *
 * @param <T> The type of object being optimized.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.15.2020
 */
public class Multistarter<T extends Copyable<T>> implements Metaheuristic<T> {
	
	private final Metaheuristic<T> search;
	
	// deliberately package-private
	final RestartSchedule r;
	
	/**
	 * Constructs a multistart metaheuristic that executes multiple runs of
	 * a specified metaheuristic, whose run lengths follow a specified schedule.
	 * @param search The metaheuristic to restart multiple times.
	 * @param r The schedule of run lengths for the multistart search
	 */
	public Multistarter(Metaheuristic<T> search, RestartSchedule r) {
		this.search = search;
		this.r = r;
	}
	
	/**
	 * Constructs a multistart metaheuristic that executes multiple runs of
	 * a specified metaheuristic, whose runs are all the same in length.
	 * @param search The metaheuristic to restart multiple times.
	 * @param runLength The length of every restarted run of the metaheuristic.
	 * @throws IllegalArgumentException if runLength &lt; 1
	 */
	public Multistarter(Metaheuristic<T> search, int runLength) {
		this(search, new ConstantRestartSchedule(runLength));
	}
	
	@Override
	public final ProgressTracker<T> getProgressTracker() {
		return search.getProgressTracker(); 
	}
	
	@Override
	public final void setProgressTracker(ProgressTracker<T> tracker) {
		if (tracker != null) {
			search.setProgressTracker(tracker);
		}
	}
	
	@Override
	public final Problem<T> getProblem() {
		return search.getProblem();
	}
	
	/**
	 * <p>Gets the total run length of all restarts of the underlying metaheuristic
	 * combined.
	 * This may differ from what may be expected based on run lengths passed to 
	 * the optimize and reoptimize methods of the underlying metaheuristic.  
	 * For example, the optimize method terminates 
	 * if it finds the theoretical best solution, and also immediately returns if
	 * a prior call found the theoretical best.  In such cases, the total run length may
	 * be less than the requested run length.</p>
	 *
	 * <p>The meaning of run length may vary based on what metaheuristic is being restarted.</p>
	 * @return the total run length of all restarts of the underlying metaheuristic, which includes
	 * across multiple calls to the restart mechanism
	 */
	@Override
	public final long getTotalRunLength() {
		return search.getTotalRunLength();
	}
	
	/**
	 * <p>Executes a multistart search, calling the underlying metaheuristic the specified
	 * number of times, keeping track of the best solution across the multiple runs of the search.
	 * Each restart begins at a new randomly generate initial state.</p>
	 *
	 * <p>If this method is called multiple times, the restart schedule is not reinitialized,
	 * and the run lengths for the additional restarts will continue where the schedule left off.</p>
	 *
	 * @param numRestarts The number of times to restart the metaheuristic.
	 *
	 * @return The best end of run solution (and its cost) of this set of restarts, 
	 * which may or may not be the same as the solution contained
	 * in this metaheuristic's {@link ProgressTracker}, which contains the best of all runs.
	 * Returns null if the run did not execute, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 */
	@Override
	public final SolutionCostPair<T> optimize(int numRestarts) {
		ProgressTracker<T> tracker = search.getProgressTracker();
		SolutionCostPair<T> bestRestart = null;
		for (int i = 0; i < numRestarts && !tracker.isStopped() && !tracker.didFindBest(); i++) {
			SolutionCostPair<T> thisRestart = search.optimize(r.nextRunLength());
			if (bestRestart == null || thisRestart != null && thisRestart.compareTo(bestRestart) < 0)
				bestRestart = thisRestart;
		}
		return bestRestart;
	}
	
	@Override
	public Multistarter<T> split() {
		return new Multistarter<T>(search.split(), r.split());
	}
}