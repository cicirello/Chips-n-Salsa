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

package org.cicirello.search.evo;

import java.util.Arrays;

/**
 * Maintains which individuals survive to next generationfrom combination of current population and
 * the derivatives (e.g., children and mutants, etc).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class ReplacementTracker implements ReplacementStrategy.Replacements {

  private final int[] parents;
  private final int[] children;
  private boolean includesParents;

  ReplacementTracker(int MU, int LAMBDA) {
    parents = new int[MU];
    children = new int[LAMBDA];
  }

  @Override
  public void addFromParentPopulation(int i) {
    parents[i]++;
    includesParents = true;
  }

  @Override
  public void addFromChildPopulation(int i) {
    children[i]++;
  }

  boolean includesParents() {
    return includesParents;
  }

  int[] parentCounts() {
    return parents;
  }

  int[] childCounts() {
    return children;
  }

  void clearParentCounts() {
    Arrays.fill(parents, 0);
    includesParents = false;
  }

  void clearChildCounts() {
    Arrays.fill(children, 0);
  }
}
