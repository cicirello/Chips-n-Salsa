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

import org.cicirello.util.Copyable;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.operators.Initializer;

/**
 * <p>Iterative sampling is the simplest possible form of a stochastic sampling search.
 * In iterative sampling, the search generates N random candidate solutions to the 
 * problem, each sampled uniformly at random from the space of possible solutions.
 * It evaluates each of the N candidate solutions with respect to the optimization
 * problem's cost function, and returns the best of the N candidate solutions.</p>
 *
 * <p>For an early empirical comparison of iterative sampling with systematic search
 * algorithms, see:<br>
 * P. Langley. Systematic and nonsystematic search strategies. 
 * Proceedings of the First International Conference on Artificial Intelligence
 * Planning Systems, pages 145–152, 1992.</p>
 *
 * @param <T> The type of object under optimization.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 8.12.2020
 */
public final class IterativeSampling<T extends Copyable<T>> extends AbstractStochasticSampler<T> {
	
	private final Initializer<T> initializer;
	
	public static final int XYZ = 0;
	/**
	 * Constructs an iterative sampling search for a real-valued optimization problem.
	 * @param problem An instance of an optimization problem to solve.
	 * @param initializer The source of random solutions.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public IterativeSampling(OptimizationProblem<T> problem, Initializer<T> initializer, ProgressTracker<T> tracker) {
		super(problem, tracker);
		if (initializer == null) {
			throw new NullPointerException();
		}
		this.initializer = initializer;
	}
	
	/**
	 * Constructs an iterative sampling search for a integer-valued optimization problem.
	 * @param problem An instance of an optimization problem to solve.
	 * @param initializer The source of random solutions.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public IterativeSampling(IntegerCostOptimizationProblem<T> problem, Initializer<T> initializer, ProgressTracker<T> tracker) {
		super(problem, tracker);
		if (initializer == null) {
			throw new NullPointerException();
		}
		this.initializer = initializer;
	}
	
	/**
	 * Constructs an iterative sampling search for a real-valued optimization problem.
	 * A {@link ProgressTracker} is created for you.
	 * @param problem An instance of an optimization problem to solve.
	 * @param initializer The source of random solutions.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public IterativeSampling(OptimizationProblem<T> problem, Initializer<T> initializer) {
		this(problem, initializer, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs an iterative sampling search for a integer-valued optimization problem.
	 * A {@link ProgressTracker} is created for you.
	 * @param problem An instance of an optimization problem to solve.
	 * @param initializer The source of random solutions.
	 * @throws NullPointerException if any of the parameters are null.
	 */
	public IterativeSampling(IntegerCostOptimizationProblem<T> problem, Initializer<T> initializer) {
		this(problem, initializer, new ProgressTracker<T>());
	}
	
	/*
	 * private copy constructor in support of the split method.
	 * note: copies references to thread-safe components, and splits potentially non-threadsafe components 
	 */
	private IterativeSampling(IterativeSampling<T> other) {
		super(other);
		// split: might not be threadsafe
		initializer = other.initializer.split();
	}
	
	@Override
	public IterativeSampling<T> split() {
		return new IterativeSampling<T>(this);
	}
	
	@Override
	SolutionCostPair<T> sample() {
		T s = initializer.createCandidateSolution();
		return evaluateAndPackageSolution(s);
	}
}