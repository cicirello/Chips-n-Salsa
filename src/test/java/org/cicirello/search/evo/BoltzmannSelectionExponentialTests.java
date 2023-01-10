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

package org.cicirello.search.evo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/** JUnit test cases for BoltzmannSelection. */
public class BoltzmannSelectionExponentialTests {

  @Test
  public void testBoltzmannSelectionExponential() {
    double[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());

    BoltzmannSelection selection = new BoltzmannSelection(1.0, 0.1, 0.5, false);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 0.0;
    double div = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    BoltzmannSelection split = selection.split();
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 0.1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }

    // Use split copy
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 0.1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }

    // reinitialize and use original
    selection.init(500);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 0.1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoltzmannSelection(10.0, 0.0, 1.0, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoltzmannSelection(0.09, 0.1, 1.0, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoltzmannSelection(5.0, 0.1, 0.0, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoltzmannSelection(5.0, 0.1, 1.0, false));
  }

  @Test
  public void testBoltzmannSelectionExponentialInteger() {
    int[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());

    BoltzmannSelection selection = new BoltzmannSelection(1.0, 0.1, 0.5, false);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 0.0;
    double div = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    BoltzmannSelection split = selection.split();
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 0.1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }

    // Use split copy
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 0.1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }

    // reinitialize and use original
    selection.init(500);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div *= 0.5;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div = 0.1;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
  }
}
