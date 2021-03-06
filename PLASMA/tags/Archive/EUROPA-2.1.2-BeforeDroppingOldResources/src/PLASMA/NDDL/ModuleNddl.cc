#include "ModuleNddl.hh"
#include "PlanDatabase.hh"
#include "Rule.hh"
#include "NddlXml.hh"
#include "NddlInterpreter.hh"

namespace EUROPA {

  ModuleNddl::ModuleNddl()
      : Module("NDDL")
  {

  }

  ModuleNddl::~ModuleNddl()
  {
  }

  void ModuleNddl::initialize()
  {
  }

  void ModuleNddl::uninitialize()
  {
  }

  void ModuleNddl::initialize(EngineId engine)
  {
      NddlInterpreter* interp = new NddlInterpreter(engine);
	  engine->addLanguageInterpreter("nddl", interp);
	  engine->addLanguageInterpreter("nddl-ast", new NddlToASTInterpreter(engine));

      // TODO: This is only to make it visible in java
	  // drop this when current Java parser gets downgraded to nddl2
	  engine->addLanguageInterpreter("nddl3", interp);

      PlanDatabase* pdb = (PlanDatabase*)engine->getComponent("PlanDatabase");
      RuleSchema* rs = (RuleSchema*)engine->getComponent("RuleSchema");
	  engine->addLanguageInterpreter("nddl-xml", new NddlXmlInterpreter(pdb->getClient(),rs->getId()));
  }

  void ModuleNddl::uninitialize(EngineId engine)
  {
	  LanguageInterpreter *old;

	  engine->removeLanguageInterpreter("nddl3");
	  old = engine->removeLanguageInterpreter("nddl");
	  check_error(old != NULL);
      delete old;

	  old = engine->removeLanguageInterpreter("nddl-ast");
	  check_error(old != NULL);
	  delete old;

	  old = engine->removeLanguageInterpreter("nddl-xml");
	  check_error(old != NULL);
	  delete old;
	  // TODO: Finish cleanup
  }
}
