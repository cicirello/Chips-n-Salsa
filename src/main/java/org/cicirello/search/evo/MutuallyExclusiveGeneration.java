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

import org.cicirello.math.rand.RandomVariates;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.util.Copyable;

/**
 * In a MutuallyExclusiveGeneration, each member of the population is subject to crossover or
 * mutation (or possibly neither), but never both in a single generation.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class MutuallyExclusiveGeneration<T extends Copyable<T>> implements Generation<T> {

  private final MutationOperator<T> mutation;
  private final double M_PRIME;
  private final CrossoverOperator<T> crossover;
  private final double C;

  MutuallyExclusiveGeneration(
      MutationOperator<T> mutation,
      double mutationRate,
      CrossoverOperator<T> crossover,
      double crossoverRate) {
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
    if (mutationRate + crossoverRate > 1.0) {
      throw new IllegalArgumentException(
          "mutually exclusive operators requires mutationRate + crossoverRate <= 1.0");
    }
    C = crossoverRate;
    M_PRIME = C < 1.0 ? mutationRate / (1.0 - C) : 0.0;
    this.mutation = mutation;
    this.crossover = crossover;
  }

  MutuallyExclusiveGeneration(MutuallyExclusiveGeneration<T> other) {
    // Must be split
    mutation = other.mutation.split();
    crossover = other.crossover.split();

    // primitives
    M_PRIME = other.M_PRIME;
    C = other.C;
  }

  @Override
  public MutuallyExclusiveGeneration<T> split() {
    return new MutuallyExclusiveGeneration<T>(this);
  }

  @Override
  public int apply(Population<T> pop) {
    pop.select();
    // Since select() above randomizes ordering, just use a binomial
    // to get count of number of pairs of parents to cross and cross the first
    // count pairs of parents. Pair up parents with indexes: first and (first + count).
    final int LAMBDA = pop.mutableSize();
    final int count = RandomVariates.nextBinomial(LAMBDA >> 1, C);
    for (int first = 0; first < count; first++) {
      int second = first + count;
      crossover.cross(pop.get(first), pop.get(second));
      pop.updateFitness(first);
      pop.updateFitness(second);
    }
    final int crossed = count << 1;
    int mutateCount = 0;
    if (crossed < LAMBDA && M_PRIME > 0.0) {
      mutateCount =
          M_PRIME < 1.0 ? RandomVariates.nextBinomial(LAMBDA - crossed, M_PRIME) : LAMBDA - crossed;
      for (int j = crossed + mutateCount - 1; j >= crossed; j--) {
        mutation.mutate(pop.get(j));
        pop.updateFitness(j);
      }
    }
    pop.replace();
    return crossed + mutateCount;
  }
}
