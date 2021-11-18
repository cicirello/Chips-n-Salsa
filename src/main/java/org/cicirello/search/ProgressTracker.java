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
 
package org.cicirello.search;

import org.cicirello.util.Copyable;

/**
 * This class is used to track search algorithm progress, and supports
 * multithreaded search algorithms.  For a multithreaded search algorithm,
 * all search threads should share a single instance.  The {@link #update update} 
 * methods and the {@link #getSolutionCostPair} method 
 * of this class use synchronization for thread-safety.
 * All other methods are non-blocking.
 *
 * @param <T> The type of object the search is optimizing.
 *
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class ProgressTracker<T extends Copyable<T>> {
	
	private volatile int bestCost;
	private volatile double bestCostD;
	private volatile T bestSolution;
	private volatile long when;
	
	private volatile boolean foundBest;
	private volatile boolean stop;
	private volatile boolean containsIntCost;
	
	private long origin;
	
	private final Object lock;
	
	/**
	 * Constructs a ProgressTracker.
	 */
	public ProgressTracker() {
		lock = new Object();
		bestCost = Integer.MAX_VALUE;
		bestCostD = Double.POSITIVE_INFINITY;
		when = origin = System.nanoTime();
		/* Assuming default initial values for the following: 
		 * bestSolution = null;
		 * foundBest = false;
		 * stop = false;
		 * containsIntCost = false;
		 */
	}
	
	/**
	 * Updates the best solution contained in this progress tracker.
	 * The update takes place only if the new solution has lower cost than
	 * the current best cost solution stored in the progress tracker.  This method
	 * is thread-safe.  However, it uses synchronization for thread-safety, so
	 * it is strongly suggested that in multithreaded search implementations that
	 * you reserve calls to this method for when the search believes it has likely found 
	 * a better solution than all currently running threads.  Although in theory the 
	 * functionality of this class is such that it can be used as the sole means of a
	 * search algorithm keeping track of the best found solution.  If multiple
	 * parallel runs attempt to share this object for that purpose, significant blocking
	 * may occur.  You may consider using the nonblocking {@link #getCost} method
	 * first.
	 * @param cost The cost of the solution.
	 * @param solution The new solution.
	 * @return The cost of the best solution found.  This may or may not be equal
	 * to the cost passed as a parameter.  If the returned value is less than cost, then
	 * that means the best solution was previously updated by this or another thread.
	 * @deprecated Use {@link #update(int, Copyable, boolean)} instead.
	 */
	@Deprecated
	public int update(int cost, T solution) {
		return update(cost, solution, false);
	}
	
	/**
	 * Updates the best solution contained in this progress tracker.
	 * The update takes place only if the new solution has lower cost than
	 * the current best cost solution stored in the progress tracker.  This method
	 * is thread-safe.  However, it uses synchronization for thread-safety, so
	 * it is strongly suggested that in multithreaded search implementations that
	 * you reserve calls to this method for when the search believes it has likely found 
	 * a better solution than all currently running threads.  Although in theory the 
	 * functionality of this class is such that it can be used as the sole means of a
	 * search algorithm keeping track of the best found solution.  If multiple
	 * parallel runs attempt to share this object for that purpose, significant blocking
	 * may occur.  You may consider using the nonblocking {@link #getCostDouble} method
	 * first.
	 * @param cost The cost of the solution.
	 * @param solution The new solution.
	 * @return The cost of the best solution found.  This may or may not be equal
	 * to the cost passed as a parameter.  If the returned value is less than cost, then
	 * that means the best solution was previously updated by this or another thread.
	 * @deprecated Use {@link #update(double, Copyable, boolean)} instead.
	 */
	@Deprecated
	public double update(double cost, T solution) {
		return update(cost, solution, false);
	}
	
	/**
	 * Updates the best solution contained in this progress tracker.
	 * The update takes place only if the new solution has lower cost than
	 * the current best cost solution stored in the progress tracker.  This method
	 * is thread-safe.  However, it uses synchronization for thread-safety, so
	 * it is strongly suggested that in multithreaded search implementations that
	 * you reserve calls to this method for when the search believes it has likely found 
	 * a better solution than all currently running threads.  Although in theory the 
	 * functionality of this class is such that it can be used as the sole means of a
	 * search algorithm keeping track of the best found solution.  If multiple
	 * parallel runs attempt to share this object for that purpose, significant blocking
	 * may occur.  You may consider using the nonblocking {@link #getCost} method
	 * first.
	 * @param cost The cost of the solution.
	 * @param solution The new solution.
	 * @param isKnownOptimal Pass true if this solution is known to be the optimal such as if
	 * it is equal to a lower bound for the problem, and otherwise pass false.
	 * @return The cost of the best solution found.  This may or may not be equal
	 * to the cost passed as a parameter.  If the returned value is less than cost, then
	 * that means the best solution was previously updated by this or another thread.
	 */
	public int update(int cost, T solution, boolean isKnownOptimal) {
		synchronized (lock) {
			if (bestSolution == null || cost < bestCost) {
				bestCostD = bestCost = cost;
				bestSolution = solution.copy();
				containsIntCost = true;
				foundBest = isKnownOptimal;
				when = System.nanoTime();
			}
			return bestCost;
		}
	}
	
	/**
	 * Updates the best solution contained in this progress tracker.
	 * The update takes place only if the new solution has lower cost than
	 * the current best cost solution stored in the progress tracker.  This method
	 * is thread-safe.  However, it uses synchronization for thread-safety, so
	 * it is strongly suggested that in multithreaded search implementations that
	 * you reserve calls to this method for when the search believes it has likely found 
	 * a better solution than all currently running threads.  Although in theory the 
	 * functionality of this class is such that it can be used as the sole means of a
	 * search algorithm keeping track of the best found solution.  If multiple
	 * parallel runs attempt to share this object for that purpose, significant blocking
	 * may occur.  You may consider using the nonblocking {@link #getCostDouble} method
	 * first.
	 * @param cost The cost of the solution.
	 * @param solution The new solution.
	 * @param isKnownOptimal Pass true if this solution is known to be the optimal such as if
	 * it is equal to a lower bound for the problem, and otherwise pass false.
	 * @return The cost of the best solution found.  This may or may not be equal
	 * to the cost passed as a parameter.  If the returned value is less than cost, then
	 * that means the best solution was previously updated by this or another thread.
	 */
	public double update(double cost, T solution, boolean isKnownOptimal) {
		synchronized (lock) {
			if (bestSolution == null || cost < bestCostD) {
				bestCostD = cost;
				bestSolution = solution.copy();
				containsIntCost = false;
				foundBest = isKnownOptimal;
				when = System.nanoTime();
			}
			return bestCostD;
		}
	}
	
	/**
	 * Gets the current best solution and its corresponding cost from the ProgressTracker.
	 * This method
	 * is thread-safe, and the solution and cost contained in the returned object are
	 * guaranteed to correspond with each other.  However, it uses synchronization for thread-safety,
	 * so if all you need is the current cost, you should instead use the non-blocking 
	 * {@link #getCost} or {@link #getCostDouble} methods.  Likewise, if all you need is
	 * the solution, you should instead use the non-blocking {@link #getSolution} method.
	 * @return current best solution and its corresponding cost
	 */
	public SolutionCostPair<T> getSolutionCostPair() {
		synchronized (lock) {
			if (containsIntCost)
				return new SolutionCostPair<T>(bestSolution, bestCost, foundBest);
			else
				return new SolutionCostPair<T>(bestSolution, bestCostD, foundBest);
		}
	}
	
	/**
	 * Gets the cost of the current best solution stored in the ProgressTracker.
	 * If update(double, T) was used to set a floating-point valued cost, then
	 * the behavior of this method is undefined.  Use the {@link #getCostDouble} method instead.
	 * @return the cost of the current best solution
	 */
	public int getCost() {
		return bestCost;
	}
	
	/**
	 * Gets the cost of the current best solution stored in the ProgressTracker.
	 * @return the cost of the current best solution
	 */
	public double getCostDouble() {
		return bestCostD;
	}
	
	/**
	 * Gets the current best solution stored in the ProgressTracker.
	 * @return the current best solution
	 */
	public T getSolution() {
		return bestSolution;
	}
	
	/**
	 * Gets the amount of time (nanoseconds precision) that elapsed
	 * between when this ProgressTracker was constructed and the most recent
	 * successful update of the best solution contained within the tracker.
	 * @return time (in nanoseconds) between ProgressTracker construction and
	 * most recent recording of best solution. 
	 */
	public long elapsed() {
		return when - origin;
	}
	
	/**
	 * Record that the best solution contained in the ProgressTracker is the
	 * best possible solution to the problem.
	 * @deprecated Use {@link #update(int, Copyable, boolean)} or
	 * {@link #update(double, Copyable, boolean)} instead to set the 
	 * foundBest flag when the solution is updated.
	 */
	@Deprecated
	public void setFoundBest() {
		foundBest = true;
	}
	
	/**
	 * Check whether the solution contained in the ProgressTracker has been marked
	 * as the best possible solution.
	 * @return true if the ProgressTracker contains the best possible solution.
	 */
	public boolean didFindBest() {
		return foundBest;
	}
	
	/**
	 * Set a flag that indicates that all searches sharing this ProgressTracker
	 * should stop their search when able to (e.g., such as at the end of the next iteration.
	 */
	public void stop() {
		stop = true;
	}
	
	/**
	 * Resets the stop flag to false, essentially undoing a previous call to {@link #stop}.
	 */
	public void start() {
		stop = false;
	}
	
	/**
	 * Checks whether a flag is set indicating that all searches sharing this ProgressTracker
	 * should stop.
	 * @return true if the searches sharing this ProgressTracker should stop.
	 */
	public boolean isStopped() {
		return stop;
	}
	
	/**
	 * Checks whether the cost of the solution contained in this ProgressTracker
	 * is integer valued.  
	 * @return true if the most recently set solution has integer valued cost, and false 
	 * otherwise. If this method returns false, then the behavior of the {@link #getCost}
	 * method is undefined.
	 */
	public boolean containsIntCost() {
		return containsIntCost;
	}
}
