/*
 Copyright (C) 2008 Srinivas Hasti
 Copyright (C) 2008 Dominik Holenstein

 This source code is release under the BSD License.

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.

 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

package org.jquantlib.time.calendars;

import static org.jquantlib.time.Month.August;
import static org.jquantlib.time.Month.December;
import static org.jquantlib.time.Month.January;
import static org.jquantlib.time.Month.May;

import org.jquantlib.lang.annotation.QualityAssurance;
import org.jquantlib.lang.annotation.QualityAssurance.Quality;
import org.jquantlib.lang.annotation.QualityAssurance.Version;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Date;
import org.jquantlib.time.Month;
import org.jquantlib.time.Weekday;

/**
 * France calendar Holidays:
 * <ul>
 * <li>Saturdays</li>
 * <li>Sundays</li>
 * <li>New Year's Day, JANUARY 1st</li>
 * <li>Good Friday</li>
 * <li>Easter Monday</li>
 * <li>Ascension Day</li>
 * <li>Whit Monday</li>
 * <li>Labour Day, May 1st</li>
 * <li>Victory in Europe Day</li>
 * <li>Bastille Day</li>
 * <li>Assumption of Mary to Heaven</li>
 * <li>All Saints' Day</li>
 * <li>Armistice Day</li>
 * <li>Christmas, December 25th</li>
 * <li>St. Stephen's Day, December 26th</li>
 * </ul>
 *
 * @category calendars
 *
 * @author Srinivas Hasti
 * @author Dominik Holenstein
 * @author Zahid Hussain
 * @author Mehdi Radhouani
 */

@QualityAssurance(quality = Quality.Q3_DOCUMENTATION, version = Version.V097, reviewers = { "Zahid Hussain" })
public class France extends Calendar {

    //
    // public constructors
    //

    public France() {
        impl = new Impl();
    }

    //
    // private final inner classes
    //

    private class Impl extends WesternImpl {
        @Override
        public String name() {
            return "France";
        }

        @Override
        public boolean isBusinessDay(final Date date) {
            final Weekday w = date.weekday();
            final int d = date.dayOfMonth(), dd = date.dayOfYear();
            final Month m = date.month();
            final int y = date.year();
            final int em = easterMonday(y);
            if (isWeekend(w)
                    // New Year's Day
                    || (d == 1 && m == January)
                    // Good Friday
                    || (dd == em - 3)
                    // Easter Monday
                    || (dd == em)
                    // Ascension Day
                    || (dd == em + 38)
                    // Whit Monday
                    || (dd == em + 49)
                    // Labour Day
                    || (d == 1 && m == May)
                    // Victory in Europe Day
                    || (d == 8 && m == May)
                    // Bastille Day
                    || (d == 14 && m == July)
                    // Assumption of Mary to Heaven
                    || (d == 15 && m == August)
                    // All Saints' Day
                    || (d == 1 && m == November)
                    // Armistice Day
                    || (d == 11 && m == November)
                    // Christmas Day
                    || (d == 25 && m == December)
                    // St. Stephen's Day
                    || (d == 26 && m == December)) {
                return false;
            }
            return true;
        }

    }
}
