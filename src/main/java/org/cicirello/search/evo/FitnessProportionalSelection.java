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

/**
 * This class implements fitness proportional selection, sometimes referred to as weighted roulette
 * wheel, for evolutionary algorithms. In fitness proportional selection, a member of the population
 * is chosen randomly with probability proportional to its fitness relative to the total fitness of
 * the population. For example, if the fitness of population member i is f<sub>i</sub>, then the
 * probability of selecting population member i is: f<sub>i</sub> / &sum;<sub>j</sub> f<sub>j</sub>,
 * for j &isin; { 1, 2, ..., N }, where N is the population size. To select M members of the
 * population, M independent random decisions are executed in this way, thus requiring generating M
 * random numbers of type double.
 *
 * <p><b>This selection operator requires positive fitness values. Behavior is undefined if any
 * fitness values are less than or equal to 0.</b> If your fitness values may be negative, you can
 * use {@link FitnessShifter}, which transforms fitness values such that minimum fitness equals 1.
 *
 * <p>The runtime to select M population members from a population of size N is O(N + M lg N).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class FitnessProportionalSelection extends AbstractRouletteWheelSelection {

  /** Construct a fitness proportional selection operator. */
  public FitnessProportionalSelection() {}

  /* package private for use by subclasses in same package */
  FitnessProportionalSelection(FitnessProportionalSelection other) {
    super(other);
  }

  @Override
  public FitnessProportionalSelection split() {
    return new FitnessProportionalSelection(this);
  }
}
