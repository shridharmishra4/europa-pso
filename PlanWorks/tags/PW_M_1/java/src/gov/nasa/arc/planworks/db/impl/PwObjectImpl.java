// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: PwObjectImpl.java,v 1.6 2003-05-20 18:25:34 taylor Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 14May03
//

package gov.nasa.arc.planworks.db.impl;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.planworks.db.PwObject;


/**
 * <code>PwObjectImpl</code> - 
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                         NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class PwObjectImpl implements PwObject {

  private String key;
  private String name;
  private List timelineIdList; 
  private PwPartialPlanImpl partialPlan;
  private String collectionName;

  /**
   * <code>PwObjectImpl</code> - constructor 
   *
   * @param key - <code>String</code> - 
   * @param name - <code>String</code> - 
   * @param partialPlan - <code>PwPartialPlanImpl</code> - 
   * @param collectionName - <code>String</code> - 
   */
  public PwObjectImpl( String key, String name, PwPartialPlanImpl partialPlan,
                       String collectionName) {
    this.key = key;
    this.name = name;
    this.partialPlan = partialPlan;
    this.collectionName = collectionName;
    timelineIdList = new ArrayList();
  } // end constructor


  /**
   * <code>addTimeline</code>
   *
   * @param name - <code>String</code> - 
   * @param key - <code>String</code> - 
   * @return timeline - <code>PwTimelineImpl</code> - 
   */
  public PwTimelineImpl addTimeline( String name, String key) {
    timelineIdList.add( key);
    PwTimelineImpl timeline = new PwTimelineImpl( name, key, partialPlan,
                                                  collectionName);
    partialPlan.getTimelineMap().put( key, timeline);
    return timeline;
  } // end addTimeline

  /**
   * <code>getKey</code>
   *
   * @return key - <code>String</code> -
   */
  public String getKey() {
    return this.key;
  }

  /**
   * <code>getName</code>
   *
   * @return name - <code>String</code> -
   */
  public String getName() {
    return this.name;
  }


  /**
   * <code>getTimelineList</code> -
   *
   * @return timelineList - <code>List</code> - of PwTimelineImpl
   */
  public List getTimelineList() {
    List retval = new ArrayList(timelineIdList.size());
    for (int i = 0; i < timelineIdList.size(); i++) {
      retval.add ( partialPlan.getTimeline( (String) timelineIdList.get(i),
                                            collectionName));
    }
    return retval;
  }
	 

} // end class PwObjectImpl
