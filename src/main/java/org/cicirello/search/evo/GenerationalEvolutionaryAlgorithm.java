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

import org.cicirello.util.Copyable;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.math.rand.RandomVariates;

/**
 * <p>This class implements an evolutionary algorithm with a generational
 * model, such as is commonly used in genetic algorithms, where a
 * population of children are formed by applying genetic operators to
 * members of the parent population, and where the children replace the 
 * parents in the next generation.</p>
 *
 * <p>The crossover, mutation, and selection operators are completely configurable
 * by passing instances of classes that implement the {@link CrossoverOperator},
 * {@link MutationOperator}, and {@link SelectionOperator} classes to one of the
 * constructors.</p>
 *
 * <p>This class supports a variety of evolutionary algorithm models, depending
 * upon the constructor you use, including:</p>
 * <ul>
 * <li>The typical generational model using both crossover and mutation, controlled by
 * a crossover rate and a mutation rate, such that each child may be the result of
 * crossover alone, mutation alone, a combination of both crossover and mutation, or
 * a simple copy of a parent.</li>
 * <li>A generational model using mutually exclusive crossover and mutation operators, 
 * controlled by a crossover rate and a mutation rate, but such that each child is the
 * result of crossover, or mutation, or a simply copy of a parent, but never the result
 * of both crossover and mutation.</li>
 * <li>A generational mutation-only evolutionary algorithm.</li>
 * </ul>
 * <p>Note that it does not include a constructor dedicated to a crossover-only case
 * since it would be rare (if ever) that you would find it desirable not to use a 
 * mutation operator. However, if you find a crossover-only use-case, then simply
 * pass any mutation operator and 0.0 for the mutation rate to one of the constructors.</p>
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class GenerationalEvolutionaryAlgorithm<T extends Copyable<T>> extends AbstractEvolutionaryAlgorithm<T> {
	
	private final MutationOperator<T> mutation;
	private final double M;
	private final CrossoverOperator<T> crossover;
	private final double C;
	
	private final SingleGen<T> sr;
	private final GenerationOption go;
	
	// Constructors for standard generational model using both crossover and mutation operators
	
	/**
	 * Constructs and initializes the evolutionary algorithm for a typical EA utilizing both a crossover operator
	 * and a mutation operator, and such that members of the population are permitted to undergo both crossover and
	 * mutation in the same generation. This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(new BasePopulation.Double<T>(n, initializer, f, selection, tracker), f.getProblem(), mutation, mutationRate, crossover, crossoverRate, false);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for a typical EA utilizing both a crossover operator
	 * and a mutation operator, and such that members of the population are permitted to undergo both crossover and
	 * mutation in the same generation. This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(new BasePopulation.Integer<T>(n, initializer, f, selection, tracker), f.getProblem(), mutation, mutationRate, crossover, crossoverRate, false);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for a typical EA utilizing both a crossover operator
	 * and a mutation operator, and such that members of the population are permitted to undergo both crossover and
	 * mutation in the same generation. This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection) {
		this(new BasePopulation.Double<T>(n, initializer, f, selection, new ProgressTracker<T>()), f.getProblem(), mutation, mutationRate, crossover, crossoverRate, false);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for a typical EA utilizing both a crossover operator
	 * and a mutation operator, and such that members of the population are permitted to undergo both crossover and
	 * mutation in the same generation. This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection) {
		this(new BasePopulation.Integer<T>(n, initializer, f, selection, new ProgressTracker<T>()), f.getProblem(), mutation, mutationRate, crossover, crossoverRate, false);
	}
	
	
	// Constructors for generational model with both crossover and mutation but 
	// special case where crossover and mutation are mutually exclusive (i.e.,
	// each member of the population can be involved in crossover or mutation, but not
	// both).
	
	/**
	 * Constructs and initializes the evolutionary algorithm for a typical EA utilizing both a crossover operator
	 * and a mutation operator. This constructor enables configuring the EA for either the more common case where
	 * population members are permitted to undergo both crossover and mutation in the same generation 
	 * (mutuallyExclusiveOps = false), as well as the case when the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation
	 * (mutuallyExclusiveOps = true). This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param mutuallyExclusiveOps If true, each member of the population will undergo crossover or mutation, but never both.
	 *   If false, then a member of the population may undergo both crossover and mutation.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, boolean mutuallyExclusiveOps, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(new BasePopulation.Double<T>(n, initializer, f, selection, tracker), f.getProblem(), mutation, mutationRate, crossover, crossoverRate, mutuallyExclusiveOps);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for a typical EA utilizing both a crossover operator
	 * and a mutation operator. This constructor enables configuring the EA for either the more common case where
	 * population members are permitted to undergo both crossover and mutation in the same generation 
	 * (mutuallyExclusiveOps = false), as well as the case when the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation
	 * (mutuallyExclusiveOps = true). This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param mutuallyExclusiveOps If true, each member of the population will undergo crossover or mutation, but never both.
	 *   If false, then a member of the population may undergo both crossover and mutation.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, boolean mutuallyExclusiveOps, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(new BasePopulation.Integer<T>(n, initializer, f, selection, tracker), f.getProblem(), mutation, mutationRate, crossover, crossoverRate, mutuallyExclusiveOps);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for a typical EA utilizing both a crossover operator
	 * and a mutation operator. This constructor enables configuring the EA for either the more common case where
	 * population members are permitted to undergo both crossover and mutation in the same generation 
	 * (mutuallyExclusiveOps = false), as well as the case when the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation
	 * (mutuallyExclusiveOps = true). This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param mutuallyExclusiveOps If true, each member of the population will undergo crossover or mutation, but never both.
	 *   If false, then a member of the population may undergo both crossover and mutation.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, boolean mutuallyExclusiveOps, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection) {
		this(new BasePopulation.Double<T>(n, initializer, f, selection, new ProgressTracker<T>()), f.getProblem(), mutation, mutationRate, crossover, crossoverRate, mutuallyExclusiveOps);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm for a typical EA utilizing both a crossover operator
	 * and a mutation operator. This constructor enables configuring the EA for either the more common case where
	 * population members are permitted to undergo both crossover and mutation in the same generation 
	 * (mutuallyExclusiveOps = false), as well as the case when the genetic operators follow a mutually exclusive 
	 * property where each population member is involved in at most one of those operations in a single generation
	 * (mutuallyExclusiveOps = true). This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param crossover The crossover operator.
	 * @param crossoverRate The probability that a pair of parents undergo crossover.
	 * @param mutuallyExclusiveOps If true, each member of the population will undergo crossover or mutation, but never both.
	 *   If false, then a member of the population may undergo both crossover and mutation.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, boolean mutuallyExclusiveOps, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection) {
		this(new BasePopulation.Integer<T>(n, initializer, f, selection, new ProgressTracker<T>()), f.getProblem(), mutation, mutationRate, crossover, crossoverRate, mutuallyExclusiveOps);
	}
	
	
	// Mutation-Only Constructors
	
	/**
	 * Constructs and initializes the evolutionary algorithm with mutation only. This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if mutationRate is less than 0.
	 * @throws NullPointerException if any of mutation, initializer, f, selection, or tracker are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(new BasePopulation.Double<T>(n, initializer, f, selection, tracker), f.getProblem(), mutation, mutationRate);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm with mutation only. This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if mutationRate is less than 0.
	 * @throws NullPointerException if any of mutation, initializer, f, selection, or tracker are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(new BasePopulation.Integer<T>(n, initializer, f, selection, tracker), f.getProblem(), mutation, mutationRate);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm with mutation only. This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if mutationRate is less than 0.
	 * @throws NullPointerException if any of mutation, initializer, f, or selection are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection) {
		this(new BasePopulation.Double<T>(n, initializer, f, selection, new ProgressTracker<T>()), f.getProblem(), mutation, mutationRate);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm with mutation only. This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param mutationRate The probability that a member of the population is mutated once during a generation. Note that
	 *     this is not a per-bit rate since this class is generalized to evolution of any {@link Copyable} object type.
	 *     For {@link org.cicirello.search.representations.BitVector} optimization and traditional genetic algorithm 
	 *     interpretation of mutation rate, configure
	 *     your mutation operator with the per-bit mutation rate, and then pass 1.0 for this parameter.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if mutationRate is less than 0.
	 * @throws NullPointerException if any of mutation, initializer, f, or selection are null.
	 */
	public GenerationalEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, double mutationRate, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection) {
		this(new BasePopulation.Integer<T>(n, initializer, f, selection, new ProgressTracker<T>()), f.getProblem(), mutation, mutationRate);
	}
	
	
	// Internal Constructors
	
	/*
	 * Internal helper constructor for standard EAs with full generation (both crossover and mutation).
	 */
	private GenerationalEvolutionaryAlgorithm(Population<T> pop, Problem<T> problem, MutationOperator<T> mutation, double mutationRate, CrossoverOperator<T> crossover, double crossoverRate, boolean mutuallyExclusiveOps) {
		super(pop, problem);
		if (mutation == null) {
			throw new NullPointerException("mutation must be non-null");
		}
		if (crossover == null) {
			throw new NullPointerException("crossover must be non-null");
		}
		if (mutationRate < 0.0) {
			throw new IllegalArgumentException("mutationRate must not be negative");
		}
		if (crossoverRate < 0.0) {
			throw new IllegalArgumentException("crossoverRate must not be negative");
		}
		
		if (mutuallyExclusiveOps) {
			if (mutationRate + crossoverRate > 1.0) {
				throw new IllegalArgumentException("mutually exclusive operators requires mutationRate + crossoverRate <= 1.0");
			}
			M = mutationRate;
			sr = mutuallyExclusiveOperators();
			go = GenerationOption.MUTUALLY_EXCLUSIVE_OPERATORS;
		} else if (mutationRate < 1.0) {
			M = mutationRate;
			sr = fullGeneration(); 
			go = GenerationOption.FULL_GENERATION;
		} else {
			M = 1.0;
			sr = alwaysMutateFullGeneration();
			go = GenerationOption.FULL_GENERATION_ALWAYS_MUTATE;
		}
		C = crossoverRate < 1.0 ? crossoverRate : 1.0;
		this.mutation = mutation;
		this.crossover = crossover;
	}
	
	/*
	 * Internal helper constructor for Mutation-Only EAs.
	 */
	private GenerationalEvolutionaryAlgorithm(Population<T> pop, Problem<T> problem, MutationOperator<T> mutation, double mutationRate) {
		super(pop, problem);
		if (mutation == null) {
			throw new NullPointerException("mutation must be non-null");
		}
		if (mutationRate < 0.0) {
			throw new IllegalArgumentException("mutationRate must not be negative");
		}
		this.mutation = mutation;
		crossover = null;
		C = 0.0;
		
		if (mutationRate < 1.0) {
			M = mutationRate;
			sr = mutationOnly(); 
			go = GenerationOption.MUTATION_ONLY;
		} else {
			M = 1.0;
			sr = alwaysMutate();
			go = GenerationOption.ALWAYS_MUTATION;
		}
	}
	
	/*
	 * Internal constructor for use by split method.
	 * package private so subclasses in same package can use it for initialization for their own split methods.
	 */
	GenerationalEvolutionaryAlgorithm(GenerationalEvolutionaryAlgorithm<T> other) {
		super(other);
		
		// Must be split
		mutation = other.mutation.split();
		crossover = other.crossover != null ? other.crossover.split() : null; 
		
		// Threadsafe so just copy reference or values
		M = other.M;
		C = other.C;
		
		// Initialize the runner   
		go = other.go;
		switch (go) {
			case FULL_GENERATION: sr = fullGeneration(); break;
			case FULL_GENERATION_ALWAYS_MUTATE: sr = alwaysMutateFullGeneration(); break;
			case MUTUALLY_EXCLUSIVE_OPERATORS: sr = mutuallyExclusiveOperators(); break;
			case MUTATION_ONLY: sr = mutationOnly(); break;
			default: //case ALWAYS_MUTATION: 
				sr = alwaysMutate(); break;
		}
	}
	
	@Override
	public GenerationalEvolutionaryAlgorithm<T> split() {
		return new GenerationalEvolutionaryAlgorithm<T>(this);
	}
		
	private interface SingleGen<T extends Copyable<T>> {
		int optimizeSingleGen(Population<T> pop);
	}
	
	private enum GenerationOption {
		FULL_GENERATION, FULL_GENERATION_ALWAYS_MUTATE, MUTUALLY_EXCLUSIVE_OPERATORS, MUTATION_ONLY, ALWAYS_MUTATION
	}
	
	@Override
	final int oneGeneration(Population<T> pop) {
		return sr.optimizeSingleGen(pop);
	}
	
	private SingleGen<T> mutuallyExclusiveOperators() {
		return (pop) -> {
			pop.select();
			// Since select() above randomizes ordering, just use a binomial
			// to get count of number of pairs of parents to cross and cross the first 
			// count pairs of parents. Pair up parents with indexes: first and (first + count).
			final int count = C < 1.0 ? RandomVariates.nextBinomial(pop.mutableSize()/2, C) : pop.mutableSize()/2;
			for (int first = 0; first < count; first++) {
				int second = first + count;
				crossover.cross(pop.get(first), pop.get(second));
				pop.updateFitness(first);
				pop.updateFitness(second);
			}
			final int crossed = count + count;
			int mutateCount = 0;
			if (C < 1.0) {
				final double M_PRIME = M / (1.0 - C);
				mutateCount = crossed < pop.mutableSize() ? 
					(M_PRIME < 1.0 ? RandomVariates.nextBinomial(pop.mutableSize()-crossed, M_PRIME) : pop.mutableSize()-crossed) 
					: 0;
				for (int j = crossed + mutateCount - 1; j >= crossed; j--) {
					mutation.mutate(pop.get(j));
					pop.updateFitness(j);
				}
			}
			pop.replace();
			return crossed + mutateCount;
		};
	}
	
	private SingleGen<T> fullGeneration() {
		return (pop) -> {
			pop.select();
			// Since select() above randomizes ordering, just use a binomial
			// to get count of number of pairs of parents to cross and cross the first 
			// count pairs of parents. Pair up parents with indexes: first and (first + count).
			final int count = RandomVariates.nextBinomial(pop.mutableSize()/2, C);
			for (int first = 0; first < count; first++) {
				int second = first + count;
				crossover.cross(pop.get(first), pop.get(second));
				pop.updateFitness(first);
				pop.updateFitness(second);
			}
			int[] operateOnThese = RandomIndexer.sample(pop.mutableSize(), M);
			for (int j = 0; j < operateOnThese.length; j++) {
				mutation.mutate(pop.get(operateOnThese[j]));
				pop.updateFitness(operateOnThese[j]);
			}
			pop.replace();
			return operateOnThese.length + count + count;
		};
	}
	
	private SingleGen<T> alwaysMutateFullGeneration() {
		return (pop) -> {
			pop.select();
			// Since select() above randomizes ordering, just use a binomial
			// to get count of number of pairs of parents to cross and cross the first 
			// count pairs of parents. Pair up parents with indexes: first and (first + count).
			final int count = RandomVariates.nextBinomial(pop.mutableSize()/2, C);
			for (int first = 0; first < count; first++) {
				int second = first + count;
				crossover.cross(pop.get(first), pop.get(second));
				pop.updateFitness(first);
				pop.updateFitness(second);
			}
			final int LAMBDA = pop.mutableSize();
			for (int j = 0; j < LAMBDA; j++) {
				mutation.mutate(pop.get(j));
				pop.updateFitness(j);
			}
			pop.replace();
			return LAMBDA + count + count;
		};
	}
	
	private SingleGen<T> mutationOnly() {
		return (pop) -> {
			pop.select();
			// Since select() above randomizes ordering, just use a binomial
			// to get count of how many to mutate and mutate the first count individuals.
			final int count = RandomVariates.nextBinomial(pop.mutableSize(), M);
			for (int j = 0; j < count; j++) {
				mutation.mutate(pop.get(j));
				pop.updateFitness(j);
			}
			pop.replace();
			return count;
		};
	}
	
	private SingleGen<T> alwaysMutate() {
		return (pop) -> {
			final int LAMBDA = pop.mutableSize();
			pop.select();
			for (int j = 0; j < LAMBDA; j++) {
				mutation.mutate(pop.get(j));
				pop.updateFitness(j);
			}
			pop.replace();
			return LAMBDA;
		};
	}
	
}