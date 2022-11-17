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

package org.cicirello.search.operators.reals;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.search.representations.RealValued;
import org.cicirello.search.representations.RealVector;
import org.cicirello.search.representations.SingleReal;

/** Test code common to the test classes for the real-valued mutation operators. */
class SharedTestRealMutationOps {

  // We don't test the distribution of results.
  // Instead, we simply verify that mutation is capable of both increasing
  // and decreasing values.  This constant controls the max number of trials
  // executed in verifying this.  E.g., pass if at least 1 out of MAX_TRIALS
  // increases value, and if at least 1 out of MAX_TRIALS decreases.
  // A Cauchy is symmetric about 0.0, so approximately half of mutations
  // should decrease and approximately half should increase.
  static final int MAX_TRIALS = 100;

  void verifyMutate1(AbstractRealMutation<RealValued> m) {
    verifyMutate1(m, false);
  }

  void verifyMutate1(AbstractRealMutation<RealValued> m, boolean assertInterval) {
    verifyMutatesSingleBothDirections(m, assertInterval, MAX_TRIALS);
    for (int j = 0; j < 5; j++) {
      verifyMutatesVectorBothDirections(m, assertInterval, MAX_TRIALS, j, j);
    }
  }

  void verifyMutate1(AbstractRealMutation<RealValued> m, double p) {
    verifyMutate1(m, p, false);
  }

  void verifyMutate1(AbstractRealMutation<RealValued> m, double p, boolean assertInterval) {
    final int TRIALS = (int) (2 * MAX_TRIALS / p);
    verifyMutatesSingleBothDirections(m, assertInterval, TRIALS);
    final int N = m.length() > 3 ? 3 + m.length() : 6;
    for (int j = 0; j < N; j++) {
      verifyMutatesVectorBothDirections(m, assertInterval, TRIALS, j, j);
    }
  }

  void verifyMutate1(AbstractRealMutation<RealValued> m, int K) {
    verifyMutate1(m, K, false);
  }

  void verifyMutate1(AbstractRealMutation<RealValued> m, int K, boolean assertInterval) {
    verifyMutatesSingleBothDirections(m, assertInterval, MAX_TRIALS);
    final int N = m.length() > 3 ? 3 + m.length() : 6;
    for (int j = 0; j < N; j++) {
      final int TRIALS = (int) (2 * MAX_TRIALS / (K < j ? 1.0 * K / j : 1));
      verifyMutatesVectorBothDirections(m, assertInterval, TRIALS, j, K);
    }
  }

  private void verifyMutatesSingleBothDirections(
      AbstractRealMutation<RealValued> m, boolean assertInterval, int TRIALS) {
    int countLow = 0;
    int countHigh = 0;
    for (int i = 0; i < TRIALS && (countLow == 0 || countHigh == 0); i++) {
      SingleReal f = new SingleReal(9.0);
      m.mutate(f);
      if (assertInterval) {
        assertTrue(Math.abs(9.0 - f.get()) <= m.get(0));
      }
      if (f.get() < 9.0) countLow++;
      else if (f.get() > 9.0) countHigh++;
    }
    assertTrue(countLow > 0);
    assertTrue(countHigh > 0);
  }

  private void verifyMutatesVectorBothDirections(
      AbstractRealMutation<RealValued> m,
      boolean assertInterval,
      int TRIALS,
      int j,
      int MAX_COUNT) {
    double[] v = new double[j];
    for (int k = 0; k < j; k++) {
      v[k] = 9.0 - k;
    }
    final int z = j;
    int[] low = new int[z];
    int[] high = new int[z];
    for (int i = 0; i < TRIALS; i++) {
      RealVector f = new RealVector(v.clone());
      m.mutate(f);
      boolean done = true;
      int kCount = 0;
      for (int k = 0; k < j; k++) {
        if (assertInterval) {
          assertTrue(Math.abs(v[k] - f.get(k)) <= m.get(0));
        }
        if (f.get(k) < v[k]) {
          low[k]++;
          kCount++;
        }
        if (f.get(k) > v[k]) {
          high[k]++;
          kCount++;
        }
        if (low[k] == 0 || high[k] == 0) done = false;
      }
      assertTrue(kCount <= MAX_COUNT);
      if (done) break;
    }
    for (int k = 0; k < low.length; k++) {
      assertTrue(low[k] > 0);
      assertTrue(high[k] > 0);
    }
  }

  void verifyUndo(AbstractUndoableRealMutation<RealValued> m) {
    boolean changed = false;
    for (int i = 0; i < MAX_TRIALS; i++) {
      SingleReal f = new SingleReal(9.0);
      SingleReal f2 = f.copy();
      m.mutate(f);
      if (!f.equals(f2)) {
        changed = true;
      }
      m.undo(f);
      assertEquals(f2, f);
    }
    assertTrue(changed);
    for (int j = 0; j < 5; j++) {
      double[] v = new double[j];
      for (int k = 0; k < j; k++) {
        v[k] = 9.0 - k;
      }
      changed = false;
      for (int i = 0; i < MAX_TRIALS; i++) {
        RealVector f = new RealVector(v.clone());
        RealVector f2 = f.copy();
        m.mutate(f);
        if (!f.equals(f2)) {
          changed = true;
        }
        m.undo(f);
        assertEquals(f2, f);
      }
    }
    verifySplitUndo(m);
  }

  void verifySplitUndo(AbstractUndoableRealMutation<RealValued> mutationOriginal) {
    for (int i = 0; i < 10; i++) {
      SingleReal v1 = new SingleReal(9);
      SingleReal v2 = v1.copy();
      SingleReal v3 = v1.copy();
      mutationOriginal.mutate(v2);
      AbstractUndoableRealMutation<RealValued> mutation = mutationOriginal.split();
      mutation.mutate(v3);
      mutationOriginal.undo(v2);
      assertEquals(v1, v2);
      mutation.undo(v3);
      assertEquals(v1, v3);
    }
    for (int i = 0; i < 10; i++) {
      double[] vector = {2, 4, 8, 16, 32, 64, 128, 256};
      RealVector v1 = new RealVector(vector);
      RealVector v2 = v1.copy();
      RealVector v3 = v1.copy();
      mutationOriginal.mutate(v2);
      AbstractUndoableRealMutation<RealValued> mutation = mutationOriginal.split();
      mutation.mutate(v3);
      mutationOriginal.undo(v2);
      assertEquals(v1, v2);
      mutation.undo(v3);
      assertEquals(v1, v3);
    }
  }
}
