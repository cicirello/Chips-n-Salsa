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
	
	private static class FakeProblemData implements SingleMachineSchedulingProblemData {

		private int[] d;
		
		public FakeProblemData(int[] d) {
			this.d = d;
		}
		
		@Override public int numberOfJobs() { return d.length; }
		@Override public int getProcessingTime(int j) { return 0; }
		@Override public int[] getCompletionTimes(Permutation schedule) { return null; }
		@Override public boolean hasDueDates() { return true; }
		@Override public int getDueDate(int j) { return d[j]; }
	}	
}