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
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * <p>This class enables running multiple copies of a metaheuristic, or multiple metaheuristics, 
 * in parallel with multiple threads. It specifically requires that all metaheuristics are
 * solving the same problem, but otherwise, they may be the same or different metaheuristics.</p>
 *
 * @param <T> The type of object being optimized.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.11.2021
 */
public class ParallelMetaheuristic<T extends Copyable<T>> implements Metaheuristic<T>, AutoCloseable {
	
	private final ArrayList<Metaheuristic<T>> metaheuristics;
	private final ExecutorService threadPool;
	
	/**
	 * Constructs a parallel metaheuristic that executes multiple identical copies of a
	 * metaheuristic in parallel across multiple threads.    
	 * @param search The metaheuristic to run in parallel.
	 * @param numThreads The number of threads to use.
	 * @throws IllegalArgumentException if numThreads is less than 1.
	 */
	public ParallelMetaheuristic(Metaheuristic<T> search, int numThreads) {
		if (numThreads < 1) throw new IllegalArgumentException("must be at least 1 thread");
		metaheuristics = new ArrayList<Metaheuristic<T>>(numThreads);
		metaheuristics.add(search);
		for (int i = 1; i < numThreads; i++) {
			metaheuristics.add(search.split());
		}
		threadPool = Executors.newFixedThreadPool(numThreads);
	}
	
	/**
	 * Constructs a parallel metaheuristic that executes multiple 
	 * metaheuristics in parallel across multiple threads.  
	 * @param searches A collection of the metaheuristics to run in parallel.
	 * The number of threads will 
	 * be equal to the size of this collection.
	 * @throws IllegalArgumentException if the Collection of Metaheuristics don't all share the same
	 * problem (i.e., requires that s1.getProblem() == s2.getProblem() for all s1, s2 in searches).
	 * @throws IllegalArgumentException if the Collection of Metaheuristics don't all share a single
	 * ProgressTracker (i.e., requires that 
	 * s1.getProgressTracker() == s2.getProgressTracker() for all s1, s2 in searches).
	 */
	public ParallelMetaheuristic(Collection<? extends Metaheuristic<T>> searches) {
		this(searches, true);
	}
	
	/*
	 * package private for use by subclasses in same package.
	 */
	ParallelMetaheuristic(Collection<? extends Metaheuristic<T>> searches, boolean verifyState) {
		if (verifyState) {
			ProgressTracker<T> t = null;
			Problem<T> problem = null;
			for (Metaheuristic<T> m : searches) {
				if (problem == null) {
					problem = m.getProblem();
				} else if(m.getProblem() != problem) {
					throw new IllegalArgumentException("All metaheuristics in searches must solve the same problem.");
				}
				if (t==null) {
					t = m.getProgressTracker();
				} else if (m.getProgressTracker() != t) {
					throw new IllegalArgumentException("All metaheuristics must share a single ProgressTracker.");
				}
			}
		}
		this.metaheuristics = new ArrayList<Metaheuristic<T>>(searches.size());
		for (Metaheuristic<T> m : searches) {
			this.metaheuristics.add(m);
		}
		threadPool = Executors.newFixedThreadPool(metaheuristics.size());
	}
	
	/*
	 * package-private copy constructor to support split() method.
	 */
	ParallelMetaheuristic(ParallelMetaheuristic<T> other) {
		// Must generate a list of metaheuristics, each one a split of each from the other.
		metaheuristics = new ArrayList<Metaheuristic<T>>(other.metaheuristics.size());
		for (Metaheuristic<T> m : other.metaheuristics) {
			this.metaheuristics.add(m.split());
		}
		
		// Needs its own thread pool
		threadPool = Executors.newFixedThreadPool(metaheuristics.size());
		if (other.isClosed()) close();		
	}
	
	/**
	 * <p>Executes a parallel metaheuristic search.  
	 * The number of threads, the specific metaheuristic
	 * executed by each thread, etc are determined by how
	 * the ParallelMetaheuristic was configured at the time of construction.  
	 * The optimize method runs the optimize method of each of the parallel 
	 * instances of the search for the specified
	 * run length (same run length for all parallel instances), 
	 * keeping track of the best solution 
	 * across the multiple parallel runs of the search. The meaning of
	 * run length may vary based on the component metaheuristics.
	 * Each run of each parallel search begins at a new randomly generate initial state.</p>
	 *
	 * @param runLength The run length for all parallel metaheuristics.
	 *
	 * @return The best end of run solution (and its cost) of this set of parallel runs, 
	 * which may or may not be the same as the solution contained
	 * in this metaheuristic's {@link ProgressTracker}, which contains the best of all runs
	 * across all calls to optimize.
	 * Returns null if the run did not execute, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 *
	 * @throws IllegalStateException if the {@link #close} method was previously called.
	 */
	@Override
	public final SolutionCostPair<T> optimize(int runLength) {
		return threadedOptimize(search -> (
			() -> search.optimize(runLength)
		));
	}
	
	/**
	 * <p>Initiates an orderly shutdown of the thread pool used by this ParallelMetaheuristic.
	 * The ParallelMetaheuristic utilizes a fixed thread pool so that multiple calls to
	 * the {@link #optimize} method can reuse threads to minimize the expensive task of
	 * thread creation.  When you no longer need the ParallelMetaheuristic, you should call
	 * the close method to ensure that unneeded threads do not persist.
	 * Once close is called, all subsequent calls to {@link #optimize} will throw an exception.</p>
	 * <p>This method is invoked automatically on objects managed by the try-with-resources statement.</p>
	 */
	@Override
	public final void close() {
		threadPool.shutdown();
	}
	
	/**
	 * Checks whether the thread pool has been shutdown.
	 * @return true if and only if the {@link #close} method has been called previously.
	 */
	public final boolean isClosed() {
		return threadPool.isShutdown();
	}
	
	@Override
	public ParallelMetaheuristic<T> split() {
		return new ParallelMetaheuristic<T>(this);
	}
	
	@Override
	public final ProgressTracker<T> getProgressTracker() {
		return metaheuristics.get(0).getProgressTracker();
	}
	
	@Override
	public final void setProgressTracker(ProgressTracker<T> tracker) {
		if (tracker != null) {
			for (Metaheuristic<T> m : metaheuristics) {
				m.setProgressTracker(tracker);
			}
		}
	}
	
	@Override
	public final Problem<T> getProblem() {
		return metaheuristics.get(0).getProblem();
	}
	
	/**
	 * <p>Gets the total run length of all runs of all parallel instances of 
	 * the underlying metaheuristics combined.
	 * This may differ from what may be expected based on run lengths passed to 
	 * the optimize and reoptimize methods of the underlying metaheuristics.  
	 * For example, the optimize method terminates 
	 * if it finds the theoretical best solution, and also immediately returns if
	 * a prior call found the theoretical best.  In such cases, the total run length may
	 * be less than the requested run length.</p>
	 *
	 * <p>The meaning of run length may vary based on the underlying metaheuristic.</p>
	 * @return the total run length of all runs of the underlying metaheuristics, which includes
	 * across multiple calls and across all parallel instances.
	 */
	@Override
	public final long getTotalRunLength() {
		long total = 0;
		for (Metaheuristic<T> m : metaheuristics) {
			total = total + m.getTotalRunLength();
		}
		return total;
	}
	
	/*
	 * package-private for use by subclasses in package only.
	 *
	 * optimize of this class, and reoptimize of subclasses delegate work to this method.
	 */
	final SolutionCostPair<T> threadedOptimize(Function<Metaheuristic<T>, Callable<SolutionCostPair<T>>> icf) {
		if (threadPool.isShutdown()) {
			throw new IllegalStateException("This ParallelMetaheuristic was previously closed.");
		}
		
		SolutionCostPair<T> bestParallelRun = null;
		ProgressTracker<T> tracker = metaheuristics.get(0).getProgressTracker();
		if (!tracker.isStopped() && !tracker.didFindBest()) {
			ArrayList<Future<SolutionCostPair<T>>> futures = new ArrayList<Future<SolutionCostPair<T>>>(); 
			for (Metaheuristic<T> m : metaheuristics) {
				futures.add(threadPool.submit(icf.apply(m)));
			}
			for (Future<SolutionCostPair<T>> f : futures) {
				try {
					SolutionCostPair<T> pair = f.get();
					if (bestParallelRun == null || pair != null && pair.compareTo(bestParallelRun) < 0) {
						bestParallelRun = pair;
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
		return bestParallelRun;
	}

}
