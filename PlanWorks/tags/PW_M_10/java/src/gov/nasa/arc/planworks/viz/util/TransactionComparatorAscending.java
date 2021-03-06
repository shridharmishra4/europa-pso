// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 
// $Id: TransactionComparatorAscending.java,v 1.3 2003-11-03 19:02:41 taylor Exp $
//
// PlanWorks
//
// Will Taylor -- started 27oct03
//

package gov.nasa.arc.planworks.viz.util;

import java.util.Comparator;

import gov.nasa.arc.planworks.db.PwTransaction;
import gov.nasa.arc.planworks.viz.ViewConstants;


public class TransactionComparatorAscending implements Comparator {

  private String field;

  public TransactionComparatorAscending( String field) {
    this.field = field;
  }

  public int compare(Object o1, Object o2) {
    boolean isAscending =true;
    if (field.equals( ViewConstants.TRANSACTION_KEY_HEADER)) {
      Integer s1 = ((PwTransaction) o1).getId();
      Integer s2 = ((PwTransaction) o2).getId();
      return s1.compareTo(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_TYPE_HEADER)) {
      String s1 = ((PwTransaction) o1).getType();
      String s2 = ((PwTransaction) o2).getType();
      return SortStringComparator.compareTo( s1, s2, isAscending);
    } else if (field.equals( ViewConstants.TRANSACTION_SOURCE_HEADER)) {
      String s1 = ((PwTransaction) o1).getSource();
      String s2 = ((PwTransaction) o2).getSource();
      return SortStringComparator.compareTo( s1, s2, isAscending);
    } else if (field.equals( ViewConstants.TRANSACTION_OBJECT_KEY_HEADER)) {
      Integer s1 = ((PwTransaction) o1).getObjectId();
      Integer s2 = ((PwTransaction) o2).getObjectId();
      return s1.compareTo(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_STEP_NUM_HEADER)) {
      Integer s1 = ((PwTransaction) o1).getStepNumber();
      Integer s2 = ((PwTransaction) o2).getStepNumber();
      return s1.compareTo(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_OBJ_NAME_HEADER)) {
      String s1 = ((PwTransaction) o1).getInfo()[0];
      String s2 = ((PwTransaction) o2).getInfo()[0];
      return SortStringComparator.compareTo( s1, s2, isAscending);
    } else if (field.equals( ViewConstants.TRANSACTION_PREDICATE_HEADER)) {
      String s1 = ((PwTransaction) o1).getInfo()[1];
      String s2 = ((PwTransaction) o2).getInfo()[1];
      return SortStringComparator.compareTo( s1, s2, isAscending);
    } else if (field.equals( ViewConstants.TRANSACTION_PARAMETER_HEADER)) {
      String s1 = ((PwTransaction) o1).getInfo()[2];
      String s2 = ((PwTransaction) o2).getInfo()[2];
      return SortStringComparator.compareTo( s1, s2, isAscending);
    }
    System.err.println( "TransactionComparatorAscending: field '" + field +
                        "' not handled");
    System.exit( -1);
    return 0;
  }

  public boolean equals(Object o1, Object o2) {
    if (field.equals( ViewConstants.TRANSACTION_KEY_HEADER)) {
      Integer s1 = ((PwTransaction) o1).getId();
      Integer s2 = ((PwTransaction) o2).getId();
      return s1.equals(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_TYPE_HEADER)) {
      String s1 = ((PwTransaction) o1).getType();
      String s2 = ((PwTransaction) o2).getType();
      return s1.equals(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_SOURCE_HEADER)) {
      String s1 = ((PwTransaction) o1).getSource();
      String s2 = ((PwTransaction) o2).getSource();
      return s1.equals(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_OBJECT_KEY_HEADER)) {
      Integer s1 = ((PwTransaction) o1).getObjectId();
      Integer s2 = ((PwTransaction) o2).getObjectId();
      return s1.equals(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_STEP_NUM_HEADER)) {
      Integer s1 = ((PwTransaction) o1).getStepNumber();
      Integer s2 = ((PwTransaction) o2).getStepNumber();
      return s1.equals(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_OBJ_NAME_HEADER)) {
      String s1 = ((PwTransaction) o1).getInfo()[0];
      String s2 = ((PwTransaction) o2).getInfo()[0];
      return s1.equals(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_PREDICATE_HEADER)) {
      String s1 = ((PwTransaction) o1).getInfo()[1];
      String s2 = ((PwTransaction) o2).getInfo()[1];
      return s1.equals(s2);
    } else if (field.equals( ViewConstants.TRANSACTION_PARAMETER_HEADER)) {
      String s1 = ((PwTransaction) o1).getInfo()[2];
      String s2 = ((PwTransaction) o2).getInfo()[2];
      return s1.equals(s2);
    }
    System.err.println( "TransactionComparatorAscending: field '" + field +
                        "' not handled");
    System.exit( -1);
    return true;
  }

} // end class TransactionComparatorAscending

