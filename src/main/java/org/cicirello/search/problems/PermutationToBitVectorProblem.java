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

import org.cicirello.util.Copyable;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.representations.BitVector;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.Initializer;
import org.cicirello.search.operators.bits.BitVectorInitializer;

/**
 * <p>This class implements a mapping between Permutation problems and BitVector problems,
 * enabling using {@link BitVector BitVector} search operators to solve problems defined over the space of
 * {@link Permutation Permutation} objects. It can also be used as an {@link Initializer} of BitVectors
 * by search algorithms to ensure that the search is using BitVectors of the appropriate length to represent
 * permutations of the desired length for the problem you are solving. In fact, to ensure that your search
 * is using the correct bit length, you should use this as your Initializer.</p>
 *
 * <p>The mapping uses a classic transformation from vector of bits to a permutation of the N integers from 0 to N-1
 * that works as follows. Consider a list of integers L initially containing the N integers in sorted
 * order: L = [0, 1, 2, ..., N-1]. The list L is circular, such that any integer is an index into L. For example,
 * if L is of length 5, indexes 0, 5, 10, etc all refer to the element in position 0. Likewise, indexes 1, 6, 11, etc
 * all refer to the element in position 1, and so forth. The bit vector must be length: (N - 1) ceiling(lg N). Each
 * contiguous group of ceiling(lg N) bits is treated as an index into L. There are N-1 such indexes available based
 * on the bit vector length. Each such index is used to select an
 * element from L, add it to the next available position of an initially empty permutation P, and remove it from L.
 * After N-1 such operations, there will be one element left in L, which is then added to the end. The {@link #toPermutation toPermutation(BitVector)}
 * method implements this transformation.</p>
 *
 * <p>Here is an example. Consider a very small permutation length N = 6. 
 * For this length permutation, the bit vector must be (6 - 1) ceiling(lg 6) = 5 * 3 = 15 bits in length.
 * Let's consider the transformation from the bit vector 111110001010100.</p>
 * <ul>
 * <li>L is initially L = [0, 1, 2, 3, 4, 5], and P is initially P = [].</li>
 * <li>The first 3 bits, 111, is 7 in decimal, and 7 mod 6 = 1. L[1]=1, so we remove the
 * 1 from L and add it to the end of P. This leads to L = [0, 2, 3, 4, 5], and P is P = [1].</li>
 * <li>The next 3 bits, 110, is 6 in decimal, and 6 mod 5 = 1. L[1]=2, so we remove the
 * 2 from L and add it to the end of P. This leads to L = [0, 3, 4, 5], and P is P = [1, 2].</li>
 * <li>The next 3 bits, 001, is 1 in decimal, and 1 mod 4 = 1. L[1]=3, so we remove the
 * 3 from L and add it to the end of P. This leads to L = [0, 4, 5], and P is P = [1, 2, 3].</li>
 * <li>The next 3 bits, 010, is 2 in decimal, and 2 mod 3 = 2. L[2]=5, so we remove the
 * 5 from L and add it to the end of P. This leads to L = [0, 4], and P is P = [1, 2, 3, 5].</li>
 * <li>The last 3 bits, 100, is 4 in decimal, and 4 mod 2 = 0. L[0]=0, so we remove the
 * 0 from L and add it to the end of P. This leads to L = [4], and P is P = [1, 2, 3, 5, 0].</li>
 * <li>Reached the end of the bit vector, and there is only one element left in L, the 4, which
 * we simply add to the end of P to arrive at P = [1, 2, 3, 5, 0, 4].</li>
 * </ul>
 *
 * <p>This class has two nested subclasses, {@link DoubleCost} and {@link IntegerCost}, that handle
 * the transformations from Permutation optimization problems with costs of type double and int, respectively
 * (i.e., classes {@link OptimizationProblem} and {@link IntegerCostOptimizationProblem}).</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class PermutationToBitVectorProblem implements Initializer<BitVector> {
	
	private final BitVectorInitializer init;
	private final int bitsPerElement;
	private final int permutationLength;
	
	/**
	 * Initializes the PermutationToBitVectorProblem mapping for a specific permutation length.
	 *
	 * @param permutationLength The length of the permutations under optimization, in number of elements.
	 *     This is NOT the length of the BitVectors. For example, if the problem is the Traveling Salesperson,
	 *     and if the instance has 100 cities, then you would pass 100 for this parameter.
	 *
	 * @throws IllegalArgumentException if permutationLength is less than 1.
	 */
	public PermutationToBitVectorProblem(int permutationLength) {
		if (permutationLength < 1) throw new IllegalArgumentException("permutationLength must be positive");
		bitsPerElement = 32 - Integer.numberOfLeadingZeros(permutationLength-1);
		init = new BitVectorInitializer(bitsPerElement * (permutationLength - 1));
		this.permutationLength = permutationLength;
	}
	
	/*
	 * package private for use by split
	 */
	PermutationToBitVectorProblem(PermutationToBitVectorProblem other) {
		init = other.init.split();
		bitsPerElement = other.bitsPerElement;
		permutationLength = other.permutationLength;
	}
	
	@Override
	public final BitVector createCandidateSolution() {
		return init.createCandidateSolution();
	}
	
	@Override
	public PermutationToBitVectorProblem split() {
		return new PermutationToBitVectorProblem(this);
	}
	
	/**
	 * Converts a BitVector to a Permutation. Assumes that the length of the BitVector bits
	 * is supportedBitVectorLength(), and behavior is undefined otherwise.
	 * @param bits The BitVector
	 * @return A Permutation derived from the BitVector
	 */
	public final Permutation toPermutation(BitVector bits) {
		Permutation p = new Permutation(permutationLength, 0);
		if (permutationLength > 1) {
			BitVector.BitIterator iter = bits.bitIterator(bitsPerElement);
			for (int remaining = permutationLength; remaining > 1; remaining--) {
				int j = permutationLength - remaining;
				int i = j + (iter.nextBitBlock() % remaining);
				if (i != j) {
					p.removeAndInsert(i, j);
				}
			}
		}
		return p;
	}
	
	/**
	 * Computes the length of BitVectors supported by this instance.
	 * @return the length of BitVectors supported by this instance
	 */
	public final int supportedBitVectorLength() {
		return bitsPerElement * (permutationLength - 1);
	}
	
	
	/**
	 * <p>This class implements a mapping between Permutation problems and BitVector problems, where cost values are
	 * of type double. This enables
	 * using {@link BitVector BitVector} search operators to solve problems defined over the space of
	 * {@link Permutation Permutation} objects. It can also be used as an {@link Initializer} of BitVectors
	 * by search algorithms to ensure that the search is using BitVectors of the appropriate length to represent
	 * permutations of the desired length for the problem you are solving. In fact, to ensure that your search
	 * is using the correct bit length, you should use this as your Initializer.</p>
	 *
	 * <p>The superclass, {@link PermutationToBitVectorProblem}, handles the transformation between BitVectors and
	 * Permutations. See that class's documentation for the details of how a BitVector is interpreted as a Permutation.</p>
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	public static final class DoubleCost extends PermutationToBitVectorProblem implements OptimizationProblem<BitVector> {
		
		private final OptimizationProblem<Permutation> problem;
		
		/**
		 * Initializes the mapping between Permutation problem and BitVector problem for a specific permutation length.
		 *
		 * @param problem The original Permutation problem.
		 *
		 * @param permutationLength The length of the permutations under optimization, in number of elements.
		 *     This is NOT the length of the BitVectors. For example, if the problem is the Traveling Salesperson,
		 *     and if the instance has 100 cities, then you would pass 100 for this parameter.
		 *
		 * @throws IllegalArgumentException if permutationLength is less than 1.
		 */
		public DoubleCost(OptimizationProblem<Permutation> problem, int permutationLength) {
			super(permutationLength);
			this.problem = problem;
		}
		
		/*
		 * package private for use by split
		 */
		DoubleCost(DoubleCost other) {
			super(other);
			
			// thread safe so just copy reference
			problem = other.problem;
		}
		
		@Override
		public DoubleCost split() {
			return new DoubleCost(this);
		}
		
		@Override
		public double cost(BitVector candidate) {
			return problem.cost(toPermutation(candidate));
		}
		
		@Override
		public double costAsDouble(BitVector candidate) {
			return problem.costAsDouble(toPermutation(candidate));
		}
		
		@Override
		public boolean isMinCost(double cost) {
			return problem.isMinCost(cost);
		}
		
		@Override
		public double minCost() {
			return problem.minCost();
		}
		
		@Override
		public double value(BitVector candidate) {
			return problem.value(toPermutation(candidate));
		}
	}
	
	/**
	 * <p>This class implements a mapping between Permutation problems and BitVector problems, where cost values are
	 * of type int. This enables
	 * using {@link BitVector BitVector} search operators to solve problems defined over the space of
	 * {@link Permutation Permutation} objects. It can also be used as an {@link Initializer} of BitVectors
	 * by search algorithms to ensure that the search is using BitVectors of the appropriate length to represent
	 * permutations of the desired length for the problem you are solving. In fact, to ensure that your search
	 * is using the correct bit length, you should use this as your Initializer.</p>
	 *
	 * <p>The superclass, {@link PermutationToBitVectorProblem}, handles the transformation between BitVectors and
	 * Permutations. See that class's documentation for the details of how a BitVector is interpreted as a Permutation.</p>
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	public static final class IntegerCost extends PermutationToBitVectorProblem implements IntegerCostOptimizationProblem<BitVector> {
		
		private final IntegerCostOptimizationProblem<Permutation> problem;
		
		/**
		 * Initializes the mapping between Permutation problem and BitVector problem for a specific permutation length.
		 *
		 * @param problem The original Permutation problem.
		 *
		 * @param permutationLength The length of the permutations under optimization, in number of elements.
		 *     This is NOT the length of the BitVectors. For example, if the problem is the Traveling Salesperson,
		 *     and if the instance has 100 cities, then you would pass 100 for this parameter.
		 *
		 * @throws IllegalArgumentException if permutationLength is less than 1.
		 */
		public IntegerCost(IntegerCostOptimizationProblem<Permutation> problem, int permutationLength) {
			super(permutationLength);
			this.problem = problem;
		}
		
		/*
		 * package private for use by split
		 */
		IntegerCost(IntegerCost other) {
			super(other);
			
			// thread safe so just copy reference
			problem = other.problem;
		}
		
		@Override
		public IntegerCost split() {
			return new IntegerCost(this);
		}
		
		@Override
		public int cost(BitVector candidate) {
			return problem.cost(toPermutation(candidate));
		}
		
		@Override
		public double costAsDouble(BitVector candidate) {
			return problem.costAsDouble(toPermutation(candidate));
		}
		
		@Override
		public boolean isMinCost(int cost) {
			return problem.isMinCost(cost);
		}
		
		@Override
		public int minCost() {
			return problem.minCost();
		}
		
		@Override
		public int value(BitVector candidate) {
			return problem.value(toPermutation(candidate));
		}
	}
}
