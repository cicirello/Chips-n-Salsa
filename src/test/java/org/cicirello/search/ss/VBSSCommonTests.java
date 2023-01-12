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

package org.cicirello.search.ss;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit tests for ValueBiasedStochasticSampling not dependent on cost type. */
public class VBSSCommonTests extends SharedTestStochasticSampler {

  @Test
  public void testExponentialBiasFunction() {
    ValueBiasedStochasticSampling.BiasFunction bias =
        ValueBiasedStochasticSampling.createExponentialBias(0.25);
    double[] expected = {1, Math.exp(1), Math.exp(2), Math.exp(3)};
    for (int v = 0, i = 0; v <= 12; v += 4, i++) {
      assertEquals(expected[i], bias.bias(v), 1E-10);
    }
  }

  @Test
  public void testAdjustForBias() {
    for (int n = 2; n < 8; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      ValueBiasedStochasticSampling<Permutation> ch =
          new ValueBiasedStochasticSampling<Permutation>(h);
      for (int k = 2; k <= n; k++) {
        double[] values = new double[n];
        for (int i = 0, j = 1; i < k; i++, j *= 2) {
          values[k - 1 - i] = j;
        }
        ch.adjustForBias(values, k);
        assertEquals(1.0, values[k - 1], 1E-10);
        for (int i = k - 3; i >= 0; i--) {
          assertEquals(2 * (values[i + 2] - values[i + 1]), values[i + 1] - values[i], 1E-10);
        }
      }
    }
  }

  @Test
  public void testSelect() {
    for (int n = 2; n < 8; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      ValueBiasedStochasticSampling<Permutation> ch =
          new ValueBiasedStochasticSampling<Permutation>(h);
      for (int k = 2; k <= n; k++) {
        double inc = 1.0 / k;
        double[] values = new double[n];
        values[0] = inc;
        for (int i = 1; i < k; i++) {
          values[i] = values[i - 1] + inc;
        }
        double u = 0.0;
        for (int i = 0; i < k; i++, u += inc) {
          assertEquals(i, ch.select(values, k, u));
        }
        u = inc / 2;
        for (int i = 0; i < k; i++, u += inc) {
          assertEquals(i, ch.select(values, k, u));
        }
        u = 1.0 - 1E-10;
        for (int i = k - 1; i >= 0; i--, u -= inc) {
          assertEquals(i, ch.select(values, k, u));
        }
      }
    }
  }
}
