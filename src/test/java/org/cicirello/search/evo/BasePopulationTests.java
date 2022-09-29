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

/**
 * JUnit test cases for BasePopulation.
 */
public class BasePopulationTests extends SharedTestPopulations {
	
	private static final double EPSILON = 1e-10;
	
	@Test
	public void testExceptions() {
		NullPointerException thrown = assertThrows( 
			NullPointerException.class,
			() -> new BasePopulation.Double<TestObject>(10, null, new TestFitnessDouble(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new BasePopulation.Double<TestObject>(10, new TestInitializer(), null, new TestSelectionOp(), new ProgressTracker<TestObject>(), 0)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new BasePopulation.Double<TestObject>(10, new TestInitializer(), new TestFitnessDouble(), null, new ProgressTracker<TestObject>(), 0)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new BasePopulation.Double<TestObject>(10, new TestInitializer(), new TestFitnessDouble(), new TestSelectionOp(), null, 0)
		);
		
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new BasePopulation.Integer<TestObject>(10, null, new TestFitnessInteger(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new BasePopulation.Integer<TestObject>(10, new TestInitializer(), null, new TestSelectionOp(), new ProgressTracker<TestObject>(), 0)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new BasePopulation.Integer<TestObject>(10, new TestInitializer(), new TestFitnessInteger(), null, new ProgressTracker<TestObject>(), 0)
		);
		thrown = assertThrows( 
			NullPointerException.class,
			() -> new BasePopulation.Integer<TestObject>(10, new TestInitializer(), new TestFitnessInteger(), new TestSelectionOp(), null, 0)
		);
		
		IllegalArgumentException thrown2 = assertThrows( 
			IllegalArgumentException.class,
			() -> new BasePopulation.Double<TestObject>(0, new TestInitializer(), new TestFitnessDouble(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0)
		);
		thrown2 = assertThrows( 
			IllegalArgumentException.class,
			() -> new BasePopulation.Integer<TestObject>(0, new TestInitializer(), new TestFitnessInteger(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0)
		);
		thrown2 = assertThrows( 
			IllegalArgumentException.class,
			() -> new BasePopulation.Double<TestObject>(10, new TestInitializer(), new TestFitnessDouble(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 10)
		);
		thrown2 = assertThrows( 
			IllegalArgumentException.class,
			() -> new BasePopulation.Integer<TestObject>(10, new TestInitializer(), new TestFitnessInteger(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 10)
		);
		
		final Population pop1 = new BasePopulation.Double<TestObject>(3, new TestInitializer(), new TestFitnessDouble(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0);
		UnsupportedOperationException thrown3 = assertThrows( 
			UnsupportedOperationException.class,
			() -> pop1.getParameter(0, 0)
		);
		final Population pop2 = new BasePopulation.Integer<TestObject>(3, new TestInitializer(), new TestFitnessInteger(), new TestSelectionOp(), new ProgressTracker<TestObject>(), 0);
		thrown3 = assertThrows( 
			UnsupportedOperationException.class,
			() -> pop2.getParameter(0, 0)
		);
	}
	
	@Test
	public void testBasePopulationElitismDouble() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		BasePopulation.Double<TestObject> pop = new BasePopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			3
		);
		verifyDouble(pop, f, tracker, selection, p -> ((BasePopulation.Double<TestObject>)p).getFitnessOfMostFit(), 3);
	}
	
	@Test
	public void testBasePopulationDouble() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		BasePopulation.Double<TestObject> pop = new BasePopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0
		);
		verifyDouble(pop, f, tracker, selection, p -> ((BasePopulation.Double<TestObject>)p).getFitnessOfMostFit(), 0);
	}
	
	@Test
	public void testBasePopulationDouble_SelectCopies() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDouble f = new TestFitnessDouble();
		BasePopulation.Double<TestObject> pop = new BasePopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0
		);
		verifySelectCopies(pop);
	}
	
	@Test
	public void testBasePopulationDoubleIntCost() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
		BasePopulation.Double<TestObject> pop = new BasePopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0
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
		BasePopulation.Double<TestObject> pop2 = pop.split();
		
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
	public void testBasePopulationDoubleIntCost_SelectCopies() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessDoubleIntCost f = new TestFitnessDoubleIntCost();
		BasePopulation.Double<TestObject> pop = new BasePopulation.Double<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0
		);
		verifySelectCopies(pop);
	}
	
	@Test
	public void testBasePopulationElitismInteger() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		BasePopulation.Integer<TestObject> pop = new BasePopulation.Integer<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			3
		);
		verifyInteger(pop, f, tracker, selection, p -> ((BasePopulation.Integer<TestObject>)p).getFitnessOfMostFit(), 3);
	}
	
	@Test
	public void testBasePopulationInteger() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		BasePopulation.Integer<TestObject> pop = new BasePopulation.Integer<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0
		);
		verifyInteger(pop, f, tracker, selection, p -> ((BasePopulation.Integer<TestObject>)p).getFitnessOfMostFit(), 0);
	}
	
	@Test
	public void testBasePopulationInteger_SelectCopies() {
		TestObject.reinit();
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		TestSelectionOp selection = new TestSelectionOp();
		TestFitnessInteger f = new TestFitnessInteger();
		BasePopulation.Integer<TestObject> pop = new BasePopulation.Integer<TestObject>(
			10,
			new TestInitializer(),
			f,
			selection,
			tracker, 
			0
		);
		verifySelectCopies(pop);
	}
}
