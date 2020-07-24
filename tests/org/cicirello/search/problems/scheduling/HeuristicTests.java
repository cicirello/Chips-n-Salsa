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
import org.cicirello.search.ss.PartialPermutation;
import org.cicirello.search.ss.IncrementalEvaluation;

/**
 * JUnit tests for scheduling heuristics.
 */
public class HeuristicTests {
	
	@Test
	public void testEDD() {
		int[] duedates = { 3, 0, 1, 7 };
		double[] expected = {0.25, 1.0, 0.5, 0.125};
		FakeProblemDuedates problem = new FakeProblemDuedates(duedates);
		EarliestDueDate h = new EarliestDueDate(problem);
		for (int j = 0; j < duedates.length; j++) {
			assertEquals(expected[j], h.h(null, j, null), 1E-10);
		}
	}
	
	@Test
	public void testWSPT() {
		double e = WeightedShortestProcessingTime.EPSILON;
		int highP = (int)Math.ceil(1 / e)*2;
		int[] w =    { 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
		int[] p =    { 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
		double[] expected = { 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p);
		WeightedShortestProcessingTime h = new WeightedShortestProcessingTime(problem);
		for (int j = 0; j < expected.length; j++) {
			assertEquals(expected[j], h.h(null, j, null), 1E-10);
		}
	}
	
	private static class FakeProblemDuedates implements SingleMachineSchedulingProblem {
		
		private FakeProblemData data;
		
		public FakeProblemDuedates(int[] d) {
			data = new FakeProblemData(d);
		}
		
		@Override
		public SingleMachineSchedulingProblemData getInstanceData() {
			return data;
		}
		
		@Override public int cost(Permutation p) { return 10; }
		@Override public int value(Permutation p) { return 10; }
	}
	
	private static class FakeProblemWeightsPTime implements SingleMachineSchedulingProblem {
		
		private FakeProblemData data;
		
		public FakeProblemWeightsPTime(int[] w, int[] p) {
			data = new FakeProblemData(w, p);
		}
		
		@Override
		public SingleMachineSchedulingProblemData getInstanceData() {
			return data;
		}
		
		@Override public int cost(Permutation p) { return 10; }
		@Override public int value(Permutation p) { return 10; }
	}
	
	private static class FakeProblemData implements SingleMachineSchedulingProblemData {

		private int[] d;
		private int[] w;
		private int[] p;
		
		public FakeProblemData(int[] d) {
			this.d = d.clone();
		}
		
		public FakeProblemData(int[] w, int[] p) {
			this.w = w.clone();
			this.p = p.clone();
		}
		
		@Override public int numberOfJobs() { return d.length; }
		@Override public int getProcessingTime(int j) { return p==null? 0: p[j]; }
		@Override public int[] getCompletionTimes(Permutation schedule) { return null; }
		@Override public boolean hasDueDates() { return d != null; }
		@Override public int getDueDate(int j) { return d[j]; }
		@Override public boolean hasWeights() { return w != null; }
		@Override public int getWeight(int j) { return w[j]; }
	}	
}