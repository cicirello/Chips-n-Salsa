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

import org.cicirello.util.Copyable;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * <p>This class implements an evolutionary algorithm with adaptive control 
 * parameters (i.e., crossover rates and mutation rates that evolve during 
 * the search). It follows a generational model, where a population of 
 * children are formed by applying genetic operators to members of the 
 * parent population, and the children replace the parents in the next 
 * generation. It uses the typical generational model using both crossover 
 * and mutation, such that each child may be the result of crossover alone, 
 * mutation alone, a combination of both crossover and mutation, or
 * a simple copy of a parent.</p>
 *
 * <p>Rather than specifying crossover and mutation rates, this adaptive
 * evolutionary algorithm evolves these during the search. Each member of
 * the population consists of an encoding of a candidate solution to the
 * problem, along with a crossover rate C<sub>i</sub>, a mutation rate 
 * M<sub>i</sub>, and a parameter &sigma;<sub>i</sub>. During a generation,
 * parents are paired at random. Consider that i and j are parents. One of 
 * these is chosen arbitrarily. For example, consider that i was chosen.
 * With probability C<sub>i</sub> the crossover operator is applied to the
 * parents, and otherwise it is not. Then, the mutation operator is applied
 * to each member of the population i with probability M<sub>i</sub>. Note that
 * this class implements an evolutionary algorithm for the general case,
 * and not strictly bit strings, so the M<sub>i</sub> is not a per-bit
 * rate. Rather, it is the probability of a single application of whatever
 * the mutation operator is.</p>
 *
 * <p>After applying the genetic operators, all of the C<sub>i</sub> and
 * M<sub>i</sub> are themselves mutated. Specifically, each is mutated with
 * a Gaussian mutation with standard deviation &sigma;<sub>i</sub>. The
 * &sigma;<sub>i</sub> are then also mutated by a Gaussian mutation with
 * standard deviation of 0.01. The C<sub>i</sub> and M<sub>i</sub> are 
 * initialized randomly at the start such that they are each in the 
 * interval [0.1, 1.0], and the Gaussian mutation is implemented to
 * ensure that they remain in that interval (e.g., reset to 0.1 if it is
 * ever too low, and to 1.0 if it is ever too high). The &sigma;<sub>i</sub>
 * are initialized randomly in the interval [0.05, 0.15], and constrained
 * to the interval [0.01, 0.2].</p>
 *
 * <p>This specific form of adaptive control parameters is based on 
 * the approach described in the following paper:<br>
 * Vincent A. Cicirello. <a href="https://www.cicirello.org/publications/cicirello2015bict.html">Genetic Algorithm 
 * Parameter Control: Application to Scheduling with Sequence-Dependent Setups</a>. In <i>Proceedings of the 9th International 
 * Conference on Bio-inspired Information and Communications Technologies</i>, pages 136-143. December 2015.</p>
 *
 * <p>The crossover, mutation, and selection operators are completely configurable
 * by passing instances of classes that implement the {@link CrossoverOperator},
 * {@link MutationOperator}, and {@link SelectionOperator} classes to one of the
 * constructors. The EA implemented by this class can also be configured to use
 * elitism, if desired, such that a specified number of the best solutions in the
 * population survive the generation unaltered.</p>
 *
 * @param <T> The type of object under optimization.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class AdaptiveEvolutionaryAlgorithm<T extends Copyable<T>> extends AbstractEvolutionaryAlgorithm<T> {
	
	/**
	 * Constructs and initializes the evolutionary algorithm. This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param crossover The crossover operator.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param eliteCount The number of elite population members. Pass 0 for no elitism. eliteCount must be less than n.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if eliteCount is greater than or equal to n.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public AdaptiveEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, CrossoverOperator<T> crossover, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, int eliteCount, ProgressTracker<T> tracker) {
		this(new EvolvableParametersPopulation.Double<T>(n, initializer, f, selection, tracker, eliteCount, 2), f.getProblem(), mutation, crossover);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm. This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param crossover The crossover operator.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param eliteCount The number of elite population members. Pass 0 for no elitism. eliteCount must be less than n.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if eliteCount is greater than or equal to n.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public AdaptiveEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, CrossoverOperator<T> crossover, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, int eliteCount, ProgressTracker<T> tracker) {
		this(new EvolvableParametersPopulation.Integer<T>(n, initializer, f, selection, tracker, eliteCount, 2), f.getProblem(), mutation, crossover);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm. This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param crossover The crossover operator.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public AdaptiveEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, CrossoverOperator<T> crossover, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(n, mutation, crossover, initializer, f, selection, 0, tracker);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm. This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param crossover The crossover operator.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param tracker A ProgressTracker.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, selection, or tracker are null.
	 */
	public AdaptiveEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, CrossoverOperator<T> crossover, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, ProgressTracker<T> tracker) {
		this(n, mutation, crossover, initializer, f, selection, 0, tracker);
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm. This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param crossover The crossover operator.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param eliteCount The number of elite population members. Pass 0 for no elitism. eliteCount must be less than n.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if eliteCount is greater than or equal to n.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public AdaptiveEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, CrossoverOperator<T> crossover, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection, int eliteCount) {
		this(n, mutation, crossover, initializer, f, selection, eliteCount, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm. This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param crossover The crossover operator.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 * @param eliteCount The number of elite population members. Pass 0 for no elitism. eliteCount must be less than n.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws IllegalArgumentException if eliteCount is greater than or equal to n.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public AdaptiveEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, CrossoverOperator<T> crossover, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection, int eliteCount) {
		this(n, mutation, crossover, initializer, f, selection, eliteCount, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm. This constructor supports fitness functions
	 * with fitnesses of type double, the {@link FitnessFunction.Double} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param crossover The crossover operator.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public AdaptiveEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, CrossoverOperator<T> crossover, Initializer<T> initializer, FitnessFunction.Double<T> f, SelectionOperator selection) {
		this(n, mutation, crossover, initializer, f, selection, new ProgressTracker<T>());
	}
	
	/**
	 * Constructs and initializes the evolutionary algorithm. This constructor supports fitness functions
	 * with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
	 *
	 * @param n The population size.
	 * @param mutation The mutation operator.
	 * @param crossover The crossover operator.
	 * @param initializer An initializer for generating random initial population members.
	 * @param f The fitness function.
	 * @param selection The selection operator.
	 *
	 * @throws IllegalArgumentException if n is less than 1.
	 * @throws NullPointerException if any of mutation, crossover, initializer, f, or selection are null.
	 */
	public AdaptiveEvolutionaryAlgorithm(int n, MutationOperator<T> mutation, CrossoverOperator<T> crossover, Initializer<T> initializer, FitnessFunction.Integer<T> f, SelectionOperator selection) {
		this(n, mutation, crossover, initializer, f, selection, new ProgressTracker<T>());
	}
	
	// Internal Constructors
	
	/*
	 * Internal helper constructor
	 */
	private AdaptiveEvolutionaryAlgorithm(Population<T> pop, Problem<T> problem, MutationOperator<T> mutation, CrossoverOperator<T> crossover) {
		super(
			pop, 
			problem,
			new AdaptiveGeneration<T>(mutation, crossover)
		);
	}
	
	/*
	 * Internal constructor for use by split method.
	 * package private so subclasses in same package can use it for initialization for their own split methods.
	 */
	AdaptiveEvolutionaryAlgorithm(AdaptiveEvolutionaryAlgorithm<T> other) {
		super(other);
	}
	
	@Override
	public AdaptiveEvolutionaryAlgorithm<T> split() {
		return new AdaptiveEvolutionaryAlgorithm<T>(this);
	}
}
