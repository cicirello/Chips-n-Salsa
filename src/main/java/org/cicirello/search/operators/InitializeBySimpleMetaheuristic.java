/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
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

import org.cicirello.search.SimpleMetaheuristic;
import org.cicirello.util.Copyable;

/**
 * This class implements the {@link Initializer} interface to provide metaheuristics and other
 * search algorithms with a way to generate initial candidate solutions to a problem, that are
 * themselves generated via a metaheuristic. For example, you can use this class to generate initial
 * solutions for simulated annealing, etc by first running a hill climber to climb to a local optima
 * first before running the simulated annealer. The InitializeBySimpleMetaheuristic class can
 * generate initial candidate solutions using an object of any class that implements the {@link
 * SimpleMetaheuristic} interface.
 *
 * @param <T> The type of object used to represent candidate solutions to the problem.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class InitializeBySimpleMetaheuristic<T extends Copyable<T>>
    implements Initializer<T> {

  private final SimpleMetaheuristic<T> meta;

  /**
   * Constructs an Initializer that creates initial candidate solutions for metaheuristics by
   * running another simpler metaheuristic, such as a hill climber, from a random initial solution,
   * climbing to a local optima.
   *
   * @param meta The hill climber or other simple metaheuristic.
   */
  public InitializeBySimpleMetaheuristic(SimpleMetaheuristic<T> meta) {
    this.meta = meta;
  }

  @Override
  public T createCandidateSolution() {
    return meta.optimize().getSolution();
  }

  @Override
  public InitializeBySimpleMetaheuristic<T> split() {
    return new InitializeBySimpleMetaheuristic<T>(meta.split());
  }
}
