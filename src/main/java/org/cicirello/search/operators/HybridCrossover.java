/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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

package org.cicirello.search.operators;

import java.util.ArrayList;
import java.util.Collection;
import org.cicirello.math.rand.RandomIndexer;

/**
 * A HybridCrossover enables using multiple crossover operators for the evolutionary algorithm, such
 * that each time the {@link #cross} method is called, a randomly chosen crossover operator is
 * applied to the candidate solution. The random choice of crossover operator is approximately
 * uniform from among the available crossover operators.
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class HybridCrossover<T> implements CrossoverOperator<T> {

  private final ArrayList<CrossoverOperator<T>> crossoverOps;

  /**
   * Constructs a HybridCrossover from a Collection of CrossoverOperator.
   *
   * @param crossoverOps A Collection of CrossoverOperator.
   * @throws IllegalArgumentException if crossoverOps doesn't contain any CrossoverOperators.
   */
  public HybridCrossover(Collection<? extends CrossoverOperator<T>> crossoverOps) {
    if (crossoverOps.size() == 0)
      throw new IllegalArgumentException("Must pass at least 1 CrossoverOperator.");
    this.crossoverOps = new ArrayList<CrossoverOperator<T>>(crossoverOps.size());
    for (CrossoverOperator<T> op : crossoverOps) {
      this.crossoverOps.add(op);
    }
  }

  /*
   * private constructor to support split method
   */
  private HybridCrossover(HybridCrossover<T> other) {
    crossoverOps = new ArrayList<CrossoverOperator<T>>(other.crossoverOps.size());
    for (CrossoverOperator<T> op : other.crossoverOps) {
      crossoverOps.add(op.split());
    }
  }

  @Override
  public void cross(T c1, T c2) {
    crossoverOps.get(RandomIndexer.nextBiasedInt(crossoverOps.size())).cross(c1, c2);
  }

  @Override
  public HybridCrossover<T> split() {
    return new HybridCrossover<T>(this);
  }
}
