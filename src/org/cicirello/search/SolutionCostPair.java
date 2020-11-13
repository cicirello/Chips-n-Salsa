/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2020  Vincent A. Cicirello
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
 
package org.cicirello.search;

import org.cicirello.util.Copyable;

/**
 * An object of this class encapsulates a solution with its corresponding cost value.
 *
 * @param <T> The type of object the search is optimizing. 
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 6.11.2020
 */
public final class SolutionCostPair<T extends Copyable<T>> implements Comparable<SolutionCostPair<T>> {
	
	private final T solution;
	private final int cost;
	private final double costD;
	private final boolean containsIntCost;
	
	/**
	 * Constructs a SolutionCostPair with integer cost.
	 * @param solution The solution.
	 * @param cost The cost of the solution.
	 */
	public SolutionCostPair(T solution, int cost) {			
		this.solution = solution;
		costD = this.cost = cost;
		containsIntCost = true;
	}
	
	/**
	 * Constructs a SolutionCostPair with integer cost.
	 * @param solution The solution.
	 * @param cost The cost of the solution.
	 */
	public SolutionCostPair(T solution, double cost) {			
		this.solution = solution;
		costD = cost;
		this.cost = (int)(cost + 0.5);
		containsIntCost = false;
	}
	
	/**
	 * Gets the cost contained in this solution cost pair as an int.
	 * Behavior is undefined if costs are floating-point values.
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}
	
	/**
	 * Gets the cost contained in this solution cost pair as a double.
	 * @return the cost of the current best solution
	 */
	public double getCostDouble() {
		return costD;
	}
	
	/**
	 * Gets the solution in this solution cost pair.
	 * @return the solution
	 */
	public T getSolution() {
		return solution;
	}
	
	/**
	 * Checks whether the cost of the solution contained in this SolutionCostPair
	 * is integer valued.  
	 * @return true if the solution has integer valued cost, and false 
	 * otherwise. If this method returns false, then the behavior of the {@link #getCost}
	 * method is undefined.
	 */
	public boolean containsIntCost() {
		return containsIntCost;
	}
	
	/**
	 * Compares this SolutionCostPair with the specified SolutionCostPair for order. 
	 * Returns a negative integer, zero, or a positive integer as this SolutionCostPair
	 * has a cost that is less than, equal to, or greater than the cost of the specified 
	 * SolutionCostPair.
	 * @param other The other SolutionCostPair with which to compare.
	 * @return a negative integer, zero, or a positive integer as this SolutionCostPair
	 * has a cost that is less than, equal to, or greater than the cost of the specified 
	 * SolutionCostPair
	 */
	@Override
	public int compareTo(SolutionCostPair<T> other) {
		if (containsIntCost) return cost - other.cost;
		if (costD < other.costD) return -1;
		if (costD > other.costD) return 1;
		return 0;
	}
}
