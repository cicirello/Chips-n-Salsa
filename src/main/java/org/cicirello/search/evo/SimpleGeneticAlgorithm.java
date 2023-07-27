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
import org.cicirello.search.operators.bits.SinglePointCrossover;
import org.cicirello.search.representations.BitVector;

/**
 * This class is an implementation of the simple genetic algorithm (Simple GA) with the common bit
 * vector representation of solutions to optimization problems, and the generational model where
 * children replace their parents each generation. It uses the usual bit flip mutation, where each
 * bit of each member of the population is mutated (flipped) with some probability, known as the
 * mutation rate, each generation. The crossover operator is single-point crossover (see the {@link
 * SinglePointCrossover} class), and the selection operator is fitness proportional (see the {@link
 * FitnessProportionalSelection} class).
 *
 * <p>The library also includes other classes for evolutionary algorithms that may be more relevant
 * depending upon your use-case. For example, see the {@link GeneticAlgorithm} class for greater
 * flexibility in configuring the crossover and selection operators, the {@link
 * MutationOnlyGeneticAlgorithm} class if all you want to use is mutation and no crossover, and the
 * {@link GenerationalEvolutionaryAlgorithm} class if you want to optimize something other than
 * BitVectors or if you want even greater flexibility in configuring your evolutionary search.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class SimpleGeneticAlgorithm extends GeneticAlgorithm {

  /**
   * Initializes a simple genetic algorithm with a generational model where children replace the
   * parents, using the standard bit flip mutation, single-point crossover (the {@link
   * SinglePointCrossover} class), and fitness-proportional selection (the {@link
   * FitnessProportionalSelection} class). This constructor supports fitness functions with
   * fitnesses of type double, the {@link FitnessFunction.Double} interface.
   *
   * @param n The population size.
   * @param bitLength The length of each bit vector.
   * @param f The fitness function.
   * @param mutationRate The per-bit probability of flipping a bit. Each bit of each member of the
   *     population is flipped with this probability, and the decisions to flip bits are
   *     independent.
   * @param crossoverRate The probability that a pair of parents undergo crossover.
   * @param tracker A ProgressTracker.
   * @throws IllegalArgumentException if n is less than 1.
   * @throws IllegalArgumentException if mutationRate &le; 0 or if mutationRate &ge; 1.
   * @throws IllegalArgumentException if crossoverRate is less than 0.
   * @throws IllegalArgumentException if bitLength is negative.
   * @throws NullPointerException if any of f, or tracker are null.
   */
  public SimpleGeneticAlgorithm(
      int n,
      int bitLength,
      FitnessFunction.Double<BitVector> f,
      double mutationRate,
      double crossoverRate,
      ProgressTracker<BitVector> tracker) {
    super(
        n,
        bitLength,
        f,
        mutationRate,
        new SinglePointCrossover(),
        crossoverRate,
        new FitnessProportionalSelection(),
        tracker);
  }

  /**
   * Initializes a simple genetic algorithm with a generational model where children replace the
   * parents, using the standard bit flip mutation, single-point crossover (the {@link
   * SinglePointCrossover} class), and fitness-proportional selection (the {@link
   * FitnessProportionalSelection} class). This constructor supports fitness functions with
   * fitnesses of type int, the {@link FitnessFunction.Integer} interface.
   *
   * @param n The population size.
   * @param bitLength The length of each bit vector.
   * @param f The fitness function.
   * @param mutationRate The per-bit probability of flipping a bit. Each bit of each member of the
   *     population is flipped with this probability, and the decisions to flip bits are
   *     independent.
   * @param crossoverRate The probability that a pair of parents undergo crossover.
   * @param tracker A ProgressTracker.
   * @throws IllegalArgumentException if n is less than 1.
   * @throws IllegalArgumentException if mutationRate &le; 0 or if mutationRate &ge; 1.
   * @throws IllegalArgumentException if crossoverRate is less than 0.
   * @throws IllegalArgumentException if bitLength is negative.
   * @throws NullPointerException if any of f, or tracker are null.
   */
  public SimpleGeneticAlgorithm(
      int n,
      int bitLength,
      FitnessFunction.Integer<BitVector> f,
      double mutationRate,
      double crossoverRate,
      ProgressTracker<BitVector> tracker) {
    super(
        n,
        bitLength,
        f,
        mutationRate,
        new SinglePointCrossover(),
        crossoverRate,
        new FitnessProportionalSelection(),
        tracker);
  }

  /**
   * Initializes a simple genetic algorithm with a generational model where children replace the
   * parents, using the standard bit flip mutation, single-point crossover (the {@link
   * SinglePointCrossover} class), and fitness-proportional selection (the {@link
   * FitnessProportionalSelection} class). This constructor supports fitness functions with
   * fitnesses of type double, the {@link FitnessFunction.Double} interface.
   *
   * @param n The population size.
   * @param bitLength The length of each bit vector.
   * @param f The fitness function.
   * @param mutationRate The per-bit probability of flipping a bit. Each bit of each member of the
   *     population is flipped with this probability, and the decisions to flip bits are
   *     independent.
   * @param crossoverRate The probability that a pair of parents undergo crossover.
   * @throws IllegalArgumentException if n is less than 1.
   * @throws IllegalArgumentException if mutationRate &le; 0 or if mutationRate &ge; 1.
   * @throws IllegalArgumentException if crossoverRate is less than 0.
   * @throws IllegalArgumentException if bitLength is negative.
   * @throws NullPointerException if f is null.
   */
  public SimpleGeneticAlgorithm(
      int n,
      int bitLength,
      FitnessFunction.Double<BitVector> f,
      double mutationRate,
      double crossoverRate) {
    super(
        n,
        bitLength,
        f,
        mutationRate,
        new SinglePointCrossover(),
        crossoverRate,
        new FitnessProportionalSelection());
  }

  /**
   * Initializes a simple genetic algorithm with a generational model where children replace the
   * parents, using the standard bit flip mutation, single-point crossover (the {@link
   * SinglePointCrossover} class), and fitness-proportional selection (the {@link
   * FitnessProportionalSelection} class). This constructor supports fitness functions with
   * fitnesses of type int, the {@link FitnessFunction.Integer} interface.
   *
   * @param n The population size.
   * @param bitLength The length of each bit vector.
   * @param f The fitness function.
   * @param mutationRate The per-bit probability of flipping a bit. Each bit of each member of the
   *     population is flipped with this probability, and the decisions to flip bits are
   *     independent.
   * @param crossoverRate The probability that a pair of parents undergo crossover.
   * @throws IllegalArgumentException if n is less than 1.
   * @throws IllegalArgumentException if mutationRate &le; 0 or if mutationRate &ge; 1.
   * @throws IllegalArgumentException if crossoverRate is less than 0.
   * @throws IllegalArgumentException if bitLength is negative.
   * @throws NullPointerException if f is null.
   */
  public SimpleGeneticAlgorithm(
      int n,
      int bitLength,
      FitnessFunction.Integer<BitVector> f,
      double mutationRate,
      double crossoverRate) {
    super(
        n,
        bitLength,
        f,
        mutationRate,
        new SinglePointCrossover(),
        crossoverRate,
        new FitnessProportionalSelection());
  }

  /*
   * Internal constructor for use by split method
   */
  private SimpleGeneticAlgorithm(SimpleGeneticAlgorithm other) {
    super(other);
    // Just call super constructor to perform split() logic. This
    // subclass doesn't currently maintain any additional state.
    // Only reason for overriding split() method, and thus providing this
    // constructor is to ensure runtime type of split instance is same,
    // although strictly speaking it would still function correctly otherwise.
  }

  @Override
  public SimpleGeneticAlgorithm split() {
    return new SimpleGeneticAlgorithm(this);
  }
}
