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
import org.cicirello.util.Copyable;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;

/**
 * JUnit test cases for EvolvableParametersPopulation.
 */
public class EvolvableParametersPopulationTests {
	
	private static final double EPSILON = 1e-10;
	
	@Test
	public void testExceptions() {
		NullPointerException thrown = assertThrows( 
			NullPointerException.class,
			() -> new EvolvableParametersPopulation.Double<TestObject>(10, null, new TestFitnessDouble(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0, 2)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new EvolvableParametersPopulation.Double<TestObject>(10, new TestInitializer(), null, new TestSelectionOp(), new ProgressTracker<TestObject>(), 0, 2)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new EvolvableParametersPopulation.Double<TestObject>(10, new TestInitializer(), new TestFitnessDouble(), null, new ProgressTracker<TestObject>(), 0, 2)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new EvolvableParametersPopulation.Double<TestObject>(10, new TestInitializer(), new TestFitnessDouble(), new TestSelectionOp(), null, 0, 2)
		);
		
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new EvolvableParametersPopulation.Integer<TestObject>(10, null, new TestFitnessInteger(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0, 2)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new EvolvableParametersPopulation.Integer<TestObject>(10, new TestInitializer(), null, new TestSelectionOp(), new ProgressTracker<TestObject>(), 0, 2)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new EvolvableParametersPopulation.Integer<TestObject>(10, new TestInitializer(), new TestFitnessInteger(), null, new ProgressTracker<TestObject>(), 0, 2)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new EvolvableParametersPopulation.Integer<TestObject>(10, new TestInitializer(), new TestFitnessInteger(), new TestSelectionOp(), null, 0, 2)
		);
		
		IllegalArgumentException thrown2 = assertThrows( 
			IllegalArgumentException.class,
			() -> new EvolvableParametersPopulation.Double<TestObject>(0, new TestInitializer(), new TestFitnessDouble(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0, 2)
		);
		thrown2 = assertThrows( 
			IllegalArgumentException.class,
			() -> new EvolvableParametersPopulation.Integer<TestObject>(0, new TestInitializer(), new TestFitnessInteger(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0, 2)
		);
		thrown2 = assertThrows( 
			IllegalArgumentException.class,
			() -> new EvolvableParametersPopulation.Double<TestObject>(10, new TestInitializer(), new TestFitnessDouble(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 10, 2)
		);
		thrown2 = assertThrows( 
			IllegalArgumentException.class,
			() -> new EvolvableParametersPopulation.Integer<TestObject>(10, new TestInitializer(), new TestFitnessInteger(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 10, 2)
		);
	}
	
	@Test
	public void testEvolvableParametersPopulation_getParameter_Double() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		EvolvableParametersPopulation.Double<TestObject> pop = new EvolvableParametersPopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			2
		);
		pop.init();
		pop.select();
		boolean allSame0 = true;
		boolean allSame1 = true;
		double last0 = -1;
		double last1 = -1;
		for (int i = 0; i < 10; i++) {
			double param0 = pop.getParameter(i, 0).get();
			double param1 = pop.getParameter(i, 1).get();
			assertTrue(param0 >= 0.0 && param0 <= 1.0);
			assertTrue(param1 >= 0.0 && param1 <= 1.0);
			if (i > 0) {
				if (allSame0) {
					allSame0 = param0 == last0;
				}
				if (allSame1) {
					allSame1 = param1 == last1;
				}
			}
			last0 = param0;
			last1 = param1;
		}
		assertFalse(allSame0);
		assertFalse(allSame1);
		
		pop = new EvolvableParametersPopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			1
		);
		pop.init();
		pop.select();
		allSame0 = true;
		for (int i = 0; i < 10; i++) {
			double param0 = pop.getParameter(i, 0).get();
			assertTrue(param0 >= 0.0 && param0 <= 1.0);
			if (i > 0) {
				if (allSame0) {
					allSame0 = param0 == last0;
				}
			}
			last0 = param0;
		}
		assertFalse(allSame0);
	}
	
	@Test
	public void testEvolvableParametersPopulation_getParameter_Integer() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		EvolvableParametersPopulation.Integer<TestObject> pop = new EvolvableParametersPopulation.Integer<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			2
		);
		pop.init();
		pop.select();
		boolean allSame0 = true;
		boolean allSame1 = true;
		double last0 = -1;
		double last1 = -1;
		for (int i = 0; i < 10; i++) {
			double param0 = pop.getParameter(i, 0).get();
			double param1 = pop.getParameter(i, 1).get();
			assertTrue(param0 >= 0.0 && param0 <= 1.0);
			assertTrue(param1 >= 0.0 && param1 <= 1.0);
			if (i > 0) {
				if (allSame0) {
					allSame0 = param0 == last0;
				}
				if (allSame1) {
					allSame1 = param1 == last1;
				}
			}
			last0 = param0;
			last1 = param1;
		}
		assertFalse(allSame0);
		assertFalse(allSame1);
		
		pop = new EvolvableParametersPopulation.Integer<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			1
		);
		pop.init();
		pop.select();
		allSame0 = true;
		for (int i = 0; i < 10; i++) {
			double param0 = pop.getParameter(i, 0).get();
			assertTrue(param0 >= 0.0 && param0 <= 1.0);
			if (i > 0) {
				if (allSame0) {
					allSame0 = param0 == last0;
				}
			}
			last0 = param0;
		}
		assertFalse(allSame0);
	}
	
	@Test
	public void testEvolvableParametersPopulationElitismDouble() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		EvolvableParametersPopulation.Double<TestObject> pop = new EvolvableParametersPopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			3, 
			2
		);
		assertTrue(tracker == pop.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		pop.setProgressTracker(tracker);
		assertTrue(tracker == pop.getProgressTracker());
		
		pop.init();
		assertEquals(10, pop.size());
		assertEquals(7, pop.mutableSize());
		assertEquals(6.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble(), EPSILON);
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		int[] expected = { 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+0.4, pop.getFitness(i), EPSILON);
		}
		assertFalse(selection.called);
		pop.select();
		assertTrue(selection.called);
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+0.4, pop.getFitness(i), EPSILON);
			if (i < 7) {
				// subject to mutation to opposite order since we selected, which reversed.
				assertEquals(expected[i+3], pop.get(i).id);
			} 
		}
		assertEquals(6.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble(), EPSILON);
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		pop.replace();
		int[] expectedNow = { 5, 6, 5, 4, 3, 2, 1, 4, 6, 5 };
		for (int i = 0; i < 10; i++) {
			assertEquals(expectedNow[i]+0.4, pop.getFitness(i), EPSILON, "index i="+i);
		}
		pop.select();
		for (int i = 0; i < 7; i++) {
			assertEquals(expectedNow[6-i], pop.get(i).id);
		}
		assertEquals(6.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble(), EPSILON);
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		
		f.changeFitness(1);
		pop.updateFitness(1);
		f.changeFitness(10);
		pop.updateFitness(2);
		pop.replace();
		assertEquals(2+0.4+1, pop.getFitness(1), EPSILON);
		assertEquals(3+0.4+10, pop.getFitness(2), EPSILON);
		assertEquals(13.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(3, pop.getMostFit().getSolution().id);
		assertEquals(1.0/4.0, pop.getMostFit().getCostDouble(), EPSILON);
		
		int[] andNow = {1, 2, 3, 4, 5, 6, 5, 4, 6, 5};
		double[] andNowFitness = {1.4, 3.4, 13.4, 4.4, 5.4, 6.4, 5.4, 4.4, 6.4, 5.4};
		for (int i = 0; i < 10; i++) {
			assertEquals(andNowFitness[i], pop.getFitness(i), EPSILON, "index i="+i);
		}
		
		f.changeFitness(12);
		EvolvableParametersPopulation.Double<TestObject> pop2 = pop.split();
		
		// original should be same after split
		for (int i = 0; i < 10; i++) {
			assertEquals(andNowFitness[i], pop.getFitness(i), EPSILON, "index i="+i);
		}
		
		// trackers should be same
		assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());
		
		pop2.init();
		assertEquals(10, pop2.size());
		assertEquals(7, pop2.mutableSize());
		for (int i = 0; i < 10; i++) {
			assertEquals(1-i+12+0.4, pop2.getFitness(i), EPSILON);
		}
		
		// Original should be unchanged after split copy does stuff
		for (int i = 0; i < 10; i++) {
			assertEquals(andNowFitness[i], pop.getFitness(i), EPSILON, "index i="+i);
		}
		
		assertEquals(0, selection.initCalledWith);
		pop.initOperators(987);
		assertEquals(987, selection.initCalledWith);
	}
	
	@Test
	public void testEvolvableParametersPopulationDouble() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		EvolvableParametersPopulation.Double<TestObject> pop = new EvolvableParametersPopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			2
		);
		assertTrue(tracker == pop.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		pop.setProgressTracker(tracker);
		assertTrue(tracker == pop.getProgressTracker());
		
		pop.init();
		assertEquals(10, pop.size());
		assertEquals(10, pop.mutableSize());
		assertEquals(6.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble(), EPSILON);
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		int[] expected = { 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+0.4, pop.getFitness(i), EPSILON);
		}
		assertFalse(selection.called);
		pop.select();
		assertTrue(selection.called);
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+0.4, pop.getFitness(i), EPSILON);
			// subject to mutation to opposite order since we selected, which reversed.
			assertEquals(expected[i], pop.get(i).id);
			
		}
		assertEquals(6.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble(), EPSILON);
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		pop.replace();
		pop.select();
		for (int i = 0; i < 10; i++) {
			assertEquals(expected[9-i], pop.get(i).id);
			assertEquals(expected[i]+0.4, pop.getFitness(i), EPSILON);
		}
		assertEquals(6.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble(), EPSILON);
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		
		f.changeFitness(1);
		pop.updateFitness(0);
		f.changeFitness(10);
		pop.updateFitness(1);
		pop.replace();
		assertEquals(expected[9]+0.4+1, pop.getFitness(0), EPSILON);
		assertEquals(expected[8]+0.4+10, pop.getFitness(1), EPSILON);
		assertEquals(12.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(2, pop.getMostFit().getSolution().id);
		assertEquals(1.0/3.0, pop.getMostFit().getCostDouble(), EPSILON);
		
		f.changeFitness(12);
		EvolvableParametersPopulation.Double<TestObject> pop2 = pop.split();
		
		// orginal should be same
		assertEquals(expected[9]+0.4+1, pop.getFitness(0), EPSILON);
		assertEquals(expected[8]+0.4+10, pop.getFitness(1), EPSILON);
		assertEquals(12.4, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(2, pop.getMostFit().getSolution().id);
		assertEquals(1.0/3.0, pop.getMostFit().getCostDouble(), EPSILON);
		
		// trackers should be same
		assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());
		
		pop2.init();
		assertEquals(10, pop2.size());
		assertEquals(10, pop2.mutableSize());
		for (int i = 0; i < 10; i++) {
			assertEquals(1-i+12+0.4, pop2.getFitness(i), EPSILON);
		}
		
		assertFalse(pop.evolutionIsPaused());
		assertFalse(pop2.evolutionIsPaused());
		tracker.stop();
		assertTrue(pop.evolutionIsPaused());
		assertTrue(pop2.evolutionIsPaused());
		tracker.start();
		assertFalse(pop.evolutionIsPaused());
		assertFalse(pop2.evolutionIsPaused());
		tracker.update(0.0, new TestObject(), true);
		assertTrue(pop.evolutionIsPaused());
		assertTrue(pop2.evolutionIsPaused());
		
		assertEquals(0, selection.initCalledWith);
		pop.initOperators(987);
		assertEquals(987, selection.initCalledWith);
	}
	
	@Test
	public void testEvolvableParametersPopulationDouble_SelectCopies() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		EvolvableParametersPopulation.Double<TestObject> pop = new EvolvableParametersPopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			2
		);
		pop.init();
		pop.select();
		TestObject[] firstSelect = new TestObject[10];
		for (int i = 0; i < 10; i++) {
			firstSelect[i] = pop.get(i);
		}
		pop.replace();
		pop.select();
		TestObject[] secondSelect = new TestObject[10];
		for (int i = 0; i < 10; i++) {
			secondSelect[i] = pop.get(i);
		}
		for (int i = 0; i < 10; i++) {
			assertFalse(firstSelect[i] == secondSelect[9-i]);
			assertEquals(firstSelect[i], secondSelect[9-i]);
		}
	}
	
	@Test
	public void testEvolvableParametersPopulationDoubleIntCost() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
		EvolvableParametersPopulation.Double<TestObject> pop = new EvolvableParametersPopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			2
		);
		assertTrue(tracker == pop.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		pop.setProgressTracker(tracker);
		assertTrue(tracker == pop.getProgressTracker());
		
		pop.init();
		assertEquals(10, pop.size());
		assertEquals(10, pop.mutableSize());
		assertEquals(16.0, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		int[] expected = { 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+10.0, pop.getFitness(i), EPSILON);
		}
		assertFalse(selection.called);
		pop.select();
		assertTrue(selection.called);
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+10.0, pop.getFitness(i), EPSILON);
			// subject to mutation to opposite order since we selected, which reversed.
			assertEquals(expected[i], pop.get(i).id);
			
		}
		assertEquals(16.0, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		pop.replace();
		pop.select();
		for (int i = 0; i < 10; i++) {
			assertEquals(expected[9-i], pop.get(i).id);
			assertEquals(expected[i]+10.0, pop.getFitness(i), EPSILON);
		}
		assertEquals(16.0, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		
		f.changeFitness(1);
		pop.updateFitness(0);
		f.changeFitness(10);
		pop.updateFitness(1);
		pop.replace();
		assertEquals(expected[9]+10.0+1, pop.getFitness(0), EPSILON);
		assertEquals(expected[8]+10.0+10, pop.getFitness(1), EPSILON);
		assertEquals(22.0, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(2, pop.getMostFit().getSolution().id);
		assertEquals(98, pop.getMostFit().getCost());
		
		f.changeFitness(12);
		EvolvableParametersPopulation.Double<TestObject> pop2 = pop.split();
		
		// orginal should be same
		assertEquals(expected[9]+10.0+1, pop.getFitness(0), EPSILON);
		assertEquals(expected[8]+10.0+10, pop.getFitness(1), EPSILON);
		assertEquals(22.0, pop.getFitnessOfMostFit(), EPSILON);
		assertEquals(2, pop.getMostFit().getSolution().id);
		assertEquals(98, pop.getMostFit().getCost());
		
		// trackers should be same
		assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());
		
		pop2.init();
		assertEquals(10, pop2.size());
		assertEquals(10, pop2.mutableSize());
		for (int i = 0; i < 10; i++) {
			assertEquals(1-i+12+10.0, pop2.getFitness(i), EPSILON);
		}
		
		assertFalse(pop.evolutionIsPaused());
		assertFalse(pop2.evolutionIsPaused());
		tracker.stop();
		assertTrue(pop.evolutionIsPaused());
		assertTrue(pop2.evolutionIsPaused());
		tracker.start();
		assertFalse(pop.evolutionIsPaused());
		assertFalse(pop2.evolutionIsPaused());
		tracker.update(0.0, new TestObject(), true);
		assertTrue(pop.evolutionIsPaused());
		assertTrue(pop2.evolutionIsPaused());	
	}
	
	@Test
	public void testEvolvableParametersPopulationDoubleIntCost_SelectCopies() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
		EvolvableParametersPopulation.Double<TestObject> pop = new EvolvableParametersPopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			2
		);
		pop.init();
		pop.select();
		TestObject[] firstSelect = new TestObject[10];
		for (int i = 0; i < 10; i++) {
			firstSelect[i] = pop.get(i);
		}
		pop.replace();
		pop.select();
		TestObject[] secondSelect = new TestObject[10];
		for (int i = 0; i < 10; i++) {
			secondSelect[i] = pop.get(i);
		}
		for (int i = 0; i < 10; i++) {
			assertFalse(firstSelect[i] == secondSelect[9-i]);
			assertEquals(firstSelect[i], secondSelect[9-i]);
		}
	}
	
	@Test
	public void testEvolvableParametersPopulationElitismInteger() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		EvolvableParametersPopulation.Integer<TestObject> pop = new EvolvableParametersPopulation.Integer<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			3, 
			2
		);
		assertTrue(tracker == pop.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		pop.setProgressTracker(tracker);
		assertTrue(tracker == pop.getProgressTracker());
		
		pop.init();
		assertEquals(10, pop.size());
		assertEquals(7, pop.mutableSize());
		assertEquals(16, pop.getFitnessOfMostFit());
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		int[] expected = { 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+10, pop.getFitness(i));
		}
		assertFalse(selection.called);
		pop.select();
		assertTrue(selection.called);
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+10, pop.getFitness(i));
			if (i < 7) {
				// subject to mutation to opposite order since we selected, which reversed.
				assertEquals(expected[i+3], pop.get(i).id);
			} 
		}
		assertEquals(16, pop.getFitnessOfMostFit());
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		
		f.changeFitness(10);
		pop.updateFitness(4);
		
		pop.replace();
		int[] expectedNow = { 5, 6, 5, 4, 3, 2, 1, 4, 6, 5 };
		for (int i = 0; i < 10; i++) {
			if (i!=4) assertEquals(expectedNow[i]+10, pop.getFitness(i), "index i="+i);
			else assertEquals(expectedNow[i]+20, pop.getFitness(i), "index i="+i);
		}
		pop.select();
		for (int i = 0; i < 7; i++) {
			assertEquals(expectedNow[6-i], pop.get(i).id);
		}
		assertEquals(23, pop.getFitnessOfMostFit());
		assertEquals(97, pop.getMostFit().getCost());
		assertEquals(3, pop.getMostFit().getSolution().id);
		
		f.changeFitness(12);
		EvolvableParametersPopulation.Integer<TestObject> pop2 = pop.split();
		
		// original should be same after split
		for (int i = 0; i < 10; i++) {
			if (i!=4) assertEquals(expectedNow[i]+10, pop.getFitness(i), "index i="+i);
			else assertEquals(expectedNow[i]+20, pop.getFitness(i), "index i="+i);
		}
		for (int i = 0; i < 7; i++) {
			assertEquals(expectedNow[6-i], pop.get(i).id);
		}
		
		// trackers should be same
		assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());
		
		pop2.init();
		assertEquals(10, pop2.size());
		assertEquals(7, pop2.mutableSize());
		for (int i = 0; i < 10; i++) {
			assertEquals(1-i+12+10, pop2.getFitness(i));
		}
		
		// Original should be unchanged after split copy does stuff
		for (int i = 0; i < 10; i++) {
			if (i!=4) assertEquals(expectedNow[i]+10, pop.getFitness(i), "index i="+i);
			else assertEquals(expectedNow[i]+20, pop.getFitness(i), "index i="+i);
		}
		for (int i = 0; i < 7; i++) {
			assertEquals(expectedNow[6-i], pop.get(i).id);
		}
	}
	
	@Test
	public void testEvolvableParametersPopulationInteger() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		EvolvableParametersPopulation.Integer<TestObject> pop = new EvolvableParametersPopulation.Integer<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			2
		);
		assertTrue(tracker == pop.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		pop.setProgressTracker(tracker);
		assertTrue(tracker == pop.getProgressTracker());
		
		pop.init();
		assertEquals(10, pop.size());
		assertEquals(10, pop.mutableSize());
		assertEquals(16, pop.getFitnessOfMostFit());
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		int[] expected = { 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+10, pop.getFitness(i));
		}
		assertFalse(selection.called);
		pop.select();
		assertTrue(selection.called);
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+10, pop.getFitness(i));
			// subject to mutation to opposite order since we selected, which reversed.
			assertEquals(expected[i], pop.get(i).id);
			
		}
		assertEquals(16, pop.getFitnessOfMostFit());
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		pop.replace();
		pop.select();
		for (int i = 0; i < 10; i++) {
			assertEquals(expected[9-i], pop.get(i).id);
			assertEquals(expected[i]+10, pop.getFitness(i));
		}
		assertEquals(16, pop.getFitnessOfMostFit());
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		
		f.changeFitness(1);
		pop.updateFitness(0);
		f.changeFitness(10);
		pop.updateFitness(1);
		pop.replace();
		assertEquals(expected[9]+10+1, pop.getFitness(0));
		assertEquals(expected[8]+10+10, pop.getFitness(1));
		assertEquals(22, pop.getFitnessOfMostFit());
		assertEquals(2, pop.getMostFit().getSolution().id);
		assertEquals(98, pop.getMostFit().getCost());
		
		f.changeFitness(12);
		EvolvableParametersPopulation.Integer<TestObject> pop2 = pop.split();
		
		// orginal should be same
		assertEquals(expected[9]+10+1, pop.getFitness(0));
		assertEquals(expected[8]+10+10, pop.getFitness(1));
		assertEquals(22, pop.getFitnessOfMostFit());
		assertEquals(2, pop.getMostFit().getSolution().id);
		assertEquals(98, pop.getMostFit().getCost());
		
		// trackers should be same
		assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());
		
		pop2.init();
		assertEquals(10, pop2.size());
		assertEquals(10, pop2.mutableSize());
		for (int i = 0; i < 10; i++) {
			assertEquals(1-i+12+10, pop2.getFitness(i));
		}
		
		assertFalse(pop.evolutionIsPaused());
		assertFalse(pop2.evolutionIsPaused());
		tracker.stop();
		assertTrue(pop.evolutionIsPaused());
		assertTrue(pop2.evolutionIsPaused());
		tracker.start();
		assertFalse(pop.evolutionIsPaused());
		assertFalse(pop2.evolutionIsPaused());
		tracker.update(0, new TestObject(), true);
		assertTrue(pop.evolutionIsPaused());
		assertTrue(pop2.evolutionIsPaused());	
		
		assertEquals(0, selection.initCalledWith);
		pop.initOperators(987);
		assertEquals(987, selection.initCalledWith);
	}
	
	@Test
	public void testEvolvableParametersPopulationInteger_SelectCopies() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		EvolvableParametersPopulation.Integer<TestObject> pop = new EvolvableParametersPopulation.Integer<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0, 
			2
		);
		pop.init();
		pop.select();
		TestObject[] firstSelect = new TestObject[10];
		for (int i = 0; i < 10; i++) {
			firstSelect[i] = pop.get(i);
		}
		pop.replace();
		pop.select();
		TestObject[] secondSelect = new TestObject[10];
		for (int i = 0; i < 10; i++) {
			secondSelect[i] = pop.get(i);
		}
		for (int i = 0; i < 10; i++) {
			assertFalse(firstSelect[i] == secondSelect[9-i]);
			assertEquals(firstSelect[i], secondSelect[9-i]);
		}
	}

	
	private static class TestSelectionOp implements SelectionOperator {
		
		boolean called;
		int initCalledWith;
		
		public TestSelectionOp() {
			called = false;
		}
		@Override
		public void select(PopulationFitnessVector.Integer fitnesses, int[] selected) {
			int next = selected.length - 1;
			for (int i = 0; i < selected.length; i++) {
				selected[i] = next;
				next--;
			}
			called = true;
		}
		@Override
		public void select(PopulationFitnessVector.Double fitnesses, int[] selected) {
			int next = selected.length - 1;
			for (int i = 0; i < selected.length; i++) {
				selected[i] = next;
				next--;
			}
			called = true;
		}
		
		@Override
		public TestSelectionOp split() {
			return new TestSelectionOp();
		}
		
		@Override
		public void init(int generations) {
			initCalledWith = generations;
		}
	}
	
	private static class TestFitnessDouble implements FitnessFunction.Double<TestObject> {
		
		private TestProblemDouble problem;
		private int adjustment;
		
		public TestFitnessDouble() {
			problem = new TestProblemDouble();
		}
		
		public double fitness(TestObject c) {
			return c.id + 0.4 + adjustment;
		}
		
		public Problem<TestObject> getProblem() {
			return problem;
		}
		
		public void changeFitness(int adjustment) {
			this.adjustment = adjustment;
		}
	}
	
	private static class TestFitnessDoubleIntCost implements FitnessFunction.Double<TestObject> {
		
		private TestProblemInteger problem;
		private int adjustment;
		
		public TestFitnessDoubleIntCost() {
			problem = new TestProblemInteger();
		}
		
		public double fitness(TestObject c) {
			return c.id + 10 + adjustment;
		}
		
		public Problem<TestObject> getProblem() {
			return problem;
		}
		
		public void changeFitness(int adjustment) {
			this.adjustment = adjustment;
		}
	}
	
	private static class TestFitnessInteger implements FitnessFunction.Integer<TestObject> {
		
		private TestProblemInteger problem;
		private int adjustment;
		
		public TestFitnessInteger() {
			problem = new TestProblemInteger();
		}
		
		public int fitness(TestObject c) {
			return c.id + 10 + adjustment;
		}
		
		public Problem<TestObject> getProblem() {
			return problem;
		}
		
		public void changeFitness(int adjustment) {
			this.adjustment = adjustment;
		}
	}
	
	private static class TestProblemInteger implements IntegerCostOptimizationProblem<TestObject> {
		public int cost(TestObject c) {
			return 100 - c.id;
		}
		public int value(TestObject c) {
			return cost(c);
		}
	}
	
	private static class TestProblemDouble implements OptimizationProblem<TestObject> {
		public double cost(TestObject c) {
			return 1.0 / (1.0 + c.id);
		}
		public double value(TestObject c) {
			return cost(c);
		}
	}
	
	private static class TestInitializer implements Initializer<TestObject> {
		public TestObject createCandidateSolution() {
			return new TestObject();
		}
		public TestInitializer split() {
			return this;
		}
	}
	
	private static class TestObject implements Copyable<TestObject> {
		
		private static int IDENTIFIER = 0;
		private static boolean increase = true;
		public static void reinit() {
			increase = true;
			IDENTIFIER = 0;
		}
		
		private int id;
		
		public TestObject() {
			if (increase) IDENTIFIER++;
			else IDENTIFIER--;
			id = IDENTIFIER;
			if (IDENTIFIER == 6) increase = false;
		}
		
		private TestObject(int id) {
			this.id = id;
		}
		
		public TestObject copy() {
			return new TestObject(id);
		}
		
		@Override
		public int hashCode() {
			return id;
		}
		
		@Override
		public boolean equals(Object other) {
			return id == ((TestObject)other).id;
		}
	}
}