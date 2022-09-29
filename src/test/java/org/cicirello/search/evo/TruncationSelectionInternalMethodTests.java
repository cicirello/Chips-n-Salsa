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

package org.cicirello.search.evo;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for TruncationSelection.
 */
public class TruncationSelectionInternalMethodTests extends SharedTestSelectionOperators {
	
	@Test
	public void testTruncationSelectionPartitioningBestToRight_Integer() {
		for (int k = 1; k <= 9; k++) {
			TruncationSelection selection = new TruncationSelection(k);
			for (int n = 1; n <= 8; n++) {
				int truncateCount = Math.max(n - k, 0);
				PopFitVectorIntegerSimple pop1 = new PopFitVectorIntegerSimple(n);
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					truncateCount,
					truncateCount + 1,
					false,
					false
				);
				pop1.reverse();
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					truncateCount,
					truncateCount + 1,
					false,
					false
				);
				
				// All duplicates low fitness
				pop1 = new PopFitVectorIntegerSimple(lowDuplicatesTestData(truncateCount, n));
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					1,
					2,
					true,
					false
				);
				pop1.reverse();
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					1,
					2,
					true,
					false
				);
				
				// all duplicates high fitness
				pop1 = new PopFitVectorIntegerSimple(highDuplicatesTestData(truncateCount, n));
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					99,
					100,
					false,
					true
				);
				pop1.reverse();
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					99,
					100,
					false,
					true
				);
				
				// duplicates surrounding boundary
				pop1 = new PopFitVectorIntegerSimple(boundaryDuplicatesTestData(truncateCount, n));
				verifyBestToRight(
					truncateCount, 
					n, 
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount), 
					pop1, 
					100, 
					100,
					false,
					false
				);
				pop1.reverse();
				verifyBestToRight(
					truncateCount, 
					n, 
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount), 
					pop1, 
					100, 
					100,
					false,
					false
				);
			}
		}
	}
	
	@Test
	public void testTruncationSelectionPartitioningBestToRight_Double() {
		for (int k = 1; k <= 9; k++) {
			TruncationSelection selection = new TruncationSelection(k);
			for (int n = 1; n <= 8; n++) {
				int truncateCount = Math.max(n - k, 0);
				PopFitVectorDoubleSimple pop1 = new PopFitVectorDoubleSimple(n);
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					truncateCount,
					truncateCount + 1,
					false,
					false
				);
				pop1.reverse();
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					truncateCount,
					truncateCount + 1,
					false,
					false
				);
				
				// All duplicates low fitness
				pop1 = new PopFitVectorDoubleSimple(lowDuplicatesTestData(truncateCount, n));
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					1,
					2,
					true,
					false
				);
				pop1.reverse();
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					1,
					2,
					true,
					false
				);
				
				// all duplicates high fitness
				pop1 = new PopFitVectorDoubleSimple(highDuplicatesTestData(truncateCount, n));
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					99,
					100,
					false,
					true
				);
				pop1.reverse();
				verifyBestToRight(
					truncateCount, 
					n,
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount),
					pop1,
					99,
					100,
					false,
					true
				);
				
				// duplicates surrounding boundary
				pop1 = new PopFitVectorDoubleSimple(boundaryDuplicatesTestData(truncateCount, n));
				verifyBestToRight(
					truncateCount, 
					n, 
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount), 
					pop1, 
					100, 
					100,
					false,
					false
				);
				pop1.reverse();
				verifyBestToRight(
					truncateCount, 
					n, 
					selection.bestFitToRight(pop1, selection.initSelectFrom(n), 0, n-1, truncateCount), 
					pop1, 
					100, 
					100,
					false,
					false
				);
			}
		}
	}
	
	private String toString(int[] array) {
		String s = "" + array[0];
		for (int i = 1; i < array.length; i++) {
			s += " " + array[i];
		}
		return s;
	}
	
	private void verifyBestToRight(int truncateCount, int n, int[] partitioned, PopFitVectorDoubleSimple pop1, int firstLimit, int secondLimit, boolean firstExact, boolean secondExact) {
		assertEquals(n, partitioned.length);
		boolean[] found = new boolean[n];
		for (int i = 0; i < truncateCount; i++) {
			assertFalse(found[partitioned[i]]);
			found[partitioned[i]] = true;
			if (firstExact) {
				assertEquals(firstLimit, pop1.getFitness(partitioned[i]));
			} else {
				assertTrue(pop1.getFitness(partitioned[i]) <= firstLimit);
			}
		}
		for (int i = truncateCount; i < n; i++) {
			assertFalse(found[partitioned[i]]);
			found[partitioned[i]] = true;
			if (secondExact) {
				assertEquals(secondLimit, pop1.getFitness(partitioned[i]));
			} else {
				assertTrue(pop1.getFitness(partitioned[i]) >= secondLimit);
			}
		}
	}
	
	private void verifyBestToRight(int truncateCount, int n, int[] partitioned, PopFitVectorIntegerSimple pop1, int firstLimit, int secondLimit, boolean firstExact, boolean secondExact) {
		assertEquals(n, partitioned.length);
		boolean[] found = new boolean[n];
		for (int i = 0; i < truncateCount; i++) {
			assertFalse(found[partitioned[i]]);
			found[partitioned[i]] = true;
			if (firstExact) {
				assertEquals(firstLimit, pop1.getFitness(partitioned[i]));
			} else {
				assertTrue(pop1.getFitness(partitioned[i]) <= firstLimit);
			}
		}
		for (int i = truncateCount; i < n; i++) {
			assertFalse(found[partitioned[i]]);
			found[partitioned[i]] = true;
			if (secondExact) {
				assertEquals(secondLimit, pop1.getFitness(partitioned[i]));
			} else {
				assertTrue(pop1.getFitness(partitioned[i]) >= secondLimit);
			}
		}
	}
	
	private int[] lowDuplicatesTestData(int truncateCount, int n) {
		int[] fits = new int[n];
		for (int i = 0; i < n; i++) {
			if (i < truncateCount) {
				fits[i] = 1;
			} else {
				fits[i] = 2 + i;
			}
		}
		return fits;
	}
	
	private int[] highDuplicatesTestData(int truncateCount, int n) {
		int[] fits = new int[n];
		for (int i = 0; i < n; i++) {
			if (i < truncateCount) {
				fits[i] = 1 + i;
			} else {
				fits[i] = 100;
			}
		}
		return fits;
	}
	
	private int[] boundaryDuplicatesTestData(int truncateCount, int n) {
		int[] fits = new int[n];
		for (int i = 0; i < n; i++) {
			if (i < truncateCount - 1) {
				fits[i] = 1 + i;
			} else if (i < truncateCount + 2) {
				fits[i] = 100;
			} else {
				fits[i] = 200 + i;
			}
		}
		return fits;
	}
}
