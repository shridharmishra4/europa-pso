// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 
// $Id: NodeGenerics.java,v 1.1 2003-09-18 20:48:43 taylor Exp $
//
// PlanWorks
//
// Will Taylor -- started 17sep03
//

package gov.nasa.arc.planworks.viz.nodes;

import java.awt.Point;
import javax.swing.JPopupMenu;

import gov.nasa.arc.planworks.PlanWorks;
import gov.nasa.arc.planworks.db.PwDomain;
import gov.nasa.arc.planworks.db.PwSlot;
import gov.nasa.arc.planworks.db.PwToken;
import gov.nasa.arc.planworks.db.PwVariable;
import gov.nasa.arc.planworks.util.Utilities;
import gov.nasa.arc.planworks.viz.views.VizView;


/**
 * <code>NodeGenerics</code> - generic static methods for node handling
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *       NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class NodeGenerics {

  private NodeGenerics() {
  }

  /**
   * <code>showPopupMenu</code> - show pop up menu in vizView at viewCoords location
   *
   * @param popupMenu - <code>JPopupMenu</code> - 
   * @param vizView - <code>VizView</code> - 
   * @param viewCoords - <code>Point</code> - 
   */
  public static void showPopupMenu( JPopupMenu popupMenu, VizView vizView, Point viewCoords) {
    boolean isLocationAbsolute = false;
    Point popupPoint = Utilities.computeNestedLocation( viewCoords, vizView,
                                                        isLocationAbsolute);
    popupMenu.show( PlanWorks.planWorks, (int) popupPoint.getX(),
                    (int) popupPoint.getY());
  } // end showPopupMenu

  /**
   * <code>getStartEndIntervals</code>
   *
   * use startVariable for every token + the endVariable for the last one
   * if a slot is empty, and has no tokens, use the endVariable from
   * the previous slot node
   *
   * @param view - <code>VizView</code> - 
   * @param slot - <code>PwSlot</code> - 
   * @param previousSlot - <code>PwSlot</code> - 
   * @param isLastSlot - <code>boolean</code> - 
   * @param alwaysReturnEnd - <code>boolean</code> - 
   * @return - <code>PwDomain[]</code> - 
   */
  public static PwDomain[] getStartEndIntervals( VizView view, PwSlot slot,
                                                 PwSlot previousSlot,
                                                 boolean isLastSlot,
                                                 boolean alwaysReturnEnd) {
    PwDomain[] intervalArray = new PwDomain[2];
    PwDomain startIntervalDomain = null;
    PwDomain endIntervalDomain = null;
    PwVariable intervalVariable = null, lastIntervalVariable = null;
    PwToken baseToken = slot.getBaseToken();
    PwToken previousToken = null;
    if (previousSlot != null) {
      previousToken = previousSlot.getBaseToken();
    }
    PwDomain intervalDomain = null, lastIntervalDomain = null;
    if (baseToken == null) { // empty slot
      if ((previousToken == null) || // first slot
          (isLastSlot == true)) { // last slot
        intervalArray[0] = null;
        intervalArray[1] = null;
        return intervalArray;
     } else {
        // empty slot between filled slots
        intervalVariable = previousToken.getEndVariable();
      }
    } else if (isLastSlot || alwaysReturnEnd) {
      intervalVariable = baseToken.getStartVariable();
      lastIntervalVariable = baseToken.getEndVariable();
    } else {
      intervalVariable = baseToken.getStartVariable();      
    }

    if (intervalVariable == null) {
      startIntervalDomain = intervalDomain;
    } else {
      startIntervalDomain = intervalVariable.getDomain();
    }

    if ((lastIntervalVariable != null) || (lastIntervalDomain != null)) {
      if (lastIntervalVariable == null) {
        endIntervalDomain = lastIntervalDomain;
      } else {
        endIntervalDomain = lastIntervalVariable.getDomain();
      }
    }
    if (alwaysReturnEnd && (endIntervalDomain == null)) {
      endIntervalDomain = startIntervalDomain;
    }
//     System.err.println( "getStartEndIntervals: " + slot.getBaseToken());
//     System.err.println( "  startIntervalDomain " + startIntervalDomain.toString());
//     System.err.println( "  endIntervalDomain " + endIntervalDomain.toString());
    intervalArray[0] = startIntervalDomain;
    intervalArray[1] = endIntervalDomain;
    return intervalArray;
  } // end getStartEndIntervals

  /**
   * <code>getShortestDuration</code>
   *
   * @param slot - <code>PwSlot</code> - 
   * @param startTimeIntervalDomain - <code>PwDomain</code> - 
   * @param endTimeIntervalDomain - <code>PwDomain</code> - 
   * @return - <code>String</code> - 
   */
  public static String getShortestDuration( PwSlot slot,
                                            PwDomain startTimeIntervalDomain,
                                            PwDomain endTimeIntervalDomain) {
    PwVariable durationVariable = null;
    if ((slot == null) ||  // free token
        (slot.getBaseToken() == null)) { // empty slot
      int startUpper = startTimeIntervalDomain.getUpperBoundInt();
      int endUpper = endTimeIntervalDomain.getUpperBoundInt();
      int startLower = startTimeIntervalDomain.getLowerBoundInt();
      int endLower = endTimeIntervalDomain.getLowerBoundInt();
      int valueStart = Math.max( startLower, startUpper);
      if (valueStart == PwDomain.PLUS_INFINITY_INT) {
        valueStart = Math.min( startLower, startUpper);
      }
      if (valueStart == PwDomain.MINUS_INFINITY_INT) {
        valueStart = 0;
      }
      int valueEnd = Math.min( endLower, endUpper);
      if (valueEnd == PwDomain.MINUS_INFINITY_INT) {
        valueEnd = Math.max( endLower, endUpper);
      }
      if (valueEnd == PwDomain.PLUS_INFINITY_INT) {
        valueEnd = valueStart;
      }
      return String.valueOf( Math.abs( valueEnd - valueStart));
    } else {
      durationVariable = slot.getBaseToken().getDurationVariable();
      if (durationVariable != null) {
        return durationVariable.getDomain().getLowerBound();
      } else {
        return "0";
      }
    }
  } // end getShortestDuration

  /**
   * <code>getLongestDuration</code>
   *
   * @param slot - <code>PwSlot</code> - 
   * @param startTimeIntervalDomain - <code>PwDomain</code> - 
   * @param endTimeIntervalDomain - <code>PwDomain</code> - 
   * @return - <code>String</code> - 
   */
  public static String getLongestDuration( PwSlot slot,
                                           PwDomain startTimeIntervalDomain,
                                           PwDomain endTimeIntervalDomain) {
    PwVariable durationVariable = null;
    if ((slot == null) ||  // free token
        (slot.getBaseToken() == null)) { // empty slot
      String upperBound = endTimeIntervalDomain.getUpperBound();
      String lowerBound = startTimeIntervalDomain.getLowerBound();
      if (upperBound.equals( PwDomain.PLUS_INFINITY)) {
        return PwDomain.PLUS_INFINITY;
      } else if (lowerBound.equals( PwDomain.MINUS_INFINITY)) {
        return PwDomain.PLUS_INFINITY;
      } else {
        return String.valueOf( endTimeIntervalDomain.getUpperBoundInt() -
                               startTimeIntervalDomain.getLowerBoundInt());      
      }
    } else {
      durationVariable = slot.getBaseToken().getDurationVariable();
      if (durationVariable != null) {
        return durationVariable.getDomain().getUpperBound();
      } else {
        return "0";
      }
    }
  } // end getLongestDuration



} // end class NodeGenerics

