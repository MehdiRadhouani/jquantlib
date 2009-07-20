package org.jquantlib.instruments;

import static org.jquantlib.Error.QL_FAIL;
import static org.jquantlib.Error.QL_REQUIRE;

import java.util.List;

import org.jquantlib.Configuration;
import org.jquantlib.cashflow.CashFlow;
import org.jquantlib.cashflow.CashFlows;
import org.jquantlib.cashflow.FloatingRateCoupon;
import org.jquantlib.pricingengines.PricingEngine;
import org.jquantlib.pricingengines.arguments.Arguments;
import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.util.Date;
import org.jquantlib.util.DateFactory;


//\test
//- the correctness of the returned value is tested by checking
//that the price of a cap (resp. floor) decreases
//(resp. increases) with the strike rate.
//- the relationship between the values of caps, floors and the
//resulting collars is checked.
//- the put-call parity between the values of caps, floors and
//swaps is checked.
//- the correctness of the returned implied volatility is tested
//by using it for reproducing the target value.
//- the correctness of the returned value is tested by checking
//it against a known good value.

public class CapFloor extends NewInstrument {
    
    public enum Type { Cap, Floor, Collar };
    
    private Type type_;
    private List<CashFlow> floatingLeg_;
    private List</*@Rate*/ Double> capRates_;
    private List</*@Rate*/ Double> floorRates_;
    private Handle<YieldTermStructure> termStructure_;
    
    public CapFloor(CapFloor.Type type,
            final List<CashFlow> floatingLeg,
            final List</*@Rate*/ Double> capRates,
            final List</*@Rate*/ Double> floorRates,
            final Handle<YieldTermStructure> termStructure,
            final PricingEngine engine){
        
        this.type_ = type;
        this.floatingLeg_ = floatingLeg;
        this.capRates_ = capRates;
        this.floorRates_ = floorRates;
        this.termStructure_ = termStructure;
        
        setPricingEngine(engine);
      
        
        
   if (type_ == Type.Cap || type_ == Type.Collar) {
            QL_REQUIRE(!(capRates_.size() == 0), "no cap rates given");
            // capRates_.reserve(floatingLeg_.size());
            while (capRates_.size() < floatingLeg_.size()) {
                // this looks kind of suspicious...
                capRates_.add(capRates_.get(capRates_.size() - 1));
            }
        }

        if (type_ == Type.Floor || type_ == Type.Collar) {
            QL_REQUIRE(!(floorRates_.size() == 0), "no floor rates given");
            // floorRates_.reserve(floatingLeg_.size());
            while (floorRates_.size() < floatingLeg_.size()) {
                floorRates_.add(floorRates_.get(floorRates_.size() - 1));
            }
        }

        for (CashFlow cashFlow : floatingLeg_) {
            // registerWith(i*);
            cashFlow.addObserver(this);
        }

        termStructure_.addObserver(this);
        Configuration.getSystemConfiguration(null).getGlobalSettings().getEvaluationDate().addObserver(this);
    }
    
    public CapFloor(Type type,
            final List<CashFlow> floatingLeg,
            final List</*@Rate*/ Double> strikes,
            final Handle<YieldTermStructure> termStructure,
            final PricingEngine engine){
        this.type_ = type;
        this.floatingLeg_ = floatingLeg;
        this.termStructure_ = termStructure;
        
        setPricingEngine(engine);
        
        QL_REQUIRE(!(strikes.size()==0), "no strikes given");
        if (type_ == Type.Cap) {
            capRates_ = strikes;
            //capRates_.reserve(floatingLeg_.size());
            while (capRates_.size() < floatingLeg_.size()){
                capRates_.add(capRates_.get(capRates_.size()-1));
            }
        } else if (type_ == Type.Floor) {
            floorRates_ = strikes;
            //floorRates_.reserve(floatingLeg_.size());
            while (floorRates_.size() < floatingLeg_.size()){
                floorRates_.add(floorRates_.get(floorRates_.size()-1));
            }
        } else{
            QL_FAIL("only Cap/Floor types allowed in this constructor");
        }
        
        for (CashFlow cashFlow : floatingLeg_) {
            // registerWith(i*);
            cashFlow.addObserver(this);
        }

        termStructure_.addObserver(this);
        Configuration.getSystemConfiguration(null).getGlobalSettings().getEvaluationDate().addObserver(this);
    }
    
    public /*@Rate*/double atmRate(){
        return CashFlows.atmRate(floatingLeg_, termStructure_);
    }
    
    public boolean isExpired(){
        Date lastPaymentDate = DateFactory.getFactory().getMinDate();
      for (int i=0; i<floatingLeg_.size(); i++){
          //FIXME: kind of ugly... intention: get the last date of all dates in the floatingdate c++ max syntax.
          lastPaymentDate = lastPaymentDate.le(floatingLeg_.get(i).date())?floatingLeg_.get(i).date():lastPaymentDate;
      }
      return lastPaymentDate.le(termStructure_.getLink().referenceDate());
    }
    
    public Date startDate(){
        return CashFlows.startDate(floatingLeg_);
    }
    
    public Date maturityDate() {
        return CashFlows.maturityDate(floatingLeg_);
    }

    public Date lastFixingDate() {
        CashFlow lastCoupon = floatingLeg_.get(floatingLeg_.size() - 1); // no linkedlist :-(
        FloatingRateCoupon lastFloatingCoupon = (FloatingRateCoupon) lastCoupon;
        return lastFloatingCoupon.fixingDate();
    }
//TODO: inner class arguments
//void void setupArguments(/*PricingEngine.arguments* args*/Arguments args)  {
//   CapFloor::arguments* arguments =
//       dynamic_cast<CapFloor::arguments*>(args);
//   QL_REQUIRE(arguments != 0, "wrong argument type");
//
//   Size n = floatingLeg_.size();
//
//   arguments->startTimes.clear();
//   arguments->startTimes.reserve(n);
//
//   arguments->fixingDates.clear();
//   arguments->fixingDates.reserve(n);
//
//   arguments->fixingTimes.clear();
//   arguments->fixingTimes.reserve(n);
//
//   arguments->endTimes.clear();
//   arguments->endTimes.reserve(n);
//
//   arguments->accrualTimes.clear();
//   arguments->accrualTimes.reserve(n);
//
//   arguments->forwards.clear();
//
//   arguments->discounts.clear();
//
//   arguments->nominals.clear();
//   arguments->nominals.reserve(n);
//
//   arguments->gearings.clear();
//   arguments->gearings.reserve(n);
//
//   arguments->capRates.clear();
//
//   arguments->floorRates.clear();
//
//   arguments->type = type_;
//
//   Date today = Settings::instance().evaluationDate();
//   Date settlement = termStructure_->referenceDate();
//   DayCounter counter = termStructure_->dayCounter();
//
//   for (Size i=0; i<n; i++) {
//       boost::shared_ptr<FloatingRateCoupon> coupon =
//           boost::dynamic_pointer_cast<FloatingRateCoupon>(floatingLeg_[i]);
//       QL_REQUIRE(coupon, "non-iborCoupon given");
//       Date beginDate = coupon->accrualStartDate();
//       Time time = counter.yearFraction(settlement, beginDate);
//       arguments->startTimes.push_back(time);
//       Date fixingDate = coupon->fixingDate();
//       arguments->fixingDates.push_back(fixingDate);
//       time = counter.yearFraction(today, fixingDate);
//       arguments->fixingTimes.push_back(time);
//       time = counter.yearFraction(settlement, coupon->date());
//       arguments->endTimes.push_back(time);
//       // this is passed explicitly for precision
//       arguments->accrualTimes.push_back(coupon->accrualPeriod());
//       // this is passed explicitly for precision
//       if (arguments->endTimes.back() >= 0.0) { // but only if needed
//           arguments->forwards.push_back(coupon->adjustedFixing());
//           arguments->discounts.push_back(
//                             termStructure_->discount(coupon->date()));
//       } else {
//           arguments->forwards.push_back(Null<Rate>());
//           arguments->discounts.push_back(Null<DiscountFactor>());
//       }
//       arguments->nominals.push_back(coupon->nominal());
//       Spread spread = coupon->spread();
//       Real gearing = coupon->gearing();
//       QL_REQUIRE(gearing > 0.0, "positive gearing required");
//       arguments->gearings.push_back(gearing);
//       arguments->spreads.push_back(spread);
//       if (type_ == Cap || type_ == Collar)
//           arguments->capRates.push_back((capRates_[i]-spread)/gearing);
//       if (type_ == Floor || type_ == Collar)
//           arguments->floorRates.push_back(
//                                        (floorRates_[i]-spread)/gearing);
//   }
//}

//void CapFloor::arguments::validate() const {
//   QL_REQUIRE(endTimes.size() == startTimes.size(),
//              "number of start times (" << startTimes.size()
//              << ") different from that of end times ("
//              << endTimes.size() << ")");
//   QL_REQUIRE(accrualTimes.size() == startTimes.size(),
//              "number of start times (" << startTimes.size()
//              << ") different from that of accrual times ("
//              << accrualTimes.size() << ")");
//   QL_REQUIRE(type == CapFloor::Floor ||
//              capRates.size() == startTimes.size(),
//              "number of start times (" << startTimes.size()
//              << ") different from that of cap rates ("
//              << capRates.size() << ")");
//   QL_REQUIRE(type == CapFloor::Cap ||
//              floorRates.size() == startTimes.size(),
//              "number of start times (" << startTimes.size()
//              << ") different from that of floor rates ("
//              << floorRates.size() << ")");
//   QL_REQUIRE(gearings.size() == startTimes.size(),
//              "number of start times (" << startTimes.size()
//              << ") different from that of gearings ("
//              << floorRates.size() << ")");
//   QL_REQUIRE(nominals.size() == startTimes.size(),
//              "number of start times (" << startTimes.size()
//              << ") different from that of nominals ("
//              << nominals.size() << ")");
//}
//
//Volatility CapFloor::impliedVolatility(Real targetValue,
//                                      Real accuracy,
//                                      Size maxEvaluations,
//                                      Volatility minVol,
//                                      Volatility maxVol) const {
//   calculate();
//   QL_REQUIRE(!isExpired(), "instrument expired");
//
//   Volatility guess = 0.10;   // no way we can get a more accurate one
//
//   ImpliedVolHelper f(*this, termStructure_, targetValue);
//   Brent solver;
//   //NewtonSafe solver;
//   solver.setMaxEvaluations(maxEvaluations);
//   return solver.solve(f, accuracy, guess, minVol, maxVol);
//}
//
//
//CapFloor::ImpliedVolHelper::ImpliedVolHelper(
//                         const CapFloor& cap,
//                         const Handle<YieldTermStructure>& termStructure,
//                         Real targetValue)
//: termStructure_(termStructure), targetValue_(targetValue) {
//
//   vol_ = boost::shared_ptr<SimpleQuote>(new SimpleQuote(0.0));
//   Handle<Quote> h(vol_);
//   engine_ = boost::shared_ptr<PricingEngine>(new BlackCapFloorEngine(h));
//   cap.setupArguments(engine_->getArguments());
//
//   results_ =
//       dynamic_cast<const Instrument::results*>(engine_->getResults());
//}
//
//Real CapFloor::ImpliedVolHelper::operator()(Volatility x) const {
//   vol_->setValue(x);
//   engine_->calculate();
//   return results_->value-targetValue_;
//}
//
//Real CapFloor::ImpliedVolHelper::derivative(Volatility x) const {
//   vol_->setValue(x);
//   engine_->calculate();
//   return 0.0;
//   //return results_->vega;
//}
//
//std::ostream& operator<<(std::ostream& out, CapFloor::Type t) {
//   switch (t) {
//     case CapFloor::Cap:
//       return out << "Cap";
//     case CapFloor::Floor:
//       return out << "Floor";
//     case CapFloor::Collar:
//       return out << "Collar";
//     default:
//       QL_FAIL("unknown CapFloor::Type (" << Integer(t) << ")");
//   }
//}

    
    
    
    


    @Override
    protected void performCalculations() throws ArithmeticException {
        // TODO Auto-generated method stub
        
    }
    @Override
    protected void setupArguments(Arguments arguments) {
        // TODO Auto-generated method stub
        
    }
    
    
    

//    */
//    class CapFloor : public Instrument {
//      public:
//        enum Type { Cap, Floor, Collar };
//        class arguments;
//        class engine;
//        CapFloor(Type type,
//                 const Leg& floatingLeg,
//                 const std::vector<Rate>& capRates,
//                 const std::vector<Rate>& floorRates,
//                 const Handle<YieldTermStructure>& termStructure,
//                 const boost::shared_ptr<PricingEngine>& engine);
//        CapFloor(Type type,
//                 const Leg& floatingLeg,
//                 const std::vector<Rate>& strikes,
//                 const Handle<YieldTermStructure>& termStructure,
//                 const boost::shared_ptr<PricingEngine>& engine);
//        //! \name Instrument interface
//        //@{
//        bool isExpired() const;
//        void setupArguments(PricingEngine::arguments*) const;
//        //@}
//        //! \name Inspectors
//        //@{
//        Type type() const { return type_; }
//        const Leg& leg() const {
//            return floatingLeg_;
//        }
//        const std::vector<Rate>& capRates() const {
//            return capRates_;
//        }
//        const std::vector<Rate>& floorRates() const {
//            return floorRates_;
//        }
//        const Leg& floatingLeg() const {
//            return floatingLeg_;
//        }
//        Rate atmRate() const;
//        Date startDate() const;
//        Date maturityDate() const;
//        Date lastFixingDate() const;
//        //@}
//        //! implied term volatility
//        Volatility impliedVolatility(Real price,
//                                     Real accuracy = 1.0e-4,
//                                     Size maxEvaluations = 100,
//                                     Volatility minVol = 1.0e-7,
//                                     Volatility maxVol = 4.0) const;
//      private:
//        Type type_;
//        Leg floatingLeg_;
//        std::vector<Rate> capRates_;
//        std::vector<Rate> floorRates_;
//        Handle<YieldTermStructure> termStructure_;
//        // helper class for implied volatility calculation
//        class ImpliedVolHelper {
//          public:
//            ImpliedVolHelper(const CapFloor&,
//                             const Handle<YieldTermStructure>&,
//                             Real targetValue);
//            Real operator()(Volatility x) const;
//            Real derivative(Volatility x) const;
//          private:
//            boost::shared_ptr<PricingEngine> engine_;
//            Handle<YieldTermStructure> termStructure_;
//            Real targetValue_;
//            boost::shared_ptr<SimpleQuote> vol_;
//            const Instrument::results* results_;
//        };
//    };
//
//    //! Concrete cap class
//    /*! \ingroup instruments */
//    class Cap : public CapFloor {
//      public:
//        Cap(const Leg& floatingLeg,
//            const std::vector<Rate>& exerciseRates,
//            const Handle<YieldTermStructure>& termStructure,
//            const boost::shared_ptr<PricingEngine>& engine)
//        : CapFloor(CapFloor::Cap, floatingLeg,
//                   exerciseRates, std::vector<Rate>(),
//                   termStructure, engine) {}
//    };
//
//    //! Concrete floor class
//    /*! \ingroup instruments */
//    class Floor : public CapFloor {
//      public:
//        Floor(const Leg& floatingLeg,
//              const std::vector<Rate>& exerciseRates,
//              const Handle<YieldTermStructure>& termStructure,
//              const boost::shared_ptr<PricingEngine>& engine)
//        : CapFloor(CapFloor::Floor, floatingLeg,
//                   std::vector<Rate>(), exerciseRates,
//                   termStructure, engine) {}
//    };
//
//    //! Concrete collar class
//    /*! \ingroup instruments */
//    class Collar : public CapFloor {
//      public:
//        Collar(const Leg& floatingLeg,
//               const std::vector<Rate>& capRates,
//               const std::vector<Rate>& floorRates,
//               const Handle<YieldTermStructure>& termStructure,
//               const boost::shared_ptr<PricingEngine>& engine)
//        : CapFloor(CapFloor::Collar, floatingLeg, capRates, floorRates,
//                   termStructure, engine) {}
//    };
//
//
//    //! %Arguments for cap/floor calculation
//    class CapFloor::arguments : public virtual PricingEngine::arguments {
//      public:
//        arguments() : type(CapFloor::Type(-1)) {}
//        CapFloor::Type type;
//        std::vector<Time> startTimes;
//        std::vector<Date> fixingDates;
//        std::vector<Time> fixingTimes;
//        std::vector<Time> endTimes;
//        std::vector<Time> accrualTimes;
//        std::vector<Rate> capRates;
//        std::vector<Rate> floorRates;
//        std::vector<Rate> forwards;
//        std::vector<Real> gearings;
//        std::vector<Real> spreads;
//        std::vector<DiscountFactor> discounts;
//        std::vector<Real> nominals;
//        void validate() const;
//    };
//
//    //! base class for cap/floor engines
//    class CapFloor::engine
//        : public GenericEngine<CapFloor::arguments, CapFloor::results> {};
//
//    std::ostream& operator<<(std::ostream&, CapFloor::Type);




}