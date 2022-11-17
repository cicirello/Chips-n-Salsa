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

import java.util.concurrent.ThreadLocalRandom;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.util.Copyable;

/**
 * An AdaptiveGeneration is uses control parameters encoded with the population member to determine
 * whether to crossover or mutate.
 *
 * <p>Based on approach of:
 *
 * <p>Vincent A. Cicirello. <a
 * href="https://www.cicirello.org/publications/cicirello2015bict.html">Genetic Algorithm Parameter
 * Control: Application to Scheduling with Sequence-Dependent Setups</a>. In <i>Proceedings of the
 * 9th International Conference on Bio-inspired Information and Communications Technologies</i>,
 * pages 136-143. December 2015.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class AdaptiveGeneration<T extends Copyable<T>> implements Generation<T> {

  private final MutationOperator<T> mutation;
  private final CrossoverOperator<T> crossover;

  AdaptiveGeneration(MutationOperator<T> mutation, CrossoverOperator<T> crossover) {
    if (mutation == null) {
      throw new NullPointerException("mutation must be non-null");
    }
    if (crossover == null) {
      throw new NullPointerException("crossover must be non-null");
    }
    this.mutation = mutation;
    this.crossover = crossover;
  }

  AdaptiveGeneration(AdaptiveGeneration<T> other) {
    // Must be split
    mutation = other.mutation.split();
    crossover = other.crossover.split();
  }

  @Override
  public AdaptiveGeneration<T> split() {
    return new AdaptiveGeneration<T>(this);
  }

  @Override
  public int apply(Population<T> pop) {
    pop.select();
    // Since select() above randomizes ordering, consecutive pairs are random
    // and can be used as parents.
    final int LAMBDA = pop.mutableSize();
    int count = 0;
    ThreadLocalRandom r = ThreadLocalRandom.current();
    for (int second = 1; second < LAMBDA; second += 2) {
      int first = second - 1;
      if (r.nextDouble() < pop.getParameter(first, 0).get()) {
        crossover.cross(pop.get(first), pop.get(second));
        pop.updateFitness(first);
        pop.updateFitness(second);
        count += 2;
      }
    }
    for (int j = 0; j < LAMBDA; j++) {
      if (r.nextDouble() < pop.getParameter(j, 1).get()) {
        mutation.mutate(pop.get(j));
        pop.updateFitness(j);
        count++;
      }
    }
    pop.replace();
    return count;
  }
}
