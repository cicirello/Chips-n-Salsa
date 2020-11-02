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
 
package org.cicirello.search.concurrent;

import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.restarts.Multistarter;
import org.cicirello.search.restarts.RestartSchedule;
import org.cicirello.util.Copyable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
 *
 * @param <T> The type of object being optimized.
 *
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.15.2020
 */
public final class ParallelMultistarter<T extends Copyable<T>> implements Metaheuristic<T>, AutoCloseable {
	
	private final ArrayList<Multistarter<T>> multistarters;
	private final ExecutorService threadPool;
	
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
		if (numThreads < 1) throw new IllegalArgumentException("must be at least 1 thread");
		if (runLength < 1) throw new IllegalArgumentException("runLength must be at least 1");
		multistarters = new ArrayList<Multistarter<T>>();
		multistarters.add(new Multistarter<T>(search, runLength));
		for (int i = 1; i < numThreads; i++) {
			multistarters.add(new Multistarter<T>(search.split(), runLength));
		}
		threadPool = Executors.newFixedThreadPool(numThreads);
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
		if (numThreads < 1) throw new IllegalArgumentException("must be at least 1 thread");
		multistarters = new ArrayList<Multistarter<T>>();
		multistarters.add(new Multistarter<T>(search, r));
		for (int i = 1; i < numThreads; i++) {
			multistarters.add(new Multistarter<T>(search.split(), r.split()));
		}
		threadPool = Executors.newFixedThreadPool(numThreads);
	}
	
	/**
	 * Constructs a parallel multistart metaheuristic that executes multiple runs of
	 * a specified metaheuristic in parallel across multiple threads.  Each parallel instance 
	 * follows its own restart schedule of run lengths.
	 * @param search The metaheuristic to restart multiple times in parallel.
	 * @param schedules The schedules of run lengths, one for each thread.  The number of threads will 
	 * be equal to the number of restart schedules.
	 */
	public ParallelMultistarter(Metaheuristic<T> search, Collection<? extends RestartSchedule> schedules) {
		multistarters = new ArrayList<Multistarter<T>>();
		boolean addedFirst = false;
		for (RestartSchedule r : schedules) {
			if (addedFirst) multistarters.add(new Multistarter<T>(search.split(), r));
			else {
				multistarters.add(new Multistarter<T>(search, r));
				addedFirst = true;
			}
		}
		threadPool = Executors.newFixedThreadPool(multistarters.size());
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
		if (searches.size() != schedules.size()) {
			throw new IllegalArgumentException("number of searches and number of schedules must be the same");
		}
		multistarters = new ArrayList<Multistarter<T>>();
		Iterator<? extends RestartSchedule> rs = schedules.iterator();
		ProgressTracker<T> t = null; 
		Problem<T> problem = null;
		for (Metaheuristic<T> s : searches) {
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
			multistarters.add(new Multistarter<T>(s, rs.next()));
		}
		threadPool = Executors.newFixedThreadPool(multistarters.size());
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
		if (runLength < 1) throw new IllegalArgumentException("runLength must be at least 1");
		multistarters = new ArrayList<Multistarter<T>>();
		ProgressTracker<T> t = null;
		Problem<T> problem = null;
		for (Metaheuristic<T> s : searches) {
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
			multistarters.add(new Multistarter<T>(s, runLength));
		}
		threadPool = Executors.newFixedThreadPool(multistarters.size());
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
		if (numThreads < 1) throw new IllegalArgumentException("must be at least 1 thread");
		multistarters = new ArrayList<Multistarter<T>>();
		multistarters.add(multistartSearch);
		for (int i = 1; i < numThreads; i++) {
			multistarters.add(multistartSearch.split());
		}
		threadPool = Executors.newFixedThreadPool(numThreads);
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
		this.multistarters = new ArrayList<Multistarter<T>>();
		ProgressTracker<T> t = null;
		Problem<T> problem = null;
		for (Multistarter<T> m : multistarters) {
			if (problem == null) {
				problem = m.getProblem();
			} else if(m.getProblem() != problem) {
				throw new IllegalArgumentException("All Multistarters in searches must solve the same problem.");
			}
			if (t==null) {
				t = m.getProgressTracker();
			} else if (m.getProgressTracker() != t) {
				throw new IllegalArgumentException("All Multistarters must share a single ProgressTracker.");
			}
			this.multistarters.add(m);
		}
		threadPool = Executors.newFixedThreadPool(multistarters.size());
	}
	
	
	/**
	 * <p>Executes a parallel multistart search.  The number of threads, the specific metaheuristic
	 * executed by each thread, the restart schedules, etc are determined by how
	 * the ParallelMultistarter was configured at the time of construction.  
	 * The optimize method runs the optimize method of each of the parallel 
	 * instances of the search the specified
	 * number of times, keeping track of the best solution 
	 * across the multiple parallel runs of the search.
	 * Each restart of each parallel search begins at a new randomly generate initial state.</p>
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
	 * @throws IllegalStateException if the {@link #close} method was previously called.
	 */
	@Override
	public SolutionCostPair<T> optimize(int numRestarts) {
		
		if (threadPool.isShutdown()) {
			throw new IllegalStateException("This ParallelMultistarter was previously closed.");
		}
		
		class MultistartCallable implements Callable<SolutionCostPair<T>> {
		
			Multistarter<T> multistartSearch;
			
			MultistartCallable(Multistarter<T> multistartSearch) {
				this.multistartSearch = multistartSearch;
			}
			
			@Override
			public SolutionCostPair<T> call() {
				return multistartSearch.optimize(numRestarts);
			}
		}
		
		SolutionCostPair<T> bestRestart = null;
		ProgressTracker<T> tracker = multistarters.get(0).getProgressTracker();
		if (!tracker.isStopped() && !tracker.didFindBest()) {
			ArrayList<Future<SolutionCostPair<T>>> futures = new ArrayList<Future<SolutionCostPair<T>>>(); 
			for (Multistarter<T> m : multistarters) {
				futures.add(threadPool.submit(new MultistartCallable(m)));
			}
			for (Future<SolutionCostPair<T>> f : futures) {
				try {
					SolutionCostPair<T> pair = f.get();
					if (bestRestart == null || pair != null && pair.compareTo(bestRestart) < 0) {
						bestRestart = pair;
					}
				} 
				catch (InterruptedException ex) { 
					// Future.get() throws this if the current
					// thread is interrupted.
					//  1) Cancel this task.
					//  2) Preserve interrupt status to cancel remaining.
					f.cancel(true);
					Thread.currentThread().interrupt();
				}
				catch (ExecutionException ex) { 
					// Future.get() throws this if the thread the pool is executing
					// throws any exception. We'll ignore this, skipping
					// the problematic thread and collecting results of other
					// threads.
				}
			}
		}
		return bestRestart; 
	}
	
	/**
	 * <p>Initiates an orderly shutdown of the thread pool used by this ParallelMultistarter.
	 * The ParallelMultistarter utilizes a fixed thread pool so that multiple calls to
	 * the {@link #optimize} method can reuse threads to minimize the expensive task of
	 * thread creation.  When you no longer need the ParallelMultistarter, you should call
	 * the close method to ensure that unneeded threads do not persist.
	 * Once close is called, all subsequent calls to {@link #optimize} will throw an exception.</p>
	 * <p>This method is invoked automatically on objects managed by the try-with-resources statement.</p>
	 */
	@Override
	public void close() {
		threadPool.shutdown();
	}
	
	@Override
	public ParallelMultistarter<T> split() {
		ArrayList<Multistarter<T>> splits = new ArrayList<Multistarter<T>>();
		for (Multistarter<T> m : multistarters) {
			splits.add(m.split());
		}
		ParallelMultistarter<T> pm = new ParallelMultistarter<T>(splits);
		if (threadPool.isShutdown()) pm.close();
		return pm;
	}
	
	@Override
	public ProgressTracker<T> getProgressTracker() {
		return multistarters.get(0).getProgressTracker();
	}
	
	@Override
	public void setProgressTracker(ProgressTracker<T> tracker) {
		if (tracker != null) {
			for (Multistarter<T> m : multistarters) {
				m.setProgressTracker(tracker);
			}
		}
	}
	
	@Override
	public final Problem<T> getProblem() {
		return multistarters.get(0).getProblem();
	}
	
	/**
	 * <p>Gets the total run length of all restarts of all parallel instances of 
	 * the underlying metaheuristics combined.
	 * This may differ from what may be expected based on run lengths passed to 
	 * the optimize and reoptimize methods of the underlying metaheuristics.  
	 * For example, the optimize method terminates 
	 * if it finds the theoretical best solution, and also immediately returns if
	 * a prior call found the theoretical best.  In such cases, the total run length may
	 * be less than the requested run length.</p>
	 *
	 * <p>The meaning of run length may vary based on what metaheuristic is being restarted.</p>
	 * @return the total run length of all restarts of the underlying metaheuristic, which includes
	 * across multiple calls to the restart mechanism and across all parallel instances.
	 */
	@Override
	public long getTotalRunLength() {
		long total = 0;
		for (Multistarter<T> m : multistarters) {
			total = total + m.getTotalRunLength();
		}
		return total;
	}
	
}
