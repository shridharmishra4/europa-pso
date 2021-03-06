// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: PwRuleImpl.java,v 1.4 2004-04-30 21:49:39 miatauro Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 26nov03
//

package gov.nasa.arc.planworks.db.impl;

import java.util.List;

import gov.nasa.arc.planworks.db.PwRule;


/**
 * <code>PwRuleImpl</code> - 
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                         NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class PwRuleImpl implements PwRule {

  private Long sequenceId;
  private Integer id;
  private String text;
  
  /**
   * <code>PwRuleImpl</code> - constructor 
   *
   * @param id - <code>Integer</code> - 
   * @param text - <code>String</code> - 
   */
  public PwRuleImpl( Long sequenceId, Integer id, String text) {
    this.sequenceId = sequenceId;
    this.id = id;
    this.text = text;
  } // end constructor

  /**
   * <code>getId</code>
   *
   * @return - <code>Integer</code> - 
   */
  public Integer getId() {
    return id;
  }

  /**
   * <code>getText</code>
   *
   * @return - <code>String</code> - 
   */
  public String getText() {
    return text;
  }

} // end PwRuleImpl
