// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: PwPredicateImpl.java,v 1.7 2003-06-26 18:19:50 miatauro Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 15May03
//

package gov.nasa.arc.planworks.db.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import gov.nasa.arc.planworks.db.PwPredicate;


/**
 * <code>PwPredicateImpl</code> - 
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                         NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class PwPredicateImpl implements PwPredicate {

  private String name;
  private Integer key;
  private List parameterIdList; // element String
  private PwPartialPlanImpl partialPlan;

  /**
   * <code>PwPredicateImpl</code> - constructor 
   *
   * @param name - <code>String</code> - 
   * @param key - <code>String</code> - 
   * @param partialPlan - <code>PwPartialPlanImpl</code> - 
   */
  public PwPredicateImpl( Integer key, String name, PwPartialPlanImpl partialPlan) {
    this.name = name;
    this.key = key;
    this.parameterIdList = new ArrayList();
    this.partialPlan = partialPlan;
  } // end constructor

  /**
   * <code>getName</code>
   *
   * @return name - <code>String</code> -
   */
  public String getName() {
    return name;
  }

  /**
   * <code>getKey</code>
   *
   * @return name - <code>String</code> -
   */
  public Integer getKey() {
    return key;
  }
	
  /**
   * <code>getParameterList</code>
   *
   * @return - <code>List</code> - of PwParameter
   */
  public List getParameterList() {
    List retval = new ArrayList( parameterIdList.size());
    for (int i = 0; i < parameterIdList.size(); i++) {
      retval.add( partialPlan.getParameter( (Integer)parameterIdList.get( i)));
    }
    return retval;
  }

  /**
   * <code>addParameter</code>
   *
   * @param name - <code>String</code> - 
   * @param key - <code>String</code> - 
   * @return - <code>PwParameterImpl</code> - 
   */
  public PwParameterImpl addParameter( Integer key, String name) {
    PwParameterImpl parameter = new PwParameterImpl(key, name);
    parameterIdList.add( key);
    return parameter;
  } // end addParameter


} // end class PwPredicateImpl
