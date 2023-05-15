/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;

/**
 * This class implements an evolutionary algorithm with a generational model, such as is commonly
 * used in genetic algorithms, where a population of children are formed by applying genetic
 * operators to members of the parent population, and where the children replace the parents in the
 * next generation. It uses the typical generational model using both crossover and mutation,
 * controlled by a crossover rate and a mutation rate, such that each child may be the result of
 * crossover alone, mutation alone, a combination of both crossover and mutation, or a simple copy
 * of a parent.
 *
 * <p>The crossover, mutation, and selection operators are completely configurable by passing
 * instances of classes that implement the {@link CrossoverOperator}, {@link MutationOperator}, and
 * {@link SelectionOperator} classes to one of the constructors.
 *
 * @deprecated <b>IMPORTANT:</b> This class is being introduced temporarily in support of research
 *     experiments. It is likewise being deprecated in the same release that introduces it. It will
 *     be removed in a future release with no further notice once it fulfills its research purpose.
 *     That removal will not be considered a breaking change since library users have been notified
 *     at class introduction, and should thus not depend upon it in the first place. It uses the
 *     typical standard implementation of a generation of an EA. The existing class {@link
 *     GenerationalEvolutionaryAlgorithm} instead uses a non-standard, but logically and
 *     statistically equivalent, highly optimized implementation of a generation of an EA. Thus, you
 *     should use the {@link GenerationalEvolutionaryAlgorithm} class instead of this one.
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
@Deprecated
public class NaiveGenerationalEvolutionaryAlgorithm<T extends Copyable<T>>
    extends AbstractEvolutionaryAlgorithm<T> {

  /**
   * Constructs and initializes the evolutionary algorithm. This constructor supports fitness
   * functions with fitnesses of type double, the {@link FitnessFunction.Double} interface.
   *
   * @param n The population size.
   * @param mutation The mutation operator.
   * @param mutationRate The probability that a member of the population is mutated once during a
   *     generation. Note that this is not a per-bit rate since this class is generalized to
   *     evolution of any {@link Copyable} object type. For {@link
   *     org.cicirello.search.representations.BitVector} optimization and traditional genetic
   *     algorithm interpretation of mutation rate, configure your mutation operator with the
   *     per-bit mutation rate, and then pass 1.0 for this parameter.
   * @param crossover The crossover operator.
   * @param crossoverRate The probability that a pair of parents undergo crossover.
   * @param initializer An initializer for generating random initial population members.
   * @param f The fitness function.
   * @param selection The selection operator.
   * @throws IllegalArgumentException if n is less than 1.
   * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
   * @throws NullPointerException if any of mutation, crossover, initializer, f, selection are null.
   */
  public NaiveGenerationalEvolutionaryAlgorithm(
      int n,
      MutationOperator<T> mutation,
      double mutationRate,
      CrossoverOperator<T> crossover,
      double crossoverRate,
      Initializer<T> initializer,
      FitnessFunction.Double<T> f,
      SelectionOperator selection) {
    this(
        new BasePopulation.DoubleFitness<T>(n, initializer, f, selection, new ProgressTracker<T>()),
        f.getProblem(),
        mutation,
        mutationRate,
        crossover,
        crossoverRate);
  }

  /**
   * Constructs and initializes the evolutionary algorithm. This constructor supports fitness
   * functions with fitnesses of type int, the {@link FitnessFunction.Integer} interface.
   *
   * @param n The population size.
   * @param mutation The mutation operator.
   * @param mutationRate The probability that a member of the population is mutated once during a
   *     generation. Note that this is not a per-bit rate since this class is generalized to
   *     evolution of any {@link Copyable} object type. For {@link
   *     org.cicirello.search.representations.BitVector} optimization and traditional genetic
   *     algorithm interpretation of mutation rate, configure your mutation operator with the
   *     per-bit mutation rate, and then pass 1.0 for this parameter.
   * @param crossover The crossover operator.
   * @param crossoverRate The probability that a pair of parents undergo crossover.
   * @param initializer An initializer for generating random initial population members.
   * @param f The fitness function.
   * @param selection The selection operator.
   * @throws IllegalArgumentException if n is less than 1.
   * @throws IllegalArgumentException if either mutationRate or crossoverRate are less than 0.
   * @throws NullPointerException if any of mutation, crossover, initializer, f, selection are null.
   */
  public NaiveGenerationalEvolutionaryAlgorithm(
      int n,
      MutationOperator<T> mutation,
      double mutationRate,
      CrossoverOperator<T> crossover,
      double crossoverRate,
      Initializer<T> initializer,
      FitnessFunction.Integer<T> f,
      SelectionOperator selection) {
    this(
        new BasePopulation.IntegerFitness<T>(
            n, initializer, f, selection, new ProgressTracker<T>()),
        f.getProblem(),
        mutation,
        mutationRate,
        crossover,
        crossoverRate);
  }

  // Internal Constructors

  /*
   * Internal helper constructor for standard EAs with full generation (both crossover and mutation).
   */
  private NaiveGenerationalEvolutionaryAlgorithm(
      Population<T> pop,
      Problem<T> problem,
      MutationOperator<T> mutation,
      double mutationRate,
      CrossoverOperator<T> crossover,
      double crossoverRate) {
    super(
        pop,
        problem,
        mutationRate >= 1.0
            ? new NaiveAlwaysMutateGeneration<T>(mutation, crossover, crossoverRate)
            : new NaiveSimpleGeneration<T>(mutation, mutationRate, crossover, crossoverRate));
  }

  /*
   * Internal constructor for use by split method.
   * package private so subclasses in same package can use it for initialization for their own split methods.
   */
  NaiveGenerationalEvolutionaryAlgorithm(NaiveGenerationalEvolutionaryAlgorithm<T> other) {
    super(other);
  }

  @Override
  public NaiveGenerationalEvolutionaryAlgorithm<T> split() {
    return new NaiveGenerationalEvolutionaryAlgorithm<T>(this);
  }
}
