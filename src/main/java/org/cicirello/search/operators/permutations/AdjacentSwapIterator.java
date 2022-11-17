/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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

package org.cicirello.search.operators.permutations;

import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.MutationIterator;

/**
 * Internal (package-private) class implementing an iterator over all adjacent swaps.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 3.22.2021
 */
final class AdjacentSwapIterator implements MutationIterator {

  private boolean rolled;
  private boolean hasMore;
  private final Permutation p;
  private int i;
  private int x;

  AdjacentSwapIterator(Permutation p) {
    this.p = p;
    // default init: rolled = false;
    hasMore = p.length() >= 2;
    x = i = -1;
  }

  @Override
  public boolean hasNext() {
    return hasMore && !rolled;
  }

  @Override
  public void nextMutant() {
    if (!hasMore) throw new IllegalStateException("no neighbors left");
    if (rolled)
      throw new IllegalStateException("illegal to call nextMutant after calling rollback");
    if (i >= 0) {
      p.swap(i, i + 1);
    }
    i++;
    p.swap(i, i + 1);
    if (i == p.length() - 2) hasMore = false;
  }

  @Override
  public void setSavepoint() {
    x = i;
  }

  @Override
  public void rollback() {
    if (!rolled) {
      rolled = true;
      if (x < 0) {
        if (i >= 0) p.swap(i, i + 1);
      } else if (i != x) {
        p.swap(i, i + 1);
        p.swap(x, x + 1);
      }
    }
  }
}
