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

/** JUnit test cases for rank selection operators that use SUS. */
public class RankSelectionWithSUSTests extends SharedTestSelectionOperators {

  @Test
  public void testExponentialRankSUS() {
    double[] cValues = {Math.ulp(0.0), 0.25, 0.5, 0.75, 1.0 - Math.ulp(1.0)};
    for (double c : cValues) {
      ExponentialRankStochasticUniversalSampling selection =
          new ExponentialRankStochasticUniversalSampling(c);
      validateIndexes_Double(selection, c >= 0.5);
      validateIndexes_Integer(selection, c >= 0.5);
      ExponentialRankStochasticUniversalSampling selection2 = selection.split();
      validateIndexes_Double(selection2, c >= 0.5);
      validateIndexes_Integer(selection2, c >= 0.5);
    }
    double c = 0.5;
    ExponentialRankStochasticUniversalSampling selection =
        new ExponentialRankStochasticUniversalSampling(c);
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

      validateExpectedCountsSUSWithRanks(selection, f1);

      PopFitVectorInteger f2 = new PopFitVectorInteger(n);
      weights = selection.computeWeightRunningSum(f2);
      for (int i = n - 1; i > 0; i--) {
        double delta = weights[i] - weights[i - 1];
        double expectedDelta = Math.pow(c, n - (i + 1));
        assertEquals(expectedDelta, delta, 1E-10);
      }
      assertEquals(Math.pow(c, n - 1), weights[0], 1E-10);

      validateExpectedCountsSUSWithRanks(selection, f2);
    }

    PopFitVectorDoubleSimple f1 = new PopFitVectorDoubleSimple(100);
    validateExpectedCountsSUSWithRanks(selection, f1);
    PopFitVectorIntegerSimple f2 = new PopFitVectorIntegerSimple(100);
    validateExpectedCountsSUSWithRanks(selection, f2);

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new ExponentialRankStochasticUniversalSampling(0.0));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new ExponentialRankStochasticUniversalSampling(1.0));
  }

  @Test
  public void testLinearRankSUS() {
    double[] cValues = {1.0, 1.25, 1.5, 1.75, 2.0};
    for (double c : cValues) {
      LinearRankStochasticUniversalSampling selection =
          new LinearRankStochasticUniversalSampling(c);
      validateIndexes_Double(selection);
      validateIndexes_Integer(selection);
      LinearRankStochasticUniversalSampling selection2 = selection.split();
      validateIndexes_Double(selection2);
      validateIndexes_Integer(selection2);
    }
    LinearRankStochasticUniversalSampling selection =
        new LinearRankStochasticUniversalSampling(2.0);
    validateHigherFitnessSelectedMoreOften_Double(selection);
    validateHigherFitnessSelectedMoreOften_Integer(selection);

    LinearRankStochasticUniversalSampling selectionUniform =
        new LinearRankStochasticUniversalSampling(1.0);

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

      validateExpectedCountsSUSWithRanks(selection, f1);
      validateExpectedCountsSUSWithRanks(selectionUniform, f1);

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

      validateExpectedCountsSUSWithRanks(selection, f2);
      validateExpectedCountsSUSWithRanks(selectionUniform, f2);
    }

    PopFitVectorDoubleSimple f1 = new PopFitVectorDoubleSimple(100);
    validateExpectedCountsSUSWithRanks(selection, f1);
    validateExpectedCountsSUSWithRanks(selectionUniform, f1);
    PopFitVectorIntegerSimple f2 = new PopFitVectorIntegerSimple(100);
    validateExpectedCountsSUSWithRanks(selection, f2);
    validateExpectedCountsSUSWithRanks(selectionUniform, f2);

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new LinearRankStochasticUniversalSampling(2.0 + Math.ulp(2.0)));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new LinearRankStochasticUniversalSampling(1.0 - Math.ulp(1.0)));
  }

  private void validateExpectedCountsSUSWithRanks(
      StochasticUniversalSampling selection, PopulationFitnessVector.Integer pf) {
    int[] selected = new int[pf.size()];
    selection.select(pf, selected);
    int[] expectedMin = new int[pf.size()];
    int[] expectedMax = new int[pf.size()];
    int[] counts = new int[pf.size()];
    for (int i = 0; i < pf.size(); i++) {
      counts[selected[i]]++;
    }
    double[] runningSum = selection.computeWeightRunningSum(pf);
    expectedMin[0] = (int) (pf.size() * runningSum[0] / runningSum[runningSum.length - 1]);
    expectedMax[0] = (int) Math.ceil(pf.size() * runningSum[0] / runningSum[runningSum.length - 1]);
    for (int i = 1; i < pf.size(); i++) {
      expectedMin[i] =
          (int)
              (pf.size() * (runningSum[i] - runningSum[i - 1]) / runningSum[runningSum.length - 1]);
      expectedMax[i] =
          (int)
              Math.ceil(
                  pf.size()
                      * (runningSum[i] - runningSum[i - 1])
                      / runningSum[runningSum.length - 1]);
    }
    for (int i = 0; i < pf.size(); i++) {
      assertTrue(
          counts[i] >= expectedMin[i], "i:" + i + " count:" + counts[i] + " min:" + expectedMin[i]);
      assertTrue(
          counts[i] <= expectedMax[i], "i:" + i + " count:" + counts[i] + " max:" + expectedMax[i]);
    }
  }

  private void validateExpectedCountsSUSWithRanks(
      StochasticUniversalSampling selection, PopulationFitnessVector.Double pf) {
    int[] selected = new int[pf.size()];
    selection.select(pf, selected);
    int[] expectedMin = new int[pf.size()];
    int[] expectedMax = new int[pf.size()];
    int[] counts = new int[pf.size()];
    for (int i = 0; i < pf.size(); i++) {
      counts[selected[i]]++;
    }
    double[] runningSum = selection.computeWeightRunningSum(pf);
    expectedMin[0] = (int) (pf.size() * runningSum[0] / runningSum[runningSum.length - 1]);
    expectedMax[0] = (int) Math.ceil(pf.size() * runningSum[0] / runningSum[runningSum.length - 1]);
    for (int i = 1; i < pf.size(); i++) {
      expectedMin[i] =
          (int)
              (pf.size() * (runningSum[i] - runningSum[i - 1]) / runningSum[runningSum.length - 1]);
      expectedMax[i] =
          (int)
              Math.ceil(
                  pf.size()
                      * (runningSum[i] - runningSum[i - 1])
                      / runningSum[runningSum.length - 1]);
    }
    for (int i = 0; i < pf.size(); i++) {
      assertTrue(
          counts[i] >= expectedMin[i], "i:" + i + " count:" + counts[i] + " min:" + expectedMin[i]);
      assertTrue(
          counts[i] <= expectedMax[i], "i:" + i + " count:" + counts[i] + " max:" + expectedMax[i]);
    }
  }
}
