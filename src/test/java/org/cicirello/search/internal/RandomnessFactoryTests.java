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

package org.cicirello.search.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.SplittableRandom;
import java.util.random.RandomGenerator;
import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.junit.jupiter.api.*;

/** JUnit tests for RandomnessFactory. */
public class RandomnessFactoryTests {

  @Test
  public void testThreadLocal() {
    // To ensure independence from rest of test cases, start by
    // configuring the default (in case other test cases changed this).
    RandomnessFactory.configureDefault();

    EnhancedSplittableGenerator threadLocal =
        RandomnessFactory.threadLocalEnhancedSplittableGenerator();
    int x = threadLocal.nextInt(100);
    EnhancedSplittableGenerator threadLocalSecondCall =
        RandomnessFactory.threadLocalEnhancedSplittableGenerator();
    assertSame(threadLocal, threadLocalSecondCall);

    // To ensure independence from rest of test cases, finish by
    // configuring the default.
    RandomnessFactory.configureDefault();
  }

  @Test
  public void testSeeded() {
    // To ensure independence from rest of test cases, start by
    // configuring the default (in case other test cases changed this).
    RandomnessFactory.configureDefault();

    RandomnessFactory.configure(42L);

    RandomGenerator.SplittableGenerator[] facts = new RandomGenerator.SplittableGenerator[5];
    facts[0] = RandomnessFactory.createSplittableGenerator();
    facts[1] = RandomnessFactory.createSplittableGenerator();
    facts[2] = facts[0].split();
    facts[3] = facts[1].split();
    facts[4] = facts[1].split();
    for (int i = 0; i < facts.length; i++) {
      assertFalse(facts[i] instanceof EnhancedSplittableGenerator);
      assertTrue(facts[i] instanceof SplittableRandom);
    }
    verifyDifferent(facts);

    facts[0] = RandomnessFactory.createEnhancedSplittableGenerator();
    facts[1] = RandomnessFactory.createEnhancedSplittableGenerator();
    facts[2] = facts[0].split();
    facts[3] = facts[1].split();
    facts[4] = facts[1].split();
    for (int i = 0; i < facts.length; i++) {
      assertTrue(facts[i] instanceof EnhancedSplittableGenerator);
    }
    verifyDifferent(facts);

    RandomnessFactory.configure(42L);
    facts[0] = RandomnessFactory.createSplittableGenerator();
    RandomnessFactory.configure(42L);
    facts[1] = RandomnessFactory.createSplittableGenerator();
    facts[2] = facts[0].split();
    facts[3] = facts[1].split();

    verifySame(facts[0], facts[1]);
    verifySame(facts[2], facts[3]);

    RandomnessFactory.configure(42L);
    facts[0] = RandomnessFactory.createEnhancedSplittableGenerator();
    RandomnessFactory.configure(42L);
    facts[1] = RandomnessFactory.createEnhancedSplittableGenerator();
    facts[2] = facts[0].split();
    facts[3] = facts[1].split();

    verifySame(facts[0], facts[1]);
    verifySame(facts[2], facts[3]);

    // To ensure independence from rest of test cases, finish by
    // configuring the default.
    RandomnessFactory.configureDefault();
  }

  @Test
  public void testRandomnessFactory() {
    // To ensure independence from rest of test cases, start by
    // configuring the default (in case other test cases changed this).
    RandomnessFactory.configureDefault();

    RandomGenerator.SplittableGenerator[] facts = new RandomGenerator.SplittableGenerator[5];
    facts[0] = RandomnessFactory.createSplittableGenerator();
    facts[1] = RandomnessFactory.createSplittableGenerator();
    facts[2] = facts[0].split();
    facts[3] = facts[1].split();
    facts[4] = facts[1].split();
    for (int i = 0; i < facts.length; i++) {
      assertFalse(facts[i] instanceof EnhancedSplittableGenerator);
      assertTrue(facts[i] instanceof SplittableRandom);
    }
    verifyDifferent(facts);

    RandomnessFactory.configure(RandomGenerator.SplittableGenerator.of("L64X128MixRandom"));
    facts[0] = RandomnessFactory.createSplittableGenerator();
    facts[1] = RandomnessFactory.createSplittableGenerator();
    facts[2] = facts[0].split();
    facts[3] = facts[1].split();
    facts[4] = facts[1].split();
    for (int i = 0; i < facts.length; i++) {
      assertFalse(facts[i] instanceof EnhancedSplittableGenerator);
      assertFalse(facts[i] instanceof SplittableRandom);
    }
    verifyDifferent(facts);

    facts[0] = RandomnessFactory.createEnhancedSplittableGenerator();
    facts[1] = RandomnessFactory.createEnhancedSplittableGenerator();
    facts[2] = facts[0].split();
    facts[3] = facts[1].split();
    facts[4] = facts[1].split();
    for (int i = 0; i < facts.length; i++) {
      assertTrue(facts[i] instanceof EnhancedSplittableGenerator);
    }
    verifyDifferent(facts);

    RandomnessFactory.configure(null);
    facts[0] = RandomnessFactory.createEnhancedSplittableGenerator();
    facts[1] = RandomnessFactory.createEnhancedSplittableGenerator();
    facts[2] = facts[0].split();
    facts[3] = facts[1].split();
    facts[4] = facts[1].split();
    for (int i = 0; i < facts.length; i++) {
      assertTrue(facts[i] instanceof EnhancedSplittableGenerator);
    }
    verifyDifferent(facts);

    // To ensure independence from rest of test cases, finish by
    // configuring the default (in case other test cases changed this).
    RandomnessFactory.configureDefault();
  }

  private void verifySame(
      RandomGenerator.SplittableGenerator s1, RandomGenerator.SplittableGenerator s2) {
    final int MIN = 10;
    for (int i = 0; i < MIN; i++) {
      assertEquals(s1.nextLong(), s2.nextLong());
    }
  }

  private void verifyDifferent(RandomGenerator.SplittableGenerator[] facts) {
    final int MAX = 100;
    boolean[] confirmed = new boolean[facts.length];
    long[] next = new long[facts.length];
    int verifiedCount = 0;
    for (int i = 0; i < MAX; i++) {
      int[] counts = new int[facts.length];
      for (int j = 0; j < confirmed.length; j++) {
        if (!confirmed[j]) {
          next[j] = facts[j].nextLong();
          for (int k = j - 1; k >= 0; k--) {
            if (!confirmed[k] && next[j] == next[k]) {
              counts[j]++;
              counts[k]++;
            }
          }
        }
      }
      for (int j = 0; j < confirmed.length; j++) {
        if (!confirmed[j] && counts[j] == 0) {
          confirmed[j] = true;
          verifiedCount++;
        }
      }
      if (verifiedCount == facts.length) {
        break;
      }
    }
    for (int j = 0; j < confirmed.length; j++) {
      assertTrue(confirmed[j]);
    }
  }
}
