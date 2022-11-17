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

package org.cicirello.search.operators.bits;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.search.representations.BitVector;
import org.junit.jupiter.api.*;

/** JUnit test cases for BitFlipMutation. */
public class BitFlipMutationTests {

  @Test
  public void testExceptions() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new BitFlipMutation(0.0));
    thrown = assertThrows(IllegalArgumentException.class, () -> new BitFlipMutation(1.0));
  }

  @Test
  public void testMutateChange() {
    // Verify that mutate changes the BitVector.
    // Since it doesn't guarantee that any bits are changed during a single
    // call, must verify that it changes the BitVector on some calls.
    for (double m = 0.2; m < 0.9; m += 0.2) {
      BitFlipMutation mutation = new BitFlipMutation(m);
      BitVector v1 = new BitVector(100, true);
      BitVector v2 = v1.copy();
      boolean changed = false;
      for (int trial = 0; !changed && trial < 100; trial++) {
        mutation.mutate(v2);
        if (!v1.equals(v2)) changed = true;
      }
      assertTrue(changed);
    }
  }

  @Test
  public void testMutateExpected() {
    BitFlipMutation mutation = new BitFlipMutation(0.25);
    int sum = 0;
    for (int trial = 0; trial < 100; trial++) {
      // start with 100 zeros.
      BitVector v1 = new BitVector(100);
      // mutate it
      mutation.mutate(v1);
      sum += v1.countOnes();
    }
    double average = sum / 100.0;
    assertTrue(
        average >= 15 && average <= 35, "for m=0.25, average bits out of 100 was " + average);
    mutation = new BitFlipMutation(0.5);
    sum = 0;
    for (int trial = 0; trial < 100; trial++) {
      // start with 100 zeros.
      BitVector v1 = new BitVector(100);
      // mutate it
      mutation.mutate(v1);
      sum += v1.countOnes();
    }
    average = sum / 100.0;
    assertTrue(average >= 40 && average <= 60, "for m=0.5, average bits out of 100 was " + average);
    mutation = new BitFlipMutation(0.75);
    sum = 0;
    for (int trial = 0; trial < 100; trial++) {
      // start with 100 zeros.
      BitVector v1 = new BitVector(100);
      // mutate it
      mutation.mutate(v1);
      sum += v1.countOnes();
    }
    average = sum / 100.0;
    assertTrue(
        average >= 65 && average <= 85, "for m=0.75, average bits out of 100 was " + average);
  }

  @Test
  public void testUndo() {
    BitFlipMutation mutation = new BitFlipMutation(0.1);
    for (int i = 0; i < 10; i++) {
      BitVector v1 = new BitVector(100, true);
      BitVector v2 = v1.copy();
      mutation.mutate(v2);
      mutation.undo(v2);
      assertEquals(v1, v2);
    }
    mutation = new BitFlipMutation(0.1);
    BitVector v1 = new BitVector(100, true);
    BitVector v2 = v1.copy();
    mutation.undo(v2);
    assertEquals(v1, v2);
  }

  @Test
  public void testSplit() {
    BitFlipMutation mutationOriginal = new BitFlipMutation(0.25);
    BitFlipMutation mutation = mutationOriginal.split();
    int sum = 0;
    for (int trial = 0; trial < 100; trial++) {
      // start with 100 zeros.
      BitVector v1 = new BitVector(100);
      // mutate it
      mutation.mutate(v1);
      sum += v1.countOnes();
    }
    double average = sum / 100.0;
    assertTrue(
        average >= 15 && average <= 35, "for m=0.25, average bits out of 100 was " + average);
    mutationOriginal = new BitFlipMutation(0.5);
    mutation = mutationOriginal.split();
    sum = 0;
    for (int trial = 0; trial < 100; trial++) {
      // start with 100 zeros.
      BitVector v1 = new BitVector(100);
      // mutate it
      mutation.mutate(v1);
      sum += v1.countOnes();
    }
    average = sum / 100.0;
    assertTrue(average >= 40 && average <= 60, "for m=0.5, average bits out of 100 was " + average);
    mutationOriginal = new BitFlipMutation(0.75);
    mutation = mutationOriginal.split();
    sum = 0;
    for (int trial = 0; trial < 100; trial++) {
      // start with 100 zeros.
      BitVector v1 = new BitVector(100);
      // mutate it
      mutation.mutate(v1);
      sum += v1.countOnes();
    }
    average = sum / 100.0;
    assertTrue(
        average >= 65 && average <= 85, "for m=0.75, average bits out of 100 was " + average);
  }

  @Test
  public void testSplitUndo() {
    BitFlipMutation mutationOriginal = new BitFlipMutation(0.5);
    for (int i = 0; i < 10; i++) {
      BitVector v1 = new BitVector(64);
      BitVector v2 = v1.copy();
      BitVector v3 = v1.copy();
      mutationOriginal.mutate(v2);
      assertNotEquals(v1, v2);
      BitFlipMutation mutation = mutationOriginal.split();
      mutation.mutate(v3);
      assertNotEquals(v1, v3);
      mutationOriginal.undo(v2);
      assertEquals(v1, v2);
      mutation.undo(v3);
      assertEquals(v1, v3);
    }
  }
}
