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

import java.util.HashSet;
import org.cicirello.ds.IntBinaryHeap;
import org.cicirello.ds.IntBinaryHeapDouble;

/**
 * GenerationalElitistReplacement keeps the <i>k</i> best-fit distinct members of the current
 * population without application of evolutionary operators, and replaces the other <i>(N-k)</i>
 * members of the population. This is similar to the classic replacement strategy of the simple
 * genetic algorithm and other generational models, but with a few elite members of the population.
 *
 * <p>If the representation of the population members doesn't implement the {@link Object#hashCode}
 * and {@link Object#equals} methods, then this replacement strategy will still partially function
 * with the exception of failing to maintain distinct elite members. All of the directly supported
 * representations within the library implement those methods, so this caveat is primarily for any
 * custom representations you may use.
 *
 * @param <T> the representation of population members
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class GenerationalElitistReplacement<T> implements ReplacementStrategy<T> {

  private final int elite;

  /** Constructs the replacement strategy. The default behavior is 1 elite population member. */
  public GenerationalElitistReplacement() {
    this(1);
  }

  /**
   * Constructs the replacement strategy.
   *
   * @param elite the number of elite population members, which must be at least 1. This
   *     implementation sets elite to 1 if you pass a value less than 1.
   */
  public GenerationalElitistReplacement(int elite) {
    this.elite = Math.max(1, elite);
  }

  @Override
  public void replace(
      PopulationCandidates.IntegerFitness<T> parentPopulation,
      PopulationCandidates.IntegerFitness<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize) {

    if (elite == 1) {
      replacements.chooseFromParentPopulation(findSingleElite(parentPopulation), 1);
      internalReplace(replacements, targetPopulationSize - 1, childPopulation.size());
    } else {
      final int[] eliteParents = findElite(parentPopulation, Math.min(elite, targetPopulationSize));
      for (int parentIndex : eliteParents) {
        replacements.chooseFromParentPopulation(parentIndex, 1);
      }
      internalReplace(
          replacements, targetPopulationSize - eliteParents.length, childPopulation.size());
    }
  }

  @Override
  public void replace(
      PopulationCandidates.DoubleFitness<T> parentPopulation,
      PopulationCandidates.DoubleFitness<T> childPopulation,
      Replacements replacements,
      int targetPopulationSize) {

    if (elite == 1) {
      replacements.chooseFromParentPopulation(findSingleElite(parentPopulation), 1);
      internalReplace(replacements, targetPopulationSize - 1, childPopulation.size());
    } else {
      final int[] eliteParents = findElite(parentPopulation, Math.min(elite, targetPopulationSize));
      for (int parentIndex : eliteParents) {
        replacements.chooseFromParentPopulation(parentIndex, 1);
      }
      internalReplace(
          replacements, targetPopulationSize - eliteParents.length, childPopulation.size());
    }
  }

  @Override
  public GenerationalElitistReplacement<T> split() {
    // This operator doesn't maintain any mutable state across calls, so it is safe to
    // share across threads. Thus, just return this.
    return this;
  }

  private int findSingleElite(PopulationCandidates.DoubleFitness<T> parentPopulation) {
    int mostFitIndex = 0;
    double bestFitness = parentPopulation.fitness(0);
    final int mu = parentPopulation.size();
    for (int i = 1; i < mu; i++) {
      final double fitness = parentPopulation.fitness(i);
      if (fitness > bestFitness) {
        mostFitIndex = i;
        bestFitness = fitness;
      }
    }
    return mostFitIndex;
  }

  private int findSingleElite(PopulationCandidates.IntegerFitness<T> parentPopulation) {
    int mostFitIndex = 0;
    int bestFitness = parentPopulation.fitness(0);
    final int mu = parentPopulation.size();
    for (int i = 1; i < mu; i++) {
      final int fitness = parentPopulation.fitness(i);
      if (fitness > bestFitness) {
        mostFitIndex = i;
        bestFitness = fitness;
      }
    }
    return mostFitIndex;
  }

  private int[] findElite(
      PopulationCandidates.DoubleFitness<T> parentPopulation, final int targetEliteCount) {
    HashSet<T> eliteSet = new HashSet<T>(targetEliteCount);
    final int mu = parentPopulation.size();
    IntBinaryHeapDouble pq = IntBinaryHeapDouble.createMinHeap(mu);
    int i = 0;
    for (; i < mu && eliteSet.size() < targetEliteCount; i++) {
      if (eliteSet.add(parentPopulation.candidate(i))) {
        pq.offer(i, parentPopulation.fitness(i));
      }
    }
    for (; i < mu; i++) {
      final double fitness = parentPopulation.fitness(i);
      final T c = parentPopulation.candidate(i);
      if (fitness > pq.peekPriority() && !eliteSet.contains(c)) {
        eliteSet.remove(parentPopulation.candidate(pq.pollThenOffer(i, fitness)));
        eliteSet.add(c);
      }
    }
    return pq.toArray();
  }

  private int[] findElite(
      PopulationCandidates.IntegerFitness<T> parentPopulation, final int targetEliteCount) {
    HashSet<T> eliteSet = new HashSet<T>(targetEliteCount);
    final int mu = parentPopulation.size();
    IntBinaryHeap pq = IntBinaryHeap.createMinHeap(mu);
    int i = 0;
    for (; i < mu && eliteSet.size() < targetEliteCount; i++) {
      if (eliteSet.add(parentPopulation.candidate(i))) {
        pq.offer(i, parentPopulation.fitness(i));
      }
    }
    for (; i < mu; i++) {
      final int fitness = parentPopulation.fitness(i);
      final T c = parentPopulation.candidate(i);
      if (fitness > pq.peekPriority() && !eliteSet.contains(c)) {
        eliteSet.remove(parentPopulation.candidate(pq.pollThenOffer(i, fitness)));
        eliteSet.add(c);
      }
    }
    return pq.toArray();
  }

  private void internalReplace(Replacements replacements, final int remaining, final int lambda) {
    final int minCopies = remaining / lambda;
    final int extraCount = remaining % lambda;
    int i = 0;
    for (; i < extraCount; i++) {
      replacements.chooseFromChildPopulation(i, minCopies + 1);
    }
    for (; i < lambda; i++) {
      replacements.chooseFromChildPopulation(i, minCopies);
    }
  }
}
