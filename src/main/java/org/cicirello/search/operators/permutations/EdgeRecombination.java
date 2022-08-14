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

import org.cicirello.search.operators.CrossoverOperator;
import org.cicirello.permutations.Permutation;
import org.cicirello.math.rand.RandomIndexer;
import java.util.Arrays;

/**
 * <p>Implementation of the Edge Recombination operator, a crossover operator for permutations.
 * Edge Recombination assumes that the permutations represent a cyclic sequence of edges. That is, if
 * 3 follows 5 in the permutation, then that corresponds to an undirected edge between 3 and 5.
 * Given this assumption, it is suitable for problems where permutations do represent a sequence of
 * edges such as the traveling salesperson. However, the Chips-n-Salsa library does not limit its
 * use to such problems, and you can use it on any problem with solutions represented as permutations.</p>
 *
 * <p>Imagine a hypothetical graph consisting of the n elements of a permutation of length n as the n
 * vertexes of the graph. The edge set begins with the n adjacent pairs from parent p1. For example, if 
 * p1 = [3, 0, 2, 1, 4], then the edge set of this graph is initialized with the undirected edges: (3, 0), (0, 2),
 * (2, 1), (1, 4), and (4, 3). Now add to that edge set any edges, determined in a similar way, from parent p2,
 * provided it doesn't already contain the relevant edge. Consider p2 = [4, 3, 2, 1, 0]. Thus, we would add (3, 2), 
 * (1, 0),and (0, 4) to get an undirected edge set of: { (3, 0), (0, 2), (2, 1), (1, 4), (4, 3), (3, 2), (1, 0), (0, 4) }. 
 * Child c1 is initialized with the first element of parent p1, in this example p1 = [3]. We then examine the adjacent vertexes
 * to the most recently added element. The 3 is adjacent to 0, 4, and 2. We'll pick one of these to add in the next
 * spot of the permutation. We'll pick the one that is adjacent to the fewest elements not yet used. 0 is adjacent to 2, 1, and 4.
 * 4 is adjacent to 1 and 0. 2 is adjacent to 0 and 1. When there is a tie, such as here with the 2 and 4, the tie is broken
 * at random. Imagine that the random tie breaker gave us 4. We now have p1 = [3, 4]. We now consider the adjacent elements of
 * 4 that are not yet in the permutation, which in this case is 0 and 1. We pick the one with the fewest adjacent elements
 * not yet in the permutation. The 0 is adjacent to 1 and 2. The 1 is adjacent to 0 and 2. Since we have a tie, we pick randomly.
 * Consider for the example that the random choice have us 1. We now have p1 = [3, 4, 1]. We now examine the adjacent elements of 1
 * that are not yet in the permutation. The 1 is adjacent to 0 and 2. We pick the one that is adjacent to the fewest not yet used 
 * elements. They are the only two remaining and they are adjacent to each other. We thus pick randomly. Consider that the random
 * element is 0, and we now have p1 = [3, 4, 1, 0]. And at this point, there is only one element left, so the final permutation is
 * p1 = [3, 4, 1, 0, 2]. We can form the other child in a similar way, but initialized with the first element of the other parent.
 * </p>
 *
 * <p>The Edge Recombination operator uses a special data structure that its creators, Whitley et al, call an edge map
 * for efficient implementation.</p>
 *
 * <p>The worst case runtime of a call to {@link #cross cross} is O(n), where n is the length of the
 * permutations.</p>
 *
 * <p>The edge recombination operator was introduced in the following paper:<br>
 * D. Whitley, T. Starkweather, and D. Fuquay. Scheduling Problems and Traveling Salesmen: The Genetic
 * Edge Recombination Operator. <i>Proceedings of the International Conference on 
 * Genetic Algorithms</i>, 1989, pp. 133-140.</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class EdgeRecombination implements CrossoverOperator<Permutation> {
	
	/**
	 * Constructs a edge recombination operator.
	 */
	public EdgeRecombination() { }
	
	@Override
	public void cross(Permutation c1, Permutation c2) {
		if (c1.length() <= 1) return;
		c1.apply( 
			(raw1, raw2) -> {
				EdgeMap map = new EdgeMap(raw1, raw2);
				build(raw1, new EdgeMap(map));
				build(raw2, map);
			},
			c2
		);
	}
	
	@Override
	public EdgeRecombination split() {
		// doesn't maintain any state, so safe to return this
		return this;
	}
	
	private void build(int[] raw, EdgeMap map) {
		// 0th element is as in parent, so start iteration at 1.
		for (int i = 1; i < raw.length; i++) {
			// 1. record that we used raw[i-1]
			map.used(raw[i-1]);
			// 2. pick an adjacent element of raw[i-1] and add to raw[i]
			raw[i] = map.pick(raw[i-1]);
		}
	}
	
	final static class EdgeMap {
		
		final int[][] adj;
		final int[] count;
		final boolean[] done;
		
		/*
		 * Assumes length is greater than 1
		 */
		EdgeMap(int[] raw1, int[] raw2) {
			adj = new int[raw1.length][4];
			count = new int[raw1.length];
			done = new boolean[raw1.length];
			boolean[][] in = new boolean[raw1.length][raw1.length];
			adj[raw1[0]][0] = raw1[raw1.length-1];
			in[raw1[0]][raw1[raw1.length-1]] = true;
			for (int i = 1; i < raw1.length; i++) {
				adj[raw1[i]][0] = raw1[i-1]; 
				in[raw1[i]][raw1[i-1]] = true;
			}
			if (raw1.length <= 2) {
				Arrays.fill(count, 1);
			} else {
				Arrays.fill(count, 2);
				adj[raw1[raw1.length-1]][1] = raw1[0];
				in[raw1[raw1.length-1]][raw1[0]] = true;
				for (int i = 1; i < raw1.length; i++) {
					adj[raw1[i-1]][1] = raw1[i]; 
					in[raw1[i-1]][raw1[i]] = true;
				}
				if (!in[raw2[0]][raw2[raw2.length-1]]) {
					adj[raw2[0]][count[raw2[0]]] = raw2[raw2.length-1];
					in[raw2[0]][raw2[raw2.length-1]] = true;
					count[raw2[0]]++;
				}
				if (!in[raw2[raw2.length-1]][raw2[0]]) {
					adj[raw2[raw2.length-1]][count[raw2[raw2.length-1]]] = raw2[0];
					in[raw2[raw2.length-1]][raw2[0]] = true;
					count[raw2[raw2.length-1]]++;
				}
				for (int i = 1; i < raw2.length; i++) {
					if(!in[raw2[i]][raw2[i-1]]) {
						adj[raw2[i]][count[raw2[i]]] = raw2[i-1]; 
						in[raw2[i]][raw2[i-1]] = true;
						count[raw2[i]]++;
					}
					if(!in[raw2[i-1]][raw2[i]]) {
						adj[raw2[i-1]][count[raw2[i-1]]] = raw2[i]; 
						in[raw2[i-1]][raw2[i]] = true;
						count[raw2[i-1]]++;
					}
				}
			}
		}
		
		EdgeMap(EdgeMap other) {
			count = other.count.clone();
			// deliberately not cloning done... this copy constructor
			// only used on the initial EdgeMap, so nothing done
			done = new boolean[count.length];
			adj = new int[other.adj.length][];
			for (int i = 0; i < adj.length; i++) {
				adj[i] = other.adj[i].clone();
			}
		}
		
		final int pick(int from) {
			if (count[from] == 1) {
				return adj[from][0];
			}
			if (count[from] > 0) {
				int[] minIndexes = new int[4];
				int num = 1;
				for (int i = 1; i < count[from]; i++) {
					if (count[adj[from][i]] < count[adj[from][minIndexes[0]]]) {
						minIndexes[0] = i;
						num = 1;
					} else if (count[adj[from][i]] == count[adj[from][minIndexes[0]]]) {
						minIndexes[num] = i;
						num++;
					}
				}
				if (num > 1) {
					// The num can be at most 3, so nextBiasedInt's lack of rejection sampling
					// should introduce an extremely negligible bias away from uniformity.
					return adj[from][minIndexes[RandomIndexer.nextBiasedInt(num)]];
				}
				return adj[from][minIndexes[0]];
			}
			// IS IT POSSIBLE TO GET HERE?
			// IS IT POSSIBLE FOR NONE AVAILABLE?
			// IF NOT, THEN ABOVE IF STATEMENT NOT NEEDED AND CAN JUST DO THE BLOCK.
			// ALSO WOULDN'T NEED THE DONE ARRAY AT ALL.
			// NOTE: Test cases include unit tests of this specific method that include
			// an extra call after the permutation is complete to artificially create a
			// scenario that ends up here. Try to confirm if a real scenario exists.
			return anyRemaining();
		}
		
		final void used(int element) {
			for (int i = 0; i < count[element]; i++) {
				remove(adj[element][i], element);
			}
			done[element] = true;
		}
		
		final void remove(int list, int element) {
			int i = 0;
			// guaranteed to be in list
			while (adj[list][i] != element) {
				i++;
			}
			count[list]--;
			adj[list][i] = adj[list][count[list]];
		}
		
		final int anyRemaining() {
			int[] minIndexes = new int[adj.length];
			int num = 0;
			for (int i = 0; i < done.length; i++) {
				if (!done[i]) {
					if (num == 0) {
						minIndexes[0] = i;
						num = 1;
					} else if (count[i] == count[minIndexes[0]]) {
						minIndexes[num] = i;
						num++;
					} else if (count[i] < count[minIndexes[0]]) {
						minIndexes[0] = i;
						num = 1;
					}
				}
			}
			if (num > 1) {
				// The num should be very small, so nextBiasedInt's lack of rejection sampling
				// should introduce an extremely negligible bias away from uniformity. In fact, this
				// case is believed extremely statistically rare.
				return minIndexes[RandomIndexer.nextBiasedInt(num)];
			} 
			if (num == 1) {
				return minIndexes[0];
			}
			return -1;
		}
	}
}
