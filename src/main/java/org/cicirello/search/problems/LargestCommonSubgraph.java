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

import org.cicirello.permutations.Permutation;
import org.cicirello.representations.BitVector;
import java.util.SplittableRandom;
import java.util.ArrayList;



public final class LargestCommonSubgraph implements IntegerCostOptimizationProblem<Permutation> {
	
	private final ArrayList<Edge> edgesG1;
	private final BitVector[] adjacencyMatrixG2;
	
	public LargestCommonSubgraph(int v, double density, boolean isomorphic) {
		this(v);
		if (isomorphic) {
			createIsomorphicRandomInstanceData(v, density, new SplittableRandom());
		} else {
			createRandomInstanceData(v, v, density, density, new SplittableRandom());
		}
	}
	
	public LargestCommonSubgraph(int v1, int v2, double density1, double density2) {
		this(v2 > v1 ? v2 : v1);
		if (v1 < v2 || (v1 == v2 && density1 <= density2)) {
			createRandomInstanceData(v1, v2, density1, density2, new SplittableRandom());
		} else {
			createRandomInstanceData(v2, v1, density2, density1, new SplittableRandom());
		}
	}
	
	public LargestCommonSubgraph(int v, double density, boolean isomorphic, long seed) {
		this(v);
		if (isomorphic) {
			createIsomorphicRandomInstanceData(v, density, new SplittableRandom(seed));
		} else {
			createRandomInstanceData(v, v, density, density, new SplittableRandom(seed));
		}
	}
	
	public LargestCommonSubgraph(int v1, int v2, double density1, double density2, long seed) {
		this(v2 > v1 ? v2 : v1);
		if (v1 < v2 || (v1 == v2 && density1 <= density2)) {
			createRandomInstanceData(v1, v2, density1, density2, new SplittableRandom(seed));
		} else {
			createRandomInstanceData(v2, v1, density2, density1, new SplittableRandom(seed));
		}
	}
	
	private LargestCommonSubgraph(int largerV) {
		edgesG1 = new ArrayList<Edge>();
		adjacencyMatrixG2 = new BitVector[largerV];
	}
	
	/**
	 * Gets the size of the instance as the number of vertexes in the larger graph.
	 * This is the required permutation length.
	 *
	 * @return the number of vertexes in the larger of the two graphs.
	 */
	public int size() {
		return adjacencyMatrixG2.length;
	}
	
	@Override
	public int cost(Permutation candidate) {
		return edgesG1.size() - value(candidate)
	}
	
	@Override
	public int value(Permutation candidate) {
		int count = 0;
		for (Edge e : edgesG1) {
			if (adjacencyMatrixG2[candidate.get(e.x)].isOne(candidate.get(e.y))) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public int minCost() {
		return 0;
	}
	
	private void createIsomorphicRandomInstanceData(int v, double density, SplittableRandom gen) {
		if (v <= 0) {
			throw new IllegalArgumentException("Graphs must have at least 1 vertex.");
		}
		if (density < 0.0) {
			throw new IllegalArgumentException("The graph density must be non-negative.");
		}
		if (density > 1.0) {
			throw new IllegalArgumentException("The graph density must be no greater than 1.0.");
		}
		for (int i = 0; i < v; i++) {
			adjacencyMatrixG2[i] = new BitVector(v);
		}
		Permutation perm = new Permutation(v, gen);
		for (int i = 0; i < v; i++) {
			for (int j = i+1; j < v; j++) {
				if (gen.nextDouble() < density) {
					edgesG1.add(new Edge(i, j));
					adjacencyMatrixG2[perm.get(i)].flip(perm.get(j));
					adjacencyMatrixG2[perm.get(j)].flip(perm.get(i));
				}
			}
		}
	}
	
	private void createRandomInstanceData(int v1, int v2, double density1, double density2, SplittableRandom gen) {
		if (v1 <= 0) {
			throw new IllegalArgumentException("Graphs must have at least 1 vertex.");
		}
		if (density1 < 0.0 || density2 < 0.0) {
			throw new IllegalArgumentException("The graph density must be non-negative.");
		}
		if (density1 > 1.0 || density2 > 1.0) {
			throw new IllegalArgumentException("The graph density must be no greater than 1.0.");
		}
		for (int i = 0; i < v1; i++) {
			for (int j = i+1; j < v1; j++) {
				if (gen.nextDouble() < density1) {
					edgesG1.add(new Edge(i, j));
				}
			}
		}
		for (int i = 0; i < v2; i++) {
			adjacencyMatrixG2[i] = new BitVector(v2);
		}
		for (int i = 0; i < v2; i++) {
			for (int j = i+1; j < v2; j++) {
				if (gen.nextDouble() < density1) {
					adjacencyMatrixG2[i].flip(j);
					adjacencyMatrixG2[j].flip(i);
				}
			}
		}
	}
	
	private class Edge {
		private int x;
		private int y;
		
		private Edge(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
