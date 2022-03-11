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
 
package org.cicirello.search.sa;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.math.rand.RandomIndexer;

/**
 * JUnit test cases for the parameter free linear cooling.
 */
public class ParamFreeLinearCoolingTests {
	
	private static final double EPSILON = 1e-10;
	
	@Test
	public void testSplit() {
		double logP = Math.log(0.95);
		ParameterFreeLinearCooling cOriginal = new ParameterFreeLinearCooling();
		cOriginal.init(500);
		for (int i = 0; i < 10; i++) cOriginal.accept(3, 2);
		ParameterFreeLinearCooling c = cOriginal.split();
		c.init(101);
		for (int j = 0; j < 10; j++) {
			assertEquals(0.0, c.getTemperature(), EPSILON);
			assertEquals(0.0, c.getDeltaT(), EPSILON);
			assertEquals(0, c.getSteps());
			assertTrue(c.accept(2, 1));
		}
		double expectedT = -1/logP;
		double expectedDeltaT = (expectedT - 0.001) / 90;
		assertEquals(expectedT, c.getTemperature(), EPSILON);
		assertEquals(expectedDeltaT, c.getDeltaT(), EPSILON);
		assertEquals(1, c.getSteps());	
	}
	
	@Test
	public void testInitialParamEstimates() {
		double logP = Math.log(0.95);
		
		ParameterFreeLinearCooling c = new ParameterFreeLinearCooling();
		for (int i = 1; i <= 4; i *= 2) {
			c.init(101);
			for (int j = 0; j < 10; j++) {
				assertEquals(0.0, c.getTemperature(), EPSILON);
				assertEquals(0.0, c.getDeltaT(), EPSILON);
				assertEquals(0, c.getSteps());
				assertTrue(c.accept(i+1, 1));
			}
			double expectedT = -i/logP;
			double expectedDeltaT = (expectedT - 0.001) / 90;
			assertEquals(expectedT, c.getTemperature(), EPSILON);
			assertEquals(expectedDeltaT, c.getDeltaT(), EPSILON);
			assertEquals(1, c.getSteps());
		}
		for (int i = 1; i <= 4; i *= 2) {
			c.init(101);
			for (int j = 0; j < 10; j++) {
				assertEquals(0.0, c.getTemperature(), EPSILON);
				assertEquals(0.0, c.getDeltaT(), EPSILON);
				assertEquals(0, c.getSteps());
				assertTrue(c.accept(1, 1+i));
			}
			double expectedT = -i/logP;
			double expectedDeltaT = (expectedT - 0.001) / 90;
			assertEquals(expectedT, c.getTemperature(), EPSILON);
			assertEquals(expectedDeltaT, c.getDeltaT(), EPSILON);
			assertEquals(1, c.getSteps());
		}
		// Make sure same cost leads to extra estimation iteration
		for (int i = 1; i <= 4; i *= 2) {
			c.init(101);
			c.accept(5, 5);
			for (int j = 0; j < 10; j++) {
				assertEquals(0.0, c.getTemperature(), EPSILON);
				assertEquals(0.0, c.getDeltaT(), EPSILON);
				assertEquals(0, c.getSteps());
				assertTrue(c.accept(i+1, 1));
			}
			double expectedT = -i/logP;
			double expectedDeltaT = (expectedT - 0.001) / 89;
			assertEquals(expectedT, c.getTemperature(), EPSILON);
			assertEquals(expectedDeltaT, c.getDeltaT(), EPSILON);
			assertEquals(1, c.getSteps());
		}
		// Force initial t to be small
		{
			c.init(101);
			double diff = -0.02 * Math.log(0.95) / 20.0;
			for (int j = 0; j < 10; j++) {
				assertEquals(0.0, c.getTemperature(), EPSILON);
				assertEquals(0.0, c.getDeltaT(), EPSILON);
				assertEquals(0, c.getSteps());
				assertTrue(c.accept(diff+1, 1));
			}
			double expectedT = 0.002;
			double expectedDeltaT = (expectedT - 0.001) / 90;
			assertEquals(expectedT, c.getTemperature(), EPSILON);
			assertEquals(expectedDeltaT, c.getDeltaT(), EPSILON);
			assertEquals(1, c.getSteps());
		}
		
		
		c.init(10011);
		for (int j = 0; j < 10; j++) {
			assertEquals(0.0, c.getTemperature(), EPSILON);
			assertEquals(0.0, c.getDeltaT(), EPSILON);
			assertEquals(0, c.getSteps());
			// forcing initial temperature to be tiny, so we can force steps > 1
			assertTrue(c.accept(1.0001, 1));
		}
		assertEquals(0.002, c.getTemperature(), EPSILON);
		assertEquals(0.001 / 625, c.getDeltaT(), EPSILON);
		assertEquals(16, c.getSteps());
		c.init(10012);
		for (int j = 0; j < 10; j++) {
			assertEquals(0.0, c.getTemperature(), EPSILON);
			assertEquals(0.0, c.getDeltaT(), EPSILON);
			assertEquals(0, c.getSteps());
			// forcing initial temperature to be tiny, so we can force steps > 1
			assertTrue(c.accept(1.0001, 1));
		}
		assertEquals(0.002, c.getTemperature(), EPSILON);
		assertEquals(0.001 / 626, c.getDeltaT(), EPSILON);
		assertEquals(16, c.getSteps());
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
				assertTrue(c.getTemperature() > 0.001);
				c.accept(i+1, 1);
				expectedT -= expectedDeltaT;
				assertEquals(expectedT, c.getTemperature(), EPSILON);
			}
			assertEquals(0.001, c.getTemperature(), EPSILON);
		}
		c.init(10011);
		for (int j = 0; j < 10; j++) {
			c.accept(1.0001, 1);
		}
		double expectedT = 0.002;
		double expectedDeltaT = 0.001 / 625;
		for (int j = 10; j < 10010; j++) {
			assertTrue(c.getTemperature() > 0.001);
			c.accept(1.0001, 1);
			if (j % 16 == 9) expectedT -= expectedDeltaT;
			assertEquals(expectedT, c.getTemperature(), EPSILON);
		}
		assertEquals(0.001, c.getTemperature(), EPSILON);
		c.init(10012);
		for (int j = 0; j < 10; j++) {
			c.accept(1.0001, 1);
		}
		expectedT = 0.002;
		expectedDeltaT = 0.001 / 626;
		for (int j = 10; j < 10026; j++) {
			assertTrue(c.getTemperature() > 0.001);
			c.accept(1.0001, 1);
			if (j % 16 == 9) expectedT -= expectedDeltaT;
			assertEquals(expectedT, c.getTemperature(), EPSILON);
		}
		assertEquals(0.001, c.getTemperature(), EPSILON);
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
			assertTrue(c.accept(i, 9));
		}
		// verify accept will reject by passing infinite cost. 
		for (int i = 0; i < 10; i++) {
			assertFalse(c.accept(Double.POSITIVE_INFINITY, 9));
		}
		int count = 0;
		for (int i = 0; i < 1000; i++) {
			if (c.accept(10001 + RandomIndexer.nextInt(5), 10000)) count++;
		}
		assertTrue(count > 0);
		assertTrue(count < 1000);
	}
}