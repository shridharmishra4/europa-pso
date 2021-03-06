// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: PwVariableImpl.java,v 1.17 2004-01-14 21:22:36 miatauro Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 15May03
//

package gov.nasa.arc.planworks.db.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import gov.nasa.arc.planworks.db.DbConstants;
import gov.nasa.arc.planworks.db.PwDomain;
import gov.nasa.arc.planworks.db.PwParameter;
import gov.nasa.arc.planworks.db.PwVariable;
import gov.nasa.arc.planworks.util.UniqueSet;

/**
 * <code>PwVariableImpl</code> - 
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                         NASA Ames Research Center - Code IC
 * @version 0.0
 * NOTE: For the moment, the assumption is made (due to a EUROPA design decision) that a
 * variable is on at most one token (globals obviously have no tokens).  Should you be using this
 * in a non-EUROPA context, then certain changes will have to be made to account for that, but the
 * general form of this data structure doesn't disallow the possibility.
 */
public class PwVariableImpl implements PwVariable {

  private Integer id;
  private String type;
  private UniqueSet constraintIdList; // element String
  private UniqueSet parameterNameList;
  private UniqueSet tokenIdList;
  private PwDomainImpl domain; // PwEnumeratedDomainImpl || PwIntervalDomainImpl
  private PwPartialPlanImpl partialPlan;


  /**
   * <code>PwVariableImpl</code> - constructor 
   *
   * @param id - <code>Integer</code> - 
   * @param type - <code>String</code> - 
   * @param constraintIdList - <code>List</code> - 
   * @param paramId - <code>Integer</code> - 
   * @param domain - <code>PwDomainImpl</code> - PwEnumeratedDomainImpl || PwIntervalDomainImpl
   * @param partialPlan - <code>PwPartialPlanImpl</code> - 
   */
  public PwVariableImpl( Integer id, String type, List constraintIds, List parameterNames,
                         List tokenIds, PwDomainImpl domain, PwPartialPlanImpl partialPlan) {
    this.id = id;
    this.type = type;
    this.constraintIdList = new UniqueSet(constraintIds);
    this.parameterNameList = new UniqueSet(parameterNames);
    this.tokenIdList = new UniqueSet(tokenIds);
    this.domain = domain;
    this.partialPlan = partialPlan;
  } // end constructor

  public PwVariableImpl( Integer id, String type, PwDomainImpl domain,
                         PwPartialPlanImpl partialPlan) {
    this.id = id;
    this.type = type;
    this.constraintIdList = new UniqueSet();
    this.parameterNameList = new UniqueSet();
    this.tokenIdList = new UniqueSet();
    this.domain = domain;
    this.partialPlan = partialPlan;
  } // end constructor

  /**
   * <code>getId</code>
   *
   * @return - <code>Integer</code> - 
   */
  public Integer getId()  {
    return this.id;
  }

  /**
   * <code>getType</code>
   *
   * @return - <code>String</code> - 
   */
  public String getType() {
    return this.type;
  }

  /**
   * <code>getDomain</code>
   *
   * @return - <code>PwDomain</code> - 
   */
  public PwDomain getDomain()  {
    return this.domain;
  }


  public List getParameterNameList() {
    return new ArrayList(parameterNameList);
  }
  /**
   * <code>getParameterList</code>
   *
   * @return - <code>List of PwParameter</code> - 
   */
//   public List getParameterList() {
//     List retval = new ArrayList(parameterIdList.size());
//     ListIterator tokenIdIterator = tokenIdList.listIterator();
//     ListIterator paramIdIterator = parameterIdList.listIterator();
//     // System.err.println( "PwVariableImpl.getParameterList: type " + type + " tokenIdList " +
//     //                     tokenIdList + " parameterIdList " + parameterIdList);
//     if (type.equals(DbConstants.START_VAR) ||
//         type.equals(DbConstants.END_VAR) ||
//         type.equals(DbConstants.DURATION_VAR) ||
//         type.equals(DbConstants.OBJECT_VAR) ||
//         type.equals(DbConstants.REJECT_VAR)) {
//       return parameterIdList;
//     }
//     while(tokenIdIterator.hasNext()) {
//       Integer tokenId = (Integer) tokenIdIterator.next();
//       Integer paramId = (Integer) paramIdIterator.next();
//       retval.add(partialPlan.getToken(tokenId).getPredicate().getParameter(paramId));
//     }
//     return retval;
//   }

  /**
   * <code>getConstraintList</code>
   *
   * @return - <code>List</code> - of PwConstraint
   */
  public List getConstraintList() {
    List retval = new ArrayList( constraintIdList.size());
    for (int i = 0; i < constraintIdList.size(); i++) {
      retval.add( partialPlan.getConstraint( (Integer) constraintIdList.get( i)));
    }
    return retval;
  }

  /**
   * <code>getTokenList</code>
   *
   * @return - <code>List</code> - of PwToken
   */
  public List getTokenList() {
    List retval = new ArrayList(tokenIdList.size());
    for(int i = 0; i < tokenIdList.size(); i++) {
      retval.add(partialPlan.getToken((Integer) tokenIdList.get(i)));
    }
    return retval;
  }

  public void removeConstraint(Integer constraintId) {
    constraintIdList.remove(constraintId);
  }

  public void addConstraint(Integer constraintId) {
    constraintIdList.add(constraintId);
  }

  public void addParameter(String paramName) {
    parameterNameList.add(paramName);
  }

  public void addToken(Integer tokenId) {
    tokenIdList.add(tokenId);
  }

} // end class PwVariableImpl
