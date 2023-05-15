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

import java.util.concurrent.ThreadLocalRandom;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.util.Copyable;

/**
 * An NaiveAlwaysMutateGeneration is the common cycle of: select, apply crossover to pairs of
 * parents based on C, apply mutation to each population member with rate M, replace. However, it is
 * the special case when M=1.0, such that every member of the population is mutated once in each
 * generation.
 *
 * @deprecated <b>IMPORTANT:</b> Use {@link AlwaysMutateGeneration} instead, which is highly
 *     optimized and significantly more efficient than this NaiveAlwaysMutateGeneration class. The
 *     NaiveAlwaysMutateGeneration class serves a very specific research purpose. This class is
 *     being introduced temporarily in support of research experiments. It is likewise being
 *     deprecated in the same release that introduces it. It will be removed in a future release
 *     with no further notice once it fulfills its research purpose. That removal will not be
 *     considered a breaking change since library users have been notified at class introduction,
 *     and should thus not depend upon it in the first place.
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
@Deprecated
final class NaiveAlwaysMutateGeneration<T extends Copyable<T>> implements Generation<T> {

  private final MutationOperator<T> mutation;
  private final CrossoverOperator<T> crossover;
  private final double C;

  NaiveAlwaysMutateGeneration(
      MutationOperator<T> mutation, CrossoverOperator<T> crossover, double crossoverRate) {
    C = crossoverRate;
    this.mutation = mutation;
    this.crossover = crossover;
  }

  NaiveAlwaysMutateGeneration(NaiveAlwaysMutateGeneration<T> other) {
    // Must be split
    mutation = other.mutation.split();
    crossover = other.crossover.split();

    // primitives
    C = other.C;
  }

  @Override
  public NaiveAlwaysMutateGeneration<T> split() {
    return new NaiveAlwaysMutateGeneration<T>(this);
  }

  @Override
  public int apply(Population<T> pop) {
    pop.select();
    // Since select() above randomizes ordering, just pair up parents with indexes: first and (first
    // + count).
    final int LAMBDA = pop.mutableSize();
    final int count = LAMBDA >> 1;
    int numEvals = 0;
    for (int first = 0; first < count; first++) {
      if (ThreadLocalRandom.current().nextDouble() < C) {
        int second = first + count;
        crossover.cross(pop.get(first), pop.get(second));
        pop.updateFitness(first);
        pop.updateFitness(second);
        numEvals += 2;
      }
    }
    // Mutate all of them
    for (int j = 0; j < LAMBDA; j++) {
      mutation.mutate(pop.get(j));
      pop.updateFitness(j);
    }
    pop.replace();
    return numEvals + LAMBDA;
  }
}
