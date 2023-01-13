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

package org.cicirello.search.sa;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.math.rand.RandomIndexer;
import org.junit.jupiter.api.*;

/** JUnit test cases for the LogarithmicCooling annealing schedule. */
public class LogarithmicCoolingTests {

  @Test
  public void testLogarithmicCoolingExceptions() {
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new LogarithmicCooling(0.0));
  }

  @Test
  public void testLogarithmicCooling() {
    double t0 = 10;
    LogarithmicCooling c = new LogarithmicCooling(t0);
    c.init(100);
    for (int i = 0; i < 10; i++) {
      double expected = t0 / StrictMath.log(StrictMath.E + i);
      assertEquals(expected, c.getTemperature());
      c.accept(2, 5);
    }
    c.init(100);
    for (int i = 0; i < 10; i++) {
      double expected = t0 / StrictMath.log(StrictMath.E + i);
      assertEquals(expected, c.getTemperature());
      c.accept(2, 5);
    }
    t0 = 100;
    c = new LogarithmicCooling(t0);
    c.init(100);
    for (int i = 0; i < 10; i++) {
      double expected = t0 / StrictMath.log(StrictMath.E + i);
      assertEquals(expected, c.getTemperature());
      c.accept(2, 5);
    }
    c.init(100);
    for (int i = 0; i < 10; i++) {
      double expected = t0 / StrictMath.log(StrictMath.E + i);
      assertEquals(expected, c.getTemperature());
      c.accept(2, 5);
    }
    // verify accepting correctly
    c.init(100);
    for (int i = 0; i < 10; i++) {
      assertTrue(c.accept(i, 9));
    }
    // verify accept will reject by passing infinite cost.
    for (int i = 0; i < 10; i++) {
      assertFalse(c.accept(Double.POSITIVE_INFINITY, 9));
    }
    // verify accept both accepts some higher cost neighbors and rejects other higher cost
    // neighbors.
    final int RUN_LENGTH = 1000;
    c.init(RUN_LENGTH);
    int count = 0;
    for (int i = 0; i < RUN_LENGTH; i++) {
      if (c.accept(10001 + RandomIndexer.nextInt(5), 10000)) count++;
      if (count > 0 && count != i + 1) break;
    }
    assertTrue(count > 0);
    assertTrue(count < RUN_LENGTH);
    // Test split
    LogarithmicCooling orig = new LogarithmicCooling(t0);
    orig.init(5);
    orig.accept(2, 5);
    c = orig.split();
    orig.accept(2, 5);
    c.init(100);
    orig.accept(2, 5);
    for (int i = 0; i < 10; i++) {
      double expected = t0 / StrictMath.log(StrictMath.E + i);
      assertEquals(expected, c.getTemperature());
      c.accept(2, 5);
    }
  }
}
