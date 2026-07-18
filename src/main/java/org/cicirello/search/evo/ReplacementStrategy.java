/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2026 Vincent A. Cicirello
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

import org.cicirello.search.concurrent.Splittable;

/**
 * Implement this interface to provide a replacement strategy for use by genetic algorithms and
 * other forms of evolutionary computation.
 *
 * @param <T> the representation of population members
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface ReplacementStrategy<T> extends Splittable<ReplacementStrategy<T>> {

  /**
   * Chooses the members of the population of the next generation from among those currently in the
   * population and the pool of candidates formed from crossover and mutation. Implementations must
   * not attempt to further manipulate the state of the individuals in these sets. Doing so can lead
   * to undefined behavior.
   *
   * @param parentPopulation the current population
   * @param childPopulation the members of the population chosen by the selection operator which
   *     have already gone through crossover, mutation, both, or neither (based on crossover rates,
   *     mutation rates, etc)
   * @param replacements records which members of the parent and child populations serve as
   *     replacements
   * @param targetPopulationSize the size of the target population for the next generation. In most
   *     cases this will likely be the same as parentPopulation.size(). But, do not make that
   *     assumption in your implementation to allow less common EA structures where population size
   *     may vary
   */
  void replace(
      PopulationCandidates.IntegerFitness<T> parentPopulation,
      PopulationCandidates.IntegerFitness<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize);

  /**
   * Chooses the members of the population of the next generation from among those currently in the
   * population and the pool of candidates formed from crossover and mutation. Implementations must
   * not attempt to further manipulate the state of the individuals in these sets. Doing so can lead
   * to undefined behavior.
   *
   * @param parentPopulation the current population
   * @param childPopulation the members of the population chosen by the selection operator which
   *     have already gone through crossover, mutation, both, or neither (based on crossover rates,
   *     mutation rates, etc)
   * @param replacements records which members of the parent and child populations serve as
   *     replacements
   * @param targetPopulationSize the size of the target population for the next generation. In most
   *     cases this will likely be the same as parentPopulation.size(). But, do not make that
   *     assumption in your implementation to allow less common EA structures where population size
   *     may vary
   */
  void replace(
      PopulationCandidates.DoubleFitness<T> parentPopulation,
      PopulationCandidates.DoubleFitness<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize);

  /**
   * Perform any initialization necessary for the replacement strategy at the start of the run of
   * the evolutionary algorithm. This method is called by the evolutionary algorithm at the start of
   * a run (i.e., whenever an EA's optimize or reoptimize methods are called. The default
   * implementation of this method does nothing, which is appropriate for most replacement
   * strategies since the behavior of most standard replacement strategies doesn't change during
   * runs. However, if you imlement a custom replacement strategy that adapts in some way (e.g.,
   * some parameter should change each generation), then the init method enables reinitializing such
   * parameters at the start of runs.
   *
   * @param generations The number of generations for the run of the evolutionary algorithm about to
   *     commence.
   */
  default void init(int generations) {}

  /**
   * Interface for {@link ReplacementStrategy} implementations to specify which members of the
   * parent and child populations serve as replacements into the next generation.
   *
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  interface Replacements {

    /**
     * Add the i-th member of the parent population to the set of replacements. You may add the same
     * member of the population multiple times by calling this method multiple times.
     *
     * @param i index of the population member
     */
    void addFromParentPopulation(int i);

    /**
     * Add the i-th member of the parent population to the set of replacements. You may add the same
     * member of the population multiple times by calling this method multiple times.
     *
     * @param i index of the population member
     */
    void addFromChildPopulation(int i);
  }
}
