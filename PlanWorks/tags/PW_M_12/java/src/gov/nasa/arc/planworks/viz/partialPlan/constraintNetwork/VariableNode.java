// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 
// $Id: VariableNode.java,v 1.11 2004-01-16 19:05:37 taylor Exp $
//
// PlanWorks
//
// Will Taylor -- started 28july03
//

package gov.nasa.arc.planworks.viz.partialPlan.constraintNetwork;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

// PlanWorks/java/lib/JGo/JGo.jar
import com.nwoods.jgo.JGoBrush;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoPen;
import com.nwoods.jgo.JGoText;
import com.nwoods.jgo.JGoView;

// PlanWorks/java/lib/JGo/Classier.jar
import com.nwoods.jgo.examples.BasicNode;

import gov.nasa.arc.planworks.PlanWorks;
import gov.nasa.arc.planworks.db.DbConstants;
import gov.nasa.arc.planworks.db.PwEnumeratedDomain;
import gov.nasa.arc.planworks.db.PwObject;
import gov.nasa.arc.planworks.db.PwPartialPlan;
import gov.nasa.arc.planworks.db.PwVariable;
import gov.nasa.arc.planworks.mdi.MDIInternalFrame;
import gov.nasa.arc.planworks.util.ColorMap;
import gov.nasa.arc.planworks.util.MouseEventOSX;
import gov.nasa.arc.planworks.viz.ViewConstants;
import gov.nasa.arc.planworks.viz.nodes.BasicNodeLink;
import gov.nasa.arc.planworks.viz.nodes.ExtendedBasicNode;
import gov.nasa.arc.planworks.viz.nodes.NodeGenerics;
import gov.nasa.arc.planworks.viz.nodes.TokenNode;
import gov.nasa.arc.planworks.viz.partialPlan.PartialPlanView;
import gov.nasa.arc.planworks.viz.partialPlan.navigator.NavigatorView;


/**
 * <code>VariableNode</code> - JGo widget to render a token variable with a
 *                          label 
 *             Object->JGoObject->JGoArea->TextNode->VariableNode
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *       NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class VariableNode extends ExtendedBasicNode {

  private static final boolean IS_FONT_BOLD = false;
  private static final boolean IS_FONT_UNDERLINED = false;
  private static final boolean IS_FONT_ITALIC = false;
  private static final int TEXT_ALIGNMENT = JGoText.ALIGN_LEFT;
  private static final boolean IS_TEXT_MULTILINE = false;
  private static final boolean IS_TEXT_EDITABLE = false;

  private PwVariable variable;
  private TokenNode tokenNode;
  private boolean isFreeToken;
  private PartialPlanView partialPlanView;
  private String nodeLabel;
  private List tokenNodeList; // element TokenNode
  private List constraintNodeList; // element ConstraintNode
  private List variableTokenLinkList; // element BasicNodeLink
  private boolean areNeighborsShown;
  private boolean inLayout;
  private boolean hasZeroConstraints;
  private int tokenLinkCount;
  private int constraintLinkCount;
  private boolean isDebug;
  private boolean hasBeenVisited;
  private Color backgroundColor;

  /**
   * <code>VariableNode</code> - constructor 
   *
   * @param variable - <code>PwVariable</code> - 
   * @param tokenNode - <code>TokenNode</code> - 
   * @param variableLocation - <code>Point</code> - 
   * @param backgroundColor - <code>Color</code> - 
   * @param isFreeToken - <code>boolean</code> - 
   * @param isDraggable - <code>boolean</code> - 
   * @param partialPlanView - <code>PartialPlanView</code> - 
   */
  public VariableNode( PwVariable variable, TokenNode tokenNode, Point variableLocation, 
                       Color backgroundColor, boolean isFreeToken, boolean isDraggable,
                       PartialPlanView partialPlanView) { 
    super( ViewConstants.ELLIPSE);
    this.variable = variable;
    this.isFreeToken = isFreeToken;
    this.partialPlanView = partialPlanView;
    tokenNodeList = new ArrayList();
    tokenNodeList.add( tokenNode);
    constraintNodeList = new ArrayList();
    variableTokenLinkList = new ArrayList();
    this.backgroundColor = backgroundColor;

    inLayout = false;
    hasZeroConstraints = true;
    if (variable.getConstraintList().size() > 0) {
      hasZeroConstraints = false;
    }
    hasBeenVisited = false;
    resetNode( false);

    isDebug = false;
    // isDebug = true;
    StringBuffer labelBuf = new StringBuffer( variable.getDomain().toString());
    labelBuf.append( "\nkey=").append( variable.getId().toString());
    nodeLabel = labelBuf.toString();
    // System.err.println( "VariableNode: " + nodeLabel);

    configure( variableLocation, backgroundColor, isDraggable);
  } // end constructor

  private final void configure( Point variableLocation, Color backgroundColor,
                                boolean isDraggable) {
    setLabelSpot( JGoObject.Center);
    initialize( variableLocation, nodeLabel);
    setBrush( JGoBrush.makeStockBrush( backgroundColor));  
    getLabel().setEditable( false);
    setDraggable( isDraggable);
    // do not allow user links
    getPort().setVisible( false);
    getLabel().setMultiline( true);
    if (hasZeroConstraints) {
      setPen( new JGoPen( JGoPen.SOLID, 2,  ColorMap.getColor( "black")));
    }
  } // end configure


  public Color getColor(){return backgroundColor;}

  /**
   * <code>getVariable</code>
   *
   * @return - <code>PwVariable</code> - 
   */
  public PwVariable getVariable() {
    return variable;
  }

  /**
   * <code>getPartialPlanView</code>
   *
   * @return - <code>PartialPlanView</code> - 
   */
  public PartialPlanView getPartialPlanView() {
    return partialPlanView;
  }

  /**
   * <code>getToolTipText</code>
   *
   * @return - <code>String</code> - 
   */
  public String getToolTipText() {
      String operation = null;
      if (areNeighborsShown) {
        operation = "close";
      } else {
        operation = "open";
      }
    if ((! hasZeroConstraints) && (partialPlanView instanceof ConstraintNetworkView)) {
      StringBuffer tip = new StringBuffer( "<html> ");
      NodeGenerics.getVariableNodeToolTipText( variable, partialPlanView, tip);
      if (isDebug) {
        tip.append( " linkCnt ").append( String.valueOf( tokenLinkCount +
                                                         constraintLinkCount));
      }
       tip.append( "<br> Mouse-L: ").append( operation);
       return tip.append("</html>").toString();
    } else {
      return variable.getType();
    }
  } // end getToolTipText

  /**
   * <code>getToolTipText</code> - when over 1/8 scale overview variable node
   *
   * @param isOverview - <code>boolean</code> - 
   * @return - <code>String</code> - 
   */
  public String getToolTipText( boolean isOverview) {
    StringBuffer tip = new StringBuffer( "<html> ");
    tip.append( variable.getDomain().toString());
    tip.append( "<br>key=");
    tip.append( variable.getId().toString());
    tip.append( "</html>");
    return tip.toString();
  } // end getToolTipText

  /**
   * <code>getTokenNodeList</code>
   *
   * @return - <code>List</code> - of TokenNode
   */
  public List getTokenNodeList() {
    return tokenNodeList;
  }

  /**
   * <code>addTokenNode</code>
   *
   * @param tokenNode - <code>TokenNode</code> - 
   */
  public void addTokenNode( TokenNode tokenNode) {
    if (!tokenNodeList.contains(tokenNode)) {
      tokenNodeList.add( tokenNode);
    }
  }

  /**
   * <code>getConstraintNodeList</code>
   *
   * @return - <code>List</code> - of ConstraintNode
   */
  public List getConstraintNodeList() {
    return constraintNodeList;
  }

  /**
   * <code>addConstraintNode</code>
   *
   * @param constraintNode - <code>ConstraintNode</code> - 
   */
  public void addConstraintNode( ConstraintNode constraintNode) {
    if (!constraintNodeList.contains(constraintNode)) {
      constraintNodeList.add( constraintNode);
    }
  }

  /**
   * <code>getVariableTokenLinkList</code>
   *
   * @return - <code>List</code> - 
   */
  public List getVariableTokenLinkList() {
    return variableTokenLinkList;
  }

  /**
   * <code>addLink</code>
   *
   * @param link - <code>BasicNodeLink</code> - 
   */
  public void addLink( BasicNodeLink link) {
    if (!variableTokenLinkList.contains(link)) {
      variableTokenLinkList.add( link);
    }
  }

  /**
   * <code>inLayout</code>
   *
   * @return - <code>boolean</code> - 
   */
  public boolean inLayout() {
    return inLayout;
  }

  /**
   * <code>setInLayout</code>
   *
   * @param value - <code>boolean</code> - 
   */
  public void setInLayout( boolean value) {
    int width = 1;
    inLayout = value;
    if (value == false) {
      if (hasZeroConstraints) {
        width = 2;
      }
      setPen( new JGoPen( JGoPen.SOLID, width,  ColorMap.getColor( "black")));
      areNeighborsShown = false;
    }
  }

  /**
   * <code>setAreNeighborsShown</code>
   *
   * @param areShown - <code>boolean</code> - 
   */
  protected void setAreNeighborsShown( boolean areShown) {
    areNeighborsShown = areShown;
  }

  /**
   * <code>areNeighborsShown</code>
   *
   * @return - <code>boolean</code> - 
   */
  protected boolean areNeighborsShown() {
    return areNeighborsShown;
  }

  /**
   * <code>toString</code>
   *
   * @return - <code>String</code> - 
   */
  public String toString() {
    return variable.getId().toString();
  }

  /**
   * <code>incrTokenLinkCount</code>
   *
   */
  public void incrTokenLinkCount() {
    tokenLinkCount++;
  }

  /**
   * <code>decTokenLinkCount</code>
   *
   */
  public void decTokenLinkCount() {
    tokenLinkCount--;
  }

  /**
   * <code>getTokenLinkCount</code>
   *
   * @return - <code>int</code> - 
   */
  public int getTokenLinkCount() {
    return tokenLinkCount;
  }

  /**
   * <code>incrConstraintLinkCount</code>
   *
   */
  public void incrConstraintLinkCount() {
    constraintLinkCount++;
  }

  /**
   * <code>decConstraintLinkCount</code>
   *
   */
  public void decConstraintLinkCount() {
    constraintLinkCount--;
  }

  /**
   * <code>getConstraintLinkCount</code>
   *
   * @return - <code>int</code> - 
   */
  public int getConstraintLinkCount() {
    return constraintLinkCount;
  }

  /**
   * <code>getLinkCount</code> - tokenLinkCount + constraintLinkCount
   *
   * @return - <code>int</code> - 
   */
  public int getLinkCount() {
    return tokenLinkCount + constraintLinkCount;
  }

  /**
   * <code>resetNode</code> - when closed by token close traversal
   *
   * @param isDebug - <code>boolean</code> - 
   */
  public void resetNode( boolean isDebug) {
    areNeighborsShown = false;
    if (isDebug && (constraintLinkCount != 0)) {
      System.err.println( "reset variable node: " + variable.getId() +
                          "; constraintLinkCount != 0: " + constraintLinkCount);
    }
    constraintLinkCount = 0;
    if (isDebug && (tokenLinkCount != 0)) {
      System.err.println( "reset variable node: " + variable.getId() +
                          "; tokenLinkCount != 0: " + tokenLinkCount);
    }
    tokenLinkCount = 0;
  } // end resetNode

  /**
   * <code>setHasBeenVisited</code>
   *
   * @param value - <code>boolean</code> - 
   */
  public void setHasBeenVisited( boolean value) {
    hasBeenVisited =  value;
  }

  /**
   * <code>hasBeenVisited</code>
   *
   * @return - <code>boolean</code> - 
   */
  public boolean hasBeenVisited() {
    return hasBeenVisited;
  }

  /**
   * <code>doMouseClick</code> - For Constraint Network View, Mouse-left opens/closes
   *            variableNode to show constraintNodes
   *
   * @param modifiers - <code>int</code> - 
   * @param dc - <code>Point</code> - 
   * @param vc - <code>Point</code> - 
   * @param view - <code>JGoView</code> - 
   * @return - <code>boolean</code> - 
   */
  public boolean doMouseClick( int modifiers, Point docCoords, Point viewCoords, JGoView view) {
    JGoObject obj = view.pickDocObject( docCoords, false);
    //         System.err.println( "doMouseClick obj class " +
    //                             obj.getTopLevelObject().getClass().getName());
    VariableNode variableNode = (VariableNode) obj.getTopLevelObject();
    if (MouseEventOSX.isMouseLeftClick( modifiers, PlanWorks.isMacOSX())) {
      if ((! hasZeroConstraints) && (partialPlanView instanceof ConstraintNetworkView)) {
        if (! areNeighborsShown) {
          //System.err.println
          //  ( "doMouseClick: Mouse-L show constraint/token nodes of variable id " +
          //    variableNode.getVariable().getId());
          addVariableNodeTokensAndConstraints( this, (ConstraintNetworkView) partialPlanView);
          areNeighborsShown = true;
        } else {
          //System.err.println
          //  ( "doMouseClick: Mouse-L hide constraint/token nodes of variable id " +
          //    variableNode.getVariable().getId());
          removeVariableNodeTokensAndConstraints( this, (ConstraintNetworkView) partialPlanView);
          areNeighborsShown = false;
        }
        return true;
      }
    } else if (MouseEventOSX.isMouseRightClick( modifiers, PlanWorks.isMacOSX())) {
      mouseRightPopupMenu( viewCoords);
      return true;
    }
    return false;
  } // end doMouseClick   

  private void mouseRightPopupMenu( Point viewCoords) {
    JPopupMenu mouseRightPopup = new JPopupMenu();
    JMenuItem navigatorItem = new JMenuItem( "Open Navigator View");
    navigatorItem.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent evt) {
          MDIInternalFrame navigatorFrame = partialPlanView.openNavigatorViewFrame();
          Container contentPane = navigatorFrame.getContentPane();
          PwPartialPlan partialPlan = partialPlanView.getPartialPlan();
          contentPane.add( new NavigatorView( VariableNode.this, partialPlan,
                                              partialPlanView.getViewSet(),
                                              navigatorFrame));
        }
      });
    mouseRightPopup.add( navigatorItem);

    NodeGenerics.showPopupMenu( mouseRightPopup, partialPlanView, viewCoords);
  } // end mouseRightPopupMenu

  /**
   * <code>addVariableNodeTokensAndConstraints</code> - protected since
   *                                           needed by ConstraintJGoView
   *
   * @param variableNode - <code>VariableNode</code> - 
   * @param constraintNetworkView - <code>ConstraintNetworkView</code> - 
   */
  protected void addVariableNodeTokensAndConstraints
    ( VariableNode variableNode, ConstraintNetworkView constraintNetworkView) {
    constraintNetworkView.setStartTimeMSecs( System.currentTimeMillis());
    boolean areNodesChanged = constraintNetworkView.addConstraintNodes( variableNode);
    boolean areLinksChanged =
      constraintNetworkView.addTokenAndConstraintToVariableLinks( variableNode);
    if (areNodesChanged || areLinksChanged) {
      constraintNetworkView.setLayoutNeeded();
      constraintNetworkView.setFocusNode( variableNode);
      constraintNetworkView.redraw();
    }
    setPen( new JGoPen( JGoPen.SOLID, 2,  ColorMap.getColor( "black")));
  } // end addVariableNodeTokensAndConstraints

  private void removeVariableNodeTokensAndConstraints
    ( VariableNode variableNode, ConstraintNetworkView constraintNetworkView) {
    constraintNetworkView.setStartTimeMSecs( System.currentTimeMillis());
    boolean areLinksChanged = constraintNetworkView.removeTokenToVariableLinks( variableNode);
    boolean areNodesChanged = constraintNetworkView.removeConstraintNodes( variableNode);
    if (areNodesChanged || areLinksChanged) {
      constraintNetworkView.setLayoutNeeded();
      constraintNetworkView.setFocusNode( variableNode);
      constraintNetworkView.redraw();
    }
    setPen( new JGoPen( JGoPen.SOLID, 1,  ColorMap.getColor( "black")));
  } // end removeVariableNodeTokensAndConstraints


  public boolean equals(VariableNode v) {
    return variable.getId().equals(v.getVariable().getId());
  }

} // end class VariableNode
