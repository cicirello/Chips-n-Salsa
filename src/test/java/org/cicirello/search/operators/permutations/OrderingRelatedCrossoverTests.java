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
 
package org.cicirello.search.operators.permutations;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;

/**
 * JUnit test cases for ordering related crossover operators,
 * such as OX, NWOX, UOBX.
 */
public class OrderingRelatedCrossoverTests {
	
	private final static int NUM_SAMPLES = 5;
	
	@Test
	public void testOX() {
		OrderCrossover ox = new OrderCrossover();
		for (int n = 1; n <= 64; n *= 2) {
			for (int s = 0; s < NUM_SAMPLES; s++) {
				Permutation p1 = new Permutation(n);
				Permutation p2 = new Permutation(n);
				Permutation parent1 = new Permutation(p1);
				Permutation parent2 = new Permutation(p2);
				ox.cross(parent1, parent2);
				assertTrue(validPermutation(parent1));
				assertTrue(validPermutation(parent2));
				//if (n >= 32) {
				//	boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
				//	int[] startAndEnd = findStartAndEnd(fixedPoints);
				//	validateOrderingOX(parent1, p2, startAndEnd);
				//	validateOrderingOX(parent2, p1, startAndEnd);
				//}
			}
		}
		assertSame(ox, ox.split());
		final int n = 1000;
		Permutation p1 = new Permutation(n);
		Permutation p2 = new Permutation(n);
		Permutation parent1 = new Permutation(p1);
		Permutation parent2 = new Permutation(p2);
		ox.cross(parent1, parent2);
		assertTrue(validPermutation(parent1));
		assertTrue(validPermutation(parent2));
		boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
		int[] startAndEnd = findStartAndEnd(fixedPoints);
		// The following may on infrequent occasions exhibit a false failure.
		// This is due to the above findStartAndEnd heuristically guessing what
		// the random cross region was. I believe the probability of a false
		// failure is approximately 1/n. Rerun if fails.
		validateOrderingOX(parent1, p2, startAndEnd);
		validateOrderingOX(parent2, p1, startAndEnd);
	}
	
	@Test
	public void testNWOX() {
		NonWrappingOrderCrossover nwox = new NonWrappingOrderCrossover();
		for (int n = 1; n <= 64; n *= 2) {
			for (int s = 0; s < NUM_SAMPLES; s++) {
				Permutation p1 = new Permutation(n);
				Permutation p2 = new Permutation(n);
				Permutation parent1 = new Permutation(p1);
				Permutation parent2 = new Permutation(p2);
				nwox.cross(parent1, parent2);
				assertTrue(validPermutation(parent1));
				assertTrue(validPermutation(parent2));
				
				boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
				//int[] startAndEnd = findStartAndEnd(fixedPoints);
				// Deliberately using UOBX validation here to verify non-fixed points
				// follow relative ordering property only.
				validateOrderingUOBX(parent1, p2, fixedPoints);
				validateOrderingUOBX(parent2, p1, fixedPoints);
			}
		}
		assertSame(nwox, nwox.split());
		final int n = 1000;
		Permutation p1 = new Permutation(n);
		Permutation p2 = new Permutation(n);
		Permutation parent1 = new Permutation(p1);
		Permutation parent2 = new Permutation(p2);
		nwox.cross(parent1, parent2);
		assertTrue(validPermutation(parent1));
		assertTrue(validPermutation(parent2));
		boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
		int[] startAndEnd = findStartAndEnd(fixedPoints);
		// The following may on infrequent occasions exhibit a false failure.
		// This is due to the above findStartAndEnd heuristically guessing what
		// the random cross region was. I believe the probability of a false
		// failure is approximately 1/n. Rerun if fails.
		validateOrderingNWOX(parent1, p2, startAndEnd);
		validateOrderingNWOX(parent2, p1, startAndEnd);
	}
	
	@Test
	public void testNWOXIdenticalParents() {
		NonWrappingOrderCrossover nwox = new NonWrappingOrderCrossover();
		for (int n = 1; n <= 32; n*= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(p1);
			Permutation parent1 = new Permutation(p1);
			Permutation parent2 = new Permutation(p2);
			nwox.cross(parent1, parent2);
			assertEquals(p1, parent1);
			assertEquals(p2, parent2);
		}
		assertSame(nwox, nwox.split());
	}
	
	@Test
	public void testUOBXIdenticalParents() {
		UniformOrderBasedCrossover uobx = new UniformOrderBasedCrossover();
		for (int n = 1; n <= 32; n*= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(p1);
			Permutation parent1 = new Permutation(p1);
			Permutation parent2 = new Permutation(p2);
			uobx.cross(parent1, parent2);
			assertEquals(p1, parent1);
			assertEquals(p2, parent2);
		}
		assertSame(uobx, uobx.split());
	}
	
	@Test
	public void testUOBXNear0U() {
		UniformOrderBasedCrossover uobx = new UniformOrderBasedCrossover(Math.ulp(0.0));
		for (int n = 1; n <= 32; n*= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(n);
			Permutation parent1 = new Permutation(p1);
			Permutation parent2 = new Permutation(p2);
			uobx.cross(parent1, parent2);
			// the near 0 u should essentially swap the parents
			// other than a low probability statistical anomaly
			assertEquals(p2, parent1);
			assertEquals(p1, parent2);
		}
	}
	
	@Test
	public void testUOBXNear1U() {
		UniformOrderBasedCrossover uobx = new UniformOrderBasedCrossover(1.0-Math.ulp(1.0));
		for (int n = 1; n <= 32; n*= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(n);
			Permutation parent1 = new Permutation(p1);
			Permutation parent2 = new Permutation(p2);
			uobx.cross(parent1, parent2);
			// the near 1.0 u should essentially keep all of the parents
			// other than a low probability statistical anomaly
			assertEquals(p1, parent1);
			assertEquals(p2, parent2);
		}
	}
	
	@Test
	public void testUOBXTypicalCase() {
		UniformOrderBasedCrossover uobx = new UniformOrderBasedCrossover();
		for (int n = 1; n <= 32; n*= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(n);
			Permutation parent1 = new Permutation(p1);
			Permutation parent2 = new Permutation(p2);
			uobx.cross(parent1, parent2);
			assertTrue(validPermutation(parent1));
			assertTrue(validPermutation(parent2));
			boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
			validateOrderingUOBX(parent1, p2, fixedPoints);
			validateOrderingUOBX(parent2, p1, fixedPoints);
		}
		
		uobx = new UniformOrderBasedCrossover(0.25);
		for (int n = 8; n <= 32; n*= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(n);
			Permutation parent1 = new Permutation(p1);
			Permutation parent2 = new Permutation(p2);
			uobx.cross(parent1, parent2);
			assertTrue(validPermutation(parent1));
			assertTrue(validPermutation(parent2));
			boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
			validateOrderingUOBX(parent1, p2, fixedPoints);
			validateOrderingUOBX(parent2, p1, fixedPoints);
		}
		
		uobx = new UniformOrderBasedCrossover(0.75);
		for (int n = 8; n <= 32; n*= 2) {
			Permutation p1 = new Permutation(n);
			Permutation p2 = new Permutation(n);
			Permutation parent1 = new Permutation(p1);
			Permutation parent2 = new Permutation(p2);
			uobx.cross(parent1, parent2);
			assertTrue(validPermutation(parent1));
			assertTrue(validPermutation(parent2));
			boolean[] fixedPoints = findFixedPoints(parent1, parent2, p1, p2);
			validateOrderingUOBX(parent1, p2, fixedPoints);
			validateOrderingUOBX(parent2, p1, fixedPoints);
		}
	}
	
	@Test
	public void testExceptionsUOBX() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new UniformOrderBasedCrossover(0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new UniformOrderBasedCrossover(1.0)
		);
	}
	
	private void validateOrderingUOBX(Permutation child, Permutation order, boolean[] fixedPoints) {
		int[] inv = order.getInverse();
		int last = -1;
		for (int i = 0; i < fixedPoints.length; i++) {
			if (!fixedPoints[i]) {
				if (last >= 0) {
					assertTrue(inv[child.get(i)] > inv[child.get(last)]);
				}
				last = i;
			}
		}
	}
	
	private void validateOrderingOX(Permutation child, Permutation order, int[] startAndEnd) {
		int[] inv = order.getInverse();
		for (int i = startAndEnd[1] + 2; i < inv.length; i++) {
			assertTrue(inv[child.get(i)] > inv[child.get(i-1)], "This may infrequently result in a false failure because test case heuristically guesses where the random cross region was. Rerun if fails.");
		}
		for (int i = 1; i < startAndEnd[0]; i++) {
			assertTrue(inv[child.get(i)] > inv[child.get(i-1)], "This may infrequently result in a false failure because test case heuristically guesses where the random cross region was. Rerun if fails.");
		}
		if (0 < startAndEnd[0] && inv.length-1 > startAndEnd[1]) {
			assertTrue(inv[child.get(0)] > inv[child.get(inv.length-1)], "This may infrequently result in a false failure because test case heuristically guesses where the random cross region was. Rerun if fails.");
		}
	}
	
	private void validateOrderingNWOX(Permutation child, Permutation order, int[] startAndEnd) {
		int[] inv = order.getInverse();
		for (int i = 1; i < startAndEnd[0]; i++) {
			assertTrue(inv[child.get(i)] > inv[child.get(i-1)], "This may infrequently result in a false failure because test case heuristically guesses where the random cross region was. Rerun if fails.");
		}
		for (int i = startAndEnd[1] + 2; i < inv.length; i++) {
			assertTrue(inv[child.get(i)] > inv[child.get(i-1)], "This may infrequently result in a false failure because test case heuristically guesses where the random cross region was. Rerun if fails.");
		}
		if (startAndEnd[0] - 1 >= 0 && startAndEnd[1] + 1 < inv.length) {
			assertTrue(inv[child.get(startAndEnd[1] + 1)] > inv[child.get(startAndEnd[0] - 1)], "This may infrequently result in a false failure because test case heuristically guesses where the random cross region was. Rerun if fails.");
		}
	}
	
	private int[] findStartAndEnd(boolean[] fixedPoints) {
		int[] counts = new int[fixedPoints.length];
		int max = -1;
		for (int i = 0; i < counts.length; i++) {
			for (int j = i; j < counts.length; j++) {
				if (fixedPoints[j]) counts[i]++;
				else break;
			}
			if (max < 0 || counts[i] > counts[max]) max = i;
		}
		assertTrue(max >= 0 && counts[max] > 0);
		return new int[] {max, max + counts[max] - 1};
	}
	
	private boolean[] findFixedPoints(Permutation child1, Permutation child2, Permutation original1, Permutation original2) {
		boolean[] fixedPoints = new boolean[original1.length()];
		for (int i = 0; i < fixedPoints.length; i++) {
			fixedPoints[i] = child1.get(i) == original1.get(i) && child2.get(i) == original2.get(i);
		}
		return fixedPoints;
	}
	
	private boolean validPermutation(Permutation p) {
		boolean[] foundIt = new boolean[p.length()];
		for (int i = 0; i < p.length(); i++) {
			if (foundIt[p.get(i)]) return false;
			foundIt[p.get(i)] = true;
		}
		return true;
	}
}
