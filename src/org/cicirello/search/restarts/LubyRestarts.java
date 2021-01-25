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
 
package org.cicirello.search.restarts;

import java.util.List;
import java.util.ArrayList;

/**
 * <p>The Luby restart schedule originated with constraint satisfaction search, and
 * was originally used to control when to restart a backtracking constraint satisfaction 
 * search in number of backtracks.  Its motivation is the often-encountered heavy-tailed
 * runtime distributions of constraint satisfaction search algorithms (i.e., although
 * many runs may take exponentially long to solve the instance, there tends to be a high
 * density of runs that lead directly to a solution).  The Luby schedule's intention is
 * to cut off search early, and that a restart may have a reasonable chance of resulting
 * in one of the fast runs.  The Luby schedule, thus, is focused on short numbers of
 * restarts, but does continue to increase over time to ensure completeness of
 * constraint satisfaction search.</p>  
 *
 * <p>It is not likely a good choice for simulated annealing, and other similar
 * optimization algorithms, as the Luby schedule's objective is rather different
 * than how restarts are used in simulated annealing.  However, we have included it
 * in this library as it is a well-known restart schedule for search more generally.</p>
 *
 * <p>The Luby schedule of restart lengths is as follows: 1, 1, 2, 1, 1, 2, 4,
 * 1, 1, 2, 1, 1, 2, 4, 8, 1, 1, 2, 1, 1, 2, 4, 1, 1, 2, 1, 1, 2, 4, 8, 16, ....
 * The default constructor supports this original Luby schedule.  An additional
 * constructor provides the ability to multiply the Luby schedule by a constant.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 1.25.2021
 */
public final class LubyRestarts implements RestartSchedule {
	
	private final int a;
	private int u;
	private int v;
	
	/**
	 * Constructs a restart schedule that follows the Luby sequence
	 * of run lengths.
	 */
	public LubyRestarts() {
		u = v = a = 1;
	}
	
	/**
	 * Constructs a restart schedule, such that the run lengths are equal to a*L, where
	 * L follows the Luby sequence.
	 * @param a A multiplier, which must be positive.  Each run 
	 * length is equal to a*L where 
	 * L follows the Luby sequence.
	 * @throws IllegalArgumentException if a &lt; 1
	 */
	public LubyRestarts(int a) {
		if (a < 1) throw new IllegalArgumentException("a must be positive");
		this.a = a;
		u = v = 1;
	}

	@Override
	public int nextRunLength() {
		int r = a * v;
		if ((-u & u) == v) {
			u++;
			v = 1;
		} else {
			v = v << 1;
		}
		return r;
	}
	
	@Override
	public void reset() {
		u = v = 1;
	}
	
	@Override
	public LubyRestarts split() {
		return new LubyRestarts(a);
	}
	
	/**
	 * A convenience method for generating several identical and independent LubyRestarts
	 * objects, such as when needed for a parallel search (e.g., if each instance
	 * needs its own restart schedule).
	 *
	 * @param numThreads The number of restart schedules to generate.
	 * @return a list of Luby restart schedules
	 * @throws IllegalArgumentException if numThreads &le; 0
	 */
	public static List<LubyRestarts> createRestartSchedules(int numThreads) {
		return createRestartSchedules(numThreads, 1);
	}
	
	/**
	 * A convenience method for generating several identical and independent LubyRestarts
	 * objects, such as when needed for a parallel search (e.g., if each instance
	 * needs its own restart schedule).
	 *
	 * @param numThreads The number of restart schedules to generate.
	 * @param a A multiplier, which must be positive.  Each run 
	 * length is equal to a*L where 
	 * L follows the Luby sequence.
	 * @return a list of Luby restart schedules
	 * @throws IllegalArgumentException if numThreads &le; 0 or if a &lt; 1
	 */
	public static List<LubyRestarts> createRestartSchedules(int numThreads, int a) {
		if (numThreads <= 0) throw new IllegalArgumentException("Must have at least 1 thread.");
		if (a <= 0) throw new IllegalArgumentException("a must be greater than 0");
		ArrayList<LubyRestarts> schedules = new ArrayList<LubyRestarts>(numThreads);
		for (int i = 0; i < numThreads; i++) {
			schedules.add(new LubyRestarts(a));
		}
		return schedules;
	}
}