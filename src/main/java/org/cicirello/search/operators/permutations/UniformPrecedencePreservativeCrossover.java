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
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * Implementation of Precedence Preservative Crossover (PPX), the uniform version. The paper by
 * Bierwirth et al, which introduced PPX, described two versions of the operator, including the
 * uniform version that is implemented by this class, and a two-point version implemented by the
 * {@link PrecedencePreservativeCrossover} class. They referred to both simply as PPX in that paper,
 * but these are essentially two very similar, closely related crossover operators.
 *
 * <p>The paper that originally described PPX described it as producing one child from the cross of
 * two parents. However, our implementation generalizes this in the obvious way to producing two
 * children from two parents. In the uniform version of PPX, we begin with a random array of
 * booleans the same length as the parent permutations. In the Bierwirth et al paper, it was
 * described with a random array of 1s and 2s, but this is clearly equivalent to booleans. In our
 * implementation, we use a parameter U as the probability of a true. The Bierwirth et al paper
 * didn't specify, implying equally likely or 0.5. One of this class's constructors defaults U = 0.5
 * for this reason. Each index of the boolean array controls whether the corresponding index of a
 * child permutation c1 comes from parent p1 or parent p2. If true, the next element of child c1 is
 * the first element left-to-right from parent p1 that is not yet in c1; and if false, the next
 * element of child c1 is the first element left-to-right from parent p2 that is not yet in c1.
 * Likewise, if true, the next element of child c2 is the first element left-to-right from parent p2
 * that is not yet in c2; and if false, the next element of child c2 is the first element
 * left-to-right from parent p1 that is not yet in c2.
 *
 * <p>Consider this example with parent p1 = [7, 6, 5, 4, 3, 2, 1, 0] and parent p2 = [0, 1, 2, 3,
 * 4, 5, 6, 7]. Without loss of generality, we'll use 0s and 1s for our example boolean array: [0,
 * 1, 0, 0, 0, 1, 0, 1]. Child c1 gets its first element from p2, beginning with c1 = [0]. The next
 * element of the boolean array is 1, so c1 gets its next element from p1 for c1 = [0, 7]. The next
 * 3 elements of the boolean array are 0s, so c1 gets its next 3 elements from p2 for c1 = [0, 7, 1,
 * 2, 3, 6]. It then gets its next element from p1 because of the 1 in the next spot of the boolean
 * array. The next element comes from p2 for c1 = [0, 7, 1, 2, 3, 6, 4]. And the final element comes
 * from p1, although since there is only 1 unused element it doesn't really matter. The final c1 =
 * [0, 7, 1, 2, 3, 6, 4, 5]. We can form c2 in a similar way, but the meaning of the 0s and 1s in
 * the boolean array is flipped. The result is c2 = [7, 0, 6, 5, 4, 1, 3, 2].
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(n), where n is the length of the
 * permutations.
 *
 * <p>PPX was introduced in the following paper:<br>
 * Bierwirth, C., Mattfeld, D., and Kopfer, H. On permutation representations for scheduling
 * problems. <i>Proceedings of the International Conference on Parallel Problem Solving from
 * Nature</i>, 1996, pp. 310-318.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class UniformPrecedencePreservativeCrossover
    implements CrossoverOperator<Permutation> {

  private final double u;

  /**
   * Constructs an instance of the uniform version of the precedence preservative crossover (PPX)
   * operator, with a default value of u = 0.5.
   */
  public UniformPrecedencePreservativeCrossover() {
    this(0.5);
  }

  /**
   * Constructs an instance of the uniform version of the precedence preservative crossover (PPX)
   * operator.
   *
   * @param u The probability of an index being among the cross points.
   * @throws IllegalArgumentException if u is less than or equal to 0.0, or if u is greater than or
   *     equal to 1.0.
   */
  public UniformPrecedencePreservativeCrossover(double u) {
    if (u <= 0 || u >= 1.0) throw new IllegalArgumentException("u must be: 0.0 < u < 1.0");
    this.u = u;
  }

  @Override
  public void cross(Permutation c1, Permutation c2) {
    c1.apply(
        (raw1, raw2) -> internalCross(raw1, raw2, RandomIndexer.arrayMask(raw1.length, u)), c2);
  }

  @Override
  public UniformPrecedencePreservativeCrossover split() {
    // doesn't maintain any mutable state, so safe to return this
    return this;
  }

  /*
   * package private to facilitate testing
   */
  final void internalCross(int[] raw1, int[] raw2, boolean[] mask) {
    int[] old1 = raw1.clone();
    int[] old2 = raw2.clone();
    boolean[] used1 = new boolean[raw1.length];
    boolean[] used2 = new boolean[raw1.length];

    int i = 0;
    int j = 0;
    int x = 0;
    int y = 0;
    for (int k = 0; k < mask.length; k++) {
      if (mask[k]) {
        while (used1[old1[i]]) {
          i++;
        }
        while (used2[old2[j]]) {
          j++;
        }
        used1[raw1[k] = old1[i]] = true;
        used2[raw2[k] = old2[j]] = true;
        i++;
        j++;
      } else {
        while (used1[old2[x]]) {
          x++;
        }
        while (used2[old1[y]]) {
          y++;
        }
        used1[raw1[k] = old2[x]] = true;
        used2[raw2[k] = old1[y]] = true;
        x++;
        y++;
      }
    }
  }
}
