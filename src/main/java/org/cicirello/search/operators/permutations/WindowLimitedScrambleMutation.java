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

import java.util.random.RandomGenerator;
import org.cicirello.math.rand.RandomIndexer;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.MutationOperator;

/**
 * This class implements a window-limited version of the {@link ScrambleMutation} mutation operator
 * on permutations. A window-limited mutation operator on permutations is a mutation operator such
 * that there is a window constraint, w, on the random indexes used by the mutation. For a mutation
 * operator that uses a pair of random indexes, (i, j), these indexes are constrained such that
 * |i-j| &le; w. All index pairs that satisfy the window constraint are equally likely.
 *
 * <p>The runtime (worst case and average case) of the {@link #mutate(Permutation) mutate} method is
 * O(min(n,w)), where n is the length of the permutation. Since chosen indexes can be no more than w
 * positions apart, at most (w+1) elements are moved.
 *
 * <p>For further discussion of window limited permutation mutation operators see:<br>
 * V. A. Cicirello, <a href="https://www.cicirello.org/publications/cicirello2014bict.html"
 * target=_top>"On the Effects of Window-Limits on the Distance Profiles of Permutation Neighborhood
 * Operators,"</a> in Proceedings of the 8th International Conference on Bioinspired Information and
 * Communications Technologies, pages 28-35, December 2014. <a
 * href="https://www.cicirello.org/publications/cicirello-bict-2014.pdf">[PDF]</a> <a
 * href="https://www.cicirello.org/publications/cicirello2014bict.bib">[BIB]</a> <a
 * href="http://dl.acm.org/citation.cfm?id=2744531">[From the ACM Digital Library]</a>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class WindowLimitedScrambleMutation implements MutationOperator<Permutation> {

  private final int limit;
  private final int[] indexes;
  private final RandomGenerator.SplittableGenerator generator;

  /**
   * Constructs a WindowLimitedScrambleMutation mutation operator with a default window limit of
   * Integer.MAX_VALUE.
   */
  public WindowLimitedScrambleMutation() {
    this(Integer.MAX_VALUE);
  }

  /**
   * Constructs a WindowLimitedScrambleMutation mutation operator.
   *
   * @param windowLimit The window limit, which must be positive.
   * @throws IllegalArgumentException if windowLimit &le; 0
   */
  public WindowLimitedScrambleMutation(int windowLimit) {
    if (windowLimit <= 0) throw new IllegalArgumentException("window limit must be positive");
    limit = windowLimit;
    indexes = new int[2];
    generator = RandomnessFactory.createSplittableGenerator();
  }

  private WindowLimitedScrambleMutation(WindowLimitedScrambleMutation other) {
    limit = other.limit;
    generator = other.generator.split();
    indexes = new int[2];
  }

  @Override
  public void mutate(Permutation c) {
    if (c.length() >= 2) {
      generateIndexes(c.length(), indexes);
      c.scramble(indexes[0], indexes[1], generator);
    }
  }

  @Override
  public WindowLimitedScrambleMutation split() {
    return new WindowLimitedScrambleMutation(this);
  }

  /*
   * package access to support unit testing
   */
  void generateIndexes(int n, int[] indexes) {
    if (limit >= n) {
      RandomIndexer.nextIntPair(n, indexes, generator);
    } else {
      RandomIndexer.nextWindowedIntPair(n, limit, indexes, generator);
    }
  }
}
