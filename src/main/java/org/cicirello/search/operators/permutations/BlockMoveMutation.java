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

package org.cicirello.search.operators.permutations;

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.operators.UndoableMutationOperator;

/**
 * This class implements a block move mutation on permutations, where one mutation consists in
 * removing a randomly chosen "block" (i.e., subsequence) and reinserting it at a different randomly
 * chosen index. The block move is chosen uniformly at random from among all possible block moves.
 *
 * <p>A block move is sometimes also called a block transposition, and can be described equivalently
 * as swapping two adjacent blocks. Consider the permutation: p1 = [0, 1, 2, 3, 4, 5, 6, 7]. Now
 * consider a block move that consists in removing block [4, 5, 6] and reinserting it at index 2.
 * The result is p2 = [0, 1, 4, 5, 6, 2, 3, 7]. This can be described equivalently as swapping the
 * two adjacent blocks [4, 5, 6] and [2, 3]. This mutation operator is related to the {@link
 * BlockInterchangeMutation}, which swaps a pair of randomly selected non-overlapping blocks.
 *
 * <p>The runtime (worst case and average case) of both the {@link #mutate(Permutation) mutate} and
 * {@link #undo(Permutation) undo} methods is O(n), where n is the length of the permutation. The
 * worst case runtime occurs when the removed block is at one end of the permutation, and reinserted
 * at the opposite end, which causes all n permutation elements to move. On average, a block move
 * affects n/2 element locations.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class BlockMoveMutation
    implements UndoableMutationOperator<Permutation>, IterableMutationOperator<Permutation> {

  // needed to implement undo
  private final int[] indexes;

  private final EnhancedSplittableGenerator generator;

  /** Constructs a BlockMoveMutation mutation operator. */
  public BlockMoveMutation() {
    this(RandomnessFactory.createEnhancedSplittableGenerator());
  }

  /** package access for use by window limited, within package */
  BlockMoveMutation(EnhancedSplittableGenerator generator) {
    this.generator = generator;
    indexes = new int[3];
  }

  private BlockMoveMutation(BlockMoveMutation other) {
    this(other.generator.split());
  }

  @Override
  public void mutate(Permutation c) {
    if (c.length() >= 2) {
      generateIndexes(c.length(), indexes);
      c.removeAndInsert(indexes[1], indexes[2] - indexes[1] + 1, indexes[0]);
    }
  }

  @Override
  public void undo(Permutation c) {
    c.removeAndInsert(indexes[0], indexes[2] - indexes[1] + 1, indexes[1]);
  }

  @Override
  public BlockMoveMutation split() {
    return new BlockMoveMutation(this);
  }

  /**
   * {@inheritDoc}
   *
   * <p>The worst case runtime of the {@link MutationIterator#hasNext} and the {@link
   * MutationIterator#setSavepoint} methods of the {@link MutationIterator} created by this method
   * is O(1). The amortized runtime of the {@link MutationIterator#nextMutant} method is O(1). And
   * the worst case runtime of the {@link MutationIterator#rollback} method is O(n), where n is the
   * length of the Permutation.
   */
  @Override
  public MutationIterator iterator(Permutation p) {
    return new BlockMoveIterator(p);
  }

  /*
   * package access to support unit testing
   */
  void generateIndexes(int n, int[] indexes) {
    // Note 1: The nextSortedIntTriple method returns 3 all different indexes,
    // but a removed block of length 1 would require 2 identical indexes.
    // To handle this, add 1 to n, and map an index beyond end of permutation
    // to the block length 1 case.
    // Note 2: Without loss of generality, the indexes are generated to
    // move the block earlier in the permutation.  We can do this because
    // a "block move" essentially swaps two adjacent "blocks."
    generator.nextSortedIntTriple(n + 1, indexes);
    if (indexes[2] == n) {
      indexes[2] = indexes[1];
    }
  }
}
