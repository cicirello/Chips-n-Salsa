/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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
 * JUnit 4 test cases for continuous function optimization problems.
 */
public class ContinuousTests {
	
	@Test
	public void testForresterEtAl2008() {
		ForresterEtAl2008 f = new ForresterEtAl2008();
		SingleReal[] cases = { new SingleReal(0), new SingleReal(1), new SingleReal(2) };
		double[] expected = { 4*0.7568024953, 16*0.98935824662, 100*0.91294525072};
		for (int i = 0; i < cases.length; i++) {
			assertEquals(expected[i], f.value(cases[i]), 1E-8);
			assertEquals(expected[i], f.cost(cases[i]), 1E-8);
		}
		ForresterEtAl2008 f2 = f.split();
		assertTrue(f!=f2);
		for (int i = 0; i < cases.length; i++) {
			assertEquals(expected[i], f2.value(cases[i]), 1E-8);
			assertEquals(expected[i], f2.cost(cases[i]), 1E-8);
		}
	}
	
	@Test
	public void testForresterEtAl2008LowFidelity() {
		ForresterEtAl2008 f = new ForresterEtAl2008(true);
		SingleReal[] cases = { new SingleReal(0), new SingleReal(1), new SingleReal(2) };
		double[] expected = { 0.5*4*0.7568024953, 0.5*16*0.98935824662 + 10, 0.5*100*0.91294525072 + 20};
		for (int i = 0; i < cases.length; i++) {
			assertEquals(expected[i], f.value(cases[i]), 1E-8);
			assertEquals(expected[i], f.cost(cases[i]), 1E-8);
		}
		ForresterEtAl2008 f2 = f.split();
		assertTrue(f!=f2);
		for (int i = 0; i < cases.length; i++) {
			assertEquals(expected[i], f2.value(cases[i]), 1E-8);
			assertEquals(expected[i], f2.cost(cases[i]), 1E-8);
		}
	}
	
	@Test
	public void testForresterEtAl2008_createCandidateSolution() {
		ForresterEtAl2008 f = new ForresterEtAl2008();
		for (int i = 0; i < 10; i++) {
			SingleReal r = f.createCandidateSolution();
			double d = r.get();
			assertTrue(d >= 0.0 && d <= 1.0);
		}
	}
}