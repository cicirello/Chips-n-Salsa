/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2022 Vincent A. Cicirello
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

import org.cicirello.search.representations.BitVector;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.operators.bits.BitFlipMutation;
import org.cicirello.search.operators.bits.BitVectorInitializer;
import org.cicirello.search.ProgressTracker;

/**
 * <p>This class implements a (1+1)-GA, a special case of a (1+1)-EA, where solutions
 * are represented with a vector of bits. In a (1+1)-EA, the evolutionary algorithm has a
 * population size of 1, in each cycle of the algorithm a single mutant is created from
 * that single population member, forming a population of size 2, and finally the EA
 * keeps the better of the two solutions. This is perhaps the simplest case of an EA.
 * This class supports optimizing BitVector objects. Mutation is the standard bit-flip
 * mutation of a genetic algorithm, where a mutation rate M specifies the probability 
 * that each bit flips (from 0 to 1 or vice versa) during a mutation.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class OnePlusOneGeneticAlgorithm extends OnePlusOneEvolutionaryAlgorithm<BitVector> {
	
	/**
	 * Creates a OnePlusOneGeneticAlgorithm instance for real-valued optimization problems.
	 * A {@link ProgressTracker} is created for you.
	 *
	 * @param problem An instance of an optimization problem to solve.
	 * @param m The probability of flipping each bit during a mutation, which must be greater than 0.0
	 *        and less than 1.0.
	 * @param bitLength The length of BitVectors required to represent solutions to the problem.
	 *
	 * @throws IllegalArgumentException if m &le; 0 or m &ge; 1 or if bitLength is negative.
	 * @throws NullPointerException if problem is null.
	 */
	public OnePlusOneGeneticAlgorithm(OptimizationProblem<BitVector> problem, double m, int bitLength) {
		this(problem, m, bitLength, new ProgressTracker<BitVector>());
	}
	
	/**
	 * Creates a OnePlusOneGeneticAlgorithm instance for integer-valued optimization problems.
	 * A {@link ProgressTracker} is created for you.
	 *
	 * @param problem An instance of an optimization problem to solve.
	 * @param m The probability of flipping each bit during a mutation, which must be greater than 0.0
	 *        and less than 1.0.
	 * @param bitLength The length of BitVectors required to represent solutions to the problem.
	 *
	 * @throws IllegalArgumentException if m &le; 0 or m &ge; 1 or if bitLength is negative.
	 * @throws NullPointerException if problem is null.
	 */
	public OnePlusOneGeneticAlgorithm(IntegerCostOptimizationProblem<BitVector> problem, double m, int bitLength) {
		this(problem, m, bitLength, new ProgressTracker<BitVector>());
	}
	
	/**
	 * Creates a OnePlusOneGeneticAlgorithm instance for real-valued optimization problems.
	 *
	 * @param problem An instance of an optimization problem to solve.
	 * @param m The probability of flipping each bit during a mutation, which must be greater than 0.0
	 *        and less than 1.0.
	 * @param bitLength The length of BitVectors required to represent solutions to the problem.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 *
	 * @throws IllegalArgumentException if m &le; 0 or m &ge; 1 or if bitLength is negative.
	 * @throws NullPointerException if problem is null or if tracker is null.
	 */
	public OnePlusOneGeneticAlgorithm(OptimizationProblem<BitVector> problem, double m, int bitLength, ProgressTracker<BitVector> tracker) {
		super(problem, new BitFlipMutation(m), new BitVectorInitializer(bitLength), tracker);
	}
	
	/**
	 * Creates a OnePlusOneGeneticAlgorithm instance for integer-valued optimization problems.
	 *
	 * @param problem An instance of an optimization problem to solve.
	 * @param m The probability of flipping each bit during a mutation, which must be greater than 0.0
	 *        and less than 1.0.
	 * @param bitLength The length of BitVectors required to represent solutions to the problem.
	 * @param tracker A ProgressTracker object, which is used to keep track of the best
	 * solution found during the run, the time when it was found, and other related data.
	 *
	 * @throws IllegalArgumentException if m &le; 0 or m &ge; 1 or if bitLength is negative.
	 * @throws NullPointerException if problem is null or if tracker is null.
	 */
	public OnePlusOneGeneticAlgorithm(IntegerCostOptimizationProblem<BitVector> problem, double m, int bitLength, ProgressTracker<BitVector> tracker) {
		super(problem, new BitFlipMutation(m), new BitVectorInitializer(bitLength), tracker);
	}
	
	/*
	 * private copy constructor in support of the split method.
	 */
	private OnePlusOneGeneticAlgorithm(OnePlusOneGeneticAlgorithm other) {
		super(other);
	}
	
	@Override
	public OnePlusOneGeneticAlgorithm split() {
		return new OnePlusOneGeneticAlgorithm(this);
	}
}
