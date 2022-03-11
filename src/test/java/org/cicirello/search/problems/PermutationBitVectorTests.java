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
 
package org.cicirello.search.problems;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.permutations.Permutation;
import org.cicirello.permutations.distance.ExactMatchDistance;
import org.cicirello.search.representations.BitVector;
import org.cicirello.search.SolutionCostPair;

/**
 * JUnit Tests for PermutationToBitVectorProblem and its nested subclasses.
 */
public class PermutationBitVectorTests {
	
	@Test
	public void testConversionBaseClass() {
		Permutation[] expected = {
			new Permutation(1, 0), new Permutation(2, 0), new Permutation(3, 0), new Permutation(4, 0),
			new Permutation(5, 0), new Permutation(6, 0), new Permutation(7, 0), new Permutation(8, 0),
			new Permutation(9, 0), new Permutation(10, 0), new Permutation(11, 0), new Permutation(12, 0),
			new Permutation(13, 0), new Permutation(14, 0), new Permutation(15, 0), new Permutation(16, 0)
		};
		int[] expectedBitLengths = {0, 1, 2*2, 2*3, 3*4, 3*5, 3*6, 3*7, 4*8, 4*9, 4*10, 4*11, 4*12, 4*13, 4*14, 4*15};
		int[][] modCases = {
			{0}, {0}, {11}, {44}, {1253}, {0x272e}, {0x13977}, {0x9cbb8},
			{0x23456789}, {0x3456789a, 0x2}, {0x456789ab, 0x23}, {0x56789abc, 0x234}, 
			{0x6789abcd, 0x2345}, {0x789abcde, 0x23456}, {0x89abcdef, 0x234567}, {0x9abcdef0, 0x2345678}
		};
		for (int i = 0; i < expected.length; i++) {
			PermutationToBitVectorProblem converter = new PermutationToBitVectorProblem(expected[i].length());
			assertEquals(expectedBitLengths[i], converter.supportedBitVectorLength());
			assertEquals(expectedBitLengths[i], converter.createCandidateSolution().length());
			assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i])));
			if (i < modCases.length && i > 0) {
				assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i], modCases[i])));
			}
			
			PermutationToBitVectorProblem split = converter.split();
			assertTrue(converter != split);
			assertEquals(expectedBitLengths[i], split.supportedBitVectorLength());
			assertEquals(expectedBitLengths[i], split.createCandidateSolution().length());
			assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i])));
			if (i < modCases.length && i > 0) {
				assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i], modCases[i])));
			}
			
			expected[i].reverse();
			int[][] reversedCases = {
				{0}, {1}, {6}, {0x1b}, {0x29c}, {0x14e5}, {0xa72e}, {0x53977},
				{0x12345678}, {0x23456789, 0x1}, {0x3456789a, 0x12}, {0x456789ab, 0x123}, 
				{0x56789abc, 0x1234}, {0x6789abcd, 0x12345}, {0x789abcde, 0x123456}, {0x89abcdef, 0x1234567}
			};
			if (i < reversedCases.length && i > 0) {
				assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i], reversedCases[i])));
				assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i], reversedCases[i])));
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PermutationToBitVectorProblem(0)
		);
		
	}
	
	@Test
	public void testIntegerCosts() {
		Permutation[] expected = {
			new Permutation(1, 0), new Permutation(2, 0), new Permutation(3, 0), new Permutation(4, 0),
			new Permutation(5, 0), new Permutation(6, 0), new Permutation(7, 0), new Permutation(8, 0),
			new Permutation(9, 0), new Permutation(10, 0), new Permutation(11, 0), new Permutation(12, 0),
			new Permutation(13, 0), new Permutation(14, 0), new Permutation(15, 0), new Permutation(16, 0)
		};
		int[] expectedBitLengths = {0, 1, 2*2, 2*3, 3*4, 3*5, 3*6, 3*7, 4*8, 4*9, 4*10, 4*11, 4*12, 4*13, 4*14, 4*15};
		int[][] modCases = {
			{0}, {0}, {11}, {44}, {1253}, {0x272e}, {0x13977}, {0x9cbb8},
			{0x23456789}, {0x3456789a, 0x2}, {0x456789ab, 0x23}, {0x56789abc, 0x234}, 
			{0x6789abcd, 0x2345}, {0x789abcde, 0x23456}, {0x89abcdef, 0x234567}, {0x9abcdef0, 0x2345678}
		};
		for (int i = 0; i < expected.length; i++) {
			PermutationInAHaystack problem = new PermutationInAHaystack(new ExactMatchDistance(), expected[i].copy()); 
			PermutationToBitVectorProblem.IntegerCost converter = new PermutationToBitVectorProblem.IntegerCost(problem, expected[i].length());
			assertEquals(expectedBitLengths[i], converter.supportedBitVectorLength());
			assertEquals(expectedBitLengths[i], converter.createCandidateSolution().length());
			assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i])));
			if (i < modCases.length && i > 0) {
				assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i], modCases[i])));
				assertEquals(0, converter.cost(new BitVector(expectedBitLengths[i], modCases[i])));
				assertEquals(0.0, converter.costAsDouble(new BitVector(expectedBitLengths[i], modCases[i])), 1E-10);
				assertEquals(0, converter.value(new BitVector(expectedBitLengths[i], modCases[i])));
			}
			assertEquals(0, converter.cost(new BitVector(expectedBitLengths[i])));
			assertEquals(0, converter.value(new BitVector(expectedBitLengths[i])));
			assertEquals(0.0, converter.costAsDouble(new BitVector(expectedBitLengths[i])), 1E-10);
			assertTrue(converter.isMinCost(0));
			assertFalse(converter.isMinCost(1));
			assertEquals(0, converter.minCost());
			SolutionCostPair<BitVector> pair = converter.getSolutionCostPair(new BitVector(expectedBitLengths[i]));
			assertEquals(0, pair.getCost());
			assertEquals(new BitVector(expectedBitLengths[i]), pair.getSolution());
			
			PermutationToBitVectorProblem.IntegerCost split = converter.split();
			assertTrue(converter != split);
			assertEquals(expectedBitLengths[i], split.supportedBitVectorLength());
			assertEquals(expectedBitLengths[i], split.createCandidateSolution().length());
			assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i])));
			if (i < modCases.length && i > 0) {
				assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i], modCases[i])));
			}
			
			expected[i].reverse();
			int[][] reversedCases = {
				{0}, {1}, {6}, {0x1b}, {0x29c}, {0x14e5}, {0xa72e}, {0x53977},
				{0x12345678}, {0x23456789, 0x1}, {0x3456789a, 0x12}, {0x456789ab, 0x123}, 
				{0x56789abc, 0x1234}, {0x6789abcd, 0x12345}, {0x789abcde, 0x123456}, {0x89abcdef, 0x1234567}
			};
			if (i < reversedCases.length && i > 0) {
				assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i], reversedCases[i])));
				assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i], reversedCases[i])));
				assertEquals(expected[i].length()%2==0?expected[i].length():expected[i].length()-1, converter.cost(new BitVector(expectedBitLengths[i], reversedCases[i])));
				assertEquals(expected[i].length()%2==0?expected[i].length():expected[i].length()-1, converter.value(new BitVector(expectedBitLengths[i], reversedCases[i])));
				assertEquals(expected[i].length()%2==0?expected[i].length():expected[i].length()-1, converter.costAsDouble(new BitVector(expectedBitLengths[i], reversedCases[i])), 1E-10);
				pair = converter.getSolutionCostPair(new BitVector(expectedBitLengths[i], reversedCases[i]));
				assertEquals(expected[i].length()%2==0?expected[i].length():expected[i].length()-1, pair.getCost());
				assertEquals(new BitVector(expectedBitLengths[i], reversedCases[i]), pair.getSolution());
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PermutationToBitVectorProblem.IntegerCost(new PermutationInAHaystack(new ExactMatchDistance(), 10), 0)
		);
	}
	
	@Test
	public void testDoubleCosts() {
		Permutation[] expected = {
			new Permutation(1, 0), new Permutation(2, 0), new Permutation(3, 0), new Permutation(4, 0),
			new Permutation(5, 0), new Permutation(6, 0), new Permutation(7, 0), new Permutation(8, 0),
			new Permutation(9, 0), new Permutation(10, 0), new Permutation(11, 0), new Permutation(12, 0),
			new Permutation(13, 0), new Permutation(14, 0), new Permutation(15, 0), new Permutation(16, 0)
		};
		int[] expectedBitLengths = {0, 1, 2*2, 2*3, 3*4, 3*5, 3*6, 3*7, 4*8, 4*9, 4*10, 4*11, 4*12, 4*13, 4*14, 4*15};
		int[][] modCases = {
			{0}, {0}, {11}, {44}, {1253}, {0x272e}, {0x13977}, {0x9cbb8},
			{0x23456789}, {0x3456789a, 0x2}, {0x456789ab, 0x23}, {0x56789abc, 0x234}, 
			{0x6789abcd, 0x2345}, {0x789abcde, 0x23456}, {0x89abcdef, 0x234567}, {0x9abcdef0, 0x2345678}
		};
		for (int i = 0; i < expected.length; i++) {
			TestProblem problem = new TestProblem(expected[i].copy()); 
			PermutationToBitVectorProblem.DoubleCost converter = new PermutationToBitVectorProblem.DoubleCost(problem, expected[i].length());
			assertEquals(expectedBitLengths[i], converter.supportedBitVectorLength());
			assertEquals(expectedBitLengths[i], converter.createCandidateSolution().length());
			assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i])));
			if (i < modCases.length && i > 0) {
				assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i], modCases[i])));
				assertEquals(0.0, converter.cost(new BitVector(expectedBitLengths[i], modCases[i])), 1E-10);
				assertEquals(0.0, converter.costAsDouble(new BitVector(expectedBitLengths[i], modCases[i])), 1E-10);
				assertEquals(0.0, converter.value(new BitVector(expectedBitLengths[i], modCases[i])), 1E-10);
			}
			assertEquals(0.0, converter.cost(new BitVector(expectedBitLengths[i])), 1E-10);
			assertEquals(0.0, converter.value(new BitVector(expectedBitLengths[i])), 1E-10);
			assertEquals(0.0, converter.costAsDouble(new BitVector(expectedBitLengths[i])), 1E-10);
			assertTrue(converter.isMinCost(0));
			assertFalse(converter.isMinCost(1));
			assertEquals(0.0, converter.minCost(), 1E-10);
			SolutionCostPair<BitVector> pair = converter.getSolutionCostPair(new BitVector(expectedBitLengths[i]));
			assertEquals(0.0, pair.getCostDouble(), 1E-10);
			assertEquals(new BitVector(expectedBitLengths[i]), pair.getSolution());
			
			PermutationToBitVectorProblem.DoubleCost split = converter.split();
			assertTrue(converter != split);
			assertEquals(expectedBitLengths[i], split.supportedBitVectorLength());
			assertEquals(expectedBitLengths[i], split.createCandidateSolution().length());
			assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i])));
			if (i < modCases.length && i > 0) {
				assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i], modCases[i])));
			}
			
			expected[i].reverse();
			int[][] reversedCases = {
				{0}, {1}, {6}, {0x1b}, {0x29c}, {0x14e5}, {0xa72e}, {0x53977},
				{0x12345678}, {0x23456789, 0x1}, {0x3456789a, 0x12}, {0x456789ab, 0x123}, 
				{0x56789abc, 0x1234}, {0x6789abcd, 0x12345}, {0x789abcde, 0x123456}, {0x89abcdef, 0x1234567}
			};
			if (i < reversedCases.length && i > 0) {
				assertEquals(expected[i], converter.toPermutation(new BitVector(expectedBitLengths[i], reversedCases[i])));
				assertEquals(expected[i], split.toPermutation(new BitVector(expectedBitLengths[i], reversedCases[i])));
				assertEquals(expected[i].length()%2==0?expected[i].length():expected[i].length()-1, converter.cost(new BitVector(expectedBitLengths[i], reversedCases[i])), 1E-10);
				assertEquals(expected[i].length()%2==0?expected[i].length():expected[i].length()-1, converter.value(new BitVector(expectedBitLengths[i], reversedCases[i])), 1E-10);
				assertEquals(expected[i].length()%2==0?expected[i].length():expected[i].length()-1, converter.costAsDouble(new BitVector(expectedBitLengths[i], reversedCases[i])), 1E-10);
				pair = converter.getSolutionCostPair(new BitVector(expectedBitLengths[i], reversedCases[i]));
				assertEquals(expected[i].length()%2==0?expected[i].length():expected[i].length()-1, pair.getCostDouble(), 1E-10);
				assertEquals(new BitVector(expectedBitLengths[i], reversedCases[i]), pair.getSolution());
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new PermutationToBitVectorProblem.DoubleCost(new TestProblem(new Permutation(10)), 0)
		);
	}
	
	private static class TestProblem implements OptimizationProblem<Permutation> {
		
		private PermutationInAHaystack problem;
		
		public TestProblem(Permutation perm) {
			problem = new PermutationInAHaystack(new ExactMatchDistance(), perm);
		}
		
		@Override
		public double cost(Permutation p) {
			return problem.cost(p);
		}
		
		@Override
		public double value(Permutation p) {
			return problem.value(p);
		}
		
		@Override
		public double costAsDouble(Permutation p) {
			return problem.cost(p);
		}
		
		@Override
		public double minCost() {
			return 0.0;
		}
		
		@Override
		public boolean isMinCost(double c) {
			return c==0.0;
		}
	}
	
}
