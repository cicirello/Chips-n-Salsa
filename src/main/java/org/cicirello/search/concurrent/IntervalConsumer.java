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

package org.cicirello.search.concurrent;

import org.cicirello.search.SolutionCostPair;
import org.cicirello.util.Copyable;

/**
 * A callback interface enabling logging, tracking, etc the progress of a timed parallel search.
 *
 * @param <T> The type of object being optimized.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
@FunctionalInterface
public interface IntervalConsumer<T extends Copyable<T>> {

  /**
   * Fired at each time interval during a parallel search.
   *
   * @param interval The current time interval index.
   * @param bestAtInterval The best solution found up to this interval.
   */
  void accept(int interval, SolutionCostPair<T> bestAtInterval);
}
