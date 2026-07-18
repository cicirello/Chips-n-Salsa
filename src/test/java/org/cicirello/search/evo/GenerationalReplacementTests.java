/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2026 Vincent A. Cicirello
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

/** JUnit test cases for GenerationalReplacement. */
public class GenerationalReplacementTests {

  @Test
  public void testInit() {
    // This class doesn't implement init. Make sure that the default doesn't break anything.
    int n = 10;
    CandidatesDouble parents = new CandidatesDouble(n, 1000);
    CandidatesDouble children = new CandidatesDouble(n);
    ReplacementsValidator validator = new ReplacementsValidator(n);
    GenerationalReplacement<String> replacement = new GenerationalReplacement<String>();
    replacement.replace(parents, children, validator, n);
    validator.validate();
    replacement.init(1000);
    validator = new ReplacementsValidator(n);
    replacement.replace(parents, children, validator, n);
    validator.validate();
  }

  @Test
  public void testSplit() {
    int n = 10;
    GenerationalReplacement<String> replacement = new GenerationalReplacement<String>();
    assertSame(replacement, replacement.split());
  }

  @Test
  public void testDoubleFitnessSameSize() {
    int n = 10;
    CandidatesDouble parents = new CandidatesDouble(n, 1000);
    CandidatesDouble children = new CandidatesDouble(n);
    ReplacementsValidator validator = new ReplacementsValidator(n);
    GenerationalReplacement<String> replacement = new GenerationalReplacement<String>();
    replacement.replace(parents, children, validator, n);
    validator.validate();
  }

  @Test
  public void testDoubleFitnessMoreChildren() {
    int n = 10;
    CandidatesDouble parents = new CandidatesDouble(n, 1000);
    CandidatesDouble children = new CandidatesDouble(n + 1);
    ReplacementsValidator validator = new ReplacementsValidator(n + 1, n);
    GenerationalReplacement<String> replacement = new GenerationalReplacement<String>();
    replacement.replace(parents, children, validator, n);
    validator.validate();
  }

  @Test
  public void testDoubleFitnessLessChildren() {
    int n = 10;
    CandidatesDouble parents = new CandidatesDouble(n, 1000);
    CandidatesDouble children = new CandidatesDouble(n - 1);
    ReplacementsValidator validator = new ReplacementsValidator(n - 1, n - 1);
    GenerationalReplacement<String> replacement = new GenerationalReplacement<String>();
    replacement.replace(parents, children, validator, n);
    validator.validate();
  }

  @Test
  public void testIntegerFitnessSameSize() {
    int n = 10;
    CandidatesInteger parents = new CandidatesInteger(n, 1000);
    CandidatesInteger children = new CandidatesInteger(n);
    ReplacementsValidator validator = new ReplacementsValidator(n);
    GenerationalReplacement<String> replacement = new GenerationalReplacement<String>();
    replacement.replace(parents, children, validator, n);
    validator.validate();
  }

  @Test
  public void testIntegerFitnessMoreChildren() {
    int n = 10;
    CandidatesInteger parents = new CandidatesInteger(n, 1000);
    CandidatesInteger children = new CandidatesInteger(n + 1);
    ReplacementsValidator validator = new ReplacementsValidator(n + 1, n);
    GenerationalReplacement<String> replacement = new GenerationalReplacement<String>();
    replacement.replace(parents, children, validator, n);
    validator.validate();
  }

  @Test
  public void testIntegerFitnessLessChildren() {
    int n = 10;
    CandidatesInteger parents = new CandidatesInteger(n, 1000);
    CandidatesInteger children = new CandidatesInteger(n - 1);
    ReplacementsValidator validator = new ReplacementsValidator(n - 1, n - 1);
    GenerationalReplacement<String> replacement = new GenerationalReplacement<String>();
    replacement.replace(parents, children, validator, n);
    validator.validate();
  }

  private static class ReplacementsValidator implements ReplacementStrategy.Replacements {

    private boolean[] added;
    private int count;

    public ReplacementsValidator(int n) {
      added = new boolean[n];
      count = n;
    }

    public ReplacementsValidator(int n, int count) {
      added = new boolean[n];
      this.count = count;
    }

    @Override
    public void addFromParentPopulation(int i) {
      fail("generational replacement shouldn't keep originals");
    }

    @Override
    public void addFromChildPopulation(int i) {
      assertFalse(added[i]);
      added[i] = true;
    }

    public void validate() {
      int trueCount = 0;
      for (int i = 0; i < added.length; i++) {
        if (added[i]) trueCount++;
      }
      assertEquals(count, trueCount);
    }
  }

  private static class CandidatesDouble implements PopulationCandidates.DoubleFitness<String> {

    private String[] c;
    private double[] f;

    public CandidatesDouble(int n) {
      c = new String[n];
      f = new double[n];
      for (int i = 0; i < n; i++) {
        f[i] = 100 - i;
        c[i] = "C" + i;
      }
    }

    public CandidatesDouble(int n, int fitness) {
      c = new String[n];
      f = new double[n];
      for (int i = 0; i < n; i++) {
        f[i] = fitness;
        c[i] = "P" + i;
      }
    }

    @Override
    public int size() {
      return f.length;
    }

    @Override
    public double fitness(int i) {
      return f[i];
    }

    @Override
    public String candidate(int i) {
      return c[i];
    }
  }

  private static class CandidatesInteger implements PopulationCandidates.IntegerFitness<String> {

    private String[] c;
    private int[] f;

    public CandidatesInteger(int n) {
      c = new String[n];
      f = new int[n];
      for (int i = 0; i < n; i++) {
        f[i] = 100 - i;
        c[i] = "C" + i;
      }
    }

    public CandidatesInteger(int n, int fitness) {
      c = new String[n];
      f = new int[n];
      for (int i = 0; i < n; i++) {
        f[i] = fitness;
        c[i] = "P" + i;
      }
    }

    @Override
    public int size() {
      return f.length;
    }

    @Override
    public int fitness(int i) {
      return f[i];
    }

    @Override
    public String candidate(int i) {
      return c[i];
    }
  }
}
