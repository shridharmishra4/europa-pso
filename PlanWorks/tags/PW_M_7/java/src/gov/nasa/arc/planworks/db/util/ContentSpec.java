//
// * See the file "PlanWorks/disclaimers-and-notices.txt" for
// * information on usage and redistribution of this file,
// * and for a DISCLAIMER OF ALL WARRANTIES.
//

// $Id: ContentSpec.java,v 1.9 2003-09-18 23:35:25 miatauro Exp $
//
package gov.nasa.arc.planworks.db.util;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;
import gov.nasa.arc.planworks.db.PwPartialPlan;
import gov.nasa.arc.planworks.db.PwToken;
import gov.nasa.arc.planworks.db.util.MySQLDB;
import gov.nasa.arc.planworks.util.UniqueSet;
import gov.nasa.arc.planworks.viz.viewMgr.RedrawNotifier;

/*
 * <code>ContentSpec</code> -
 * @author <a href="mailto:miatauro@email.arc.nasa.gov">Michael Iatauro</a>
 * The content specification class.  Interfaces directely with the database to determine which ids
 * are/should be in the current specification, and provides a method for exposing that information
 * to other classes (VizView through ViewSet).
 */

public class ContentSpec {
  private UniqueSet validTokenIds;
  private Long partialPlanId;
  private PwPartialPlan partialPlan;
  private RedrawNotifier redrawNotifier;

  private static final String AND = "and";
  private static final String AND_PPREDICATEID = "&& (PredicateId";
  private static final String AND_PREDICATEID = "&& PredicateId";
  private static final String AND_PTIMELINEID = "&& (TimelineId";
  private static final String AND_TIMELINEID = "&& TimelineId";
  private static final String EQ = "=";
  private static final String NEG = "!";
  private static final String NOT = "not";
  private static final String OBJECTNAME = "Object.ObjectName";
  private static final String OR = "or";
  private static final String OR_TIMELINEID = "|| TimelineId";
  private static final String OR_PREDICATEID = "|| PredicateId";
  private static final String PREDICATEID = "PredicateId";
  private static final String PREDICATENAME = "PredicateName";
  private static final String PREDICATENAME_QUERY =
    "SELECT PredicateName, PredicateId FROM Predicate WHERE PartialPlanId=";
  private static final String TIMELINEID = "Timeline.TimelineId";
  private static final String TIMELINENAME = "Timeline.TimelineName";
  private static final String TIMELINENAME_QUERY =
    "SELECT Object.ObjectName, Timeline.TimelineName, Timeline.TimelineId FROM Object RIGHT JOIN Timeline ON Timeline.ObjectId=Object.ObjectId && Timeline.PartialPlanId=Object.PartialPlanId WHERE Object.PartialPlanId=";
  private static final String TOKENID = "TokenId";
  private static final String TOKENID_QUERY = "SELECT TokenId FROM Token WHERE PartialPlanId=";
  private static final int FREE_ONLY = -1;
  private static final int SLOTTED_ONLY = 0;
  private static final int ALL = 1;

  /**
   * Creates the ContentSpec object, then makes a query for all valid tokens
   *
   * @param <code>partialPlan</code> the partial plan object constrained by this object
   * @param <code>redrawNotifier</code> an interface to inform views that they need to re-draw
   */

  public ContentSpec(PwPartialPlan partialPlan, RedrawNotifier redrawNotifier) {
    this.partialPlanId = partialPlan.getId();
    this.partialPlan = partialPlan;
    this.redrawNotifier = redrawNotifier;
    this.validTokenIds = new UniqueSet();
    queryValidTokens();
  }
  /**
   * Sets all tokens valid
   */
  public void resetSpec() {
    validTokenIds.clear();
    queryValidTokens();
    redrawNotifier.notifyRedraw();
  }
  
  /**
   * Get all token ids
   */

  private void queryValidTokens() {
    try {
      ResultSet validTokens = MySQLDB.queryDatabase(TOKENID_QUERY.concat(partialPlanId.toString()));
      while(validTokens.next()) {
        validTokenIds.add(new Integer(validTokens.getInt(TOKENID)));
      }
    }
    catch(SQLException sqle) {
    }
  }

  /**
   * Get the list of ids for tokens conforming to the specification
   *
   * @return List - valid token ids
   */
  public List getValidTokenIds(){return validTokenIds;}

  public void printSpec() {
    System.err.println("Allowable tokens: ");
    ListIterator tokenIdIterator = validTokenIds.listIterator();
    while(tokenIdIterator.hasNext()) {
      System.err.println(((Integer)tokenIdIterator.next()).toString());
    }
  }
  /**
   * Given the parametes specified by the user in the ContentSpecWindow, constructs the entire
   * specification of valid ids through a database query, then informs the windows
   * goverend by this spec that they need to redraw themselves to the new specification.
   * @param timeline the result of getValues() in TimelineGroupBox.
   * @param predicate the result of getValues() in PredicateGroupBox.
   * @param timeInterval the result of getValues() in TimeIntervalGroupBox.
   * @param mergeTokens the result of getValue() in MergeBox.
   * @param tokenTypes the result of getValue() in TokenTypeBox.
   */
  public void applySpec(List timeline, List predicate, List timeInterval, boolean mergeTokens,
                        int tokenTypes) 
    throws NumberFormatException {
    try {
      StringBuffer tokenQuery = new StringBuffer(TOKENID_QUERY);
      tokenQuery.append(partialPlanId.toString()).append(" ");
      if(timeline != null) {
        tokenQuery.append(AND_PTIMELINEID);
        if(((String)timeline.get(0)).indexOf(NOT) != -1) {
          tokenQuery.append(NEG);
        }
        tokenQuery.append(EQ);
        tokenQuery.append(((Integer)timeline.get(1)).toString()).append(" ");
        for(int i = 2; i < timeline.size(); i++) {
          String connective = (String) timeline.get(i);
          if(connective.indexOf(AND) != -1) {
            tokenQuery.append(AND_TIMELINEID);
          }
          else {
            tokenQuery.append(OR_TIMELINEID);
          }
          if(connective.indexOf(NOT) != -1) {
            tokenQuery.append(NEG);
          }
          i++;
          tokenQuery.append(EQ).append(((Integer)timeline.get(i)).toString()).append(" ");
        }
        tokenQuery.append(") ");
      }
      if(predicate != null) {
        tokenQuery.append(AND_PPREDICATEID);
        if(((String)predicate.get(0)).indexOf(NOT) != -1) {
          tokenQuery.append(NEG);
        }
        tokenQuery.append(EQ);
        tokenQuery.append(((Integer)predicate.get(1)).toString()).append(" ");
        for(int i = 2; i < predicate.size(); i++) {
          String connective = (String) predicate.get(i);
          if(connective.indexOf(AND) != -1) {
            tokenQuery.append(AND_PREDICATEID);
          }
          else {
            tokenQuery.append(OR_PREDICATEID);
          }
          if(connective.indexOf(NOT) != -1) {
            tokenQuery.append(NEG);
          }
          i++;
          tokenQuery.append(EQ).append(((Integer)predicate.get(i)).toString()).append(" ");
        }
        tokenQuery.append(") ");
      }
      tokenQuery.append(";");
      ResultSet tokenIds = MySQLDB.queryDatabase(tokenQuery.toString());
      validTokenIds.clear();
      while(tokenIds.next()) {
        validTokenIds.add(new Integer(tokenIds.getInt(TOKENID)));
      }
      if(tokenTypes == FREE_ONLY) {
        ListIterator freeTokenIdIterator = validTokenIds.listIterator();
        while(freeTokenIdIterator.hasNext()) {
          Integer id = (Integer) freeTokenIdIterator.next();
          if(!partialPlan.getToken(id).isFreeToken()) {
            freeTokenIdIterator.remove();
          }
        }
      }
      else if(tokenTypes == SLOTTED_ONLY) {
        ListIterator slottedTokenIdIterator = validTokenIds.listIterator();
        while(slottedTokenIdIterator.hasNext()) {
          Integer id = (Integer) slottedTokenIdIterator.next();
          if(partialPlan.getToken(id).isFreeToken()) {
            slottedTokenIdIterator.remove();
          }
        }
      }
      else if(tokenTypes != ALL) {
        System.err.println("Invalid tokenType value " + tokenTypes + ".  Ignoring.");
      }
      if(mergeTokens) {
        UniqueSet tempValidIds = new UniqueSet();
        ListIterator mergeTokenIdIterator = validTokenIds.listIterator();
        while(mergeTokenIdIterator.hasNext()) {
          Integer id = (Integer) mergeTokenIdIterator.next();
          if(partialPlan.getSlot(partialPlan.getToken(id).getSlotId()) != null) {
            tempValidIds.add(partialPlan.getSlot(partialPlan.getToken(id).getSlotId()).getBaseToken().getId());
          }
          else if(partialPlan.getToken(id).isFreeToken()) {
            tempValidIds.add(id);
          }
        }
        validTokenIds.clear();
        validTokenIds.addAll(tempValidIds);
      }
      if(timeInterval != null) {
        ListIterator tokenIdIterator = validTokenIds.listIterator();
        while(tokenIdIterator.hasNext()) {
          Integer tokenId = (Integer) tokenIdIterator.next();
          Integer earliestStart = partialPlan.getToken(tokenId).getEarliestStart();
          Integer latestEnd = partialPlan.getToken(tokenId).getLatestEnd();
          boolean leftIsTrue = false;

          for(int i = 0; i < timeInterval.size(); i += 3) {
            String connective = (String) timeInterval.get(i);
            Integer start = (Integer) timeInterval.get(i+1);
            Integer end = (Integer) timeInterval.get(i+2);
            if(connective.indexOf(AND) > -1) {
              if(!leftIsTrue) {
                break;
              }
              leftIsTrue = evaluateTimeInterval(connective, start, end, earliestStart, latestEnd);
              if(!leftIsTrue) {
                break;
              }
            }
            else if(connective.indexOf(OR) > -1) {
              leftIsTrue = 
                (evaluateTimeInterval(connective, start, end, earliestStart, latestEnd) || 
                 leftIsTrue);
            }
          }
          if(!leftIsTrue) {
            tokenIdIterator.remove();
          }
        }
      }
    }
    catch(SQLException sqle) {
    }
    redrawNotifier.notifyRedraw();
  }

  /**
   * Given a time interval and a token's earliest start and latest end times, determines whether or not
   * the token exists in the interval.
   *
   * @param connective The logic function to apply (negation)
   * @param start The beginning of the interval
   * @param end The end of the interval
   * @param earliestStart The earliest start time of the token
   * @param latestEnd The latest end time of the token
   */

  private boolean evaluateTimeInterval(String connective, Integer start, Integer end, 
                                       Integer earliestStart, Integer latestEnd) {
    boolean negation = (connective.indexOf(NOT) > -1);
    if(earliestStart.compareTo(start) >= 0 && earliestStart.compareTo(end) <= 0) {
      return true ^ negation;
    }
    if(latestEnd.compareTo(start) >= 0 && latestEnd.compareTo(end) <= 0) {
      return true ^ negation;
    }
    if(earliestStart.compareTo(start) <= 0 && latestEnd.compareTo(end) >= 0) {
      return true ^ negation;
    }
    return false ^ negation;
  }

  /**
   * Maps predicate names to predicate Ids for use in the ContentSpecWindow
   *
   * @return Map name => Id
   */

  public Map getPredicateNames() {
    HashMap predicates = new HashMap();
    
    System.err.println("Getting predicate names...");
    try {
      ResultSet predicateNames = 
        MySQLDB.queryDatabase(PREDICATENAME_QUERY.concat(partialPlanId.toString()));
      while(predicateNames.next()) {
        predicates.put(predicateNames.getString(PREDICATENAME), 
                       new Integer(predicateNames.getInt(PREDICATEID)));
      }
    }
    catch(SQLException sqle) {}
    return predicates;
  }

  /**
   * Maps timeline names to timeline Ids for use in the ContentSpecWindow
   *
   * @return Map name => Id
   */

  public Map getTimelineNames() {
    HashMap timelines = new HashMap();
    System.err.println("Getting timeline names...");
    try {
      ResultSet timelineNames =
        MySQLDB.queryDatabase(TIMELINENAME_QUERY.concat(partialPlanId.toString()));
      String objName = null;
      while(timelineNames.next()) {
        objName = (timelineNames.getString(OBJECTNAME) == null ? objName : 
                   timelineNames.getString(OBJECTNAME));
        timelines.put("".concat(objName).concat(":").concat(timelineNames.getString(TIMELINENAME)), new Integer(timelineNames.getInt(TIMELINEID)));
      }
    }
    catch(SQLException sqle) {}
    return timelines;
  }
}
