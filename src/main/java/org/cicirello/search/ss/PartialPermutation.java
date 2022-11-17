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

package org.cicirello.search.ss;

import org.cicirello.permutations.Permutation;

/**
 * A PartialPermutation represents a permutation that is being iteratively constructed as a solution
 * to an optimization problem over the space of permutations. This class supports the implementation
 * of constructive heuristics for permutation optimization problems, as well as for stochastic
 * sampling algorithms that rely on constructive heuristics.
 *
 * <p>In the context of this library, a permutation of length n is a permutation of the integers {
 * 0, 1, ..., (n-1)}.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class PartialPermutation implements Partial<Permutation> {

  private final int[] partial;
  private final int[] remainingElements;
  private int size;
  private int remaining;

  /**
   * Constructs a PartialPermutation that will iteratively be transformed into a Permutation.
   *
   * @param n The desired length of the final Permutation, which must be non-negative.
   * @throws IllegalArgumentException if n is less than 0
   */
  public PartialPermutation(int n) {
    if (n < 0) throw new IllegalArgumentException("n must not be negative");
    partial = new int[n];
    remainingElements = new int[n];
    for (int i = 1; i < n; i++) {
      remainingElements[i] = i;
    }
    remaining = n;
    // deliberately using default: size=0;
  }

  @Override
  public Permutation toComplete() {
    if (remaining > 0) {
      System.arraycopy(remainingElements, 0, partial, size, remaining);
    }
    return new Permutation(partial);
  }

  @Override
  public boolean isComplete() {
    return remaining == 0;
  }

  @Override
  public int get(int index) {
    if (index >= size) {
      throw new ArrayIndexOutOfBoundsException("index must be less than size");
    }
    return partial[index];
  }

  @Override
  public int getLast() {
    return partial[size - 1];
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public int numExtensions() {
    return remaining;
  }

  @Override
  public int getExtension(int extensionIndex) {
    if (extensionIndex >= remaining) {
      throw new ArrayIndexOutOfBoundsException("extensionIndex must be less than numExtensions()");
    }
    return remainingElements[extensionIndex];
  }

  @Override
  public void extend(int extensionIndex) {
    if (extensionIndex >= remaining) {
      throw new ArrayIndexOutOfBoundsException("extensionIndex must be less than numExtensions()");
    }
    partial[size] = remainingElements[extensionIndex];
    size++;
    remaining--;
    remainingElements[extensionIndex] = remainingElements[remaining];
  }
}
