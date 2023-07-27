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

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.UndoableMutationOperator;

/**
 * This class implements the classic 3-Opt neighborhood as a mutation operator for permutations. The
 * 3-Opt neighborhood includes all two-changes and all three-changes. These originated specifically
 * for the TSP. A two-change for the TSP removes two edges from a tour of the cities of a TSP and
 * replaces them with two different edges such that the result is a valid tour of the cities.
 * Likewise, a three-change removes three edges from the tour of the cities of a TSP and replaces
 * them with three different edges. This implementation is not strictly for the TSP, and will
 * operate on a permutation regardless of what that permutation represents. However, it assumes that
 * the permutation represents a cyclic sequence of undirected edges, and specifically that if two
 * elements are adjacent in the permutation that it corresponds to an undirected edge between the
 * elements. For example, consider the permutation, p = [2, 1, 4, 0, 3], of the first 5 non-negative
 * integers. Now imagine that we have a graph with 5 vertexes, labeled 0 to 4. This example
 * permutation would correspond to a set of undirected edges: { (2, 1), (1, 4), (4, 0), (0, 3), (3,
 * 2) }. Notice that we included (3, 2) here in that the set of edges represented by the permutation
 * is cyclic and includes an edge between the two endpoints.
 *
 * <p>The runtime (worst case and average case) of both the {@link #mutate(Permutation) mutate} and
 * {@link #undo(Permutation) undo} methods is O(n), where n is the length of the permutation.
 *
 * <p>For any given permutation of length n, there are n*(n-3)/2 possible two-change neighbors, and
 * 4*(n*(n-1)*(n-2)/6 - n*(n-4) - n) + n*(n-4) possible three-change neighbors. Each of the possible
 * three-changes is approximately equally likely as every other three-change. Each of the possible
 * two-changes is approximately equally likely as every other two-change. The current implementation
 * does not guarantee that each of the possible two-changes are equally likely as each of the
 * possible three-changes. Currently, each two-change is slightly more likely than each
 * three-change. Although because the number of possible three-changes grows cubicly vs quadratic
 * growth in number of possible two-changes, the probability of a three-change increases rapidly as
 * permutation length increases.
 *
 * <p>For permutations of length equal to 4, the ThreeOptMutation will only perform two-changes
 * because no valid three-changes exist for that length. For permutations of length n &lt; 4, the
 * ThreeOptMutation operator makes no changes, as there are no two-change or three-change neighbors
 * of permutations of that size.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ThreeOptMutation implements UndoableMutationOperator<Permutation> {

  // needed to implement undo
  private final int[] indexes;
  private int which;
  private int lastRotation;

  private final TwoChangeMutation twoChange;
  private final EnhancedSplittableGenerator generator;

  /** Constructs a ThreeOptMutation operator. */
  public ThreeOptMutation() {
    indexes = new int[3];
    twoChange = new TwoChangeMutation();
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private ThreeOptMutation(ThreeOptMutation other) {
    generator = other.generator.split();
    twoChange = other.twoChange.split();
    indexes = new int[3];
  }

  @Override
  public void mutate(Permutation c) {
    if (c.length() >= 5) {
      generator.nextIntTriple(c.length(), indexes, true);
      which = generator.nextBiasedInt(4);
      threeOrTwoChange(indexes, which, c);
    } else if (c.length() == 4) {
      twoChange.mutate(c);
    }
  }

  @Override
  public void undo(Permutation c) {
    if (c.length() >= 5) {
      undoThreeOrTwoChange(indexes, which, c);
    } else if (c.length() == 4) {
      twoChange.undo(c);
    }
  }

  @Override
  public ThreeOptMutation split() {
    return new ThreeOptMutation(this);
  }

  /*
   * package private to support testing
   */
  void threeOrTwoChange(int[] indexes, int which, Permutation c) {
    c.rotate(lastRotation = indexes[0]);
    indexes[2] -= indexes[0];
    indexes[1] -= indexes[0];
    indexes[0] = 0;
    if (indexes[2] == 2) {
      // Two singleton segments on left: just swap for 2-change
      c.swap(0, 1);
    } else if (indexes[1] == 1) {
      if (indexes[2] == c.length() - 1) {
        // Outside segments singletons
        // Just swap for 2-change
        c.swap(0, indexes[2]);
      } else {
        // left is only singleton
        if (which == 0) {
          // Ensure all 3 changes are equally likely,
          // although 2-changes will have a bit of a bias.
          c.removeAndInsert(1, indexes[2] - 1, 0);
        } else {
          // Reversal
          if (c.length() - indexes[2] >= indexes[2] - indexes[1]) {
            c.reverse(0, indexes[2] - 1);
          } else {
            c.reverse(1, indexes[2] - 1);
          }
        }
      }
    } else if (indexes[1] == c.length() - 2) {
      // Two singletons on the right: just swap them for a 2-change
      c.swap(indexes[1], indexes[2]);
    } else if (indexes[2] == c.length() - 1) {
      // One singleton on the right
      // Other two > 1 in length
      if (which == 0) {
        // Ensure all 3 changes are equally likely,
        // although 2-changes will have a bit of a bias.
        c.removeAndInsert(indexes[2], 1, indexes[1]);
      } else {
        // Reversal
        if (indexes[1] >= indexes[2] - indexes[1]) {
          c.reverse(indexes[1], indexes[2]);
        } else {
          c.reverse(indexes[1], indexes[2] - 1);
        }
      }
    } else if (indexes[2] == indexes[1] + 1) {
      // only singleton in middle
      if (which == 0) {
        // Ensure all 3 changes are equally likely,
        // although 2-changes will have a bit of a bias.
        c.removeAndInsert(indexes[1], 1, 0);
      } else {
        // Reversal
        if (indexes[1] <= c.length() - indexes[2]) {
          c.reverse(0, indexes[1]);
        } else {
          c.reverse(indexes[1], c.length() - 1);
        }
      }
    } else {
      // No Singletons
      if (which == 0) {
        c.removeAndInsert(indexes[1], indexes[2] - indexes[1], 0);
      } else if (which == 1) {
        c.reverse(0, indexes[1] - 1);
        c.reverse(indexes[1], indexes[2] - 1);
      } else if (which == 2) {
        c.reverse(0, indexes[1] - 1);
        c.removeAndInsert(indexes[1], indexes[2] - indexes[1], 0);
      } else {
        c.reverse(indexes[1], indexes[2] - 1);
        c.removeAndInsert(indexes[1], indexes[2] - indexes[1], 0);
      }
    }
  }

  /*
   * package private to support testing
   */
  void undoThreeOrTwoChange(int[] indexes, int which, Permutation c) {
    if (indexes[2] == 2) {
      // Two singleton segments on left: just swap for 2-change
      c.swap(0, 1);
    } else if (indexes[1] == 1) {
      if (indexes[2] == c.length() - 1) {
        // Outside segments singletons
        // Just swap for 2-change
        c.swap(0, indexes[2]);
      } else {
        // left is only singleton
        if (which == 0) {
          // Ensure all 3 changes are equally likely,
          // although 2-changes will have a bit of a bias.
          c.removeAndInsert(0, indexes[2] - 1, 1);
        } else {
          // Reversal
          if (c.length() - indexes[2] >= indexes[2] - indexes[1]) {
            c.reverse(0, indexes[2] - 1);
          } else {
            c.reverse(1, indexes[2] - 1);
          }
        }
      }
    } else if (indexes[1] == c.length() - 2) {
      // Two singletons on the right: just swap them for a 2-change
      c.swap(indexes[1], indexes[2]);
    } else if (indexes[2] == c.length() - 1) {
      // One singleton on the right
      // Other two > 1 in length
      if (which == 0) {
        // Ensure all 3 changes are equally likely,
        // although 2-changes will have a bit of a bias.
        c.removeAndInsert(indexes[1], 1, indexes[2]);
      } else {
        // Reversal
        if (indexes[1] >= indexes[2] - indexes[1]) {
          c.reverse(indexes[1], indexes[2]);
        } else {
          c.reverse(indexes[1], indexes[2] - 1);
        }
      }
    } else if (indexes[2] == indexes[1] + 1) {
      // only singleton in middle
      if (which == 0) {
        // Ensure all 3 changes are equally likely,
        // although 2-changes will have a bit of a bias.
        c.removeAndInsert(0, 1, indexes[1]);
      } else {
        // Reversal
        if (indexes[1] <= c.length() - indexes[2]) {
          c.reverse(0, indexes[1]);
        } else {
          c.reverse(indexes[1], c.length() - 1);
        }
      }
    } else {
      // No Singletons
      if (which == 0) {
        c.removeAndInsert(0, indexes[2] - indexes[1], indexes[1]);
      } else if (which == 1) {
        c.reverse(0, indexes[1] - 1);
        c.reverse(indexes[1], indexes[2] - 1);
      } else if (which == 2) {
        c.removeAndInsert(0, indexes[2] - indexes[1], indexes[1]);
        c.reverse(0, indexes[1] - 1);
      } else {
        c.removeAndInsert(0, indexes[2] - indexes[1], indexes[1]);
        c.reverse(indexes[1], indexes[2] - 1);
      }
    }
    c.rotate(-lastRotation);
  }
}
