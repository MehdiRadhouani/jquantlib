/*
 Copyright (C) 2008 Richard Gomes

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
 Copyright (C) 2003 Ferdinando Ametrano
 Copyright (C) 2004 StatPro Italia srl

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

import org.jquantlib.instruments.Payoff;
import org.jquantlib.math.interpolations.CubicSplineInterpolation;
import org.jquantlib.math.Array;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Dominik Holenstein
 *
 */
//FIXME: work in progress [Dominik]
public class SampledCurve {
	
    private final static Logger logger = LoggerFactory.getLogger(SampledCurve.class);
    
	//
	// private fields
	//
	private Array grid_;
	private Array values_;
	
	
	//
	// Constructors
	//
	public SampledCurve(int gridSize){
		
	}
	
	public SampledCurve(final Array grid){
		
	}
	
	
	
	
	public double valueAtCenter() /* @Readonly */ {
		if (empty()) throw new ArithmeticException("empty sampled curve");
		int jmid = size()/2;
		if (size() % 2 == 1)
			return values_.at(jmid);
		else
			return (values_.at(jmid)+values_.at(jmid-1)/2.0);
	}
	
	public double firstDerivativeAtCenter() /* @Readonly */ {
		if(size() >= 3) throw new ArithmeticException("the size of the curve must be at least 3");
		int jmid = size()/2;
		if (size() % 2 == 1){
			return (values_.at(jmid+1)-values_.at(jmid-1)) / (grid_.at(jmid+1)-grid_.at(jmid-1));
		}
		else {
			return (values_.at(jmid)-values_.at(jmid-1)) / (grid_.at(jmid)-grid_.at(jmid-1));
		}
	}
	
	
	public double secondDerivativeAtCenter() /* Read-only */ {
        if(size()>=4) throw new ArithmeticException("the size of the curve must be at least 4");
        int jmid = size()/2;
        if (size() % 2 == 1) {
            double deltaPlus = (values_.at(jmid+1)-values_.at(jmid)/
                (grid_.at(jmid+1)-grid_.at(jmid)));
            double deltaMinus = (values_.at(jmid)-values_.at(jmid-1)/
                (grid_.at(jmid)-grid_.at(jmid-1)));
            double dS = (grid_.at(jmid+1)-grid_.at(jmid-1))/2.0;
            return (deltaPlus-deltaMinus)/dS;
        } else {
            double deltaPlus = (values_.at(jmid+1)-values_.at(jmid-1)/
                (grid_.at(jmid+1)-grid_.at(jmid-1)));
            double deltaMinus = (values_.at(jmid)-values_.at(jmid-2))/
                (grid_.at(jmid)-grid_.at(jmid-2));
            return (deltaPlus-deltaMinus)/
                (grid_.at(jmid)-grid_.at(jmid-1));
        }
    }
	
	/*
    public void regrid(final Array new_grid) {
        NaturalCubicSpline priceSpline(grid_.at(0), grid_.end(),
                                       values_.at(0));
        priceSpline.update();
     
        Array newValues = new Array(new_grid.size());
        
        Array::iterator val;
        Array::const_iterator grid;
        for (val = newValues.begin(), grid = new_grid.begin() ;
             grid != new_grid.end();
             val++, grid++) {
            *val = priceSpline(*grid, true);
        }
        values_.swap(newValues);
        grid_ = new_grid;
    }
   
    */
	
    //
    // inner classes
    //
	
	private void shiftGrid(double s){
		grid_.operatorAdd(s); 
	}
	
	private void scaleGrid(double s){
		grid_.operatorMultiply(s);
	}
	
	
	
	
	public Array grid() /* @Readonly */{
		return grid_;
	}
	
	public Array values() /*@Readonly */ {
		return values_;
	}
	
	//TODO: Check what we have to translate: the const version or the other?
	/*
	inline Array& SampledCurve::grid() {
        return grid_;
    }

    inline const Array& SampledCurve::grid() const {
        return grid_;
    }

    inline const Array& SampledCurve::values() const {
        return values_;
    }

    inline Array& SampledCurve::values() {
        return values_;
    }
	
	*/
	
	public double value(int i){
		return values_.at(i);
	}
	
	public int size(){
		return grid_.size();
	}
	
	private boolean empty() /* @Readonly */ {
		int sizeOfGrid = grid_.size();
		if (sizeOfGrid == 0)
			return true;
		else
			return false;
	}

	public void setValues(Array array) {
		this.values_ = array;
	}

	public void setLogGrid(double min, double max) {
		// TODO Auto-generated method stub
		
	}

	public void sample(Payoff payoff) {
		// TODO Auto-generated method stub
		
	}

}
