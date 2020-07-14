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
 * JUnit tests for the WeightedStaticSchedulingWithSetups class.
 */
public class WeightedStaticSetupsTests {
	
	@Test
	public void testCorrectNumJobs() {
		double[] tau = {0.0, 0.25, 0.5, 0.75, 1.0};
		double[] r = {0.0, 0.25, 0.5, 0.75, 1.0};
		double[] eta = {0.0, 0.25, 0.5, 0.75, 1.0};
		for (int n = 1; n <= 5; n++) {
			for (int i = 0; i < tau.length; i++) {
				for (int j = 0; j < r.length; j++) {
					for (int k = 0; k < eta.length; k++) {
						WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
						assertEquals(n, s.numberOfJobs());
						assertTrue(s.hasDueDates());
						assertTrue(s.hasWeights());
						assertTrue(s.hasSetupTimes());
						assertFalse(s.hasEarlyWeights());
						assertFalse(s.hasReleaseDates());
					}
				}
			}
		}
	}
	
	@Test
	public void testCompletionTimeCalculation() {
		int n = 5;
		double[] tau = {0.0, 0.25, 0.5, 0.75, 1.0};
		double[] r = {0.0, 0.25, 0.5, 0.75, 1.0};
		double[] eta = {0.0, 0.25, 0.5, 0.75, 1.0};
		for (int i = 0; i < tau.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < eta.length; k++) {
					WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
					Permutation p1 = new Permutation(new int[] {0, 1, 2, 3, 4});
					int[] c1 = s.getCompletionTimes(p1);
					int expected = 0;
					int last = n;
					for (int x = 0; x < n; x++) {
						expected += s.getProcessingTime(x) + s.getSetupTime(last,x);
						assertEquals(expected, c1[x]);
						last=x;
					}
					Permutation p2 = new Permutation(new int[] {4, 3, 2, 1, 0});
					int[] c2 = s.getCompletionTimes(p2);
					expected = 0;
					last = n;
					for (int x = n-1; x >= 0; x--) {
						expected += s.getProcessingTime(x) + s.getSetupTime(last,x);
						assertEquals(expected, c2[x]);
						last=x;
					}
				}
			}
		}
	}
	
	@Test
	public void testParamsNon0Non1() {
		int n = 10;
		double[] tau = {0.25, 0.5, 0.75};
		double[] r = {0.25, 0.5, 0.75};
		double[] eta = {0.25, 0.5, 0.75};
		for (int i = 0; i < tau.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < eta.length; k++) {
					WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
					assertEquals(n, s.numberOfJobs());
					assertTrue(s.hasDueDates());
					assertTrue(s.hasWeights());
					assertTrue(s.hasSetupTimes());
					assertFalse(s.hasEarlyWeights());
					assertFalse(s.hasReleaseDates());
					boolean diffP = false;
					boolean diffW = false;
					boolean diffS0 = false;
					boolean diffD = false;
					int pSum = 0;
					int sSum = 0;
					for (int x = 0; x < n; x++) {
						pSum += s.getProcessingTime(x);
						boolean diffS = false;
						for (int y = 0; y <= n; y++) {
							sSum += s.getSetupTime(y,x);
							if (y > 0 && !diffS && s.getSetupTime(y,x) != s.getSetupTime(y-1,x)) {
								diffS = true;
							}
						}
						assertTrue(diffS);
						if (x > 0 && !diffP && s.getProcessingTime(x) != s.getProcessingTime(x-1)) {
							diffP = true;
						}
						if (x > 0 && !diffW && s.getWeight(x) != s.getWeight(x-1)) {
							diffW = true;
						}
						if (x > 0 && !diffS0 && s.getSetupTime(x) != s.getSetupTime(x-1)) {
							diffS0 = true;
						}
						if (x > 0 && !diffD && s.getDueDate(x) != s.getDueDate(x-1)) {
							diffD = true;
						}
					}
					assertTrue(diffP);
					assertTrue(diffW);
					assertTrue(diffS0);
					assertTrue(diffD);
					for (int x = 0; x < n; x++) {
						assertTrue(s.getProcessingTime(x) >= WeightedStaticSchedulingWithSetups.MIN_PROCESS_TIME
							&& s.getProcessingTime(x) <= WeightedStaticSchedulingWithSetups.MAX_PROCESS_TIME);
						assertTrue(s.getWeight(x) >= WeightedStaticSchedulingWithSetups.MIN_WEIGHT
							&& s.getWeight(x) <= WeightedStaticSchedulingWithSetups.MAX_WEIGHT);
						assertTrue(s.getSetupTime(x) >= 0
							&& s.getSetupTime(x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						double d_min_loose = (1.0 - tau[i]) * (1.0 - r[j]) * pSum;
						double d_max_loose = (r[j] + (1.0 - tau[i]) * (1.0 - r[j])) * (pSum + sSum / (n+1.0));
						assertTrue(s.getDueDate(x) >= d_min_loose);
						assertTrue(s.getDueDate(x) <= d_max_loose);
						for (int y = 0; y <= n; y++) {
							assertTrue(s.getSetupTime(y,x) >= 0
							&& s.getSetupTime(y,x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						}
						assertEquals(0, s.getReleaseDate(x));
						assertEquals(1, s.getEarlyWeight(x));
					}
				}
			}
		}
	}
	
	@Test
	public void testEta0() {
		int n = 10;
		double[] tau = {0.25, 0.5, 0.75};
		double[] r = {0.25, 0.5, 0.75};
		double[] eta = {0.0};
		for (int i = 0; i < tau.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < eta.length; k++) {
					WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
					assertEquals(n, s.numberOfJobs());
					assertTrue(s.hasDueDates());
					assertTrue(s.hasWeights());
					assertTrue(s.hasSetupTimes());
					assertFalse(s.hasEarlyWeights());
					assertFalse(s.hasReleaseDates());
					boolean diffP = false;
					boolean diffW = false;
					boolean diffD = false;
					int pSum = 0;
					int sSum = 0;
					for (int x = 0; x < n; x++) {
						pSum += s.getProcessingTime(x);
						boolean diffS = false;
						for (int y = 0; y <= n; y++) {
							sSum += s.getSetupTime(y,x);
						}
						if (x > 0 && !diffP && s.getProcessingTime(x) != s.getProcessingTime(x-1)) {
							diffP = true;
						}
						if (x > 0 && !diffW && s.getWeight(x) != s.getWeight(x-1)) {
							diffW = true;
						}
						if (x > 0 && !diffD && s.getDueDate(x) != s.getDueDate(x-1)) {
							diffD = true;
						}
					}
					assertTrue(diffP);
					assertTrue(diffW);
					assertTrue(diffD);
					for (int x = 0; x < n; x++) {
						assertTrue(s.getProcessingTime(x) >= WeightedStaticSchedulingWithSetups.MIN_PROCESS_TIME
							&& s.getProcessingTime(x) <= WeightedStaticSchedulingWithSetups.MAX_PROCESS_TIME);
						assertTrue(s.getWeight(x) >= WeightedStaticSchedulingWithSetups.MIN_WEIGHT
							&& s.getWeight(x) <= WeightedStaticSchedulingWithSetups.MAX_WEIGHT);
						assertEquals(0, s.getSetupTime(x));
						double d_min_loose = (1.0 - tau[i]) * (1.0 - r[j]) * pSum;
						double d_max_loose = (r[j] + (1.0 - tau[i]) * (1.0 - r[j])) * (pSum + sSum / (n+1.0));
						assertTrue(s.getDueDate(x) >= d_min_loose);
						assertTrue(s.getDueDate(x) <= d_max_loose);
						for (int y = 0; y <= n; y++) {
							assertEquals(0, s.getSetupTime(y,x));
						}
						assertEquals(0, s.getReleaseDate(x));
						assertEquals(1, s.getEarlyWeight(x));
					}
				}
			}
		}
	}
	
	@Test
	public void testEta1() {
		int n = 10;
		double[] tau = {0.25, 0.5, 0.75};
		double[] r = {0.25, 0.5, 0.75};
		double[] eta = { 1.0 };
		for (int i = 0; i < tau.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < eta.length; k++) {
					WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
					assertEquals(n, s.numberOfJobs());
					assertTrue(s.hasDueDates());
					assertTrue(s.hasWeights());
					assertTrue(s.hasSetupTimes());
					assertFalse(s.hasEarlyWeights());
					assertFalse(s.hasReleaseDates());
					boolean diffP = false;
					boolean diffW = false;
					boolean diffS0 = false;
					boolean diffD = false;
					int pSum = 0;
					int sSum = 0;
					for (int x = 0; x < n; x++) {
						pSum += s.getProcessingTime(x);
						boolean diffS = false;
						for (int y = 0; y <= n; y++) {
							sSum += s.getSetupTime(y,x);
							if (y > 0 && !diffS && s.getSetupTime(y,x) != s.getSetupTime(y-1,x)) {
								diffS = true;
							}
						}
						assertTrue(diffS);
						if (x > 0 && !diffP && s.getProcessingTime(x) != s.getProcessingTime(x-1)) {
							diffP = true;
						}
						if (x > 0 && !diffW && s.getWeight(x) != s.getWeight(x-1)) {
							diffW = true;
						}
						if (x > 0 && !diffS0 && s.getSetupTime(x) != s.getSetupTime(x-1)) {
							diffS0 = true;
						}
						if (x > 0 && !diffD && s.getDueDate(x) != s.getDueDate(x-1)) {
							diffD = true;
						}
					}
					assertTrue(diffP);
					assertTrue(diffW);
					assertTrue(diffS0);
					assertTrue(diffD);
					for (int x = 0; x < n; x++) {
						assertTrue(s.getProcessingTime(x) >= WeightedStaticSchedulingWithSetups.MIN_PROCESS_TIME
							&& s.getProcessingTime(x) <= WeightedStaticSchedulingWithSetups.MAX_PROCESS_TIME);
						assertTrue(s.getWeight(x) >= WeightedStaticSchedulingWithSetups.MIN_WEIGHT
							&& s.getWeight(x) <= WeightedStaticSchedulingWithSetups.MAX_WEIGHT);
						assertTrue(s.getSetupTime(x) >= 0
							&& s.getSetupTime(x) <= 2 * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						double d_min_loose = (1.0 - tau[i]) * (1.0 - r[j]) * pSum;
						double d_max_loose = (r[j] + (1.0 - tau[i]) * (1.0 - r[j])) * (pSum + sSum / (n+1.0));
						assertTrue(s.getDueDate(x) >= d_min_loose);
						assertTrue(s.getDueDate(x) <= d_max_loose);
						for (int y = 0; y <= n; y++) {
							assertTrue(s.getSetupTime(y,x) >= 0
							&& s.getSetupTime(y,x) <= 2 * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						}
						assertEquals(0, s.getReleaseDate(x));
						assertEquals(1, s.getEarlyWeight(x));
					}
				}
			}
		}
	}
	
	@Test
	public void testTau1() {
		int n = 10;
		double[] tau = {1.0};
		double[] r = {0.25, 0.5, 0.75};
		double[] eta = {0.25, 0.5, 0.75};
		for (int i = 0; i < tau.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < eta.length; k++) {
					WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
					assertEquals(n, s.numberOfJobs());
					assertTrue(s.hasDueDates());
					assertTrue(s.hasWeights());
					assertTrue(s.hasSetupTimes());
					assertFalse(s.hasEarlyWeights());
					assertFalse(s.hasReleaseDates());
					boolean diffP = false;
					boolean diffW = false;
					boolean diffS0 = false;
					int pSum = 0;
					int sSum = 0;
					for (int x = 0; x < n; x++) {
						pSum += s.getProcessingTime(x);
						boolean diffS = false;
						for (int y = 0; y <= n; y++) {
							sSum += s.getSetupTime(y,x);
							if (y > 0 && !diffS && s.getSetupTime(y,x) != s.getSetupTime(y-1,x)) {
								diffS = true;
							}
						}
						assertTrue(diffS);
						if (x > 0 && !diffP && s.getProcessingTime(x) != s.getProcessingTime(x-1)) {
							diffP = true;
						}
						if (x > 0 && !diffW && s.getWeight(x) != s.getWeight(x-1)) {
							diffW = true;
						}
						if (x > 0 && !diffS0 && s.getSetupTime(x) != s.getSetupTime(x-1)) {
							diffS0 = true;
						}
					}
					assertTrue(diffP);
					assertTrue(diffW);
					assertTrue(diffS0);
					for (int x = 0; x < n; x++) {
						assertTrue(s.getProcessingTime(x) >= WeightedStaticSchedulingWithSetups.MIN_PROCESS_TIME
							&& s.getProcessingTime(x) <= WeightedStaticSchedulingWithSetups.MAX_PROCESS_TIME);
						assertTrue(s.getWeight(x) >= WeightedStaticSchedulingWithSetups.MIN_WEIGHT
							&& s.getWeight(x) <= WeightedStaticSchedulingWithSetups.MAX_WEIGHT);
						assertTrue(s.getSetupTime(x) >= 0
							&& s.getSetupTime(x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						assertEquals(0, s.getDueDate(x));
						for (int y = 0; y <= n; y++) {
							assertTrue(s.getSetupTime(y,x) >= 0
							&& s.getSetupTime(y,x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						}
						assertEquals(0, s.getReleaseDate(x));
						assertEquals(1, s.getEarlyWeight(x));
					}
				}
			}
		}
	}
	
	@Test
	public void testTau0() {
		int n = 10;
		double[] tau = {0.0};
		double[] r = {0.25, 0.5, 0.75};
		double[] eta = {0.25, 0.5, 0.75};
		for (int i = 0; i < tau.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < eta.length; k++) {
					WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
					assertEquals(n, s.numberOfJobs());
					assertTrue(s.hasDueDates());
					assertTrue(s.hasWeights());
					assertTrue(s.hasSetupTimes());
					assertFalse(s.hasEarlyWeights());
					assertFalse(s.hasReleaseDates());
					boolean diffP = false;
					boolean diffW = false;
					boolean diffS0 = false;
					int pSum = 0;
					int sSum = 0;
					for (int x = 0; x < n; x++) {
						pSum += s.getProcessingTime(x);
						boolean diffS = false;
						for (int y = 0; y <= n; y++) {
							sSum += s.getSetupTime(y,x);
							if (y > 0 && !diffS && s.getSetupTime(y,x) != s.getSetupTime(y-1,x)) {
								diffS = true;
							}
						}
						assertTrue(diffS);
						if (x > 0 && !diffP && s.getProcessingTime(x) != s.getProcessingTime(x-1)) {
							diffP = true;
						}
						if (x > 0 && !diffW && s.getWeight(x) != s.getWeight(x-1)) {
							diffW = true;
						}
						if (x > 0 && !diffS0 && s.getSetupTime(x) != s.getSetupTime(x-1)) {
							diffS0 = true;
						}
						if (x > 0) {
							assertEquals(s.getDueDate(0), s.getDueDate(x));
						}
					}
					assertTrue(diffP);
					assertTrue(diffW);
					assertTrue(diffS0);
					for (int x = 0; x < n; x++) {
						assertTrue(s.getProcessingTime(x) >= WeightedStaticSchedulingWithSetups.MIN_PROCESS_TIME
							&& s.getProcessingTime(x) <= WeightedStaticSchedulingWithSetups.MAX_PROCESS_TIME);
						assertTrue(s.getWeight(x) >= WeightedStaticSchedulingWithSetups.MIN_WEIGHT
							&& s.getWeight(x) <= WeightedStaticSchedulingWithSetups.MAX_WEIGHT);
						assertTrue(s.getSetupTime(x) >= 0
							&& s.getSetupTime(x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						double d_min_loose = pSum;
						double d_max_loose = (pSum + sSum / (n+1.0));
						assertTrue(s.getDueDate(x) >= d_min_loose);
						assertTrue(s.getDueDate(x) <= d_max_loose);
						for (int y = 0; y <= n; y++) {
							assertTrue(s.getSetupTime(y,x) >= 0
							&& s.getSetupTime(y,x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						}
						assertEquals(0, s.getReleaseDate(x));
						assertEquals(1, s.getEarlyWeight(x));
					}
				}
			}
		}
	}
	
	
	
	@Test
	public void testR0() {
		int n = 10;
		double[] tau = {0.25, 0.5, 0.75};
		double[] r = {0.0};
		double[] eta = {0.25, 0.5, 0.75};
		for (int i = 0; i < tau.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < eta.length; k++) {
					WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
					assertEquals(n, s.numberOfJobs());
					assertTrue(s.hasDueDates());
					assertTrue(s.hasWeights());
					assertTrue(s.hasSetupTimes());
					assertFalse(s.hasEarlyWeights());
					assertFalse(s.hasReleaseDates());
					boolean diffP = false;
					boolean diffW = false;
					boolean diffS0 = false;
					int pSum = 0;
					int sSum = 0;
					for (int x = 0; x < n; x++) {
						pSum += s.getProcessingTime(x);
						boolean diffS = false;
						for (int y = 0; y <= n; y++) {
							sSum += s.getSetupTime(y,x);
							if (y > 0 && !diffS && s.getSetupTime(y,x) != s.getSetupTime(y-1,x)) {
								diffS = true;
							}
						}
						assertTrue(diffS);
						if (x > 0 && !diffP && s.getProcessingTime(x) != s.getProcessingTime(x-1)) {
							diffP = true;
						}
						if (x > 0 && !diffW && s.getWeight(x) != s.getWeight(x-1)) {
							diffW = true;
						}
						if (x > 0 && !diffS0 && s.getSetupTime(x) != s.getSetupTime(x-1)) {
							diffS0 = true;
						}
						if (x > 0) {
							assertEquals(s.getDueDate(0), s.getDueDate(x));
						}
					}
					assertTrue(diffP);
					assertTrue(diffW);
					assertTrue(diffS0);
					for (int x = 0; x < n; x++) {
						assertTrue(s.getProcessingTime(x) >= WeightedStaticSchedulingWithSetups.MIN_PROCESS_TIME
							&& s.getProcessingTime(x) <= WeightedStaticSchedulingWithSetups.MAX_PROCESS_TIME);
						assertTrue(s.getWeight(x) >= WeightedStaticSchedulingWithSetups.MIN_WEIGHT
							&& s.getWeight(x) <= WeightedStaticSchedulingWithSetups.MAX_WEIGHT);
						assertTrue(s.getSetupTime(x) >= 0
							&& s.getSetupTime(x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						double d_min_loose = pSum * (1 - tau[i]);
						double d_max_loose = (pSum + sSum / (n+1.0)) * (1 - tau[i]);
						assertTrue(s.getDueDate(x) >= d_min_loose);
						assertTrue(s.getDueDate(x) <= d_max_loose);
						for (int y = 0; y <= n; y++) {
							assertTrue(s.getSetupTime(y,x) >= 0
							&& s.getSetupTime(y,x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						}
						assertEquals(0, s.getReleaseDate(x));
						assertEquals(1, s.getEarlyWeight(x));
					}
				}
			}
		}
	}
	
	@Test
	public void testR1() {
		int n = 10;
		double[] tau = {0.25, 0.5, 0.75};
		double[] r = {1.0};
		double[] eta = {0.25, 0.5, 0.75};
		for (int i = 0; i < tau.length; i++) {
			for (int j = 0; j < r.length; j++) {
				for (int k = 0; k < eta.length; k++) {
					WeightedStaticSchedulingWithSetups s = new WeightedStaticSchedulingWithSetups(n, tau[i], r[j], eta[k], 42);
					assertEquals(n, s.numberOfJobs());
					assertTrue(s.hasDueDates());
					assertTrue(s.hasWeights());
					assertTrue(s.hasSetupTimes());
					assertFalse(s.hasEarlyWeights());
					assertFalse(s.hasReleaseDates());
					boolean diffP = false;
					boolean diffW = false;
					boolean diffS0 = false;
					boolean diffD = false;
					int pSum = 0;
					int sSum = 0;
					for (int x = 0; x < n; x++) {
						pSum += s.getProcessingTime(x);
						boolean diffS = false;
						for (int y = 0; y <= n; y++) {
							sSum += s.getSetupTime(y,x);
							if (y > 0 && !diffS && s.getSetupTime(y,x) != s.getSetupTime(y-1,x)) {
								diffS = true;
							}
						}
						assertTrue(diffS);
						if (x > 0 && !diffP && s.getProcessingTime(x) != s.getProcessingTime(x-1)) {
							diffP = true;
						}
						if (x > 0 && !diffW && s.getWeight(x) != s.getWeight(x-1)) {
							diffW = true;
						}
						if (x > 0 && !diffS0 && s.getSetupTime(x) != s.getSetupTime(x-1)) {
							diffS0 = true;
						}
						if (x > 0 && !diffD && s.getDueDate(x) != s.getDueDate(x-1)) {
							diffD = true;
						}
					}
					assertTrue(diffP);
					assertTrue(diffW);
					assertTrue(diffS0);
					assertTrue(diffD);
					for (int x = 0; x < n; x++) {
						assertTrue(s.getProcessingTime(x) >= WeightedStaticSchedulingWithSetups.MIN_PROCESS_TIME
							&& s.getProcessingTime(x) <= WeightedStaticSchedulingWithSetups.MAX_PROCESS_TIME);
						assertTrue(s.getWeight(x) >= WeightedStaticSchedulingWithSetups.MIN_WEIGHT
							&& s.getWeight(x) <= WeightedStaticSchedulingWithSetups.MAX_WEIGHT);
						assertTrue(s.getSetupTime(x) >= 0
							&& s.getSetupTime(x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						double d_min_loose = 0;
						double d_max_loose = (pSum + sSum / (n+1.0));
						assertTrue(s.getDueDate(x) >= d_min_loose);
						assertTrue(s.getDueDate(x) <= d_max_loose);
						for (int y = 0; y <= n; y++) {
							assertTrue(s.getSetupTime(y,x) >= 0
							&& s.getSetupTime(y,x) <= 2 * eta[k] * WeightedStaticSchedulingWithSetups.AVERAGE_PROCESS_TIME);
						}
						assertEquals(0, s.getReleaseDate(x));
						assertEquals(1, s.getEarlyWeight(x));
					}
				}
			}
		}
	}
	
}