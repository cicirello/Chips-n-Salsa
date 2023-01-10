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

/** JUnit test cases for Boltzmann selection and variations. */
public class BoltzmannSelectionTests {

  // TESTS FOR THE STANDARD FITNESS PROPORTIONAL VERSION

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

  @Test
  public void testBoltzmannSelectionLinear() {
    double[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());

    BoltzmannSelection selection = new BoltzmannSelection(4.0, 0.1, 1.0, true);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 0.0;
    double div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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
    div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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
    div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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
            IllegalArgumentException.class, () -> new BoltzmannSelection(10.0, 0.0, 1.0, true));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoltzmannSelection(0.09, 0.1, 1.0, true));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoltzmannSelection(5.0, 0.1, 0.0, true));
  }

  @Test
  public void testBoltzmannSelectionLinearInteger() {
    int[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());

    BoltzmannSelection selection = new BoltzmannSelection(4.0, 0.1, 1.0, true);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 0.0;
    double div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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
    div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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
    div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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

  @Test
  public void testBoltzmannSelectionConstant() {
    double[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());

    BoltzmannSelection selection = new BoltzmannSelection(1.0);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    BoltzmannSelection split = selection.split();
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }

    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    split.init(1000);
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }

    selection = new BoltzmannSelection(0.5);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }

    selection = new BoltzmannSelection(2.0);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }

    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new BoltzmannSelection(0.0));
  }

  @Test
  public void testBoltzmannSelectionConstantInteger() {
    int[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());

    BoltzmannSelection selection = new BoltzmannSelection(1.0);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    BoltzmannSelection split = selection.split();
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }

    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    split.init(1000);
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }

    selection = new BoltzmannSelection(0.5);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }

    selection = new BoltzmannSelection(2.0);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }

    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new BoltzmannSelection(0.0));
  }

  // TESTS FOR THE SUS VERSION

  @Test
  public void testBoltzmannStochasticUniversalSamplingExponential() {
    double[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());

    BoltzmannStochasticUniversalSampling selection =
        new BoltzmannStochasticUniversalSampling(1.0, 0.1, 0.5, false);
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
    BoltzmannStochasticUniversalSampling split = selection.split();
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
            IllegalArgumentException.class,
            () -> new BoltzmannStochasticUniversalSampling(10.0, 0.0, 1.0, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new BoltzmannStochasticUniversalSampling(0.09, 0.1, 1.0, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new BoltzmannStochasticUniversalSampling(5.0, 0.1, 0.0, false));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new BoltzmannStochasticUniversalSampling(5.0, 0.1, 1.0, false));
  }

  @Test
  public void testBoltzmannStochasticUniversalSamplingExponentialInteger() {
    int[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());

    BoltzmannStochasticUniversalSampling selection =
        new BoltzmannStochasticUniversalSampling(1.0, 0.1, 0.5, false);
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
    BoltzmannStochasticUniversalSampling split = selection.split();
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

  @Test
  public void testBoltzmannStochasticUniversalSamplingLinear() {
    double[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());

    BoltzmannStochasticUniversalSampling selection =
        new BoltzmannStochasticUniversalSampling(4.0, 0.1, 1.0, true);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 0.0;
    double div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    BoltzmannStochasticUniversalSampling split = selection.split();
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
    div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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
    div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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
            IllegalArgumentException.class,
            () -> new BoltzmannStochasticUniversalSampling(10.0, 0.0, 1.0, true));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new BoltzmannStochasticUniversalSampling(0.09, 0.1, 1.0, true));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new BoltzmannStochasticUniversalSampling(5.0, 0.1, 0.0, true));
  }

  @Test
  public void testBoltzmannStochasticUniversalSamplingLinearInteger() {
    int[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());

    BoltzmannStochasticUniversalSampling selection =
        new BoltzmannStochasticUniversalSampling(4.0, 0.1, 1.0, true);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 0.0;
    double div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    BoltzmannStochasticUniversalSampling split = selection.split();
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
    div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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
    div = 4;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
    for (int i = 0; i < fitnesses.length; i++) {
      expected += Math.exp(fitnesses[i] / div);
      assertEquals(expected, weightedSum[i]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 0.0;
    div -= 1.0;
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

  @Test
  public void testBoltzmannStochasticUniversalSamplingConstant() {
    double[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Double vector = PopulationFitnessVector.Double.of(fitnesses.clone());

    BoltzmannStochasticUniversalSampling selection = new BoltzmannStochasticUniversalSampling(1.0);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    BoltzmannStochasticUniversalSampling split = selection.split();
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }

    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    split.init(1000);
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }

    selection = new BoltzmannStochasticUniversalSampling(0.5);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }

    selection = new BoltzmannStochasticUniversalSampling(2.0);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoltzmannStochasticUniversalSampling(0.0));
  }

  @Test
  public void testBoltzmannStochasticUniversalSamplingConstantInteger() {
    int[] fitnesses = {0, 1, 2, 3, 4, 5};
    PopulationFitnessVector.Integer vector = PopulationFitnessVector.Integer.of(fitnesses.clone());

    BoltzmannStochasticUniversalSampling selection = new BoltzmannStochasticUniversalSampling(1.0);
    double[] weightedSum = selection.computeWeightRunningSum(vector);
    double expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    BoltzmannStochasticUniversalSampling split = selection.split();
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }

    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }
    split.init(1000);
    weightedSum = split.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(fitnesses[i + 1]);
    }

    selection = new BoltzmannStochasticUniversalSampling(0.5);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(2 * fitnesses[i + 1]);
    }

    selection = new BoltzmannStochasticUniversalSampling(2.0);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }
    selection.init(1000);
    weightedSum = selection.computeWeightRunningSum(vector);
    expected = 1;
    for (int i = 0; i < fitnesses.length; i++) {
      assertEquals(expected, weightedSum[i]);
      if (i < fitnesses.length - 1) expected += Math.exp(0.5 * fitnesses[i + 1]);
    }

    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new BoltzmannStochasticUniversalSampling(0.0));
  }
}
