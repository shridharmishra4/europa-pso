#include "SymbolDomain.hh"

namespace EUROPA {

  SymbolDomain::SymbolDomain() 
    : EnumeratedDomain(false, getDefaultTypeName().c_str()){}

  SymbolDomain::SymbolDomain(const char* typeName)
    : EnumeratedDomain(false, typeName){}
 
  SymbolDomain::SymbolDomain(double value, const char* typeName)
    : EnumeratedDomain(value, false, typeName){}

  SymbolDomain::SymbolDomain(const std::list<double>& values, 
                             const char* typeName)
    : EnumeratedDomain(values, false, typeName){}

  SymbolDomain::SymbolDomain(const AbstractDomain& org)
    : EnumeratedDomain(org) {}


  const LabelStr&
  SymbolDomain::getDefaultTypeName()
  {
    static const LabelStr sl_typeName("symbol");
    return(sl_typeName);
  }


  SymbolDomain *
  SymbolDomain::copy() const
  {
    SymbolDomain * ptr = new SymbolDomain(*this);
    check_error(ptr != NULL);
    return ptr;
  }


} // namespace EUROPA
