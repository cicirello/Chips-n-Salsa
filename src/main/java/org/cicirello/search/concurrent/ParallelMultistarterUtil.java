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

package org.cicirello.search.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.restarts.Multistarter;
import org.cicirello.search.restarts.ReoptimizableMultistarter;
import org.cicirello.search.restarts.RestartSchedule;
import org.cicirello.util.Copyable;

/**
 * Package-private utility class for common helper methods for the various parallel multistarters.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class ParallelMultistarterUtil {

  /*
   * Strictly a utility class, so default constructor is private.
   */
  private ParallelMultistarterUtil() {}

  /**
   * Creates a list of Multistarters.
   *
   * @param multistartSearch A Multistarter configured with the metaheuristic and restart schedule.
   *     Each of the threads will be an identical copy of this Multistarter.
   * @param numThreads The number of threads to use.
   * @throws IllegalArgumentException if numThreads is less than 1.
   */
  static <T extends Copyable<T>> Collection<Multistarter<T>> toMultistarters(
      Multistarter<T> multistartSearch, int numThreads) {
    if (numThreads < 1) throw new IllegalArgumentException("must be at least 1 thread");
    ArrayList<Multistarter<T>> restarters = new ArrayList<Multistarter<T>>();
    restarters.add(multistartSearch);
    for (int i = 1; i < numThreads; i++) {
      restarters.add(multistartSearch.split());
    }
    return restarters;
  }

  /**
   * Creates a list of Multistarters.
   *
   * @param search A Metaheuristic
   * @param schedules A collection of RestartSchedules
   * @return a list of Multistarters, one for each restart schedule, all with identical and
   *     independent copies of search
   * @throws IllegalArgumentException if the collection of schedules is empty
   */
  static <T extends Copyable<T>> Collection<Multistarter<T>> toMultistarters(
      Metaheuristic<T> search, Collection<? extends RestartSchedule> schedules) {
    if (schedules.size() < 1)
      throw new IllegalArgumentException("Must pass at least one schedule.");
    ArrayList<Multistarter<T>> restarters = new ArrayList<Multistarter<T>>(schedules.size());
    boolean addedFirst = false;
    for (RestartSchedule r : schedules) {
      if (addedFirst) restarters.add(new Multistarter<T>(search.split(), r));
      else {
        restarters.add(new Multistarter<T>(search, r));
        addedFirst = true;
      }
    }
    return restarters;
  }

  /**
   * Creates a list of Multistarters.
   *
   * @param searches A collection of Metaheuristics
   * @param schedules A collection of RestartSchedules
   * @return a list of Multistarters, such that multistarter i gets metaheuristic i and restart
   *     schedule i.
   * @throws IllegalArgumentException if searches.size() is not equal to schedules.size()
   * @throws IllegalArgumentException if not all metaheuristics solve the same problem
   * @throws IllegalArgumentException if the metaheuristics don't all share a single ProgressTracker
   */
  static <T extends Copyable<T>> Collection<Multistarter<T>> toMultistarters(
      Collection<? extends Metaheuristic<T>> searches,
      Collection<? extends RestartSchedule> schedules) {
    if (searches.size() != schedules.size()) {
      throw new IllegalArgumentException(
          "number of searches and number of schedules must be the same");
    }
    ArrayList<Multistarter<T>> restarters = new ArrayList<Multistarter<T>>(searches.size());
    Iterator<? extends RestartSchedule> rs = schedules.iterator();
    ProgressTracker<T> t = null;
    Problem<T> problem = null;
    for (Metaheuristic<T> s : searches) {
      if (problem == null) {
        problem = s.getProblem();
      } else if (s.getProblem() != problem) {
        throw new IllegalArgumentException(
            "All Metaheuristics in searches must solve the same problem.");
      }
      if (t == null) {
        t = s.getProgressTracker();
      } else if (s.getProgressTracker() != t) {
        throw new IllegalArgumentException(
            "All Metaheuristics in searches must share a single ProgressTracker.");
      }
      restarters.add(new Multistarter<T>(s, rs.next()));
    }
    return restarters;
  }

  /**
   * Creates a list of ReoptimizableMultistarters.
   *
   * @param search A ReoptimizableMetaheuristic
   * @param schedules A collection of RestartSchedules
   * @return a list of ReoptimizableMultistarters, one for each restart schedule, all with identical
   *     and independent copies of search
   * @throws IllegalArgumentException if the collection of schedules is empty
   */
  static <T extends Copyable<T>>
      Collection<ReoptimizableMultistarter<T>> toReoptimizableMultistarters(
          ReoptimizableMetaheuristic<T> search, Collection<? extends RestartSchedule> schedules) {
    if (schedules.size() < 1)
      throw new IllegalArgumentException("Must pass at least one schedule.");
    ArrayList<ReoptimizableMultistarter<T>> restarters =
        new ArrayList<ReoptimizableMultistarter<T>>(schedules.size());
    boolean addedFirst = false;
    for (RestartSchedule r : schedules) {
      if (addedFirst) restarters.add(new ReoptimizableMultistarter<T>(search.split(), r));
      else {
        restarters.add(new ReoptimizableMultistarter<T>(search, r));
        addedFirst = true;
      }
    }
    return restarters;
  }

  /**
   * Creates a list of ReoptimizableMultistarters.
   *
   * @param searches A collection of ReoptimizableMetaheuristic
   * @param schedules A collection of RestartSchedules
   * @return a list of ReoptimizableMultistarters, such that multistarter i gets metaheuristic i and
   *     restart schedule i.
   * @throws IllegalArgumentException if searches.size() is not equal to schedules.size()
   * @throws IllegalArgumentException if not all metaheuristics solve the same problem
   * @throws IllegalArgumentException if the metaheuristics don't all share a single ProgressTracker
   */
  static <T extends Copyable<T>>
      Collection<ReoptimizableMultistarter<T>> toReoptimizableMultistarters(
          Collection<? extends ReoptimizableMetaheuristic<T>> searches,
          Collection<? extends RestartSchedule> schedules) {
    if (searches.size() != schedules.size()) {
      throw new IllegalArgumentException(
          "number of searches and number of schedules must be the same");
    }
    ArrayList<ReoptimizableMultistarter<T>> restarters =
        new ArrayList<ReoptimizableMultistarter<T>>(searches.size());
    Iterator<? extends RestartSchedule> rs = schedules.iterator();
    ProgressTracker<T> t = null;
    Problem<T> problem = null;
    for (ReoptimizableMetaheuristic<T> s : searches) {
      if (problem == null) {
        problem = s.getProblem();
      } else if (s.getProblem() != problem) {
        throw new IllegalArgumentException(
            "All Metaheuristics in searches must solve the same problem.");
      }
      if (t == null) {
        t = s.getProgressTracker();
      } else if (s.getProgressTracker() != t) {
        throw new IllegalArgumentException(
            "All Metaheuristics in searches must share a single ProgressTracker.");
      }
      restarters.add(new ReoptimizableMultistarter<T>(s, rs.next()));
    }
    return restarters;
  }

  static <T extends Copyable<T>> void verifyMultistarterCollection(
      Collection<? extends Multistarter<T>> multistarters) {
    ProgressTracker<T> t = null;
    Problem<T> problem = null;
    for (Multistarter<T> m : multistarters) {
      if (problem == null) {
        problem = m.getProblem();
      } else if (m.getProblem() != problem) {
        throw new IllegalArgumentException(
            "All Multistarters in searches must solve the same problem.");
      }
      if (t == null) {
        t = m.getProgressTracker();
      } else if (m.getProgressTracker() != t) {
        throw new IllegalArgumentException(
            "All Multistarters must share a single ProgressTracker.");
      }
    }
  }
}
