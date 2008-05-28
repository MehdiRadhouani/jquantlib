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
package org.jquantlib.termstructures.yield;

import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.indexes.IborIndex;
import org.jquantlib.quotes.Handle;
import org.jquantlib.quotes.Quote;
import org.jquantlib.quotes.SimpleQuote;
import org.jquantlib.termstructures.RateHelper;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.BusinessDayConvention;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.IMM;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;
import org.jquantlib.util.Date;
import org.jquantlib.util.Observable;

/**
 * @author Srinivas Hasti
 * 
 */
//TODO: Complete
public class FuturesRateHelper extends RateHelper<YieldTermStructure> {

	private double yearFraction;
	private Handle<Quote> convAdj;

	public FuturesRateHelper(Handle<Quote> price, Date immDate, int nMonths,
			Calendar calendar, BusinessDayConvention convention,
			boolean endOfMonth, DayCounter dayCounter, Handle<Quote> convAdj) {
		super(price, null, null, null);

		if (!IMM.getDefaultIMM().isIMMdate(immDate, false))
			throw new IllegalArgumentException(" is not a valid IMM date");

		earliestDate = immDate;
		latestDate = calendar.advance(immDate, new Period(nMonths,
				TimeUnit.MONTHS), convention, endOfMonth);
		yearFraction = dayCounter.getYearFraction(earliestDate, latestDate);

		// registerWith(convAdj_);
	}

	public FuturesRateHelper(double price, Date immDate, int nMonths,
			Calendar calendar, BusinessDayConvention convention,
			boolean endOfMonth, DayCounter dayCounter, double conv) {
		super(price);
		convAdj = new Handle<Quote>(new SimpleQuote(conv));
		if (!IMM.getDefaultIMM().isIMMdate(immDate, false))
			throw new IllegalArgumentException(" is not a valid IMM date");

		earliestDate = immDate;
		latestDate = calendar.advance(immDate, new Period(nMonths,
				TimeUnit.MONTHS), convention, endOfMonth);
		yearFraction = dayCounter.getYearFraction(earliestDate, latestDate);
	}

	public FuturesRateHelper(double price, Date immDate, IborIndex i,
			double conv) {
		super(price);
		convAdj = new Handle<Quote>(new SimpleQuote(conv));
		if (!IMM.getDefaultIMM().isIMMdate(immDate, false))
			throw new IllegalArgumentException(" is not a valid IMM date");
		earliestDate = immDate;
		Calendar cal = i.getFixingCalendar();
		latestDate = cal.advance(immDate, i.getTenor(), i.getConvention());
		yearFraction = i.getDayCounter().getYearFraction(earliestDate,
				latestDate);
	}

	public double getImpliedQuote() {
		if (termStructure == null)
			throw new IllegalStateException("term structure not set");
		double forwardRate = termStructure.getDiscount(earliestDate)
				/ (termStructure.getDiscount(latestDate) - 1.0) / yearFraction;
		double convA = convAdj.isEmpty() ? 0.0 : convAdj.getLink()
				.doubleValue();
		if (convA < 0.0)
			throw new IllegalStateException("Negative (" + convA
					+ ") futures convexity adjustment");
		double futureRate = forwardRate + convA;
		return 100.0 * (1.0 - futureRate);
	}

	public double getConvexityAdjustment() {
		return convAdj.isEmpty() ? 0.0 : convAdj.getLink().doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jquantlib.util.Observer#update(org.jquantlib.util.Observable,
	 *      java.lang.Object)
	 */
	@Override
	//TODO: MOVE TO BASE CLASS
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
