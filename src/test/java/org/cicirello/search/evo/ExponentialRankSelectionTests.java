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

/** JUnit test cases for ExponentialRankSelection. */
public class ExponentialRankSelectionTests extends SharedTestSelectionOperators {

  @Test
  public void testExponentialRankSelection() {
    double[] cValues = {Math.ulp(0.0), 0.25, 0.5, 0.75, 1.0 - Math.ulp(1.0)};
    for (double c : cValues) {
      ExponentialRankSelection selection = new ExponentialRankSelection(c);
      validateIndexes_Double(selection, c >= 0.5);
      validateIndexes_Integer(selection, c >= 0.5);
      ExponentialRankSelection selection2 = selection.split();
      validateIndexes_Double(selection2, c >= 0.5);
      validateIndexes_Integer(selection2, c >= 0.5);
    }
    double c = 0.5;
    ExponentialRankSelection selection = new ExponentialRankSelection(c);
    validateHigherFitnessSelectedMoreOften_Double(selection);
    validateHigherFitnessSelectedMoreOften_Integer(selection);

    for (int n = 2; n <= 8; n *= 2) {
      PopFitVectorDouble f1 = new PopFitVectorDouble(n);
      double[] weights = selection.computeWeightRunningSum(f1);
      for (int i = n - 1; i > 0; i--) {
        double delta = weights[i] - weights[i - 1];
        double expectedDelta = Math.pow(c, n - (i + 1));
        assertEquals(expectedDelta, delta, 1E-10);
      }
      assertEquals(Math.pow(c, n - 1), weights[0], 1E-10);

      PopFitVectorInteger f2 = new PopFitVectorInteger(n);
      weights = selection.computeWeightRunningSum(f2);
      for (int i = n - 1; i > 0; i--) {
        double delta = weights[i] - weights[i - 1];
        double expectedDelta = Math.pow(c, n - (i + 1));
        assertEquals(expectedDelta, delta, 1E-10);
      }
      assertEquals(Math.pow(c, n - 1), weights[0], 1E-10);
    }

    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new ExponentialRankSelection(0.0));
    thrown = assertThrows(IllegalArgumentException.class, () -> new ExponentialRankSelection(1.0));
  }
}
