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
 
package org.cicirello.search.operators;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.util.Copyable;
import java.util.ArrayList;

/**
 * JUnit tests for the various classes that implement hybrid mutation
 * operators (i.e., that combine multiple mutation operators.
 */
public class HybridMutationTests {
	
	// A few of the test cases test goodness of fit using Chi-square test.
	// These statistical tests are computed at the 95% level, which means
	// 5% of the time on average they should be expected to fail.  
	// There are several such tests in this set of test cases, so there is
	// a reasonably high chance that at least one will fail.
	// This part of the test cases can be disabled with this constant.
	// If you make any code changes that can potentially affect this, then
	// reenable by setting true.  You can then set to false after the test cases pass.
	// Just note that if enabled and the chi-square tests fail, rerun the test cases.
	private static final boolean DISABLE_STATISTICAL_TESTS = true;
	
	@Test
	public void testHybridMutation() {
		int n = 6000;
		// used for chi-square tests: tested at 95% level
		double[] threshold = { 0, 3.841, 5.991 };
		for (int k = 1; k <= 3; k++) {
			ArrayList<TestMutation> mutators = new ArrayList<TestMutation>();
			for (int i = 0; i < k; i++) {
				mutators.add(new TestMutation());
			}
			HybridMutation<TestObject> m = new HybridMutation<TestObject>(mutators); 
			TestObject t = new TestObject();
			for (int i = 0; i < n; i++) {
				m.mutate(t);
			}
			if (k==1) {
				assertEquals(n, mutators.get(0).mutationCount);
			} else {
				// Chi-square goodness-of-fit tests on the distribution
				// of mutation calls across the set of mutation ops.
				int x = 0;
				int total = 0;
				for (int i = 0; i < k; i++) {
					int o = mutators.get(i).mutationCount;
					assertTrue(o > 0 && o <= n);
					x += o*o*k;
					total += o;
				}
				assertEquals(n, total);
				double v = 1.0*x/n - n;
				if (!DISABLE_STATISTICAL_TESTS) {
					// Chi-square at 95% level
					assertTrue("chi-square test failed, rerun tests since expected to fail 5% of the time", v <= threshold[k-1]);
				}
			}
			HybridMutation<TestObject> s = m.split();
			for (int i = 0; i < 10; i++) {
				s.mutate(t);
			}
			int total = 0;
			for (int i = 0; i < k; i++) {
				int o = mutators.get(i).mutationCount;
				total += o;
			}
			// Verify split didn't keep references to pre-split
			// component mutators.
			assertEquals(n, total);
		}
	}
	
	@Test
	public void testHybridUndoableMutation() {
		int n = 6000;
		// used for chi-square tests: tested at 95% level
		double[] threshold = { 0, 3.841, 5.991 };
		for (int k = 1; k <= 3; k++) {
			ArrayList<TestMutation> mutators = new ArrayList<TestMutation>();
			for (int i = 0; i < k; i++) {
				mutators.add(new TestMutation());
			}
			HybridUndoableMutation<TestObject> m = new HybridUndoableMutation<TestObject>(mutators); 
			TestObject t = new TestObject();
			for (int i = 0; i < n; i++) {
				m.mutate(t);
			}
			if (k==1) {
				assertEquals(n, mutators.get(0).mutationCount);
			} else {
				// Chi-square goodness-of-fit tests on the distribution
				// of mutation calls across the set of mutation ops.
				int x = 0;
				int total = 0;
				for (int i = 0; i < k; i++) {
					int o = mutators.get(i).mutationCount;
					assertTrue(o > 0 && o <= n);
					x += o*o*k;
					total += o;
				}
				assertEquals(n, total);
				double v = 1.0*x/n - n;
				if (!DISABLE_STATISTICAL_TESTS) {
					// Chi-square at 95% level
					assertTrue("chi-square test failed, rerun tests since expected to fail 5% of the time", v <= threshold[k-1]);
				}
			}
			HybridUndoableMutation<TestObject> s = m.split();
			for (int i = 0; i < 10; i++) {
				s.mutate(t);
			}
			int total = 0;
			for (int i = 0; i < k; i++) {
				int o = mutators.get(i).mutationCount;
				total += o;
			}
			// Verify split didn't keep references to pre-split
			// component mutators.
			assertEquals(n, total);
		}
	}
	
	@Test
	public void testHybridUndoableMutationUndoMethod() {
		int n = 100;
		for (int k = 1; k <= 3; k++) {
			ArrayList<TestMutation> mutators = new ArrayList<TestMutation>();
			for (int i = 0; i < k; i++) {
				mutators.add(new TestMutation());
			}
			HybridUndoableMutation<TestObject> m = new HybridUndoableMutation<TestObject>(mutators); 
			TestObject t = new TestObject();
			for (int i = 0; i < n; i++) {
				m.mutate(t);
				m.mutate(t);
				// The assert statement is in the undo method of 
				// the TestMutation class found near bottom of this class.
				// It verifies that the correct undo method is called.
				m.undo(t);
				m.mutate(t);
			}
	
		}
	}
	
	@Test
	public void testWeightedHybridMutationEqualWeights() {
		int n = 6000;
		// used for chi-square tests: tested at 95% level
		double[] threshold = { 0, 3.841, 5.991 };
		for (int w = 1; w <= 2; w++) {
			for (int k = 1; k <= 3; k++) {
				int[] weights = new int[k];
				ArrayList<TestMutation> mutators = new ArrayList<TestMutation>();
				for (int i = 0; i < k; i++) {
					mutators.add(new TestMutation());
					weights[i] = w;
				}
				WeightedHybridMutation<TestObject> m = new WeightedHybridMutation<TestObject>(mutators, weights); 
				TestObject t = new TestObject();
				for (int i = 0; i < n; i++) {
					m.mutate(t);
				}
				if (k==1) {
					assertEquals(n, mutators.get(0).mutationCount);
				} else {
					// Chi-square goodness-of-fit tests on the distribution
					// of mutation calls across the set of mutation ops.
					int x = 0;
					int total = 0;
					for (int i = 0; i < k; i++) {
						int o = mutators.get(i).mutationCount;
						assertTrue(o > 0 && o <= n);
						x += o*o*k;
						total += o;
					}
					assertEquals(n, total);
					double v = 1.0*x/n - n;
					if (!DISABLE_STATISTICAL_TESTS) {
						// Chi-square at 95% level
						assertTrue("chi-square test failed, rerun tests since expected to fail 5% of the time", v <= threshold[k-1]);
					}
				}
				WeightedHybridMutation<TestObject> s = m.split();
				for (int i = 0; i < 10; i++) {
					s.mutate(t);
				}
				int total = 0;
				for (int i = 0; i < k; i++) {
					int o = mutators.get(i).mutationCount;
					total += o;
				}
				// Verify split didn't keep references to pre-split
				// component mutators.
				assertEquals(n, total);
			}
		}
	}
	
	@Test
	public void testWeightedHybridMutationUnequalWeights() {
		int n = 6000;
		// used for chi-square tests: tested at 95% level
		double[] threshold = { 0, 3.841, 5.991 };
		int k = 3;
		int[] weights = {1, 2, 1};
		ArrayList<TestMutation> mutators = new ArrayList<TestMutation>();
		for (int i = 0; i < k; i++) {
			mutators.add(new TestMutation());
		}
		WeightedHybridMutation<TestObject> m = new WeightedHybridMutation<TestObject>(mutators, weights); 
		TestObject t = new TestObject();
		for (int i = 0; i < n; i++) {
			m.mutate(t);
		}		
		// Chi-square goodness-of-fit tests on the distribution
		// of mutation calls across the set of mutation ops.
		int x = 0;
		int total = 0;
		for (int i = 0; i < k; i++) {
			int o = mutators.get(i).mutationCount;
			assertTrue(o > 0 && o <= n);
			int mult = (i==1) ? 2 : 4;
			x += o*o*mult;
			total += o;
		}
		assertEquals(n, total);
		double v = 1.0*x/n - n;
		if (!DISABLE_STATISTICAL_TESTS) {
			// Chi-square at 95% level
			assertTrue("chi-square test failed, rerun tests since expected to fail 5% of the time", v <= threshold[k-1]);
		}
		WeightedHybridMutation<TestObject> s = m.split();
		for (int i = 0; i < 10; i++) {
			s.mutate(t);
		}
		total = 0;
		for (int i = 0; i < k; i++) {
			int o = mutators.get(i).mutationCount;
			total += o;
		}
		// Verify split didn't keep references to pre-split
		// component mutators.
		assertEquals(n, total);
	}
	
	@Test
	public void testWeightedHybridUndoableMutationEqualWeights() {
		int n = 6000;
		// used for chi-square tests: tested at 95% level
		double[] threshold = { 0, 3.841, 5.991 };
		for (int w = 1; w <= 2; w++) {
			for (int k = 1; k <= 3; k++) {
				int[] weights = new int[k];
				ArrayList<TestMutation> mutators = new ArrayList<TestMutation>();
				for (int i = 0; i < k; i++) {
					mutators.add(new TestMutation());
					weights[i] = w;
				}
				WeightedHybridUndoableMutation<TestObject> m = new WeightedHybridUndoableMutation<TestObject>(mutators, weights); 
				TestObject t = new TestObject();
				for (int i = 0; i < n; i++) {
					m.mutate(t);
				}
				if (k==1) {
					assertEquals(n, mutators.get(0).mutationCount);
				} else {
					// Chi-square goodness-of-fit tests on the distribution
					// of mutation calls across the set of mutation ops.
					int x = 0;
					int total = 0;
					for (int i = 0; i < k; i++) {
						int o = mutators.get(i).mutationCount;
						assertTrue(o > 0 && o <= n);
						x += o*o*k;
						total += o;
					}
					assertEquals(n, total);
					double v = 1.0*x/n - n;
					if (!DISABLE_STATISTICAL_TESTS) {
						// Chi-square at 95% level
						assertTrue("chi-square test failed, rerun tests since expected to fail 5% of the time", v <= threshold[k-1]);
					}
				}
				WeightedHybridUndoableMutation<TestObject> s = m.split();
				for (int i = 0; i < 10; i++) {
					s.mutate(t);
				}
				int total = 0;
				for (int i = 0; i < k; i++) {
					int o = mutators.get(i).mutationCount;
					total += o;
				}
				// Verify split didn't keep references to pre-split
				// component mutators.
				assertEquals(n, total);
			}
		}
	}
	
	@Test
	public void testWeightedHybridUndoableMutationUnequalWeights() {
		int n = 6000;
		// used for chi-square tests: tested at 95% level
		double[] threshold = { 0, 3.841, 5.991 };
		int k = 3;
		int[] weights = {1, 2, 1};
		ArrayList<TestMutation> mutators = new ArrayList<TestMutation>();
		for (int i = 0; i < k; i++) {
			mutators.add(new TestMutation());
		}
		WeightedHybridUndoableMutation<TestObject> m = new WeightedHybridUndoableMutation<TestObject>(mutators, weights); 
		TestObject t = new TestObject();
		for (int i = 0; i < n; i++) {
			m.mutate(t);
		}		
		// Chi-square goodness-of-fit tests on the distribution
		// of mutation calls across the set of mutation ops.
		int x = 0;
		int total = 0;
		for (int i = 0; i < k; i++) {
			int o = mutators.get(i).mutationCount;
			assertTrue(o > 0 && o <= n);
			int mult = (i==1) ? 2 : 4;
			x += o*o*mult;
			total += o;
		}
		assertEquals(n, total);
		double v = 1.0*x/n - n;
		if (!DISABLE_STATISTICAL_TESTS) {
			// Chi-square at 95% level
			assertTrue("chi-square test failed, rerun tests since expected to fail 5% of the time", v <= threshold[k-1]);
		}
		WeightedHybridUndoableMutation<TestObject> s = m.split();
		for (int i = 0; i < 10; i++) {
			s.mutate(t);
		}
		total = 0;
		for (int i = 0; i < k; i++) {
			int o = mutators.get(i).mutationCount;
			total += o;
		}
		// Verify split didn't keep references to pre-split
		// component mutators.
		assertEquals(n, total);
	}
	
	@Test
	public void testWeightedHybridUndoableMutationUndoMethod() {
		int n = 100;
		int[][][] weights = {
			{{1}},
			{{1, 1},{1, 2}},
			{{1, 1, 1},{1, 2, 1}}
		};
		for (int k = 1; k <= 3; k++) {
			for (int w = 0; w < weights[k-1].length; w++) {
				ArrayList<TestMutation> mutators = new ArrayList<TestMutation>();
				for (int i = 0; i < k; i++) {
					mutators.add(new TestMutation());
				}
				WeightedHybridUndoableMutation<TestObject> m = new WeightedHybridUndoableMutation<TestObject>(mutators, weights[k-1][w]); 
				TestObject t = new TestObject();
				for (int i = 0; i < n; i++) {
					m.mutate(t);
					m.mutate(t);
					// The assert statement is in the undo method of 
					// the TestMutation class found near bottom of this class.
					// It verifies that the correct undo method is called.
					m.undo(t);
					m.mutate(t);
				}
			}	
		}
	}
	
	
	private static class TestMutation implements UndoableMutationOperator<TestObject> {
		
		private int id;
		int mutationCount;
		
		private static int nextID = 0;
		private static int lastCalled = -1;
		
		public TestMutation() {
			id = nextID;
			nextID++;
			mutationCount = 0;
		}
		
		public void mutate(TestObject t) {
			mutationCount++;
			lastCalled = id;
		}
		
		public void undo(TestObject t) {
			assertEquals(lastCalled, id);
		}
		
		public TestMutation split() {
			return new TestMutation();
		}
	}
	
	private static class TestObject implements Copyable<TestObject> {
		public TestObject copy() { return new TestObject(); }
	}
}