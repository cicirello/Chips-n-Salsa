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
 * JUnit test cases for helper methods from the abstract base class.
 */
public class SelectionOperatorTests extends SharedTestSelectionOperators {
	
	@Test
	public void testSortedIndexes() {
		class TestSortedIndexesSelectionOp extends AbstractWeightedSelection {
			@Override public void selectAll(double[] normalizedWeights, int[] selected) {
				// doesn't matter.... not used in test
			}
			@Override public TestSortedIndexesSelectionOp split() {
				return this;
			}
		}
		TestSortedIndexesSelectionOp selection = new TestSortedIndexesSelectionOp();
		for (int n = 1; n <= 8; n *= 2) {
			int[] expected = new int[n];
			for (int i = 0; i < n; i++) {
				expected[i] = i;
			}
			PopFitVectorDouble f1 = new PopFitVectorDouble(n);
			int[] indexes = selection.sortedIndexes(f1);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			PopFitVectorInteger f2 = new PopFitVectorInteger(n);
			indexes = selection.sortedIndexes(f2);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			PopFitVectorDoubleSimple f3 = new PopFitVectorDoubleSimple(n);
			indexes = selection.sortedIndexes(f3);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			PopFitVectorIntegerSimple f4 = new PopFitVectorIntegerSimple(n);
			indexes = selection.sortedIndexes(f4);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			
			for (int i = 0; i < n; i++) {
				expected[n-1-i] = i;
			}
			f1.reverse();
			f2.reverse();
			f3.reverse();
			f4.reverse();
			indexes = selection.sortedIndexes(f1);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			indexes = selection.sortedIndexes(f2);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			indexes = selection.sortedIndexes(f3);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
			indexes = selection.sortedIndexes(f4);
			assertEquals(n, indexes.length);
			assertArrayEquals(expected, indexes);
		}
	}
	
	@Test
	public void testComputeWeightRunningSumRanks() {
		class TestSelectionOp extends AbstractWeightedSelection {
			@Override public void selectAll(double[] normalizedWeights, int[] selected) {
				// doesn't matter.... not used in test
			}
			@Override public TestSelectionOp split() {
				return this;
			}
		}
		TestSelectionOp selection = new TestSelectionOp();
		for (int n = 1; n <= 8; n *= 2) {
			int[] indexes = new int[n];
			int[] reversed = new int[n];
			for (int i = 0; i < n; i++) {
				indexes[i] = i;
				reversed[n-1-i] = i;
			}
			double[] weightsRunningSum = selection.computeWeightRunningSumRanks(indexes, x -> x + 1);
			for (int i = 0; i < n; i++) {
				double expected = (i+1)*(i+2)/2;
				assertEquals(expected, weightsRunningSum[i], 1E-10);
			}
			weightsRunningSum = selection.computeWeightRunningSumRanks(reversed, x -> x + 1);
			for (int i = 0; i < n; i++) {
				double expected = n*(n+1)/2 - (n-i-1)*(n-i)/2;
				assertEquals(expected, weightsRunningSum[i], 1E-10);
			}
			weightsRunningSum = selection.computeWeightRunningSumRanks(indexes, x -> (x + 1)*(x + 1));
			for (int i = 0; i < n; i++) {
				int j = i+1;
				double expected = j*(j+1)*(2*j+1)/6;
				assertEquals(expected, weightsRunningSum[i], 1E-10);
			}
			weightsRunningSum = selection.computeWeightRunningSumRanks(reversed, x -> (x + 1)*(x + 1));
			for (int i = 0; i < n; i++) {
				int j = n-1-i;
				double expected = n*(n+1)*(2*n+1)/6 - j*(j+1)*(2*j+1)/6;
				assertEquals(expected, weightsRunningSum[i], 1E-10);
			}
		}
	}
}
