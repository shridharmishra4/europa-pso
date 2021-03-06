// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 
// $Id: TransactionHeaderView.java,v 1.18 2004-09-27 23:27:26 taylor Exp $
//
// PlanWorks
//
// Will Taylor -- started 120may04
//

package gov.nasa.arc.planworks.viz;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

// PlanWorks/java/lib/JGo/JGo.jar
import com.nwoods.jgo.JGoBrush;
import com.nwoods.jgo.JGoDocument;
import com.nwoods.jgo.JGoText;
import com.nwoods.jgo.JGoView;

// PlanWorks/java/lib/JGo/Classier.jar
import com.nwoods.jgo.examples.TextNode;

import gov.nasa.arc.planworks.db.PwEntity;
import gov.nasa.arc.planworks.viz.util.AskQueryEntityKey;
import gov.nasa.arc.planworks.viz.util.DBTransactionTable;

/**
 * <code>TransactionHeaderView</code> - render query text, if appropriate
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *           NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class TransactionHeaderView extends JGoView {

  private String query;
  private JGoDocument jGoDocument;

  /**
   * <code>TransactionHeaderView</code> - constructor 
   *
   * @param query - <code>String</code> - 
   */
  public TransactionHeaderView( final String query) {
    super();
    this.query = query;

    setBackground( ViewConstants.VIEW_BACKGROUND_COLOR);

    jGoDocument = this.getDocument();

    renderTransactionHeader( query);

  } // end constructor


  private void renderTransactionHeader( final String query) {
    Color bgColor = ViewConstants.VIEW_BACKGROUND_COLOR;
    int x = 0, y = 3;
    if (query != null) {
      TextNode queryNode = new TextNode( " Query: " + query + " ");
      configureTextNode( queryNode, new Point( x, y), bgColor);
      jGoDocument.addObjectAtTail( queryNode);
      y += (int) queryNode.getSize().getHeight() + 2;
    }
    setVerticalScrollBar( null);
    setHorizontalScrollBar( null);
  } // end renderTransactionHeader


  private void configureTextNode( final TextNode node, final Point location,
                                  final Color bgColor) {
    node.setBrush( JGoBrush.makeStockBrush( bgColor));
    node.getLabel().setEditable( false);
    node.getLabel().setBold( true);
    node.getLabel().setMultiline( false);
    node.getLabel().setAlignment( JGoText.ALIGN_CENTER);
    node.setDraggable( false);
    // do not allow user links
    node.getTopPort().setVisible( false);
    node.getLeftPort().setVisible( false);
    node.getBottomPort().setVisible( false);
    node.getRightPort().setVisible( false);
    node.setLocation( (int) location.getX(), (int) location.getY());
  } // end configureTextNode

  /**
   * <code>createTransByKeyItem</code>
   *
   * @param transByKeyItem - <code>JMenuItem</code> - 
   * @param contentScrollPane - <code>JScrollPane</code> - 
   * @param contentTable - <code>JTable</code> - 
   * @param colIndx - <code>int</code> - 
   * @param vizView - <code>VizView</code> - 
   */
  protected final void createTransByKeyItem( final JMenuItem transByKeyItem,
                                             final JScrollPane contentScrollPane,
                                             final JTable contentTable, final int colIndx,
                                             final VizView vizView) {
    transByKeyItem.addActionListener( new ActionListener() {
        public final void actionPerformed( final ActionEvent evt) {
          AskQueryEntityKey transByKeyDialog =
            new AskQueryEntityKey( ((DBTransactionTable) contentTable).
                                   getCurrentEntityKeyList( colIndx),
                                   transByKeyItem.getText(), "key (int)", vizView);
          Integer entityKey = transByKeyDialog.getEntityKey();
          if (entityKey != null) {
            int rowIndx = transByKeyDialog.getEntityListIndex();
            if ((contentScrollPane != null) && (contentTable != null) && (colIndx != -1)) {
              int newPosition = contentTable.getRowHeight() * rowIndx;
              contentScrollPane.getVerticalScrollBar().setValue( newPosition);
              contentTable.getSelectionModel().clearSelection();
              contentTable.getColumnModel().
                getSelectionModel().setSelectionInterval( colIndx, colIndx);
              contentTable.getSelectionModel().setSelectionInterval( rowIndx, rowIndx);
              System.err.println( "transByKeyItem: entityKey " + entityKey.toString() +
                                  " found");
            }
          }
        }
      });
  } // end createTransByKeyItem

} // end class TransactionHeaderView
