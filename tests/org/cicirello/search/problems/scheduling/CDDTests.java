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
import java.io.StringWriter;
import java.io.StringReader;
import java.io.PrintWriter;
import java.util.Scanner;


/**
 * JUnit tests for CommoDuedateScheduling.
 */
public class CDDTests {
	
	@Test
	public void testReadWriteInstanceData() {
		double[] h = { 0.0, 0.25, 0.5, 0.75, 1.0 };
		for (int n = 1; n <= 5; n++) {
			for (int i = 0; i < h.length; i++) {
				CommonDuedateScheduling s = new CommonDuedateScheduling(n, h[i], 42);
				StringWriter sOut = new StringWriter();
				PrintWriter out = new PrintWriter(sOut);
				s.toFile(out);
				CommonDuedateScheduling s2 = new CommonDuedateScheduling(new StringReader(sOut.toString()), 0, h[i]);
				assertEquals(s.numberOfJobs(), s2.numberOfJobs());
				int duedate = s.getDueDate(0);
				for (int job = 0; job < n; job++) {
					assertEquals(s.getProcessingTime(job), s2.getProcessingTime(job));
					assertEquals(s.getEarlyWeight(job), s2.getEarlyWeight(job));
					assertEquals(s.getWeight(job), s2.getWeight(job));
					assertEquals(s.getDueDate(job), s2.getDueDate(job));
					assertEquals(duedate, s2.getDueDate(job));
				}
			}
		}
	}
	
	@Test
	public void testCorrectNumJobs() {
		double[] h = { 0.0, 0.25, 0.5, 0.75, 1.0 };
		for (int n = 1; n <= 5; n++) {
			for (int i = 0; i < h.length; i++) {
				CommonDuedateScheduling s = new CommonDuedateScheduling(n, h[i], 42);
				assertEquals(n, s.numberOfJobs());
				assertTrue(s.hasDueDates());
				assertTrue(s.hasWeights());
				assertTrue(s.hasEarlyWeights());
				assertFalse(s.hasSetupTimes());
				assertFalse(s.hasReleaseDates());
			}
		}
	}
	
	@Test
	public void testConsistencyWithParameters() {
		double[] h = { 0.0, 0.25, 0.5, 0.75, 1.0 };
		int[] m = {0, 1, 1, 3, 1};
		int[] d = {1, 4, 2, 4, 1};
		for (int n = 1; n <= 10; n++) {
			for (int i = 0; i < h.length; i++) {
				CommonDuedateScheduling s = new CommonDuedateScheduling(n, h[i], 42);
				int totalP = 0;
				for (int x = 0; x < n; x++) {
					assertTrue(s.getProcessingTime(x) >= CommonDuedateScheduling.MIN_PROCESS_TIME
						&& s.getProcessingTime(x) <= CommonDuedateScheduling.MAX_PROCESS_TIME);
					assertTrue(s.getWeight(x) >= CommonDuedateScheduling.MIN_TARDINESS_WEIGHT
						&& s.getWeight(x) <= CommonDuedateScheduling.MAX_TARDINESS_WEIGHT);
					assertTrue(s.getEarlyWeight(x) >= CommonDuedateScheduling.MIN_EARLINESS_WEIGHT
						&& s.getEarlyWeight(x) <= CommonDuedateScheduling.MAX_EARLINESS_WEIGHT);
					totalP += s.getProcessingTime(x);
				}
				int expectedDuedate = totalP * m[i] / d[i];
				for (int x = 0; x < n; x++) {
					assertEquals(expectedDuedate, s.getDueDate(x));
				}
			}
		}
	}
	
}