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

import org.cicirello.search.Metaheuristic;
import org.cicirello.search.restarts.Multistarter;
import org.cicirello.search.restarts.RestartSchedule;
import org.cicirello.search.restarts.ConstantRestartSchedule;
import org.cicirello.util.Copyable;
import java.util.Collection;

/**
 * <p>This class is used for implementing parallel multistart metaheuristics.  It can be used to
 * restart any class that implements the {@link Metaheuristic} interface.  
 * A multistart metaheuristic returns the best result from among all of the restarts.
 * In the case of a parallel multistart metaheuristic, the search returns the best result from among
 * all restarts across all threads.</p>
 *
 * <p>There are several 
 * constructors enabling different ways to configure the search.  You can initialize the search
 * with a combination of a {@link Metaheuristic} and number of threads 
 * along with either a {@link RestartSchedule} 
 * for the purpose of specifying run lengths for the restarts, or a run length if all runs 
 * are to be of the same length.
 * You can also initialize the search with a Collection of {@link RestartSchedule} objects, one
 * for each thread (with number of threads implied by size of Collection.
 * Or you can initialize the search with a Collection of {@link Metaheuristic} objects and a 
 * Collection of {@link RestartSchedule} objects (both Collections of the same size).
 * You can also initialize the search with a {@link Multistarter} configured with your restart schedule,
 * along with the number of threads, or a Collection of {@link Multistarter} objects.</p>
 * 
 * <p>When calling the {@link #optimize optimize} method, the runLength parameter corresponds
 * to the number of restarts for each of the Multistarter instances, where those restarts
 * will have run lengths determined by the restart schedule specified upon construction.
 * If the {@link #optimize optimize} method is called multiple times, the restart 
 * schedules of the parallel metaheuristics are not reinitialized,
 * and the run lengths for the additional restarts will continue where the schedules left off.</p>	 
 *
 * @param <T> The type of object being optimized.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.22.2021
 */
public final class ParallelMultistarter<T extends Copyable<T>> extends ParallelMetaheuristic<T> {
	
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
	public ParallelMultistarter(Metaheuristic<T> search, int runLength, int numThreads) {
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
	public ParallelMultistarter(Metaheuristic<T> search, RestartSchedule r, int numThreads) {
		super(new Multistarter<T>(search, r), numThreads);
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
	public ParallelMultistarter(Metaheuristic<T> search, Collection<? extends RestartSchedule> schedules) {
		super(ParallelMultistarterUtil.toMultistarters(search, schedules), false);
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
	public ParallelMultistarter(Collection<? extends Metaheuristic<T>> searches, Collection<? extends RestartSchedule> schedules) {
		super(ParallelMultistarterUtil.toMultistarters(searches, schedules), false);
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
	public ParallelMultistarter(Collection<? extends Metaheuristic<T>> searches, int runLength) {
		this(searches, ConstantRestartSchedule.createRestartSchedules(searches.size(), runLength));
	}
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of
	 * a specified metaheuristic in parallel across multiple threads.  All parallel instances 
	 * follow the same restart schedule of run lengths.
	 * @param multistartSearch A Multistarter configured with the metaheuristic and restart schedule.
	 *         Each of the threads will be an identical copy of this Multistarter.
	 * @param numThreads The number of threads to use.
	 * @throws IllegalArgumentException if numThreads is less than 1.
	 */
	public ParallelMultistarter(Multistarter<T> multistartSearch, int numThreads) {
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
	public ParallelMultistarter(Collection<? extends Multistarter<T>> multistarters) {
		super(multistarters);
	}
	
	/*
	 * private copy constructor to support split() method.
	 */
	private ParallelMultistarter(ParallelMultistarter<T> other) {
		super(other);		
	}
	
	@Override
	public ParallelMultistarter<T> split() {
		return new ParallelMultistarter<T>(this);
	}
}
