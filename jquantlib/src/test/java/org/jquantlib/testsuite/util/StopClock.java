/*
 Copyright (C) 2007 Srinivas Hasti

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquantlib-dev@lists.sf.net>. The license is also available online at
 <http://jquantlib.org/license.shtml>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 
 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the originating copyright notice follows below.
 */
package org.jquantlib.testsuite.util;

public class StopClock {
	public static enum Unit {
		MICRO, NANO;
	}

	private Unit units;
	private long startTime;
	private long stopTime;

	public StopClock() {
		this.units = Unit.MICRO;
	}

	public StopClock(Unit unit) {
		this.units = unit;
	}

	public void startClock() {
		if (units == Unit.MICRO)
			startTime = System.currentTimeMillis();
		else
			startTime = System.nanoTime();
	}

	public void stopClock() {
		if (units == Unit.MICRO)
			stopTime = System.currentTimeMillis();
		else
			stopTime = System.nanoTime();
	}

	public long timeElapsed() {
		return stopTime - startTime;
	}

	public Unit getUnit() {
		return units;
	}

	public void reset() {
		startTime = 0;
		stopTime = 0;
	}
	
	public String toString(){
		return ("Time taken: "+timeElapsed()+" "+units);
	}
	
	public void log(){
		System.out.println(toString());
	}
}