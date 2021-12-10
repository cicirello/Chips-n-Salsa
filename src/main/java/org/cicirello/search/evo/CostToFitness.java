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
 
package org.cicirello.search.evo;

import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.util.Copyable;

/**
 * <p>This class provides a convenient mechanism for transforming optimization cost
 * values to fitness values. Most of the algorithms in the library require a
 * cost function to minimize through a class that implements either
 * {@link OptimizationProblem} or {@link IntegerCostOptimizationProblem}. However,
 * the evolutionary algorithms in the library require a fitness function such that higher
 * fitness implies better solution. Furthermore, some selection operators further assume
 * that fitness values are positive, such as {@link FitnessProportionalSelection} and
 * {@link StochasticUniversalSampling}.</p>
 *
 * <p>This class transforms the cost of solution s to fitness to meet these requirements 
 * with the following
 * transformation: fitness(s) = c / (c + problem.cost(s) - problem.minCost()), where c
 * is a positive constant, which defaults to 1.0 (see constructors). The problem.cost(s)
 * and problem.minCost() refer to the methods by those names in the 
 * {@link OptimizationProblem} and {@link IntegerCostOptimizationProblem} classes. Note that
 * the adjustment by problem.minCost() ensures that fitness will be positive even if
 * costs can be negative.</p>
 *
 * <p>Note that the problem's implementation of minCost must return a finite lower bound
 * for the cost function (which is assumed to be correct), otherwise the constructors of 
 * this class will throw an {@link IllegalArgumentException}.</p>
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class CostToFitness<T extends Copyable<T>> implements FitnessFunction.Double<T> {
	
	private final Problem<T> problem;
	private final double c;
	private final double adjustment;
	
	/**
	 * Constructs a fitness function that transforms the cost of solution s to fitness with
	 * the following transformation: fitness(s) = 1.0 / (1.0 + problem.cost(s) - problem.minCost()).
	 *
	 * @param problem The optimization problem.
	 *
	 * @throws IllegalArgumentException if problem.minCost() is non-finite, such as infinite or nan.
	 */
	public CostToFitness(OptimizationProblem<T> problem) {
		this(problem, 1.0);
	}
	
	/**
	 * Constructs a fitness function that transforms the cost of solution s to fitness with
	 * the following transformation: fitness(s) = 1.0 / (1.0 + problem.cost(s) - problem.minCost()).
	 *
	 * @param problem The optimization problem.
	 *
	 * @throws IllegalArgumentException if problem.minCost() equals Integer.MAX_VALUE or Integer.MIN_VALUE.
	 */
	public CostToFitness(IntegerCostOptimizationProblem<T> problem) {
		this(problem, 1.0);
	}
	
	/**
	 * Constructs a fitness function that transforms the cost of solution s to fitness with
	 * the following transformation: fitness(s) = c / (c + problem.cost(s) - problem.minCost()).
	 *
	 * @param problem The optimization problem.
	 * @param c A constant which must be positive.
	 *
	 * @throws IllegalArgumentException if c is less than or equal to 0.0.
	 * @throws IllegalArgumentException if problem.minCost() is non-finite, such as infinite or nan.
	 */
	public CostToFitness(OptimizationProblem<T> problem, double c) {
		if (c <= 0.0) {
			throw new IllegalArgumentException("c must be positive");
		}
		adjustment = problem.minCost();
		if (!java.lang.Double.isFinite(adjustment)) {
			throw new IllegalArgumentException("Only supports problems that provide finite cost lower bound via minCost method.");
		}
		this.c = c;
		this.problem = problem;
	}
	
	/**
	 * Constructs a fitness function that transforms the cost of solution s to fitness with
	 * the following transformation: fitness(s) = c / (c + problem.cost(s) - problem.minCost()).
	 *
	 * @param problem The optimization problem.
	 * @param c A constant which must be positive.
	 *
	 * @throws IllegalArgumentException if c is less than or equal to 0.0.
	 * @throws IllegalArgumentException if problem.minCost() equals Integer.MAX_VALUE or Integer.MIN_VALUE.
	 */
	public CostToFitness(IntegerCostOptimizationProblem<T> problem, double c) {
		if (c <= 0.0) {
			throw new IllegalArgumentException("c must be positive");
		}
		int m = problem.minCost();
		if (m == java.lang.Integer.MAX_VALUE || m == java.lang.Integer.MIN_VALUE) {
			throw new IllegalArgumentException("Only supports problems that provide finite cost lower bound via minCost method.");
		}
		adjustment = m;
		this.c = c;
		this.problem = problem;
	}
	
	@Override
	public double fitness(T candidate) {
		return c / (c + problem.costAsDouble(candidate) - adjustment);
	}
	
	@Override
	public Problem<T> getProblem() {
		return problem;
	}
}