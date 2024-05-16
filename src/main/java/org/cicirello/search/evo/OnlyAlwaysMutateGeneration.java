/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2024 Vincent A. Cicirello
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

import org.cicirello.search.internal.ReferenceValidator;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.util.Copyable;

/**
 * An OnlyAlwaysMutateGeneration is for mutation-only EAs that apply one mutation to each member of
 * population every generation.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class OnlyAlwaysMutateGeneration<T extends Copyable<T>> implements Generation<T> {

  private final MutationOperator<T> mutation;

  OnlyAlwaysMutateGeneration(MutationOperator<T> mutation) {
    ReferenceValidator.nullCheck(mutation);
    this.mutation = mutation;
  }

  OnlyAlwaysMutateGeneration(OnlyAlwaysMutateGeneration<T> other) {
    // Must be split
    mutation = other.mutation.split();
  }

  @Override
  public OnlyAlwaysMutateGeneration<T> split() {
    return new OnlyAlwaysMutateGeneration<T>(this);
  }

  @Override
  public int apply(Population<T> pop) {
    pop.select();
    final int count = pop.mutableSize();
    for (int j = 0; j < count; j++) {
      mutation.mutate(pop.get(j));
      pop.updateFitness(j);
    }
    pop.replace();
    return count;
  }
}
