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
import org.cicirello.search.representations.BitVector;
import java.util.SplittableRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class is an implementation of the Largest Common Subgraph
 * problem, an NP-Hard combinatorial optimization problem. In the problem,
 * we are given two graphs G<sub>1</sub> and G<sub>2</sub>. The problem is to
 * find the largest graph G<sub>c</sub>, measured in number of edges,
 * that is isomorphic to a subgraph of G<sub>1</sub> and a subgraph of G<sub>2</sub>.</p>
 *
 * <p>The {@link #value value} method computes the actual optimization objective,
 * number of edges in common, which we must maximize. The {@link #cost cost} method
 * transforms this to a minimization problem by subtracting that count from 
 * the number of edges in the smaller of G<sub>1</sub> and G<sub>2</sub> (in terms of
 * edges).</p>
 *
 * <p>This implementation assumes representing solutions with a Permutation used to
 * represent a mapping between the vertexes of the two graphs. Specifically, holding
 * the vertexes of the graph with the fewer number of vertexes in a fixed order
 * by their vertex id, a solution to the problem is represented with a Permutation of
 * the vertex ids of the other graph. Position in the permutation corresponds to the
 * mapping. For example, if p(i) = j, then vertex i in G<sub>1</sub> is mapped to
 * vertex j in G<sub>2</sub>. This assumes that G<sub>1</sub> has at most the number
 * of vertexes as G<sub>2</sub>.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class LargestCommonSubgraph implements IntegerCostOptimizationProblem<Permutation> {
	
	private final ArrayList<InternalEdge> edgesG1;
	private final BitVector[] adjacencyMatrixG2;
	private int bound;
	
	/**
	 * Constructs a random instance of the largest common subgraph problem.
	 *
	 * @param v The number of vertexes of each graph.
	 * @param density The density of each graph, which is the probability of an edge existing between 
	 * a pair of vertexes. It must be in the interval [0.0, 1.0].
	 * @param isomorphic If true, the two graphs will be isomorphic, which provides an easy way
	 * of generating instances with a known optimal solution.
	 *
	 * @throws IllegalArgumentException if v  is less than 1.
	 * @throws IllegalArgumentException if density is less than 0.0 or greater than 1.0.
	 */
	public LargestCommonSubgraph(int v, double density, boolean isomorphic) {
		this(v);
		if (isomorphic) {
			createIsomorphicRandomInstanceData(v, density, new SplittableRandom());
		} else {
			createRandomInstanceData(v, v, density, density, new SplittableRandom());
		}
	}
	
	/**
	 * Constructs a random instance of the largest common subgraph problem.
	 *
	 * @param v1 The number of vertexes of graph 1.
	 * @param v2 The number of vertexes of graph 2.
	 * @param density1 The density of graph 1, which is the probability of an edge existing between 
	 * a pair of vertexes. It must be in the interval [0.0, 1.0].
	 * @param density2 The density of graph 2, which is the probability of an edge existing between 
	 * a pair of vertexes. It must be in the interval [0.0, 1.0].
	 *
	 * @throws IllegalArgumentException if v1 and/or v2 is less than 1.
	 * @throws IllegalArgumentException if either density1 or density2 is less than 0.0 or greater than 1.0.
	 */
	public LargestCommonSubgraph(int v1, int v2, double density1, double density2) {
		this(v2 > v1 ? v2 : v1);
		if (v1 < v2 || (v1 == v2 && density1 <= density2)) {
			createRandomInstanceData(v1, v2, density1, density2, new SplittableRandom());
		} else {
			createRandomInstanceData(v2, v1, density2, density1, new SplittableRandom());
		}
	}
	
	/**
	 * Constructs a random instance of the largest common subgraph problem.
	 *
	 * @param v The number of vertexes of each graph.
	 * @param density The density of each graph, which is the probability of an edge existing between 
	 * a pair of vertexes. It must be in the interval [0.0, 1.0].
	 * @param isomorphic If true, the two graphs will be isomorphic, which provides an easy way
	 * of generating instances with a known optimal solution.
	 * @param seed The seed for the random number generator to enable replicating an instance.
	 *
	 * @throws IllegalArgumentException if v  is less than 1.
	 * @throws IllegalArgumentException if density is less than 0.0 or greater than 1.0.
	 */
	public LargestCommonSubgraph(int v, double density, boolean isomorphic, long seed) {
		this(v);
		if (isomorphic) {
			createIsomorphicRandomInstanceData(v, density, new SplittableRandom(seed));
		} else {
			createRandomInstanceData(v, v, density, density, new SplittableRandom(seed));
		}
	}
	
	/**
	 * Constructs a random instance of the largest common subgraph problem.
	 *
	 * @param v1 The number of vertexes of graph 1.
	 * @param v2 The number of vertexes of graph 2.
	 * @param density1 The density of graph 1, which is the probability of an edge existing between 
	 * a pair of vertexes. It must be in the interval [0.0, 1.0].
	 * @param density2 The density of graph 2, which is the probability of an edge existing between 
	 * a pair of vertexes. It must be in the interval [0.0, 1.0].
	 * @param seed The seed for the random number generator to enable replicating an instance.
	 *
	 * @throws IllegalArgumentException if v1 and/or v2 is less than 1.
	 * @throws IllegalArgumentException if either density1 or density2 is less than 0.0 or greater than 1.0.
	 */
	public LargestCommonSubgraph(int v1, int v2, double density1, double density2, long seed) {
		this(v2 > v1 ? v2 : v1);
		if (v1 < v2 || (v1 == v2 && density1 <= density2)) {
			createRandomInstanceData(v1, v2, density1, density2, new SplittableRandom(seed));
		} else {
			createRandomInstanceData(v2, v1, density2, density1, new SplittableRandom(seed));
		}
	}
	
	/**
	 * Constructs a random instance of the largest common subgraph problem.
	 *
	 * @param v1 The number of vertexes of graph 1.
	 * @param v2 The number of vertexes of graph 2.
	 * @param edges1 A list of the edges for graph 1. Each of the 2 endpoints of each
	 *        edge in edges1 must be in the interval [0, v1). This list must not contain duplicate
	 *        edges, and also must not contain both (a, b) and (b, a) since these are the same edge.
	 *        The behavior of this class is undefined if either of these are violated.
	 * @param edges2 A list of the edges for graph 2. Each of the 2 endpoints of each
	 *        edge in edges2 must be in the interval [0, v2). This list must not contain duplicate
	 *        edges, and also must not contain both (a, b) and (b, a) since these are the same edge.
	 *        The behavior of this class is undefined if either of these are violated.
	 *
	 * @throws IllegalArgumentException if v1 and/or v2 is less than 1.
	 * @throws IllegalArgumentException if any of the endpoints of the edges in edges1 or edges2
	 * are out of bounds for the corresponding graph.
	 */
	public LargestCommonSubgraph(int v1, int v2, List<Edge> edges1, List<Edge> edges2) {
		this(v2 > v1 ? v2 : v1);
		if (v1 < v2 || (v1 == v2 && edges1.size() <= edges2.size())) {
			initializeInstanceData(v1, v2, edges1, edges2);
		} else {
			initializeInstanceData(v2, v1, edges2, edges1);
		}
	}
	
	private LargestCommonSubgraph(int largerV) {
		edgesG1 = new ArrayList<InternalEdge>();
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
		return bound - value(candidate);
	}
	
	@Override
	public int value(Permutation candidate) {
		int count = 0;
		for (InternalEdge e : edgesG1) {
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
	
	/**
	 * Determines an upper bound on the possible size in number of edges
	 * of the largest common subgraph. This is simply the smaller of the number
	 * of edges of the two graphs. Note it may or may not be possible to actually find
	 * a common subgraph with this number of edges. It is simply an upper bound.
	 *
	 * @return the minimum of the number of edges in graph 1 and graph 2.
	 */
	public int maxValue() {
		return bound;
	}
	
	/*
	 * package private for testing
	 */
	final boolean hasEdge1(int u, int v) {
		for (InternalEdge e : edgesG1) {
			if (e.x == u && e.y == v || e.x == v && e.y == u) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * package private for testing
	 */
	final boolean hasEdge2(int u, int v) {
		return adjacencyMatrixG2[u].isOne(v);
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
					edgesG1.add(new InternalEdge(i, j));
					adjacencyMatrixG2[perm.get(i)].flip(perm.get(j));
					adjacencyMatrixG2[perm.get(j)].flip(perm.get(i));
				}
			}
		}
		bound = edgesG1.size();
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
					edgesG1.add(new InternalEdge(i, j));
				}
			}
		}
		for (int i = 0; i < v2; i++) {
			adjacencyMatrixG2[i] = new BitVector(v2);
		}
		bound = 0;
		for (int i = 0; i < v2; i++) {
			for (int j = i+1; j < v2; j++) {
				if (gen.nextDouble() < density2) {
					adjacencyMatrixG2[i].flip(j);
					adjacencyMatrixG2[j].flip(i);
					bound++;
				}
			}
		}
		if (edgesG1.size() < bound) {
			bound = edgesG1.size();
		}
	}
	
	private void initializeInstanceData(int v1, int v2, List<Edge> edges1, List<Edge> edges2) {
		if (v1 <= 0) {
			throw new IllegalArgumentException("Graphs must have at least 1 vertex.");
		}
		for (Edge e : edges1) {
			if (e.u >= v1 || e.v >= v1) {
				throw new IllegalArgumentException("Edge endpoint out of bounds.");
			}
			edgesG1.add(new InternalEdge(e));
		}
		for (int i = 0; i < v2; i++) {
			adjacencyMatrixG2[i] = new BitVector(v2);
		}
		for (Edge e : edges2) {
			if (e.u >= v2 || e.v >= v2) {
				throw new IllegalArgumentException("Edge endpoint out of bounds.");
			}
			adjacencyMatrixG2[e.u].flip(e.v);
			adjacencyMatrixG2[e.v].flip(e.u);
		}
		bound = edges1.size() <= edges2.size() ? edges1.size() : edges2.size();
	}
	
	/*
	 * Private internal class for use within the LargestCommonSubgraph class for representing
	 * edges.
	 */
	private class InternalEdge {
		private final int x;
		private final int y;
		
		private InternalEdge(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		private InternalEdge(Edge e) {
			this.x = e.u;
			this.y = e.v;
		}
	}
	
	/**
	 * <p>This class is used to represent edges when specifying
	 * instances of the {@link LargestCommonSubgraph} problem.
	 * Instances of this class are immutable. The edges are undirected.</p>
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	public static final class Edge {
		
		private final int u;
		private final int v;
		
		/**
		 * Constructs an undirected edge.
		 * @param u An endpoint of the edge.
		 * @param v The other endpoint of the edge.
		 */
		public Edge(int u, int v) {
			this.u = u;
			this.v = v;
		}
		
		/**
		 * Gets one endpoint of the edge. The edge is undirected,
		 * so there is no meaning behind which endpoint is which.
		 * Use the {@link getV} method to get the other endpoint.
		 *
		 * @return one of the endpoints 
		 */
		public int getU() {
			return u;
		}
		
		/**
		 * Gets one endpoint of the edge. The edge is undirected,
		 * so there is no meaning behind which endpoint is which.
		 * Use the {@link getU} method to get the other endpoint.
		 *
		 * @return one of the endpoints 
		 */
		public int getV() {
			return v;
		}
	}
}
