//
// * See the file "PlanWorks/disclaimers-and-notices.txt" for
// * information on usage and redistribution of this file,
// * and for a DISCLAIMER OF ALL WARRANTIES.
//

// $Id: ViewSet.java,v 1.20 2003-07-24 20:57:11 taylor Exp $
//
package gov.nasa.arc.planworks.viz.viewMgr;

import java.awt.Container;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;

import gov.nasa.arc.planworks.mdi.MDIFrame;
import gov.nasa.arc.planworks.mdi.MDIInternalFrame;
import gov.nasa.arc.planworks.mdi.MDIDesktopFrame;
import gov.nasa.arc.planworks.mdi.MDIWindowBar;
import gov.nasa.arc.planworks.db.PwPartialPlan;
import gov.nasa.arc.planworks.viz.views.VizView;
import gov.nasa.arc.planworks.viz.views.temporalExtent.TemporalExtentView;
import gov.nasa.arc.planworks.viz.views.timeline.TimelineView;
import gov.nasa.arc.planworks.viz.views.tokenNetwork.TokenNetworkView;
import gov.nasa.arc.planworks.viz.viewMgr.contentSpecWindow.ContentSpecWindow;

/**
 * <code>ViewSet</code> -
 * @author <a href="mailto:miatauro@email.arc.nasa.gov">Michael Iatauro</a>
 * A class to manage the views associated with a partial plan.
 */

//maybe the hashmap should be changed.  is that much flexibility really necessary?
//the MDIWindowBar is just for the notifyDeleted method
public class ViewSet implements RedrawNotifier, ContentSpecChecker, MDIWindowBar {
  private MDIDesktopFrame desktopFrame;
  private HashMap views;
  private ContentSpec contentSpec;
  private PwPartialPlan partialPlan;
  private ViewSetRemover remover;
  private String planName;
  private MDIInternalFrame contentSpecWindow;

  /**
   * Creates the ViewSet object, creates a new ContentSpec, and creates storage for the new views.
   * @param desktopFrame the desktop into which the views will be added.
   * @param partialPlan the partialPlan to be viewed.
   * @param planName the name of the partial plan.  Used to visually distinguish views across
   *                 partial plans.
   * @param remover the interface which is responsible for removing entire ViewSets.
   */
  public ViewSet(MDIDesktopFrame desktopFrame, PwPartialPlan partialPlan, String planName, 
                 ViewSetRemover remover) throws SQLException{
    this.views = new HashMap();
    this.partialPlan = partialPlan;
    this.planName = planName;
    this.remover = remover;
    this.desktopFrame = desktopFrame;
    this.contentSpec = new ContentSpec(partialPlan, this);
    //change the arguments to something more sensible
    System.err.println(planName);
    this.contentSpecWindow = 
      desktopFrame.createFrame("Content specification for ".concat(planName), this, false, false,
                               false, true);
    Container contentPane = this.contentSpecWindow.getContentPane();
    contentPane.add(new ContentSpecWindow(this.contentSpecWindow, contentSpec));
    this.contentSpecWindow.pack();
    this.contentSpecWindow.setVisible(true);
  }

  /**
   * Opens a new TimelineView, stuffs it in an MDIInternalFrame, adds it to the hash of frames,
   * then returns it.  If a TimelineView already exists for this partial plan, returns that.
   * @return MDIInternalFrame the frame containing the vew.
   */
  public MDIInternalFrame openTimelineView( long startTimeMSecs) {
    if(viewExists("timelineView")) {
      return (MDIInternalFrame) views.get("timelineView");
    }
    MDIInternalFrame timelineViewFrame = 
      desktopFrame.createFrame("Timeline view of ".concat(planName), this, true, true, true, true);
    views.put("timelineView", timelineViewFrame);
    Container contentPane = timelineViewFrame.getContentPane();
    contentPane.add(new TimelineView(partialPlan, startTimeMSecs, this));
    return timelineViewFrame;
  }

  /**
   * Opens a new TokenNetworkView, stuffs it in an MDIInternalFrame, adds it to the hash
   * of frames, then returns it.  If a TokenNetworkView already exists for this partial
   * plan, returns that.
   * @return MDIInternalFrame the frame containing the vew.
   */
  public MDIInternalFrame openTokenNetworkView( long startTimeMSecs) {
    if(viewExists("tokenNetworkView")) {
      return (MDIInternalFrame) views.get("tokenNetworkView");
    }
    MDIInternalFrame tokenNetworkViewFrame = 
      desktopFrame.createFrame("Token Network view of ".concat(planName), this, true, true,
                               true, true);
    views.put("tokenNetworkView", tokenNetworkViewFrame);
    Container contentPane = tokenNetworkViewFrame.getContentPane();
    contentPane.add(new TokenNetworkView(partialPlan, startTimeMSecs, this));
    return tokenNetworkViewFrame;
  }

  /**
   * Opens a new TemporalExtentView, stuffs it in an MDIInternalFrame, adds it to the hash
   * of frames, then returns it.  If a TemporalExtentView already exists for this partial
   * plan, returns that.
   * @return MDIInternalFrame the frame containing the vew.
   */
  public MDIInternalFrame openTemporalExtentView( long startTimeMSecs) {
    if(viewExists("temporalExtentView")) {
      return (MDIInternalFrame) views.get("temporalExtentView");
    }
    MDIInternalFrame temporalExtentViewFrame = 
      desktopFrame.createFrame("Temporal Extent view of ".concat(planName), this, true, true,
                               true, true);
    views.put("temporalExtentView", temporalExtentViewFrame);
    Container contentPane = temporalExtentViewFrame.getContentPane();
    contentPane.add(new TemporalExtentView(partialPlan, startTimeMSecs, this));
    return temporalExtentViewFrame;
  }

  //  public void addViewFrame(MDIInternalFrame viewFrame) {
  // if(!views.contains(viewFrame)) {
  //   views.add(viewFrame);
  // }
  //}

  /**
   * Removes a view from the ViewSet.  If there are no more views extant, removes the ViewSet.
   * @param MDIInternalFrame the frame containing the view.
   */
  public void removeViewFrame(MDIInternalFrame viewFrame) {
    if(views.containsValue(viewFrame)) {
      Container contentPane = viewFrame.getContentPane();
      for(int i = 0; i < contentPane.getComponentCount(); i++) {
        if(contentPane.getComponent(i) instanceof TimelineView) {
          views.remove("timelineView");
        } else if(contentPane.getComponent(i) instanceof TokenNetworkView) {
          views.remove("tokenNetworkView");
        } else if(contentPane.getComponent(i) instanceof TemporalExtentView) {
          views.remove("temporalExtentView");
        }
      }
    }
    if(views.isEmpty()) {
      try {contentSpecWindow.setClosed(true);}
      catch(PropertyVetoException pve){}
      remover.removeViewSet(partialPlan);
    }
  }
  /**
   * Notifies all open views of a change in the ContentSpec, and therefore a need to redraw.
   */
  public void notifyRedraw() {
    Iterator viewFrameIterator = views.values().iterator();
    while(viewFrameIterator.hasNext()) {
      MDIInternalFrame viewFrame = (MDIInternalFrame) viewFrameIterator.next();
      Container contentPane = viewFrame.getContentPane();
      for(int i = 0; i < contentPane.getComponentCount(); i++) {
        if(contentPane.getComponent(i) instanceof VizView) {
          ((VizView)contentPane.getComponent(i)).redraw();
          break;
        }
      }
    }
  }
  /**
   * Determines whether or not a key is in the current content specification.  Used by VizView to
   * determine which elements to draw.
   * @param key the key being checked.
   * @return boolean the truth value of the statement "This key is in the specification."
   */
  public boolean isInContentSpec(Integer key) {
    return contentSpec.isInContentSpec(key);
  }
  public List getValidTokenIds() {
    return contentSpec.getValidTokenIds();
  }
  public void printSpec() {
    contentSpec.printSpec();
  }
  /**
   * Closes all of the views in the ViewSet.
   */
  public void close() {
    Object [] viewSet = views.values().toArray();
    try
      {
        for(int i = 0; i < viewSet.length; i++) {
          ((MDIInternalFrame)viewSet[i]).setClosed(true);
        }
        contentSpecWindow.setClosed(true);
      }
    catch(PropertyVetoException pve){}
  }
  /**
   * Determines whether or not a view already exists.
   * @param viewName the name of the view
   * @return boolean whether or not the view exists.
   */
  public boolean viewExists(String viewName) {
    return views.containsKey(viewName);
  }
  
  public void notifyDeleted(MDIFrame frame) {
    removeViewFrame((MDIInternalFrame) frame);
  }
  public void add(JButton button) {
  }
}
