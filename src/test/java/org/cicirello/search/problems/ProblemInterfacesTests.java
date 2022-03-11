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
 
package org.cicirello.search.problems;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;

/**
 * JUnit test cases for the default methods of 
 * the various problem related interfaces.
 */
public class ProblemInterfacesTests {
	
	@Test
	public void testOptimizationProblem() {
		OptimizationProblem<Permutation> defaults = new OptimizationProblem<Permutation>() {
			public double cost(Permutation candidate) { 
				return 25;
			}
			public double value(Permutation candidate) { 
				return 25;
			}
			public double minCost() { return 9.0; }
		};
		assertFalse(defaults.isMinCost(10));
		assertTrue(defaults.isMinCost(9));
		assertEquals(25.0, defaults.costAsDouble(new Permutation(1)), 1E-10);
	}
	
	@Test
	public void testIntegerCostOptimizationProblem() {
		IntegerCostOptimizationProblem<Permutation> defaults = new IntegerCostOptimizationProblem<Permutation>() {
			public int cost(Permutation candidate) { 
				return 25;
			}
			public int value(Permutation candidate) { 
				return 25;
			}
			public int minCost() { return 9; }
		};
		assertFalse(defaults.isMinCost(10));
		assertTrue(defaults.isMinCost(9));
		assertEquals(25.0, defaults.costAsDouble(new Permutation(1)), 1E-10);
	}

}