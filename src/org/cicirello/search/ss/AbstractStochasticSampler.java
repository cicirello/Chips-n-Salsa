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
 
package org.cicirello.search.ss;

import org.cicirello.search.SimpleMetaheuristic;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.util.Copyable;

/**
 * <p>This class serves as an abstract base class for the stochastic
 * sampling search algorithms, implementing the common functionality.</p> 
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 8.12.2020
 */
abstract class AbstractStochasticSampler<T extends Copyable<T>> implements SimpleMetaheuristic<T>, Metaheuristic<T> {
	
	final OptimizationProblem<T> pOpt;
	final IntegerCostOptimizationProblem<T> pOptInt;
	ProgressTracker<T> tracker;
	private final Sampler<T> sampler;
	private int numGenerated;
	
	/**
	 * Constructs a AbstractStochasticSampler search object.
	 * @param problem The optimization problem to solve.
	 * @param tracker A ProgressTracker
	 * @throws NullPointerException if problem or tracker is null.
	 */
	AbstractStochasticSampler(Problem<T> problem, ProgressTracker<T> tracker) {
		if (problem == null || tracker == null) {
			throw new NullPointerException();
		}
		this.tracker = tracker;
		// default: numGenerated = 0;
		if (problem instanceof IntegerCostOptimizationProblem) {
			pOptInt = (IntegerCostOptimizationProblem<T>)problem;
			pOpt = null;
			sampler = initSamplerInt();
		} else {
			pOpt = (OptimizationProblem<T>)problem;
			pOptInt = null;
			sampler = initSamplerDouble();
		}
	}
	
	/*
	 * package-private copy constructor in support of the split method.
	 * note: copies references to thread-safe components, and splits potentially non-threadsafe components 
	 */
	AbstractStochasticSampler(AbstractStochasticSampler<T> other) {
		// these are threadsafe, so just copy references
		pOpt = other.pOpt;
		pOptInt = other.pOptInt;
		
		// this one must be shared.
		tracker = other.tracker;
		
		// not threadsafe
		sampler = pOptInt != null ? initSamplerInt() : initSamplerDouble();
		
		// use default of 0 for this one: numGenerated
	}
	
	@Override
	public final SolutionCostPair<T> optimize() {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		numGenerated++;
		return sampler.optimize();
	}
	
	/**
	 * <p>Generates multiple stochastic heuristic samples.  
	 * Returns the best solution of the set of samples.</p>
	 *
	 * @param numSamples The number of samples to perform.
	 * @return The best solution of this set of samples, which may or may not be the 
	 * same as the solution contained
	 * in this search's {@link org.cicirello.search.ProgressTracker ProgressTracker}, 
	 * which contains the best of all runs
	 * across all calls to the various optimize methods.
	 * Returns null if no runs executed, such as if the ProgressTracker already contains
	 * the theoretical best solution.
	 */
	@Override
	public final SolutionCostPair<T> optimize(int numSamples) {
		if (tracker.didFindBest() || tracker.isStopped()) return null;
		SolutionCostPair<T> best = null;
		for (int i = 0; i < numSamples && !tracker.didFindBest() && !tracker.isStopped(); i++) {
			SolutionCostPair<T> current = sampler.optimize();
			numGenerated++;
			if (best == null || current.compareTo(best) < 0) best = current;
		}
		return best;
	}
	
	@Override
	public final ProgressTracker<T> getProgressTracker() {
		return tracker;
	}
	
	@Override
	public final void setProgressTracker(ProgressTracker<T> tracker) {
		if (tracker != null) this.tracker = tracker;
	}
	
	@Override
	public final long getTotalRunLength() {
		return numGenerated;
	}
	
	@Override
	public final Problem<T> getProblem() {
		return (pOptInt != null) ? pOptInt : pOpt;
	}

	@Override
	public abstract AbstractStochasticSampler<T> split();
	
	
	interface Sampler<T extends Copyable<T>> {
		SolutionCostPair<T> optimize();
	}
	
	abstract Sampler<T> initSamplerInt();
	
	abstract Sampler<T> initSamplerDouble();
}

