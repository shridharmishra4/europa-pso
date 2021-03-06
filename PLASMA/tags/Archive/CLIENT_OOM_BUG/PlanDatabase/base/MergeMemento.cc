#include "MergeMemento.hh"
#include "Token.hh"
#include "PlanDatabase.hh"
#include "ConstraintLibrary.hh"
#include "Utils.hh"
#include <map>

namespace EUROPA{

  /**
   * @brief Helper method to lookup a replacement for a variable or use the one u r given.
   *
   * The need for this arises out of Token merging where we want to migrate any constraints on
   * a Token being merged to its active token. Thus, any variables of the Token being merged which
   * participate in a constraint shall be replaced with the equivalent variable in the active
   * token.
   * @param lookup A lookup table for variable in the merged token to corresponding variable in the active Token.
   * The key is a double, which is the encoding of the variable Id.
   * @param var The variable to be replaced, if found. It may not be in the map, indicating it is not a variable of the
   * merged token.
   * @return Variable Id Entry in lookup table if the given token is part of the token being merged, 
   * otherwise returns var.
   * @todo The algorithm for splitting is very sub-optimal in the non-chronological case. Consider in the future
   * an algorithm to migrate consequenecs rather than force additional splits. This is similar to deactivation
   * of a token in its simple, robust, and potentially inefficient treatment of such non-chronological retractions.
   */
  ConstrainedVariableId checkForReplacement(const std::map<double, ConstrainedVariableId>& lookup, const ConstrainedVariableId& var){
    std::map<double, ConstrainedVariableId>::const_iterator it = lookup.find((double) var);
    if(it == lookup.end())
      return var;
    else
      return (it->second);
  }

  MergeMemento::MergeMemento(const TokenId& inactiveToken, const TokenId& activeToken)
    :m_inactiveToken(inactiveToken), m_activeToken(activeToken), m_undoing(false){

    // Iterate over all variables and impose unaries on spec domain and the corresponding variable in
    // Active Token.
    const std::vector<ConstrainedVariableId>& inactiveVariables = inactiveToken->getVariables();
    const std::vector<ConstrainedVariableId>& activeVariables = activeToken->getVariables();

    check_error(inactiveVariables.size() == activeVariables.size());

    std::map<double, ConstrainedVariableId> varMap;
    std::set<ConstraintId> deactivatedConstraints;

    //Exclude this for the state variable, which will necessarily conflict with the target active token
    for(unsigned int i=1; i<inactiveVariables.size(); i++){
      check_error(varMap.find((double) inactiveVariables[i]) == varMap.end());
      // Add to the map to support lookup and store all constraints on any variables
      varMap.insert(std::pair<double, ConstrainedVariableId>((double) inactiveVariables[i], activeVariables[i]));
      inactiveVariables[i]->constraints(deactivatedConstraints);
      // i.e. not a state variable

      // Post restrictions arising from the base domain
      activeVariables[i]->handleBase(inactiveVariables[i]->baseDomain());

      // if the variable is specified, then post its value to active variable
      if(inactiveVariables[i]->isSpecified()){
	checkError(inactiveVariables[i]->lastDomain().isSingleton(),
		   inactiveVariables[i]->toString() << " is specified but not a singleton. Pas possible!");
	activeVariables[i]->handleSpecified(inactiveVariables[i]->lastDomain().getSingletonValue());
      }

      // Deactivate variable
      inactiveVariables[i]->deactivate();
    }

    // Iterate over all constraints and deactivate them, as well as create and store new ones where necessary
    for(std::set<ConstraintId>::const_iterator it = deactivatedConstraints.begin(); it != deactivatedConstraints.end(); ++it){
      ConstraintId constraint = *it;
      m_deactivatedConstraints.push_back(constraint);
      const std::vector<ConstrainedVariableId>& variables = constraint->getScope();
      std::vector<ConstrainedVariableId> newScope;

      for(unsigned int i=0;i<variables.size();i++){
	ConstrainedVariableId var = checkForReplacement(varMap, variables[i]);
	newScope.push_back(var);
      }

      // Create the new constraint if it is not a standard constraint - different for unary vs. nary
      ConstraintId newConstraint;

      // If it is not a standard constraint, then we need to create a surrogate as the target active token
      // may not have it already.
      if(!m_inactiveToken->isStandardConstraint(constraint)){
	newConstraint = ConstraintLibrary::createConstraint(constraint->getName(),
							    m_activeToken->getPlanDatabase()->getConstraintEngine(),
							    newScope);

	// Now set the source on the new constraint to give opportunity to pass data
	newConstraint->setSource(constraint);
      }

      m_newConstraints.push_back(newConstraint); // Note that it might be a noId()
    }
  }

  MergeMemento::~MergeMemento() {}

  void MergeMemento::undo(bool activeTokenDeleted){
    checkError(activeTokenDeleted || m_activeToken.isValid(), m_activeToken);
    check_error(!m_newConstraints.empty());
 
    // Start by removing all the new constraints that were created. To avoid a call back 
    // into this method for synching data structures, we set a flag for undoing
    m_undoing = true;
    discardAll(m_newConstraints);

    // Clear the deactivated ocnstraints
    m_deactivatedConstraints.clear();


    // Iterate over all active token variables and trigger a reset of the domain
    if(!activeTokenDeleted){
      const std::vector<ConstrainedVariableId>& activeVariables = m_activeToken->getVariables();
      for(unsigned int i=1;i<activeVariables.size();i++) // We skip the State Variable
	activeVariables[i]->handleReset();
    }

    // Iterate over all variables in this token and trigger a reset of the domain to force
    // re-evaluation
    const std::vector<ConstrainedVariableId>& inactiveVariables = m_inactiveToken->getVariables();
    for(unsigned int i=1;i<inactiveVariables.size();i++){ // We skip the State Variable
      inactiveVariables[i]->undoDeactivation();
      inactiveVariables[i]->handleReset();
    }
    m_undoing = false;
  }

  void MergeMemento::handleRemovalOfInactiveConstraint(const ConstraintId& constraint){
    check_error(m_deactivatedConstraints.size() + m_inactiveToken->getVariables().size() >= m_newConstraints.size());
    checkError(!constraint->isActive(), constraint->toString());

    if(m_undoing)
      return;

    // Iterate through the lists and delete when found
    std::list<ConstraintId>::iterator it_1 = m_deactivatedConstraints.begin();
    std::list<ConstraintId>::iterator it_2 = m_newConstraints.begin();

    while(it_1 != m_deactivatedConstraints.end()){
      if((*it_1) == constraint){
	ConstraintId newConstraint = *it_2;

	// Ensure that if a constraint was migrated, it has the same scope length at least.
	check_error(newConstraint.isNoId() || (newConstraint->getScope().size() == constraint->getScope().size()));

	// Remove from both lists at this location.
	m_deactivatedConstraints.erase(it_1);
	m_newConstraints.erase(it_2);

	// Now delete the new constraint which arose from migration, if it was migrated
	if(!newConstraint.isNoId())
	  delete (Constraint*) newConstraint;

	return;
      }

      it_1++;
      it_2++;
    }
  }
}
