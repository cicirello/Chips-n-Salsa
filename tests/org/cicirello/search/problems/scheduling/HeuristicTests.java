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
		double e = WeightedShortestProcessingTime.MIN_H;
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
	
	@Test
	public void testWSPT2() {
		double e = WeightedShortestProcessingTimeLateOnly.MIN_H;
		int highP = (int)Math.ceil(1 / e)*2;
		int[] w =    { 1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
		int[] p =    { 1, 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
		double[] expected = { 999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
		PartialPermutation partial = new PartialPermutation(expected.length);
		//Doesn't really matter: partial.extend(0);
		// All late tests
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0);
		WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
		IncrementalEvaluation inc = h.createIncrementalEvaluation();
		inc.extend(partial, 0);
		for (int j = 1; j < expected.length; j++) {
			assertEquals("j:"+j, expected[j], h.h(partial, j, inc), 1E-10);
		}
		// All on time tests
		problem = new FakeProblemWeightsPTime(w, p, 20);
		h = new WeightedShortestProcessingTimeLateOnly(problem);
		inc = h.createIncrementalEvaluation();
		inc.extend(partial, 0);
		for (int j = 1; j < expected.length; j++) {
			assertEquals(e, h.h(partial, j, inc), 1E-10);
		}
	}
	
	@Test
	public void testWeightedCriticalRatio() {
		double e = WeightedCriticalRatio.MIN_H;
		int highP = (int)Math.ceil(1 / e)*2;
		int[] w =    { 1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
		int[] p =    { 1, 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
		double[] expected0 = { 999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
		double[] slack = new double[p.length];
		for (int i = 1; i < p.length; i++) slack[i] = 20-p[i]-p[0];
		PartialPermutation partial = new PartialPermutation(expected0.length);
		//Doesn't really matter: partial.extend(0);
		// All late tests
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0);
		WeightedCriticalRatio h = new WeightedCriticalRatio(problem);
		IncrementalEvaluation inc = h.createIncrementalEvaluation();
		inc.extend(partial, 0);
		for (int j = 1; j < expected0.length; j++) {
			assertEquals("negativeSlack, j:"+j, expected0[j], h.h(partial, j, inc), 1E-10);
		}
		// All on time tests
		problem = new FakeProblemWeightsPTime(w, p, 20);
		h = new WeightedCriticalRatio(problem);
		inc = h.createIncrementalEvaluation();
		inc.extend(partial, 0);
		for (int j = 1; j < expected0.length; j++) {
			double expected = expected0[j]/(1.0+slack[j]/p[j]);
			if (slack[j] <= 0) expected = e;
			assertEquals("positiveSlack, j:"+j, expected < e ? e : expected, h.h(partial, j, inc), 1E-10);
		}
	}
	
	@Test
	public void testMontagne() {
		double e = Montagne.MIN_H;
		int highP = (int)Math.ceil(1 / e)*2;
		int[] w =    { 1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 2, 2, 1};
		int[] p =    { 1, 1, 2, 4, 8, 1, 2, 4, 8, 1, 2, 4, 8, highP};
		double[] expected0 = { 999, 1, 0.5, 0.25, 0.125, e, e, e, e, 2, 1, 0.5, 0.25, e};
		int pSum = 0;
		for (int i = 1; i < p.length; i++) pSum += p[i];
		PartialPermutation partial = new PartialPermutation(expected0.length);
		//Doesn't really matter: partial.extend(0);
		// All d=0
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0);
		Montagne h = new Montagne(problem);
		IncrementalEvaluation inc = h.createIncrementalEvaluation();
		inc.extend(partial, 0);
		for (int j = 1; j < expected0.length; j++) {
			assertEquals("d=0, j:"+j, expected0[j], h.h(partial, j, inc), 1E-10);
		}
		// All d = pSum
		problem = new FakeProblemWeightsPTime(w, p, pSum);
		h = new Montagne(problem);
		inc = h.createIncrementalEvaluation();
		inc.extend(partial, 0);
		for (int j = 1; j < expected0.length; j++) {
			assertEquals("d=pSum, j:"+j, e, h.h(partial, j, inc), 1E-10);
		}
		// All d = pSum / 2
		problem = new FakeProblemWeightsPTime(w, p, pSum/2);
		h = new Montagne(problem);
		inc = h.createIncrementalEvaluation();
		inc.extend(partial, 0);
		for (int j = 1; j < expected0.length; j++) {
			double correction = 1.0 - pSum/2/(1.0*pSum);
			double expected = expected0[j]*correction;
			assertEquals("d=pSum, j:"+j, expected < e ? e : expected, h.h(partial, j, inc), 1E-10);
		}
	}
	
	@Test
	public void testSchedulingHeuristicIncEvalExtend() {
		int[] w = { 1, 1, 1, 1, 1 };
		int[] p = { 3, 2, 1, 4, 5 };
		int[] e = { 3, 5, 6, 10, 15 };
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0);
		WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
		PartialPermutation partial = new PartialPermutation(e.length);
		SchedulingHeuristic.IncrementalTimeCalculator inc = (SchedulingHeuristic.IncrementalTimeCalculator)h.createIncrementalEvaluation();
		assertEquals(0, inc.currentTime());
		for (int i = 0; i < p.length; i++) {
			inc.extend(partial, i);
			if (i < partial.numExtensions()) partial.extend(i);
			else partial.extend(partial.numExtensions()-1);
			assertEquals(e[i], inc.currentTime());
		}
	}
	
	@Test
	public void testSchedulingHeuristicIncEvalExtendWithSetups() {
		int[] w = { 1, 1, 1, 1, 1 };
		int[] p = { 3, 2, 1, 4, 5 };
		int[] e = { 10, 13, 18, 29, 44 };
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 0, 7);
		WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
		PartialPermutation partial = new PartialPermutation(e.length);
		SchedulingHeuristic.IncrementalTimeCalculator inc = (SchedulingHeuristic.IncrementalTimeCalculator)h.createIncrementalEvaluation();
		assertEquals(0, inc.currentTime());
		for (int i = 0; i < p.length; i++) {
			inc.extend(partial, i);
			if (i < partial.numExtensions()) partial.extend(i);
			else partial.extend(partial.numExtensions()-1);
			assertEquals(e[i], inc.currentTime());
		}
	}
	
	@Test
	public void testSchedulingHeuristicSlack() {
		int[] w = { 1, 1, 1, 1, 1 };
		int[] p = { 3, 2, 1, 4, 5 };
		int[] e = { 7, 5, 4, 0, -5 };
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 10);
		WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
		PartialPermutation partial = new PartialPermutation(e.length);
		SchedulingHeuristic.IncrementalTimeCalculator inc = (SchedulingHeuristic.IncrementalTimeCalculator)h.createIncrementalEvaluation();
		for (int i = 0; i < p.length; i++) {
			assertEquals(e[i], inc.slack(i, partial));
			assertEquals(e[i], inc.slack(i));
			inc.extend(partial, i);
			if (i < partial.numExtensions()) partial.extend(i);
			else partial.extend(partial.numExtensions()-1);
		}
	}
	
	@Test
	public void testSchedulingHeuristicSlackWithSetups() {
		int[] w = { 1, 1, 1, 1, 1 };
		int[] p = { 3, 2, 1, 4, 5 };
		int[] e = { 19, 16, 11, 0, -15 };
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 29, 7);
		WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
		PartialPermutation partial = new PartialPermutation(e.length);
		SchedulingHeuristic.IncrementalTimeCalculator inc = (SchedulingHeuristic.IncrementalTimeCalculator)h.createIncrementalEvaluation();
		for (int i = 0; i < p.length; i++) {
			assertEquals(e[i], inc.slack(i, partial));
			inc.extend(partial, i);
			if (i < partial.numExtensions()) partial.extend(i);
			else partial.extend(partial.numExtensions()-1);
		}
	}
	
	@Test
	public void testSchedulingHeuristicSlackPlus() {
		int[] w = { 1, 1, 1, 1, 1 };
		int[] p = { 3, 2, 1, 4, 5 };
		int[] e = { 7, 5, 4, 0, 0 };
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 10);
		WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
		PartialPermutation partial = new PartialPermutation(e.length);
		SchedulingHeuristic.IncrementalTimeCalculator inc = (SchedulingHeuristic.IncrementalTimeCalculator)h.createIncrementalEvaluation();
		for (int i = 0; i < p.length; i++) {
			assertEquals(e[i], inc.slackPlus(i, partial));
			assertEquals(e[i], inc.slackPlus(i));
			inc.extend(partial, i);
			if (i < partial.numExtensions()) partial.extend(i);
			else partial.extend(partial.numExtensions()-1);
		}
	}
	
	@Test
	public void testSchedulingHeuristicSlackPlusWithSetups() {
		int[] w = { 1, 1, 1, 1, 1 };
		int[] p = { 3, 2, 1, 4, 5 };
		int[] e = { 19, 16, 11, 0, 0 };
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 29, 7);
		WeightedShortestProcessingTimeLateOnly h = new WeightedShortestProcessingTimeLateOnly(problem);
		PartialPermutation partial = new PartialPermutation(e.length);
		SchedulingHeuristic.IncrementalTimeCalculator inc = (SchedulingHeuristic.IncrementalTimeCalculator)h.createIncrementalEvaluation();
		for (int i = 0; i < p.length; i++) {
			assertEquals(e[i], inc.slackPlus(i, partial));
			inc.extend(partial, i);
			if (i < partial.numExtensions()) partial.extend(i);
			else partial.extend(partial.numExtensions()-1);
		}
	}
	
	@Test
	public void testSchedulingHeuristicTotalAveragePTime() {
		int[] w = { 1, 1, 1, 1, 1 };
		int[] p = { 3, 2, 1, 4, 5 };
		int[] expectedTotal = { 15, 12, 10, 9, 5 };
		FakeProblemWeightsPTime problem = new FakeProblemWeightsPTime(w, p, 10);
		Montagne h = new Montagne(problem);
		PartialPermutation partial = new PartialPermutation(expectedTotal.length);
		SchedulingHeuristic.IncrementalAverageProcessingCalculator inc = (SchedulingHeuristic.IncrementalAverageProcessingCalculator)h.createIncrementalEvaluation();
		for (int i = 0; i < p.length; i++) {
			assertEquals(expectedTotal[i], inc.totalProcessingTime());
			assertEquals(expectedTotal[i]/(p.length-i-0.0), inc.averageProcessingTime(), 1E-10);
			inc.extend(partial, i);
			if (i < partial.numExtensions()) partial.extend(i);
			else partial.extend(partial.numExtensions()-1);
		}
		assertEquals(0, inc.totalProcessingTime());
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
		
		public FakeProblemWeightsPTime(int[] w, int[] p, int d) {
			data = new FakeProblemData(w, p, d);
		}
		
		public FakeProblemWeightsPTime(int[] w, int[] p, int d, int s) {
			data = new FakeProblemData(w, p, d, s);
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
		private int s;
		
		public FakeProblemData(int[] d) {
			this.d = d.clone();
			s = -1;
		}
		
		public FakeProblemData(int[] w, int[] p) {
			this.w = w.clone();
			this.p = p.clone();
			s = -1;
		}
		
		public FakeProblemData(int[] w, int[] p, int d) {
			this.w = w.clone();
			this.p = p.clone();
			this.d = new int[w.length];
			for (int i = 0; i < this.d.length; i++) this.d[i] = d;
			s = -1;
		}
		
		public FakeProblemData(int[] w, int[] p, int d, int s) {
			this(w, p, d);
			this.s = s;
		}
		
		@Override public int numberOfJobs() { return d.length; }
		@Override public int getProcessingTime(int j) { return p==null? 0: p[j]; }
		@Override public int[] getCompletionTimes(Permutation schedule) { return null; }
		@Override public boolean hasDueDates() { return d != null; }
		@Override public int getDueDate(int j) { return d[j]; }
		@Override public boolean hasWeights() { return w != null; }
		@Override public int getWeight(int j) { return w[j]; }
		@Override public int getSetupTime(int j) { return s; }
		@Override public int getSetupTime(int i, int j) { return 2*i + j; }
		@Override public boolean hasSetupTimes() { return s > 0; }
	}	
}