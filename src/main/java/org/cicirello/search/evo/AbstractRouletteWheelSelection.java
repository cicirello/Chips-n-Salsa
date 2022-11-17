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

/**
 * This abstract class serves as a base class for selection operators that select population members
 * randomly with a roulette wheel style process, where each selected member is through a process
 * involving a spin of a hypothetical roulette wheel.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractRouletteWheelSelection extends AbstractWeightedSelection {

  /** Construct a roulette wheel selection operator */
  public AbstractRouletteWheelSelection() {}

  @Override
  final void selectAll(double[] normalizedWeights, int[] selected) {
    for (int i = 0; i < selected.length; i++) {
      selected[i] =
          selectOne(
              normalizedWeights,
              0,
              normalizedWeights.length - 1,
              ThreadLocalRandom.current().nextDouble());
    }
  }
}
