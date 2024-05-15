/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2024 Vincent A. Cicirello
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

import java.util.Arrays;
import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.PermutationFullBinaryOperator;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.CrossoverOperator;

/**
 * Implementation of position based crossover (PBX). The PBX operator attempts to cause children to
 * inherit most element positions from the parents, and in particular such that each child gets the
 * positions of equal number of elements (on average) from each of its parents.
 *
 * <p>To illustrate its behavior, consider the following example. Let parent p1 = [2, 5, 1, 4, 3, 0]
 * and parent p2 = [5, 4, 3, 2, 1, 0]. Step 1 generates a list that maps each element to its indexes
 * in the parents. From the above, we'd have the element to index mapping: [ 0 : {5, 5}, 1 : {2, 4},
 * 2 : {0, 3}, 3 : {4, 2}, 4 : {3, 1}, 5 : {1, 0}]. Randomize the ordering of this list of element
 * to index mappings: [3 : {4, 2}, 5 : {1, 0}, 0 : {5, 5}, 2 : {0, 3}, 1 : {2, 4}, 4 : {3, 1}]. Pick
 * a random subset of elements, with each element equally likely chosen (on average n/2 elements
 * will be chosen). For this example, imagine that elements 5 and 1 were chosen. For each of these,
 * in the element to index mappings list, swap the two indexes. For example, element 5 currently has
 * the mapping: 5 : {1, 0}. Swap the indexes to instead get: 5 : {0, 1}. Doing this for both
 * elements 5 and 1 leads to: [3 : {4, 2}, 5 : {0, 1}, 0 : {5, 5}, 2 : {0, 3}, 1 : {4, 2}, 4 : {3,
 * 1}]. In this list of element to index mappings, the first index for each element is where we will
 * attempt to put that element in child c1, and the second is where we will attempt to put it in c2.
 * Proceed as follows. Initialize empty children c1 = [x, x, x, x, x, x] and c2 = [x, x, x, x, x,
 * x]. Iterate over the mappings and if the corresponding index in a child is empty, place the
 * element there. Otherwise skip that element in that child for now. In this case, we'll get: c1 =
 * [5, x, x, 4, 3, 0] and c2 = [x, 5, 3, 2, x, 0]. Next, using the same ordering, attempt to put the
 * missing elements in the opposite index. For example, c1 is missing element 2. Its mapping is {0,
 * 3}. We failed to put it at index 0 because the 5 was already there in c1. So we would attempt to
 * put it at index 3. However, c1 already has 4 there, so we will again skip element 2 for now.
 * Element 1 is mapped to the indexes {4, 2}. We couldn't put it at index 4 in c1 because the 3 is
 * there, and we couldn't put it at index 2 in c2 because the 3 is there. So try the opposite
 * indexes. Both of which work, so we now have: c1 = [5, x, 1, 4, 3, 0] and c2 = [x, 5, 3, 2, 1, 0].
 * The next element in the ordered mapping list is 4, which we failed to put in c2 at index 3
 * because the 2 is there, so we would instead try to put it at the other index 1, but that index is
 * also taken. The last stage fills in any missing elements. It again uses the ordered list, and it
 * fills them into any empty indexes left to right. In this case, there is only one missing element
 * in each, so those elements go in the only remaining spots: c1 = [5, 2, 1, 4, 3, 0] and c2 = [4,
 * 5, 3, 2, 1, 0].
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(n), where n is the length of the
 * permutations.
 *
 * <p>The PBX operator was introduced in the following paper:
 *
 * <p>T. Barecke and M. Detyniecki, Memetic algorithms for inexact graph matching. <i>2007 IEEE
 * Congress on Evolutionary Computation</i>, Singapore, 2007, pp. 4238-4245, doi:
 * 10.1109/CEC.2007.4425024.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class PositionBasedCrossover
    implements CrossoverOperator<Permutation>, PermutationFullBinaryOperator {

  private final EnhancedSplittableGenerator generator;

  /** Constructs a position based crossover (PBX) operator. */
  public PositionBasedCrossover() {
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
  }

  private PositionBasedCrossover(PositionBasedCrossover other) {
    generator = other.generator.split();
  }

  @Override
  public void cross(Permutation c1, Permutation c2) {
    c1.apply(this, c2);
  }

  @Override
  public PositionBasedCrossover split() {
    return new PositionBasedCrossover(this);
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
    // Comments are numbered steps from paper describing algorithm
    // 1. mapping alleles to the positions in the 2 parents
    int[] targetIndexes1 = p1.getInverse();
    int[] targetIndexes2 = p2.getInverse();
    // 2. shuffled order of elements for use in traversing the above
    Permutation order = new Permutation(raw1.length, generator);
    // 2. also step 2: arbitrarily select subset of alleles.
    //    Note that paper doesn't explicitly indicate this, but assumed
    //    intended to be equally likely chosen. Switch the target indexes
    //    for these elements.
    for (int e : generator.sample(raw1.length, 0.5)) {
      int temp = targetIndexes1[e];
      targetIndexes1[e] = targetIndexes2[e];
      targetIndexes2[e] = temp;
    }
    // 3. Form the children traversing the list formed in 1 in the order of 2
    //    taking element position from the target indexes lists.
    //    In event of collisions, keep track of colliding elements.
    // 4. Try to put unused alleles in position from the other parent
    //    and keep track of any that still collide.
    // 5. Fill in with still unused alleles in same traversal order
    int[][] unused1 = new int[raw1.length][2];
    int[][] unused2 = new int[raw2.length][2];
    fillRemainingUnusedAlleles(
        raw1,
        unused1,
        fillUnusedAllelesFromOppositeParentPosition(
            raw1, unused1, fillFromTargets(raw1, order, unused1, targetIndexes1, targetIndexes2)));
    fillRemainingUnusedAlleles(
        raw2,
        unused2,
        fillUnusedAllelesFromOppositeParentPosition(
            raw2, unused2, fillFromTargets(raw2, order, unused2, targetIndexes2, targetIndexes1)));
  }

  private int fillFromTargets(
      int[] raw, Permutation order, int[][] unused, int[] targetIndexes, int[] alternateIndexes) {
    // 3. Form the children traversing the list formed in step 1 in the order of step 2
    //    taking element position from the target indexes lists.
    //    In event of collisions, keep track of colliding elements.
    int size = 0;
    Arrays.fill(raw, -1);
    for (int i = 0; i < raw.length; i++) {
      int e = order.get(i);
      if (raw[targetIndexes[e]] < 0) {
        raw[targetIndexes[e]] = e;
      } else {
        unused[size][0] = e;
        unused[size][1] = alternateIndexes[e];
        size++;
      }
    }
    return size;
  }

  private int fillUnusedAllelesFromOppositeParentPosition(int[] raw, int[][] unused, int size) {
    // 4. Try to put unused alleles in position from the other parent
    //    and keep track of any that still collide.
    int countUnused = 0;
    for (int i = 0; i < size; i++) {
      int e = unused[i][0];
      int index = unused[i][1];
      if (raw[index] < 0) {
        raw[index] = e;
      } else {
        unused[countUnused][0] = e;
        countUnused++;
      }
    }
    return countUnused;
  }

  private void fillRemainingUnusedAlleles(int[] raw, int[][] unused, int size) {
    // 5. Fill in with still unused alleles in same traversal order
    int open = 0;
    for (int i = 0; i < size; i++) {
      int e = unused[i][0];
      for (; raw[open] >= 0; open++) {
        // deliberately empty block
      }
      raw[open] = e;
    }
  }
}
