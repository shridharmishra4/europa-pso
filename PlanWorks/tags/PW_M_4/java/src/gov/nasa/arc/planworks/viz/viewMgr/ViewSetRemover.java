//
// * See the file "PlanWorks/disclaimers-and-notices.txt" for
// * information on usage and redistribution of this file,
// * and for a DISCLAIMER OF ALL WARRANTIES.
//

// $Id: ViewSetRemover.java,v 1.3 2003-06-16 18:50:40 miatauro Exp $
//
package gov.nasa.arc.planworks.viz.viewMgr;

import gov.nasa.arc.planworks.db.PwPartialPlan;

/**
 * <code>ViewSetRemover</code> -
 * @author <a href="mailto:miatauro@email.arc.nasa.gov">Michael Iatauro</a>
 * Interface to expose only the removeViewSet method to ViewSets.
 */

public interface ViewSetRemover {
  public void removeViewSet(PwPartialPlan key);
}
