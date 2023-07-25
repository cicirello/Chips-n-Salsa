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
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.operators.UndoableMutationOperator;

/**
 * This class implements a reversal mutation on permutations, where one mutation consists in
 * reversing the order of a randomly selected subpermutation. The pair of indexes that indicate the
 * subpermutation to reverse is chosen uniformly at random from among all n(n-1)/2 possible pairs of
 * indexes, where n is the length of the permutation.
 *
 * <p>The runtime (worst case and average case) of both the {@link #mutate(Permutation) mutate} and
 * {@link #undo(Permutation) undo} methods is O(n), where n is the length of the permutation. The
 * worst case runtime occurs when the random indexes are the two end points resulting in a complete
 * reversal. On average, a reversal mutation moves approximately n/3 elements.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ReversalMutation
    implements UndoableMutationOperator<Permutation>, IterableMutationOperator<Permutation> {

  // needed to implement undo
  private final int[] indexes;

  private final EnhancedSplittableGenerator generator;

  /** Constructs an ReversalMutation mutation operator. */
  public ReversalMutation() {
    indexes = new int[2];
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private ReversalMutation(ReversalMutation other) {
    generator = other.generator.split();
    indexes = new int[2];
  }

  @Override
  public void mutate(Permutation c) {
    if (c.length() >= 2) {
      generator.nextIntPair(c.length(), indexes);
      c.reverse(indexes[0], indexes[1]);
    }
  }

  @Override
  public void undo(Permutation c) {
    if (c.length() >= 2) {
      c.reverse(indexes[0], indexes[1]);
    }
  }

  @Override
  public ReversalMutation split() {
    return new ReversalMutation(this);
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
    return new ReversalIterator(p);
  }
}
