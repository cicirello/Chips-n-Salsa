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
 
package org.cicirello.search.concurrent;

import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.restarts.ReoptimizableMultistarter;
import org.cicirello.search.restarts.RestartSchedule;
import org.cicirello.search.restarts.ConstantRestartSchedule;
import org.cicirello.util.Copyable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * <p>This class is used for implementing parallel multistart metaheuristics.  It can be used to
 * restart any class that implements the {@link ReoptimizableMetaheuristic} interface.  
 * A multistart metaheuristic returns the best result from among all of the restarts.
 * In the case of a parallel multistart metaheuristic, the search returns the best result from among
 * all restarts across all threads.</p>
 *
 * <p>There are several 
 * constructors enabling different ways to configure the search.  You can initialize the search
 * with a combination of a {@link ReoptimizableMetaheuristic} and number of threads 
 * along with either a {@link RestartSchedule} 
 * for the purpose of specifying run lengths for the restarts, or a run length if all runs 
 * are to be of the same length.
 * You can also initialize the search with a Collection of {@link RestartSchedule} objects, one
 * for each thread (with number of threads implied by size of Collection.
 * Or you can initialize the search with a Collection of {@link ReoptimizableMetaheuristic} objects and a 
 * Collection of {@link RestartSchedule} objects (both Collections of the same size).
 * You can also initialize the search with a {@link ReoptimizableMultistarter} configured with your restart schedule,
 * along with the number of threads, or a Collection of {@link ReoptimizableMultistarter} objects.</p>
 * 
 * @param <T> The type of object being optimized.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 1.25.2021
 */
public final class ParallelReoptimizableMultistarter<T extends Copyable<T>> extends ParallelMultistarter<T> implements ReoptimizableMetaheuristic<T> {
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of
	 * a specified metaheuristic in parallel across multiple threads.  All restarts 
	 * are the same in length.
	 * @param search The metaheuristic to restart multiple times in parallel.
	 * @param runLength The length of every restarted run of the metaheuristic.
	 * @param numThreads The number of threads to use.
	 * @throws IllegalArgumentException if numThreads is less than 1.
	 * @throws IllegalArgumentException if nunLength is less than 1.
	 */
	public ParallelReoptimizableMultistarter(ReoptimizableMetaheuristic<T> search, int runLength, int numThreads) {
		this(search, new ConstantRestartSchedule(runLength), numThreads);
	}
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of
	 * a specified metaheuristic in parallel across multiple threads.  All parallel instances 
	 * follow the same restart schedule of run lengths.  
	 * @param search The metaheuristic to restart multiple times in parallel.
	 * @param r The schedule of run lengths.  Note that the threads do not share a
	 * single RestartSchedule.  Rather, each thread will be initialized with its own copy of r.
	 * @param numThreads The number of threads to use.
	 * @throws IllegalArgumentException if numThreads is less than 1.
	 */
	public ParallelReoptimizableMultistarter(ReoptimizableMetaheuristic<T> search, RestartSchedule r, int numThreads) {
		this(new ReoptimizableMultistarter<T>(search, r), numThreads);
	}
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of
	 * a specified metaheuristic in parallel across multiple threads.  Each parallel instance 
	 * follows its own restart schedule of run lengths.
	 * @param search The metaheuristic to restart multiple times in parallel.
	 * @param schedules The schedules of run lengths, one for each thread.  The number of threads will 
	 * be equal to the number of restart schedules.
	 * @throws IllegalArgumentException if schedules.size() is less than 1.
	 */
	public ParallelReoptimizableMultistarter(ReoptimizableMetaheuristic<T> search, Collection<? extends RestartSchedule> schedules) {
		super(toReoptimizableMultistarters(search, schedules), false);
	}
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of a set of
	 * specified metaheuristics in parallel across multiple threads.  Each parallel search 
	 * follows its own restart schedule of run lengths.
	 * @param searches A collection of the metaheuristics to restart multiple times in parallel.
	 * The number of threads will 
	 * be equal to the size of this collection.
	 * @param schedules The schedules of run lengths, one for each thread.
	 * @throws IllegalArgumentException if searches.size() is not equal to schedules.size().
	 * @throws IllegalArgumentException if the Collection of Metaheuristics don't all share the same
	 * problem (i.e., requires that s1.getProblem() == s2.getProblem() for all s1, s2 in searches).
	 * @throws IllegalArgumentException if the Collection of Metaheuristics don't all share a single
	 * ProgressTracker (i.e., requires that 
	 * s1.getProgressTracker() == s2.getProgressTracker() for all s1, s2 in searches).
	 */
	public ParallelReoptimizableMultistarter(Collection<? extends ReoptimizableMetaheuristic<T>> searches, Collection<? extends RestartSchedule> schedules) {
		super(toReoptimizableMultistarters(searches, schedules), false);
	}
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of a set of
	 * specified metaheuristics in parallel across multiple threads.  All runs of all parallel instances
	 * follows a constant run length.
	 * @param searches A collection of the metaheuristics to restart multiple times in parallel.
	 * The number of threads will 
	 * be equal to the size of this collection.
	 * @param runLength The length of all restarted runs of all parallel metaheuristics.
	 * @throws IllegalArgumentException if runLength &lt; 1.
	 * @throws IllegalArgumentException if the Collection of Metaheuristics don't all share the same
	 * problem (i.e., requires that s1.getProblem() == s2.getProblem() for all s1, s2 in searches).
	 * @throws IllegalArgumentException if the Collection of Metaheuristics don't all share a single
	 * ProgressTracker (i.e., requires that 
	 * s1.getProgressTracker() == s2.getProgressTracker() for all s1, s2 in searches).
	 */
	public ParallelReoptimizableMultistarter(Collection<? extends ReoptimizableMetaheuristic<T>> searches, int runLength) {
		this(searches, ConstantRestartSchedule.createRestartSchedules(searches.size(), runLength));
	}
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of
	 * a specified metaheuristic in parallel across multiple threads.  All parallel instances 
	 * follow the same restart schedule of run lengths.
	 * @param multistartSearch A ReoptimizableMultistarter configured with the metaheuristic and restart schedule.
	 *         Each of the threads will be an identical copy of this ReoptimizableMultistarter.
	 * @param numThreads The number of threads to use.
	 * @throws IllegalArgumentException if numThreads is less than 1.
	 */
	public ParallelReoptimizableMultistarter(ReoptimizableMultistarter<T> multistartSearch, int numThreads) {
		super(multistartSearch, numThreads);
	}
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of
	 * a set of specified metaheuristics in parallel across multiple threads.  Each of the
	 * Multistarters will run in its own thread.  The number of threads will be equal to the
	 * number of Multistarters passed to the constructor.
	 * @param multistarters A collection of Multistarters configured 
	 *         with the metaheuristics and restart schedules
	 *         for the threads.
	 * @throws IllegalArgumentException if the Collection of Multistarters don't all share the same
	 * problem (i.e., requires that s1.getProblem() == s2.getProblem() for all s1, s2 in multistarters).
	 * @throws IllegalArgumentException if the Collection of Multistarters don't all share a single
	 * ProgressTracker (i.e., requires that 
	 * s1.getProgressTracker() == s2.getProgressTracker() for all s1, s2 in multistarters).
	 */
	public ParallelReoptimizableMultistarter(Collection<ReoptimizableMultistarter<T>> multistarters) {
		super(multistarters, true);
	}
	
	/*
	 * private copy constructor to support split() method.
	 */
	private ParallelReoptimizableMultistarter(ParallelReoptimizableMultistarter<T> other) {
		super(other);
	}
	
	/**
	 * <p>Executes a parallel multistart search.  The number of threads, the specific metaheuristic
	 * executed by each thread, the restart schedules, etc are determined by how
	 * the ParallelReoptimizableMultistarter was configured at the time of construction.  
	 * The optimize method runs the optimize method of each of the parallel 
	 * instances of the search the specified
	 * number of times, keeping track of the best solution 
	 * across the multiple parallel runs of the search.
	 * Each restart of each parallel search begins at the best solution found so far, 
	 * but reinitializes any search
	 * control parameters.</p>
	 *
	 * <p>If this method is called multiple times, the restart schedules of the parallel
	 * metaheuristics are not reinitialized,
	 * and the run lengths for the additional restarts will continue where the schedules left off.</p>
	 *
	 * @param numRestarts The number of times to restart each of the parallel metaheuristics.
	 *
	 * @return The best end of run solution (and its cost) of this set of parallel restarts, 
	 * which may or may not be the same as the solution contained
	 * in this metaheuristic's {@link ProgressTracker}, which contains the best of all runs.
	 * Returns null if the run did not execute, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 *
	 * @throws IllegalStateException if the {@link ParallelMultistarter#close} method was previously called.
	 */
	@Override
	public SolutionCostPair<T> reoptimize(int numRestarts) {
		return threadedOptimize((multistartSearch) -> (
			() -> ((ReoptimizableMultistarter<T>)multistartSearch).reoptimize(numRestarts)
		));
	}
	
	@Override
	public ParallelReoptimizableMultistarter<T> split() {
		return new ParallelReoptimizableMultistarter<T>(this);
	}
	
	private static <U extends Copyable<U>> Collection<ReoptimizableMultistarter<U>> toReoptimizableMultistarters(ReoptimizableMetaheuristic<U> search, Collection<? extends RestartSchedule> schedules) {
		if (schedules.size() < 1) throw new IllegalArgumentException("Must pass at least one schedule.");
		ArrayList<ReoptimizableMultistarter<U>> restarters = new ArrayList<ReoptimizableMultistarter<U>>(schedules.size());
		boolean addedFirst = false;
		for (RestartSchedule r : schedules) {
			if (addedFirst) restarters.add(new ReoptimizableMultistarter<U>(search.split(), r));
			else {
				restarters.add(new ReoptimizableMultistarter<U>(search, r));
				addedFirst = true;
			}
		}
		return restarters;
	}
	
	private static <U extends Copyable<U>> Collection<ReoptimizableMultistarter<U>> toReoptimizableMultistarters(Collection<? extends ReoptimizableMetaheuristic<U>> searches, Collection<? extends RestartSchedule> schedules) {
		if (searches.size() != schedules.size()) {
			throw new IllegalArgumentException("number of searches and number of schedules must be the same");
		}
		ArrayList<ReoptimizableMultistarter<U>> restarters = new ArrayList<ReoptimizableMultistarter<U>>(searches.size());
		Iterator<? extends RestartSchedule> rs = schedules.iterator();
		ProgressTracker<U> t = null; 
		Problem<U> problem = null;
		for (ReoptimizableMetaheuristic<U> s : searches) {
			if (problem == null) {
				problem = s.getProblem();
			} else if(s.getProblem() != problem) {
				throw new IllegalArgumentException("All Metaheuristics in searches must solve the same problem.");
			}
			if (t==null) {
				t = s.getProgressTracker();
			} else if (s.getProgressTracker() != t) {
				throw new IllegalArgumentException("All Metaheuristics in searches must share a single ProgressTracker.");
			}
			restarters.add(new ReoptimizableMultistarter<U>(s, rs.next()));
		}
		return restarters;
	}
}