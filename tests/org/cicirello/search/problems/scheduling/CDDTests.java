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
import java.io.FileNotFoundException;
import java.io.File;

/**
 * JUnit tests for CommoDuedateScheduling.
 */
public class CDDTests {
	
	@BeforeClass 
	public static void createOutputDirectory() {
		File directory = new File("target/testcasedata");
		if (!directory.exists()){
			directory.mkdir();
		}
    }
	
	@Test
	public void testConstructorExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(-1, 0.5)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(1, -0.00001)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(1, 1.00001)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(-1, 0.5, 42)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(1, -0.00001, 42)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(1, 1.00001, 42)
		);
		String contents = "2\n3\n1\t2\t3\n1\t2\t3\n1\t2\t3\n4\n1\t1\t1\n2\t2\t2\n3\t3\t3\n4\t4\t4\n";
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(new StringReader(contents), 1, -0.000001)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(new StringReader(contents), 1, 1.000001)
		);
	}
	
	@Test
	public void testReadSkippingInstance() {
		String contents = "2\n3\n1\t2\t3\n1\t2\t3\n1\t2\t3\n4\n1\t1\t1\n2\t2\t2\n3\t3\t3\n4\t4\t4\n";
		CommonDuedateScheduling s = new CommonDuedateScheduling(new StringReader(contents), 1, 0.5);
		assertEquals(4, s.numberOfJobs());
		int duedate = s.getDueDate(0);
		for (int job = 0; job < 4; job++) {
			assertEquals(job+1, s.getProcessingTime(job));
			assertEquals(job+1, s.getEarlyWeight(job));
			assertEquals(job+1, s.getWeight(job));
			assertEquals(duedate, s.getDueDate(job));
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(new StringReader(contents), -1, 0.5)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new CommonDuedateScheduling(new StringReader(contents), 2, 0.5)
		);
	}
	
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
	public void testReadWriteToFile() {
		String contents = "1\n3\n1\t2\t3\n4\t5\t6\n7\t8\t9\n";
		CommonDuedateScheduling original = new CommonDuedateScheduling(new StringReader(contents), 0, 0.5);
		try {
			String file = "target/testcasedata/cdd.testcase.data";
			original.toFile(file);
			CommonDuedateScheduling s = new CommonDuedateScheduling(file, 0, 0.5);
			assertEquals(3, s.numberOfJobs());
			int duedate = s.getDueDate(0);
			assertEquals(6, duedate);
			for (int job = 0; job < 3; job++) {
				assertEquals(3*job+1, s.getProcessingTime(job));
				assertEquals(3*job+2, s.getEarlyWeight(job));
				assertEquals(3*job+3, s.getWeight(job));
				assertEquals(duedate, s.getDueDate(job));
			}
		} catch(FileNotFoundException ex) {
			fail("File reading/writing caused exception: " + ex);
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
				CommonDuedateScheduling s2 = new CommonDuedateScheduling(n, h[i]);
				assertEquals(n, s2.numberOfJobs());
				assertTrue(s2.hasDueDates());
				assertTrue(s2.hasWeights());
				assertTrue(s2.hasEarlyWeights());
				assertFalse(s2.hasSetupTimes());
				assertFalse(s2.hasReleaseDates());
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
	
	@Test
	public void testCompletionTimeCalculationWithH0() {
		double h = 0.0;
		for (int n = 1; n <= 10; n++) {
			final CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
			int[] perm1 = new int[n];
			int[] perm2 = new int[n];
			for (int i = 0; i < n; i++) {
				perm1[i] = i;
				perm2[n-1-i] = i;
			}
			Permutation p1 = new Permutation(perm1);
			Permutation p2 = new Permutation(perm2);
			int[] c1 = s.getCompletionTimes(p1);
			int expected = 0;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p1.get(x));
				assertEquals("forward", expected, c1[p1.get(x)]);
			}
			int[] c2 = s.getCompletionTimes(p2);
			expected = 0;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p2.get(x));
				assertEquals("backward", expected, c2[p2.get(x)]);
			}
			final int nPlus = n + 1;
			IllegalArgumentException thrown = assertThrows( 
				IllegalArgumentException.class,
				() -> s.getCompletionTimes(new Permutation(nPlus))
			);
		}
	}
	
	@Test
	public void testCompletionTimeCalculationWithH1() {
		double h = 1.0;
		for (int n = 1; n <= 10; n++) {
			CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
			int[] perm1 = new int[n];
			int[] perm2 = new int[n];
			for (int i = 0; i < n; i++) {
				perm1[i] = i;
				perm2[n-1-i] = i;
			}
			Permutation p1 = new Permutation(perm1);
			Permutation p2 = new Permutation(perm2);
			int[] c1 = s.getCompletionTimes(p1);
			int duedate = s.getDueDate(0);
			int earlySum = 0;
			int tardySum = 0;
			int onTimeJob = -1;
			for (int x = 0; x < n; x++) {
				if (c1[x] < duedate) earlySum += s.getEarlyWeight(x);
				else if (c1[x] > duedate) tardySum += s.getWeight(x);
				else onTimeJob = x;
			}
			String message = "Forward: earlySum,tardySum="+earlySum+","+tardySum;
			int notEarlySumOfTardy = tardySum;
			if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
			assertTrue(message, earlySum <= notEarlySumOfTardy);
			assertTrue(onTimeJob >= 0);
			if (onTimeJob >= 0) {
				assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
			} else {
				assertEquals(0, c1[p1.get(0)] - s.getProcessingTime(p1.get(0)));
				assertTrue(earlySum <= tardySum); 
			}
			int delay = c1[p1.get(0)] - s.getProcessingTime(p1.get(0));
			int expected = delay;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p1.get(x));
				assertEquals("forward", expected, c1[p1.get(x)]);
			}
			int[] c2 = s.getCompletionTimes(p2);
			earlySum = 0;
			tardySum = 0;
			onTimeJob = -1;
			for (int x = 0; x < n; x++) {
				if (c2[x] < duedate) earlySum += s.getEarlyWeight(x);
				else if (c2[x] > duedate) tardySum += s.getWeight(x);
				else onTimeJob = x;
			}
			message = "Backward: earlySum,tardySum,n="+earlySum+","+tardySum+","+n;
			notEarlySumOfTardy = tardySum;
			if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
			assertTrue(message, earlySum <= notEarlySumOfTardy);
			assertTrue(onTimeJob >= 0);
			if (onTimeJob >= 0) {
				assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
			} else {
				assertEquals(0, c2[p2.get(0)] - s.getProcessingTime(p2.get(0)));
				assertTrue(earlySum <= tardySum);
			}
			delay = c2[p2.get(0)] - s.getProcessingTime(p2.get(0));
			expected = delay;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p2.get(x));
				assertEquals("backward", expected, c2[p2.get(x)]);
			}
		}
	}
	
	@Test
	public void testCompletionTimeCalculationWithH05() {
		double h = 0.5;
		for (int n = 1; n <= 10; n++) {
			CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
			int[] perm1 = new int[n];
			int[] perm2 = new int[n];
			for (int i = 0; i < n; i++) {
				perm1[i] = i;
				perm2[n-1-i] = i;
			}
			Permutation p1 = new Permutation(perm1);
			Permutation p2 = new Permutation(perm2);
			int[] c1 = s.getCompletionTimes(p1);
			int duedate = s.getDueDate(0);
			int earlySum = 0;
			int tardySum = 0;
			int onTimeJob = -1;
			for (int x = 0; x < n; x++) {
				if (c1[x] < duedate) earlySum += s.getEarlyWeight(x);
				else if (c1[x] > duedate) tardySum += s.getWeight(x);
				else onTimeJob = x;
			}
			String message = "Forward: earlySum,tardySum="+earlySum+","+tardySum;
			int notEarlySumOfTardy = tardySum;
			if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
			assertTrue(message, earlySum <= notEarlySumOfTardy);
			int delay = c1[p1.get(0)] - s.getProcessingTime(p1.get(0));
			if (onTimeJob >= 0 && delay > 0) {
				assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
			} else {
				assertTrue("case with no ontime jobs", delay==0 && earlySum <= tardySum); 
			}
			int expected = delay;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p1.get(x));
				assertEquals("forward", expected, c1[p1.get(x)]);
			}
			int[] c2 = s.getCompletionTimes(p2);
			earlySum = 0;
			tardySum = 0;
			onTimeJob = -1;
			for (int x = 0; x < n; x++) {
				if (c2[x] < duedate) earlySum += s.getEarlyWeight(x);
				else if (c2[x] > duedate) tardySum += s.getWeight(x);
				else onTimeJob = x;
			}
			message = "Backward: earlySum,tardySum,n="+earlySum+","+tardySum+","+n;
			notEarlySumOfTardy = tardySum;
			if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
			assertTrue(message, earlySum <= notEarlySumOfTardy);
			delay = c2[p2.get(0)] - s.getProcessingTime(p2.get(0));
			if (onTimeJob >= 0 && delay > 0) {
				assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
			} else {
				assertTrue("case with no ontime jobs", delay==0 && earlySum <= tardySum); 
			}
			expected = delay;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p2.get(x));
				assertEquals("backward", expected, c2[p2.get(x)]);
			}
		}
	}
	
	@Test
	public void testCompletionTimeCalculationWithH025() {
		double h = 0.25;
		for (int n = 1; n <= 10; n++) {
			CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
			int[] perm1 = new int[n];
			int[] perm2 = new int[n];
			for (int i = 0; i < n; i++) {
				perm1[i] = i;
				perm2[n-1-i] = i;
			}
			Permutation p1 = new Permutation(perm1);
			Permutation p2 = new Permutation(perm2);
			int[] c1 = s.getCompletionTimes(p1);
			int duedate = s.getDueDate(0);
			int earlySum = 0;
			int tardySum = 0;
			int onTimeJob = -1;
			for (int x = 0; x < n; x++) {
				if (c1[x] < duedate) earlySum += s.getEarlyWeight(x);
				else if (c1[x] > duedate) tardySum += s.getWeight(x);
				else onTimeJob = x;
			}
			String message = "Forward: earlySum,tardySum="+earlySum+","+tardySum;
			int notEarlySumOfTardy = tardySum;
			if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
			assertTrue(message, earlySum <= notEarlySumOfTardy);
			int delay = c1[p1.get(0)] - s.getProcessingTime(p1.get(0));
			if (onTimeJob >= 0 && delay > 0) {
				assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
			} else {
				assertTrue("case with no ontime jobs", delay==0 && earlySum <= tardySum); 
			}
			int expected = delay;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p1.get(x));
				assertEquals("forward", expected, c1[p1.get(x)]);
			}
			int[] c2 = s.getCompletionTimes(p2);
			earlySum = 0;
			tardySum = 0;
			onTimeJob = -1;
			for (int x = 0; x < n; x++) {
				if (c2[x] < duedate) earlySum += s.getEarlyWeight(x);
				else if (c2[x] > duedate) tardySum += s.getWeight(x);
				else onTimeJob = x;
			}
			message = "Backward: earlySum,tardySum,n="+earlySum+","+tardySum+","+n;
			notEarlySumOfTardy = tardySum;
			if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
			assertTrue(message, earlySum <= notEarlySumOfTardy);
			delay = c2[p2.get(0)] - s.getProcessingTime(p2.get(0));
			if (onTimeJob >= 0 && delay > 0) {
				assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
			} else {
				assertTrue("case with no ontime jobs", delay==0 && earlySum <= tardySum); 
			}
			expected = delay;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p2.get(x));
				assertEquals("backward", expected, c2[p2.get(x)]);
			}
		}
	}
	
	@Test
	public void testCompletionTimeCalculationWithH075() {
		double h = 0.75;
		for (int n = 1; n <= 10; n++) {
			CommonDuedateScheduling s = new CommonDuedateScheduling(n, h, 42);
			int[] perm1 = new int[n];
			int[] perm2 = new int[n];
			for (int i = 0; i < n; i++) {
				perm1[i] = i;
				perm2[n-1-i] = i;
			}
			Permutation p1 = new Permutation(perm1);
			Permutation p2 = new Permutation(perm2);
			int[] c1 = s.getCompletionTimes(p1);
			int duedate = s.getDueDate(0);
			int earlySum = 0;
			int tardySum = 0;
			int onTimeJob = -1;
			for (int x = 0; x < n; x++) {
				if (c1[x] < duedate) earlySum += s.getEarlyWeight(x);
				else if (c1[x] > duedate) tardySum += s.getWeight(x);
				else onTimeJob = x;
			}
			String message = "Forward: earlySum,tardySum="+earlySum+","+tardySum;
			int notEarlySumOfTardy = tardySum;
			if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
			assertTrue(message, earlySum <= notEarlySumOfTardy);
			int delay = c1[p1.get(0)] - s.getProcessingTime(p1.get(0));
			if (onTimeJob >= 0 && delay > 0) {
				assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
			} else {
				assertTrue("case with no ontime jobs", delay==0 && earlySum <= tardySum); 
			}
			int expected = delay;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p1.get(x));
				assertEquals("forward", expected, c1[p1.get(x)]);
			}
			int[] c2 = s.getCompletionTimes(p2);
			earlySum = 0;
			tardySum = 0;
			onTimeJob = -1;
			for (int x = 0; x < n; x++) {
				if (c2[x] < duedate) earlySum += s.getEarlyWeight(x);
				else if (c2[x] > duedate) tardySum += s.getWeight(x);
				else onTimeJob = x;
			}
			message = "Backward: earlySum,tardySum,n="+earlySum+","+tardySum+","+n;
			notEarlySumOfTardy = tardySum;
			if (onTimeJob >= 0) notEarlySumOfTardy += s.getWeight(onTimeJob);
			assertTrue(message, earlySum <= notEarlySumOfTardy);
			delay = c2[p2.get(0)] - s.getProcessingTime(p2.get(0));
			if (onTimeJob >= 0 && delay > 0) {
				assertTrue(earlySum + s.getEarlyWeight(onTimeJob) >= tardySum);
			} else {
				assertTrue("case with no ontime jobs", delay==0 && earlySum <= tardySum); 
			}
			expected = delay;
			for (int x = 0; x < n; x++) {
				expected += s.getProcessingTime(p2.get(x));
				assertEquals("backward", expected, c2[p2.get(x)]);
			}
		}
	}
	
}