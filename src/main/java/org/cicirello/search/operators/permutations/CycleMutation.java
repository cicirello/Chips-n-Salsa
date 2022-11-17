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
 * This class implements the Cycle(kmax) form of cycle mutation on permutations, where one mutation
 * generates a random permutation cycle. Given the original parent permutation and its mutant, a
 * permutation cycle can be defined as follows. Imagine a graph with n vertexes, where n is the
 * permutation length. Now consider that for each index i, we define an edge in that graph between
 * vertex parent[i] and vertex mutant[i]. A permutation cycle consists of all of the elements from
 * one of the cycles in that graph. The length of a cycle is the number of elements in it. Consider
 * an example permutation, p1 = [0, 1, 2, 3, 4], and another permutation, p2 = [0, 3, 2, 1, 4]. This
 * pair of permutations has a 2-cycle (i.e., a cycle of length 2) consisting of elements 1 and 3.
 * Consider a second example, p1 = [0, 1, 2, 3, 4], and p2 = [0, 4, 2, 1, 3]. This example has a
 * 3-cycle consisting of elements 1, 3, and 4. Notice that position 1 has elements 1 and 4, position
 * 4 has elements 4 and 3, and position 3 has elements 3 and 1, so in the hypothetical graph
 * described above, there would be an edges from 1 to 4, 4 to 3, and 3 to 1, a cycle of length 3.
 *
 * <p>The Cycle(kmax) version of cycle mutation chooses the cycle size k uniformly at random from
 * [2, kmax], and then creates a random k-element cycle. The kmax is a parameter allowing the
 * evolutionary algorithm implementer the ability to control the allowed range of cycle sizes. The
 * combination of k elements is chosen uniformly at random from all possible combinations of k
 * elements. Note that a 2-cycle is simply a swap.
 *
 * <p>The runtime of the {@link #mutate(Permutation) mutate} method is O(min(n, kmax<sup>2</sup>)),
 * and derives from the combination of algorithms utilized by the {@link RandomSampler
 * RandomSampler} class in sampling k random integers. For small values of kmax, the runtime is
 * essentially constant. The runtime of the {@link #undo(Permutation) undo} method is O(kmax).
 *
 * <p>Cycle mutation in both of its forms, including Cycle(kmax), was introduced in the following
 * article:
 *
 * <p>Vincent A. Cicirello. 2022. <a
 * href="https://www.cicirello.org/publications/applsci-12-05506.pdf">Cycle Mutation: Evolving
 * Permutations via Cycle Induction</a>, <i>Applied Sciences</i>, 12(11), Article 5506 (June 2022).
 * doi:<a href="https://doi.org/10.3390/app12115506">10.3390/app12115506</a>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class CycleMutation implements UndoableMutationOperator<Permutation> {

  private int[] indexes;
  private final int bound;

  /**
   * Constructs an CycleMutation mutation operator.
   *
   * @param kmax The maximum length cycle to generate, which must be at least 2.
   * @throws IllegalArgumentException if kmax &lt; 2.
   */
  public CycleMutation(int kmax) {
    if (kmax < 2) throw new IllegalArgumentException("kmax too low");
    bound = kmax - 1;
  }

  @Override
  public final void mutate(Permutation c) {
    if (c.length() >= 2) {
      indexes =
          RandomSampler.sample(
              c.length(),
              2 + RandomIndexer.nextInt(bound < c.length() ? bound : c.length() - 1),
              (int[]) null);
      if (indexes.length > 2) {
        // randomize order of indexes if there are more than 2 of them
        // (no need to randomize order if only 2 indexes)
        for (int j = indexes.length - 1; j > 0; j--) {
          int i = RandomIndexer.nextInt(j + 1);
          if (i != j) {
            int temp = indexes[i];
            indexes[i] = indexes[j];
            indexes[j] = temp;
          }
        }
      }
      c.cycle(indexes);
    }
  }

  @Override
  public final void undo(Permutation c) {
    if (c.length() >= 2) {
      if (indexes.length > 2) {
        for (int i = 0, j = indexes.length - 1; i < j; i++, j--) {
          int temp = indexes[i];
          indexes[i] = indexes[j];
          indexes[j] = temp;
        }
      }
      c.cycle(indexes);
    }
  }

  @Override
  public CycleMutation split() {
    return new CycleMutation(bound + 1);
  }
}
