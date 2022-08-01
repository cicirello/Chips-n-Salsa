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

import org.cicirello.search.ProgressTracker;
import org.cicirello.search.representations.BitVector;
import org.cicirello.search.problems.OneMax;
import org.cicirello.search.problems.Plateaus;

/**
 * JUnit test cases for the OnePlusOneGeneticAlgorithm class.
 */
public class OnePlusOneGATests {
	
	@Test
	public void testIntegerCostProblemWithTracker() {
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		OneMax problem = new OneMax();
		OnePlusOneGeneticAlgorithm ga = new OnePlusOneGeneticAlgorithm(problem, 0.05, 20, tracker); 
		ga.optimize(1);
		assertEquals(tracker, ga.getProgressTracker());
		assertEquals(1, ga.getTotalRunLength());
		assertEquals(problem, ga.getProblem());
		assertTrue(tracker.containsIntCost());
	}
	
	@Test
	public void testIntegerCostProblemNoTracker() {
		OneMax problem = new OneMax();
		OnePlusOneGeneticAlgorithm ga = new OnePlusOneGeneticAlgorithm(problem, 0.05, 20); 
		ga.optimize(1);
		assertEquals(1, ga.getTotalRunLength());
		assertEquals(problem, ga.getProblem());
		assertTrue(ga.getProgressTracker().containsIntCost());
	}
	
	@Test
	public void testDoubleCostProblemWithTracker() {
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		Plateaus problem = new Plateaus();
		OnePlusOneGeneticAlgorithm ga = new OnePlusOneGeneticAlgorithm(problem, 0.05, 20, tracker); 
		ga.optimize(1);
		assertEquals(tracker, ga.getProgressTracker());
		assertEquals(1, ga.getTotalRunLength());
		assertEquals(problem, ga.getProblem());
		assertFalse(tracker.containsIntCost());
	}
	
	@Test
	public void testDoubleCostProblemNoTracker() {
		Plateaus problem = new Plateaus();
		OnePlusOneGeneticAlgorithm ga = new OnePlusOneGeneticAlgorithm(problem, 0.05, 20); 
		ga.optimize(1);
		assertEquals(1, ga.getTotalRunLength());
		assertEquals(problem, ga.getProblem());
		assertFalse(ga.getProgressTracker().containsIntCost());
	}
	
	@Test
	public void testSplit() {
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		OneMax problem = new OneMax();
		OnePlusOneGeneticAlgorithm ga = new OnePlusOneGeneticAlgorithm(problem, 0.05, 20, tracker); 
		ga.optimize(1);
		assertEquals(tracker, ga.getProgressTracker());
		assertEquals(1, ga.getTotalRunLength());
		assertEquals(problem, ga.getProblem());
		assertTrue(tracker.containsIntCost());
		
		OnePlusOneGeneticAlgorithm split = ga.split();
		assertNotSame(ga, split);
		split.optimize(1);
		ga.optimize(1);
		assertEquals(tracker, split.getProgressTracker());
		assertEquals(1, split.getTotalRunLength());
		assertEquals(problem, split.getProblem());
		assertTrue(tracker.containsIntCost());
		assertEquals(tracker, ga.getProgressTracker());
		assertEquals(2, ga.getTotalRunLength());
		assertEquals(problem, ga.getProblem());
		assertTrue(tracker.containsIntCost());
	}
}
