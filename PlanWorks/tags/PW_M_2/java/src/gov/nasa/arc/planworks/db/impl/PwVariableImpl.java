// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: PwVariableImpl.java,v 1.5 2003-06-11 01:02:12 taylor Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 15May03
//

package gov.nasa.arc.planworks.db.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import gov.nasa.arc.planworks.db.PwDomain;
import gov.nasa.arc.planworks.db.PwParameter;
import gov.nasa.arc.planworks.db.PwVariable;


/**
 * <code>PwVariableImpl</code> - 
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                         NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class PwVariableImpl implements PwVariable {

  private String key;
  private String type;
  private List constraintIdList; // element String
  private String paramId;
  private PwDomainImpl domain; // PwEnumeratedDomainImpl || PwIntervalDomainImpl
  private PwPartialPlanImpl partialPlan;


  /**
   * <code>PwVariableImpl</code> - constructor 
   *
   * @param key - <code>String</code> - 
   * @param type - <code>String</code> - 
   * @param constraintIdList - <code>String</code> - 
   * @param paramId - <code>String</code> - 
   * @param domain - <code>PwDomainImpl</code> - PwEnumeratedDomainImpl || PwIntervalDomainImpl
   * @param partialPlan - <code>PwPartialPlanImpl</code> - 
   */
  public PwVariableImpl( String key, String type, String constraintIds, String paramId,
                         PwDomainImpl domain, PwPartialPlanImpl partialPlan) {
    this.key = key;
    this.type = type;
    this.constraintIdList = new ArrayList();
    StringTokenizer tokenizer = new StringTokenizer( constraintIds);
    while (tokenizer.hasMoreTokens()) {
      this.constraintIdList.add( tokenizer.nextToken());
    }
    this.paramId = paramId;
    this.domain = domain;
    this.partialPlan = partialPlan;
  } // end constructor


  /**
   * <code>getKey</code>
   *
   * @return - <code>String</code> - 
   */
  public String getKey()  {
    return this.key;
  }

  /**
   * <code>getDomain</code>
   *
   * @return - <code>PwDomain</code> - 
   */
  public PwDomain getDomain()  {
    return this.domain;
  }

  /**
   * <code>getParameter</code>
   *
   * @return - <code>PwParameter</code> - 
   */
  public PwParameter getParameter() {
    return partialPlan.getParameter( paramId);
  }

  /**
   * <code>getConstraintList</code>
   *
   * @return - <code>List</code> - of PwConstraint
   */
  public List getConstraintList() {
    List retval = new ArrayList( constraintIdList.size());
    for (int i = 0; i < constraintIdList.size(); i++) {
      retval.add( partialPlan.getConstraint( (String) constraintIdList.get( i)));
    }
    return retval;
  }


} // end class PwVariableImpl
