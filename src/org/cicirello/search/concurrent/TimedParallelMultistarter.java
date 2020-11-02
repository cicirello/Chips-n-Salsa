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
 * <p>This parallel multistarter enables specifying the run length in terms of time, rather than by
 * number of restarts.  It then executes as many restarts as that length of time allows.  This may be 
 * more desirable for multiple use cases.  First, if the run lengths can vary from one restart to another,
 * each parallel instance of the search may have rather different run times if we were to specify the
 * number of times to restart.  This would lead to some threads sitting idle.  Second, if we were executing
 * different metaheuristics in different threads, then again one or more threads may complete early sitting
 * idle if we were to specify number of times to restart.  Third, a similar phenomena can result if we
 * were executing the same metaheuristic (e.g., simulated annealing) but where each parallel instance
 * was using a different mutation operator.  Finally, if we know how much time we can afford to search,
 * we don't need a priori know the length of time required by a restart.</p>
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
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.15.2020
 */
public final class TimedParallelMultistarter<T extends Copyable<T>> implements Metaheuristic<T>, AutoCloseable {
	
	/** The default unit of time in milliseconds. This default is 1000 ms (or 1 second).  
	 * @see #setTimeUnit
	 * @see #getTimeUnit
	 */
	public static final int TIME_UNIT_MS = 1000;
	
	private final ArrayList<Multistarter<T>> multistarters;
	private final ExecutorService threadPool;
	private int timeUnit;
	private ArrayList<SolutionCostPair<T>> history;
	
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
	public TimedParallelMultistarter(Metaheuristic<T> search, int runLength, int numThreads) {
		if (numThreads < 1) throw new IllegalArgumentException("must be at least 1 thread");
		if (runLength < 1) throw new IllegalArgumentException("runLength must be at least 1");
		multistarters = new ArrayList<Multistarter<T>>();
		multistarters.add(new Multistarter<T>(search, runLength));
		for (int i = 1; i < numThreads; i++) {
			multistarters.add(new Multistarter<T>(search.split(), runLength));
		}
		threadPool = Executors.newFixedThreadPool(numThreads);
		timeUnit = TIME_UNIT_MS;
		history = null;
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
	public TimedParallelMultistarter(Metaheuristic<T> search, RestartSchedule r, int numThreads) {
		if (numThreads < 1) throw new IllegalArgumentException("must be at least 1 thread");
		multistarters = new ArrayList<Multistarter<T>>();
		multistarters.add(new Multistarter<T>(search, r));
		for (int i = 1; i < numThreads; i++) {
			multistarters.add(new Multistarter<T>(search.split(), r.split()));
		}
		threadPool = Executors.newFixedThreadPool(numThreads);
		timeUnit = TIME_UNIT_MS;
		history = null;
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
	public TimedParallelMultistarter(Metaheuristic<T> search, Collection<? extends RestartSchedule> schedules) {
		if (schedules.size() < 1) throw new IllegalArgumentException("Must pass at least one schedule.");
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
		timeUnit = TIME_UNIT_MS;
		history = null;
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
	public TimedParallelMultistarter(Collection<? extends Metaheuristic<T>> searches, Collection<? extends RestartSchedule> schedules) {
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
		timeUnit = TIME_UNIT_MS;
		history = null;
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
	public TimedParallelMultistarter(Collection<? extends Metaheuristic<T>> searches, int runLength) {
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
		timeUnit = TIME_UNIT_MS;
		history = null;
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
	public TimedParallelMultistarter(Multistarter<T> multistartSearch, int numThreads) {
		if (numThreads < 1) throw new IllegalArgumentException("must be at least 1 thread");
		multistarters = new ArrayList<Multistarter<T>>();
		multistarters.add(multistartSearch);
		for (int i = 1; i < numThreads; i++) {
			multistarters.add(multistartSearch.split());
		}
		threadPool = Executors.newFixedThreadPool(numThreads);
		timeUnit = TIME_UNIT_MS;
		history = null;
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
	public TimedParallelMultistarter(Collection<? extends Multistarter<T>> multistarters) {
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
		timeUnit = TIME_UNIT_MS;
		history = null;
	}
	
	/**
	 * Changes the unit of time used by the {@link #optimize} method.
	 * @param timeUnit The unit of time to use with the {@link #optimize} method,
	 * specified in milliseconds.  For example, if timeUnit equals 2000, then the
	 * call optimize(3) will run for approximately 6 seconds, since 3 * 2000 is 6000
	 * milliseconds, which is 6 seconds.
	 * @see #TIME_UNIT_MS
	 * @see #getTimeUnit
	 * @throws IllegalArgumentException if timeUnit is less than 1.
	 */
	public void setTimeUnit(int timeUnit) {
		if (timeUnit < 1) throw new IllegalArgumentException("The unit of time must be at least 1 millisecond.");
		this.timeUnit = timeUnit;
	}
	
	/**
	 * Gets the unit of time used by the {@link #optimize} method.
	 * @return The unit of time used by the {@link #optimize} method,
	 * specified in milliseconds.  For example, if timeUnit equals 2000, then the
	 * call optimize(3) will run for approximately 6 seconds, since 3 * 2000 is 6000
	 * milliseconds, which is 6 seconds.
	 * @see #TIME_UNIT_MS
	 * @see #setTimeUnit
	 */
	public int getTimeUnit() {
		return timeUnit;
	}
	
	/**
	 * Gets a list of the best solution stored in this search's {@link ProgressTracker}
	 * at each time interval of the most recent call to the {@link #optimize} 
	 * method, or null if {@link #optimize} has not been called.
	 * Note that the ProgressTracker stores the best solution found across all calls
	 * to the {@link #optimize} method, so the solutions in the list returned by this
	 * method may or may not have been found during the most recent call to {@link #optimize}.
	 * The length of the list returned will be no greater than the value passed to 
	 * {@link #optimize} for the time parameter.  The length will be less than the time parameter
	 * in the event that the search terminates early due to finding the optimal solution.
	 * 
	 * @return A list of the best found solution, as stored in the ProgressTracker, at each time
	 * interval during the most recent call to the {@link #optimize} method, or null if 
	 * {@link #optimize} has not been called.
	 */
	public ArrayList<SolutionCostPair<T>> getSearchHistory() {
		return history;
	}
	
	/**
	 * <p>Executes a parallel multistart search.  The number of threads, the specific metaheuristic
	 * executed by each thread, the restart schedules, etc are determined by how
	 * the TimedParallelMultistarter was configured at the time of construction. 
	 * All parallel instances of the search are executed for approximately the length of time
	 * indicated by the time parameter, restarting as many times as time allows, 
	 * keeping track of the best solution 
	 * across the multiple parallel runs of the search.
	 * It may terminate earlier if one of the parallel searches indicates the best possible solution
	 * was found.
	 * Each restart of each parallel search begins at a new randomly generate initial state.</p>
	 *
	 * <p>If this method is called multiple times, the restart schedules of the parallel
	 * metaheuristics are not reinitialized,
	 * and the run lengths for the additional restarts will continue where the schedules left off.</p>
	 *
	 * @param time The approximate length of time for the search.  The unit of time is
	 * as indicated by the constant {@link #TIME_UNIT_MS} unless changed by a call to the
	 * {@link #setTimeUnit} method.  For example, assuming {@link #setTimeUnit} has not been called,
	 * then the search will run for approximately: time * {@link #TIME_UNIT_MS} milliseconds.
	 *
	 * @return The best end of run solution (and its cost) of this set of parallel runs, 
	 * which may or may not be the same as the solution contained
	 * in this metaheuristic's {@link ProgressTracker}, which contains the best of all runs.
	 * Returns null if the run did not execute, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 *
	 * @see #setTimeUnit
	 * @see #getTimeUnit
	 *
	 * @throws IllegalStateException if the {@link #close} method was previously called.
	 */
	@Override
	public SolutionCostPair<T> optimize(int time) {
		
		if (threadPool.isShutdown()) {
			throw new IllegalStateException("This TimedParallelMultistarter was previously closed.");
		}
		
		class MultistartCallable implements Callable<SolutionCostPair<T>> {
		
			Multistarter<T> multistartSearch;
			
			MultistartCallable(Multistarter<T> multistartSearch) {
				this.multistartSearch = multistartSearch;
			}
			
			@Override
			public SolutionCostPair<T> call() {
				return multistartSearch.optimize(Integer.MAX_VALUE);
			}
		}
		
		SolutionCostPair<T> bestRestart = null;
		ProgressTracker<T> tracker = multistarters.get(0).getProgressTracker();
		tracker.start();
		history = new ArrayList<SolutionCostPair<T>>(time);
		if (!tracker.didFindBest()) {
			ArrayList<Future<SolutionCostPair<T>>> futures = new ArrayList<Future<SolutionCostPair<T>>>(); 
			for (Multistarter<T> m : multistarters) {
				futures.add(threadPool.submit(new MultistartCallable(m)));
			}
			for (int i = 0; i < time && !tracker.didFindBest(); i++) {
				try {
					Thread.sleep(timeUnit);
				} catch (InterruptedException e) {
					System.err.println("TimedParallelMultistarter was interrupted: " + e);
					break;
				}
				history.add(tracker.getSolutionCostPair());
			}
			tracker.stop();
			for (Future<SolutionCostPair<T>> f : futures) {
				try {
					SolutionCostPair<T> pair = f.get();
					if (bestRestart == null || pair != null && pair.compareTo(bestRestart) < 0) {
						bestRestart = pair;
					}
				} 
				catch (Exception ex) {
					// There are two possible exception types that will get us in here:
					// 1) InterruptedException: Ignore temporarily while waiting
					//        for the threads to respond to our tracker.stop() above.
					// 2) ExecutionException: Future.get() throws this if the thread
					//        throws any exception. We'll ignore this too, skipping
					//        the problematic thread and collecting results of other
					//        threads.
				}
			}
		}
		return bestRestart; 
	}
	
	/**
	 * <p>Initiates an orderly shutdown of the thread pool used by this TimedParallelMultistarter.
	 * The TimedParallelMultistarter utilizes a fixed thread pool so that multiple calls to
	 * the {@link #optimize} method can reuse threads to minimize the expensive task of
	 * thread creation.  When you no longer need the TimedParallelMultistarter, you should call
	 * the close method to ensure that unneeded threads do not persist.
	 * Once close is called, all subsequent calls to {@link #optimize} will throw an exception.</p>
	 * <p>This method is invoked automatically on objects managed by the try-with-resources statement.</p>
	 */
	@Override
	public void close() {
		threadPool.shutdown();
	}
	
	@Override
	public TimedParallelMultistarter<T> split() {
		ArrayList<Multistarter<T>> splits = new ArrayList<Multistarter<T>>();
		for (Multistarter<T> m : multistarters) {
			splits.add(m.split());
		}
		TimedParallelMultistarter<T> pm = new TimedParallelMultistarter<T>(splits);
		pm.setTimeUnit(timeUnit);
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