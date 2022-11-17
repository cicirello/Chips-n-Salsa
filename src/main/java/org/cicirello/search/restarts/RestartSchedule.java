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

package org.cicirello.search.restarts;

import org.cicirello.search.concurrent.Splittable;

/**
 * Multistart metaheuristics involve periodically restarting the metaheuristic from a new initial
 * starting solution (often random). Although it is common for such restarts to have a common run
 * length, there do exist other restart schedules that vary the run length from one run to the next
 * in some way. This interface defines the functionality of a restart schedule.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public interface RestartSchedule extends Splittable<RestartSchedule> {

  /**
   * Gets the next run length in the restart schedule's sequence of run lengths.
   *
   * @return the length for the next run of a multistart metaheuristic
   */
  int nextRunLength();

  /**
   * Resets the restart schedule to its initial conditions, such that the next call to {@link
   * #nextRunLength} will return the initial run length of the schedule.
   */
  void reset();
}
