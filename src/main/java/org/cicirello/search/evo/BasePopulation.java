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

import java.util.ArrayList;
import java.util.Objects;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.util.Copyable;

/**
 * The nested classes are for simple populations with double-valued and int-valued fitnesses. This
 * class and its subclasses are for populations with elitism.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class BasePopulation {

  private BasePopulation() {}

  static int validateN(int n) {
    if (n < 1) {
      throw new IllegalArgumentException("population size n must be positive");
    }
    return n;
  }

  static int validateElite(int numElite, int n) {
    if (numElite >= n) {
      throw new IllegalArgumentException(
          "number of elite population members must be less than population size");
    }
    if (numElite < 0) {
      throw new IllegalArgumentException("number of elite population members must not be negative");
    }
    return numElite;
  }

  /**
   * The Population for an evolutionary algorithm where fitness values are type double.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static class DoubleFitness<T extends Copyable<T>> extends AbstractPopulation<T> {

    private final Initializer<T> initializer;
    private final SelectionOperator selection;

    private final PopulationVector.DoubleFitness<T> pop;
    private final PopulationVector.DoubleFitness<T> nextPop;
    private final ReplacementStrategy<T> replacement;
    private final ReplacementTracker r;
    private final PopulationMemberCreator<T> creator;

    private final FitnessFunction.Double<T> f;
    private final int MU;
    private final int LAMBDA;

    private final int[] selected;

    private double bestFitness;

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
        int numElite) {
      this(
          validateN(n),
          Objects.requireNonNull(initializer),
          Objects.requireNonNull(f),
          Objects.requireNonNull(selection),
          Objects.requireNonNull(tracker),
          (candidate, fitness) -> new PopulationMember.DoubleFitness<T>(candidate, fitness),
          validateElite(numElite, n),
          numElite > 0
              ? new GenerationalElitistReplacement<T>(numElite)
              : new GenerationalReplacement<T>());
    }

    /*
     * Validate params before calling to avoid partially instantiated object on exception
     */
    DoubleFitness(
        int n,
        Initializer<T> initializer,
        FitnessFunction.Double<T> f,
        SelectionOperator selection,
        ProgressTracker<T> tracker,
        PopulationMemberCreator<T> creator,
        int numElite,
        ReplacementStrategy<T> replacement) {
      super(tracker);
      this.initializer = initializer;
      this.selection = selection;
      this.replacement = replacement;

      this.f = f;
      MU = n;
      LAMBDA = n - numElite;

      pop = new PopulationVector.DoubleFitness<T>(MU);
      nextPop = new PopulationVector.DoubleFitness<T>(LAMBDA);
      r = new ReplacementTracker(MU, LAMBDA);
      selected = new int[LAMBDA];
      bestFitness = java.lang.Double.NEGATIVE_INFINITY;
      this.creator = creator;
    }

    /*
     * private constructor for use by split.
     */
    DoubleFitness(BasePopulation.DoubleFitness<T> other) {
      super(other);

      // these are threadsafe, so just copy references
      f = other.f;
      MU = other.MU;
      LAMBDA = other.LAMBDA;

      // split these: not threadsafe
      initializer = other.initializer.split();
      selection = other.selection.split();
      replacement = other.replacement.split();
      creator = other.creator.split();

      // initialize these fresh: not threadsafe or otherwise needs its own
      r = new ReplacementTracker(MU, LAMBDA);
      pop = new PopulationVector.DoubleFitness<T>(MU);
      nextPop = new PopulationVector.DoubleFitness<T>(LAMBDA);
      selected = new int[LAMBDA];
      bestFitness = java.lang.Double.NEGATIVE_INFINITY;
    }

    @Override
    public BasePopulation.DoubleFitness<T> split() {
      return new BasePopulation.DoubleFitness<T>(this);
    }

    @Override
    public final T get(int i) {
      return nextPop.candidate(i);
    }

    @Override
    public final SingleReal getParameter(int indexPop, int indexParam) {
      return nextPop.get(indexPop).getParameter(indexParam);
    }

    @Override
    public final int size() {
      // Use pop.size() rather than MU -- there is a weird, unlikely, rare edge case
      // associated with use of elitism, where pop.size() may be less than MU early in search.
      return pop.size();
    }

    @Override
    public final int mutableSize() {
      return LAMBDA;
    }

    /**
     * Gets fitness of the most fit candidate solution encountered in any generation.
     *
     * @return the fitness of the most fit encountered in any generation
     */
    public final double getFitnessOfMostFit() {
      return bestFitness;
    }

    @Override
    public final void updateFitness(int i) {
      double fit = f.fitness(nextPop.candidate(i));
      nextPop.get(i).setFitness(fit);
      if (fit > bestFitness) {
        bestFitness = fit;
        setMostFit(f.getProblem().getSolutionCostPair(nextPop.candidate(i).copy()));
      }
    }

    @Override
    public final void select() {
      selection.select(pop, selected);
      for (int j : selected) {
        nextPop.add(pop.get(j).copy());
      }
    }

    @Override
    public final void replace() {
      replacement.replace(pop, nextPop, r, MU);
      if (r.includesParents()) {
        ArrayList<PopulationMember.DoubleFitness<T>> keep =
            new ArrayList<PopulationMember.DoubleFitness<T>>();
        final int[] counts = r.parentCounts();
        for (int i = 0; i < MU; i++) {
          for (int j = 0; j < counts[i]; j++) {
            keep.add(pop.get(i).copy());
          }
        }
        pop.clear();
        for (PopulationMember.DoubleFitness<T> e : keep) {
          pop.add(e);
        }
        r.clearParentCounts();
      } else {
        pop.clear();
      }
      final int[] counts = r.childCounts();
      for (int i = 0; i < LAMBDA; i++) {
        if (counts[i] >= 1) {
          for (int j = 1; j < counts[i]; j++) {
            PopulationMember.DoubleFitness<T> chosen = nextPop.get(i).copy();
            chosen.mutate();
            pop.add(chosen);
          }
          PopulationMember.DoubleFitness<T> chosen = nextPop.get(i);
          chosen.mutate();
          pop.add(chosen);
        }
      }
      r.clearChildCounts();
      nextPop.clear();
    }

    @Override
    public final void initOperators(int generations) {
      selection.init(generations);
      replacement.init(generations);
    }

    @Override
    public final void init() {
      super.init();
      bestFitness = java.lang.Double.NEGATIVE_INFINITY;
      pop.clear();
      nextPop.clear();
      T newBest = null;
      for (int i = 0; i < MU; i++) {
        T c = initializer.createCandidateSolution();
        double fit = f.fitness(c);
        pop.add(creator.create(c, fit));
        if (fit > bestFitness) {
          bestFitness = fit;
          newBest = c;
        }
      }
      setMostFit(f.getProblem().getSolutionCostPair(newBest.copy()));
    }

    /*
     * Removed from Population interface since not really needed, but tests were using it,
     * so keeping it to avoid breaking test cases.
     */
    final double fitness(int i) {
      return pop.fitness(i);
    }

    @FunctionalInterface
    public static interface PopulationMemberCreator<T extends Copyable<T>> {
      PopulationMember.DoubleFitness<T> create(T candidate, double fitness);

      default PopulationMemberCreator<T> split() {
        return this;
      }
    }
  }

  /**
   * The Population for an evolutionary algorithm where fitness values are type int.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static class IntegerFitness<T extends Copyable<T>> extends AbstractPopulation<T> {

    private final Initializer<T> initializer;
    private final SelectionOperator selection;

    private final PopulationVector.IntegerFitness<T> pop;
    private final PopulationVector.IntegerFitness<T> nextPop;
    private final ReplacementStrategy<T> replacement;
    private final ReplacementTracker r;
    private final PopulationMemberCreator<T> creator;

    private final FitnessFunction.Integer<T> f;
    private final int MU;
    private final int LAMBDA;

    private final int[] selected;

    private int bestFitness;

    /**
     * Constructs the Population.
     *
     * @param n The size of the population, which must be positive.
     * @param initializer An initializer to supply the population with a means of generating random
     *     initial population members.
     * @param f The fitness function.
     * @param selection The selection operator.
     * @param tracker A ProgressTracker.
     * @param numElite The number of elite population members.
     */
    public IntegerFitness(
        int n,
        Initializer<T> initializer,
        FitnessFunction.Integer<T> f,
        SelectionOperator selection,
        ProgressTracker<T> tracker,
        int numElite) {
      this(
          validateN(n),
          Objects.requireNonNull(initializer),
          Objects.requireNonNull(f),
          Objects.requireNonNull(selection),
          Objects.requireNonNull(tracker),
          (candidate, fitness) -> new PopulationMember.IntegerFitness<T>(candidate, fitness),
          validateElite(numElite, n),
          numElite > 0
              ? new GenerationalElitistReplacement<T>(numElite)
              : new GenerationalReplacement<T>());
    }

    /*
     * Validate params before calling to avoid partially instantiated object on exception
     */
    IntegerFitness(
        int n,
        Initializer<T> initializer,
        FitnessFunction.Integer<T> f,
        SelectionOperator selection,
        ProgressTracker<T> tracker,
        PopulationMemberCreator<T> creator,
        int numElite,
        ReplacementStrategy<T> replacement) {
      super(tracker);
      this.initializer = initializer;
      this.selection = selection;
      this.replacement = replacement;

      this.f = f;
      MU = n;
      LAMBDA = n - numElite;

      pop = new PopulationVector.IntegerFitness<T>(MU);
      nextPop = new PopulationVector.IntegerFitness<T>(LAMBDA);
      r = new ReplacementTracker(MU, LAMBDA);
      selected = new int[LAMBDA];
      bestFitness = java.lang.Integer.MIN_VALUE;
      this.creator = creator;
    }

    /*
     * private constructor for use by split.
     */
    IntegerFitness(BasePopulation.IntegerFitness<T> other) {
      super(other);

      // these are threadsafe, so just copy references
      f = other.f;
      MU = other.MU;
      LAMBDA = other.LAMBDA;

      // split these: not threadsafe
      initializer = other.initializer.split();
      selection = other.selection.split();
      replacement = other.replacement.split();
      creator = other.creator.split();

      // initialize these fresh: not threadsafe or otherwise needs its own
      r = new ReplacementTracker(MU, LAMBDA);
      pop = new PopulationVector.IntegerFitness<T>(MU);
      nextPop = new PopulationVector.IntegerFitness<T>(LAMBDA);
      selected = new int[LAMBDA];
      bestFitness = java.lang.Integer.MIN_VALUE;
    }

    @Override
    public BasePopulation.IntegerFitness<T> split() {
      return new BasePopulation.IntegerFitness<T>(this);
    }

    @Override
    public final T get(int i) {
      return nextPop.candidate(i);
    }

    @Override
    public final SingleReal getParameter(int indexPop, int indexParam) {
      return nextPop.get(indexPop).getParameter(indexParam);
    }

    @Override
    public final int size() {
      // Use pop.size() rather than MU -- there is a weird, unlikely, rare edge case
      // associated with use of elitism, where pop.size() may be less than MU early in search.
      return pop.size();
    }

    @Override
    public final int mutableSize() {
      return LAMBDA;
    }

    /**
     * Gets fitness of the most fit candidate solution encountered in any generation.
     *
     * @return the fitness of the most fit encountered in any generation
     */
    public final int getFitnessOfMostFit() {
      return bestFitness;
    }

    @Override
    public final void updateFitness(int i) {
      int fit = f.fitness(nextPop.candidate(i));
      nextPop.get(i).setFitness(fit);
      if (fit > bestFitness) {
        bestFitness = fit;
        setMostFit(f.getProblem().getSolutionCostPair(nextPop.candidate(i).copy()));
      }
    }

    @Override
    public final void select() {
      selection.select(pop, selected);
      for (int j : selected) {
        nextPop.add(pop.get(j).copy());
      }
    }

    @Override
    public final void replace() {
      replacement.replace(pop, nextPop, r, MU);
      if (r.includesParents()) {
        ArrayList<PopulationMember.IntegerFitness<T>> keep =
            new ArrayList<PopulationMember.IntegerFitness<T>>();
        final int[] counts = r.parentCounts();
        for (int i = 0; i < MU; i++) {
          for (int j = 0; j < counts[i]; j++) {
            keep.add(pop.get(i).copy());
          }
        }
        pop.clear();
        for (PopulationMember.IntegerFitness<T> e : keep) {
          pop.add(e);
        }
        r.clearParentCounts();
      } else {
        pop.clear();
      }
      final int[] counts = r.childCounts();
      for (int i = 0; i < LAMBDA; i++) {
        if (counts[i] >= 1) {
          for (int j = 1; j < counts[i]; j++) {
            PopulationMember.IntegerFitness<T> chosen = nextPop.get(i).copy();
            chosen.mutate();
            pop.add(chosen);
          }
          PopulationMember.IntegerFitness<T> chosen = nextPop.get(i);
          chosen.mutate();
          pop.add(chosen);
        }
      }
      r.clearChildCounts();
      nextPop.clear();
    }

    @Override
    public final void initOperators(int generations) {
      selection.init(generations);
      replacement.init(generations);
    }

    @Override
    public final void init() {
      super.init();
      bestFitness = java.lang.Integer.MIN_VALUE;
      pop.clear();
      nextPop.clear();
      T newBest = null;
      for (int i = 0; i < MU; i++) {
        T c = initializer.createCandidateSolution();
        int fit = f.fitness(c);
        pop.add(creator.create(c, fit));
        if (fit > bestFitness) {
          bestFitness = fit;
          newBest = c;
        }
      }
      setMostFit(f.getProblem().getSolutionCostPair(newBest.copy()));
    }

    /*
     * Removed from Population interface since not really needed, but tests were using it,
     * so keeping it to avoid breaking test cases.
     */
    final int fitness(int i) {
      return pop.fitness(i);
    }

    @FunctionalInterface
    public static interface PopulationMemberCreator<T extends Copyable<T>> {
      PopulationMember.IntegerFitness<T> create(T candidate, int fitness);

      default PopulationMemberCreator<T> split() {
        return this;
      }
    }
  }
}
