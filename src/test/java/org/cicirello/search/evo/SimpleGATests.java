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

package org.cicirello.search.evo;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.OneMax;
import org.cicirello.search.representations.BitVector;

/**
 * JUnit test cases for SimpleGeneticAlgorithm.
 */
public class SimpleGATests {
	
	// Note that the SimpleGeneticAlgorithm simply relies upon superclass for
	// most of the work. Mainly need to test constructors behave correctly.
	
	@Test
	public void testDoubleFitnessWithProgressTracker() {
		double M = 0.1;
		double C = 0.75;
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm(
			n,
			L,
			new InverseCostFitnessFunction<BitVector>(problem),
			M,
			C,
			tracker
		);
		assertTrue(tracker == ga.getProgressTracker());
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(tracker.getCostDouble(), solution.getCostDouble(), 1E-10);
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertEquals(b.countZeros(), solution.getCostDouble(), 1E-10);
	}
	
	@Test
	public void testIntegerFitnessWithProgressTracker() {
		double M = 0.1;
		double C = 0.75;
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm(
			n,
			L,
			new NegativeIntegerCostFitnessFunction<BitVector>(problem),
			M,
			C,
			tracker
		);
		assertTrue(tracker == ga.getProgressTracker());
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(tracker.getCost(), solution.getCost());
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertEquals(b.countZeros(), solution.getCost());
	}
	
	@Test
	public void testDoubleFitnessNoProgressTracker() {
		double M = 0.1;
		double C = 0.75;
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm(
			n,
			L,
			new InverseCostFitnessFunction<BitVector>(problem),
			M,
			C
		);
		ProgressTracker<BitVector> tracker =  ga.getProgressTracker();
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(tracker.getCostDouble(), solution.getCostDouble(), 1E-10);
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertEquals(b.countZeros(), solution.getCostDouble(), 1E-10);
		
		SimpleGeneticAlgorithm ga2 = ga.split();
		assertTrue(tracker == ga2.getProgressTracker());
		assertTrue(problem == ga2.getProblem());
		solution = ga2.optimize(1);
		b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(b.countZeros(), solution.getCostDouble(), 1E-10);
	}
	
	@Test
	public void testIntegerFitnessNoProgressTracker() {
		double M = 0.1;
		double C = 0.75;
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		SimpleGeneticAlgorithm ga = new SimpleGeneticAlgorithm(
			n,
			L,
			new NegativeIntegerCostFitnessFunction<BitVector>(problem),
			M,
			C
		);
		ProgressTracker<BitVector> tracker =  ga.getProgressTracker();
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(tracker.getCost(), solution.getCost());
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertEquals(b.countZeros(), solution.getCost());
		
		SimpleGeneticAlgorithm ga2 = ga.split();
		assertTrue(tracker == ga2.getProgressTracker());
		assertTrue(problem == ga2.getProblem());
		solution = ga2.optimize(1);
		b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(b.countZeros(), solution.getCost());
	}
}
