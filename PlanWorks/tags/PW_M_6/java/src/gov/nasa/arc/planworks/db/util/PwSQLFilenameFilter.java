//
// * See the file "PlanWorks/disclaimers-and-notices.txt" for
// * information on usage and redistribution of this file,
// * and for a DISCLAIMER OF ALL WARRANTIES.
//

// $Id: PwSQLFilenameFilter.java,v 1.3 2003-09-05 16:51:43 miatauro Exp $
//
package gov.nasa.arc.planworks.db.util;

import java.io.File;
import java.io.FilenameFilter;

import gov.nasa.arc.planworks.db.DbConstants;

/**
 * A class for filtering out files not in the established structure for database import
 */

public class PwSQLFilenameFilter implements FilenameFilter {
  public PwSQLFilenameFilter() {
  }
  public boolean accept(File dir, String name) {
    return (name.endsWith(DbConstants.PP_PARTIAL_PLAN_EXT) || 
            name.endsWith(DbConstants.PP_OBJECTS_EXT) || 
            name.endsWith(DbConstants.PP_TIMELINES_EXT) || 
            name.endsWith(DbConstants.PP_SLOTS_EXT) ||
            name.endsWith(DbConstants.PP_TOKENS_EXT) || 
            name.endsWith(DbConstants.PP_VARIABLES_EXT) ||
            name.endsWith(DbConstants.PP_PREDICATES_EXT) || 
            name.endsWith(DbConstants.PP_PARAMETERS_EXT) ||
            name.endsWith(DbConstants.PP_ENUMERATED_DOMAINS_EXT) || 
            name.endsWith(DbConstants.PP_INTERVAL_DOMAINS_EXT) ||
            name.endsWith(DbConstants.PP_CONSTRAINTS_EXT) ||
            name.endsWith(DbConstants.PP_TOKEN_RELATIONS_EXT) || 
            name.endsWith(DbConstants.PP_PARAM_VAR_TOKEN_MAP_EXT) ||
            name.endsWith(DbConstants.PP_CONSTRAINT_VAR_MAP_EXT));
  }
}
