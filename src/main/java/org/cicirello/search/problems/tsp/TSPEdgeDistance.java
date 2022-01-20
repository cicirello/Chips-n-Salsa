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
 
package org.cicirello.search.problems.tsp;

/**
 * A functional interface for specifying a distance function between a
 * pair of cities in a TSP instance.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
@FunctionalInterface
public interface TSPEdgeDistance {
	
	/**
	 * Computes the distance between two cities in the TSP.
	 * @param x1 The x coordinate of the first city.
	 * @param y1 The y coordinate of the first city.
	 * @param x2 The x coordinate of the second city.
	 * @param y2 The y coordinate of the second city.
	 * @return The distance between the two cities.
	 */
	double distance(double x1, double y1, double x2, double y2);
	
	/**
	 * Computes the distance between two cities in the TSP as an integer. Some of the
	 * commonly used distance functions in TSP benchmark problem sets assume integer costs,
	 * such as Euclidean distance between the end points of an edge rounded to the nearest integer.
	 * The default implementation of this method rounds the result
	 * of a call to distance(i, j) to the nearest integer.
	 * @param x1 The x coordinate of the first city.
	 * @param y1 The y coordinate of the first city.
	 * @param x2 The x coordinate of the second city.
	 * @param y2 The y coordinate of the second city.
	 * @return The distance between the two cities as an integer.
	 */
	default int distanceAsInt(double x1, double y1, double x2, double y2) {
		return (int)Math.round(distance(x1, y1, x2, y2));
	}
}
