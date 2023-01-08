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

package org.cicirello.search.concurrent;

import java.util.concurrent.Callable;
import java.util.function.Function;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.util.Copyable;

/**
 * Internal package class for threaded calls to optimize.
 *
 * @param <T> The type of object being optimized.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class CallableOptimizerFactory<T extends Copyable<T>>
    implements Function<Metaheuristic<T>, Callable<SolutionCostPair<T>>> {

  private final int runLength;

  CallableOptimizerFactory(int runLength) {
    this.runLength = runLength;
  }

  @Override
  public Callable<SolutionCostPair<T>> apply(Metaheuristic<T> m) {
    return new CallOptimize(m);
  }

  private final class CallOptimize implements Callable<SolutionCostPair<T>> {

    private final Metaheuristic<T> m;

    CallOptimize(Metaheuristic<T> m) {
      this.m = m;
    }

    @Override
    public SolutionCostPair<T> call() {
      return m.optimize(runLength);
    }
  }
}
