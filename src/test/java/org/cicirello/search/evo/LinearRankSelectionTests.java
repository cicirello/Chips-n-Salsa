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

package org.cicirello.search.evo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/** JUnit test cases for LinearRankSelection. */
public class LinearRankSelectionTests extends SharedTestSelectionOperators {

  @Test
  public void testLinearRankSelection() {
    double[] cValues = {1.0, 1.25, 1.5, 1.75, 2.0};
    for (double c : cValues) {
      LinearRankSelection selection = new LinearRankSelection(c);
      validateIndexes_Double(selection);
      validateIndexes_Integer(selection);
      LinearRankSelection selection2 = selection.split();
      validateIndexes_Double(selection2);
      validateIndexes_Integer(selection2);
    }
    LinearRankSelection selection = new LinearRankSelection(2.0);
    // Following two checks may sporadically fail due to random chance.
    // Increase 2nd parameter to decrease probability of such failure (keep it even though).
    validateHigherFitnessSelectedMoreOften_Double(selection, 40);
    validateHigherFitnessSelectedMoreOften_Integer(selection, 40);

    LinearRankSelection selectionUniform = new LinearRankSelection(1.0);

    for (int n = 2; n <= 8; n *= 2) {
      PopFitVectorDouble f1 = new PopFitVectorDouble(n);
      double[] weights = selection.computeWeightRunningSum(f1);
      assertEquals(0.0, weights[0], 1E-10);
      assertEquals(n, weights[n - 1], 1E-10);
      double expectedDelta = 2.0;
      assertEquals(expectedDelta, weights[n - 1] - weights[n - 2], 1E-10);
      for (int i = n - 2; i > 0; i--) {
        double delta = weights[i] - weights[i - 1];
        assertTrue(delta <= expectedDelta);
        expectedDelta = delta;
      }

      weights = selectionUniform.computeWeightRunningSum(f1);
      for (int i = 0; i < weights.length; i++) {
        assertEquals(i + 1.0, weights[i], 1E-10);
      }

      PopFitVectorInteger f2 = new PopFitVectorInteger(n);
      weights = selection.computeWeightRunningSum(f2);
      assertEquals(0.0, weights[0], 1E-10);
      assertEquals(n, weights[n - 1], 1E-10);
      expectedDelta = 2.0;
      assertEquals(expectedDelta, weights[n - 1] - weights[n - 2], 1E-10);
      for (int i = n - 2; i > 0; i--) {
        double delta = weights[i] - weights[i - 1];
        assertTrue(delta <= expectedDelta);
        expectedDelta = delta;
      }

      weights = selectionUniform.computeWeightRunningSum(f2);
      for (int i = 0; i < weights.length; i++) {
        assertEquals(i + 1.0, weights[i], 1E-10);
      }
    }

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LinearRankSelection(2.0 + Math.ulp(2.0)));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new LinearRankSelection(1.0 - Math.ulp(1.0)));
  }
}
