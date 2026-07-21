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

/** JUnit test cases for the internal class ReplacementTracker */
public class ReplacementTrackerTests {

  @Test
  public void testReplacementTracker() {
    final int MU = 8;
    final int LAMBDA = 3;
    ReplacementTracker t = new ReplacementTracker(MU, LAMBDA);
    int[] p = t.parentCounts();
    int[] c = t.childCounts();
    assertEquals(MU, p.length);
    assertEquals(LAMBDA, c.length);
    assertFalse(t.includesParents());
    assertArrayEquals(new int[] {0, 0, 0, 0, 0, 0, 0, 0}, p);
    assertArrayEquals(new int[] {0, 0, 0}, c);

    t.addFromChildPopulation(1);
    assertFalse(t.includesParents());
    assertArrayEquals(new int[] {0, 1, 0}, c);
    t.addFromChildPopulation(0);
    assertFalse(t.includesParents());
    assertArrayEquals(new int[] {1, 1, 0}, c);
    t.addFromChildPopulation(2);
    assertFalse(t.includesParents());
    assertArrayEquals(new int[] {1, 1, 1}, c);
    t.addFromChildPopulation(0);
    assertFalse(t.includesParents());
    assertArrayEquals(new int[] {2, 1, 1}, c);
    t.addFromChildPopulation(2);
    assertFalse(t.includesParents());
    assertArrayEquals(new int[] {2, 1, 2}, c);
    t.addFromChildPopulation(0);
    assertFalse(t.includesParents());
    assertArrayEquals(new int[] {3, 1, 2}, c);

    t.addFromParentPopulation(2);
    assertTrue(t.includesParents());
    assertArrayEquals(new int[] {0, 0, 1, 0, 0, 0, 0, 0}, p);
    t.addFromParentPopulation(0);
    assertTrue(t.includesParents());
    assertArrayEquals(new int[] {1, 0, 1, 0, 0, 0, 0, 0}, p);
    t.addFromParentPopulation(4);
    assertTrue(t.includesParents());
    assertArrayEquals(new int[] {1, 0, 1, 0, 1, 0, 0, 0}, p);
    t.addFromParentPopulation(7);
    assertTrue(t.includesParents());
    assertArrayEquals(new int[] {1, 0, 1, 0, 1, 0, 0, 1}, p);
    t.addFromParentPopulation(4);
    assertTrue(t.includesParents());
    assertArrayEquals(new int[] {1, 0, 1, 0, 2, 0, 0, 1}, p);
    t.addFromParentPopulation(4);
    assertTrue(t.includesParents());
    assertArrayEquals(new int[] {1, 0, 1, 0, 3, 0, 0, 1}, p);

    t.clearChildCounts();
    assertArrayEquals(new int[] {0, 0, 0}, c);
    assertArrayEquals(new int[] {1, 0, 1, 0, 3, 0, 0, 1}, p);
    assertTrue(t.includesParents());

    t.addFromChildPopulation(2);
    assertArrayEquals(new int[] {0, 0, 1}, c);
    assertArrayEquals(new int[] {1, 0, 1, 0, 3, 0, 0, 1}, p);
    assertTrue(t.includesParents());

    t.clearParentCounts();
    assertArrayEquals(new int[] {0, 0, 1}, c);
    assertArrayEquals(new int[] {0, 0, 0, 0, 0, 0, 0, 0}, p);
    assertFalse(t.includesParents());
  }
}
