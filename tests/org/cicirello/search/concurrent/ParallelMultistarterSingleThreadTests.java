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
 
package org.cicirello.search.concurrent;

import org.junit.*;
import static org.junit.Assert.*;
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
 * JUnit 4 tests for ParallelMultistarter using a single thread.
 */
public class ParallelMultistarterSingleThreadTests {
	
	@Test
	public void testOptimizeExceptions() {
		TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
		ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
		restarter.close();
		IllegalStateException thrown = assertThrows( 
			IllegalStateException.class,
			() -> restarter.optimize(1)
		);
	}
	
	@Test
	public void testOptimizeStoppedFoundBest() {
		TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
		ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
		long expected = restarter.getTotalRunLength();
		restarter.getProgressTracker().stop();
		restarter.optimize(1);
		assertEquals(expected, restarter.getTotalRunLength());
		restarter.close();
		heur = new TestRestartedMetaheuristic();
		restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
		expected = restarter.getTotalRunLength();
		restarter.getProgressTracker().setFoundBest();
		restarter.optimize(1);
		assertEquals(expected, restarter.getTotalRunLength());
		restarter.close();
	}
	
	@Test
	public void testSetProgressTrackerNull() {
		TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
		ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
		ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
		restarter.setProgressTracker(null);
		assertEquals(tracker, restarter.getProgressTracker());
		restarter.close();
	}
	
	@Test
	public void testConstantLength_Constructor1() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, r, 1);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
		TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new ParallelMultistarter<TestObject>(heur, 0, 1)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new ParallelMultistarter<TestObject>(heur, 1, 0)
		);
	}
	
	@Test
	public void testConstantLength_Split() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ParallelMultistarter<TestObject> restarter1 = new ParallelMultistarter<TestObject>(heur, r, 1);
				ParallelMultistarter<TestObject> restarter = restarter1.split();
				verifyConstantLengthSplit(restarter, heur, r, re);
				restarter1.close();
				restarter.close();
				assertTrue(restarter != restarter1);
			}
		}
		TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
		ParallelMultistarter<TestObject> restarter1 = new ParallelMultistarter<TestObject>(heur, 1, 1);
		restarter1.close();
		ParallelMultistarter<TestObject> restarter = restarter1.split();
		IllegalStateException thrown = assertThrows( 
			IllegalStateException.class,
			() -> restarter.optimize(1)
		);
		restarter.close();
	}
	
	@Test
	public void testSetProgressTracker() {
		TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
		ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
		ProgressTracker<TestObject> tracker = new ProgressTracker<TestObject>();
		restarter.setProgressTracker(tracker);
		restarter.close();
		assertTrue(tracker == restarter.getProgressTracker());
		assertTrue(tracker == heur.getProgressTracker());
	}
	
	@Test
	public void testGetProblem() {
		TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
		ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, 1, 1);
		restarter.close();
		assertTrue(heur.getProblem() == restarter.getProblem());
	}
	
	@Test
	public void testConstantLength_Constructor2() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 1);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
		TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(1), 0)
		);
	}
	
	@Test
	public void testConstantLength_Constructor3() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
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
				ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
				schedules.add(new ConstantRestartSchedule(r));				
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, schedules);
				verifyConstantLength(restarter, (TestRestartedMetaheuristic)heurs.get(0), r, re);
				restarter.close();
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic());
				ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
				schedules.add(new ConstantRestartSchedule(100));
				schedules.add(new ConstantRestartSchedule(200));
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, schedules);
			}
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				TestProblem problem = new TestProblem();
				TestRestartedMetaheuristic h = new TestRestartedMetaheuristic(problem);
				h.setProgressTracker(new ProgressTracker<TestObject>());
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic(problem));
				heurs.add(h);
				ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
				schedules.add(new ConstantRestartSchedule(100));
				schedules.add(new ConstantRestartSchedule(200));
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, schedules);
			}
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic());
				heurs.add(new TestRestartedMetaheuristic(new TestProblem()));
				ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
				schedules.add(new ConstantRestartSchedule(100));
				schedules.add(new ConstantRestartSchedule(200));
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, schedules);
			}
		);
	}
	
	@Test
	public void testConstantLength_Constructor5() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic());
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, r);
				verifyConstantLength(restarter, (TestRestartedMetaheuristic)heurs.get(0), r, re);
				restarter.close();
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic());
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, 0);
			}
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				TestProblem problem = new TestProblem();
				TestRestartedMetaheuristic h = new TestRestartedMetaheuristic(problem);
				h.setProgressTracker(new ProgressTracker<TestObject>());
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic(problem));
				heurs.add(h);
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, 1);
			}
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				ArrayList<Metaheuristic<TestObject>> heurs = new ArrayList<Metaheuristic<TestObject>>();
				heurs.add(new TestRestartedMetaheuristic());
				heurs.add(new TestRestartedMetaheuristic(new TestProblem()));
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs, 1);
			}
		);
	}
	
	@Test
	public void testConstantLength_Constructor6() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, r);
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(multiStarter, 1);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				Multistarter<TestObject> multiStarter = new Multistarter<TestObject>(heur, 1);
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(multiStarter, 0);
			}
		);
	}
	
	@Test
	public void testConstantLength_Constructor7() {
		for (int r = 1; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				heurs.add(new Multistarter<TestObject>(heur, r));
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
				verifyConstantLength(restarter, heur, r, re);
				restarter.close();
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
				heurs.add(new Multistarter<TestObject>(heur, 1));
				heurs.add(new Multistarter<TestObject>(new TestRestartedMetaheuristic(new TestProblem()), 1));
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
			}
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> {
				ArrayList<Multistarter<TestObject>> heurs = new ArrayList<Multistarter<TestObject>>();
				TestProblem p = new TestProblem();
				TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(p);
				heur.setProgressTracker(new ProgressTracker<TestObject>());
				heurs.add(new Multistarter<TestObject>(heur, 1));
				heurs.add(new Multistarter<TestObject>(new TestRestartedMetaheuristic(p), 1));
				ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heurs);
			}
		);
	}
	
	@Test
	public void testStopped_Constructor1() {
		for (int r = 10; r <= 1000; r *= 10) {
			for (int re = 1; re <= 5; re++) {
				int max = re * r;
				for (int early = r-5, i=1; early < max; early += r, i++) {
					TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early+1);
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, r, 1);
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
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 1);
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
					ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
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
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(multiStarter, 1);
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
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, r, 1);
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
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(heur, new ConstantRestartSchedule(r), 1);
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
					ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
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
					ParallelMultistarter<TestObject> restarter = new ParallelMultistarter<TestObject>(multiStarter, 1);
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
		assertEquals(re*r, restarter.getTotalRunLength());
		assertEquals(re, heur.optCounter);
		assertEquals(0, heur.reoptCounter);
		assertFalse(tracker.didFindBest());
		assertFalse(tracker.isStopped());
	}
	
	private void verifyConstantLengthSplit(ParallelMultistarter<TestObject> restarter, TestRestartedMetaheuristic heur, int r, int re) {
		ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
		assertNotNull(tracker);
		assertEquals(0, restarter.getTotalRunLength());
		assertFalse(tracker.didFindBest());
		assertFalse(tracker.isStopped());
		SolutionCostPair<TestObject> pair = restarter.optimize(re);
		assertNotNull(pair);
		assertTrue(pair.getCost()>1);
		assertEquals(re*r, restarter.getTotalRunLength());
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
		assertEquals(early, restarter.getTotalRunLength());
		assertEquals(i, heur.optCounter);
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
		assertEquals(early, restarter.getTotalRunLength());
		assertEquals(i, heur.optCounter);
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
		private final OptimizationProblem<TestObject> problem;
		
		public TestRestartedMetaheuristic() {
			tracker = new ProgressTracker<TestObject>();
			elapsed = 0;
			stopAtEval = findBestAtEval = Integer.MAX_VALUE;
			which = 0;
			rand = new SplittableRandom(42);
			problem = new TestProblem();
		}
		
		public TestRestartedMetaheuristic(TestProblem p) {
			tracker = new ProgressTracker<TestObject>();
			elapsed = 0;
			stopAtEval = findBestAtEval = Integer.MAX_VALUE;
			which = 0;
			rand = new SplittableRandom(42);
			problem = p;
		}
		
		public TestRestartedMetaheuristic(int stopAtEval, int findBestAtEval) {
			this(stopAtEval, findBestAtEval, new SplittableRandom(42));
		}
		
		public TestRestartedMetaheuristic(int stopAtEval, int findBestAtEval, SplittableRandom rand) {
			tracker = new ProgressTracker<TestObject>();
			elapsed = 0;
			this.stopAtEval = stopAtEval;
			this.findBestAtEval = findBestAtEval;
			if (stopAtEval < findBestAtEval) which = 1;
			else if (stopAtEval > findBestAtEval) which = 2;
			else which = 0;
			this.rand = rand;
			problem = new TestProblem();
		}
		
		@Override
		public TestRestartedMetaheuristic split() {
			return new TestRestartedMetaheuristic(stopAtEval, findBestAtEval, rand.split());
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
	
	private static class TestProblem implements OptimizationProblem<TestObject> {
		public double cost(TestObject o) { return 5; }
		public boolean isMinCost(double c) { return false; }
		public double minCost() { return -10000; }
		public double value(TestObject o) { return 5; }
	}
}