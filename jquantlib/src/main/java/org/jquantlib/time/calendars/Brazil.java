/*
 Copyright (C) 2008 Srinivas Hasti

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
package org.jquantlib.time.calendars;

import static org.jquantlib.time.Weekday.Friday;
import static org.jquantlib.util.Date.Month.April;
import static org.jquantlib.util.Date.Month.December;
import static org.jquantlib.util.Date.Month.January;
import static org.jquantlib.util.Date.Month.July;
import static org.jquantlib.util.Date.Month.May;
import static org.jquantlib.util.Date.Month.November;
import static org.jquantlib.util.Date.Month.October;
import static org.jquantlib.util.Date.Month.September;

import org.jquantlib.time.Calendar;
import org.jquantlib.time.Weekday;
import org.jquantlib.time.WesternCalendar;
import org.jquantlib.util.Date;
import org.jquantlib.util.Date.Month;

// ! Brazilian calendar
/**
 * ! Banking holidays:
 * <ul>
 * <li>Saturdays</li>
 * <li>Sundays</li>
 * <li>New Year's Day, January 1st</li>
 * <li>Tiradentes's Day, April 21th</li>
 * <li>Labour Day, May 1st</li>
 * <li>Independence Day, September 21th</li>
 * <li>Nossa Sra. Aparecida Day, October 12th</li>
 * <li>All Souls Day, November 2nd</li>
 * <li>Republic Day, November 15th</li>
 * <li>Christmas, December 25th</li>
 * <li>Passion of Christ</li>
 * <li>Carnival</li>
 * <li>Corpus Christi</li>
 * </ul>
 * 
 * Holidays for the Bovespa stock exchange
 * <ul>
 * <li>Saturdays</li>
 * <li>Sundays</li>
 * <li>New Year's Day, January 1st</li>
 * <li>Sao Paulo City Day, January 25th</li>
 * <li>Tiradentes's Day, April 21th</li>
 * <li>Labour Day, May 1st</li>
 * <li>Revolution Day, July 9th</li>
 * <li>Independence Day, September 21th</li>
 * <li>Nossa Sra. Aparecida Day, October 12th</li>
 * <li>All Souls Day, November 2nd</li>
 * <li>Republic Day, November 15th</li>
 * <li>Black Consciousness Day, November 20th (since 2007)</li>
 * <li>Christmas, December 25th</li>
 * <li>Passion of Christ</li>
 * <li>Carnival</li>
 * <li>Corpus Christi</li>
 * <li>the last business day of the year</li>
 * </ul>
 * 
 * @author Srinivas Hasti
 */
public class Brazil extends DelegateCalendar {
    public static enum Market {
        SETTLEMENT, // !< generic settlement calendar
        EXCHANGE
        // !< BOVESPA calendar
    };

    private final static Brazil SETTLEMENT_CALENDAR = new Brazil(Market.SETTLEMENT);
    private final static Brazil EXCHANGE_CALENDAR   = new Brazil(Market.EXCHANGE);

    private Brazil(Market market) {
        Calendar delegate;
        switch (market) {
            case SETTLEMENT:
                delegate = new SettlementCalendar();
                break;
            case EXCHANGE:
                delegate = new ExchangeCalendar();
                break;
            default:
                throw new IllegalArgumentException("unknown market");
        }
        setDelegate(delegate);
    }

    public static Brazil getCalendar(Market market) {
        switch (market) {
            case SETTLEMENT:
                return SETTLEMENT_CALENDAR;
            case EXCHANGE:
                return EXCHANGE_CALENDAR;
            default:
                throw new IllegalArgumentException("unknown market");
        }
    }

    private final class SettlementCalendar extends WesternCalendar {
        @Override
        public String getName() {
            return "Brazil";
        }

        @Override
        public boolean isBusinessDay(Date date) {
            Weekday w = date.getWeekday();
            int d = date.getDayOfMonth();
            int m = date.getMonth();
            int y = date.getYear();
            int dd = date.getDayOfYear();
            int em = easterMonday(y);

            if (isWeekend(w)
            // New Year's Day
                    || (d == 1 && m == Month.January.toInteger())
                    // Tiradentes Day
                    || (d == 21 && m == Month.April.toInteger())
                    // Labor Day
                    || (d == 1 && m == Month.May.toInteger())
                    // Independence Day
                    || (d == 7 && m == Month.September.toInteger())
                    // Nossa Sra. Aparecida Day
                    || (d == 12 && m == Month.October.toInteger())
                    // All Souls Day
                    || (d == 2 && m == Month.November.toInteger())
                    // Republic Day
                    || (d == 15 && m == Month.November.toInteger())
                    // Christmas
                    || (d == 25 && m == Month.December.toInteger())
                    // Passion of Christ
                    || (dd == em - 3)
                    // Carnival
                    || (dd == em - 49 || dd == em - 48)
                    // Corpus Christi
                    || (dd == em + 59))
                return false;
            return true;
        }
    }

    private final class ExchangeCalendar extends WesternCalendar {
        @Override
        public String getName() {
            return "BOVESPA";
        }

        @Override
        public boolean isBusinessDay(Date date) {
            Weekday w = date.getWeekday();
            int d = date.getDayOfMonth();
            Month m = date.getMonthEnum();
            int y = date.getYear();
            int dd = date.getDayOfYear();
            int em = easterMonday(y);

            if (isWeekend(w)
            // New Year's Day
                    || (d == 1 && m == January)
                    // Sao Paulo City Day
                    || (d == 25 && m == January)
                    // Tiradentes Day
                    || (d == 21 && m == April)
                    // Labor Day
                    || (d == 1 && m == May)
                    // Revolution Day
                    || (d == 9 && m == July)
                    // Independence Day
                    || (d == 7 && m == September)
                    // Nossa Sra. Aparecida Day
                    || (d == 12 && m == October)
                    // All Souls Day
                    || (d == 2 && m == November)
                    // Republic Day
                    || (d == 15 && m == November)
                    // Black Consciousness Day
                    || (d == 20 && m == November && y >= 2007)
                    // Christmas
                    || (d == 25 && m == December)
                    // Passion of Christ
                    || (dd == em - 3)
                    // Carnival
                    || (dd == em - 49 || dd == em - 48)
                    // Corpus Christi
                    || (dd == em + 59)
                    // last business day of the year
                    || (m == December && (d == 31 || (d >= 29 && w == Friday))))
                return false;
            return true;
        }
    }
}
