#include "EnumeratedDomain.hh"
#include "LabelStr.hh"
#include "Entity.hh"
#include <cmath>

namespace Prototype {

  const AbstractDomain::DomainType& EnumeratedDomain::getType() const {
    static const AbstractDomain::DomainType s_type = AbstractDomain::REAL_ENUMERATION;
    return(s_type);
  }

  EnumeratedDomain::EnumeratedDomain(bool isNumeric)
    : AbstractDomain(false, true, DomainListenerId::noId()), m_isNumeric(isNumeric) {
  }

  EnumeratedDomain::EnumeratedDomain(const std::list<double>& values,
                                     bool closed,
                                     const DomainListenerId& listener, 
                                     bool isNumeric)
    : AbstractDomain(false, true, listener), m_isNumeric(isNumeric) {
    for (std::list<double>::const_iterator it = values.begin(); it != values.end(); ++it)
      insert(*it);
    if (closed)
      close();
  }

  EnumeratedDomain::EnumeratedDomain(double value,
                                     const DomainListenerId& listener,
                                     bool isNumeric)
    : AbstractDomain(false, true, listener), m_isNumeric(isNumeric) {
    insert(value);
    close();
  }

  EnumeratedDomain::EnumeratedDomain(const EnumeratedDomain& org)
    : AbstractDomain(org.m_closed, true, DomainListenerId::noId()) {
    m_values = org.m_values;
    m_isNumeric = org.m_isNumeric;
  }

  bool EnumeratedDomain::isFinite() const {
    check_error(!isDynamic());
    return(true); // Always finite, even if bounds are infinite, since there are always a finite number of values to select.
  }

  bool EnumeratedDomain::isNumeric() const {
    return(m_isNumeric);
  }

  // What if it's dynamic? --wedgingt 2004 Mar 3
  bool EnumeratedDomain::isSingleton() const {
    return(m_values.size() == 1);
  }

  // What if it's dynamic? --wedgingt 2004 Mar 3
  bool EnumeratedDomain::isEmpty() const {
    return(m_values.empty());
  }

  void EnumeratedDomain::empty() {
    m_values.clear();
    notifyChange(DomainListener::EMPTIED);
  }

  void EnumeratedDomain::close() {
    AbstractDomain::close();
  }

  int EnumeratedDomain::getSize() const {
    check_error(!isDynamic());
    return(m_values.size());
  }

  void EnumeratedDomain::insert(double value) {
    check_error(check_value(value));
    std::set<double>::iterator it = m_values.begin();
    for ( ; it != m_values.end(); it++) {
      if (fabs(value - *it) < minDelta())
        return; // Already a member.
      if (value < *it) // Since members are sorted, value goes before *it.
        break;
    }
    m_values.insert(it, value);
    // We only consider insertion a relaxation if the domain is closed.
    if (!isDynamic())
      notifyChange(DomainListener::RELAXED);
  }

  void EnumeratedDomain::remove(double value) {
    check_error(check_value(value));
    std::set<double>::iterator it = m_values.begin();
    for ( ; it != m_values.end(); it++)
      if (fabs(value - *it) < minDelta())
        break;
    if (it == m_values.end())
      return; // not present: no-op
    m_values.erase(it);
    if (!isEmpty())
      notifyChange(DomainListener::VALUE_REMOVED);
    else
      notifyChange(DomainListener::EMPTIED);
  }

  void EnumeratedDomain::set(const AbstractDomain& dom) {
    intersect(dom);
    notifyChange(DomainListener::SET);
  }

  void EnumeratedDomain::set(double value) {
    if (!isMember(value)) {
      empty();
      return;
    }

    m_values.clear();
    m_values.insert(value);
    notifyChange(DomainListener::SET_TO_SINGLETON);
  }

  void EnumeratedDomain::reset(const AbstractDomain& dom) {
    if (*this != dom) {
      relax(dom);
      notifyChange(DomainListener::RESET);
    }
  }

  bool EnumeratedDomain::equate(AbstractDomain& dom) {
    check_error(isDynamic() || dom.isDynamic() || (!isEmpty() && !dom.isEmpty()));
    check_error(dom.isNumeric() == isNumeric()); // Confirm they are treated the same way for numbers

    if (dom.isInterval()) {
      bool changed = intersect(dom);
      if (isEmpty())
        return(true);
      return(changed || dom.intersect(*this));
    } else {
      bool changed_a = false;
      bool changed_b = false;
      EnumeratedDomain& l_dom = static_cast<EnumeratedDomain&>(dom);
      std::set<double>::iterator it_a = m_values.begin();
      std::set<double>::iterator it_b = l_dom.m_values.begin();

      while (it_a != m_values.end() && it_b != l_dom.m_values.end()) {
        double val_a = *it_a;
        double val_b = *it_b;

        if (val_a == val_b) {
          ++it_a;
          ++it_b;
        } else
          if (val_a < val_b) {
            std::set<double>::iterator target = m_values.lower_bound(val_b);
            m_values.erase(it_a, target);
            it_a = target;
            changed_a = true;
            check_error(!isMember(val_a));
          } else {
            std::set<double>::iterator target = l_dom.m_values.lower_bound(val_a);
            l_dom.m_values.erase(it_b, target);
            it_b = target;
            changed_b = true;
            check_error(!l_dom.isMember(val_b));
          }
      }

      if (it_a != m_values.end() && !l_dom.isEmpty()) {
        m_values.erase(it_a, m_values.end());
        changed_a = true;
        check_error(it_b == l_dom.m_values.end());
      } else
        if (it_b != l_dom.m_values.end() && !isEmpty()) {
          l_dom.m_values.erase(it_b, l_dom.m_values.end());
          changed_b = true;
          check_error(it_a == m_values.end());
        }

      if (changed_a) {
        if (isEmpty())
          notifyChange(DomainListener::EMPTIED);
        else
          if (isSingleton())
            notifyChange(DomainListener::RESTRICT_TO_SINGLETON);
          else
            notifyChange(DomainListener::VALUE_REMOVED);
      }

      if (changed_b) {
        if (l_dom.isEmpty())
          l_dom.notifyChange(DomainListener::EMPTIED);
        else
          if (isSingleton())
            l_dom.notifyChange(DomainListener::RESTRICT_TO_SINGLETON);
          else
            l_dom.notifyChange(DomainListener::VALUE_REMOVED);
      }

      check_error(!isEmpty() || ! dom.isEmpty());
      check_error(isEmpty() || dom.isEmpty() || (l_dom.m_values == m_values));
      return(changed_a || changed_b);
    }
  }

  bool EnumeratedDomain::isMember(double value) const {
    check_error(!isDynamic());
    std::set<double>::iterator it = m_values.begin();
    for ( ; it != m_values.end(); it++) {
      if (fabs(value - *it) < minDelta())
        return(true);
      if (value < *it) // Since members are sorted, value should be before *it.
        break;
    }
    return(false);
  }

  bool EnumeratedDomain::operator==(const AbstractDomain& dom) const {
    if (!dom.isEnumerated())
      return(dom.isFinite() &&
             getSize() == dom.getSize() &&
             isSubsetOf(dom));
    const EnumeratedDomain& l_dom = static_cast<const EnumeratedDomain&>(dom);
    if (!AbstractDomain::operator==(dom))
      return(false);
    // If any member of either is not a member of the other, they're not equal.
    // Since membership is not simple (due to minDelta()), this has to be done
    // via a scan of both memberships, one member at a time.
    std::set<double>::iterator it = m_values.begin();
    for ( ; it != m_values.end(); it++)
      if (!l_dom.isMember(*it))
        return(false);
    for (it = l_dom.m_values.begin(); it != l_dom.m_values.end(); it++)
      if (!isMember(*it))
        return(false);
    return(true);
  }

  bool EnumeratedDomain::operator!=(const AbstractDomain& dom) const {
    return(!operator==(dom));
  }

  void EnumeratedDomain::relax(const AbstractDomain& dom) {
    if (isDynamic())
      return;
    check_error(dom.isDynamic() || !dom.isEmpty());
    check_error(isSubsetOf(dom));
    check_error(dom.isEnumerated());

    if (!((*this) == dom)) {
      const EnumeratedDomain& l_dom = static_cast<const EnumeratedDomain&>(dom);
      m_values = l_dom.m_values;
      notifyChange(DomainListener::RELAXED);
    }
  }

  double EnumeratedDomain::getSingletonValue() const {
    check_error(isSingleton());
    return(*m_values.begin());
  }

  void EnumeratedDomain::getValues(std::list<double>& results) const {
    check_error(results.empty());
    check_error(isFinite());

    for (std::set<double>::iterator it = m_values.begin(); it != m_values.end(); ++it)
      results.push_back(*it);
  }

  double EnumeratedDomain::getUpperBound() const {
    double lb, ub;
    getBounds(lb, ub);
    return(ub);
  }

  double EnumeratedDomain::getLowerBound() const {
    double lb, ub;
    getBounds(lb, ub);
    return(lb);
  }

  bool EnumeratedDomain::getBounds(double& lb, double& ub) const {
    check_error(!isEmpty());
    lb = *m_values.begin();
    ub = *(--m_values.end());
    check_error(lb <= ub);
    return(!isNumeric() || lb == MINUS_INFINITY || ub == PLUS_INFINITY);
  }

  bool EnumeratedDomain::intersect(const AbstractDomain& dom) {
    check_error(isDynamic() || dom.isDynamic() || (!isEmpty() && !dom.isEmpty()));
    check_error(isNumeric() == dom.isNumeric());

    bool changed = false;
    if (dom.isInterval()) {
      std::set<double>::iterator it = m_values.begin();
      while (it != m_values.end()) {
        double value = *it;
        if (!dom.isMember(value)) {
          changed = true;
          if (value > dom.getUpperBound()) {
            m_values.erase(it, m_values.end());
            break;
          } else
            m_values.erase(it++);
        } else {
          ++it;
        }
      }
    } else {
      const EnumeratedDomain& l_dom = static_cast<const EnumeratedDomain&>(dom);
      std::set<double>::iterator it_a = m_values.begin();
      std::set<double>::const_iterator it_b = l_dom.m_values.begin();

      while (it_a != m_values.end() && it_b != l_dom.m_values.end()) {
        double val_a = *it_a;
        double val_b = *it_b;

        if (val_a == val_b) {
          ++it_a;
          ++it_b;
        } else
          if (val_a < val_b) {
            std::set<double>::iterator target = m_values.find(val_b);
            m_values.erase(it_a, target);
            it_a = target;
            changed = true;
            check_error(!isMember(val_a));
          } else
            it_b = l_dom.m_values.find(val_a);
      }

      if (it_a != m_values.end()) {
        m_values.erase(it_a, m_values.end());
        changed = true;
      }
    }
    if (!changed)
      return(false);

    if (isEmpty())
      notifyChange(DomainListener::EMPTIED);
    else
      if (isSingleton())
        notifyChange(DomainListener::RESTRICT_TO_SINGLETON);
      else
        notifyChange(DomainListener::VALUE_REMOVED);

    return(true);
  }

  bool EnumeratedDomain::difference(const AbstractDomain& dom) {
    check_error(isNumeric() == dom.isNumeric());

    // Trivial implementation, for all members of this domain that
    // are present in dom, remove them.
    bool value_removed = false;

    for (std::set<double>::iterator it = m_values.begin(); it != m_values.end();) {
      double value = *it;
      if (dom.isMember(value)) {
        m_values.erase(it++);
        value_removed = true;
      } else
        ++it;
    }

    if (m_values.empty())
      notifyChange(DomainListener::EMPTIED);
    else
      if (value_removed)
        notifyChange(DomainListener::VALUE_REMOVED);

    return(value_removed);
  }

  AbstractDomain& EnumeratedDomain::operator=(const AbstractDomain& dom) {
    check_error(dom.isEnumerated());
    check_error(m_listener.isNoId());
    const EnumeratedDomain& e_dom = static_cast<const EnumeratedDomain&>(dom);
    m_values.clear();
    m_values = e_dom.m_values;
    return(*this);
  }

  bool EnumeratedDomain::isSubsetOf(const AbstractDomain& dom) const {
    check_error(dom.isDynamic() || !dom.isEmpty());
    check_error(isNumeric() == dom.isNumeric());

    for (std::set<double>::const_iterator it = m_values.begin(); it != m_values.end(); ++it) {
      if (!dom.isMember(*it))
        return(false);
    }
    return(true);
  }

  bool EnumeratedDomain::intersects(const AbstractDomain& dom) const {
    check_error(dom.isDynamic() || !dom.isEmpty());
    check_error(isNumeric() == dom.isNumeric());

    for (std::set<double>::const_iterator it = m_values.begin(); it != m_values.end(); ++it) {
      if (dom.isMember(*it))
        return(true);
    }
    return(false);
  }

  void EnumeratedDomain::operator>>(ostream&os) const {


    // Now commence output
    AbstractDomain::operator>>(os);
    std::string comma = "";
    os << "{";

    // First construct a lexicographic ordering for the set of values.
    std::set<std::string> orderedSet;

    for (std::set<double>::const_iterator it = m_values.begin(); it != m_values.end(); ++it) {
      double valueAsDouble = *it;

      if (isNumeric()){
	os << comma << valueAsDouble;
	comma = ", ";
      }
      else if (LabelStr::isString(valueAsDouble))
	orderedSet.insert(LabelStr(valueAsDouble).toString());
      else {
	EntityId entity(valueAsDouble);
	orderedSet.insert(entity->getName().toString());
      }
    }

    for (std::set<std::string>::const_iterator it = orderedSet.begin(); it != orderedSet.end(); ++it) {
      check_error(!isNumeric());
      os << comma;
      os << *it;
      comma = ",";
    }

    os << "}";
  }

  EnumeratedDomain *EnumeratedDomain::copy() const {
    EnumeratedDomain *ptr = new EnumeratedDomain(*this);
    check_error(ptr != 0);
    return(ptr);
  }

} /* namespace Prototype */
