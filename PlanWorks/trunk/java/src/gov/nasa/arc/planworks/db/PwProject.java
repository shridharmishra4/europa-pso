// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: PwProject.java,v 1.5 2003-06-08 00:14:08 taylor Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 08May03
//

package gov.nasa.arc.planworks.db;

import java.util.List;

import gov.nasa.arc.planworks.db.impl.PwProjectImpl;
import gov.nasa.arc.planworks.util.DuplicateNameException;
import gov.nasa.arc.planworks.util.ResourceNotFoundException;

/**
 * <code>PwProject</code> - 
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                         NASA Ames Research Center - Code IC
 * @version 0.0
 */
public abstract class PwProject {


  /**
   * <code>initProjects</code> - register loaded XML files data base (eXist)
   *                             restore loaded projects configuration info
   *
   */
  public static void initProjects() throws ResourceNotFoundException {
    PwProjectImpl.initProjects();
  }

  /**
   * <code>createProject</code>
   *
   * @param url - <code>String</code> - 
   * @return - <code>PwProject</code> - 
   */
  public static PwProject createProject( String url)
    throws DuplicateNameException, ResourceNotFoundException {
    return (new PwProjectImpl( url));
  }

  /**
   * <code>openProject</code>
   *
   * @param url - <code>String</code> - 
   * @return - <code>PwProject</code> - 
   */
  public static PwProject openProject( String url)
    throws ResourceNotFoundException {
    boolean isInDb = true;
    return (new PwProjectImpl( url, isInDb));
  }

  /**
   * <code>getProject</code> - get project instance after it is Created or Opened
   *
   * @param url - <code>String</code> - 
   * @return - <code>PwProject</code> - 
   */
  public static PwProject getProject( String url) {
    return PwProjectImpl.getProject( url);
  }

  /**
   * <code>listProjects</code>
   *
   * @return - <code>List</code> - of String (url)
   */
  public static List listProjects() {
    return PwProjectImpl.listProjects();
  }

  /**
   * <code>saveProjects</code> - save project names & urls in /xml/proj/projects.xml
   *                             and save project specific info in separate files
   *
   */
  public static void saveProjects() throws Exception {
    PwProjectImpl.saveProjects();
  }

  /**
   * <code>getUrl</code>
   *
   * @return - <code>String</code> - 
   */
  public abstract String getUrl();

  /**
   * <code>getName</code> - project name (directory containing
   *                               planning sequences
   *
   * @return - <code>String</code> - 
   */
  public abstract String getName();

  /**
   * <code>listPlanningSequences</code>
   *
   * @return - <code>List</code> -  List of String (name of sequence)
   *                                each sequence is set of partial plans
   */
  public abstract List listPlanningSequences();

  /**
   * <code>getPlanningSequence</code>
   *
   * @param seqName - <code>String</code> - 
   * @return - <code>PwPlanningSequence</code> - 
   * @exception ResourceNotFoundException if an error occurs
   */
  public abstract PwPlanningSequence getPlanningSequence( String seqName)
    throws ResourceNotFoundException;

  /**
   * <code>getPlanningSequenceNames</code>
   *
   * @return - <code>List</code> - of String
   */
  public abstract List getPlanningSequenceNames();

  /**
   * <code>getPartialPlanNames</code>
   *
   * @param seqName - <code>String</code> - 
   * @return - <code>List</code> - of List of String
   */
  public abstract List getPartialPlanNames( String seqName);

  /**
   * <code>close</code> - remove project from /xml/proj/projects.xml, and
   *                      remove /xml/proj/<projectName>.xml
   *
   * @exception Exception if an error occurs
   */
  public abstract void close() throws Exception, ResourceNotFoundException;

  /**
   * <code>requiresSaving</code>
   *
   * @return - <code>boolean</code> - 
   */
  public abstract boolean requiresSaving();

  /**
   * <code>save</code> - save project url, name, & seqDirNames in /xml/proj/<projectName>.xml
   *
   * @exception Exception if an error occurs
   */
  public abstract void save() throws Exception;




} // end class PwProject
