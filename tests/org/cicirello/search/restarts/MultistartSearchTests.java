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
 
package org.cicirello.search.restarts;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.util.Copyable;
import java.util.Random;

/**
 * JUnit 4 test cases for search restarter.
 */
public class MultistartSearchTests {
	
	@Test
	public void testMultistarterConstantLength() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 10; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				Multistarter<TestObject> restarter = new Multistarter<TestObject>(heur, r);
				ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
				assertNotNull(tracker);
				assertEquals(0, restarter.getTotalRunLength());
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
				assertEquals(0, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				SolutionCostPair<TestObject> pair = restarter.optimize(re);
				assertNotNull(pair);
				assertTrue(pair.getCost()>1);
				assertEquals(re*r, restarter.getTotalRunLength());
				assertEquals(re, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
			}
			for (int re = 1; re <= 10; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ReoptimizableMultistarter<TestObject> restarter = new ReoptimizableMultistarter<TestObject>(heur, r);
				ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
				assertNotNull(tracker);
				assertEquals(0, restarter.getTotalRunLength());
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
				assertEquals(0, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
				assertNotNull(pair);
				assertTrue(pair.getCost()>1);
				assertEquals(re*r, restarter.getTotalRunLength());
				assertEquals(0, heur.optCounter);
				assertEquals(re, heur.reoptCounter);
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
			}
		}
	}
	
	@Test
	public void testMultistarterConstantLengthStopped() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 10; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					Multistarter<TestObject> restarter = new Multistarter<TestObject>(heur, r);
					ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
					assertNotNull(tracker);
					assertEquals(0, restarter.getTotalRunLength());
					assertFalse(tracker.didFindBest());
					assertFalse(tracker.isStopped());
					assertEquals(0, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					SolutionCostPair<TestObject> pair = restarter.optimize(re);
					assertNotNull(pair);
					assertTrue(pair.getCost()>1);
					assertEquals(early, restarter.getTotalRunLength());
					assertEquals(i, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					assertFalse(tracker.didFindBest());
					assertTrue(tracker.isStopped());
				}
			}
			for (int re = 1; re <= 10; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					ReoptimizableMultistarter<TestObject> restarter = new ReoptimizableMultistarter<TestObject>(heur, r);
					ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
					assertNotNull(tracker);
					assertEquals(0, restarter.getTotalRunLength());
					assertFalse(tracker.didFindBest());
					assertFalse(tracker.isStopped());
					assertEquals(0, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
					assertNotNull(pair);
					assertTrue(pair.getCost()>1);
					assertEquals(early, restarter.getTotalRunLength());
					assertEquals(0, heur.optCounter);
					assertEquals(i, heur.reoptCounter);
					assertFalse(tracker.didFindBest());
					assertTrue(tracker.isStopped());
				}
			}
		}
	}
	
	@Test
	public void testMultistarterConstantLengthBest() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 10; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					Multistarter<TestObject> restarter = new Multistarter<TestObject>(heur, r);
					ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
					assertNotNull(tracker);
					assertEquals(0, restarter.getTotalRunLength());
					assertFalse(tracker.didFindBest());
					assertFalse(tracker.isStopped());
					assertEquals(0, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					SolutionCostPair<TestObject> pair = restarter.optimize(re);
					assertNotNull(pair);
					assertEquals(1, pair.getCost());
					assertEquals(early, restarter.getTotalRunLength());
					assertEquals(i, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					assertTrue(tracker.didFindBest());
					assertFalse(tracker.isStopped());
				}
			}
			for (int re = 1; re <= 10; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					ReoptimizableMultistarter<TestObject> restarter = new ReoptimizableMultistarter<TestObject>(heur, r);
					ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
					assertNotNull(tracker);
					assertEquals(0, restarter.getTotalRunLength());
					assertFalse(tracker.didFindBest());
					assertFalse(tracker.isStopped());
					assertEquals(0, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
					assertNotNull(pair);
					assertEquals(1, pair.getCost());
					assertEquals(early, restarter.getTotalRunLength());
					assertEquals(0, heur.optCounter);
					assertEquals(i, heur.reoptCounter);
					assertTrue(tracker.didFindBest());
					assertFalse(tracker.isStopped());
				}
			}
		}
	}
	
	@Test
	public void testMultistarterWithRestartSchedule() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 10; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				Multistarter<TestObject> restarter = new Multistarter<TestObject>(heur, new ConstantRestartSchedule(r));
				ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
				assertNotNull(tracker);
				assertEquals(0, restarter.getTotalRunLength());
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
				assertEquals(0, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				SolutionCostPair<TestObject> pair = restarter.optimize(re);
				assertNotNull(pair);
				assertTrue(pair.getCost() > 1);
				assertEquals(re*r, restarter.getTotalRunLength());
				assertEquals(re, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
			}
			for (int re = 1; re <= 10; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ReoptimizableMultistarter<TestObject> restarter = new ReoptimizableMultistarter<TestObject>(heur, new ConstantRestartSchedule(r));
				ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
				assertNotNull(tracker);
				assertEquals(0, restarter.getTotalRunLength());
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
				assertEquals(0, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
				assertNotNull(pair);
				assertTrue(pair.getCost() > 1);
				assertEquals(re*r, restarter.getTotalRunLength());
				assertEquals(0, heur.optCounter);
				assertEquals(re, heur.reoptCounter);
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
			}
		}
	}
	
	@Test
	public void testMultistarterWithRestartScheduleStopped() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 10; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					Multistarter<TestObject> restarter = new Multistarter<TestObject>(heur, new ConstantRestartSchedule(r));
					ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
					assertNotNull(tracker);
					assertEquals(0, restarter.getTotalRunLength());
					assertFalse(tracker.didFindBest());
					assertFalse(tracker.isStopped());
					assertEquals(0, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					SolutionCostPair<TestObject> pair = restarter.optimize(re);
					assertNotNull(pair);
					assertTrue(pair.getCost() > 1);
					assertEquals(early, restarter.getTotalRunLength());
					assertEquals(i, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					assertFalse(tracker.didFindBest());
					assertTrue(tracker.isStopped());
				}
			}
			for (int re = 1; re <= 10; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					ReoptimizableMultistarter<TestObject> restarter = new ReoptimizableMultistarter<TestObject>(heur, new ConstantRestartSchedule(r));
					ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
					assertNotNull(tracker);
					assertEquals(0, restarter.getTotalRunLength());
					assertFalse(tracker.didFindBest());
					assertFalse(tracker.isStopped());
					assertEquals(0, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
					assertNotNull(pair);
					assertTrue(pair.getCost() > 1);
					assertEquals(early, restarter.getTotalRunLength());
					assertEquals(0, heur.optCounter);
					assertEquals(i, heur.reoptCounter);
					assertFalse(tracker.didFindBest());
					assertTrue(tracker.isStopped());
				}
			}
		}
	}
	
	@Test
	public void testMultistarterWithRestartScheduleBest() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 10; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					Multistarter<TestObject> restarter = new Multistarter<TestObject>(heur, new ConstantRestartSchedule(r));
					ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
					assertNotNull(tracker);
					assertEquals(0, restarter.getTotalRunLength());
					assertFalse(tracker.didFindBest());
					assertFalse(tracker.isStopped());
					assertEquals(0, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					SolutionCostPair<TestObject> pair = restarter.optimize(re);
					assertNotNull(pair);
					assertEquals(1, pair.getCost());
					assertEquals(early, restarter.getTotalRunLength());
					assertEquals(i, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					assertTrue(tracker.didFindBest());
					assertFalse(tracker.isStopped());
				}
			}
			for (int re = 1; re <= 10; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					ReoptimizableMultistarter<TestObject> restarter = new ReoptimizableMultistarter<TestObject>(heur, new ConstantRestartSchedule(r));
					ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
					assertNotNull(tracker);
					assertEquals(0, restarter.getTotalRunLength());
					assertFalse(tracker.didFindBest());
					assertFalse(tracker.isStopped());
					assertEquals(0, heur.optCounter);
					assertEquals(0, heur.reoptCounter);
					SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
					assertNotNull(pair);
					assertEquals(1, pair.getCost());
					assertEquals(early, restarter.getTotalRunLength());
					assertEquals(0, heur.optCounter);
					assertEquals(i, heur.reoptCounter);
					assertTrue(tracker.didFindBest());
					assertFalse(tracker.isStopped());
				}
			}
		}
	}
	
	
	@Test
	public void testMultistarterSplit() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 10; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				Multistarter<TestObject> restarter = new Multistarter<TestObject>(heur, r);
				ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
				assertNotNull(tracker);
				assertEquals(0, restarter.getTotalRunLength());
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
				assertEquals(0, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				SolutionCostPair<TestObject> pair = restarter.optimize(re);
				assertNotNull(pair);
				assertTrue(pair.getCost()>1);
				assertEquals(re*r, restarter.getTotalRunLength());
				assertEquals(re, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
				
				//split and use split
				Multistarter<TestObject> split = restarter.split();
				ProgressTracker<TestObject> trackerSplit = split.getProgressTracker();
				assertNotNull(trackerSplit);
				assertEquals(0, split.getTotalRunLength());
				assertFalse(trackerSplit.didFindBest());
				assertFalse(trackerSplit.isStopped());
				assertEquals(re, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				SolutionCostPair<TestObject> pairSplit = split.optimize(re);
				assertNotNull(pairSplit);
				assertTrue(pairSplit.getCost()>1);
				assertEquals(re*r, split.getTotalRunLength());
				assertEquals(re, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				assertFalse(trackerSplit.didFindBest());
				assertFalse(trackerSplit.isStopped());
				
				// use original and make sure split didn't effect
				pair = restarter.optimize(re);
				assertNotNull(pair);
				assertTrue(pair.getCost()>1);
				assertEquals(2*re*r, restarter.getTotalRunLength());
				assertEquals(2*re, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
			}
			for (int re = 1; re <= 10; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ReoptimizableMultistarter<TestObject> restarter = new ReoptimizableMultistarter<TestObject>(heur, r);
				ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
				assertNotNull(tracker);
				assertEquals(0, restarter.getTotalRunLength());
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
				assertEquals(0, heur.optCounter);
				assertEquals(0, heur.reoptCounter);
				SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
				assertNotNull(pair);
				assertTrue(pair.getCost()>1);
				assertEquals(re*r, restarter.getTotalRunLength());
				assertEquals(0, heur.optCounter);
				assertEquals(re, heur.reoptCounter);
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
				
				//split and use split
				ReoptimizableMultistarter<TestObject> split = restarter.split();
				ProgressTracker<TestObject> trackerSplit = split.getProgressTracker();
				assertNotNull(trackerSplit);
				assertEquals(0, split.getTotalRunLength());
				assertFalse(trackerSplit.didFindBest());
				assertFalse(trackerSplit.isStopped());
				assertEquals(0, heur.optCounter);
				assertEquals(re, heur.reoptCounter);
				SolutionCostPair<TestObject> pairSplit = split.reoptimize(re);
				assertNotNull(pairSplit);
				assertTrue(pairSplit.getCost()>1);
				assertEquals(re*r, split.getTotalRunLength());
				assertEquals(0, heur.optCounter);
				assertEquals(re, heur.reoptCounter);
				assertFalse(trackerSplit.didFindBest());
				assertFalse(trackerSplit.isStopped());
				
				// use original and make sure split didn't effect
				pair = restarter.reoptimize(re);
				assertNotNull(pair);
				assertTrue(pair.getCost()>1);
				assertEquals(2*re*r, restarter.getTotalRunLength());
				assertEquals(0, heur.optCounter);
				assertEquals(2*re, heur.reoptCounter);
				assertFalse(tracker.didFindBest());
				assertFalse(tracker.isStopped());
			}
		}
	}
	
	
	private static class TestRestartedMetaheuristic implements ReoptimizableMetaheuristic<TestObject> {
		
		private ProgressTracker<TestObject> tracker;
		private int elapsed;
		private final int stopAtEval;
		private final int findBestAtEval;
		private final int which; // 0 for both at same time, 1 for stop, 2 for best
		int optCounter;
		int reoptCounter;
		private final Random rand = new Random(42);
		
		public TestRestartedMetaheuristic() {
			tracker = new ProgressTracker<TestObject>();
			elapsed = 0;
			stopAtEval = findBestAtEval = Integer.MAX_VALUE;
			which = 0;
		}
		
		public TestRestartedMetaheuristic(int stopAtEval, int findBestAtEval) {
			tracker = new ProgressTracker<TestObject>();
			elapsed = 0;
			this.stopAtEval = stopAtEval;
			this.findBestAtEval = findBestAtEval;
			if (stopAtEval < findBestAtEval) which = 1;
			else if (stopAtEval > findBestAtEval) which = 2;
			else which = 0;
		}
		
		@Override
		public TestRestartedMetaheuristic split() {
			return new TestRestartedMetaheuristic(stopAtEval, findBestAtEval);
		}
		
		@Override
		public ProgressTracker<TestObject> getProgressTracker() {
			return tracker;
		}
		
		@Override
		public void setProgressTracker(ProgressTracker<TestObject> tracker) {
			if (tracker != null) this.tracker = tracker;
		}
		
		@Override
		public OptimizationProblem<TestObject> getProblem() {
			// not used by tests.
			return null;
		}
		
		@Override
		public long getTotalRunLength() {
			return elapsed;
		}
		
		@Override
		public SolutionCostPair<TestObject> optimize(int runLength) {
			optCounter++;
			int c = update(runLength);
			return new SolutionCostPair<TestObject>(new TestObject(), c);
		}
		
		@Override
		public SolutionCostPair<TestObject> reoptimize(int runLength) {	
			reoptCounter++;
			int c = update(runLength);
			return new SolutionCostPair<TestObject>(new TestObject(), c);
		}
		
		private int update(int runLength) {
			elapsed += runLength;
			int c = rand.nextInt(18) + 2;
			switch (which) {
				case 0: 
					if (elapsed >= stopAtEval) {
						elapsed = stopAtEval;
						tracker.stop();
						tracker.setFoundBest();
						c = 1;
					}
					break;
				case 1:
					if (elapsed >= stopAtEval) {
						elapsed = stopAtEval;
						tracker.stop();
					}
					break;
				case 2:
					if (elapsed >= findBestAtEval) {
						elapsed = findBestAtEval;
						tracker.setFoundBest();
						c = 1;
					}
					break;
			}
			return c;
		}
	}
	
	private static class TestObject implements Copyable<TestObject> {
		
		public TestObject() {}
		
		@Override
		public TestObject copy() {
			return new TestObject();
		}
	}
}