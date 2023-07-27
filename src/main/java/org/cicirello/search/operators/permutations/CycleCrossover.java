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
import org.cicirello.permutations.PermutationFullBinaryOperator;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * Implementation of cycle crossover (CX). CX selects a random index into the permutations, computes
 * the permutation cycle of the pair of parent permutations that includes that randomly chosen
 * element, and then exchanges the elements of the cycle between the parents in forming the
 * children.
 *
 * <p>For example, consider the permutation p1 = [0, 1, 2, 3, 4, 5, 6, 7] and the permutation p2 =
 * [1, 2, 0, 5, 6, 7, 4, 3]. Consider that the random index is 3. At index 3 in p1 is element 3, and
 * at that same index in p2 is element 5. At the same position as 5 in p1 is 7 in p2. And at the
 * same position as element 7 in p1 is element 3 in p2, thus completing the cycle. The elements of
 * the cycle are exchanged between the parents to form the children. Thus, the children are c1 = [0,
 * 1, 2, 5, 4, 7, 6, 3] and c2 = [1, 2, 0, 3, 6, 5, 4, 7].
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(n), where n is the length of the
 * permutations.
 *
 * <p>The CX operator was introduced in the following paper:<br>
 * Oliver, I.M., Smith, D.J., and Holland, J.R.C. A study of permutation crossover operators on the
 * traveling salesman problem. <i>Proceedings of the 2nd International Conference on Genetic
 * Algorithms</i>, 1987, pp. 224-230.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class CycleCrossover
    implements CrossoverOperator<Permutation>, PermutationFullBinaryOperator {

  private final EnhancedSplittableGenerator generator;

  /** Constructs a cycle crossover (CX) operator. */
  public CycleCrossover() {
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private CycleCrossover(CycleCrossover other) {
    generator = other.generator.split();
  }

  @Override
  public void cross(Permutation c1, Permutation c2) {
    c1.apply(this, c2);
  }

  @Override
  public CycleCrossover split() {
    return new CycleCrossover(this);
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
    boolean[] inCycle = new boolean[raw1.length];
    int[] cycle = new int[raw1.length];
    int count = 0;
    int[] inv1 = p1.getInverse();
    int i = generator.nextInt(raw1.length);
    while (!inCycle[i]) {
      inCycle[i] = true;
      cycle[count] = i;
      count++;
      i = inv1[raw2[i]];
    }
    for (i = 0; i < count; i++) {
      int temp = raw1[cycle[i]];
      raw1[cycle[i]] = raw2[cycle[i]];
      raw2[cycle[i]] = temp;
    }
  }
}
