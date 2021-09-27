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

package org.cicirello.search.problems.scheduling;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.permutations.Permutation;

/**
 * JUnit tests for the various classes that implement
 * scheduling problem cost functions.
 */
public class CostFunctionTests {

	@Test
	public void testWeightedLateness() {   
		int[] inc = {0, 1, 2, 3, 4};      
		int[] dec = {4, 3, 2, 1, 0};      
		int[] p = {  7,  8,  4,  6, 3 };  
		int[] d = { 10, 13, 22, 15, 26}; 
		int[] w = {  2,  4,  1,  3,  4};  
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noS = new TestScheduleData(p, d, w);
		WeightedLateness costFunction = new WeightedLateness(noS);
		assertEquals(noS, costFunction.getInstanceData());
		assertEquals(37, costFunction.cost(p1));
		assertEquals(-51, costFunction.cost(p2));
		assertEquals(37, costFunction.value(p1));
		assertEquals(-51, costFunction.value(p2));
		assertTrue(costFunction.minCost() <= costFunction.cost(p1));
		assertTrue(costFunction.minCost() <= costFunction.cost(p2));
		
		int[][] s = {
			{99,  1, 99, 99, 99},
			{6,  99,  7, 99, 99},
			{99,  3, 99,  2, 99},
			{99, 99,  4, 99,  3},
			{99, 99, 99,  5, 99},
			{ 3, 99, 99, 99,  2}		
		};
		TestScheduleData withS = new TestScheduleData(p, s, d, w);
		costFunction = new WeightedLateness(withS);
		assertEquals(173, costFunction.cost(p1));
		assertEquals(85, costFunction.cost(p2));
		assertEquals(173, costFunction.value(p1));
		assertEquals(85, costFunction.value(p2));
		assertTrue(costFunction.minCost() <= costFunction.cost(p1));
		assertTrue(costFunction.minCost() <= costFunction.cost(p2));
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WeightedLateness(new TestScheduleDataNoDuedates())
		);
	}
	
	@Test
	public void testWeightedTardiness() {   
		int[] inc = {0, 1, 2, 3, 4};      
		int[] dec = {4, 3, 2, 1, 0};      
		int[] p = {  7,  8,  4,  6, 3 };  
		int[] d = { 10, 13, 22, 15, 26}; 
		int[] w = {  2,  4,  1,  3,  4};  
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noS = new TestScheduleData(p, d, w);
		WeightedTardiness costFunction = new WeightedTardiness(noS);
		assertEquals(noS, costFunction.getInstanceData());
		assertEquals(46, costFunction.cost(p1));
		assertEquals(68, costFunction.cost(p2));
		assertEquals(46, costFunction.value(p1));
		assertEquals(68, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		int[][] s = {
			{99,  1, 99, 99, 99},
			{6,  99,  7, 99, 99},
			{99,  3, 99,  2, 99},
			{99, 99,  4, 99,  3},
			{99, 99, 99,  5, 99},
			{ 3, 99, 99, 99,  2}		
		};
		TestScheduleData withS = new TestScheduleData(p, s, d, w);
		costFunction = new WeightedTardiness(withS);
		assertEquals(173, costFunction.cost(p1));
		assertEquals(169, costFunction.cost(p2));
		assertEquals(173, costFunction.value(p1));
		assertEquals(169, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WeightedTardiness(new TestScheduleDataNoDuedates())
		);
	}
	
	@Test
	public void testWeightedEarlinessTardiness() {   
		int[] inc = {0, 1, 2, 3, 4};      
		int[] dec = {4, 3, 2, 1, 0};      
		int[] p = {  7,  8,  4,  6, 3 };  
		int[] d = { 10, 13, 22, 15, 26}; 
		int[] w = {  2,  4,  1,  3,  4};  
		int[] we = { 1, 2, 3, 5, 6};
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noS = new TestScheduleData(p, d, w, we);
		WeightedEarlinessTardiness costFunction = new WeightedEarlinessTardiness(noS);
		assertEquals(noS, costFunction.getInstanceData());
		assertEquals(58, costFunction.cost(p1));
		assertEquals(263, costFunction.cost(p2));
		assertEquals(58, costFunction.value(p1));
		assertEquals(263, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		int[][] s = {
			{99,  1, 99, 99, 99},
			{6,  99,  7, 99, 99},
			{99,  3, 99,  2, 99},
			{99, 99,  4, 99,  3},
			{99, 99, 99,  5, 99},
			{ 3, 99, 99, 99,  2}		
		};
		TestScheduleData withS = new TestScheduleData(p, s, d, w, we);
		costFunction = new WeightedEarlinessTardiness(withS);
		assertEquals(173, costFunction.cost(p1));
		assertEquals(295, costFunction.cost(p2));
		assertEquals(173, costFunction.value(p1));
		assertEquals(295, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WeightedEarlinessTardiness(new TestScheduleDataNoDuedates())
		);
	}
	
	@Test
	public void testWeightedNumberTardy() {   
		int[] inc = {0, 1, 2, 3, 4};      
		int[] dec = {4, 3, 2, 1, 0};      
		int[] p = {  7,  8,  4,  6, 3 };  
		int[] d = { 10, 13, 22, 15, 26}; 
		int[] w = {  2,  4,  1,  3,  4};  
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noS = new TestScheduleData(p, d, w);
		WeightedNumberTardyJobs costFunction = new WeightedNumberTardyJobs(noS);
		assertEquals(noS, costFunction.getInstanceData());
		assertEquals(11, costFunction.cost(p1));
		assertEquals(6, costFunction.cost(p2));
		assertEquals(11, costFunction.value(p1));
		assertEquals(6, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		int[][] s = {
			{99,  1, 99, 99, 99},
			{6,  99,  7, 99, 99},
			{99,  3, 99,  2, 99},
			{99, 99,  4, 99,  3},
			{99, 99, 99,  5, 99},
			{ 3, 99, 99, 99,  2}		
		};
		TestScheduleData withS = new TestScheduleData(p, s, d, w);
		costFunction = new WeightedNumberTardyJobs(withS);
		assertEquals(12, costFunction.cost(p1));
		assertEquals(10, costFunction.cost(p2));
		assertEquals(12, costFunction.value(p1));
		assertEquals(10, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WeightedNumberTardyJobs(new TestScheduleDataNoDuedates())
		);
	}
	
	@Test
	public void testWeightedSquaredTardiness() { 
		int[] inc = {0, 1, 2, 3, 4};      
		int[] dec = {4, 3, 2, 1, 0};      
		int[] p = {  7,  8,  4,  6, 3 };  
		int[] d = { 10, 13, 22, 15, 26}; 
		int[] w = {  2,  4,  1,  3,  4}; 
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noS = new TestScheduleData(p, d, w);
		WeightedSquaredTardiness costFunction = new WeightedSquaredTardiness(noS);
		assertEquals(noS, costFunction.getInstanceData());
		assertEquals(332, costFunction.cost(p1));
		assertEquals(904, costFunction.cost(p2));
		assertEquals(332, costFunction.value(p1));
		assertEquals(904, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		int[][] s = {
			{99,  1, 99, 99, 99},
			{6,  99,  7, 99, 99},
			{99,  3, 99,  2, 99},
			{99, 99,  4, 99,  3},
			{99, 99, 99,  5, 99},
			{ 3, 99, 99, 99,  2}		
		};
		TestScheduleData withS = new TestScheduleData(p, s, d, w);
		costFunction = new WeightedSquaredTardiness(withS);
		assertEquals(3091, costFunction.cost(p1));
		assertEquals(4831, costFunction.cost(p2));
		assertEquals(3091, costFunction.value(p1));
		assertEquals(4831, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WeightedSquaredTardiness(new TestScheduleDataNoDuedates())
		);
	}

	@Test
	public void testMaximumLateness() {
		int[] inc = {0, 1, 2, 3, 4};
		int[] dec = {4, 3, 2, 1, 0};
		int[] p = {  7,  8,  4,  6, 3 }; 
		int[] d = { 10, 13, 22, 15, 26}; 
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noS = new TestScheduleData(p, d);
		MinimizeMaximumLateness costFunction = new MinimizeMaximumLateness(noS);
		assertEquals(noS, costFunction.getInstanceData());
		assertEquals(10, costFunction.cost(p1));
		assertEquals(18, costFunction.cost(p2));
		assertEquals(10, costFunction.value(p1));
		assertEquals(18, costFunction.value(p2));
		assertTrue(costFunction.minCost() <= costFunction.cost(p1));
		assertTrue(costFunction.minCost() <= costFunction.cost(p2));
		
		int[] d2 = { 100, 100, 100, 100, 100};
		noS = new TestScheduleData(p, d2);
		costFunction = new MinimizeMaximumLateness(noS);
		assertEquals(-72, costFunction.cost(p1));
		assertEquals(-72, costFunction.cost(p2));
		assertEquals(-72, costFunction.value(p1));
		assertEquals(-72, costFunction.value(p2));
		assertTrue(costFunction.minCost() <= costFunction.cost(p1));
		assertTrue(costFunction.minCost() <= costFunction.cost(p2));
		
		int[][] s = {
			{99,  1, 99, 99, 99},
			{6,  99,  7, 99, 99},
			{99,  3, 99,  2, 99},
			{99, 99,  4, 99,  3},
			{99, 99, 99,  5, 99},
			{ 3, 99, 99, 99,  2}		
		};
		TestScheduleData withS = new TestScheduleData(p, s, d);
		costFunction = new MinimizeMaximumLateness(withS);
		assertEquals(23, costFunction.cost(p1));
		assertEquals(38, costFunction.cost(p2));
		assertEquals(23, costFunction.value(p1));
		assertEquals(38, costFunction.value(p2));
		assertTrue(costFunction.minCost() <= costFunction.cost(p1));
		assertTrue(costFunction.minCost() <= costFunction.cost(p2));
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new MinimizeMaximumLateness(new TestScheduleDataNoDuedates())
		);
	}
	
	@Test
	public void testMaximumFlowtime() {
		int[] inc = {0, 1, 2, 3, 4};
		int[] dec = {4, 3, 2, 1, 0};
		int[] p = {  7,  8,  4,  6, 3 }; 
		int[] r = { 2, 5, 5, 7, 10}; 
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noR = new TestScheduleData(p);
		MinimizeMaximumFlowtime costFunction = new MinimizeMaximumFlowtime(noR);
		assertEquals(noR, costFunction.getInstanceData());
		assertEquals(28, costFunction.cost(p1));
		assertEquals(28, costFunction.cost(p2));
		assertEquals(28, costFunction.value(p1));
		assertEquals(28, costFunction.value(p2));
		assertTrue(costFunction.minCost() <= costFunction.cost(p1));
		assertTrue(costFunction.minCost() <= costFunction.cost(p2));
		
		TestScheduleData withR = new TestScheduleData(p, r, false);
		costFunction = new MinimizeMaximumFlowtime(withR);
		assertEquals(20, costFunction.cost(p1));
		assertEquals(36, costFunction.cost(p2));
		assertEquals(20, costFunction.value(p1));
		assertEquals(36, costFunction.value(p2));
		assertTrue(costFunction.minCost() <= costFunction.cost(p1));
		assertTrue(costFunction.minCost() <= costFunction.cost(p2));
	}
	
	@Test
	public void testWeightedFlowtime() {
		int[] inc = {0, 1, 2, 3, 4};     
		int[] dec = {4, 3, 2, 1, 0};     
		int[] p = {  7,  8,  4,  6, 3 }; 
		int[] r = { 2, 5, 5, 7, 10};     
		int[] w = { 2, 1, 2, 3, 1 };
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noR = new TestScheduleData(p, (int[])null, w);
		WeightedFlowtime costFunction = new WeightedFlowtime(noR);
		assertEquals(noR, costFunction.getInstanceData());
		assertEquals(170, costFunction.cost(p1));
		assertEquals(133, costFunction.cost(p2));
		assertEquals(170, costFunction.value(p1));
		assertEquals(133, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		TestScheduleData withR = new TestScheduleData(p, r, w, false);
		costFunction = new WeightedFlowtime(withR);
		assertEquals(138, costFunction.cost(p1));
		assertEquals(173, costFunction.cost(p2));
		assertEquals(138, costFunction.value(p1));
		assertEquals(173, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
	}
	
	@Test
	public void testMaximumTardiness() {
		int[] inc = {0, 1, 2, 3, 4};
		int[] dec = {4, 3, 2, 1, 0};
		int[] p = {  7,  8,  4,  6, 3 }; 
		int[] d = { 10, 13, 22, 15, 26}; 
		Permutation p1 = new Permutation(inc);
		Permutation p2 = new Permutation(dec);
		TestScheduleData noS = new TestScheduleData(p, d);
		MinimizeMaximumTardiness costFunction = new MinimizeMaximumTardiness(noS);
		assertEquals(noS, costFunction.getInstanceData());
		assertEquals(10, costFunction.cost(p1));
		assertEquals(18, costFunction.cost(p2));
		assertEquals(10, costFunction.value(p1));
		assertEquals(18, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		int[] d2 = { 100, 100, 100, 100, 100};
		noS = new TestScheduleData(p, d2);
		costFunction = new MinimizeMaximumTardiness(noS);
		assertEquals(0, costFunction.cost(p1));
		assertEquals(0, costFunction.cost(p2));
		assertEquals(0, costFunction.value(p1));
		assertEquals(0, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		int[][] s = {
			{99,  1, 99, 99, 99},
			{6,  99,  7, 99, 99},
			{99,  3, 99,  2, 99},
			{99, 99,  4, 99,  3},
			{99, 99, 99,  5, 99},
			{ 3, 99, 99, 99,  2}		
		};
		TestScheduleData withS = new TestScheduleData(p, s, d);
		costFunction = new MinimizeMaximumTardiness(withS);
		assertEquals(23, costFunction.cost(p1));
		assertEquals(38, costFunction.cost(p2));
		assertEquals(23, costFunction.value(p1));
		assertEquals(38, costFunction.value(p2));
		assertEquals(0, costFunction.minCost());
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new MinimizeMaximumTardiness(new TestScheduleDataNoDuedates())
		);
	}

	@Test
	public void testMakespan() {
		for (int n = 1; n <= 5; n++) {
			int[] p = new int[n];
			int[] inOrder = new int[n];
			int[] revOrder = new int[n];
			for (int j = 0; j < n; j++) {
				p[j] = 2 + j;
				inOrder[j] = j;
				revOrder[j] = n-1-j;
			}
			Permutation p1 = new Permutation(inOrder);
			Permutation p2 = new Permutation(revOrder);
			TestScheduleData onlyP = new TestScheduleData(p);
			MinimizeMakespan costFunction = new MinimizeMakespan(onlyP);
			assertEquals(onlyP, costFunction.getInstanceData());
			int expected = n+(n+1)*n/2;
			assertEquals(expected, costFunction.cost(p1));
			assertEquals(expected, costFunction.cost(p2));
			assertEquals(expected, costFunction.value(p1));
			assertEquals(expected, costFunction.value(p2));
			assertTrue(costFunction.minCost() <= expected);
			
			int[][] s = new int[n+1][n];
			for (int i = 0; i <= n; i++) {
				for (int j = 0; j < n; j++) {
					s[i][j] = 100;
				}
			}
			int expectedS1 = expected;
			int expectedS2 = expected;
			for (int j = 1; j < n; j++) {
				s[j-1][j] = 4 + 2*j;
				expectedS1 += s[j-1][j]; 
			}
			for (int j = 0; j < n-1; j++) {
				s[j+1][j] = 3 + 2*j;
				expectedS2 += s[j+1][j]; 
			}
			s[n][0] = 25;
			expectedS1 += s[n][0]; 
			if (n>1) {
				s[n][n-1] = 42;
				expectedS2 += s[n][n-1];
			}				
			TestScheduleData withS = new TestScheduleData(p, s);
			costFunction = new MinimizeMakespan(withS);
			assertEquals(expectedS1, costFunction.cost(p1));
			if (n>1) assertEquals(expectedS2, costFunction.cost(p2));
			assertEquals(expectedS1, costFunction.value(p1));
			if (n>1) assertEquals(expectedS2, costFunction.value(p2));
			assertTrue(costFunction.minCost() <= expectedS1);
			if (n>1) assertTrue(costFunction.minCost() <= expectedS2);
		}
	}
	
	/*
	 * Fake designed for test cases.
	 */
	private static class TestScheduleDataNoDuedates implements SingleMachineSchedulingProblemData {
		public boolean hasDueDates() { return false; }
		public int[] getCompletionTimes(Permutation s) { return null; }
		public int getProcessingTime(int j) { return 0; }
		public int numberOfJobs() { return 1; }
	}
	
	
	
	/*
	 * Fake designed for test cases.
	 */
	private static class TestScheduleData implements SingleMachineSchedulingProblemData {
		
		private int[] p;
		private int[][] s;
		private int[] d;
		private int[] w;
		private int[] we;
		private int[] r;
		
		public TestScheduleData(int[] p) {
			this.p = p.clone();
		}
		
		public TestScheduleData(int[] p, int[] d) {
			this(p);
			if (d != null) this.d = d.clone();
		}
		
		public TestScheduleData(int[] p, int[] x, boolean xIsD) {
			this(p);
			if (xIsD) this.d = x.clone();
			else this.r = x.clone();
		}
		
		public TestScheduleData(int[] p, int[] d, int[] w) {
			this(p, d);
			this.w = w.clone();
		}
		
		public TestScheduleData(int[] p, int[] x, int[] w, boolean xIsD) {
			this(p, x, xIsD);
			this.w = w.clone();
		}
		
		public TestScheduleData(int[] p, int[] d, int[] w, int[] we) {
			this(p, d, w);
			this.we = we.clone();
		}
		
		public TestScheduleData(int[] p, int[][] s) {
			this(p);
			this.s = new int[s.length][];
			for (int i = 0; i < s.length; i++) {
				this.s[i] = s[i].clone();
			}
		}
		
		public TestScheduleData(int[] p, int[][] s, int[] d) {
			this(p, s);
			this.d = d.clone();
		}
		
		public TestScheduleData(int[] p, int[][] s, int[] d, int[] w) {
			this(p, s, d);
			this.w = w.clone();
		}
		
		public TestScheduleData(int[] p, int[][] s, int[] d, int[] w, int[] we) {
			this(p, s, d, w);
			this.we = we.clone();
		}
		
		@Override public int numberOfJobs() { return p.length; }
		@Override public int getProcessingTime(int j) { return p[j]; }
		@Override public int getSetupTime(int i, int j) { return s[i][j]; }
		@Override public int getDueDate(int j) { return d[j]; }
		@Override public int getReleaseDate(int j) { return r!=null ? r[j] : 0; }
		@Override public int getWeight(int j) { return w[j]; }
		@Override public int getEarlyWeight(int j) { return we[j]; }
		@Override public boolean hasSetupTimes() { return s != null; } 
		@Override public boolean hasDueDates() { return d != null; } 
		@Override public boolean hasWeights() { return w != null; } 
		@Override public boolean hasEarlyWeights() { return we != null; }
		@Override public boolean hasReleaseDates() { return r != null; }
		
		@Override 
		public int[] getCompletionTimes(Permutation schedule) {
			int[] c = new int[p.length];
			int time = 0;
			if (r!=null) {
				// test cases don't handle mix of setups and release
				for (int i = 0; i < schedule.length(); i++) {
					int j = schedule.get(i);
					if (r[j] > time) time = r[j];
					time += p[j];
					c[j] = time;
				}
			} else if (s != null) {
				int last = c.length;
				for (int i = 0; i < schedule.length(); i++) {
					int j = schedule.get(i);
					time += s[last][j] + p[j];
					c[j] = time;
					last = j;
				}
			} else {
				for (int i = 0; i < schedule.length(); i++) {
					int j = schedule.get(i);
					time += p[j];
					c[j] = time;
				}
			}
			return c;
		}
	}
}