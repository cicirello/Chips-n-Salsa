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
 
package org.cicirello.search.ss;


import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import org.cicirello.search.problems.Problem;
import org.cicirello.util.Copyable;
import org.cicirello.search.SolutionCostPair;


/**
 * JUnit tests for the HybridConstructiveHeuristic class.
 */
public class HybridConstructiveHeuristicTests {
	
	@Test
	public void testHybridConstructiveHeuristic() {
		ArrayList<TestHeuristic> heuristics = new ArrayList<TestHeuristic>();
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics)
		);
		
		TestProblem problem = new TestProblem();
		TestHeuristic h100 = new TestHeuristic(100, problem);
		heuristics.add(h100);
		
		// One heuristic case
		HybridConstructiveHeuristic<TestObject> hybrid = new HybridConstructiveHeuristic<TestObject>(heuristics);
		IncrementalEvaluation<TestObject> inc = hybrid.createIncrementalEvaluation();
		Partial<TestObject> partial = hybrid.createPartial(5);
		assertNotNull(partial);
		assertEquals(problem, hybrid.getProblem());
		assertEquals(10, hybrid.completeLength());
		// assert is in the call to the h method
		hybrid.h(partial, 0, inc);
		assertEquals(1, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		hybrid.h(partial, 0, inc);
		assertEquals(2, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		inc = hybrid.createIncrementalEvaluation();
		partial = hybrid.createPartial(5);
		assertEquals(0, h100.hCallCount);
		hybrid.h(partial, 0, inc);
		assertEquals(1, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		hybrid.h(partial, 0, inc);
		assertEquals(2, h100.hCallCount);
		assertEquals(2, h100.incCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		
		
		// Three heuristics case
		TestHeuristic h101 = new TestHeuristic(101, problem);
		heuristics.add(h101);
		TestHeuristic h102 = new TestHeuristic(102, problem);
		heuristics.add(h102);
		h100.incCallCount = 0;
		hybrid = new HybridConstructiveHeuristic<TestObject>(heuristics);
		for (int i = 0; i < 60; i++) {
			inc = hybrid.createIncrementalEvaluation();
			partial = hybrid.createPartial(5);
			int totalHCallsPre = h100.hCallCount + h101.hCallCount + h102.hCallCount;
			int totalIncCalls = h100.incCallCount + h101.incCallCount + h102.incCallCount;
			assertEquals(i+1, totalIncCalls);
			// assert is in the call to the h method
			hybrid.h(partial, 0, inc);
			int totalHCalls = h100.hCallCount + h101.hCallCount + h102.hCallCount;
			assertEquals(totalHCallsPre+1, totalHCalls);
			inc.extend(partial, 0);
			assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
			hybrid.h(partial, 0, inc);
			totalHCalls = h100.hCallCount + h101.hCallCount + h102.hCallCount;
			assertEquals(totalHCallsPre+2, totalHCalls);
			assertEquals(i+1, totalIncCalls);
			inc.extend(partial, 0);
			assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
			if (h100.incCallCount > 1 && h101.incCallCount > 1 && h102.incCallCount > 1) {
				break;
			}
		}
		assertTrue(h100.incCallCount > 1 && h101.incCallCount > 1 && h102.incCallCount > 1);
		
		// Wrong problem object case
		heuristics.add(new TestHeuristic(999, new TestProblem()));
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics)
		);
	}
	
	@Test
	public void testHybridConstructiveHeuristicRoundRobin() {
		ArrayList<TestHeuristic> heuristics = new ArrayList<TestHeuristic>();
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics, true)
		);
		
		TestProblem problem = new TestProblem();
		TestHeuristic h100 = new TestHeuristic(100, problem);
		heuristics.add(h100);
		
		// One heuristic case
		HybridConstructiveHeuristic<TestObject> hybrid = new HybridConstructiveHeuristic<TestObject>(heuristics, true);
		IncrementalEvaluation<TestObject> inc = hybrid.createIncrementalEvaluation();
		Partial<TestObject> partial = hybrid.createPartial(5);
		assertNotNull(partial);
		assertEquals(problem, hybrid.getProblem());
		assertEquals(10, hybrid.completeLength());
		// assert is in the call to the h method
		hybrid.h(partial, 0, inc);
		assertEquals(1, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		hybrid.h(partial, 0, inc);
		assertEquals(2, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		inc = hybrid.createIncrementalEvaluation();
		partial = hybrid.createPartial(5);
		assertEquals(0, h100.hCallCount);
		hybrid.h(partial, 0, inc);
		assertEquals(1, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		hybrid.h(partial, 0, inc);
		assertEquals(2, h100.hCallCount);
		assertEquals(2, h100.incCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		
		// Three heuristics case
		TestHeuristic h101 = new TestHeuristic(101, problem);
		heuristics.add(h101);
		TestHeuristic h102 = new TestHeuristic(102, problem);
		heuristics.add(h102);
		h100.incCallCount = 0;
		hybrid = new HybridConstructiveHeuristic<TestObject>(heuristics, true);
		for (int i = 0; i < 6; i++) {
			inc = hybrid.createIncrementalEvaluation();
			partial = hybrid.createPartial(5);
			assertEquals((i+3)/3, h100.incCallCount);
			assertEquals((i+2)/3, h101.incCallCount);
			assertEquals((i+1)/3, h102.incCallCount);
			TestHeuristic thisIteration = i%3==0? h100 : (i%3==1? h101 : h102);
			assertEquals(0, thisIteration.hCallCount);
			// assert is in the call to the h method
			hybrid.h(partial, 0, inc);
			assertEquals(1, thisIteration.hCallCount);
			assertEquals(i%3+100, TestHeuristic.lastHCalled);
			inc.extend(partial, 0);
			assertEquals(i%3+100, TestIncrementalEvaluation.lastExtendCalled);
			hybrid.h(partial, 0, inc);
			assertEquals(2, thisIteration.hCallCount);
			assertEquals(i%3+100, TestHeuristic.lastHCalled);
			inc.extend(partial, 0);
			assertEquals(i%3+100, TestIncrementalEvaluation.lastExtendCalled);
		}
		
		// Wrong problem object case
		heuristics.add(new TestHeuristic(999, new TestProblem()));
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics, true)
		);
	}
	
	@Test
	public void testHybridConstructiveHeuristicWeighted() {
		ArrayList<TestHeuristic> heuristics = new ArrayList<TestHeuristic>();
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics, new int[0])
		);
		
		TestProblem problem = new TestProblem();
		TestHeuristic h100 = new TestHeuristic(100, problem);
		heuristics.add(h100);
		
		// One heuristic case
		int[] weights = { 1 };
		HybridConstructiveHeuristic<TestObject> hybrid = new HybridConstructiveHeuristic<TestObject>(heuristics, weights);
		IncrementalEvaluation<TestObject> inc = hybrid.createIncrementalEvaluation();
		Partial<TestObject> partial = hybrid.createPartial(5);
		assertNotNull(partial);
		assertEquals(problem, hybrid.getProblem());
		assertEquals(10, hybrid.completeLength());
		// assert is in the call to the h method
		hybrid.h(partial, 0, inc);
		assertEquals(1, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		hybrid.h(partial, 0, inc);
		assertEquals(2, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		inc = hybrid.createIncrementalEvaluation();
		partial = hybrid.createPartial(5);
		assertEquals(0, h100.hCallCount);
		hybrid.h(partial, 0, inc);
		assertEquals(1, h100.hCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		hybrid.h(partial, 0, inc);
		assertEquals(2, h100.hCallCount);
		assertEquals(2, h100.incCallCount);
		inc.extend(partial, 0);
		assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
		
		
		// Three heuristics case
		weights = new int[3];
		weights[0] = 4;
		weights[1] = 1;
		weights[2] = 2;
		TestHeuristic h101 = new TestHeuristic(101, problem);
		heuristics.add(h101);
		TestHeuristic h102 = new TestHeuristic(102, problem);
		heuristics.add(h102);
		h100.incCallCount = 0;
		hybrid = new HybridConstructiveHeuristic<TestObject>(heuristics, weights);
		final int NUM_SAMPLES = 200;
		for (int i = 0; i < NUM_SAMPLES; i++) {
			inc = hybrid.createIncrementalEvaluation();
			partial = hybrid.createPartial(5);
			int totalHCallsPre = h100.hCallCount + h101.hCallCount + h102.hCallCount;
			int totalIncCalls = h100.incCallCount + h101.incCallCount + h102.incCallCount;
			assertEquals(i+1, totalIncCalls);
			// assert is in the call to the h method
			hybrid.h(partial, 0, inc);
			int totalHCalls = h100.hCallCount + h101.hCallCount + h102.hCallCount;
			assertEquals(totalHCallsPre+1, totalHCalls);
			inc.extend(partial, 0);
			assertEquals(TestHeuristic.lastHCalled, TestIncrementalEvaluation.lastExtendCalled);
			assertEquals(i+1, totalIncCalls);
		}
		assertTrue(
			h100.incCallCount > h102.incCallCount 
			&& h102.incCallCount > h101.incCallCount
			&& h101.incCallCount > 1
		); 
		
		// 0 weight cases
		final int[] w0 = {0, 1, 2};
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics, w0)
		);
		w0[0] = 4;
		w0[1] = 0;
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics, w0)
		);
		
		// Wrong number of weights case
		final int[] w = {4, 1, 2, 1};
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics, w)
		);
		
		// Wrong problem object case
		heuristics.add(new TestHeuristic(999, new TestProblem()));
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new HybridConstructiveHeuristic<TestObject>(heuristics, w)
		);
	}


	private static class TestHeuristic implements ConstructiveHeuristic<TestObject> {
		
		private final int id;
		private final TestProblem problem; 
		private int hCallCount;
		private int incCallCount;
		private static int lastHCalled;
		
		public TestHeuristic(int id, TestProblem problem) {
			this.id = id;
			this.problem = problem;
		}
		
		@Override
		public double h​(Partial<TestObject> p, int element, IncrementalEvaluation<TestObject> incEval) {
			assertEquals(id, ((TestIncrementalEvaluation)incEval).id);
			hCallCount++;
			lastHCalled = id;
			return 1.0;
		}
		
		@Override
		public IncrementalEvaluation<TestObject> createIncrementalEvaluation() {
			hCallCount = 0;
			incCallCount++;
			return new TestIncrementalEvaluation(id);
		}
		
		@Override
		public Partial<TestObject> createPartial​(int n) {
			return new TestPartial(id, n);
		}
		
		@Override
		public int completeLength() {
			return 10;
		}
		
		@Override
		public Problem<TestObject> getProblem() {
			return problem;
		}
	}
	
	private static class TestObject implements Copyable<TestObject> {
		
		private int value;
		
		public TestObject(int value) {
			this.value = value;
		}
		
		@Override
		public TestObject copy() {
			return new TestObject(value);
		}
	}
	
	private static class TestProblem implements Problem<TestObject> {
		@Override
		public SolutionCostPair<TestObject> getSolutionCostPair​(TestObject candidate) {
			return new SolutionCostPair<TestObject>(candidate, candidate.value, false);
		}
	}
	
	private static class TestPartial implements Partial<TestObject> {
		
		private final int finalValue;
		private int size;
		private final int n;
		
		public TestPartial(int finalValue, int n) {
			this.finalValue = finalValue;
			this.n = n;
		}
		
		@Override public TestObject toComplete() {
			return new TestObject(finalValue);
		}
		
		@Override public boolean isComplete() {
			return size==n;
		}
		
		@Override public int get​(int index) {
			return 0;
		}
		
		@Override public int getLast() {
			return 0;
		}
		
		@Override public int size() {
			return size;
		}
		
		@Override public int numExtensions() {
			return 1;
		}
		
		@Override public int getExtension​(int extensionIndex) {
			return 0;
		}
		
		@Override public void extend​(int extensionIndex) {
			if (!isComplete()) size++;
		}
	}
	
	private static class TestIncrementalEvaluation implements IncrementalEvaluation<TestObject> {
		
		private final int id;
		private static int lastExtendCalled;
		
		public TestIncrementalEvaluation(int id) {
			this.id = id;
		}
		
		@Override public void extend​(Partial<TestObject> p, int element) {
			lastExtendCalled = id;
		}
	}
}
