/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.math.rand.RandomIndexer;

/**
 * JUnit 4 test cases for the parameter free linear cooling.
 */
public class ParamFreeLinearCoolingTests {
	
	private static final double EPSILON = 1e-10;
	
	@Test
	public void testInitialParamEstimates() {
		double logP = Math.log(0.95);
		
		ParameterFreeLinearCooling c = new ParameterFreeLinearCooling();
		for (int i = 1; i <= 4; i *= 2) {
			c.init(101);
			for (int j = 0; j < 10; j++) {
				assertEquals("temperature shouldn't be set yet", 0.0, c.getTemperature(), EPSILON);
				assertEquals("deltaT shouldn't be set yet", 0.0, c.getDeltaT(), EPSILON);
				assertEquals("steps shouldn't be set yet", 0, c.getSteps());
				assertTrue("Should accept all if params not set yet", c.accept(i+1, 1));
			}
			double expectedT = -i/logP;
			double expectedDeltaT = (expectedT - 0.001) / 90;
			assertEquals("Checking initial temperature", expectedT, c.getTemperature(), EPSILON);
			assertEquals("Checking initial deltaT", expectedDeltaT, c.getDeltaT(), EPSILON);
			assertEquals("Checking initial steps", 1, c.getSteps());
		}
		for (int i = 1; i <= 4; i *= 2) {
			c.init(101);
			for (int j = 0; j < 10; j++) {
				assertEquals("temperature shouldn't be set yet", 0.0, c.getTemperature(), EPSILON);
				assertEquals("deltaT shouldn't be set yet", 0.0, c.getDeltaT(), EPSILON);
				assertEquals("steps shouldn't be set yet", 0, c.getSteps());
				assertTrue("Should accept all if params not set yet", c.accept(1, 1+i));
			}
			double expectedT = -i/logP;
			double expectedDeltaT = (expectedT - 0.001) / 90;
			assertEquals("Checking initial temperature", expectedT, c.getTemperature(), EPSILON);
			assertEquals("Checking initial deltaT", expectedDeltaT, c.getDeltaT(), EPSILON);
			assertEquals("Checking initial steps", 1, c.getSteps());
		}
		c.init(10011);
		for (int j = 0; j < 10; j++) {
			assertEquals("temperature shouldn't be set yet", 0.0, c.getTemperature(), EPSILON);
			assertEquals("deltaT shouldn't be set yet", 0.0, c.getDeltaT(), EPSILON);
			assertEquals("steps shouldn't be set yet", 0, c.getSteps());
			// forcing initial temperature to be tiny, so we can force steps > 1
			assertTrue("Should accept all if params not set yet", c.accept(1.0001, 1));
		}
		assertEquals("Checking initial temperature", 0.002, c.getTemperature(), EPSILON);
		assertEquals("Checking initial deltaT", 0.001 / 625, c.getDeltaT(), EPSILON);
		assertEquals("Checking initial steps", 16, c.getSteps());
		c.init(10012);
		for (int j = 0; j < 10; j++) {
			assertEquals("temperature shouldn't be set yet", 0.0, c.getTemperature(), EPSILON);
			assertEquals("deltaT shouldn't be set yet", 0.0, c.getDeltaT(), EPSILON);
			assertEquals("steps shouldn't be set yet", 0, c.getSteps());
			// forcing initial temperature to be tiny, so we can force steps > 1
			assertTrue("Should accept all if params not set yet", c.accept(1.0001, 1));
		}
		assertEquals("Checking initial temperature", 0.002, c.getTemperature(), EPSILON);
		assertEquals("Checking initial deltaT", 0.001 / 626, c.getDeltaT(), EPSILON);
		assertEquals("Checking initial steps", 16, c.getSteps());
	}
	
	@Test
	public void testCooling() {
		double logP = Math.log(0.95);
		
		ParameterFreeLinearCooling c = new ParameterFreeLinearCooling();
		for (int i = 1; i <= 4; i *= 2) {
			c.init(101);
			for (int j = 0; j < 10; j++) {
				c.accept(i+1, 1);
			}
			double expectedT = -i/logP;
			double expectedDeltaT = (expectedT - 0.001) / 90;
			for (int j = 10; j < 100; j++) {
				assertTrue("Shouldn't hit min temp until specified maxEvals", c.getTemperature() > 0.001);
				c.accept(i+1, 1);
				expectedT -= expectedDeltaT;
				assertEquals("verifying cools correctly", expectedT, c.getTemperature(), EPSILON);
			}
			assertEquals("verifying hits correct min temp", 0.001, c.getTemperature(), EPSILON);
		}
		c.init(10011);
		for (int j = 0; j < 10; j++) {
			c.accept(1.0001, 1);
		}
		double expectedT = 0.002;
		double expectedDeltaT = 0.001 / 625;
		for (int j = 10; j < 10010; j++) {
			assertTrue("Shouldn't hit min temp until specified maxEvals", c.getTemperature() > 0.001);
			c.accept(1.0001, 1);
			if (j % 16 == 9) expectedT -= expectedDeltaT;
			assertEquals("verifying cools correctly", expectedT, c.getTemperature(), EPSILON);
		}
		assertEquals("verifying hits correct min temp", 0.001, c.getTemperature(), EPSILON);
		c.init(10012);
		for (int j = 0; j < 10; j++) {
			c.accept(1.0001, 1);
		}
		expectedT = 0.002;
		expectedDeltaT = 0.001 / 626;
		for (int j = 10; j < 10026; j++) {
			assertTrue("Shouldn't hit min temp until specified maxEvals", c.getTemperature() > 0.001);
			c.accept(1.0001, 1);
			if (j % 16 == 9) expectedT -= expectedDeltaT;
			assertEquals("verifying cools correctly", expectedT, c.getTemperature(), EPSILON);
		}
		assertEquals("verifying hits correct min temp", 0.001, c.getTemperature(), EPSILON);
	}
	
	@Test
	public void testAcceptanceAndRejection() {
		ParameterFreeLinearCooling c = new ParameterFreeLinearCooling();
		c.init(1030);
		for (int j = 0; j < 10; j++) {
			c.accept(2, 1);
		}
		// verify accept correctly
		for (int i = 0; i < 10; i++) {
			assertTrue("should always accept lower or same cost neighbors", c.accept(i, 9));
		}
		// verify accept will reject by passing infinite cost. 
		for (int i = 0; i < 10; i++) {
			assertFalse("should reject if neighbor cost is significantly above current cost", c.accept(Double.POSITIVE_INFINITY, 9));
		}
		int count = 0;
		for (int i = 0; i < 1000; i++) {
			if (c.accept(10001 + RandomIndexer.nextInt(5), 10000)) count++;
		}
		assertTrue("Verify accepts some higher cost neighbors", count > 0);
		assertTrue("Verify rejects some higher cost neighbors", count < 1000);
	}
}