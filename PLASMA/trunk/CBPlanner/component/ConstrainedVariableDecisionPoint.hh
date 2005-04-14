#ifndef _H_ConstrainedVariableDecisionPoint
#define _H_ConstrainedVariableDecisionPoint

#include "DecisionPoint.hh"
#include "ConstrainedVariable.hh"
#include <vector>

namespace EUROPA {

  class ConstrainedVariableDecisionPoint : public DecisionPoint {
  public:
    virtual ~ConstrainedVariableDecisionPoint();

    const bool assign();
    const bool retract();
    const bool hasRemainingChoices();
    void initializeChoices();

    const ConstrainedVariableId& getVariable() const;

    //QUICK FIX to support decision manager extension.
    std::vector<double>& getChoices();

    void print(std::ostream& os) const;
  protected:
    ConstrainedVariableDecisionPoint(const DbClientId& dbClient, const ConstrainedVariableId&, const OpenDecisionManagerId& odm);
  private:
    friend class OpenDecisionManager;
    friend class DefaultOpenDecisionManager;
    friend class HSTSOpenDecisionManager;

    unsigned int getNrChoices();
    const double getChoiceValue(const unsigned int index) const;
    
    std::vector<double> m_choices;
    unsigned int m_choiceIndex;
    ConstrainedVariableId m_var;
    OpenDecisionManagerId m_odm;
  };

std::ostream& operator <<(std::ostream& os, const Id<ConstrainedVariableDecisionPoint>&);

}
#endif
