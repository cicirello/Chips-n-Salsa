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
 * This class implements an insertion mutation on permutations, where one mutation consists in
 * removing a randomly chosen element and reinserting it at a different randomly chosen location.
 * The removed element is chosen uniformly at random from among all possible elements, and the new
 * location is chosen uniformly at random from among all possible new locations.
 *
 * <p>The runtime (worst case and average case) of both the {@link #mutate(Permutation) mutate} and
 * {@link #undo(Permutation) undo} methods is O(n), where n is the length of the permutation. The
 * worst case runtime occurs when the removed element is at one end of the permutation, and
 * reinserted at the opposite end, which causes all n permutation elements to move one position
 * each. On average, an insertion mutation moves approximately n/3 elements.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class InsertionMutation
    implements UndoableMutationOperator<Permutation>, IterableMutationOperator<Permutation> {

  // needed to implement undo
  private final int[] indexes;

  private final EnhancedSplittableGenerator generator;

  /** Constructs an InsertionMutation mutation operator. */
  public InsertionMutation() {
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
    indexes = new int[2];
  }

  private InsertionMutation(InsertionMutation other) {
    generator = other.generator.split();
    indexes = new int[2];
  }

  @Override
  public final void mutate(Permutation c) {
    if (c.length() >= 2) {
      generator.nextIntPair(c.length(), indexes);
      c.removeAndInsert(indexes[0], indexes[1]);
    }
  }

  @Override
  public final void undo(Permutation c) {
    if (c.length() >= 2) c.removeAndInsert(indexes[1], indexes[0]);
  }

  @Override
  public InsertionMutation split() {
    return new InsertionMutation(this);
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
    return new InsertionIterator(p);
  }
}
