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

import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.PermutationBinaryOperator;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.util.IntegerArray;

/**
 * Implementation of non-wrapping order crossover (NWOX). NWOX selects a random subsection similar
 * to a 2-point crossover for bit-strings. One child gets the positions of the elements in the
 * chosen subsection from parent 1, and the relative order of the remainder of the elements from
 * parent 2. The other child gets the positions of the elements in the chosen subsection from parent
 * 2, and the relative order of the remainder of the elements from parent 1. The relative ordered
 * elements begin at index 0 (unless that index is part of the random segment) continue until the
 * cross segment is reached, jump the cross region, and continue until the right end of the
 * permutation.
 *
 * <p>For example, consider the permutation p1 = [0, 1, 2, 3, 4, 5, 6, 7] and the permutation p2 =
 * [1, 2, 0, 5, 6, 7, 4, 3]. Consider that the random subsection of the permutations begins at index
 * 2 and ends at index 4, inclusive. Thus, one child will get the positions of 2, 3, 4 from p1, and
 * the relative ordering of the rest from p2. First, fill in the 2, 3, 4 from p1 to get c1 = [x, x,
 * 2, 3, 4, x, x, x]. c1 will get the relative order of the rest of the elements from p2, namely 1,
 * 0, 5, 6, 7. Fill these in that order into c1 beginning at the left-most index, and jumping over
 * the cross segment, and continuing to the right end to get c1 = [1, 0, 2, 3, 4, 5, 6, 7]. In a
 * similar way, c2 begins with the positions of 0, 5, 6 from p2, such that c2 = [x, x, 0, 5, 6, x,
 * x, x]. c2 then gets the relative order of the remainder of the elements from p1, namely 1, 2, 3,
 * 4, 7. After filling these in the given order, from the left-most open index to the right-most, we
 * end up with c2 = [1, 2, 0, 5, 6, 3, 4, 7].
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(n), where n is the length of the
 * permutations.
 *
 * <p>NWOX was introduced in the following paper:
 *
 * <p>Vincent A. Cicirello. <a
 * href="https://www.cicirello.org/publications/cicirello2006gecco.html">Non-Wrapping Order
 * Crossover: An Order Preserving Crossover Operator that Respects Absolute Position</a>.
 * <i>Proceedings of the Genetic and Evolutionary Computation Conference (GECCO'06)</i>, volume 2,
 * pages 1125-1131. ACM Press, July 2006. doi:<a
 * href="http://dx.doi.org/10.1145/1143997.1144177">10.1145/1143997.1144177</a>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class NonWrappingOrderCrossover
    implements CrossoverOperator<Permutation>, PermutationBinaryOperator {

  /** Constructs a non-wrapping order crossover (NWOX) operator. */
  public NonWrappingOrderCrossover() {}

  @Override
  public void cross(Permutation c1, Permutation c2) {
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
  public void apply(int[] raw1, int[] raw2) {
    int i = RandomIndexer.nextInt(raw1.length);
    int j = RandomIndexer.nextInt(raw1.length);
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
      System.arraycopy(list2.array(), 0, raw1, 0, i);
      System.arraycopy(list1.array(), 0, raw2, 0, i);
      System.arraycopy(list2.array(), i, raw1, j + 1, rightSide);
      System.arraycopy(list1.array(), i, raw2, j + 1, rightSide);
    }
  }

  @Override
  public NonWrappingOrderCrossover split() {
    // doesn't maintain any state, so safe to return this
    return this;
  }
}
