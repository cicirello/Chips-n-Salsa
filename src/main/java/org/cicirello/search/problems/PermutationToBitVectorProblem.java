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
	public BitVector createCandidateSolution() {
		return init.createCandidateSolution();
	}
	
	@Override
	public PermutationToBitVectorProblem split() {
		return new PermutationToBitVectorProblem(this);
	}
	
	/**
	 * Converts a BitVector to a Permutation.
	 * @param bits The BitVector
	 * @return A Permutation derived from the BitVector
	 */
	public Permutation toPermutation(BitVector bits) {
		// TO DO
		return null;
	}
	
	
	
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
		public SolutionCostPair<BitVector> getSolutionCostPair(BitVector candidate) {
			double c = cost(candidate);
			return new SolutionCostPair<BitVector>(candidate, c, isMinCost(c));
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
		public SolutionCostPair<BitVector> getSolutionCostPair(BitVector candidate) {
			int c = cost(candidate);
			return new SolutionCostPair<BitVector>(candidate, c, isMinCost(c));
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
