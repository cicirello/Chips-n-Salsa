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

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.util.Copyable;

/**
 * An abstract base class for the functionality common to all forms of population within the library
 * to avoid redundancy.
 *
 * @param <T> The type of object under optimization.
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractPopulation<T extends Copyable<T>> implements Population<T> {

  private ProgressTracker<T> tracker;
  private SolutionCostPair<T> mostFit;

  /** package-private for use by subclasses in this package only. */
  AbstractPopulation(ProgressTracker<T> tracker) {
    this.tracker = tracker;
    mostFit = null;
  }

  /** package-private for use by subclasses in this package only. */
  AbstractPopulation(AbstractPopulation<T> other) {
    // These must be shared, so just copy reference.
    tracker = other.tracker;

    // Must have its own.
    mostFit = null;
  }

  @Override
  public void init() {
    mostFit = null;
  }

  @Override
  public final SolutionCostPair<T> getMostFit() {
    return mostFit;
  }

  @Override
  public boolean evolutionIsPaused() {
    return tracker.didFindBest() || tracker.isStopped();
  }

  @Override
  public final ProgressTracker<T> getProgressTracker() {
    return tracker;
  }

  @Override
  public final void setProgressTracker(ProgressTracker<T> tracker) {
    this.tracker = tracker;
  }

  final void setMostFit(SolutionCostPair<T> mostFit) {
    this.mostFit = mostFit;
    tracker.update(mostFit);
  }

  @Override
  public abstract AbstractPopulation<T> split();
}
