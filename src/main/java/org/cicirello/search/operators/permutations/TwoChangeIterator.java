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

package org.cicirello.search.operators.permutations;

import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.MutationIterator;

/**
 * Internal (package-private) class implementing an iterator over all two changes.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
final class TwoChangeIterator implements MutationIterator {

  // NOTE: Much of this implementation has been adapted
  // from WindowLimitedReversalIterator, in the following
  // ways: (1) the window limit w is p.length()-3; and
  // (2) to avoid redundant reversals logically equivalent
  // to the same two change, excludes reversals that include
  // right end.

  private boolean rolled;
  private boolean hasMore;
  private final Permutation p;
  private final int w;
  private int i;
  private int j;
  private int u;
  private int v;
  private int x;
  private int y;

  TwoChangeIterator(Permutation p) {
    this.p = p;
    this.w = p.length() - 3;
    hasMore = p.length() >= 4;
    // Default inits:
    //    y = x = u = v = i = j = 0;
    //    rolled = false;
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
    if (i == j) {
      if (w >= 2) v = j = 2;
      else v = j = 1;
    } else {
      if (u == 0 || v == p.length() - 2 || v - u >= w - 1) {
        p.reverse(u, v);
        j++;
        if (j >= p.length() - 1) {
          i = 0;
          j = 1;
        } else {
          i++;
        }
        u = i;
        v = j;
      } else {
        u--;
        v++;
      }
    }
    p.swap(u, v);
    if (u == p.length() - 3) hasMore = false;
  }

  @Override
  public void setSavepoint() {
    x = u;
    y = v;
  }

  @Override
  public void rollback() {
    if (!rolled) {
      rolled = true;
      if (y == 0) {
        if (v > 0) p.reverse(u, v);
      } else if (u != x || v != y) {
        p.reverse(u, v);
        p.reverse(x, y);
      }
    }
  }
}
