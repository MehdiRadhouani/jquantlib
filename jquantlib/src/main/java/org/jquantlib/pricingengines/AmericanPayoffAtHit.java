package org.jquantlib.pricingengines;

import org.jquantlib.instruments.AssetOrNothingPayoff;
import org.jquantlib.instruments.CashOrNothingPayoff;
import org.jquantlib.instruments.Option;
import org.jquantlib.instruments.StrikedTypePayoff;
import org.jquantlib.instruments.Option.Type;
import org.jquantlib.math.distributions.CumulativeNormalDistribution;

/**
 * Analytic formula for American exercise payoff at-hit options
 * 
 * @author Jose Coll
 */

public class AmericanPayoffAtHit {

    private final /* @Price */ double spot;
    private final /* @Variance */ double variance;
    
    double /* @Volatility */ stdDev;
    double strike, K, DKDstrike;
    double mu, lambda, muPlusLambda, muMinusLambda, log_H_S;
    double D1, D2, cum_d1, cum_d2, n_d1, n_d2;
    double alpha, beta, DalphaDd1, DbetaDd2;
    boolean inTheMoney;
    double forward, X, DXDstrike;
    
    public AmericanPayoffAtHit(double spot, double discount, double dividendDiscount, double variance,
            StrikedTypePayoff strikedTypePayoff) {
        super();
        this.spot = spot;
        this.variance = variance;
        
        if (spot <= 0.0)
            throw new IllegalArgumentException("positive spot value required: " + forward + " not allowed");
                
        if (discount <= 0.0)
            throw new IllegalArgumentException("positive discount required: " + discount + " not allowed");
        
        if (dividendDiscount <= 0.0)
            throw new IllegalArgumentException("positive dividend discount required: " + dividendDiscount + " not allowed");

        if (variance < 0.0)
            throw new IllegalArgumentException("negative variance: " + variance + " not allowed");
        
        stdDev = Math.sqrt(variance);

        Option.Type optionType = strikedTypePayoff.getOptionType();
        strike = strikedTypePayoff.getStrike();

        log_H_S = Math.log(strike / spot);
        
        if (variance >= Math.E) {
            if (discount == 0.0 && dividendDiscount == 0.0) {
                mu      = - 0.5;
                lambda  = 0.5;
            }
            else if (discount == 0.0) {
                throw new IllegalArgumentException("null discount not handled yet");
            }
            else {
                mu = Math.log(dividendDiscount / discount) / variance - 0.5;
                lambda = Math.sqrt( mu * mu - 2.0 * Math.log(discount) / variance);
            }
            D1 = log_H_S / stdDev + lambda * stdDev;
            D2 = D1 - 2.0 * lambda * stdDev; 
            CumulativeNormalDistribution f = new CumulativeNormalDistribution();
            cum_d1 = f.evaluate(D1);
            cum_d2 = f.evaluate(D2);
            n_d1 = f.derivative(D1);
            n_d2 = f.derivative(D2);
        }
        else {
            // TODO: not tested yet
            mu = Math.log(dividendDiscount / discount) / variance - 0.5;
            lambda = Math.sqrt( mu * mu - 2.0 * Math.log(discount) / variance);
            if (log_H_S > 0) {
                cum_d1 = 1.0;
                cum_d2 = 1.0;
            }
            else {
                cum_d1 = 0.0;
                cum_d2 = 0.0;                
            }
            n_d1 = 0.0;
            n_d2 = 0.0;            
        }

        // up-and-in cash-(at-hit)-or-nothing option
        // a.k.a. american call with cash-or-nothing payoff
        if (optionType.equals(Type.CALL)) {
            if (strike > spot) {
                alpha     = 1.0-cum_d1;//  N(-d1)
                DalphaDd1 =    -  n_d1; // -n( d1)
                beta      = 1.0-cum_d2;//  N(-d2)
                DbetaDd2  =    -  n_d2; // -n( d2)
            } else {
                alpha     = 0.5;
                DalphaDd1 = 0.0;
                beta      = 0.5;
                DbetaDd2  = 0.0;
            }            
        }
        // down-and-in cash-(at-hit)-or-nothing option
        // a.k.a. american put with cash-or-nothing payoff
        else if (optionType.equals(Type.PUT)) {
            if (strike < spot) {
                alpha     =     cum_d1;//  N(d1)
                DalphaDd1 =       n_d1; //  n(d1)
                beta      =     cum_d2;//  N(d2)
                DbetaDd2  =       n_d2; //  n(d2)
            } else {
                alpha     = 0.5;
                DalphaDd1 = 0.0;
                beta      = 0.5;
                DbetaDd2  = 0.0;
            }            
        }
        else {
            throw new IllegalArgumentException("invalid option type");
        }
        
        muPlusLambda = mu + lambda;
        muMinusLambda = mu - lambda;
        inTheMoney = (optionType.equals(Type.CALL) && strike < spot) || 
                     (optionType.equals(Type.PUT) && strike > spot);
        if (inTheMoney) {
            forward     = 1.0;
            X           = 1.0;
            DXDstrike   = 0.0;
        } else {
            forward = Math.pow(strike / spot, muPlusLambda);
            X       = Math.pow(strike / spot, muMinusLambda);
            //DXDstrike_ = ......;
        }  
        
        // binary cash-or-nothing payoff ?
        if (strikedTypePayoff instanceof CashOrNothingPayoff) {
            CashOrNothingPayoff coo = (CashOrNothingPayoff) strikedTypePayoff;
            K = coo.getCashPayoff();
            DKDstrike = 0.0;
        }
        
        // binary asset-or-nothing payoff ?
        if (strikedTypePayoff instanceof AssetOrNothingPayoff) {
            AssetOrNothingPayoff aoo = (AssetOrNothingPayoff) strikedTypePayoff;
            if (inTheMoney) {
                K = spot;
                DKDstrike = 0.0;                
            }
            else {
                K = aoo.getStrike();
                DKDstrike = 1.0;
            }
        }
    }
    
    public /* @Price */ double value() /* @ReadOnly */ {
        /* @Price */ final double result = K * (forward * alpha + X * beta);
        return result;
    }       
    
    public double delta() /* @ReadOnly */{
        double tempDelta = - spot * stdDev;
        double DalphaDs = DalphaDd1/tempDelta;
        double DbetaDs  = DbetaDd2/tempDelta;

        double DforwardDs, DXDs;
        if (inTheMoney) {
            DforwardDs = 0.0;
            DXDs       = 0.0;
        } else {
            DforwardDs = -muPlusLambda  * forward / spot;
            DXDs       = -muMinusLambda * X       / spot;
        }

        double delta = K * (
              DalphaDs * forward + alpha * DforwardDs
            + DbetaDs  * X       + beta  * DXDs
            );
        return delta;
    }

    public double gamma() /* @ReadOnly */{
        
        double tempDelta = - spot * stdDev;
        double DalphaDs = DalphaDd1/tempDelta;
        double DbetaDs  = DbetaDd2/tempDelta;
        double D2alphaDs2 = -DalphaDs/spot*(1-D1/stdDev);
        double D2betaDs2  = -DbetaDs /spot*(1-D2/stdDev);

        double DforwardDs, DXDs, D2forwardDs2, D2XDs2;
        if (inTheMoney) {
            DforwardDs = 0.0;
            DXDs       = 0.0;
            D2forwardDs2 = 0.0;
            D2XDs2       = 0.0;
        } else {
            DforwardDs = -muPlusLambda  * forward / spot;
            DXDs       = -muMinusLambda * X       / spot;
            D2forwardDs2 = muPlusLambda  * forward / (spot*spot)*(1+muPlusLambda);
            D2XDs2       = muMinusLambda * X       / (spot*spot)*(1+muMinusLambda);
        }

        double gamma = K * (
              D2alphaDs2 * forward   + DalphaDs * DforwardDs
            + DalphaDs   * DforwardDs + alpha   * D2forwardDs2
            + D2betaDs2  * X         + DbetaDs  * DXDs
            + DbetaDs    * DXDs       + beta    * D2XDs2
            );        
        
        return gamma;
    } 
    
    public double theta(final /* @Time */ double maturity) /* @ReadOnly */ {

        if (maturity <= 0.0)
            throw new IllegalArgumentException("negative maturity: " + maturity + " not allowed");

        // actually D.Dr / T
        double DalphaDr = -DalphaDd1/(lambda*stdDev) * (1.0 + mu);
        double DbetaDr  =  DbetaDd2 /(lambda*stdDev) * (1.0 + mu);
        double DforwardDr, DXDr;
        if (inTheMoney) {
            DforwardDr = 0.0;
            DXDr       = 0.0;
        } else {
            DforwardDr = forward * (1.0+(1.0+mu)/lambda) * log_H_S / variance;
            DXDr       = X       * (1.0-(1.0+mu)/lambda) * log_H_S / variance;
        }

        double theta = maturity * K * (
              DalphaDr * forward
            + alpha   * DforwardDr
            + DbetaDr  * X
            + beta    * DXDr
            );      
        
        return theta;
    }    
    
}