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

/**
 * <p>The Variable Annealing Length (VAL) restart schedule originated,
 * as you would expect from the word "annealing" in its name, as a 
 * restart schedule for Simulated Annealing.  Its motivation is two-fold.
 * First, a commonly encountered observation is that a single long run
 * of simulated annealing usually outperforms multiple short runs
 * whose combined length is that of the long run (assuming the annealing schedule
 * is tuned well).  Second, it is often the case that we don't know beforehand 
 * how long of a run we have time to execute, thus our annealing schedule may not
 * be tuned properly for our available time (e.g., we may cool too quickly or
 * too slowly).</p>
 *
 * <p>The VAL restart schedule starts with a short run, and increases run length
 * exponentially across restarts.  Specifically, define r<sub>i</sub> as the run length
 * for run i, with the following: r<sub>i</sub> = 1000 * 2<sup>i</sup>.  For simulated
 * annealing, run length is number of evaluations (i.e., iterations of the 
 * simulated annealing main loop).  You can compute the sequence of run lengths
 * incrementally with r<sub>0</sub> = 1000 and r<sub>i</sub> = 2r<sub>i-1</sub>.  
 * The first few run lengths in the sequence are: 1000, 2000, 4000, ....</p>
 *
 * <p>The VAL restart schedule was introduced in:<br>
 * Vincent A. Cicirello. 
 * <a href=https://www.cicirello.org/publications/cicirello2017SoCS2.html target=_top>"Variable 
 * Annealing Length and Parallelism in Simulated Annealing."</a> 
 * In Proceedings of the Tenth International Symposium on Combinatorial Search (SoCS 2017), 
 * pages 2-10. AAAI Press, June 2017.</p>
 *
 * <p>This class supports both the original schedule as defined above, as well as
 * including a parameter to specify the initial run length r<sub>0</sub> as something
 * other than 1000.  In this case, the subsequent run lengths are still twice the
 * previous.  For example, if you start r<sub>0</sub> = 50, then the run lengths
 * will follow the sequence: 50, 100, 200, 400, ....</p>
 *
 * <p>Although not originally stated in the paper that proposed this restart schedule,
 * this implementation converges to a constant restart length of Integer.MAX_VALUE if the next run length
 * of the schedule would otherwise exceed the maximum positive 32-bit integer value.</p>
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.8.2020
 */
public final class VariableAnnealingLength implements RestartSchedule {
	
	private final int r0;
	private int r;
	
	/**
	 * The default constructor constructs the original Variable Annealing Length (VAL)
	 * restart schedule of: Vincent A. Cicirello. "Variable Annealing Length and 
	 * Parallelism in Simulated Annealing." In Proceedings of the Tenth International 
	 * Symposium on Combinatorial Search (SoCS 2017), pages 2-10. AAAI Press, June 2017.
	 * Specifically, the initial run length is 1000, and each subsequent run length is
	 * twice the previous.
	 */ 
	public VariableAnnealingLength() {
		r = r0 = 1000;
	}
	
	/**
	 * This constructor enables specifying the initial run length for the first run.
	 * Subsequent runs otherwise follow the original schedule and are
	 * twice the length of the previous run.  This restart schedule originated with
	 * simulated annealing, but when used with other metaheuristics it may be desirable to
	 * either increase or decrease the initial run length from what was originally used.
	 * @param r0 The initial run length for the first run.
	 */ 
	public VariableAnnealingLength(int r0) {
		if (r0 < 1) throw new IllegalArgumentException("r0 must be positive");
		r = this.r0 = r0;
	}
	
	@Override
	public int nextRunLength() {
		int next = r;
		if (r < 0x40000000) r = r << 1;
		else r = 0x7fffffff;
		return next;
	}
	
	@Override
	public void reset() {
		r = r0;
	}
	
	@Override
	public VariableAnnealingLength split() {
		return new VariableAnnealingLength(r0);
	}
}