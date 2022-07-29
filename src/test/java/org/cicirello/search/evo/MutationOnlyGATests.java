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
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.OneMax;
import org.cicirello.search.representations.BitVector;

/**
 * JUnit test cases for MutationOnlyGeneticAlgorithm.
 */
public class MutationOnlyGATests {
	
	// With Initializer and ProgressTracker
	
	@Test
	public void testDoubleFitnessM0() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = Math.ulp(0.0);
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			new AllZerosInitializer(L),
			new InverseCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection()),
			tracker
		);
		assertTrue(tracker == ga.getProgressTracker());
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(L*1.0, solution.getCostDouble(), 1E-10);
		assertEquals(L*1.0, tracker.getCostDouble(), 1E-10);
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertTrue(b.allZeros());
		assertTrue(tracker.getSolution().allZeros());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				0.0,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		
		MutationOnlyGeneticAlgorithm ga2 = ga.split();
		assertTrue(ga.getProgressTracker() == ga2.getProgressTracker());
		assertTrue(ga.getProblem() == ga2.getProblem());
		assertTrue(ga != ga2);
	}
	
	@Test
	public void testDoubleFitnessM1() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = 1.0 - Math.ulp(1.0);
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			new AllZerosInitializer(L),
			new InverseCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection()),
			tracker
		);
		assertTrue(tracker == ga.getProgressTracker());
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(0.0, solution.getCostDouble(), 1E-10);
		assertEquals(0.0, tracker.getCostDouble(), 1E-10);
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertTrue(b.allOnes());
		assertTrue(tracker.getSolution().allOnes());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				1.0,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				null,
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				(FitnessFunction.Double<BitVector>)null,
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				null,
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				null
			)
		);
	}
	
	@Test
	public void testIntegerFitnessM0() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = Math.ulp(0.0);
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			new AllZerosInitializer(L),
			new NegativeIntegerCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection()),
			tracker
		);
		assertTrue(tracker == ga.getProgressTracker());
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(L, solution.getCost());
		assertEquals(L, tracker.getCost());
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertTrue(b.allZeros());
		assertTrue(tracker.getSolution().allZeros());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				0.0,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		
		MutationOnlyGeneticAlgorithm ga2 = ga.split();
		assertTrue(ga.getProgressTracker() == ga2.getProgressTracker());
		assertTrue(ga.getProblem() == ga2.getProblem());
		assertTrue(ga != ga2);
	}
	
	@Test
	public void testIntegerFitnessM1() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = 1.0 - Math.ulp(1.0);
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			new AllZerosInitializer(L),
			new NegativeIntegerCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection()),
			tracker
		);
		assertTrue(tracker == ga.getProgressTracker());
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(0, solution.getCost());
		assertEquals(0, tracker.getCost());
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertTrue(b.allOnes());
		assertTrue(tracker.getSolution().allOnes());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				1.0,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				null,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				(FitnessFunction.Integer<BitVector>)null,
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				null,
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				null
			)
		);
	}
	
	// With Initializer but no ProgressTracker
	
	@Test
	public void testDoubleFitnessNoTrackerM0() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = Math.ulp(0.0);
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			new AllZerosInitializer(L),
			new InverseCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection())
		);
		ProgressTracker<BitVector> tracker = ga.getProgressTracker();
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(L*1.0, solution.getCostDouble(), 1E-10);
		assertEquals(L*1.0, tracker.getCostDouble(), 1E-10);
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertTrue(b.allZeros());
		assertTrue(tracker.getSolution().allZeros());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				0.0,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
	}
	
	@Test
	public void testDoubleFitnessNoTrackerM1() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = 1.0 - Math.ulp(1.0);
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			new AllZerosInitializer(L),
			new InverseCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection())
		);
		ProgressTracker<BitVector> tracker = ga.getProgressTracker();
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(0.0, solution.getCostDouble(), 1E-10);
		assertEquals(0.0, tracker.getCostDouble(), 1E-10);
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertTrue(b.allOnes());
		assertTrue(tracker.getSolution().allOnes());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				1.0,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				null,
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				(FitnessFunction.Double<BitVector>)null,
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				null
			)
		);
	}
	
	@Test
	public void testIntegerFitnessNoTrackerM0() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = Math.ulp(0.0);
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			new AllZerosInitializer(L),
			new NegativeIntegerCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection())
		);
		ProgressTracker<BitVector> tracker = ga.getProgressTracker();
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(L, solution.getCost());
		assertEquals(L, tracker.getCost());
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertTrue(b.allZeros());
		assertTrue(tracker.getSolution().allZeros());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				0.0,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
	}
	
	@Test
	public void testIntegerFitnessNoTrackerM1() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = 1.0 - Math.ulp(1.0);
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			new AllZerosInitializer(L),
			new NegativeIntegerCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection())
		);
		ProgressTracker<BitVector> tracker = ga.getProgressTracker();
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertEquals(0, solution.getCost());
		assertEquals(0, tracker.getCost());
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		assertTrue(b.allOnes());
		assertTrue(tracker.getSolution().allOnes());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				1.0,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				null,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				(FitnessFunction.Integer<BitVector>)null,
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				new AllZerosInitializer(L),
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				null
			)
		);
	}
	
	// With bitLength and ProgressTracker
	
	@Test
	public void testDoubleFitnessBitLength() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = 0.5;
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			L,
			new InverseCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection()),
			tracker
		);
		assertTrue(tracker == ga.getProgressTracker());
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertTrue(L > solution.getCostDouble() && solution.getCostDouble() > 0);
		assertTrue(L > tracker.getCostDouble() && tracker.getCostDouble() > 0);
		assertEquals(tracker.getCostDouble(), solution.getCostDouble(), 1E-10);
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new InverseCostFitnessFunction<BitVector>(problem),
				0.0,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				L,
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				(FitnessFunction.Double<BitVector>)null,
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				null,
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				null
			)
		);
	}
	
	@Test
	public void testIntegerFitnessBitLength() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = 0.5;
		ProgressTracker<BitVector> tracker = new ProgressTracker<BitVector>();
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			L,
			new NegativeIntegerCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection()),
			tracker
		);
		assertTrue(tracker == ga.getProgressTracker());
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertTrue(L > solution.getCost() && solution.getCost() > 0);
		assertTrue(L > tracker.getCost() && tracker.getCost() > 0);
		assertEquals(tracker.getCost(), solution.getCost());
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				0.0,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				L,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				(FitnessFunction.Integer<BitVector>)null,
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				null,
				tracker
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection()),
				null
			)
		);
	}
	
	// With bitLength but no ProgressTracker
	
	@Test
	public void testDoubleFitnessBitLengthNoTracker() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = 0.5;
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			L,
			new InverseCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection())	
		);
		ProgressTracker<BitVector> tracker = ga.getProgressTracker();
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertTrue(L > solution.getCostDouble() && solution.getCostDouble() > 0);
		assertTrue(L > tracker.getCostDouble() && tracker.getCostDouble() > 0);
		assertEquals(tracker.getCostDouble(), solution.getCostDouble(), 1E-10);
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new InverseCostFitnessFunction<BitVector>(problem),
				0.0,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				L,
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				(FitnessFunction.Double<BitVector>)null,
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new InverseCostFitnessFunction<BitVector>(problem),
				M,
				null
			)
		);
	}
	
	@Test
	public void testIntegerFitnessBitLengthNoTracker() {
		int n = 10;
		int L = 32;
		OneMax problem = new OneMax();
		double M = 0.5;
		MutationOnlyGeneticAlgorithm ga = new MutationOnlyGeneticAlgorithm(
			n,
			L,
			new NegativeIntegerCostFitnessFunction<BitVector>(problem),
			M,
			new FitnessShifter(new FitnessProportionalSelection())
		);
		ProgressTracker<BitVector> tracker = ga.getProgressTracker();
		assertTrue(problem == ga.getProblem());
		SolutionCostPair<BitVector> solution = ga.optimize(1);
		assertTrue(L > solution.getCost() && solution.getCost() > 0);
		assertTrue(L > tracker.getCost() && tracker.getCost() > 0);
		assertEquals(tracker.getCost(), solution.getCost());
		BitVector b = solution.getSolution();
		assertEquals(L, b.length());
		assertEquals(L, tracker.getSolution().length());
		
		IllegalArgumentException thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				0.0,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownIllegal = assertThrows( 
			IllegalArgumentException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				0,
				L,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		NullPointerException thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				(FitnessFunction.Integer<BitVector>)null,
				M,
				new FitnessShifter(new FitnessProportionalSelection())
			)
		);
		thrownNull = assertThrows( 
			NullPointerException.class,
			() -> new MutationOnlyGeneticAlgorithm(
				n,
				L,
				new NegativeIntegerCostFitnessFunction<BitVector>(problem),
				M,
				null
			)
		);
	}
	
	private static class AllZerosInitializer implements Initializer<BitVector> {
		private int L;
		
		public AllZerosInitializer(int L) {
			this.L = L;
		}
		
		@Override
		public BitVector createCandidateSolution() {
			return new BitVector(L);
		}
		
		@Override
		public AllZerosInitializer split() {
			return new AllZerosInitializer(L);
		}
	}
	
}