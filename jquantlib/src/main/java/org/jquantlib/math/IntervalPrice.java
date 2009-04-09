/*
 Copyright (C) 2008 Anand Mani
 
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

/*
 Copyright (C) 2006 Joseph Wang

 This file is part of QuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://quantlib.org/

 QuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <quantlib-dev@lists.sf.net>. The license is also available online at
 <http://quantlib.org/license.shtml>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
*/

package org.jquantlib.math;

import java.io.Serializable;
import java.util.Iterator;

import org.jquantlib.util.Date;
import org.jquantlib.util.TimeSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * Interval Price
 * 
 * @author Anand Mani
 */
public class IntervalPrice implements Serializable {
	
    private final static Logger logger = LoggerFactory.getLogger(IntervalPrice.class);
    
    
    //
    // private fields
    //
    
    private double open;
    private double close;
    private double high;
    private double low;
    
    
    //
    // public constructors
    //
    
	public IntervalPrice(
	        final /*@Real*/ double open, final /*@Real*/ double close, 
	        final /*@Real*/ double high, final /*@Real*/ double low) {
	    setValues(open, close, high, low);
	}

    
	//
	// public methods
	//
	
	public /*@Real*/ double open() /*@ReadOnly*/ {
        return this.open;
    }

	public /*@Real*/ double close() /*@ReadOnly*/ {
		return this.close;
	}

	public /*@Real*/ double high() /*@ReadOnly*/ {
		return this.high;
	}

	public /*@Real*/ double low() /*@ReadOnly*/ {
		return this.low;
	}

	public /*@Real*/ double value(final IntervalPrice.Type type) /*@ReadOnly*/ {
        switch (type) {
            case Open:
                return this.open;
            case Close:
                return this.close;
            case High:
                return this.high;
            case Low:
                return this.low;
            default:
                throw new IllegalArgumentException("Unknown price type");
        }
    }

	public void setValue(final Type type, final /*@Real*/ double value) {
        switch (type) {
            case Open:
                this.open = value;
                break;
            case Close:
                this.close = value;
                break;
            case High:
                this.high = value;
                break;
            case Low:
                this.low = value;
                break;
            default:
                throw new IllegalArgumentException("Unknown price type");
        }
	}

	public void setValues(
	        final /*@Real*/ double open, final /*@Real*/ double close, 
	        final /*@Real*/ double high, final /*@Real*/ double low) {
        this.open  = open;
        this.close = close;
        this.high  = high;
        this.low   = low;
	}



	//
	// public static methods
	//
	
	
    public static TimeSeries<IntervalPrice> makeSeries(
            final Date[] d, final double[] open, final double[] close, final double[] high, final double[] low) {

        final int dsize = d.length;
        if (open.length != dsize || close.length != dsize || high.length != dsize || low.length != dsize) {
            String msg = MessageFormatter.arrayFormat("size mismatch({}, {}, {}, {}, {})", 
                    new Object[] { dsize, open.length, close.length, high.length, low.length } );
            logger.debug(msg);
            throw new IllegalArgumentException(msg);
        }
        
        final TimeSeries<IntervalPrice> retval = new TimeSeries();
        for (int i=0; i< dsize; i++) {
            retval.add(d[i], new IntervalPrice(open[i], close[i], high[i], low[i]));
        }
        
        return retval;
    }

    public static double[] extractValues(final TimeSeries<IntervalPrice> ts, IntervalPrice.Type type)  {
        final double[] result = new double[ts.size()];
        final Iterator<IntervalPrice> it = ts.valuesIntervalPrice().iterator();
        
        for (int i=0; i<ts.size(); i++) {
            result[i] = it.next().value(type);
        }
        return result;
    }

    public static TimeSeries<Double> extractComponent(final TimeSeries<IntervalPrice> ts, IntervalPrice.Type type) {
        final Date[] dates = ts.dates();
        final double[] values = extractValues(ts, type);
        return new TimeSeries<Double>(dates, values) { /* anonymous */ };
    }

	
	
	//
	// public inner classes
	//

    public enum Type {
        Open, Close, High, Low
    }

}
