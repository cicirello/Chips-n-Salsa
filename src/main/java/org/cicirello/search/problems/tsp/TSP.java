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

/**
 *
 *
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public abstract class TSP {
	
	private final double[] x;
	private final double[] y;
	private final TSPEdgeDistance d;
	
	/**
	 * Constructs a random TSP instance with cities randomly distributed within
	 * a square region. The edge cost of a pair of cities is the Euclidean distance
	 * between them.
	 * @param n The number of cities.
	 * @param w The width (and height) of a square region containing the cities.
	 * @throws IllegalArgumentException if n &lt; 2.
	 * @throws IllegalArgumentException if w &#x2264; 0.0.
	 */
	public TSP(int n, double w) {
		this(n, w, new SplittableRandom());
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
	public TSP(int n, double w, TSPEdgeDistance distance) {
		this(n, w, distance, new SplittableRandom());
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
	public TSP(int n, double w, long seed) {
		this(n, w, new SplittableRandom(seed));
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
	public TSP(int n, double w, TSPEdgeDistance distance, long seed) {
		this(n, w, distance, new SplittableRandom(seed));
	}
	
	private TSP(int n, double w, SplittableRandom gen) {
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
	
	private TSP(int n, double w, TSPEdgeDistance distance, SplittableRandom gen) {
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
	 * Computes the distance between two cities in the TSP.
	 * @param i The id of the first city.
	 * @param j The id of the second city.
	 * @return The distance between the two cities.
	 * @throws NullPointerException if i &lt; 0 or j &lt; 0 or i &ge; length() or j &ge; length().
	 */
	public final double distance(int i, int j) {
		return d.distance(x[i], y[i], x[j], y[j]);
	}
	
	/**
	 * Computes the distance between two cities in the TSP.
	 * @param i The id of the first city.
	 * @param j The id of the second city.
	 * @return The distance between the two cities as an integer.
	 * @throws NullPointerException if i &lt; 0 or j &lt; 0 or i &ge; length() or j &ge; length().
	 */
	public final double distanceAsInt(int i, int j) {
		return d.distanceAsInt(x[i], y[i], x[j], y[j]);
	}
	
	/**
	 * Gets the number of cities in this instance of the TSP.
	 * @return the number of cities in this TSP.
	 */
	public final int length() {
		return x.length;
	}
	
}
