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
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.problems.OptimizationProblem;
import org.junit.jupiter.api.*;

/** Common to test cases for HBSS, VBSS, etc. */
public class SharedTestStochasticSampler {

  /*
   * Fake heuristic designed for predictable test cases:
   * designed to prefer even permutation elements (largest to smallest), followed by odd
   * (largest to smallest).
   */
  static class IntHeuristicNullIncremental extends IntHeuristic {
    public IntHeuristicNullIncremental(IntProblem problem, int n) {
      super(problem, n);
    }

    public IntHeuristicNullIncremental(IntProblem problem, int n, int alpha) {
      super(problem, n, alpha);
    }

    @Override
    public IntIncEval createIncrementalEvaluation() {
      return null;
    }
  }

  /*
   * Fake heuristic designed for predictable test cases:
   * designed to prefer even permutation elements (largest to smallest), followed by odd
   * (largest to smallest).
   */
  static class IntHeuristic implements ConstructiveHeuristic<Permutation> {
    private IntProblem problem;
    private int n;
    private int alpha;

    public IntHeuristic(IntProblem problem, int n) {
      this(problem, n, n);
    }

    public IntHeuristic(IntProblem problem, int n, int alpha) {
      this.problem = problem;
      this.n = n;
      this.alpha = alpha;
    }

    @Override
    public IntProblem getProblem() {
      return problem;
    }

    @Override
    public int completeLength() {
      return n;
    }

    @Override
    public IntIncEval createIncrementalEvaluation() {
      return new IntIncEval();
    }

    @Override
    public double h(
        Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
      IntIncEval inc = (IntIncEval) incEval;
      if (element % 2 == 0) return alpha + element;
      else return element;
    }

    @Override
    public final Partial<Permutation> createPartial(int n) {
      return new PartialPermutation(n);
    }
  }

  /*
   * Fake heuristic designed for predictable test cases:
   * designed to prefer even permutation elements (largest to smallest), followed by odd
   * (largest to smallest).
   */
  static class DoubleHeuristic implements ConstructiveHeuristic<Permutation> {
    private DoubleProblem problem;
    private int n;
    private int alpha;

    public DoubleHeuristic(DoubleProblem problem, int n) {
      this(problem, n, n);
    }

    public DoubleHeuristic(DoubleProblem problem, int n, int alpha) {
      this.problem = problem;
      this.n = n;
      this.alpha = alpha;
    }

    @Override
    public DoubleProblem getProblem() {
      return problem;
    }

    @Override
    public int completeLength() {
      return n;
    }

    @Override
    public DoubleIncEval createIncrementalEvaluation() {
      return new DoubleIncEval();
    }

    @Override
    public double h(
        Partial<Permutation> p, int element, IncrementalEvaluation<Permutation> incEval) {
      DoubleIncEval inc = (DoubleIncEval) incEval;
      if (element % 2 == 0) return alpha + element;
      else return element;
    }

    @Override
    public final Partial<Permutation> createPartial(int n) {
      return new PartialPermutation(n);
    }
  }

  /*
   * Fake designed for predictable test cases.
   */
  static class IntIncEval implements IncrementalEvaluation<Permutation> {
    private int sum;

    @Override
    public void extend(Partial<Permutation> p, int element) {
      sum += element + 1;
    }
  }

  /*
   * Fake designed for predictable test cases.
   */
  static class DoubleIncEval implements IncrementalEvaluation<Permutation> {
    private int sum;

    @Override
    public void extend(Partial<Permutation> p, int element) {
      sum += element + 1;
    }
  }

  /*
   * We need a problem for the tests.
   * Fake problem. Doesn't really matter for what we are testing.
   */
  static class IntProblem implements IntegerCostOptimizationProblem<Permutation> {
    @Override
    public int cost(Permutation candidate) {
      int sum = 0;
      for (int i = 0; i < candidate.length(); i++) {
        sum += candidate.get(i);
      }
      return sum + candidate.length();
    }

    @Override
    public int value(Permutation candidate) {
      return cost(candidate);
    }
  }

  /*
   * We need a problem for the tests.
   * Fake problem. Doesn't really matter for what we are testing.
   */
  static class DoubleProblem implements OptimizationProblem<Permutation> {
    @Override
    public double cost(Permutation candidate) {
      int sum = 0;
      for (int i = 0; i < candidate.length(); i++) {
        sum += candidate.get(i);
      }
      return sum + candidate.length();
    }

    @Override
    public double value(Permutation candidate) {
      return cost(candidate);
    }
  }
}
