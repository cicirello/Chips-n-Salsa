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
 
package org.cicirello.search.problems;

import org.cicirello.search.representations.SingleReal;

/**
 * This class defines polynomial root finding as an optimization problem, enabling
 * solving via simulated annealing or other metaheuristic optimization algorithms.
 * In polynomial root-finding, we have an equation of the form:
 * 0 = c<sub>0</sub> + c<sub>1</sub>*x + c<sub>2</sub>*x<sup>2</sup> + 
 * ... + c<sub>N</sub>*x<sup>N</sup>.
 * The problem is to find a value of x, known as a root or as a zero, that satisfies
 * the equation.  This class represents root-finding as an optimization problem where
 * one must find a value of x to minimize the value of: 
 * abs(c<sub>0</sub> + c<sub>1</sub>*x + c<sub>2</sub>*x<sup>2</sup> + 
 * ... + c<sub>N</sub>*x<sup>N</sup>).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class PolynomialRootFinding implements OptimizationProblem<SingleReal> {
	
	private final double[] coefficients;
	private final double precision;
	
	/**
	 * The default for the precision of the result.
	 * @see #isMinCost
	 */
	public static final double DEFAULT_PRECISION = 1e-10;
	
	/**
	 * Defines a problem for finding a root of a quadratic equation 
	 * of the form: 0 = a*x<sup>2</sup> + b*x + c.
	 * The problem defined is to find a value of x that satisfies the
	 * above equation.
	 * Uses the {@link #DEFAULT_PRECISION} for the precision of the
	 * result, i.e., any x such 
	 * that {@link #DEFAULT_PRECISION} &ge; abs( a*x<sup>2</sup> + b*x + c). 
	 *
	 * @param a The coefficient of the quadratic term.
	 * @param b The coefficient of the linear term.
	 * @param c The constant term.
	 *
	 * @see #isMinCost
	 */
	public PolynomialRootFinding(double a, double b, double c) {
		this(a, b, c, DEFAULT_PRECISION);
	}
	
	/**
	 * Defines a problem for finding a root of a quadratic equation 
	 * of the form: 0 = a*x<sup>2</sup> + b*x + c.
	 * The problem defined is to find a value of x that satisfies the
	 * above equation.
	 * Programmer can specify the precision of the result, i.e., finds any x such 
	 * that precision &ge; abs( a*x<sup>2</sup> + b*x + c). 
	 *
	 * @param a The coefficient of the quadratic term.
	 * @param b The coefficient of the linear term.
	 * @param c The constant term.
	 * @param precision The precision of the result.
	 *
	 * @see #isMinCost
	 */
	public PolynomialRootFinding(double a, double b, double c, double precision) {
		coefficients = new double[3];
		coefficients[0] = c;
		coefficients[1] = b;
		coefficients[2] = a;
		this.precision = precision;
	}
	
	/**
	 * Defines a problem for finding a root of a polynomial equation
	 * of the form: 0 = c<sub>0</sub> + c<sub>1</sub>*x + c<sub>2</sub>*x<sup>2</sup> + 
	 * ... + c<sub>N</sub>*x<sup>N</sup>.
	 * The problem defined is to find a value of x that satisfies the
	 * above equation.
	 * Uses the {@link #DEFAULT_PRECISION} for the precision of the
	 * result, i.e., any x such 
	 * that {@link #DEFAULT_PRECISION} &ge; abs(c<sub>0</sub> + c<sub>1</sub>*x + 
	 * c<sub>2</sub>*x<sup>2</sup> + 
	 * ... + c<sub>N</sub>*x<sup>N</sup>). 
	 *
	 * @param coefficients An array of the coefficients of the polynomial.  The degree of
	 * the polynomial is equal to coefficients.length - 1.  The value of coefficients[i] is
	 * the coefficient of the x<sup>i</sup> term.  Thus, coefficients[0] is the constant term.
	 * The length of coefficients must be at least 2.
	 *
	 * @throws IllegalArgumentException if coefficients.length &lt; 2
	 * @throws NullPointerException if coefficients is null
	 *
	 * @see #isMinCost
	 */
	public PolynomialRootFinding(double[] coefficients) {
		this(coefficients, DEFAULT_PRECISION);
	}
	
	/**
	 * Defines a problem for finding a root of a polynomial equation
	 * of the form: 0 = c<sub>0</sub> + c<sub>1</sub>*x + c<sub>2</sub>*x<sup>2</sup> + 
	 * ... + c<sub>N</sub>*x<sup>N</sup>.
	 * The problem defined is to find a value of x that satisfies the
	 * above equation.
	 * Programmer can specify the precision of the result, i.e., finds any x such 
	 * that precision &ge; abs(c<sub>0</sub> + c<sub>1</sub>*x + 
	 * c<sub>2</sub>*x<sup>2</sup> + 
	 * ... + c<sub>N</sub>*x<sup>N</sup>). 
	 *
	 * @param coefficients An array of the coefficients of the polynomial.  The degree of
	 * the polynomial is equal to coefficients.length - 1.  The value of coefficients[i] is
	 * the coefficient of the x<sup>i</sup> term.  Thus, coefficients[0] is the constant term.
	 * The length of coefficients must be at least 2.
	 * @param precision The precision of the result.
	 *
	 * @throws IllegalArgumentException if coefficients.length &lt; 2
	 * @throws NullPointerException if coefficients is null
	 *
	 * @see #isMinCost
	 */
	public PolynomialRootFinding(double[] coefficients, double precision) {
		if (coefficients.length < 2) {
			throw new IllegalArgumentException("Degree of the polynomial must be at least 1.");
		}
		this.coefficients = coefficients.clone();
		this.precision = precision;
	}
	
	@Override
	public double cost(SingleReal candidate) {
		double c = coefficients[0];
		double x = candidate.get(0);
		double term = x;
		int last = coefficients.length - 1;
		for (int i = 1; i < last; i++) {
			c += coefficients[i] * term;
			term *= x;
		}
		c += coefficients[last] * term;
		return Math.abs(c);
	}
	
	@Override
	public double minCost() {
		return 0.0;
	}
	
	/**
	 * {@inheritDoc}
	 * @return true if abs(cost) is &le; precision.
	 * @see #DEFAULT_PRECISION
	 */
	@Override
	public boolean isMinCost(double cost) {
		return Math.abs(cost) <= precision;
	}
	
	@Override
	public double value(SingleReal candidate) {
		return cost(candidate);
	}
}