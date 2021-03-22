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
import org.cicirello.util.Copyable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.Collection;
import java.util.function.Function;

/**
 * <p>This class enables running multiple copies of a metaheuristic, or multiple metaheuristics, 
 * in parallel with multiple threads. It specifically requires that all metaheuristics are
 * solving the same problem, but otherwise they may be the same or different metaheuristics.</p>
 *
 * @param <T> The type of object being optimized.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.22.2021
 */
public class ParallelReoptimizableMetaheuristic<T extends Copyable<T>> extends ParallelMetaheuristic<T> implements ReoptimizableMetaheuristic<T> {
	
	/**
	 * Constructs a parallel metaheuristic that executes multiple identical copies of a
	 * metaheuristic in parallel across multiple threads.    
	 * @param search The metaheuristic to run in parallel.
	 * @param numThreads The number of threads to use.
	 * @throws IllegalArgumentException if numThreads is less than 1.
	 */
	public ParallelReoptimizableMetaheuristic(ReoptimizableMetaheuristic<T> search, int numThreads) {
		super(search, numThreads);
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
	public ParallelReoptimizableMetaheuristic(Collection<? extends ReoptimizableMetaheuristic<T>> searches) {
		super(searches);
	}
	
	/*
	 * package-private copy constructor to support split() method.
	 */
	ParallelReoptimizableMetaheuristic(ParallelReoptimizableMetaheuristic<T> other) {
		super(other);
	}
	
	/*
	 * package private for use by subclasses in same package.
	 */
	ParallelReoptimizableMetaheuristic(Collection<? extends ReoptimizableMetaheuristic<T>> searches, boolean verifyState) {
		super(searches, verifyState);
	}
	
	/**
	 * <p>Executes a parallel metaheuristic search.  The number of threads, the specific metaheuristic
	 * executed by each thread, etc are determined by how
	 * the ParallelReoptimizableMetaheuristic was configured at the time of construction.  
	 * The reoptimize method runs the reoptimize method of each of the parallel 
	 * instances of the search the specified for the specified run length (identical for
	 * all parallel instances), keeping track of the best solution 
	 * across the multiple parallel runs of the search.</p>
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
	 * @throws IllegalStateException if the {@link ParallelMetaheuristic#close} method 
	 * was previously called.
	 */
	@Override
	public final SolutionCostPair<T> reoptimize(int runLength) {
		return threadedOptimize((search) -> (
			() -> ((ReoptimizableMetaheuristic<T>)search).reoptimize(runLength)
		));
	}
	
	@Override
	public ParallelReoptimizableMetaheuristic<T> split() {
		return new ParallelReoptimizableMetaheuristic<T>(this);
	}
	
}
