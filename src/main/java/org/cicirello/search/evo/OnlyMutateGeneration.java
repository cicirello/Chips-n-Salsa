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
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.util.Copyable;

/**
 * An OnlyMutateGeneration is for mutation-only EAs.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class OnlyMutateGeneration<T extends Copyable<T>> implements Generation<T> {

  private final MutationOperator<T> mutation;
  private final double M;

  OnlyMutateGeneration(MutationOperator<T> mutation, double mutationRate) {
    if (mutation == null) {
      throw new NullPointerException("mutation must be non-null");
    }
    if (mutationRate < 0.0) {
      throw new IllegalArgumentException("mutationRate must not be negative");
    }
    this.mutation = mutation;
    // no need to check if > 1 because would use OnlyAlwaysMutateGeneration in that case
    M = mutationRate;
  }

  OnlyMutateGeneration(OnlyMutateGeneration<T> other) {
    // Must be split
    mutation = other.mutation.split();

    // primitives
    M = other.M;
  }

  @Override
  public OnlyMutateGeneration<T> split() {
    return new OnlyMutateGeneration<T>(this);
  }

  @Override
  public int apply(Population<T> pop) {
    pop.select();
    final int count = RandomVariates.nextBinomial(pop.mutableSize(), M);
    // Since select() randomizes ordering, just use a binomial
    // to get count of how many to mutate and mutate the first count individuals.
    // Although if M is 1.0 just mutate them all without computing the binomial.
    for (int j = 0; j < count; j++) {
      mutation.mutate(pop.get(j));
      pop.updateFitness(j);
    }
    pop.replace();
    return count;
  }
}
