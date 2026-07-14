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

package org.cicirello.search.operators.permutations;

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.PermutationBinaryOperator;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.util.IntegerArray;

/**
 * Abstract package-access base class for common portions of OrderCrossover and
 * NonWrappingOrderCrossover.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
abstract class AbstractOrderCrossover
    implements CrossoverOperator<Permutation>, PermutationBinaryOperator {

  private final EnhancedSplittableGenerator generator;

  /** Constructs an order crossover (OX) operator. */
  AbstractOrderCrossover() {
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  AbstractOrderCrossover(AbstractOrderCrossover other) {
    generator = other.generator.split();
  }

  @Override
  public final void cross(Permutation c1, Permutation c2) {
    c1.apply(this, c2);
  }

  /**
   * See {@link PermutationBinaryOperator} for details of this method. This method is not intended
   * for direct usage. Use the {@link #cross} method instead.
   *
   * @param raw1 The raw representation of the first permutation.
   * @param raw2 The raw representation of the second permutation.
   */
  @Override
  public final void apply(int[] raw1, int[] raw2) {
    int i = generator.nextInt(raw1.length);
    int j = generator.nextInt(raw1.length);
    if (j < i) {
      int temp = i;
      i = j;
      j = temp;
    }
    final int orderedCount = raw1.length - (j - i + 1);
    if (orderedCount > 0) {
      boolean[] in1 = new boolean[raw1.length];
      boolean[] in2 = new boolean[raw1.length];
      for (int k = i; k <= j; k++) {
        in1[raw1[k]] = true;
        in2[raw2[k]] = true;
      }
      IntegerArray list1 = new IntegerArray(orderedCount);
      IntegerArray list2 = new IntegerArray(orderedCount);
      for (int k = 0; k < raw1.length; k++) {
        if (!in2[raw1[k]]) {
          list1.add(raw1[k]);
        }
        if (!in1[raw2[k]]) {
          list2.add(raw2[k]);
        }
      }
      int rightSide = orderedCount - i;
      formPermutations(list1, list2, raw1, raw2, rightSide, i, j);
    }
  }

  /**
   * Form the child permutations.
   *
   * @param list1 elements of parent 1 in the relative order from parent 1 that are not in the cross
   *     region of parent 2
   * @param list2 elements of parent 2 in the relative order from parent 2 that are not in the cross
   *     region of parent 1
   * @param raw1 The raw representation of the first permutation
   * @param raw2 The raw representation of the second permutation
   * @param rightSide the first index after the cross region
   * @param i first index of cross region
   * @param j last index of cross region
   */
  abstract void formPermutations(
      IntegerArray list1, IntegerArray list2, int[] raw1, int[] raw2, int rightSide, int i, int j);
}
