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

import org.cicirello.math.rand.RandomSampler;
import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.PermutationFullBinaryOperator;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * Implementation of uniform partially matched crossover (UPMX). UPMX is a variation of partially
 * matched crossover (PMX), but whereas PMX selects a contiguous cross region similar to a two-point
 * bit-string crossover, UPMX selects a set of non-contiguous cross points similar to uniform
 * crossover for bit-strings. UPMX begins by selecting a set of random cross points, using a
 * parameter U, which is the probability that an index is a cross point. Thus, for permutation
 * length N, the expected number of cross points is U*N. UPMX then initializes the children as
 * copies of the parents, and proceeds to make a sequence of swaps within child c1 as to cause c1 at
 * the end of those swaps to contain the elements from the cross points of p2 at those indexes. And
 * similarly for the other child. Essentially, the cross points define a set of swaps that are made
 * internally within each child.
 *
 * <p>Consider as an example parent permutation p1 = [7, 6, 5, 4, 3, 2, 1, 0] and parent permutation
 * p2 = [1, 2, 0, 5, 6, 4, 7, 3]. Now consider that the random cross sites are indexes 3, 1, and 6.
 * Child c1 is initialized as a copy of p1, i.e., c1 = [7, 6, 5, 4, 3, 2, 1, 0]. At index 3 in p1 is
 * element 4, and at index 3 in p2 is element 5, so UPMX swaps the 4 and the 5 within c1 to get c1 =
 * [7, 6, 4, 5, 3, 2, 1, 0]. At index 1, we find elements 6 and 2 in p1 and p2, respectively, so
 * UMPX swaps the 6 and the 2 within c1 to get c1 = [7, 2, 4, 5, 3, 6, 1, 0]. Finally, at index 6,
 * we find 1 and 7 in p1 and p2, respectively. Thus, UPMX swaps the 1 and 7 within c1 to get the
 * final child c1 = [1, 2, 4, 5, 3, 6, 7, 0]. In a similar way, we can derive child c2, such that c2
 * = [7, 6, 0, 4, 2, 5, 1, 3].
 *
 * <p>UPMX was introduced in the following paper:
 *
 * <p>Vincent A. Cicirello and Stephen F. Smith. <a
 * href="https://www.cicirello.org/publications/cicirello2000gecco.html">Modeling GA Performance for
 * Control Parameter Optimization</a>. <i>Proceedings of the Genetic and Evolutionary Computation
 * Conference (GECCO-2000)</i>, pages 235-242. Morgan Kaufmann Publishers, July 2000. <a
 * href="https://www.cicirello.org/publications/gecco2000-cicirello.pdf">[PDF]</a> <a
 * href="https://www.cicirello.org/publications/cicirello2000gecco.bib">[BIB]</a> <a
 * href="http://dl.acm.org/citation.cfm?id=2933750">[From ACM Digital Library]</a>
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(N), where N is the length of the
 * permutations.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class UniformPartiallyMatchedCrossover
    implements CrossoverOperator<Permutation>, PermutationFullBinaryOperator {

  private final double u;

  /**
   * Constructs a uniform partially matched crossover (UPMX) operator, with a default u = 1.0 / 3.0.
   * The rationale for this default u is that it leads UPMX to the same expected number of swaps as
   * PMX, only scattered throughout the permutation.
   */
  public UniformPartiallyMatchedCrossover() {
    this(0.3333333333333333);
  }

  /**
   * Constructs a uniform partially matched crossover (UPMX) operator.
   *
   * @param u The probability of an index being among the cross points.
   * @throws IllegalArgumentException if u is less than or equal to 0.0, or if u is greater than or
   *     equal to 1.0.
   */
  public UniformPartiallyMatchedCrossover(double u) {
    if (u <= 0 || u >= 1.0) throw new IllegalArgumentException("u must be: 0.0 < u < 1.0");
    this.u = u;
  }

  @Override
  public void cross(Permutation c1, Permutation c2) {
    c1.apply(this, c2);
  }

  @Override
  public UniformPartiallyMatchedCrossover split() {
    // doesn't maintain any mutable state, so safe to return this.
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
    internalCross(raw1, raw2, p1, p2, RandomSampler.sample(raw1.length, u));
  }

  /*
   * package private to facilitate unit testing
   */
  final void internalCross(int[] raw1, int[] raw2, Permutation c1, Permutation c2, int[] indexes) {
    int[] inv1 = c1.getInverse();
    int[] inv2 = c2.getInverse();
    int[] old1 = raw1.clone();
    int[] old2 = raw2.clone();
    for (int k : indexes) {
      int g = inv1[old2[k]];
      if (k != g) {
        int temp = raw1[k];
        raw1[k] = raw1[g];
        raw1[g] = temp;
        inv1[raw1[g]] = g;
        inv1[old2[k]] = k;
      }
      g = inv2[old1[k]];
      if (k != g) {
        int temp = raw2[k];
        raw2[k] = raw2[g];
        raw2[g] = temp;
        inv2[raw2[g]] = g;
        inv2[old1[k]] = k;
      }
    }
  }
}
