/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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
 * <p>The Parallel Variable Annealing Length (P-VAL) restart schedule originated,
 * as you would expect from the word "annealing" in its name, as a 
 * restart schedule for Simulated Annealing.  Specifically, it is a parallel
 * version of the Variable Annealing Length (VAL) restart schedule.  Its intended use
 * is to schedule the run lengths for restarts across a set of parallel multistart
 * metaheuristics.  See the {@link VariableAnnealingLength} class for the sequential
 * version of this restart schedule.</p>
 *
 * <p>The motivation underlying P-VAL is two-fold.
 * First, a commonly encountered observation is that a single long run
 * of simulated annealing usually outperforms multiple short runs
 * whose combined length is that of the long run (assuming the annealing schedule
 * is tuned well).  Second, it is often the case that we don't know beforehand 
 * how long of a run we have time to execute, thus our annealing schedule may not
 * be tuned properly for our available time (e.g., we may cool too quickly or
 * too slowly).</p>
 *
 * <p>The sequential version of the restart schedule, known as VAL, 
 * starts with a short run, and increases run length
 * exponentially across restarts.  Specifically, define r<sub>i</sub> as the run length
 * for run i, with the following: r<sub>i</sub> = 1000 * 2<sup>i</sup>.  For simulated
 * annealing, run length is number of evaluations (i.e., iterations of the 
 * simulated annealing main loop).  You can compute the sequence of run lengths
 * incrementally with r<sub>0</sub> = 1000 and r<sub>i</sub> = 2r<sub>i-1</sub>.  
 * The first few run lengths in the sequence are: 1000, 2000, 4000, ....</p>
 *
 * <p>P-VAL is a parallel version of VAL.  Its essence is the same sequence of restarts,
 * but those restart run lengths are spread across a maximum of 4 parallel instances of the
 * search.  If there are more than 4 parallel instances, the schedule repeats with more than one
 * search following the same schedule.
 * The run length, r<sub>i,t</sub>, of restart i of thread t is as follows:
 * r<sub>i,t</sub> = 1000 * 2<sup>(t mod 4) + i*min(4,N)</sup>, where i begins at 0, N
 * is the number of parallel search instances (i.e., threads), and the thread id t is
 * an integer in [0, N).  Note that we do not implement this with an actual exponentiation.
 * Rather, each run length of the sequence is computed incrementally from the prior run length.
 * Here are a few examples:</p>
 *
 * <p>Example 1 (N=3): Thread 0 follows the schedule of run lengths: 1000, 8000, 64000, ....
 * Thread 1 follows the schedule of run lengths: 2000, 16000, 128000, ....  
 * Thread 2 follows the schedule of run lengths: 4000, 32000, 256000, ....</p>
 *
 * <p>Example 2 (N=4): Thread 0 follows the schedule of run lengths: 1000, 16000, 256000, ....
 * Thread 1 follows the schedule of run lengths: 2000, 32000, 512000, ....  
 * Thread 2 follows the schedule of run lengths: 4000, 64000, 1024000, ....
 * Thread 3 follows the schedule of run lengths: 8000, 128000, 2048000, ....</p>
 *
 * <p>Example 3 (N&gt;4): Thread 0 follows the schedule of run lengths: 1000, 16000, 256000, ....
 * Thread 1 follows the schedule of run lengths: 2000, 32000, 512000, ....  
 * Thread 2 follows the schedule of run lengths: 4000, 64000, 1024000, ....
 * Thread 3 follows the schedule of run lengths: 8000, 128000, 2048000, ....
 * Thread t, where t&ge;4, follows the same schedule of run lengths as thread (t-4).</p>
 *
 * <p>See the original publication, referenced below, for the theoretical rationale for
 * beginning the schedule fresh every 4 threads, along with an experimental comparison between
 * P-VAL and a preliminary version referred to in that paper as P-VAL-0, which did not start 
 * anew every 4 threads, as validation of that theoretical result.</p>
 *
 * <p>This class supports both the original schedule as defined above, as well as
 * including a parameter to specify the initial run length r<sub>0,0</sub> as something
 * other than 1000.</p>
 *
 * <p>Although not originally stated in the paper that proposed this restart schedule,
 * this implementation converges to a constant restart length of Integer.MAX_VALUE if the next run length
 * of the schedule would otherwise exceed the maximum positive 32-bit integer value.</p>
 *
 * <p>Since this restart schedule assumes multiple threads, and since each thread requires
 * its own RestartSchedule object that maintains state independent of the others, we do not
 * provide a public constructor for this class.  Instead, we provide a couple static factory
 * methods, {@link #createRestartSchedules(int)} and {@link #createRestartSchedules(int,int)},
 * each of which create an array of restart schedules for the desired number of parallel 
 * instances.</p>
 *
 * <p>The P-VAL restart schedule was introduced in:<br>
 * Vincent A. Cicirello. 
 * <a href=https://www.cicirello.org/publications/cicirello2017SoCS2.html target=_top>"Variable 
 * Annealing Length and Parallelism in Simulated Annealing."</a> 
 * In Proceedings of the Tenth International Symposium on Combinatorial Search (SoCS 2017), 
 * pages 2-10. AAAI Press, June 2017.</p>
 *
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ParallelVariableAnnealingLength implements RestartSchedule {
	
	private final int shift;
	private final int shiftLimit;
	private final int r0;
	private int r;
	
	/*
	 * Constructor is private.  The factory method must be used.
	 * Rationale: This restart schedule only makes sense in a parallel /
	 * multithreaded scenario, and in particular only makes sense with a
	 * combination of restart schedules, one per thread.
	 */	 
	private ParallelVariableAnnealingLength(int shift, int r0) {
		r = this.r0 = r0;
		this.shift = shift;
		shiftLimit = 0x40000000 >> (shift - 1);
	}
	
	/*
	 * Copy constructor is private.  The factory method must be used.
	 * Rationale: This restart schedule only makes sense in a parallel /
	 * multithreaded scenario, and in particular only makes sense with a
	 * combination of restart schedules, one per thread.
	 */	 
	private ParallelVariableAnnealingLength(ParallelVariableAnnealingLength other) {
		r = r0 = other.r0;
		shift = other.shift;
		shiftLimit = other.shiftLimit;
	}
	
	@Override
	public int nextRunLength() {
		int next = r;
		if (r < shiftLimit) r = r << shift;
		else r = 0x7fffffff;
		return next;
	}
	
	@Override
	public void reset() {
		r = r0;
	}
	
	@Override
	public ParallelVariableAnnealingLength split() {
		return new ParallelVariableAnnealingLength(this);
	}
	
	/**
	 * <p>Creates a list of restart schedules that together follow the Parallel Variable Annealing 
	 * Length (P-VAL) schedule of: Vincent A. Cicirello. "Variable Annealing Length and 
	 * Parallelism in Simulated Annealing." In Proceedings of the Tenth International 
	 * Symposium on Combinatorial Search (SoCS 2017), pages 2-10. AAAI Press, June 2017.</p>
	 *
	 * <p>The list that is returned is of the size of the requested number of threads.
	 * This should correspond to the number of parallel instances of the search you
	 * intend to execute.  This factory method ensures that the combination of individual 
	 * restart schedules together conforms to the parallel restart schedule known as P-VAL.</p>
	 *
	 * @param numThreads The number of parallel instances of the search.
	 * @return The P-VAL restart schedule as a list of separate restart schedules, one
	 * for each desired parallel instance.  The combination of restart schedules together implement
	 * P-VAL.
	 * @throws IllegalArgumentException if numThreads &le; 0.
	 */
	public static List<ParallelVariableAnnealingLength> createRestartSchedules(int numThreads) {
		return createRestartSchedules(numThreads, 1000);
	}
	
	/**
	 * <p>Creates a list of restart schedules that together follow the Parallel Variable Annealing 
	 * Length (P-VAL) schedule of: Vincent A. Cicirello. "Variable Annealing Length and 
	 * Parallelism in Simulated Annealing." In Proceedings of the Tenth International 
	 * Symposium on Combinatorial Search (SoCS 2017), pages 2-10. AAAI Press, June 2017.</p>
	 *
	 * <p>The list that is returned is of the size of the requested number of threads.
	 * This should correspond to the number of parallel instances of the search you
	 * intend to execute.  This factory method ensures that the combination of individual 
	 * restart schedules together conforms to the parallel restart schedule known as P-VAL.</p>
	 *
	 * <p>This factory method is a mild modification of the original P-VAL restart schedule.
	 * Specifically, the original schedule sets the shortest run length of any of the parallel
	 * search instances at 1000, while this factory method enables the programmer to specify this.
	 * The original run length of 1000 is appropriate for simulated annealing, however, for
	 * other metaheuristics you may have reason to initialize the schedule with either shorter
	 * or longer runs.</p>
	 *
	 * @param numThreads The number of parallel instances of the search.
	 * @param r0 The shortest run length of any of the parallel instances.
	 * @return The P-VAL restart schedule as a list of separate restart schedules, one
	 * for each desired parallel instance.  The combination of restart schedules together implement
	 * P-VAL.
	 * @throws IllegalArgumentException if numThreads &le; 0 or if r0 &le; 0.
	 */
	public static List<ParallelVariableAnnealingLength> createRestartSchedules(int numThreads, int r0) {
		if (numThreads <= 0) throw new IllegalArgumentException("Must have at least 1 thread.");
		if (r0 <= 0) throw new IllegalArgumentException("r0 must be greater than 0");
		ArrayList<ParallelVariableAnnealingLength> schedules = new ArrayList<ParallelVariableAnnealingLength>(numThreads);
		int shift = numThreads < 4 ? numThreads : 4;
		for (int i = 0; i < shift; i++) {
			schedules.add(new ParallelVariableAnnealingLength(shift, r0));
			r0 = r0 << 1;
		}
		for (int i = shift; i < numThreads; i++) {
			schedules.add(new ParallelVariableAnnealingLength(schedules.get(i-4)));
		}
		return schedules;
	}
	
	
}