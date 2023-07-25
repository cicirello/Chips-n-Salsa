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
import org.cicirello.search.operators.MutationIterator;

/**
 * This class implements a window-limited version of the {@link BlockMoveMutation} mutation operator
 * on permutations. A window-limited mutation operator on permutations is a mutation operator such
 * that there is a window constraint, w, on the random indexes used by the mutation. For a mutation
 * operator like {@link BlockMoveMutation} that uses three random indexes (i, j, k), two to define
 * the block and the third for the reinsertion point, these indexes are constrained such that |i-j|
 * &le; w, |i-k| &le; w, |j-k| &le; w. All index triples that satisfy the window constraint are
 * equally likely.
 *
 * <p>The runtime (worst case and average case) of both the {@link #mutate(Permutation) mutate} and
 * {@link #undo(Permutation) undo} methods is O(min(n,w)), where n is the length of the permutation.
 *
 * <p>For further discussion of window limited permutation mutation operators see:<br>
 * V. A. Cicirello, <a href="https://www.cicirello.org/publications/cicirello2014bict.html"
 * target=_top>"On the Effects of Window-Limits on the Distance Profiles of Permutation Neighborhood
 * Operators,"</a> in Proceedings of the 8th International Conference on Bioinspired Information and
 * Communications Technologies, pages 28–35, December 2014.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class WindowLimitedBlockMoveMutation extends BlockMoveMutation {

  private final int limit;

  /**
   * Constructs a WindowLimitedBlockMoveMutation mutation operator with a default window limit of
   * Integer.MAX_VALUE.
   */
  public WindowLimitedBlockMoveMutation() {
    this(Integer.MAX_VALUE);
  }

  /**
   * Constructs a WindowLimitedBlockMoveMutation mutation operator.
   *
   * @param windowLimit The window limit, which must be positive.
   * @throws IllegalArgumentException if windowLimit &le; 0
   */
  public WindowLimitedBlockMoveMutation(int windowLimit) {
    super();
    if (windowLimit <= 0) throw new IllegalArgumentException("window limit must be positive");
    limit = windowLimit;
  }

  private WindowLimitedBlockMoveMutation(WindowLimitedBlockMoveMutation other) {
    super(other);
    limit = other.limit;
  }

  @Override
  public WindowLimitedBlockMoveMutation split() {
    return new WindowLimitedBlockMoveMutation(this);
  }

  @Override
  public MutationIterator iterator(Permutation p) {
    return new WindowLimitedBlockMoveIterator(p, limit);
  }

  @Override
  final void generateIndexes(int n, int[] indexes, EnhancedSplittableGenerator generator) {
    if (limit >= n) {
      super.generateIndexes(n, indexes, generator);
      return;
    }
    // Note 1: The nextWindowedIntTriple method returns 3 all different indexes,
    // but a removed block of length 1 would require 2 identical indexes.
    // To handle this, add 1 to n and also add 1 to limit,
    // and map an index beyond end of permutation to the block length 1 case.
    // And also map the case of the max index minus the min index greater than limit
    // to the block length 1 case.
    // Note 2: Without loss of generality, the indexes are generated to
    // move the block earlier in the permutation.  We can do this because
    // a "block move" essentially swaps two adjacent "blocks."
    generator.nextWindowedIntTriple(n + 1, limit + 1, indexes, true);
    if (indexes[2] == n || indexes[2] - indexes[0] > limit) indexes[2] = indexes[1];
  }
}
