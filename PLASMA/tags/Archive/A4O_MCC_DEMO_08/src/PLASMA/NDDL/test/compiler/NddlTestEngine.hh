#ifndef _H_NDDL_TEST_ENGINE_
#define _H_NDDL_TEST_ENGINE_

#include "Engine.hh"

using namespace EUROPA;

class NddlTestEngine : public EngineBase
{
  public:
	NddlTestEngine();
	virtual ~NddlTestEngine();

	virtual void init();
	void run(const char* txSource, bool useInterpreter);

  protected:
	virtual void createModules();
};

#include "Nddl.hh"
#include "ConstraintEngine.hh"
#include "PlanDatabase.hh"
#include "PlanDatabaseWriter.hh"
#include "Rule.hh"
#include "RulesEngine.hh"
#include "DefaultPropagator.hh"

// Transactions
#include "DbClientTransactionPlayer.hh"
#include "DbClientTransactionLog.hh"

// Support for registered constraints
#include "ConstraintFactory.hh"
#include "Constraints.hh"
#include "EqualityConstraintPropagator.hh"
#include "CommonAncestorConstraint.hh"
#include "HasAncestorConstraint.hh"

// Misc
#include "ModuleConstraintEngine.hh"
#include "ModulePlanDatabase.hh"
#include "ModuleTemporalNetwork.hh"
#include "ModuleRulesEngine.hh"
#include "ModuleNddl.hh"

#include <fstream>
#include <sstream>

void initialize(CESchema* ces)
{
  /*
   *  TODO: constraint registration below needs to be removed, ModuleConstraintLibrary::initialize takes care of this
   *  leaving it for now for backwards compatibility since some constraints are named differently
   * and some other constraints like Lock and Ancestor are not registered by ModuleConstraintLibrary::initialize for some reason
   */

  // Procedural Constraints used with Default Propagation
  REGISTER_CONSTRAINT(ces,EqualConstraint, "eq", "Default");
  REGISTER_CONSTRAINT(ces,NotEqualConstraint, "neq", "Default");
  REGISTER_CONSTRAINT(ces,LessThanEqualConstraint, "leq", "Default");
  REGISTER_CONSTRAINT(ces,LessThanConstraint, "lessThan", "Default");
  REGISTER_CONSTRAINT(ces,AddEqualConstraint, "addEq", "Default");
  REGISTER_CONSTRAINT(ces,NegateConstraint, "neg", "Default");
  REGISTER_CONSTRAINT(ces,MultEqualConstraint, "mulEq", "Default");
  REGISTER_CONSTRAINT(ces,AddMultEqualConstraint, "addMulEq", "Default");
  REGISTER_CONSTRAINT(ces,AddMultEqualConstraint, "addmuleq", "Default");
  REGISTER_CONSTRAINT(ces,SubsetOfConstraint, "subsetOf", "Default");
  REGISTER_CONSTRAINT(ces,SubsetOfConstraint, "Singleton", "Default");
  REGISTER_CONSTRAINT(ces,LockConstraint, "Lock", "Default");
  REGISTER_CONSTRAINT(ces,CommonAncestorConstraint, "commonAncestor", "Default");
  REGISTER_CONSTRAINT(ces,HasAncestorConstraint, "hasAncestor", "Default");
  REGISTER_CONSTRAINT(ces,TestEQ, "testEQ", "Default");
  REGISTER_CONSTRAINT(ces,TestLEQ, "testLEQ", "Default");
  REGISTER_CONSTRAINT(ces,EqualSumConstraint, "sum", "Default");
}

NddlTestEngine::NddlTestEngine()
{
}

void NddlTestEngine::init()
{
    createModules();
    doStart();
    initialize((CESchema*)getComponent("CESchema"));
}

NddlTestEngine::~NddlTestEngine()
{
    doShutdown();
}

void NddlTestEngine::createModules()
{
    addModule((new ModuleConstraintEngine())->getId());
    addModule((new ModuleConstraintLibrary())->getId());
    addModule((new ModulePlanDatabase())->getId());
    addModule((new ModuleRulesEngine())->getId());
    addModule((new ModuleTemporalNetwork())->getId());
    addModule((new ModuleNddl())->getId());
}

// Used to be in main, but now shared by NddlResourceTestEngine
void NddlTestEngine::run(const char* txSource, bool useInterpreter)
{
    PlanDatabase* planDatabase = (PlanDatabase*) getComponent("PlanDatabase");
    planDatabase->getClient()->enableTransactionLogging();

    Schema* schema = (Schema*) getComponent("Schema");
    RuleSchema* ruleSchema = (RuleSchema*) getComponent("RuleSchema");

    if (useInterpreter) {
        executeScript("nddl-xml",txSource,true /*isFile*/);
    }
    else {
        EUROPA::NDDL::loadSchema(schema->getId(),ruleSchema->getId());
        executeScript("nddl-xml-txn",txSource,true /*isFile*/);
    }

    ConstraintEngine* ce = (ConstraintEngine*)getComponent("ConstraintEngine");
    assert(ce->constraintConsistent());

    PlanDatabaseWriter::write(planDatabase->getId(), std::cout);
    std::cout << "Finished\n";
}

#endif // _H_NDDL_TEST_ENGINE_
