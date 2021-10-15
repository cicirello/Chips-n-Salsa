/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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
 
package org.cicirello.search.problems.tsp;

import java.util.SplittableRandom;
import org.cicirello.search.problems.OptimizationProblem;
import org.cicirello.search.problems.IntegerCostOptimizationProblem;
import org.cicirello.permutations.Permutation;

/**
 * <p>This class and its nested classes implement the Traveling Salesperson Problem (TSP).
 * It provides two inner classes where edge weights are computed as needed, one for edge 
 * costs that are floating-point valued (class {@link Double}), and
 * one for integer cost edges (class {@link Integer}).</p>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public abstract class TSP {
	
	/* These are package-private to provide access to the static inner classes. */
	final double[] x;
	final double[] y;
	final TSPEdgeDistance d;
	
	/* package-private constructor */
	TSP(int n, double w, SplittableRandom gen) {
		this(
			n,
			w,
			(x1, y1, x2, y2) -> {
				double deltaX = x1 - x2;
				double deltaY = y1 - y2;
				return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
			},
			gen
		);
	}
	
	/* package-private constructor */
	TSP(int n, double w, TSPEdgeDistance distance, SplittableRandom gen) {
		if (n < 2) {
			throw new IllegalArgumentException("Must be at least 2 cities.");
		}
		if (w <= 0.0) {
			throw new IllegalArgumentException("Width of region must be positive.");
		}
		x = new double[n];
		y = new double[n];
		for (int i = 0; i < n; i++) {
			x[i] = gen.nextDouble(w);
			y[i] = gen.nextDouble(w);
		}
		d = distance;
	}
	
	/**
	 * Gets the number of cities in the TSP instance.
	 * @return number of cities
	 */
	public final int length() {
		return x.length;
	}
	
	/**
	 * Gets the x coordinate of a city.
	 * @param i The city index.
	 * @return the x coordinate of city i.
	 * @throws NullPointerException if i &lt; 0 or i &ge; length().
	 */
	public final double getX(int i) {
		return x[i];
	}
	
	/**
	 * Gets the y coordinate of a city.
	 * @param i The city index.
	 * @return the y coordinate of city i.
	 * @throws NullPointerException if i &lt; 0 or i &ge; length().
	 */
	public final double getY(int i) {
		return y[i];
	}
		
	/**
	 * <p>Cost function for the Traveling Salesperson Problem (TSP), where edge costs 
	 * are floating-point valued. This implementation only requires linear memory, but
	 * must recompute an edge cost every time it is needed. If your instance of the TSP is
	 * small enough to afford quadratic memory, then you may prefer the {@link TSP.DoubleMatrix}
	 * class which precomputes all edge costs between all pairs of cities.</p>
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	public final static class Double extends TSP implements OptimizationProblem<Permutation> {
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. The edge cost of a pair of cities is the Euclidean distance
		 * between them.
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public Double(int n, double w) {
			super(n, w, new SplittableRandom());
		}
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. 
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param distance The distance function to use for the edge costs.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public Double(int n, double w, TSPEdgeDistance distance) {
			super(n, w, distance, new SplittableRandom());
		}
			
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. The edge cost of a pair of cities is the Euclidean distance
		 * between them.
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param seed The seed for the random number generator to enable reproducing the
		 * same instance for experiment reproducibility.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public Double(int n, double w, long seed) {
			super(n, w, new SplittableRandom(seed));
		}
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. 
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param distance The distance function to use for the edge costs.
		 * @param seed The seed for the random number generator to enable reproducing the
		 * same instance for experiment reproducibility.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public Double(int n, double w, TSPEdgeDistance distance, long seed) {
			super(n, w, distance, new SplittableRandom(seed));
		}
		
		@Override
		public double cost(Permutation candidate) {
			if (candidate.length() != x.length) {
				throw new IllegalArgumentException("Permutation must be same length as number of cities.");
			}
			int i = candidate.get(0);
			int j = candidate.get(candidate.length()-1);
			double total = d.distance(x[i], y[i], x[j], y[j]);
			for (int k = 1; k < candidate.length(); k++) {
				i = candidate.get(k);
				j = candidate.get(k-1);
				total = total + d.distance(x[i], y[i], x[j], y[j]);
			}
			return total;
		}
		
		@Override
		public double value(Permutation candidate) {
			return cost(candidate);
		}
	}
	
	/**
	 * Cost function for the Traveling Salesperson Problem (TSP), where edge costs 
	 * are integer valued.
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	public final static class Integer extends TSP implements IntegerCostOptimizationProblem<Permutation> {
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. The edge cost of a pair of cities is the Euclidean distance
		 * between them rounded to the nearest integer.
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public Integer(int n, double w) {
			super(n, w, new SplittableRandom());
		}
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. 
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param distance The distance function to use for the edge costs.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public Integer(int n, double w, TSPEdgeDistance distance) {
			super(n, w, distance, new SplittableRandom());
		}
			
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. The edge cost of a pair of cities is the Euclidean distance
		 * between them rounded to the nearest integer.
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param seed The seed for the random number generator to enable reproducing the
		 * same instance for experiment reproducibility.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public Integer(int n, double w, long seed) {
			super(n, w, new SplittableRandom(seed));
		}
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. 
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param distance The distance function to use for the edge costs.
		 * @param seed The seed for the random number generator to enable reproducing the
		 * same instance for experiment reproducibility.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public Integer(int n, double w, TSPEdgeDistance distance, long seed) {
			super(n, w, distance, new SplittableRandom(seed));
		}
		
		@Override
		public int cost(Permutation candidate) {
			if (candidate.length() != x.length) {
				throw new IllegalArgumentException("Permutation must be same length as number of cities.");
			}
			int i = candidate.get(0);
			int j = candidate.get(candidate.length()-1);
			int total = d.distanceAsInt(x[i], y[i], x[j], y[j]);
			for (int k = 1; k < candidate.length(); k++) {
				i = candidate.get(k);
				j = candidate.get(k-1);
				total = total + d.distanceAsInt(x[i], y[i], x[j], y[j]);
			}
			return total;
		}
		
		@Override
		public int value(Permutation candidate) {
			return cost(candidate);
		}
	}
	
	/**
	 * <p>Cost function for the Traveling Salesperson Problem (TSP), where edge costs 
	 * are floating-point valued, and where all edge costs between pairs of cities
	 * are precomputed. This implementation requires quadratic memory, and may be prohibitive
	 * for large instances, in which case you may prefer to use the {@link TSP.Double} class,
	 * that only requires linear memory but recomputes an edge cost every time it is needed.</p>
	 *
	 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
	 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
	 */
	public final static class DoubleMatrix extends TSP implements OptimizationProblem<Permutation> {
		
		private final double[][] weights;
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. The edge cost of a pair of cities is the Euclidean distance
		 * between them.
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public DoubleMatrix(int n, double w) {
			super(n, w, new SplittableRandom());
			weights = computeWeights();
		}
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. 
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param distance The distance function to use for the edge costs.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public DoubleMatrix(int n, double w, TSPEdgeDistance distance) {
			super(n, w, distance, new SplittableRandom());
			weights = computeWeights();
		}
			
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. The edge cost of a pair of cities is the Euclidean distance
		 * between them.
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param seed The seed for the random number generator to enable reproducing the
		 * same instance for experiment reproducibility.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public DoubleMatrix(int n, double w, long seed) {
			super(n, w, new SplittableRandom(seed));
			weights = computeWeights();
		}
		
		/**
		 * Constructs a random TSP instance with cities randomly distributed within
		 * a square region. 
		 * @param n The number of cities.
		 * @param w The width (and height) of a square region containing the cities.
		 * @param distance The distance function to use for the edge costs.
		 * @param seed The seed for the random number generator to enable reproducing the
		 * same instance for experiment reproducibility.
		 * @throws IllegalArgumentException if n &lt; 2.
		 * @throws IllegalArgumentException if w &#x2264; 0.0.
		 */
		public DoubleMatrix(int n, double w, TSPEdgeDistance distance, long seed) {
			super(n, w, distance, new SplittableRandom(seed));
			weights = computeWeights();
		}
		
		@Override
		public double cost(Permutation candidate) {
			if (candidate.length() != x.length) {
				throw new IllegalArgumentException("Permutation must be same length as number of cities.");
			}
			double total = weights[candidate.get(0)][candidate.get(candidate.length()-1)];
			for (int k = 1; k < candidate.length(); k++) {
				total = total + weights[candidate.get(k)][candidate.get(k-1)];
			}
			return total;
		}
		
		@Override
		public double value(Permutation candidate) {
			return cost(candidate);
		}
		
		private double[][] computeWeights() {
			double[][] w = new double[x.length][x.length];
			for (int i = 0; i < w.length; i++) {
				for (int j = i+1; j < w.length; j++) {
					w[i][j] = w[j][i] = d.distance(x[i], y[i], x[j], y[j]);
				}
			}
			return w;
		}
	}
	
}
