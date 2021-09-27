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
 
package org.cicirello.search.sa;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * JUnit 4 test cases for the AcceptanceTracker.
 */
public class AcceptanceTrackerTests {
	
	@Test
	public void testAcceptanceTracker() {
		TestAnnealingSchedule schedule = new TestAnnealingSchedule();
		AcceptanceTracker tracker = new AcceptanceTracker(schedule);
		
		int N = 10;
		for (int runs = 1; runs <= N; runs++) {
			tracker.init(N);
			for (int i = 0; i < N; i++) {
				int value = i < runs ? 90 : 110;
				tracker.accept(value, 100);
			}
		}
		tracker.accept(100, 100);
		for (int i = 0; i < N; i++) {
			assertEquals(1.0 - 0.1*i, tracker.getAcceptanceRate(i), 1e-10);
		}
		
		AcceptanceTracker trackerSplit = null; 
		int M = 10;
		N = 20;
		for (int runs = 1; runs <= N; runs++) {
			tracker.init(N);
			if (runs == 3) {
				trackerSplit = tracker.split();
				for (int r2 = 1; r2 <= M; r2++) {
					trackerSplit.init(M);
					for (int i = 0; i < M; i++) {
						trackerSplit.accept(i < r2 ? 90 : 110, 100);
					}
				}
			}
			for (int i = 0; i < N; i++) {
				int value = i < runs ? 90 : 110;
				tracker.accept(value, 100);
			}
		}
		tracker.accept(100, 100);
		for (int i = 0; i < N; i++) {
			assertEquals(1.0 - 0.05*i, tracker.getAcceptanceRate(i), 1e-10);
		}
		
		for (int i = 0; i < M; i++) {
			assertEquals(1.0 - 0.1*i, trackerSplit.getAcceptanceRate(i), 1e-10);
		}
		
		trackerSplit.reset(M);
		for (int r2 = 1; r2 <= M; r2++) {
			trackerSplit.init(M);
			for (int i = 0; i < M; i++) {
				trackerSplit.accept(i < r2 ? 110 : 90, 100);
			}
		}
		for (int i = 0; i < M; i++) {
			assertEquals(0.1*i, trackerSplit.getAcceptanceRate(i), 1e-10);
		}
		
		assertEquals(30, schedule.initCount);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> tracker.reset(0)
		);
	}
	
	private static class TestAnnealingSchedule implements AnnealingSchedule {
		
		int initCount;
		
		public void init(int maxEvals) {
			initCount++;
		}
		
		public boolean accept(double neighborCost, double currentCost) {
			return neighborCost <= currentCost;
		}
		
		public TestAnnealingSchedule split() {
			return new TestAnnealingSchedule();
		}
	}
}