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

package org.cicirello.search.operators.permutations;

import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.math.rand.RandomSampler;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.UndoableMutationOperator;

/**
 * This class implements a scramble mutation on permutations, where one mutation consists in
 * randomizing the order of a non-contiguous subset of the permutation elements. It is controlled by
 * a parameter U, which is the probability that an element is included in the scramble. On average,
 * you can expect U*n elements to be randomized, where n is the length of the permutation. This
 * version of uniform scramble mutation also supports the undo method.
 *
 * <p>The worst case and average case runtime of the {@link #mutate(Permutation) mutate} method is
 * O(n), where n is the length of the permutation. The worst case runtime of the {@link
 * #undo(Permutation) undo} method is also O(n), but its average runtime is O(U*n).
 *
 * <p>If you don't need the {@link #undo(Permutation) undo} method, then it is recommended that you
 * instead use the {@link UniformScrambleMutation} class instead to avoid the O(n) extra memory
 * required to store the prior permutation state, as well as the time associated with copying that
 * state prior to mutation.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class UndoableUniformScrambleMutation
    implements UndoableMutationOperator<Permutation> {

  private final double u;
  private final boolean guaranteeChange;

  private int[] last;
  private int[] indexes;

  /**
   * Constructs a UndoableUniformScrambleMutation mutation operator.
   *
   * @param u The probability that an element is included in a scramble. If the permutation length
   *     is n, you can expect approximately u*n elements on average to be scrambled.
   * @throws IllegalArgumentException if u is negative or if u is greater than 1.0.
   */
  public UndoableUniformScrambleMutation(double u) {
    this(u, false);
  }

  /**
   * Constructs a UndoableUniformScrambleMutation mutation operator.
   *
   * @param u The probability that an element is included in a scramble. If the permutation length
   *     is n, you can expect approximately u*n elements on average to be scrambled.
   * @param guaranteeChange If true, then the {@link #mutate(Permutation) mutate} method will be
   *     guaranteed to change the locations of at least 2 elements. Otherwise, if false, it may be
   *     possible (e.g., for low values of u) for the mutate method not to change anything during
   *     some calls.
   * @throws IllegalArgumentException if u is negative or if u is greater than 1.0.
   */
  public UndoableUniformScrambleMutation(double u, boolean guaranteeChange) {
    if (u < 0 || u > 1.0) throw new IllegalArgumentException("u must be in [0.0, 1.0].");
    this.u = u;
    this.guaranteeChange = guaranteeChange;
  }

  @Override
  public final void mutate(Permutation c) {
    if (c.length() >= 2) {
      last = c.toArray();
      indexes = RandomSampler.sample(c.length(), u);
      if (guaranteeChange && indexes.length < 2) {
        indexes = RandomIndexer.nextIntPair(c.length(), null);
      }
      c.scramble(indexes);
    }
  }

  @Override
  public final void undo(Permutation c) {
    if (c.length() >= 2) {
      c.apply(
          perm -> {
            for (int i = 0; i < indexes.length; i++) {
              perm[indexes[i]] = last[indexes[i]];
            }
          });
    }
  }

  @Override
  public UndoableUniformScrambleMutation split() {
    return new UndoableUniformScrambleMutation(u, guaranteeChange);
  }
}
