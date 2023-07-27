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

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit test cases for OX. */
public class OXTests extends SharedTestCodeOrderingCrossovers {

  // Insert @Test here to activate during testing to visually inspect cross results
  public void visuallyInspectCrossResult() {
    visualInspection(3, new OrderCrossover());
  }

  @Test
  public void testOX() {
    OrderCrossover ox = new OrderCrossover();
    for (int n = 1; n <= 64; n *= 2) {
      for (int s = 0; s < NUM_SAMPLES; s++) {
        Permutation p1 = new Permutation(n);
        Permutation p2 = new Permutation(n);
        Permutation parent1 = new Permutation(p1);
        Permutation parent2 = new Permutation(p2);
        ox.cross(parent1, parent2);
        assertTrue(validPermutation(parent1));
        assertTrue(validPermutation(parent2));
        // if (n >= 32) {
        //	boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
        //	int[] startAndEnd = findStartAndEnd(fixedPoints);
        //	validateOrderingOX(parent1, p2, startAndEnd);
        //	validateOrderingOX(parent2, p1, startAndEnd);
        // }
      }
    }
    OrderCrossover s = ox.split();
    assertNotSame(ox, s);
    final int n = 2000;
    final int RUNS = 4;
    boolean passed = false;
    for (int run = 0; run < RUNS && !passed; run++) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      ox.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
      boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
      int[] startAndEnd = findStartAndEnd(fixedPoints);
      passed =
          validateOrderingOX(parent1, p2, startAndEnd)
              && validateOrderingOX(parent2, p1, startAndEnd);
    }
    // The following may on infrequent occasions exhibit a false failure.
    // This is due to the above findStartAndEnd heuristically guessing what
    // the random cross region was. I believe the probability of a false
    // failure is very approximately (2/n)^RUNS, which for 3 runs of n=2000 is about 1 in
    // 1000000000. Rerun if fails.
    assertTrue(
        passed,
        "This may infrequently result in a false failure because test case heuristically guesses where the random cross region was. Rerun if fails.");
    // assertTrue(validateOrderingOX(parent1, p2, startAndEnd), "This may infrequently result in a
    // false failure because test case heuristically guesses where the random cross region was.
    // Rerun if fails.");
    // assertTrue(validateOrderingOX(parent2, p1, startAndEnd), "This may infrequently result in a
    // false failure because test case heuristically guesses where the random cross region was.
    // Rerun if fails.");
    passed = false;
    for (int run = 0; run < RUNS && !passed; run++) {
      Permutation p1 = new Permutation(n);
      Permutation p2 = new Permutation(n);
      Permutation parent1 = new Permutation(p1);
      Permutation parent2 = new Permutation(p2);
      s.cross(parent1, parent2);
      assertTrue(validPermutation(parent1));
      assertTrue(validPermutation(parent2));
      boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
      int[] startAndEnd = findStartAndEnd(fixedPoints);
      passed =
          validateOrderingOX(parent1, p2, startAndEnd)
              && validateOrderingOX(parent2, p1, startAndEnd);
    }
    // The following may on infrequent occasions exhibit a false failure.
    // This is due to the above findStartAndEnd heuristically guessing what
    // the random cross region was. I believe the probability of a false
    // failure is very approximately (2/n)^RUNS, which for 3 runs of n=2000 is about 1 in
    // 1000000000. Rerun if fails.
    assertTrue(
        passed,
        "This may infrequently result in a false failure because test case heuristically guesses where the random cross region was. Rerun if fails.");
    // assertTrue(validateOrderingOX(parent1, p2, startAndEnd), "This may infrequently result in a
    // false failure because test case heuristically guesses where the random cross region was.
    // Rerun if fails.");
    // assertTrue(validateOrderingOX(parent2, p1, startAndEnd), "This may infrequently result in a
    // false failure because test case heuristically guesses where the random cross region was.
    // Rerun if fails.");
  }

  private boolean validateOrderingOX(Permutation child, Permutation order, int[] startAndEnd) {
    int[] inv = order.getInverse();
    boolean result = true;
    for (int i = startAndEnd[1] + 2; i < inv.length; i++) {
      result = result && inv[child.get(i)] > inv[child.get(i - 1)];
      // assertTrue(inv[child.get(i)] > inv[child.get(i-1)], "This may infrequently result in a
      // false failure because test case heuristically guesses where the random cross region was.
      // Rerun if fails.");
    }
    for (int i = 1; i < startAndEnd[0]; i++) {
      result = result && inv[child.get(i)] > inv[child.get(i - 1)];
      // assertTrue(inv[child.get(i)] > inv[child.get(i-1)], "This may infrequently result in a
      // false failure because test case heuristically guesses where the random cross region was.
      // Rerun if fails.");
    }
    if (0 < startAndEnd[0] && inv.length - 1 > startAndEnd[1]) {
      result = result && inv[child.get(0)] > inv[child.get(inv.length - 1)];
      // assertTrue(inv[child.get(0)] > inv[child.get(inv.length-1)], "This may infrequently result
      // in a false failure because test case heuristically guesses where the random cross region
      // was. Rerun if fails.");
    }
    return result;
  }
}
