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
 
package org.cicirello.search.sa;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.math.rand.RandomIndexer;

/**
 * JUnit test cases for the Self-Tuning Lam annealing schedule.
 */
public class SelfTuningLamTests {
	
	private static final double EPSILON = 1e-10;
	
	@Test
	public void testSplit() {
		SelfTuningLam mOriginal = new SelfTuningLam();
		mOriginal.init(500);
		for (int i = 0; i < 10; i++) mOriginal.accept(3, 2);
		SelfTuningLam m = mOriginal.split();
		m.init(100);
		for (int i = 0; i < 15; i++) m.accept(3, 2);
		assertEquals(0.441, m.getTargetRate(), EPSILON, "target rate end of phase 1");
		m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate start of phase 2");
		for (int i = 16; i < 65; i++) m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate end of phase 2");
		m.accept(3, 2);
		assertEquals(0.44*Math.pow(440, -1.0/35.0), m.getTargetRate(), EPSILON, "target rate start of phase 3");
		for (int i = 66; i < 100; i++) m.accept(3, 2);
		assertEquals(0.001, m.getTargetRate(), EPSILON, "target rate end of phase 3");
	}
	
	@Test
	public void testTargetRate() {
		final double LAM_RATE_POINT_ONE_PERCENT_OF_RUN = 0.9768670788789564;
		final double LAM_RATE_ONE_PERCENT_OF_RUN = 0.8072615745900611;
		SelfTuningLam m = new SelfTuningLam();
		m.init(100);
		m.accept(3, 2);
		assertEquals(LAM_RATE_ONE_PERCENT_OF_RUN, m.getTargetRate(), EPSILON, "target rate after tuning");
		for (int i = 1; i < 15; i++) m.accept(3, 2);
		assertEquals(0.441, m.getTargetRate(), EPSILON, "target rate end of phase 1");
		m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate start of phase 2");
		for (int i = 16; i < 65; i++) m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate end of phase 2");
		m.accept(3, 2);
		assertEquals(0.44*Math.pow(440, -1.0/35.0), m.getTargetRate(), EPSILON, "target rate start of phase 3");
		for (int i = 66; i < 100; i++) m.accept(3, 2);
		assertEquals(0.001, m.getTargetRate(), EPSILON, "target rate end of phase 3");
		// repeating to make sure init resets stuff correctly
		m.init(100);
		m.accept(3, 2);
		assertEquals(LAM_RATE_ONE_PERCENT_OF_RUN, m.getTargetRate(), EPSILON, "target rate after tuning");
		for (int i = 1; i < 15; i++) m.accept(3, 2);
		assertEquals(0.441, m.getTargetRate(), EPSILON, "target rate end of phase 1");
		m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate start of phase 2");
		for (int i = 16; i < 65; i++) m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate end of phase 2");
		m.accept(3, 2);
		assertEquals(0.44*Math.pow(440, -1.0/35.0), m.getTargetRate(), EPSILON, "target rate start of phase 3");
		for (int i = 66; i < 100; i++) m.accept(3, 2);
		assertEquals(0.001, m.getTargetRate(), EPSILON, "target rate end of phase 3");
		// now repeating with longer run length
		m.init(1000);
		for (int i = 0; i < 10; i++) m.accept(3, 2);
		assertEquals(LAM_RATE_ONE_PERCENT_OF_RUN, m.getTargetRate(), EPSILON, "target rate after tuning");
		for (int i = 10; i < 150; i++) m.accept(3, 2);
		assertEquals(0.441, m.getTargetRate(), EPSILON, "target rate end of phase 1");
		m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate start of phase 2");
		for (int i = 151; i < 650; i++) m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate end of phase 2");
		m.accept(3, 2);
		assertEquals(0.44*Math.pow(440, -1.0/350.0), m.getTargetRate(), EPSILON, "target rate start of phase 3");
		for (int i = 651; i < 1000; i++) m.accept(3, 2);
		assertEquals(0.001, m.getTargetRate(), EPSILON, "target rate end of phase 3");
		// now repeating with an even longer run length
		m.init(10000);
		for (int i = 0; i < 10; i++) m.accept(3, 2);
		assertEquals(LAM_RATE_POINT_ONE_PERCENT_OF_RUN, m.getTargetRate(), EPSILON, "target rate after tuning");
		for (int i = 10; i < 1500; i++) m.accept(3, 2);
		assertEquals(0.441, m.getTargetRate(), EPSILON, "target rate end of phase 1");
		m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate start of phase 2");
		for (int i = 1501; i < 6500; i++) m.accept(3, 2);
		assertEquals(0.44, m.getTargetRate(), EPSILON, "target rate end of phase 2");
		m.accept(3, 2);
		assertEquals(0.44*Math.pow(440, -1.0/3500.0), m.getTargetRate(), EPSILON, "target rate start of phase 3");
		for (int i = 6501; i < 10000; i++) m.accept(3, 2);
		assertEquals(0.001, m.getTargetRate(), EPSILON, "target rate end of phase 3");
	}
	
	@Test
	public void testAccept() {
		final double LAM_RATE_POINT_ONE_PERCENT_OF_RUN = 0.9768670788789564;
		final double LAM_RATE_ONE_PERCENT_OF_RUN = 0.8072615745900611;
		SelfTuningLam m = new SelfTuningLam();
		double alpha = 2.0 / 11.0;
		m.init(1000);
		double expected = LAM_RATE_ONE_PERCENT_OF_RUN;
		for (int i = 0; i < 1000; i++) {
			double t0 = m.getTemperature();
			assertEquals(expected, m.getAcceptRate(), EPSILON);
			// force an acceptance with neighbor cost <= current cost
			assertTrue(m.accept(i, 999), "Should definitely accept when new cost is <= old");
			double t1 = m.getTemperature();
			if (i >= 10) {
				if (m.getAcceptRate() < m.getTargetRate()) 
					assertTrue(t1 > t0, "temperature should increase if acceptRate is too low");
				else if (m.getAcceptRate() > m.getTargetRate())
					assertTrue(t1 < t0, "temperature should decrease if acceptRate is too low");
			}
			if (i >= 10) {
				expected = (1-alpha) * expected + alpha;
			}
		}
		m.init(1000);
		expected = LAM_RATE_ONE_PERCENT_OF_RUN;
		for (int i = 0; i < 1000; i++) {
			double t0 = m.getTemperature();
			assertEquals(expected, m.getAcceptRate(), EPSILON);
			// Make sure doesn't break if all phase0 costs are same
			if (i < 10) {
				assertTrue(m.accept(999, 999), "Should accept all phase0");
			} else {
				assertTrue(m.accept(i, 999), "Should definitely accept when new cost is <= old");
			}
			double t1 = m.getTemperature();
			if (i >= 10) {
				if (m.getAcceptRate() < m.getTargetRate()) 
					assertTrue(t1 > t0, "temperature should increase if acceptRate is too low");
				else if (m.getAcceptRate() > m.getTargetRate())
					assertTrue(t1 < t0, "temperature should decrease if acceptRate is too low");
			}
			if (i >= 10) {
				expected = (1-alpha) * expected + alpha;
			}
		}
		m.init(10000);
		alpha = 2.0 / 101.0;
		expected = LAM_RATE_POINT_ONE_PERCENT_OF_RUN;
		for (int i = 0; i < 1600; i++) {
			double t0 = m.getTemperature();
			assertEquals(expected, m.getAcceptRate(), EPSILON);
			// force an acceptance with neighbor cost <= current cost
			assertTrue(m.accept(i, 9999), "Should definitely accept when new cost is <= old");
			double t1 = m.getTemperature();
			if (i >= 10) {
				if (m.getAcceptRate() < m.getTargetRate()) 
					assertTrue(t1 > t0);
				else if (m.getAcceptRate() > m.getTargetRate())
					assertTrue(t1 < t0);
			}
			if (i >= 10) {
				expected = (1-alpha) * expected + alpha;
			}
		}		
		m.init(1000);
		alpha = 2.0 / 11.0;
		expected = LAM_RATE_ONE_PERCENT_OF_RUN;
		for (int i = 0; i < 1000; i++) {
			double t0 = m.getTemperature();
			assertEquals(expected, m.getAcceptRate(), EPSILON);
			// force a rejection with infinite cost neighbor
			if (i>=10) {
				assertFalse(m.accept(Double.POSITIVE_INFINITY, 0));
			} else {
				m.accept(Double.POSITIVE_INFINITY, 0);
			}
			double t1 = m.getTemperature();
			// Unlike prior cases, we can't check for proper temperature shift
			// as this case will force temperature to infinity.
			if (i >= 10) {
				expected = (1-alpha) * expected;
			}
		}	
		final int RUN_LENGTH = 1000;
		m.init(RUN_LENGTH);
		int count = 0;
		for (int i = 0; i < RUN_LENGTH; i++) {
			double t0 = m.getTemperature();
			if (m.accept(10001 + RandomIndexer.nextInt(5), 10000)) count++;
			double t1 = m.getTemperature();
			if (i >= 10) {
				if (m.getAcceptRate() < m.getTargetRate()) 
					assertTrue(t1 > t0);
				else if (m.getAcceptRate() > m.getTargetRate())
					assertTrue(t1 < t0);
			}
		}
		assertTrue(count > 0);
		assertTrue(count < RUN_LENGTH);
	}
	
	@Test
	public void testTemperatureInitialization() {
		final double LAM_RATE_POINT_ONE_PERCENT_OF_RUN = 0.9768670788789564;
		final double LAM_RATE_ONE_PERCENT_OF_RUN = 0.8072615745900611;
		SelfTuningLam m = new SelfTuningLam();
		
		// Tests with 1000 evals for 0.01 percent of run case
		m.init(1000);
		for (int i = 0; i < 10; i++) {
			m.accept(10+i, 20);
		}
		double expected = 5.5 * 0.18987910472222955;
		assertEquals(expected, m.getTemperature(), EPSILON);
		m.init(1000);
		for (int i = 0; i < 10; i++) {
			m.accept(10+i, 9);
		}
		expected = -5.5 / Math.log(LAM_RATE_ONE_PERCENT_OF_RUN);
		assertEquals(expected, m.getTemperature(), EPSILON);
		m.init(1000);
		for (int i = 0; i < 10; i++) {
			if (i%2==0) m.accept(10+i, 9);
			else m.accept(9+i, 20);
		}
		expected = -5.5 / Math.log(2*LAM_RATE_ONE_PERCENT_OF_RUN-1);
		assertEquals(expected, m.getTemperature(), EPSILON);
		
		m.init(1000);
		for (int i = 0; i < 10; i++) {
			if (i<3) m.accept(10+i, 9);
			else m.accept(10+i, 20);
		}
		expected = -3.4 / Math.log((LAM_RATE_ONE_PERCENT_OF_RUN-0.7)/0.3);
		assertEquals(expected, m.getTemperature(), EPSILON);
		
		// Tests with 10000 evals for 0.001 percent of run case
		m.init(10000);
		for (int i = 0; i < 10; i++) {
			m.accept(10+i, 20);
		}
		expected = -5.5 / Math.log(11*LAM_RATE_POINT_ONE_PERCENT_OF_RUN-10.0);
		assertEquals(expected, m.getTemperature(), EPSILON);
		m.init(10000);
		for (int i = 0; i < 10; i++) {
			m.accept(10+i, 9);
		}
		expected = -5.5 / Math.log(LAM_RATE_POINT_ONE_PERCENT_OF_RUN);
		assertEquals(expected, m.getTemperature(), EPSILON);
		m.init(10000);
		for (int i = 0; i < 10; i++) {
			if (i%2==0) m.accept(10+i, 9);
			else m.accept(9+i, 20);
		}
		expected = -5.5 / Math.log(2*LAM_RATE_POINT_ONE_PERCENT_OF_RUN-1);
		assertEquals(expected, m.getTemperature(), EPSILON);
		
		// Tests with 100000 evals for 0.001 percent of run case
		m.init(100000);
		for (int i = 0; i < 100; i++) {
			m.accept(10+i, 110);
		}
		expected = 50.5 * 0.3141120890121576;
		assertEquals(expected, m.getTemperature(), EPSILON);
		
		m.init(100000);
		for (int i = 0; i < 100; i++) {
			if (i<4) m.accept(10+i, 9);
			else m.accept(1+i, 105);
		}
		expected = -50.5 / Math.log((LAM_RATE_POINT_ONE_PERCENT_OF_RUN-0.96)/0.04);
		assertEquals(expected, m.getTemperature(), EPSILON);
		
		// Tests with 1000000 evals for 0.001 percent of run case
		m.init(1000000);
		for (int i = 0; i < 1000; i++) {
			m.accept(10+i, 1010);
		}
		expected = 500.5 * 0.3141120890121576;
		assertEquals(expected, m.getTemperature(), EPSILON);
	}
	
}