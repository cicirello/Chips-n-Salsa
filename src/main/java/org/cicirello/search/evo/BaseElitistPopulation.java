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

import java.util.ArrayList;
import java.util.Arrays;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.operators.Initializer;
import org.cicirello.util.Copyable;

/**
 * The nested classes are for simple populations with double-valued and int-valued fitnesses. This
 * class and its subclasses are for populations with elitism.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class BaseElitistPopulation {

  private BaseElitistPopulation() {}

  /**
   * The Population for an evolutionary algorithm where fitness values are type double.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static final class DoubleFitness<T extends Copyable<T>> extends AbstractPopulation<T>
      implements PopulationFitnessVector.Double {

    private final Initializer<T> initializer;
    private final SelectionOperator selection;

    private final ArrayList<PopulationMember.DoubleFitness<T>> pop;
    private final ArrayList<PopulationMember.DoubleFitness<T>> nextPop;
    private final EliteSet.DoubleFitness<T> elite;
    private final boolean[] updated;

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
      super(tracker);
      if (n < 1) {
        throw new IllegalArgumentException("population size n must be positive");
      }
      if (numElite >= n) {
        throw new IllegalArgumentException(
            "number of elite population members must be less than population size");
      }
      if (numElite <= 0) {
        throw new IllegalArgumentException("number of elite population members must be positive");
      }
      if (initializer == null || f == null || selection == null || tracker == null) {
        throw new NullPointerException("passed a null object for a required parameter");
      }
      this.initializer = initializer;
      this.selection = selection;

      elite = new EliteSet.DoubleFitness<T>(numElite);

      this.f = f;
      MU = n;
      LAMBDA = n - numElite;

      pop = new ArrayList<PopulationMember.DoubleFitness<T>>(MU);
      nextPop = new ArrayList<PopulationMember.DoubleFitness<T>>(LAMBDA);
      selected = new int[LAMBDA];
      updated = new boolean[LAMBDA];
      bestFitness = java.lang.Double.NEGATIVE_INFINITY;
    }

    /*
     * private constructor for use by split.
     */
    private DoubleFitness(BaseElitistPopulation.DoubleFitness<T> other) {
      super(other);

      // these are threadsafe, so just copy references
      f = other.f;
      MU = other.MU;
      LAMBDA = other.LAMBDA;

      // split these: not threadsafe
      initializer = other.initializer.split();
      selection = other.selection.split();

      // initialize these fresh: not threadsafe or otherwise needs its own
      pop = new ArrayList<PopulationMember.DoubleFitness<T>>(MU);
      nextPop = new ArrayList<PopulationMember.DoubleFitness<T>>(LAMBDA);
      elite = new EliteSet.DoubleFitness<T>(MU - LAMBDA);
      selected = new int[LAMBDA];
      updated = new boolean[LAMBDA];
      bestFitness = java.lang.Double.NEGATIVE_INFINITY;
    }

    @Override
    public BaseElitistPopulation.DoubleFitness<T> split() {
      return new BaseElitistPopulation.DoubleFitness<T>(this);
    }

    @Override
    public T get(int i) {
      return nextPop.get(i).getCandidate();
    }

    @Override
    public double getFitness(int i) {
      return pop.get(i).getFitness();
    }

    @Override
    public int size() {
      // Use pop.size() rather than MU -- there is a weird, unlikely, rare edge case
      // associated with use of elitism, where pop.size() may be less than MU early in search.
      return pop.size();
    }

    @Override
    public int mutableSize() {
      return LAMBDA;
    }

    /**
     * Gets fitness of the most fit candidate solution encountered in any generation.
     *
     * @return the fitness of the most fit encountered in any generation
     */
    public double getFitnessOfMostFit() {
      return bestFitness;
    }

    @Override
    public void updateFitness(int i) {
      double fit = f.fitness(nextPop.get(i).getCandidate());
      nextPop.get(i).setFitness(fit);
      updated[i] = true;
      if (fit > bestFitness) {
        bestFitness = fit;
        setMostFit(f.getProblem().getSolutionCostPair(nextPop.get(i).getCandidate().copy()));
      }
    }

    @Override
    public void select() {
      selection.select(this, selected);
      for (int j : selected) {
        nextPop.add(pop.get(j).copy());
      }
    }

    @Override
    public void replace() {
      pop.clear();
      for (PopulationMember.DoubleFitness<T> e : nextPop) {
        pop.add(e);
      }

      for (PopulationMember.DoubleFitness<T> e : elite) {
        pop.add(e);
      }
      for (int i = 0; i < LAMBDA; i++) {
        if (updated[i]) {
          elite.offer(nextPop.get(i));
          updated[i] = false;
        }
      }

      nextPop.clear();
    }

    @Override
    public void initOperators(int generations) {
      selection.init(generations);
    }

    @Override
    public void init() {
      super.init();
      bestFitness = java.lang.Double.NEGATIVE_INFINITY;
      pop.clear();
      nextPop.clear();
      T newBest = null;
      for (int i = 0; i < MU; i++) {
        T c = initializer.createCandidateSolution();
        double fit = f.fitness(c);
        pop.add(new PopulationMember.DoubleFitness<T>(c, fit));
        if (fit > bestFitness) {
          bestFitness = fit;
          newBest = c;
        }
      }
      setMostFit(f.getProblem().getSolutionCostPair(newBest.copy()));

      elite.clear();
      elite.offerAll(pop);
      Arrays.fill(updated, false);
    }
  }

  /**
   * The Population for an evolutionary algorithm where fitness values are type int.
   *
   * @param <T> The type of object under optimization.
   * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
   *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
   */
  static final class IntegerFitness<T extends Copyable<T>> extends AbstractPopulation<T>
      implements PopulationFitnessVector.Integer {

    private final Initializer<T> initializer;
    private final SelectionOperator selection;

    private final ArrayList<PopulationMember.IntegerFitness<T>> pop;
    private final ArrayList<PopulationMember.IntegerFitness<T>> nextPop;
    private final EliteSet.IntegerFitness<T> elite;
    private final boolean[] updated;

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
      super(tracker);
      if (n < 1) {
        throw new IllegalArgumentException("population size n must be positive");
      }
      if (numElite >= n) {
        throw new IllegalArgumentException(
            "number of elite population members must be less than population size");
      }
      if (numElite <= 0) {
        throw new IllegalArgumentException("number of elite population members must be positive");
      }
      if (initializer == null || f == null || selection == null || tracker == null) {
        throw new NullPointerException("passed a null object for a required parameter");
      }
      this.initializer = initializer;
      this.selection = selection;

      elite = new EliteSet.IntegerFitness<T>(numElite);

      this.f = f;
      MU = n;
      LAMBDA = n - numElite;

      pop = new ArrayList<PopulationMember.IntegerFitness<T>>(MU);
      nextPop = new ArrayList<PopulationMember.IntegerFitness<T>>(LAMBDA);
      selected = new int[LAMBDA];
      updated = new boolean[LAMBDA];
      bestFitness = java.lang.Integer.MIN_VALUE;
    }

    /*
     * private constructor for use by split.
     */
    private IntegerFitness(BaseElitistPopulation.IntegerFitness<T> other) {
      super(other);

      // these are threadsafe, so just copy references
      f = other.f;
      MU = other.MU;
      LAMBDA = other.LAMBDA;

      // split these: not threadsafe
      initializer = other.initializer.split();
      selection = other.selection.split();

      // initialize these fresh: not threadsafe or otherwise needs its own
      pop = new ArrayList<PopulationMember.IntegerFitness<T>>(MU);
      nextPop = new ArrayList<PopulationMember.IntegerFitness<T>>(LAMBDA);
      elite = new EliteSet.IntegerFitness<T>(MU - LAMBDA);
      selected = new int[LAMBDA];
      updated = new boolean[LAMBDA];
      bestFitness = java.lang.Integer.MIN_VALUE;
    }

    @Override
    public BaseElitistPopulation.IntegerFitness<T> split() {
      return new BaseElitistPopulation.IntegerFitness<T>(this);
    }

    @Override
    public T get(int i) {
      return nextPop.get(i).getCandidate();
    }

    @Override
    public int getFitness(int i) {
      return pop.get(i).getFitness();
    }

    @Override
    public int size() {
      // Use pop.size() rather than MU -- there is a weird, unlikely, rare edge case
      // associated with use of elitism, where pop.size() may be less than MU early in search.
      return pop.size();
    }

    @Override
    public int mutableSize() {
      return LAMBDA;
    }

    /**
     * Gets fitness of the most fit candidate solution encountered in any generation.
     *
     * @return the fitness of the most fit encountered in any generation
     */
    public int getFitnessOfMostFit() {
      return bestFitness;
    }

    @Override
    public void updateFitness(int i) {
      int fit = f.fitness(nextPop.get(i).getCandidate());
      nextPop.get(i).setFitness(fit);
      updated[i] = true;
      if (fit > bestFitness) {
        bestFitness = fit;
        setMostFit(f.getProblem().getSolutionCostPair(nextPop.get(i).getCandidate().copy()));
      }
    }

    @Override
    public void select() {
      selection.select(this, selected);
      for (int j : selected) {
        nextPop.add(pop.get(j).copy());
      }
    }

    @Override
    public void replace() {
      pop.clear();
      for (PopulationMember.IntegerFitness<T> e : nextPop) {
        pop.add(e);
      }
      for (PopulationMember.IntegerFitness<T> e : elite) {
        pop.add(e);
      }
      for (int i = 0; i < LAMBDA; i++) {
        if (updated[i]) {
          elite.offer(nextPop.get(i));
          updated[i] = false;
        }
      }

      nextPop.clear();
    }

    @Override
    public void initOperators(int generations) {
      selection.init(generations);
    }

    @Override
    public void init() {
      super.init();
      bestFitness = java.lang.Integer.MIN_VALUE;
      pop.clear();
      nextPop.clear();
      T newBest = null;
      for (int i = 0; i < MU; i++) {
        T c = initializer.createCandidateSolution();
        int fit = f.fitness(c);
        pop.add(new PopulationMember.IntegerFitness<T>(c, fit));
        if (fit > bestFitness) {
          bestFitness = fit;
          newBest = c;
        }
      }
      setMostFit(f.getProblem().getSolutionCostPair(newBest.copy()));
      elite.clear();
      elite.offerAll(pop);
      Arrays.fill(updated, false);
    }
  }
}
