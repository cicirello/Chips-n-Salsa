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

import org.cicirello.search.representations.BitVector;
import org.junit.jupiter.api.*;

/** JUnit test cases for BNPReplacement. */
public class BNPReplacementTests {

  @Test
  public void testDouble() {
    // Validates that the replacement chooses the target population size number of members,
    // that too many calls throws an exception, and that init resets.
    int n = 10;
    int NUM_GENS = 5;
    CandidatesDouble parents = new CandidatesDouble(n);
    CandidatesDouble children = new CandidatesDouble(n);
    ReplacementsValidator validator = new ReplacementsValidator(n);
    BNPReplacement<BitVector> replacement =
        new BNPReplacement<BitVector>(
            (b1, b2) -> {
              BitVector b3 = b1.copy();
              b3.xor(b2);
              return (double) b3.countOnes();
            });
    replacement.init(NUM_GENS);
    for (int i = 0; i < NUM_GENS; i++) {
      replacement.replace(parents, children, validator, n);
      validator.validate();
      validator.clear();
    }
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          replacement.replace(parents, children, validator, n);
        });
    replacement.init(NUM_GENS + 4);
    for (int i = 0; i < NUM_GENS + 4; i++) {
      replacement.replace(parents, children, validator, n);
      validator.validate();
      validator.clear();
    }
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          replacement.replace(parents, children, validator, n);
        });
  }

  @Test
  public void testInteger() {
    // Validates that the replacement chooses the target population size number of members,
    // that too many calls throws an exception, and that init resets.
    int n = 10;
    int NUM_GENS = 5;
    CandidatesInteger parents = new CandidatesInteger(n);
    CandidatesInteger children = new CandidatesInteger(n);
    ReplacementsValidator validator = new ReplacementsValidator(n);
    BNPReplacement<BitVector> replacement =
        new BNPReplacement<BitVector>(
            (b1, b2) -> {
              BitVector b3 = b1.copy();
              b3.xor(b2);
              return (double) b3.countOnes();
            });
    replacement.init(NUM_GENS);
    for (int i = 0; i < NUM_GENS; i++) {
      replacement.replace(parents, children, validator, n);
      validator.validate();
      validator.clear();
    }
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          replacement.replace(parents, children, validator, n);
        });
    replacement.init(NUM_GENS + 4);
    for (int i = 0; i < NUM_GENS + 4; i++) {
      replacement.replace(parents, children, validator, n);
      validator.validate();
      validator.clear();
    }
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          replacement.replace(parents, children, validator, n);
        });
  }

  @Test
  public void testSplitDouble() {
    // For a split copy:
    // Validates that the replacement chooses the target population size number of members,
    // that too many calls throws an exception, and that init resets.
    int n = 10;
    int NUM_GENS = 5;
    CandidatesDouble parents = new CandidatesDouble(n);
    CandidatesDouble children = new CandidatesDouble(n);
    ReplacementsValidator validator = new ReplacementsValidator(n);
    BNPReplacement<BitVector> original =
        new BNPReplacement<BitVector>(
            (b1, b2) -> {
              BitVector b3 = b1.copy();
              b3.xor(b2);
              return (double) b3.countOnes();
            });
    BNPReplacement<BitVector> replacement = original.split();
    replacement.init(NUM_GENS);
    for (int i = 0; i < NUM_GENS; i++) {
      replacement.replace(parents, children, validator, n);
      validator.validate();
      validator.clear();
    }
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          replacement.replace(parents, children, validator, n);
        });
    replacement.init(NUM_GENS + 4);
    for (int i = 0; i < NUM_GENS + 4; i++) {
      replacement.replace(parents, children, validator, n);
      validator.validate();
      validator.clear();
    }
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          replacement.replace(parents, children, validator, n);
        });
  }

  @Test
  public void testSplitInteger() {
    // For a split copy:
    // Validates that the replacement chooses the target population size number of members,
    // that too many calls throws an exception, and that init resets.
    int n = 10;
    int NUM_GENS = 5;
    CandidatesInteger parents = new CandidatesInteger(n);
    CandidatesInteger children = new CandidatesInteger(n);
    ReplacementsValidator validator = new ReplacementsValidator(n);
    BNPReplacement<BitVector> original =
        new BNPReplacement<BitVector>(
            (b1, b2) -> {
              BitVector b3 = b1.copy();
              b3.xor(b2);
              return (double) b3.countOnes();
            });
    BNPReplacement<BitVector> replacement = original.split();
    replacement.init(NUM_GENS);
    for (int i = 0; i < NUM_GENS; i++) {
      replacement.replace(parents, children, validator, n);
      validator.validate();
      validator.clear();
    }
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          replacement.replace(parents, children, validator, n);
        });
    replacement.init(NUM_GENS + 4);
    for (int i = 0; i < NUM_GENS + 4; i++) {
      replacement.replace(parents, children, validator, n);
      validator.validate();
      validator.clear();
    }
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          replacement.replace(parents, children, validator, n);
        });
  }

  private static class CandidatesDouble implements PopulationCandidates.DoubleFitness<BitVector> {

    private BitVector[] c;

    public CandidatesDouble(int n) {
      c = new BitVector[n];
      for (int i = 0; i < n; i++) {
        c[i] = new BitVector(100, true);
      }
    }

    @Override
    public int size() {
      return c.length;
    }

    @Override
    public double fitness(int i) {
      return c[i].countOnes();
    }

    @Override
    public BitVector candidate(int i) {
      return c[i];
    }
  }

  private static class CandidatesInteger implements PopulationCandidates.IntegerFitness<BitVector> {

    private BitVector[] c;

    public CandidatesInteger(int n) {
      c = new BitVector[n];
      for (int i = 0; i < n; i++) {
        c[i] = new BitVector(100, true);
      }
    }

    @Override
    public int size() {
      return c.length;
    }

    @Override
    public int fitness(int i) {
      return c[i].countOnes();
    }

    @Override
    public BitVector candidate(int i) {
      return c[i];
    }
  }

  private static class ReplacementsValidator implements ReplacementStrategy.Replacements {

    private int count;
    public int trueCount;

    public ReplacementsValidator(int n) {
      count = n;
      trueCount = 0;
    }

    @Override
    public void chooseFromParentPopulation(int i, int count) {
      trueCount += count;
    }

    @Override
    public void chooseFromChildPopulation(int i, int count) {
      trueCount += count;
    }

    public void validate() {
      assertEquals(count, trueCount);
    }

    public void clear() {
      trueCount = 0;
    }
  }
}
