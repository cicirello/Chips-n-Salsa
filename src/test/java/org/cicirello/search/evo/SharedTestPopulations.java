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
import org.cicirello.search.problems.Problem;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.search.ProgressTracker;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 * Code in common for testing the different classes that implement populations.
 */
public class SharedTestPopulations {
	
	void verifyInteger(PopulationFitnessVector.Integer popVector, TestFitnessInteger f, ProgressTracker<TestObject> tracker, TestSelectionOp selection, ToIntFunction<Population<TestObject>> mostFitFitness, int elite) {
		@SuppressWarnings("unchecked")
		Population<TestObject> pop = (Population<TestObject>)popVector;
		
		assertTrue(tracker == pop.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		pop.setProgressTracker(tracker);
		assertTrue(tracker == pop.getProgressTracker());
		
		pop.init();
		assertEquals(10, pop.size());
		assertEquals(10-elite, pop.mutableSize());
		assertEquals(16, mostFitFitness.applyAsInt(pop));
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		int[] expected = { 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+10, popVector.getFitness(i));
		}
		assertFalse(selection.called);
		pop.select();
		assertTrue(selection.called);
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+10, popVector.getFitness(i));
			// subject to mutation to opposite order since we selected, which reversed.
			if (elite == 0) {
				assertEquals(expected[i], pop.get(i).id);
			} else if (i < 10-elite) {
				assertEquals(expected[i+elite], pop.get(i).id);
			}
		}
		assertEquals(16, mostFitFitness.applyAsInt(pop));
		assertEquals(94, pop.getMostFit().getCost());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		
		// only needed for elite case
		int[] expectedNow = { 5, 6, 5, 4, 3, 2, 1, 4, 6, 5 };
		
		if (elite == 0) {
			pop.replace();
			pop.select();
			for (int i = 0; i < 10; i++) {
				assertEquals(expected[9-i], pop.get(i).id);
				assertEquals(expected[i]+10, popVector.getFitness(i));
			}
			assertEquals(16, mostFitFitness.applyAsInt(pop));
			assertEquals(94, pop.getMostFit().getCost());
			assertEquals(6, pop.getMostFit().getSolution().id);
			assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
			
			f.changeFitness(1);
			pop.updateFitness(0);
			f.changeFitness(10);
			pop.updateFitness(1);
			pop.replace();
			assertEquals(expected[9]+10+1, popVector.getFitness(0));
			assertEquals(expected[8]+10+10, popVector.getFitness(1));
			assertEquals(22, mostFitFitness.applyAsInt(pop));
			assertEquals(2, pop.getMostFit().getSolution().id);
			assertEquals(98, pop.getMostFit().getCost());			
		} else {
			f.changeFitness(10);
			pop.updateFitness(4);
			
			pop.replace();
			for (int i = 0; i < 10; i++) {
				if (i!=4) assertEquals(expectedNow[i]+10, popVector.getFitness(i), "index i="+i);
				else assertEquals(expectedNow[i]+20, popVector.getFitness(i), "index i="+i);
			}
			pop.select();
			for (int i = 0; i < 7; i++) {
				assertEquals(expectedNow[6-i], pop.get(i).id);
			}
			assertEquals(23, mostFitFitness.applyAsInt(pop));
			assertEquals(97, pop.getMostFit().getCost());
			assertEquals(3, pop.getMostFit().getSolution().id);
		}
		
		f.changeFitness(12);
		Population<TestObject> pop2 = pop.split();
		@SuppressWarnings("unchecked")
		PopulationFitnessVector.Integer popVector2 = (PopulationFitnessVector.Integer)pop2;
		
		if (elite == 0) {
			// orginal should be same
			assertEquals(expected[9]+10+1, popVector.getFitness(0));
			assertEquals(expected[8]+10+10, popVector.getFitness(1));
			assertEquals(22, mostFitFitness.applyAsInt(pop));
			assertEquals(2, pop.getMostFit().getSolution().id);
			assertEquals(98, pop.getMostFit().getCost());
		} else {
			// original should be same after split
			for (int i = 0; i < 10; i++) {
				if (i!=4) assertEquals(expectedNow[i]+10, popVector.getFitness(i), "index i="+i);
				else assertEquals(expectedNow[i]+20, popVector.getFitness(i), "index i="+i);
			}
			for (int i = 0; i < 7; i++) {
				assertEquals(expectedNow[6-i], pop.get(i).id);
			}
		}
		
		// trackers should be same
		assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());
		
		pop2.init();
		assertEquals(10, pop2.size());
		assertEquals(10-elite, pop2.mutableSize());
		for (int i = 0; i < 10; i++) {
			assertEquals(1-i+12+10, popVector2.getFitness(i));
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
	}
	
	void verifyDouble(PopulationFitnessVector.Double popVector, TestFitnessDouble f, ProgressTracker<TestObject> tracker, TestSelectionOp selection, ToDoubleFunction<Population<TestObject>> mostFitFitness, int elite) {
		@SuppressWarnings("unchecked")
		Population<TestObject> pop = (Population<TestObject>)popVector; 
		
		assertTrue(tracker == pop.getProgressTracker());
		tracker = new ProgressTracker<TestObject>();
		pop.setProgressTracker(tracker);
		assertTrue(tracker == pop.getProgressTracker());
		
		pop.init();
		assertEquals(10, pop.size());
		assertEquals(10-elite, pop.mutableSize());
		assertEquals(6.4, mostFitFitness.applyAsDouble(pop));
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		int[] expected = { 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+0.4, popVector.getFitness(i));
		}
		assertFalse(selection.called);
		pop.select();
		assertTrue(selection.called);
		for (int i = 0; i < 10; i++) {
			// fitnesses of original before selection.
			assertEquals(expected[9-i]+0.4, popVector.getFitness(i));
			// subject to mutation to opposite order since we selected, which reversed.
			if (elite == 0) {
				assertEquals(expected[i], pop.get(i).id);
			} else if (i < 10 - elite) {
				assertEquals(expected[i+elite], pop.get(i).id);
			}
		}
		assertEquals(6.4, mostFitFitness.applyAsDouble(pop));
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		pop.replace();
		// next line for elite case only
		int[] expectedNow = { 5, 6, 5, 4, 3, 2, 1, 4, 6, 5 };
		if (elite > 0) {
			for (int i = 0; i < 10; i++) {
				assertEquals(expectedNow[i]+0.4, popVector.getFitness(i), "index i="+i);
			}
		}
		pop.select();
		if (elite > 0) {
			for (int i = 0; i < 10-elite; i++) {
				assertEquals(expectedNow[9-elite-i], pop.get(i).id);
			}
		} else {
			for (int i = 0; i < 10; i++) {
				assertEquals(expected[9-i], pop.get(i).id);
				assertEquals(expected[i]+0.4, popVector.getFitness(i));
			}
		}
		assertEquals(6.4, mostFitFitness.applyAsDouble(pop));
		assertEquals(1.0/7.0, pop.getMostFit().getCostDouble());
		assertEquals(6, pop.getMostFit().getSolution().id);
		assertEquals(tracker.getSolution(), pop.getMostFit().getSolution());
		
		int eliteAdjust = elite > 0 ? 1 : 0;
		f.changeFitness(1);
		pop.updateFitness(eliteAdjust);
		f.changeFitness(10);
		pop.updateFitness(1+eliteAdjust);
		pop.replace();
		assertEquals(expected[9]+0.4+1+eliteAdjust, popVector.getFitness(eliteAdjust));
		assertEquals(expected[8]+0.4+10+eliteAdjust, popVector.getFitness(1+eliteAdjust));
		assertEquals(12.4+eliteAdjust, mostFitFitness.applyAsDouble(pop));
		assertEquals(2+eliteAdjust, pop.getMostFit().getSolution().id);
		assertEquals(1.0/(3.0+eliteAdjust), pop.getMostFit().getCostDouble());
		
		if (elite > 0) {
			int[] andNow = {1, 2, 3, 4, 5, 6, 5, 4, 6, 5};
			double[] andNowFitness = {1.4, 3.4, 13.4, 4.4, 5.4, 6.4, 5.4, 4.4, 6.4, 5.4};
			for (int i = 0; i < 10; i++) {
				assertEquals(andNowFitness[i], popVector.getFitness(i), "index i="+i);
			}
		}
		
		f.changeFitness(12);
		Population<TestObject> pop2 = pop.split();
		@SuppressWarnings("unchecked")
		PopulationFitnessVector.Double popVector2 = (PopulationFitnessVector.Double)pop2;
		
		// orginal should be same
		assertEquals(expected[9]+0.4+1+eliteAdjust, popVector.getFitness(eliteAdjust));
		assertEquals(expected[8]+0.4+10+eliteAdjust, popVector.getFitness(1+eliteAdjust));
		assertEquals(12.4+eliteAdjust, mostFitFitness.applyAsDouble(pop));
		assertEquals(2+eliteAdjust, pop.getMostFit().getSolution().id);
		assertEquals(1.0/(3.0+eliteAdjust), pop.getMostFit().getCostDouble());
		
		// trackers should be same
		assertTrue(pop.getProgressTracker() == pop2.getProgressTracker());
		
		pop2.init();
		assertEquals(10, pop2.size());
		assertEquals(10-elite, pop2.mutableSize());
		for (int i = 0; i < 10; i++) {
			assertEquals(1-i+12+0.4, popVector2.getFitness(i));
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
	
	void verifySelectCopies(Population<TestObject> pop) {
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
	
	static class TestSelectionOp implements SelectionOperator {
		
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
	
	static class TestFitnessDouble implements FitnessFunction.Double<TestObject> {
		
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
	
	static class TestFitnessDoubleIntCost implements FitnessFunction.Double<TestObject> {
		
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
	
	static class TestFitnessInteger implements FitnessFunction.Integer<TestObject> {
		
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
	
	static class TestProblemInteger implements IntegerCostOptimizationProblem<TestObject> {
		public int cost(TestObject c) {
			return 100 - c.id;
		}
		public int value(TestObject c) {
			return cost(c);
		}
	}
	
	static class TestProblemDouble implements OptimizationProblem<TestObject> {
		public double cost(TestObject c) {
			return 1.0 / (1.0 + c.id);
		}
		public double value(TestObject c) {
			return cost(c);
		}
	}
	
	static class TestInitializer implements Initializer<TestObject> {
		public TestObject createCandidateSolution() {
			return new TestObject();
		}
		public TestInitializer split() {
			return this;
		}
	}
	
	static class TestObject implements Copyable<TestObject> {
		
		private static int IDENTIFIER = 0;
		private static boolean increase = true;
		
		public static void reinit() {
			increase = true;
			IDENTIFIER = 0;
		}
		
		int id;
		
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
