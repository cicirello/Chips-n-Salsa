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
import org.cicirello.permutations.PermutationFullBinaryOperator;
import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.util.IntegerArray;

/**
 * Implementation of the crossover operator for permutations that is often referred to as Order
 * Crossover 2 (OX2). It is rather different in function than the original Order Crossover (OX),
 * implemented in class {@link OrderCrossover}. OX2, which was introduced by Syswerda (see reference
 * below), is very nearly identical to Uniform Order Based Crossover (UOBX) also introduced by
 * Syswerda in the same paper. UOBX is implemented in the {@link UniformOrderBasedCrossover} class.
 * Each child produced by OX2 from a given pair of parents can be produced by UOBX from the same
 * pair of parents. Likewise each child produced by UOBX from a given pair of parents can be
 * produced by OX2 from the same pair of parents. However, the pair of children produced by OX2 from
 * a given pair of parents will typically differ from the pair of children produced by UOBX and vice
 * versa. Therefore, OX2 and UOBX are not exactly equivalent. However, it is not clear whether there
 * is ever an occasion when either one will lead to significantly different performance relative to
 * the other. The Chips-n-Salsa library includes both operators in the interest of comprehensiveness
 * with respect to commonly encountered permutation crossover operators.
 *
 * <p>OX2 begins by selecting a random set of indexes. The original description implied each index
 * equally likely chosen as not chosen. However, in our implementation, we provide a parameter u,
 * which is the probability that an index is chosen, much like the parameter of a uniform crossover
 * for bit-strings. We provide a constructor with a default of u=0.5. The elements at those indexes
 * in parent p2 are found in parent p1. Child c1 is then a copy of p1 but with those elements
 * rearranged into the relative order from p2. In a similar way, the elements at the chosen indexes
 * in parent p1 are found in parent p2. Child c2 is then a copy of p2 but with those elements
 * rearranged into the relative order from p1.
 *
 * <p>Consider this example. Let p1 = [1, 0, 3, 2, 5, 4, 7, 6] and p2 = [6, 7, 4, 5, 2, 3, 0, 1].
 * Let the random indexes include: 1, 2, 6, and 7. The elements at those indexes in p2, ordered as
 * in p2, are: 7, 4, 0, 1. These are therefore rearranged within p1 to produce c1 = [7, 4, 3, 2, 5,
 * 0, 1, 6]. The elements at the random indexes in p1, ordered as in p1, are: 0, 3, 7, 6. These are
 * therefore rearranged within p2 to produce c2 = [0, 3, 4, 5, 2, 7, 6, 1].
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(n), where n is the length of the
 * permutations.
 *
 * <p>OX2 was introduced in the following paper:<br>
 * Syswerda, G. Schedule Optimization using Genetic Algorithms. <i>Handbook of Genetic
 * Algorithms</i>, 1991.
 *
 * <p>Although it got its name Order Crossover 2 (OX2) from others in order to distinguish it from
 * the original OX, such as this paper:<br>
 * T. Starkweather, S McDaniel, K Mathias, D Whitley, and C Whitley. A Comparison of Genetic
 * Sequencing Operators. <i>Proceedings of the Fourth International Conference on Genetic
 * Algorithms</i>, pages 69-76, 1991.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class OrderCrossoverTwo
    implements CrossoverOperator<Permutation>, PermutationFullBinaryOperator {

  private final double u;

  /**
   * Constructs Syswerda's order crossover operator, often referred to as OX2. Uses a default U=0.5.
   */
  public OrderCrossoverTwo() {
    this(0.5);
  }

  /**
   * Constructs Syswerda's order crossover operator, often referred to as OX2.
   *
   * @param u The probability of selecting an index.
   * @throws IllegalArgumentException if u is less than or equal to 0.0, or if u is greater than or
   *     equal to 1.0.
   */
  public OrderCrossoverTwo(double u) {
    if (u <= 0 || u >= 1.0) throw new IllegalArgumentException("u must be: 0.0 < u < 1.0");
    this.u = u;
  }

  @Override
  public void cross(Permutation c1, Permutation c2) {
    c1.apply(this, c2);
  }

  @Override
  public OrderCrossoverTwo split() {
    // doesn't maintain any mutable state, so safe to return this
    return this;
  }

  /**
   * See {@link PermutationFullBinaryOperator} for details of this method. This method is not
   * intended for direct usage. Use the {@link #cross} method instead.
   *
   * @param raw1 The raw representation of the first permutation.
   * @param raw2 The raw representation of the second permutation.
   * @param p1 The first permutation.
   * @param p2 The second permutation.
   */
  @Override
  public void apply(int[] raw1, int[] raw2, Permutation p1, Permutation p2) {
    internalCross(raw1, raw2, p1, p2, RandomIndexer.arrayMask(raw1.length, u));
  }

  /*
   * package private to facilitate testing
   */
  final void internalCross(int[] raw1, int[] raw2, Permutation p1, Permutation p2, boolean[] mask) {
    int[] inv1 = p1.getInverse();
    int[] inv2 = p2.getInverse();
    IntegerArray elementOrder1 = new IntegerArray(raw1.length);
    IntegerArray elementOrder2 = new IntegerArray(raw1.length);
    boolean[] indexes1 = new boolean[raw1.length];
    boolean[] indexes2 = new boolean[raw1.length];
    for (int i = 0; i < mask.length; i++) {
      if (mask[i]) {
        elementOrder1.add(raw2[i]);
        elementOrder2.add(raw1[i]);
        indexes1[inv1[raw2[i]]] = true;
        indexes2[inv2[raw1[i]]] = true;
      }
    }
    int j = 0;
    int k = 0;
    for (int i = 0; i < indexes1.length; i++) {
      if (indexes1[i]) {
        raw1[i] = elementOrder1.get(j);
        j++;
      }
      if (indexes2[i]) {
        raw2[i] = elementOrder2.get(k);
        k++;
      }
    }
  }
}
