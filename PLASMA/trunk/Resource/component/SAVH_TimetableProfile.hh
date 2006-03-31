#ifndef _H_SAVH_TimetableProfile
#define _H_SAVH_TimetableProfile

#include "SAVH_ResourceDefs.hh"
#include "SAVH_Profile.hh"
#include "DomainListener.hh"

namespace EUROPA {
  namespace SAVH {
    
    class TimetableProfile : public Profile {
    public:
      TimetableProfile(const ConstraintEngineId ce, const FVDetectorId flawDetector);
      
    protected:
    private:
      void initRecompute(InstantId inst);
      void initRecompute();
      void recomputeLevels(InstantId inst);
      void handleTransactionAdded(const TransactionId t);
      void handleTransactionRemoved(const TransactionId t);
      void handleTransactionTimeChanged(const TransactionId t, const DomainListener::ChangeType& type);
      void handleTransactionQuantityChanged(const TransactionId t, const DomainListener::ChangeType& type);
      void handleTransactionsOrdered(const TransactionId t1, const TransactionId t2);
      
      double m_lowerLevelMin, m_lowerLevelMax, m_upperLevelMin, m_upperLevelMax;
      double m_minCumulativeConsumption, m_maxCumulativeConsumption;
      double m_minCumulativeProduction, m_maxCumulativeProduction;
      
    };
  }
}

#endif
