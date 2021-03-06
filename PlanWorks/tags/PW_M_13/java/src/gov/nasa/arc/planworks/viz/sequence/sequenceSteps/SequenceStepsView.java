// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: SequenceStepsView.java,v 1.17 2004-02-12 01:26:51 taylor Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 24sep03
//

package gov.nasa.arc.planworks.viz.sequence.sequenceSteps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

// PlanWorks/java/lib/JGo/JGo.jar
import com.nwoods.jgo.JGoArea;
import com.nwoods.jgo.JGoBrush;
import com.nwoods.jgo.JGoDocument;
import com.nwoods.jgo.JGoPen;
import com.nwoods.jgo.JGoRectangle;
import com.nwoods.jgo.JGoText;
import com.nwoods.jgo.JGoView;

// PlanWorks/java/lib/JGo/Classier.jar
import com.nwoods.jgo.examples.BasicNode;

import gov.nasa.arc.planworks.PlanWorks;
import gov.nasa.arc.planworks.CreateSequenceViewThread;
import gov.nasa.arc.planworks.SequenceViewMenuItem;
import gov.nasa.arc.planworks.db.PwConstraint;
import gov.nasa.arc.planworks.db.PwObject;
import gov.nasa.arc.planworks.db.PwPartialPlan;
import gov.nasa.arc.planworks.db.PwPlanningSequence;
import gov.nasa.arc.planworks.db.PwSlot;
import gov.nasa.arc.planworks.db.PwTimeline;
import gov.nasa.arc.planworks.db.PwToken;
import gov.nasa.arc.planworks.db.PwVariable;
import gov.nasa.arc.planworks.util.ColorMap;
import gov.nasa.arc.planworks.util.MouseEventOSX;
import gov.nasa.arc.planworks.util.ResourceNotFoundException;
import gov.nasa.arc.planworks.util.Utilities;
import gov.nasa.arc.planworks.viz.ViewConstants;
import gov.nasa.arc.planworks.viz.ViewGenerics;
import gov.nasa.arc.planworks.viz.VizView;
import gov.nasa.arc.planworks.viz.VizViewOverview;
import gov.nasa.arc.planworks.viz.nodes.NodeGenerics;
import gov.nasa.arc.planworks.viz.nodes.TokenNode;
import gov.nasa.arc.planworks.viz.sequence.SequenceView;
import gov.nasa.arc.planworks.viz.sequence.SequenceViewSet;
import gov.nasa.arc.planworks.viz.viewMgr.ViewableObject;
import gov.nasa.arc.planworks.viz.viewMgr.ViewSet;


/**
 * <code>SequenceStepsView</code> - render a histogram of plan data base size
 *                        over sequence steps
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                  NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class SequenceStepsView extends SequenceView {

  protected static final String DB_TOKENS = "numTokens";
  protected static final String DB_VARIABLES = "numVariables";
  protected static final String DB_CONSTRAINTS = "numConstraints";

  public static final Color TOKENS_BG_COLOR = ColorMap.getColor( "seaGreen1");
  public static final Color VARIABLES_BG_COLOR = ColorMap.getColor( "skyBlue");
  public static final Color DB_CONSTRAINTS_BG_COLOR = ColorMap.getColor( "lightYellow");

  private PwPlanningSequence planSequence;
  private long startTimeMSecs;
  private ViewSet viewSet;
  private SequenceStepsJGoView jGoView;
  private JGoDocument document;
  private Graphics graphics;
  private FontMetrics fontMetrics;
  private Font font;
  private float heightScaleFactor;
  private List stepElementList;

  /**
   * variable <code>selectedStepElement</code>
   *
   */
  protected StepElement selectedStepElement;

  /**
   * <code>SequenceStepsView</code> - constructor 
   *                             Use SwingUtilities.invokeLater( runInit) to
   *                             properly render the JGo widgets
   *
   * @param planSequence - <code>ViewableObject</code> - 
   * @param viewSet - <code>ViewSet</code> - 
   */
  public SequenceStepsView( ViewableObject planSequence,  ViewSet viewSet) {
    super( (PwPlanningSequence) planSequence, (SequenceViewSet) viewSet);
    this.planSequence = (PwPlanningSequence) planSequence;
    this.startTimeMSecs = System.currentTimeMillis();
    this.viewSet = (SequenceViewSet) viewSet;

    setLayout( new BoxLayout( this, BoxLayout.Y_AXIS));

    stepElementList = new ArrayList();
    selectedStepElement = null;

    jGoView = new SequenceStepsJGoView();
    jGoView.setBackground( ViewConstants.VIEW_BACKGROUND_COLOR);
    add( jGoView, BorderLayout.NORTH);
    jGoView.validate();
    jGoView.setVisible( true);
    this.setVisible( true);

    SwingUtilities.invokeLater( runInit);
  } // end constructor


  Runnable runInit = new Runnable() {
      public void run() {
        init();
      }
    };

  /**
   * <code>init</code> - wait for instance to become displayable, determine
   *                     appropriate font metrics, and render the JGo timeline,
   *                     and slot widgets
   *
   *    These functions are not done in the constructor to avoid:
   *    "Cannot measure text until a JGoView exists and is part of a visible window".
   *    called by componentShown method on the JFrame
   *    JGoView.setVisible( true) must be completed -- use runInit in constructor
   */
  public void init() {
    jGoView.setCursor( new Cursor( Cursor.WAIT_CURSOR));
    // wait for ConstraintNetworkView instance to become displayable
    while (! this.isDisplayable()) {
      try {
        Thread.currentThread().sleep(50);
      } catch (InterruptedException excp) {
      }
      // System.err.println( "constraintNetworkView displayable " + this.isDisplayable());
    }
    graphics = this.getGraphics();
    font = new Font( ViewConstants.TIMELINE_VIEW_FONT_NAME,
                     ViewConstants.TIMELINE_VIEW_FONT_STYLE,
                     ViewConstants.TIMELINE_VIEW_FONT_SIZE);
    // does nothing
    // jGoView.setFont( font);
    fontMetrics = graphics.getFontMetrics( font);

    // graphics.dispose();

    document = jGoView.getDocument();

    heightScaleFactor = computeHeightScaleFactor();
    renderHistogram();

    expandViewFrame( viewSet.openView( this.getClass().getName()),
                     (int) jGoView.getDocumentSize().getWidth(),
                     (int) jGoView.getDocumentSize().getHeight());
    long stopTimeMSecs = System.currentTimeMillis();
    System.err.println( "   ... elapsed time: " +
                        (stopTimeMSecs - startTimeMSecs) + " msecs.");
    jGoView.setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  } // end init


  /**
   * <code>redraw</code>
   *
   */
  public void redraw() {
    Thread thread = new RedrawViewThread();
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();
  }

  class RedrawViewThread extends Thread {

    public RedrawViewThread() {
    }  // end constructor

    public void run() {
      redrawView();
    } //end run

  } // end class RedrawViewThread

  private void redrawView() {
    jGoView.setCursor( new Cursor( Cursor.WAIT_CURSOR));
    document.deleteContents();
    renderHistogram();

    expandViewFrame( viewSet.openView( this.getClass().getName()),
                     (int) jGoView.getDocumentSize().getWidth(),
                     (int) jGoView.getDocumentSize().getHeight());

    jGoView.setCursor( new Cursor( Cursor.DEFAULT_CURSOR));
  } // end redrawView


  /**
   * <code>getJGoDocument</code>
   *
   * @return - <code>JGoDocument</code> - 
   */
  public final JGoDocument getJGoDocument()  {
    return this.document;
  }

  /**
   * <code>getJGoView</code> - needed for PlanWorksTest
   *
   * @return - <code>JGoView</code> - 
   */
  public final JGoView getJGoView()  {
    return jGoView;
  }

  /**
   * <code>getFontMetrics</code>
   *
   * @return - <code>FontMetrics</code> - 
   */
  public final FontMetrics getFontMetrics()  {
    return fontMetrics;
  }

  /**
   * <code>getStepElementList</code>
   *
   * @return - <code>List</code> - of List of StepElement
   */
  public final List getStepElementList() {
    return stepElementList;
  }

  /**
   * <code>getSelectedStepElement</code>
   *
   * @return - <code>StepElement</code> - 
   */
  public final StepElement getSelectedStepElement() {
    return selectedStepElement;
  }

  /**
   * <code>setSelectedStepElement</code>
   *
   * @param element - <code>StepElement</code> - 
   * @return - <code>StepElement</code> - 
   */
  public final void setSelectedStepElement( StepElement element) {
    selectedStepElement = element;
  }

  private float computeHeightScaleFactor() {
    int maxDbSize = 0;
    Iterator sizeItr = planSequence.getPlanDBSizeList().iterator();
    while (sizeItr.hasNext()) {
      int [] planDbSizes= (int[]) sizeItr.next();
      int dbSize = planDbSizes[0] + planDbSizes[1] + planDbSizes[2];
//       System.err.println( "dbSize " + dbSize + " " + planDbSizes[0] +
//                           " " + planDbSizes[1] + " " + planDbSizes[2]);
      if (dbSize > maxDbSize) {
        maxDbSize = dbSize;
      }
    }
//     System.err.println( "computeHeightScaleFactor: " +
//                         ViewConstants.STEP_VIEW_Y_MAX / (float) maxDbSize +
//                         " maxDbSize " + maxDbSize);
    return ViewConstants.STEP_VIEW_Y_MAX / (float) maxDbSize;
  } // end computeHeightScaleFactor

  private void renderHistogram() {
    // System.err.println( "stepCount " + planSequence.getStepCount());
    // System.err.println( "stepNumbers " + planSequence.getPartialPlanNamesList());
    
    int x = ViewConstants.STEP_VIEW_X_INIT;
    int stepNumber = 0;
    Iterator sizeItr = planSequence.getPlanDBSizeList().iterator();
    while (sizeItr.hasNext()) {
      int y = ViewConstants.STEP_VIEW_Y_INIT;
      String partialPlanName = "step".concat( String.valueOf( stepNumber));
      int [] planDbSizes= (int[]) sizeItr.next();

      int height = Math.max( 1, (int) (planDbSizes[0] * heightScaleFactor));
      List elementList = new ArrayList();
      StepElement stepElement = new StepElement( x, y, height, DB_TOKENS,
                                                 planDbSizes[0], TOKENS_BG_COLOR,
                                                 partialPlanName, planSequence, this);
      elementList.add( stepElement);
      document.addObjectAtTail( stepElement);
      y += height;

      height = Math.max( 1, (int) (planDbSizes[1] * heightScaleFactor));
      stepElement = new StepElement( x, y, height, DB_VARIABLES,
                                     planDbSizes[1], VARIABLES_BG_COLOR,
                                     partialPlanName, planSequence, this);
      elementList.add( stepElement);
      document.addObjectAtTail( stepElement);
      y += height;

      height = Math.max( 1, (int) (planDbSizes[2] * heightScaleFactor));
      stepElement = new StepElement( x, y, height, DB_CONSTRAINTS,
                                     planDbSizes[2], DB_CONSTRAINTS_BG_COLOR,
                                     partialPlanName, planSequence, this);
      elementList.add( stepElement);
      document.addObjectAtTail( stepElement);
      y += height;

      stepElementList.add( elementList);
      // display step number for every 10th step
      if ((stepNumber % 10) == 0) {
        JGoText textObject = new JGoText( new Point( x, y + 4), String.valueOf( stepNumber));
        textObject.setResizable( false);
        textObject.setEditable( false);
        textObject.setDraggable( false);
        textObject.setBkColor( ViewConstants.VIEW_BACKGROUND_COLOR);
        document.addObjectAtTail( textObject);
      }
      x += ViewConstants.STEP_VIEW_STEP_WIDTH;
      stepNumber++;
    }
  } // end renderHistogram


  /**
   * <code>SequenceStepsJGoView</code> - subclass JGoView to add doBackgroundClick
   *
   */
  class SequenceStepsJGoView extends JGoView {

    /**
     * <code>SequenceStepsJGoView</code> - constructor 
     *
     */
    public SequenceStepsJGoView() {
      super();
    }

    /**
     * <code>doBackgroundClick</code> - Mouse-Right pops up menu:
     *
     * @param modifiers - <code>int</code> - 
     * @param docCoords - <code>Point</code> - 
     * @param viewCoords - <code>Point</code> - 
     */
    public void doBackgroundClick( int modifiers, Point docCoords, Point viewCoords) {
      if (MouseEventOSX.isMouseLeftClick( modifiers, PlanWorks.isMacOSX())) {
        // do nothing
      } else if (MouseEventOSX.isMouseRightClick( modifiers, PlanWorks.isMacOSX())) {

        mouseRightPopupMenu( viewCoords);

      }
    } // end doBackgroundClick

  } // end class SequenceStepsJGoView

  private void mouseRightPopupMenu( Point viewCoords) {
    JPopupMenu mouseRightPopup = new JPopupMenu();

//     JMenuItem modelRulesViewItem = new JMenuItem( "Open Model Rules View");
//     createModelRulesViewItem( modelRulesViewItem, this, viewCoords);
//     mouseRightPopup.add( modelRulesViewItem);

    JMenuItem overviewWindowItem = new JMenuItem( "Overview Window");
    createOverviewWindowItem( overviewWindowItem, this, viewCoords);
    mouseRightPopup.add( overviewWindowItem);

    JMenuItem refreshItem = new JMenuItem("Refresh");
    createRefreshItem(refreshItem, this);
    mouseRightPopup.add(refreshItem);

    createCloseHideShowViewItems( mouseRightPopup);

    NodeGenerics.showPopupMenu( mouseRightPopup, this, viewCoords);
  } // end mouseRightPopupMenu


  private void createRefreshItem(JMenuItem refreshItem, 
                                 final SequenceStepsView sequenceStepsView) {
    refreshItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          System.err.println("Refreshing planning sequence...");
          planSequence.refresh();
          System.err.println("Redrawing sequence steps view...");
          heightScaleFactor = computeHeightScaleFactor();
          redraw();
          System.err.println("   ... Done.");
        }
      });
  }

  private void createOverviewWindowItem( JMenuItem overviewWindowItem,
                                         final SequenceStepsView sequenceStepsView,
                                         final Point viewCoords) {
    overviewWindowItem.addActionListener( new ActionListener() { 
        public void actionPerformed( ActionEvent evt) {
          VizViewOverview currentOverview =
            ViewGenerics.openOverviewFrame( PlanWorks.SEQUENCE_STEPS_VIEW, planSequence,
                                            sequenceStepsView, viewSet, jGoView, viewCoords);
          if (currentOverview != null) {
            overview = currentOverview;
          }
        }
      });
  } // end createOverviewWindowItem


  private void createModelRulesViewItem( JMenuItem modelRulesViewItem,
                                         final SequenceStepsView sequenceStepsView,
                                         final Point viewCoords) {
    modelRulesViewItem.addActionListener( new ActionListener() { 
        public void actionPerformed( ActionEvent evt) {
          String seqName = planSequence.getName();
          SequenceViewMenuItem modelRulesItem =
            new SequenceViewMenuItem( seqName, planSequence.getUrl(), seqName);
          Thread thread = new CreateSequenceViewThread(PlanWorks.MODEL_RULES_VIEW,
                                                       modelRulesItem);
          thread.setPriority(Thread.MIN_PRIORITY);
          thread.start();
        }
      });
  } // end createModelRulesViewItem


} // end class SequenceStepsView
