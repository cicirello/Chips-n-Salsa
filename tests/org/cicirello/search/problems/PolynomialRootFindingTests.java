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
 
package org.cicirello.search.problems;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.search.representations.RealValued;

/**
 * JUnit 4 test cases for the PolynomialRootFinding.
 */
public class PolynomialRootFindingTests {
	
	@Test
	public void testCostQuadratic() {
		SingleReal x = new SingleReal(5);
		
		PolynomialRootFinding p = new PolynomialRootFinding(1, 0, 0);
		assertEquals(25.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 1, 0);
		assertEquals(5.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 0, 1);
		assertEquals(1.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(1, 1, 0);
		assertEquals(30.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(1, 0, 1);
		assertEquals(26.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(1, 1, 1);
		assertEquals(31.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 1, 1);
		assertEquals(6.0, p.cost(x), 1E-10);
		
		p = new PolynomialRootFinding(2, 0, 0);
		assertEquals(50.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 2, 0);
		assertEquals(10.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 0, 2);
		assertEquals(2.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(2, 2, 0);
		assertEquals(60.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(2, 0, 2);
		assertEquals(52.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(2, 2, 2);
		assertEquals(62.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 2, 2);
		assertEquals(12.0, p.cost(x), 1E-10);
		
		p = new PolynomialRootFinding(1, 0, 0, 0.001);
		assertEquals(25.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 1, 0, 0.001);
		assertEquals(5.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 0, 1, 0.001);
		assertEquals(1.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(1, 1, 0, 0.001);
		assertEquals(30.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(1, 0, 1, 0.001);
		assertEquals(26.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(1, 1, 1, 0.001);
		assertEquals(31.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 1, 1, 0.001);
		assertEquals(6.0, p.cost(x), 1E-10);
		
		p = new PolynomialRootFinding(2, 0, 0, 0.001);
		assertEquals(50.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 2, 0, 0.001);
		assertEquals(10.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 0, 2, 0.001);
		assertEquals(2.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(2, 2, 0, 0.001);
		assertEquals(60.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(2, 0, 2, 0.001);
		assertEquals(52.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(2, 2, 2, 0.001);
		assertEquals(62.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(0, 2, 2, 0.001);
		assertEquals(12.0, p.cost(x), 1E-10);
	}
	
	@Test
	public void testCostPoly() {
		SingleReal x = new SingleReal(5);
		
		PolynomialRootFinding p = new PolynomialRootFinding(new double[] {0, 0, 1});
		assertEquals(25.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 1, 0});
		assertEquals(5.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 0, 0});
		assertEquals(1.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 1, 1});
		assertEquals(30.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 0, 1});
		assertEquals(26.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 1, 1});
		assertEquals(31.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 1, 0});
		assertEquals(6.0, p.cost(x), 1E-10);
		
		p = new PolynomialRootFinding(new double[] {0, 0, 2});
		assertEquals(50.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 2, 0});
		assertEquals(10.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 0, 0});
		assertEquals(2.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 2, 2});
		assertEquals(60.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 0, 2});
		assertEquals(52.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 2, 2});
		assertEquals(62.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 2, 0});
		assertEquals(12.0, p.cost(x), 1E-10);
		
		p = new PolynomialRootFinding(new double[] {0, 0, 1}, 0.001);
		assertEquals(25.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 1, 0}, 0.001);
		assertEquals(5.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 0, 0}, 0.001);
		assertEquals(1.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 1, 1}, 0.001);
		assertEquals(30.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 0, 1}, 0.001);
		assertEquals(26.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 1, 1}, 0.001);
		assertEquals(31.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 1, 0}, 0.001);
		assertEquals(6.0, p.cost(x), 1E-10);
		
		p = new PolynomialRootFinding(new double[] {0, 0, 2}, 0.001);
		assertEquals(50.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 2, 0}, 0.001);
		assertEquals(10.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 0, 0}, 0.001);
		assertEquals(2.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 2, 2}, 0.001);
		assertEquals(60.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 0, 2}, 0.001);
		assertEquals(52.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 2, 2}, 0.001);
		assertEquals(62.0, p.cost(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 2, 0}, 0.001);
		assertEquals(12.0, p.cost(x), 1E-10);
	}
	
	@Test
	public void testValueQuadratic() {
		SingleReal x = new SingleReal(5);
		
		PolynomialRootFinding p = new PolynomialRootFinding(1, 0, 0);
		assertEquals(25.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 1, 0);
		assertEquals(5.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 0, 1);
		assertEquals(1.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(1, 1, 0);
		assertEquals(30.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(1, 0, 1);
		assertEquals(26.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(1, 1, 1);
		assertEquals(31.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 1, 1);
		assertEquals(6.0, p.value(x), 1E-10);
		
		p = new PolynomialRootFinding(2, 0, 0);
		assertEquals(50.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 2, 0);
		assertEquals(10.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 0, 2);
		assertEquals(2.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(2, 2, 0);
		assertEquals(60.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(2, 0, 2);
		assertEquals(52.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(2, 2, 2);
		assertEquals(62.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 2, 2);
		assertEquals(12.0, p.value(x), 1E-10);
		
		p = new PolynomialRootFinding(1, 0, 0, 0.001);
		assertEquals(25.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 1, 0, 0.001);
		assertEquals(5.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 0, 1, 0.001);
		assertEquals(1.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(1, 1, 0, 0.001);
		assertEquals(30.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(1, 0, 1, 0.001);
		assertEquals(26.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(1, 1, 1, 0.001);
		assertEquals(31.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 1, 1, 0.001);
		assertEquals(6.0, p.value(x), 1E-10);
		
		p = new PolynomialRootFinding(2, 0, 0, 0.001);
		assertEquals(50.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 2, 0, 0.001);
		assertEquals(10.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 0, 2, 0.001);
		assertEquals(2.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(2, 2, 0, 0.001);
		assertEquals(60.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(2, 0, 2, 0.001);
		assertEquals(52.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(2, 2, 2, 0.001);
		assertEquals(62.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(0, 2, 2, 0.001);
		assertEquals(12.0, p.value(x), 1E-10);
	}
	
	@Test
	public void testValuePoly() {
		SingleReal x = new SingleReal(5);
		
		PolynomialRootFinding p = new PolynomialRootFinding(new double[] {0, 0, 1});
		assertEquals(25.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 1, 0});
		assertEquals(5.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 0, 0});
		assertEquals(1.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 1, 1});
		assertEquals(30.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 0, 1});
		assertEquals(26.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 1, 1});
		assertEquals(31.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 1, 0});
		assertEquals(6.0, p.value(x), 1E-10);
		
		p = new PolynomialRootFinding(new double[] {0, 0, 2});
		assertEquals(50.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 2, 0});
		assertEquals(10.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 0, 0});
		assertEquals(2.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 2, 2});
		assertEquals(60.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 0, 2});
		assertEquals(52.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 2, 2});
		assertEquals(62.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 2, 0});
		assertEquals(12.0, p.value(x), 1E-10);
		
		p = new PolynomialRootFinding(new double[] {0, 0, 1}, 0.001);
		assertEquals(25.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 1, 0}, 0.001);
		assertEquals(5.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 0, 0}, 0.001);
		assertEquals(1.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 1, 1}, 0.001);
		assertEquals(30.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 0, 1}, 0.001);
		assertEquals(26.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 1, 1}, 0.001);
		assertEquals(31.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {1, 1, 0}, 0.001);
		assertEquals(6.0, p.value(x), 1E-10);
		
		p = new PolynomialRootFinding(new double[] {0, 0, 2}, 0.001);
		assertEquals(50.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 2, 0}, 0.001);
		assertEquals(10.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 0, 0}, 0.001);
		assertEquals(2.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {0, 2, 2}, 0.001);
		assertEquals(60.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 0, 2}, 0.001);
		assertEquals(52.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 2, 2}, 0.001);
		assertEquals(62.0, p.value(x), 1E-10);
		p = new PolynomialRootFinding(new double[] {2, 2, 0}, 0.001);
		assertEquals(12.0, p.value(x), 1E-10);
	}
	
	@Test
	public void testMinCost() {
		PolynomialRootFinding p1 = new PolynomialRootFinding(1, 2, 3);
		assertEquals(0.0, p1.minCost(), 1E-10);
		PolynomialRootFinding p2 = new PolynomialRootFinding(1, 2, 3, 0.001);
		assertEquals(0.0, p2.minCost(), 1E-10);
		double[] c = {1, 2, 3};
		PolynomialRootFinding p3 = new PolynomialRootFinding(c);
		assertEquals(0.0, p3.minCost(), 1E-10);
		PolynomialRootFinding p4 = new PolynomialRootFinding(c, 0.001);
		assertEquals(0.0, p4.minCost(), 1E-10);
	}
	
	@Test
	public void testIsMinCost() {
		PolynomialRootFinding p1 = new PolynomialRootFinding(1, 2, 3);
		assertTrue(p1.isMinCost(PolynomialRootFinding.DEFAULT_PRECISION));
		assertFalse(p1.isMinCost(1.001*PolynomialRootFinding.DEFAULT_PRECISION));
		assertTrue(p1.isMinCost(0.999*PolynomialRootFinding.DEFAULT_PRECISION));
		PolynomialRootFinding p2 = new PolynomialRootFinding(1, 2, 3, 0.001);
		assertTrue(p2.isMinCost(0.001));
		assertFalse(p2.isMinCost(0.001001));
		assertTrue(p2.isMinCost(0.000999));
		double[] c = {1, 2, 3};
		PolynomialRootFinding p3 = new PolynomialRootFinding(c);
		assertTrue(p3.isMinCost(PolynomialRootFinding.DEFAULT_PRECISION));
		assertFalse(p3.isMinCost(1.001*PolynomialRootFinding.DEFAULT_PRECISION));
		assertTrue(p3.isMinCost(0.999*PolynomialRootFinding.DEFAULT_PRECISION));
		PolynomialRootFinding p4 = new PolynomialRootFinding(c, 0.001);
		assertTrue(p4.isMinCost(0.001));
		assertFalse(p4.isMinCost(0.001001));
		assertTrue(p4.isMinCost(0.000999));
	}
}