// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// PlanWorks
//
// Will Taylor -- started 18may03
//

package gov.nasa.arc.planworks.viz;

import java.awt.Color;
import java.awt.Font;

// PlanWorks/java/lib/JGo/JGo.jar
import com.nwoods.jgo.JGoText;

import gov.nasa.arc.planworks.util.ColorMap;


/**
 * Describe interface <code>ViewConstants</code> here.
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *        NASA Ames Research Center - Code IC
 * @version 0.0
 */
public interface ViewConstants {

  /**
   * constant <code>VIEW_BACKGROUND_COLOR - java.awt.Color</code> 
   *
   */
  public static final Color VIEW_BACKGROUND_COLOR = ColorMap.getColor( "lightGray");

  public static final int INTERNAL_FRAME_X_DELTA = 100;

  public static final int INTERNAL_FRAME_X_DELTA_DIV_4 = 25;

  // views.timeline.TimelineView

  /**
   * constant <code>TIMELINE_VIEW_X_INIT</code>
   *
   */
  public static final int TIMELINE_VIEW_X_INIT = 10; 

  /**
   * constant <code>TIMELINE_VIEW_Y_INIT</code>
   *
   */
  public static final int TIMELINE_VIEW_Y_INIT = 10;

  /**
   * constant <code>TIMELINE_VIEW_X_DELTA</code>
   *
   */
  public static final int TIMELINE_VIEW_X_DELTA = 50;

  /**
   * constant <code>TIMELINE_VIEW_Y_DELTA</code>
   *
   */
  public static final int TIMELINE_VIEW_Y_DELTA = 80; // 60;

  /**
   * constant <code>TIMELINE_VIEW_FONT_SIZE</code>
   *
   */
  public static final int TIMELINE_VIEW_FONT_SIZE = 12;

  /**
   * constant <code>TIMELINE_VIEW_FONT_NAME</code> - fixed width font
   *
   */
  public static final String TIMELINE_VIEW_FONT_NAME = "Monospaced";

  /**
   * constant <code>TIMELINE_VIEW_FONT_STYLE</code>
   *
   */
  public static final int TIMELINE_VIEW_FONT_STYLE = Font.PLAIN;

  /**
   * constant <code>TIME_INTERVAL_STRINGS_OVERLAP_OFFSET</code>
   *
   */
  public static final int TIME_INTERVAL_STRINGS_OVERLAP_OFFSET = 4;

  /**
   * constant <code>TIMELINE_VIEW_EMPTY_NODE_LABEL</code>
   *
   */
  public static final String TIMELINE_VIEW_EMPTY_NODE_LABEL = "-empty-";

  /**
   * constant <code>TIMELINE_VIEW_EMPTY_NODE_LABEL_LEN</code>
   *
   */
  public static final int TIMELINE_VIEW_EMPTY_NODE_LABEL_LEN = 7;

  /**
   * constant <code>TIMELINE_VIEW_INSET_SIZE</code>
   *
   */
  public static final int TIMELINE_VIEW_INSET_SIZE = 10;

  /**
   * constant <code>TIMELINE_VIEW_INSET_SIZE_HALF</code>
   *
   */
  public static final int TIMELINE_VIEW_INSET_SIZE_HALF = TIMELINE_VIEW_INSET_SIZE / 2;

  /**
   * constant <code>TIMELINE_VIEW_IS_FONT_BOLD</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_FONT_BOLD = false;

  /**
   * constant <code>TIMELINE_VIEW_IS_FONT_UNDERLINED</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_FONT_UNDERLINED = false;

  /**
   * constant <code>TIMELINE_VIEW_IS_FONT_ITALIC</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_FONT_ITALIC = false;

  /**
   * constant <code>TIMELINE_VIEW_TEXT_ALIGNMENT</code>
   *
   */
  public static final int TIMELINE_VIEW_TEXT_ALIGNMENT = JGoText.ALIGN_LEFT;

  /**
   * constant <code>TIMELINE_VIEW_IS_TEXT_MULTILINE</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_TEXT_MULTILINE = false;

  /**
   * constant <code>TIMELINE_VIEW_IS_TEXT_EDITABLE</code>
   *
   */
  public static final boolean TIMELINE_VIEW_IS_TEXT_EDITABLE = false;

  /**
   * constant <code>FREE_TOKEN_BG_COLOR</code>
   *
   */
  public static final String FREE_TOKEN_BG_COLOR = "lightGray";

  /**
   * constant <code>EMPTY_SLOT_WIDTH</code>
   *
   */
  public static final int EMPTY_SLOT_WIDTH = 10;

  // Temporal Extent View

  /**
   * constant <code>TEMPORAL_NODE_X_DELTA</code>
   *
   */
  public static final int TEMPORAL_NODE_X_DELTA = 5;

  /**
   * constant <code>TEMPORAL_NODE_Y_DELTA</code>
   *
   */
  public static final int TEMPORAL_NODE_Y_DELTA = 10;

  /**
   * constant <code>TEMPORAL_NODE_CELL_HEIGHT</code>
   *
   */
  public static final int TEMPORAL_NODE_CELL_HEIGHT = 68; // 46;

  /**
   * constant <code>TEMPORAL_NODE_Y_LABEL_OFFSET</code>
   *
   */
  public static final int TEMPORAL_NODE_Y_LABEL_OFFSET = 10;

  /**
   * constant <code>TEMPORAL_NODE_Y_START_OFFSET</code>
   *
   */
  public static final int TEMPORAL_NODE_Y_START_OFFSET = 30; // 22;

  /**
   * constant <code>TEMPORAL_NODE_Y_END_OFFSET</code>
   *
   */
  public static final int TEMPORAL_NODE_Y_END_OFFSET = 42; // 34;

  /**
   * constant <code>TEMPORAL_MIN_END_X_LOC</code>
   *
   */
  public static final int TEMPORAL_MIN_END_X_LOC = 250;

  /**
   * constant <code>TEMPORAL_MIN_MAX_SLOTS</code>
   *
   */
  public static final int TEMPORAL_MIN_MAX_SLOTS = 8;

  /**
   * constant <code>TEMPORAL_LARGE_LABEL_RANGE</code>
   *
   */
  public static final int TEMPORAL_LARGE_LABEL_RANGE = 1000000;

  /**
   * constant <code>JGO_DOC_BORDER_WIDTH</code>
   *
   */
  public static final int JGO_DOC_BORDER_WIDTH = 5;

  /**
   * constant <code>JGO_SCROLL_BAR_WIDTH</code>
   *
   */
  public static final int JGO_SCROLL_BAR_WIDTH = 17;


  // cannot figure out how to get this from MDIInternalFrame
  /**
   * constant <code>MDI_FRAME_DECORATION_HEIGHT</code>
   *
   */
  public static final int MDI_FRAME_DECORATION_HEIGHT = 70;

  // cannot figure out how to get this from MDIInternalFrame
  /**
   * constant <code>MDI_FRAME_DECORATION_WIDTH</code>
   *
   */
  public static final int MDI_FRAME_DECORATION_WIDTH = 35;

  /**
   * constant <code>FRAME_DECORATION_HEIGHT</code>
   *
   */
  public static final int FRAME_DECORATION_HEIGHT = 75;

  /**
   * constant <code>FRAME_DECORATION_WIDTH</code>
   *
   */
  public static final int FRAME_DECORATION_WIDTH = 8;

  public static final int STEP_VIEW_X_INIT = 10;

  public static final int STEP_VIEW_Y_INIT = 10;

  public static final int STEP_VIEW_STEP_WIDTH = 10;

  public static final int STEP_VIEW_Y_MAX = 100;

  public static final String DB_TRANSACTION_KEY_HEADER =        "TX_KEY "; 
  public static final String DB_TRANSACTION_TYPE_HEADER =       "      TRANSACTION_TYPE     "; 
  public static final String DB_TRANSACTION_SOURCE_HEADER =     " SOURCE  ";   
  public static final String DB_TRANSACTION_OBJECT_KEY_HEADER = "OBJ_KEY";
  public static final String DB_TRANSACTION_STEP_NUM_HEADER =   "  STEP  ";
  public static final String DB_TRANSACTION_OBJ_NAME_HEADER =   "     OBJ_NAME     ";
  public static final String DB_TRANSACTION_PREDICATE_HEADER =  "  PREDICATE_NAME  ";
  public static final String DB_TRANSACTION_PARAMETER_HEADER =  "  PARAMETER_NAME  ";

  public static final String QUERY_TOKEN_KEY_HEADER =        "TOK_KEY"; 
  public static final String QUERY_TOKEN_PREDICATE_HEADER =  "  PREDICATE_NAME  "; 
  public static final String QUERY_TOKEN_STEP_NUM_HEADER  =  "  STEP  ";

  public static final String QUERY_VARIABLE_KEY_HEADER =      "VAR_KEY"; 
  public static final String QUERY_VARIABLE_NAME_HEADER =     "   VARIABLE_NAME  "; 
  public static final String QUERY_VARIABLE_STEP_NUM_HEADER = "  STEP  ";

  // Extended BasicNode Shapes for viz/nodes/ExtendedBasicNode

  public static final int RECTANGLE = 0;         // PwToken
  public static final int ELLIPSE = 1;           // not used - long text goes outside border
  public static final int DIAMOND = 2;           // PwConstraint
  public static final int LEFT_TRAPEZOID = 3;    // PwObject
  public static final int RIGHT_TRAPEZOID = 4;   // PwTimeline
  public static final int HEXAGON = 5;           // PwSlot
  public static final int PINCHED_RECTANGLE = 6; // PwVariable
  public static final int PINCHED_HEXAGON = 7;   // PwResource

  /**
   * constant <code>RESOURCE_PROFILE_CELL_HEIGHT</code>
   *
   */
  public static final int RESOURCE_PROFILE_CELL_HEIGHT = 120; 

  /**
   * constant <code>RESOURCE_PROFILE_MIN_Y_OFFSET</code>
   *
   */
  public static final int RESOURCE_PROFILE_MIN_Y_OFFSET = 18;

  /**
   * constant <code>RESOURCE_PROFILE_MAX_Y_OFFSET</code>
   *
   */
  public static final int RESOURCE_PROFILE_MAX_Y_OFFSET = 18;

  /**
   * constant <code>RESOURCE_LEVEL_SCALE_WIDTH_OFFSET</code>
   *
   */
  public static final int RESOURCE_LEVEL_SCALE_WIDTH_OFFSET = 5;

  /**
   * constant <code>RESOURCE_TRANSACTION_HEIGHT</code>
   *
   */
  public static final int RESOURCE_TRANSACTION_HEIGHT = 10;

} // end ViewConstants
