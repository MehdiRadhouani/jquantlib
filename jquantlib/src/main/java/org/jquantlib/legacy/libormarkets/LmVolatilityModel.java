/*
 Copyright (C)
 2009  Ueli Hofstetter

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

package org.jquantlib.legacy.libormarkets;

import java.util.ArrayList;
import java.util.List;

import org.jquantlib.math.Array;
import org.jquantlib.model.Parameter;

public abstract class LmVolatilityModel {

    private static final String integrated_variance_not_supported = "integratedVariance() method is not supported";

    protected final int size_;
    protected List<Parameter> arguments_;

    public LmVolatilityModel(final int size, final int nArguments){
        this.size_ = size;
        this.arguments_ = new ArrayList<Parameter>(nArguments);
    }

    public int size(){
        return size_;
    }

    public boolean isTimeIndependent() {
        return false;
    }

    public double volatility(final int i, final double t){
        return volatility(t, new Array()).get(i);
    }

    public double volatility(final int i, final double t, final Array x){
        return volatility(t, x).get(i);
    }

    public Array volatility(final double t) {
        return volatility(t, new Array());
    }

    public abstract Array volatility(double t, Array x);

    public double integratedVariance(final int i, final int ii, final double t, final Array list){
        throw new AssertionError(integrated_variance_not_supported);
    }

    public void setParams(final List<Parameter> arguments){
       this.arguments_ = arguments;
       generateArguments();
    }

    protected abstract void generateArguments();

}
