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

/** JUnit tests for HeuristicBiasedStochasticSampling not dependent on cost type. */
public class HBSSCommonTests extends SharedTestStochasticSampler {

  @Test
  public void testPrecomputeBiases() {
    for (int n = 0; n < 8; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      HeuristicBiasedStochasticSampling<Permutation> ch =
          new HeuristicBiasedStochasticSampling<Permutation>(h);
      double[] biases = ch.precomputeBiases(n);
      double expected = 0;
      for (int i = 1; i <= n; i++) {
        expected += 1.0 / i;
        assertEquals(expected, biases[i - 1], 1E-10);
      }
    }
    for (int n = 0; n < 8; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      HeuristicBiasedStochasticSampling<Permutation> ch =
          new HeuristicBiasedStochasticSampling<Permutation>(h, 2.0);
      double[] biases = ch.precomputeBiases(n);
      double expected = 0;
      for (int i = 1; i <= n; i++) {
        expected += 1.0 / (i * i);
        assertEquals(expected, biases[i - 1], 1E-10);
      }
    }
    for (int n = 0; n < 8; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      HeuristicBiasedStochasticSampling<Permutation> ch =
          new HeuristicBiasedStochasticSampling<Permutation>(h, true);
      double[] biases = ch.precomputeBiases(n);
      double expected = 0;
      for (int i = 1; i <= n; i++) {
        expected += Math.exp(-i);
        assertEquals(expected, biases[i - 1], 1E-10);
      }
    }
    HeuristicBiasedStochasticSampling.BiasFunction bias =
        new HeuristicBiasedStochasticSampling.BiasFunction() {
          @Override
          public double bias(int rank) {
            return 1.0 / (rank * rank);
          }
        };
    for (int n = 0; n < 8; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      HeuristicBiasedStochasticSampling<Permutation> ch =
          new HeuristicBiasedStochasticSampling<Permutation>(h, bias);
      double[] biases = ch.precomputeBiases(n);
      double expected = 0;
      for (int i = 1; i <= n; i++) {
        expected += 1.0 / (i * i);
        assertEquals(expected, biases[i - 1], 1E-10);
      }
    }
  }

  @Test
  public void testSelect() {
    for (int n = 2; n < 8; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      HeuristicBiasedStochasticSampling<Permutation> ch =
          new HeuristicBiasedStochasticSampling<Permutation>(h);
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

  @Test
  public void testRandomizedSelect() {
    for (int n = 0; n < 8; n++) {
      IntProblem problem = new IntProblem();
      IntHeuristic h = new IntHeuristic(problem, n);
      HeuristicBiasedStochasticSampling<Permutation> ch =
          new HeuristicBiasedStochasticSampling<Permutation>(h);
      for (int k = 1; k <= n; k++) {
        double[] values = new double[n];
        for (int i = 0; i < k; i++) values[i] = 100 - i;
        for (int i = k; i < n; i++) values[i] = 9999;
        for (int chosenRank = 1; chosenRank <= k; chosenRank++) {
          int[] indexes = new int[n];
          for (int i = 0; i < n; i++) indexes[i] = i;
          String message = "Decreasing: chosenRank:" + chosenRank + " k:" + k;
          assertEquals(
              chosenRank - 1, ch.randomizedSelect(indexes, values, k, chosenRank), message);
        }
      }
      for (int k = 1; k <= n; k++) {
        double[] values = new double[n];
        for (int i = 0; i < k; i++) values[i] = 2 + i;
        for (int i = k; i < n; i++) values[i] = 9999;
        for (int chosenRank = 1; chosenRank <= k; chosenRank++) {
          int[] indexes = new int[n];
          for (int i = 0; i < n; i++) indexes[i] = i;
          String message = "Increasing: chosenRank:" + chosenRank + " k:" + k;
          assertEquals(
              k - chosenRank, ch.randomizedSelect(indexes, values, k, chosenRank), message);
        }
      }
    }
  }
}
