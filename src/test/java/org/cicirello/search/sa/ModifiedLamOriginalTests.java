/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2024 Vincent A. Cicirello
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

import java.util.SplittableRandom;
import org.cicirello.math.rand.EnhancedRandomGenerator;
import org.junit.jupiter.api.*;

/**
 * JUnit test cases for the Modified Lam annealing schedule, the version that is a direct
 * implementation as the schedule was originally described.
 */
public class ModifiedLamOriginalTests {

  private final EnhancedRandomGenerator generator;

  public ModifiedLamOriginalTests() {
    generator = new EnhancedRandomGenerator(new SplittableRandom(42));
  }

  private static final double EPSILON = 1e-10;

  @Test
  public void testSplit() {
    ModifiedLamOriginal mOriginal = new ModifiedLamOriginal();
    mOriginal.init(500);
    for (int i = 0; i < 10; i++) mOriginal.accept(3, 2);
    ModifiedLamOriginal m = mOriginal.split();
    m.init(100);
    assertEquals(1.0, m.getTargetRate(), EPSILON);
    for (int i = 0; i < 15; i++) m.accept(3, 2);
    assertEquals(0.441, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    for (int i = 16; i < 65; i++) m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44 * Math.pow(440, -1.0 / 35.0), m.getTargetRate(), EPSILON);
    for (int i = 66; i < 100; i++) m.accept(3, 2);
    assertEquals(0.001, m.getTargetRate(), EPSILON);
  }

  @Test
  public void testTargetRate() {
    ModifiedLamOriginal m = new ModifiedLamOriginal();
    m.init(100);
    assertEquals(1.0, m.getTargetRate(), EPSILON);
    for (int i = 0; i < 15; i++) m.accept(3, 2);
    assertEquals(0.441, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    for (int i = 16; i < 65; i++) m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44 * Math.pow(440, -1.0 / 35.0), m.getTargetRate(), EPSILON);
    for (int i = 66; i < 100; i++) m.accept(3, 2);
    assertEquals(0.001, m.getTargetRate(), EPSILON);
    // repeating to make sure init resets stuff correctly
    m.init(100);
    assertEquals(1.0, m.getTargetRate(), EPSILON);
    for (int i = 0; i < 15; i++) m.accept(3, 2);
    assertEquals(0.441, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    for (int i = 16; i < 65; i++) m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44 * Math.pow(440, -1.0 / 35.0), m.getTargetRate(), EPSILON);
    for (int i = 66; i < 100; i++) m.accept(3, 2);
    assertEquals(0.001, m.getTargetRate(), EPSILON);
    // now repeating with longer run length
    m.init(1000);
    assertEquals(1.0, m.getTargetRate(), EPSILON);
    for (int i = 0; i < 150; i++) m.accept(3, 2);
    assertEquals(0.441, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    for (int i = 151; i < 650; i++) m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44 * Math.pow(440, -1.0 / 350.0), m.getTargetRate(), EPSILON);
    for (int i = 651; i < 1000; i++) m.accept(3, 2);
    assertEquals(0.001, m.getTargetRate(), EPSILON);
    // now repeating with an even longer run length
    m.init(10000);
    assertEquals(1.0, m.getTargetRate(), EPSILON);
    for (int i = 0; i < 1500; i++) m.accept(3, 2);
    assertEquals(0.441, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    for (int i = 1501; i < 6500; i++) m.accept(3, 2);
    assertEquals(0.44, m.getTargetRate(), EPSILON);
    m.accept(3, 2);
    assertEquals(0.44 * Math.pow(440, -1.0 / 3500.0), m.getTargetRate(), EPSILON);
    for (int i = 6501; i < 10000; i++) m.accept(3, 2);
    assertEquals(0.001, m.getTargetRate(), EPSILON);
  }

  @Test
  public void testAccept() {
    ModifiedLamOriginal m = new ModifiedLamOriginal();
    m.init(1000);
    double expected = 0.5;
    for (int i = 0; i < 1000; i++) {
      double t0 = m.getTemperature();
      assertEquals(expected, m.getAcceptRate(), EPSILON);
      // force an acceptance with neighbor cost <= current cost
      assertTrue(m.accept(i, 999));
      double t1 = m.getTemperature();
      if (m.getAcceptRate() < m.getTargetRate()) assertTrue(t1 > t0);
      else if (m.getAcceptRate() > m.getTargetRate()) assertTrue(t1 < t0);
      expected = 0.998 * expected + 0.002;
    }
    m.init(1000);
    expected = 0.5;
    for (int i = 0; i < 1000; i++) {
      double t0 = m.getTemperature();
      assertEquals(expected, m.getAcceptRate(), EPSILON);
      // force a rejection with infinite cost neighbor
      assertFalse(m.accept(Double.POSITIVE_INFINITY, 0));
      double t1 = m.getTemperature();
      if (m.getAcceptRate() < m.getTargetRate()) assertTrue(t1 > t0);
      else if (m.getAcceptRate() > m.getTargetRate()) assertTrue(t1 < t0);
      expected = 0.998 * expected;
    }
    final int RUN_LENGTH = 1000;
    m.init(RUN_LENGTH);
    int count = 0;
    for (int i = 0; i < RUN_LENGTH; i++) {
      double t0 = m.getTemperature();
      if (m.accept(10001 + generator.nextInt(5), 10000)) count++;
      double t1 = m.getTemperature();
      if (m.getAcceptRate() < m.getTargetRate()) assertTrue(t1 > t0);
      else if (m.getAcceptRate() > m.getTargetRate()) assertTrue(t1 < t0);
    }
    assertTrue(count > 0);
    assertTrue(count < RUN_LENGTH);
  }
}
