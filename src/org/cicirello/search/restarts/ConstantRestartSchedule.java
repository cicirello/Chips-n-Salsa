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
 
package org.cicirello.search.restarts;

/**
 * This is the basic constant run length restart schedule, such that every restart
 * of the multistart metaheuristic is the same in length.
 *
 * @since 1.0
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 * @version 5.8.2020
 */
public final class ConstantRestartSchedule implements RestartSchedule {
	
	private final int r;
	
	/**
	 * Constructs the restart schedule.
	 * @param runLength The length of the run for all restarts. The runLength must be positive.
	 * @throws IllegalArgumentException if runLength &lt; 1
	 */
	public ConstantRestartSchedule(int runLength) {
		if (runLength < 1) throw new IllegalArgumentException("runLength must be positive");
		r = runLength;
	}
	
	@Override
	public int nextRunLength() {
		return r;
	}
	
	@Override
	public void reset() {}
	
	@Override
	public ConstantRestartSchedule split() {
		// should be safe for multiple threads to share.
		return this;
	}
}