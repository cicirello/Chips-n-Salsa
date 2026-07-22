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

import java.util.Objects;
import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.Initializer;
import org.cicirello.util.Copyable;

/**
 * The nested classes are for populations with double-valued and int-valued fitnesses for use by EAs
 * with parameters that evolve during the search. This class and its subclasses are for populations
 * with elitism.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class EvolvableParametersPopulation {

  private EvolvableParametersPopulation() {}

  /**
   * The Population for an evolutionary algorithm where fitness values are type double, and such
   * that parameters evolve during the search.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static final class DoubleFitness<T extends Copyable<T>> extends BasePopulation.DoubleFitness<T> {

    /**
     * Constructs the Population.
     *
     * @param n The size of the population, which must be positive.
     * @param initializer An initializer to supply the population with a means of generating random
     *     initial population members.
     * @param f The fitness function.
     * @param selection The selection operator.
     * @param tracker A ProgressTracker.
     * @param numElite the number of elite members of the population.
     */
    public DoubleFitness(
        int n,
        Initializer<T> initializer,
        FitnessFunction.Double<T> f,
        SelectionOperator selection,
        ProgressTracker<T> tracker,
        int numElite,
        int numParams) {
      super(
          BasePopulation.validateN(n),
          Objects.requireNonNull(initializer),
          Objects.requireNonNull(f),
          Objects.requireNonNull(selection),
          Objects.requireNonNull(tracker),
          new EvolvableParametersPopulationMemberCreator<T>(numParams),
          BasePopulation.validateElite(numElite, n));
    }

    /*
     * private constructor for use by split.
     */
    private DoubleFitness(EvolvableParametersPopulation.DoubleFitness<T> other) {
      super(other);
    }

    @Override
    public EvolvableParametersPopulation.DoubleFitness<T> split() {
      return new EvolvableParametersPopulation.DoubleFitness<T>(this);
    }

    private static class EvolvableParametersPopulationMemberCreator<T extends Copyable<T>>
        implements PopulationMemberCreator<T> {

      private final int numParams;
      private final EnhancedSplittableGenerator generator;

      EvolvableParametersPopulationMemberCreator(int numParams) {
        this.numParams = numParams;
        generator = RandomnessFactory.createEnhancedSplittableGenerator();
      }

      private EvolvableParametersPopulationMemberCreator(
          EvolvableParametersPopulationMemberCreator other) {
        numParams = other.numParams;
        generator = other.generator.split();
      }

      @Override
      public PopulationMember.DoubleFitness<T> create(T candidate, double fitness) {
        return new PopulationMember.EvolvableDoubleFitness<T>(
            candidate, fitness, numParams, generator);
      }

      @Override
      public PopulationMemberCreator<T> split() {
        return new EvolvableParametersPopulationMemberCreator<T>(this);
      }
    }
  }

  /**
   * The Population for an evolutionary algorithm where fitness values are type int, and such that
   * parameters evolve during the search.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static final class IntegerFitness<T extends Copyable<T>>
      extends BasePopulation.IntegerFitness<T> {

    /**
     * Constructs the Population.
     *
     * @param n The size of the population, which must be positive.
     * @param initializer An initializer to supply the population with a means of generating random
     *     initial population members.
     * @param f The fitness function.
     * @param selection The selection operator.
     * @param tracker A ProgressTracker.
     * @param numElite the number of elite members of the population.
     */
    public IntegerFitness(
        int n,
        Initializer<T> initializer,
        FitnessFunction.Integer<T> f,
        SelectionOperator selection,
        ProgressTracker<T> tracker,
        int numElite,
        int numParams) {
      super(
          BasePopulation.validateN(n),
          Objects.requireNonNull(initializer),
          Objects.requireNonNull(f),
          Objects.requireNonNull(selection),
          Objects.requireNonNull(tracker),
          new EvolvableParametersPopulationMemberCreator<T>(numParams),
          BasePopulation.validateElite(numElite, n));
    }

    /*
     * private constructor for use by split.
     */
    private IntegerFitness(EvolvableParametersPopulation.IntegerFitness<T> other) {
      super(other);
    }

    @Override
    public EvolvableParametersPopulation.IntegerFitness<T> split() {
      return new EvolvableParametersPopulation.IntegerFitness<T>(this);
    }

    private static class EvolvableParametersPopulationMemberCreator<T extends Copyable<T>>
        implements PopulationMemberCreator<T> {

      private final int numParams;
      private final EnhancedSplittableGenerator generator;

      EvolvableParametersPopulationMemberCreator(int numParams) {
        this.numParams = numParams;
        generator = RandomnessFactory.createEnhancedSplittableGenerator();
      }

      private EvolvableParametersPopulationMemberCreator(
          EvolvableParametersPopulationMemberCreator other) {
        numParams = other.numParams;
        generator = other.generator.split();
      }

      @Override
      public PopulationMember.IntegerFitness<T> create(T candidate, int fitness) {
        return new PopulationMember.EvolvableIntegerFitness<T>(
            candidate, fitness, numParams, generator);
      }

      @Override
      public PopulationMemberCreator<T> split() {
        return new EvolvableParametersPopulationMemberCreator<T>(this);
      }
    }
  }
}
