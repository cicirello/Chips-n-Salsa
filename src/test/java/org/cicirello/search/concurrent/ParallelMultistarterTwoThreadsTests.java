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
 
package org.cicirello.search.concurrent;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.search.Metaheuristic;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.restarts.ConstantRestartSchedule;
import org.cicirello.search.restarts.RestartSchedule;
import org.cicirello.search.restarts.Multistarter;
import org.cicirello.util.Copyable;
import java.util.ArrayList;
import java.util.SplittableRandom;

/**
 * JUnit tests for ParallelMultistarter using 2 threads.
 */
public class ParallelMultistarterTwoThreadsTests {
	
	@Test
	public void testConstantLength_Constructor1() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, r, 2);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
	}
	
	@Test
	public void testConstantLength_Constructor2() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 2);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
	}
	
	@Test
	public void testConstantLength_Constructor3() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
				schedules.add(new ConstantRestartSchedule(r));
				schedules.add(new ConstantRestartSchedule(r));				
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, schedules);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
	}
	
	@Test
	public void testConstantLength_Constructor4() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic());
				heurs.add(new TestRestartedMetaheuristic());
				heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
				ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
				schedules.add(new ConstantRestartSchedule(r));
				schedules.add(new ConstantRestartSchedule(r));				
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, schedules);
				verifyConstantLength(restarter, (TestRestartedMetaheuristic)heurs.get(0), r, re);
				restarter.close();
			}
		}
	}
	
	@Test
	public void testConstantLength_Constructor5() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic());
				heurs.add(new TestRestartedMetaheuristic());
				heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, r);
				verifyConstantLength(restarter, (TestRestartedMetaheuristic)heurs.get(0), r, re);
				restarter.close();
			}
		}
	}
	
	@Test
	public void testConstantLength_Constructor6() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, r);
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(multiStarter, 2);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
	}
	
	@Test
	public void testConstantLength_Constructor7() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				heurs.add(new Multistarter<TestObject>(heur, r));
				TestRestartedMetaheuristic heur2 = new TestRestartedMetaheuristic();
				heur2.setProgressTracker(heur.getProgressTracker());
				heurs.add(new Multistarter<TestObject>(heur2, r));
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
	}
	
	@Test
	public void testStopped_Constructor1() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, r, 2);
					verifyConstantLengthStopped(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testStopped_Constructor2() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 2);
					verifyConstantLengthStopped(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testStopped_Constructor3() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
					schedules.add(new ConstantRestartSchedule(r));
					schedules.add(new ConstantRestartSchedule(r));					
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, schedules);
					verifyConstantLengthStopped(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testStopped_Constructor4() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
					heurs.add(new TestRestartedMetaheuristic(early, early+1));
					heurs.add(new TestRestartedMetaheuristic(early, early+1));
					heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
					ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
					schedules.add(new ConstantRestartSchedule(r));
					schedules.add(new ConstantRestartSchedule(r));					
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, schedules);
					verifyConstantLengthStopped(restarter, (TestRestartedMetaheuristic)heurs.get(0), r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testStopped_Constructor5() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
					heurs.add(new TestRestartedMetaheuristic(early, early+1));
					heurs.add(new TestRestartedMetaheuristic(early, early+1));
					heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, r);
					verifyConstantLengthStopped(restarter, (TestRestartedMetaheuristic)heurs.get(0), r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testStopped_Constructor6() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, r);
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(multiStarter, 2);
					verifyConstantLengthStopped(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testStopped_Constructor7() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					heurs.add(new Multistarter<TestObject>(heur, r));
					TestRestartedMetaheuristic heur2 = new TestRestartedMetaheuristic(early, early+1);
					heur2.setProgressTracker(heur.getProgressTracker());
					heurs.add(new Multistarter<TestObject>(heur2, r));
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
					verifyConstantLengthStopped(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testBest_Constructor1() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, r, 2);
					verifyConstantLengthBest(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testBest_Constructor2() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 2);
					verifyConstantLengthBest(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testBest_Constructor3() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
					schedules.add(new ConstantRestartSchedule(r));
					schedules.add(new ConstantRestartSchedule(r));
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, schedules);
					verifyConstantLengthBest(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testBest_Constructor4() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
					heurs.add(new TestRestartedMetaheuristic(early+1, early));
					heurs.add(new TestRestartedMetaheuristic(early+1, early));
					heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
					ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
					schedules.add(new ConstantRestartSchedule(r));
					schedules.add(new ConstantRestartSchedule(r));					
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, schedules);
					verifyConstantLengthBest(restarter, (TestRestartedMetaheuristic)heurs.get(0), r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testBest_Constructor5() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
					heurs.add(new TestRestartedMetaheuristic(early+1, early));
					heurs.add(new TestRestartedMetaheuristic(early+1, early));
					heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, r);
					verifyConstantLengthBest(restarter, (TestRestartedMetaheuristic)heurs.get(0), r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testBest_Constructor6() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, r);
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(multiStarter, 2);
					verifyConstantLengthBest(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	@Test
	public void testBest_Constructor7() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early+1, early);
					heurs.add(new Multistarter<TestObject>(heur, r));
					TestRestartedMetaheuristic heur2 = new TestRestartedMetaheuristic(early+1, early);
					heurs.add(new Multistarter<TestObject>(heur2, r));
					heur2.setProgressTracker(heur.getProgressTracker());
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
					verifyConstantLengthBest(restarter, heur, r, re, early, i);
					restarter.close();
				}
			}
		}
	}
	
	
	
	
	
	
	private void verifyConstantLength(ParallelMultistarter<TestObject> restarter, TestRestartedMetaheuristic heur, int r, int re) {
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
		assertEquals(2*re*r, restarter.getTotalRunLength());
		assertEquals(re, heur.optCounter);
		assertEquals(0, heur.reoptCounter);
		assertFalse(tracker.didFindBest());
		assertFalse(tracker.isStopped());
	}
	
	private void verifyConstantLengthStopped(ParallelMultistarter<TestObject> restarter, TestRestartedMetaheuristic heur, int r, int re, int early, int i) {
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
		assertTrue(2*early >= restarter.getTotalRunLength() && restarter.getTotalRunLength() >= early, "total run length");
		assertTrue(i >= heur.optCounter, "num calls to optimize");
		assertEquals(0, heur.reoptCounter);
		assertFalse(tracker.didFindBest());
		assertTrue(tracker.isStopped());
	}
	
	private void verifyConstantLengthBest(ParallelMultistarter<TestObject> restarter, TestRestartedMetaheuristic heur, int r, int re, int early, int i) {
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
		assertTrue(2*early >= restarter.getTotalRunLength() && restarter.getTotalRunLength() >= early, "total run length");
		assertTrue(i >= heur.optCounter, "num calls to optimize");
		assertEquals(0, heur.reoptCounter);
		assertTrue(tracker.didFindBest());
		assertFalse(tracker.isStopped());	
	}
	
	private static class TestRestartedMetaheuristic implements ReoptimizableMetaheuristic<TestObject> {
		
		private ProgressTracker<TestObject> tracker;
		private int elapsed;
		private final int stopAtEval;
		private final int findBestAtEval;
		private final int which; // 0 for both at same time, 1 for stop, 2 for best
		int optCounter;
		int reoptCounter;
		private final SplittableRandom rand;
		private static OptimizationProblem<TestObject> problem = new TestProblem();
		
		public TestRestartedMetaheuristic() {
			tracker = new ProgressTracker<TestObject>();
			elapsed = 0;
			stopAtEval = findBestAtEval = Integer.MAX_VALUE;
			which = 0;
			rand = new SplittableRandom(42);
			optCounter = reoptCounter = 0;
		}
		
		public TestRestartedMetaheuristic(int stopAtEval, int findBestAtEval) {
			this(stopAtEval, findBestAtEval, new SplittableRandom(42), new ProgressTracker<TestObject>());
		}
		
		public TestRestartedMetaheuristic(int stopAtEval, int findBestAtEval, SplittableRandom rand, ProgressTracker<TestObject> tracker) {
			this.tracker = tracker;
			elapsed = 0;
			this.stopAtEval = stopAtEval;
			this.findBestAtEval = findBestAtEval;
			if (stopAtEval < findBestAtEval) which = 1;
			else if (stopAtEval > findBestAtEval) which = 2;
			else which = 0;
			this.rand = rand;
			optCounter = reoptCounter = 0;
		}
		
		@Override
		public TestRestartedMetaheuristic split() {
			return new TestRestartedMetaheuristic(stopAtEval, findBestAtEval, rand.split(), tracker);
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
			return problem;
		}
		
		@Override
		public long getTotalRunLength() {
			return elapsed;
		}
		
		@Override
		public SolutionCostPair<TestObject> optimize(int runLength) {
			optCounter++;
			int c = update(runLength);
			return new SolutionCostPair<TestObject>(new TestObject(), c, false);
		}
		
		@Override
		public SolutionCostPair<TestObject> reoptimize(int runLength) {	
			reoptCounter++;
			int c = update(runLength);
			return new SolutionCostPair<TestObject>(new TestObject(), c, false);
		}
		
		private int update(int runLength) {
			elapsed += runLength;
			int c = rand.nextInt(18) + 2;
			switch (which) {
				case 0: 
					if (elapsed >= stopAtEval) {
						elapsed = stopAtEval;
						tracker.stop();
						// Replaces old call to deprecated setFoundBest()
						tracker.update(1, new TestObject(), true);
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
						// Replaces old call to deprecated setFoundBest()
						tracker.update(1, new TestObject(), true);
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
	
	private static class TestProblem implements OptimizationProblem<TestObject> {
		public double cost(TestObject o) { return 5; }
		public boolean isMinCost(double c) { return false; }
		public double minCost() { return -10000; }
		public double value(TestObject o) { return 5; }
	}
}