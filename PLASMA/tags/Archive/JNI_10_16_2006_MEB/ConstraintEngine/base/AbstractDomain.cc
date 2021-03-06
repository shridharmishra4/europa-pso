#include "AbstractDomain.hh"
#include "Entity.hh"
#include "Error.hh"
#include "Utils.hh"
#include "Debug.hh"

namespace EUROPA {

  ostream& operator<<(ostream& os, const AbstractDomain& dom) {
    dom >> os;
    return(os);
  }

  DomainComparator* DomainComparator::s_instance = NULL;

  /**
   * @brief Constructor overwrites prior static instance, effecting a last-writer wins policy
   * for establishing the shared allocator.
   */
  DomainComparator::DomainComparator(){
    // Assign instance to current value
    s_instance = this;
  }

  /**
   * @brief On destruction, if the static instance is this object, then set it to null.
   */
  DomainComparator::~DomainComparator(){
    if(s_instance == this)
      s_instance = NULL;
  }

  const DomainComparator& DomainComparator::getComparator(){
    if(s_instance == NULL)
      new DomainComparator();
    return *s_instance;
  }

  void  DomainComparator::setComparator(DomainComparator* comparator) { 
    //check_error(s_instance == NULL, "The comparator can only be set when it is currently null");
    s_instance = comparator;
  }
  
  bool DomainComparator::comparatorIsNull() {
    return s_instance == NULL;
  }

  /**
   * @brief Implements the default tests for comparison.
   */
  bool DomainComparator::canCompare(const AbstractDomain& domx, const AbstractDomain& domy) const {
	  // The following numeric test now replaces this test.
    // If either is Boolean, both must be Boolean. Assumes they type names don't matter
    //if(domx.isBool() || domy.isBool())
    //   return(domx.isBool() && domy.isBool());
  
    // If either is numeric, both must be numeric. Assumes they type names don't matter
    if(domx.isNumeric() || domy.isNumeric())
      return(domx.isNumeric() && domy.isNumeric());
			
    // If either is string, both must be string. Assumes they type names don't matter
    if(domx.isString() && domy.isString())
      return(domx.isString() && domy.isString());

    // Now we know that neither is numeric. We also can infer that
    // they must be enumerated since neither is numeric. Thus, the
    // types must match
    return (domx.getTypeName() == domy.getTypeName());
  }

  DomainListener::DomainListener()
    : m_id(this) {
  }

  DomainListener::~DomainListener() {
    m_id.remove();
  }

  const DomainListenerId& DomainListener::getId() const {
    return(m_id);
  }

  AbstractDomain::AbstractDomain(bool closed, bool enumerated, const char* typeName)
    : m_closed(closed), m_enumerated(enumerated),  m_typeName(typeName), m_minDelta(EPSILON) {}

  AbstractDomain::AbstractDomain(const AbstractDomain& org)
    : m_closed(org.m_closed), m_enumerated(org.m_enumerated), m_typeName(org.m_typeName), m_minDelta(org.m_minDelta){}

  AbstractDomain::~AbstractDomain() {}

  bool AbstractDomain::operator==(const AbstractDomain& dom) const {
    return(m_closed == dom.m_closed && isFinite() == dom.isFinite());
  }

  bool AbstractDomain::operator!=(const AbstractDomain& dom) const {
    return(!operator==(dom));
  }

  void AbstractDomain::close() {
    check_error(isOpen(), "Attempted to close a domain that is already closed");
    m_closed = true;
    notifyChange(DomainListener::CLOSED);
    if (isEmpty()) // Empty initially, want to generate the event
      empty();
  }

  void AbstractDomain::open() {
    check_error(isClosed(), "Attempted to re-open a domain that is already open.");
    m_closed = false;
    notifyChange(DomainListener::OPENED);
  }

  bool AbstractDomain::isClosed() const {
    return(m_closed);
  }

  bool AbstractDomain::isOpen() const {
    return(!m_closed);
  }

  const LabelStr& AbstractDomain::getTypeName() const {
    return(m_typeName);
  }

  bool AbstractDomain::isEnumerated() const {
    return(m_enumerated);
  }

  bool AbstractDomain::isInterval() const {
    return(!m_enumerated);
  }

  bool AbstractDomain::isInfinite() const {
    return(!isFinite());
  }

  void AbstractDomain::setListener(const DomainListenerId& listener) {
    check_error(m_listener.isNoId()); // Only set once
    m_listener = listener;

    // Now we want to send any events, since we added the listener a bit late in the game
    if (isClosed()) {
      notifyChange(DomainListener::CLOSED);
      if (isEmpty())
        notifyChange(DomainListener::EMPTIED); // Have to do this to force inconsistency
    }
  }

  const DomainListenerId& AbstractDomain::getListener() const {
    return(m_listener);
  }

  void AbstractDomain::notifyChange(const DomainListener::ChangeType& changeType) {
    checkError(m_listener.isNoId() || m_listener.isValid(), m_listener);

    if (m_listener.isNoId())
      return;

    //Ensure we get the tightes notification if restricted to a singleton
    if(DomainListener::isRestriction(changeType) && isSingleton())
      m_listener->notifyChange(DomainListener::RESTRICT_TO_SINGLETON);
    else
      m_listener->notifyChange(changeType);
  }

  void AbstractDomain::operator>>(ostream& os) const {
    os << getTypeName().toString() << (m_closed ? ":CLOSED" : ":OPEN");
  }

  double AbstractDomain::translateNumber(double number, bool) const {
    return(number);
  }

  bool AbstractDomain::check_value(double value) const {
    checkPrecision(value);
    return(!isNumeric() || (value >= MINUS_INFINITY && value <= PLUS_INFINITY));
  }

  /**
   * @see DomainComparator::comparator
   */
  bool AbstractDomain::canBeCompared(const AbstractDomain& domx, const AbstractDomain& domy) {
    debugMsg("AbstractDomain:canBeCompared", "domx.isBool " << domx.isBool() << " domx.isNumeric " << domx.isNumeric() << " domx.isString " << domx.isString() << " domx.isSymbolic " << domx.isSymbolic());
    debugMsg("AbstractDomain:canBeCompared", "domy.isBool " << domy.isBool() << " domy.isNumeric " << domy.isNumeric() << " domy.isString " << domy.isString() << " domy.isSymbolic " << domy.isSymbolic());
    debugMsg("AbstractDomain:canBeCompared", "type of domx " << domx.getTypeName().toString() << " type of domy " << domy.getTypeName().toString());
    debugMsg("AbstractDomain:canBeCompared", domx.toString());
    debugMsg("AbstractDomain:canBeCompared", domy.toString());
    bool result = DomainComparator::getComparator().canCompare(domx, domy);
    debugMsg("AbstractDomain:canBeCompared", "returning " << result);
    return result;
  }


  bool AbstractDomain::areBoundsFinite() const {
    return (!isNumeric() || 
	    (!isEmpty() && isClosed() && 
	     getUpperBound() < PLUS_INFINITY && getLowerBound() > MINUS_INFINITY));
  }

  std::string AbstractDomain::toString() const {
    std::stringstream s_stream;
    s_stream << *this;
    return s_stream.str();
  }

  void AbstractDomain::assertSafeComparison(const AbstractDomain& domA, const AbstractDomain& domB){
    check_error(canBeCompared(domA, domB),
		domA.getTypeName().toString() + " cannot be compared with " + domB.getTypeName().toString());

  }

  std::string  AbstractDomain::toString(double value) const {
    checkError(isMember(value),  value << " not in " << toString());

    if(isNumeric())
      return EUROPA::toString(value);
    else {
      checkError(LabelStr::isString(value), toString());
      return LabelStr(value).toString();
    }
  }
}
