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

/** JUnit test cases for the parameter free exponential cooling. */
public class ParamFreeExponentialCoolingTests {

  private final EnhancedRandomGenerator generator;

  public ParamFreeExponentialCoolingTests() {
    generator = new EnhancedRandomGenerator(new SplittableRandom(42));
  }

  private static final double EPSILON = 1e-10;

  @Test
  public void testSplit() {
    double logP = Math.log(0.95);
    ParameterFreeExponentialCooling cOriginal = new ParameterFreeExponentialCooling();
    cOriginal.init(500);
    for (int i = 0; i < 10; i++) cOriginal.accept(3, 2);
    ParameterFreeExponentialCooling c = cOriginal.split();
    c.init(101);
    for (int j = 0; j < 10; j++) {
      assertEquals(0.0, c.getTemperature(), EPSILON);
      assertEquals(0.0, c.getAlpha(), EPSILON);
      assertEquals(0, c.getSteps());
      assertTrue(c.accept(2, 1));
    }
    double expectedT = -1 / logP;
    double expectedA = Math.pow(0.001 / expectedT, 1.0 / 90.0);
    assertEquals(expectedT, c.getTemperature(), EPSILON);
    assertEquals(expectedA, c.getAlpha(), EPSILON);
    assertEquals(1, c.getSteps());
  }

  @Test
  public void testInitialParamEstimates() {
    double logP = Math.log(0.95);

    ParameterFreeExponentialCooling c = new ParameterFreeExponentialCooling();
    for (int i = 1; i <= 4; i *= 2) {
      c.init(101);
      for (int j = 0; j < 10; j++) {
        assertEquals(0.0, c.getTemperature(), EPSILON);
        assertEquals(0.0, c.getAlpha(), EPSILON);
        assertEquals(0, c.getSteps());
        assertTrue(c.accept(i + 1, 1));
      }
      double expectedT = -i / logP;
      double expectedA = Math.pow(0.001 / expectedT, 1.0 / 90.0);
      assertEquals(expectedT, c.getTemperature(), EPSILON);
      assertEquals(expectedA, c.getAlpha(), EPSILON);
      assertEquals(1, c.getSteps());
    }
    for (int i = 1; i <= 4; i *= 2) {
      c.init(101);
      for (int j = 0; j < 10; j++) {
        assertEquals(0.0, c.getTemperature(), EPSILON);
        assertEquals(0.0, c.getAlpha(), EPSILON);
        assertEquals(0, c.getSteps());
        assertTrue(c.accept(1, 1 + i));
      }
      double expectedT = -i / logP;
      double expectedA = Math.pow(0.001 / expectedT, 1.0 / 90.0);
      assertEquals(expectedT, c.getTemperature(), EPSILON);
      assertEquals(expectedA, c.getAlpha(), EPSILON);
      assertEquals(1, c.getSteps());
    }
    // Make sure same cost leads to extra estimation iteration
    for (int i = 1; i <= 4; i *= 2) {
      c.init(101);
      c.accept(2, 2);
      for (int j = 0; j < 10; j++) {
        assertEquals(0.0, c.getTemperature(), EPSILON);
        assertEquals(0.0, c.getAlpha(), EPSILON);
        assertEquals(0, c.getSteps());
        assertTrue(c.accept(i + 1, 1));
      }
      double expectedT = -i / logP;
      double expectedA = Math.pow(0.001 / expectedT, 1.0 / 89.0);
      assertEquals(expectedT, c.getTemperature(), EPSILON);
      assertEquals(expectedA, c.getAlpha(), EPSILON);
      assertEquals(1, c.getSteps());
    }
    // Force initial t to be small
    {
      c.init(101);
      double diff = -0.02 * Math.log(0.95) / 20.0;
      for (int j = 0; j < 10; j++) {
        assertEquals(0.0, c.getTemperature(), EPSILON);
        assertEquals(0.0, c.getAlpha(), EPSILON);
        assertEquals(0, c.getSteps());
        assertTrue(c.accept(diff + 1, 1));
      }
      double expectedT = 0.002;
      double expectedA = Math.pow(0.001 / expectedT, 1.0 / 90.0);
      assertEquals(expectedT, c.getTemperature(), EPSILON);
      assertEquals(expectedA, c.getAlpha(), EPSILON);
      assertEquals(1, c.getSteps());
    }

    c.init(10001);
    for (int j = 0; j < 10; j++) {
      assertEquals(0.0, c.getTemperature(), EPSILON);
      assertEquals(0.0, c.getAlpha(), EPSILON);
      assertEquals(0, c.getSteps());
      assertTrue(c.accept(2, 1));
    }
    double expectedT = -1 / logP;
    double expectedA = Math.pow(0.001 / expectedT, 1.0 / 4995.0);
    assertEquals(expectedT, c.getTemperature(), EPSILON);
    assertEquals(expectedA, c.getAlpha(), EPSILON);
    assertEquals(2, c.getSteps());
    c.init(10002);
    for (int j = 0; j < 10; j++) {
      assertEquals(0.0, c.getTemperature(), EPSILON);
      assertEquals(0.0, c.getAlpha(), EPSILON);
      assertEquals(0, c.getSteps());
      assertTrue(c.accept(2, 1));
    }
    expectedA = Math.pow(0.001 / expectedT, 1.0 / 4996.0);
    assertEquals(expectedT, c.getTemperature(), EPSILON);
    assertEquals(expectedA, c.getAlpha(), EPSILON);
    assertEquals(2, c.getSteps());
    c.init(20003);
    for (int j = 0; j < 10; j++) {
      assertEquals(0.0, c.getTemperature(), EPSILON);
      assertEquals(0.0, c.getAlpha(), EPSILON);
      assertEquals(0, c.getSteps());
      assertTrue(c.accept(2, 1));
    }
    expectedA = Math.pow(0.001 / expectedT, 1.0 / 4998.0);
    assertEquals(expectedT, c.getTemperature(), EPSILON);
    assertEquals(expectedA, c.getAlpha(), EPSILON);
    assertEquals(4, c.getSteps());
  }

  @Test
  public void testCooling() {
    double logP = Math.log(0.95);

    ParameterFreeExponentialCooling c = new ParameterFreeExponentialCooling();
    for (int i = 1; i <= 4; i *= 2) {
      c.init(101);
      for (int j = 0; j < 10; j++) {
        c.accept(i + 1, 1);
      }
      double expectedT = -i / logP;
      double expectedA = Math.pow(0.001 / expectedT, 1.0 / 90.0);
      for (int j = 10; j < 100; j++) {
        assertTrue(c.getTemperature() > 0.001);
        c.accept(i + 1, 1);
        expectedT *= expectedA;
        assertEquals(expectedT, c.getTemperature(), EPSILON);
      }
      assertEquals(0.001, c.getTemperature(), EPSILON);
      c.accept(2, 1);
      assertEquals(0.001, c.getTemperature(), EPSILON);
    }
    c.init(10001);
    for (int j = 0; j < 10; j++) {
      c.accept(2, 1);
    }
    double expectedT = -1 / logP;
    double expectedA = Math.pow(0.001 / expectedT, 1.0 / 4995.0);
    for (int j = 10; j < 10000; j++) {
      assertTrue(c.getTemperature() > 0.001);
      c.accept(2, 1);
      if (j % 2 == 1) expectedT *= expectedA;
      assertEquals(expectedT, c.getTemperature(), EPSILON);
    }
    assertEquals(0.001, c.getTemperature(), EPSILON);
    c.init(10002);
    for (int j = 0; j < 10; j++) {
      c.accept(2, 1);
    }
    expectedT = -1 / logP;
    expectedA = Math.pow(0.001 / expectedT, 1.0 / 4996.0);
    for (int j = 10; j < 10001; j++) {
      assertTrue(c.getTemperature() > 0.001);
      c.accept(2, 1);
      if (j % 2 == 1) expectedT *= expectedA;
      assertEquals(expectedT, c.getTemperature(), EPSILON);
    }
    c.accept(2, 1);
    assertEquals(0.001, c.getTemperature(), EPSILON);
    c.init(20003);
    for (int j = 0; j < 10; j++) {
      c.accept(2, 1);
    }
    expectedT = -1 / logP;
    expectedA = Math.pow(0.001 / expectedT, 1.0 / 4998.0);
    for (int j = 10; j < 20002; j++) {
      assertTrue(c.getTemperature() > 0.001);
      c.accept(2, 1);
      if (j % 4 == 1) expectedT *= expectedA;
      assertEquals(expectedT, c.getTemperature(), EPSILON);
    }
    assertEquals(0.001, c.getTemperature(), EPSILON);
  }

  @Test
  public void testAcceptanceAndRejection() {
    ParameterFreeExponentialCooling c = new ParameterFreeExponentialCooling();
    c.init(1030);
    for (int j = 0; j < 10; j++) {
      c.accept(2, 1);
    }
    // verify accept correctly
    for (int i = 0; i < 10; i++) {
      assertTrue(c.accept(i, 9));
    }
    // verify accept will reject by passing infinite cost.
    for (int i = 0; i < 10; i++) {
      assertFalse(c.accept(Double.POSITIVE_INFINITY, 9));
    }
    int count = 0;
    for (int i = 0; i < 1000; i++) {
      if (c.accept(10001 + generator.nextInt(5), 10000)) count++;
    }
    assertTrue(count > 0);
    assertTrue(count < 1000);
  }
}
