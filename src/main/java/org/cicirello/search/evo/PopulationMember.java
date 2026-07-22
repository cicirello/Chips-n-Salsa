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

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.search.operators.reals.GaussianMutation;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.util.Copyable;

/**
 * The PopulationMember class represents a single member of a population for use by implementations
 * of genetic algorithms and other evolutionary algorithms. It includes the candidate solution to
 * the problem that this member of the population represents as well as its fitness.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class PopulationMember<T extends Copyable<T>> {

  final T candidate;

  /*
   * for use only by the nested classes
   */
  private PopulationMember(T candidate) {
    this.candidate = candidate;
  }

  /*
   * for use only by the nested classes
   */
  private PopulationMember(PopulationMember<? extends Copyable<T>> other) {
    this.candidate = other.candidate.copy();
  }

  /**
   * Gets a reference to the candidate solution contained in this PopulationMember.
   *
   * @return the candidate solution
   */
  public final T candidate() {
    return candidate;
  }

  /**
   * Optional method that should be implemented by subclasses for populations that evolve control
   * parameters along with the solution. Gets a parameter value from a member of the population.
   *
   * @param indexParam Index into the parameters of that population member.
   * @throws UnsupportedOperationException by default. You must override to support adaptive control
   *     parameters.
   */
  public SingleReal getParameter(int indexParam) {
    throw new UnsupportedOperationException(
        "This population member doesn't encode control parameters.");
  }

  public void mutate() {
    // Do nothing in this base class as default behavior for
    // standard case when parameters are not evolvable.
  }

  /**
   * The PopulationMember class represents a single member of a population for use by
   * implementations of genetic algorithms and other evolutionary algorithms, specifically where
   * fitness values are of type double. It includes the candidate solution to the problem that this
   * member of the population represents as well as its fitness.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static class DoubleFitness<T extends Copyable<T>> extends PopulationMember<T>
      implements Copyable<DoubleFitness<T>> {

    private double fitness;

    /**
     * Construct a member of the population.
     *
     * @param candidate The candidate solution for the member of the population.
     * @param fitness The fitness of the candidate solution.
     */
    public DoubleFitness(T candidate, double fitness) {
      super(candidate);
      this.fitness = fitness;
    }

    private DoubleFitness(DoubleFitness<? extends Copyable<T>> other) {
      super(other);
      this.fitness = other.fitness;
    }

    @Override
    public DoubleFitness<T> copy() {
      return new DoubleFitness<T>(this);
    }

    /**
     * Gets the fitness of this population member as currently stored.
     *
     * @return the fitness of the population member
     */
    public final double fitness() {
      return fitness;
    }

    /**
     * Changes the fitness of the population member, such as if necessary after a mutation or
     * crossover.
     *
     * @param fitness The fitness of the candidate solution.
     */
    public final void setFitness(double fitness) {
      this.fitness = fitness;
    }
  }

  /**
   * The PopulationMember class represents a single member of a population for use by
   * implementations of genetic algorithms and other evolutionary algorithms, specifically where
   * fitness values are of type int. It includes the candidate solution to the problem that this
   * member of the population represents as well as its fitness.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static class IntegerFitness<T extends Copyable<T>> extends PopulationMember<T>
      implements Copyable<IntegerFitness<T>> {

    private int fitness;

    /**
     * Construct a member of the population.
     *
     * @param candidate The candidate solution for the member of the population.
     * @param fitness The fitness of the candidate solution.
     */
    public IntegerFitness(T candidate, int fitness) {
      super(candidate);
      this.fitness = fitness;
    }

    private IntegerFitness(IntegerFitness<? extends Copyable<T>> other) {
      super(other);
      this.fitness = other.fitness;
    }

    @Override
    public IntegerFitness<T> copy() {
      return new IntegerFitness<T>(this);
    }

    /**
     * Gets the fitness of this population member as currently stored.
     *
     * @return the fitness of the population member
     */
    public final int fitness() {
      return fitness;
    }

    /**
     * Changes the fitness of the population member, such as if necessary after a mutation or
     * crossover.
     *
     * @param fitness The fitness of the candidate solution.
     */
    public final void setFitness(int fitness) {
      this.fitness = fitness;
    }
  }

  /**
   * The PopulationMember.EvolvableDoubleFitness class represents a single member of a population
   * for use by implementations of genetic algorithms and other evolutionary algorithms, with
   * evolvable control parameters.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static final class EvolvableDoubleFitness<T extends Copyable<T>> extends DoubleFitness<T> {

    private final SingleReal[] params;
    private final GaussianMutation<SingleReal> mutator;
    private static final GaussianMutation<GaussianMutation> mutationMutator =
        GaussianMutation.createGaussianMutation(0.01, 0.01, 0.2);

    public EvolvableDoubleFitness(
        T candidate, double fitness, int numParams, EnhancedSplittableGenerator generator) {
      this(candidate, fitness, numParams, 0.1, 1.0, generator);
    }

    public EvolvableDoubleFitness(
        T candidate,
        double fitness,
        int numParams,
        double minRate,
        double maxRate,
        EnhancedSplittableGenerator generator) {
      super(candidate, fitness);
      params = new SingleReal[numParams];
      for (int i = 0; i < numParams; i++) {
        params[i] = new SingleReal(generator.nextDouble(minRate, maxRate));
      }
      mutator =
          GaussianMutation.createGaussianMutation(
              generator.nextDouble(0.05, 0.15), minRate, maxRate);
    }

    private EvolvableDoubleFitness(EvolvableDoubleFitness<? extends Copyable<T>> other) {
      super(other);
      params = new SingleReal[other.params.length];
      for (int i = 0; i < params.length; i++) {
        params[i] = other.params[i].copy();
      }
      mutator = other.mutator.copy();
    }

    @Override
    public EvolvableDoubleFitness<T> copy() {
      return new EvolvableDoubleFitness<T>(this);
    }

    @Override
    public SingleReal getParameter(int indexParam) {
      return params[indexParam];
    }

    /** Mutates the parameters. */
    @Override
    public void mutate() {
      for (SingleReal p : params) {
        mutator.mutate(p);
      }
      mutationMutator.mutate(mutator);
    }
  }

  /**
   * The PopulationMember.EvolvableIntegerFitness class represents a single member of a population
   * for use by implementations of genetic algorithms and other evolutionary algorithms, with
   * evolvable control parameters.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static final class EvolvableIntegerFitness<T extends Copyable<T>> extends IntegerFitness<T> {

    private final SingleReal[] params;
    private final GaussianMutation<SingleReal> mutator;
    private static final GaussianMutation<GaussianMutation> mutationMutator =
        GaussianMutation.createGaussianMutation(0.01, 0.01, 0.2);

    public EvolvableIntegerFitness(
        T candidate, int fitness, int numParams, EnhancedSplittableGenerator generator) {
      this(candidate, fitness, numParams, 0.1, 1.0, generator);
    }

    public EvolvableIntegerFitness(
        T candidate,
        int fitness,
        int numParams,
        double minRate,
        double maxRate,
        EnhancedSplittableGenerator generator) {
      super(candidate, fitness);
      params = new SingleReal[numParams];
      for (int i = 0; i < numParams; i++) {
        params[i] = new SingleReal(generator.nextDouble(minRate, maxRate));
      }
      mutator =
          GaussianMutation.createGaussianMutation(
              generator.nextDouble(0.05, 0.15), minRate, maxRate);
    }

    private EvolvableIntegerFitness(EvolvableIntegerFitness<? extends Copyable<T>> other) {
      super(other);
      params = new SingleReal[other.params.length];
      for (int i = 0; i < params.length; i++) {
        params[i] = other.params[i].copy();
      }
      mutator = other.mutator.copy();
    }

    @Override
    public EvolvableIntegerFitness<T> copy() {
      return new EvolvableIntegerFitness<T>(this);
    }

    @Override
    public SingleReal getParameter(int indexParam) {
      return params[indexParam];
    }

    /** Mutates the parameters. */
    @Override
    public void mutate() {
      for (SingleReal p : params) {
        mutator.mutate(p);
      }
      mutationMutator.mutate(mutator);
    }
  }
}
