// 
// * See the file "PlanWorks/disclaimers-and-notices.txt" for 
// * information on usage and redistribution of this file, 
// * and for a DISCLAIMER OF ALL WARRANTIES. 
// 

// $Id: PwProjectImpl.java,v 1.37 2003-12-03 02:29:50 taylor Exp $
//
// PlanWorks -- 
//
// Will Taylor -- started 08May03
//

package gov.nasa.arc.planworks.db.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import gov.nasa.arc.planworks.db.DbConstants;
import gov.nasa.arc.planworks.db.PwModel;
import gov.nasa.arc.planworks.db.PwProject;
import gov.nasa.arc.planworks.db.PwPlanningSequence;
import gov.nasa.arc.planworks.util.DuplicateNameException;
import gov.nasa.arc.planworks.util.ResourceNotFoundException;
import gov.nasa.arc.planworks.db.util.MySQLDB;

/**
 * <code>PwProjectImpl</code> - Data base API for PlanWorks
 *                manages Project instances
 *
 * @author <a href="mailto:william.m.taylor@nasa.gov">Will Taylor</a>
 *                         NASA Ames Research Center - Code IC
 * @version 0.0
 */
public class PwProjectImpl extends PwProject {

  private static HashMap projects;

  /**
   * <code>initProjects</code> - initialize projects in the database
   *
   */
  synchronized public static void initProjects() 
    throws ResourceNotFoundException, IOException {
    projects = new HashMap();
    connectToDataBase();

    ListIterator dbProjectNameIterator = MySQLDB.getProjectNames().listIterator();
    while(dbProjectNameIterator.hasNext()) {
      String name = (String) dbProjectNameIterator.next();
      projects.put(name, new PwProjectImpl(name, true));
    }
  } // end initProjects

  /**
   * <code>createProject</code> - create named project not in database
   *
   * @param name - <code>String</code> - the name of the project
   * @return <code>PwProject</code> the complete project object.
   * @exception DuplicateNameException - if the a probect by the given name exists in the database
   */

  public static PwProject createProject(String name) throws DuplicateNameException {
    if(MySQLDB.projectExists(name)) {
      throw new DuplicateNameException("A project named '" + name + "' already exists.");
    }
    PwProjectImpl retval = null;
    retval = new PwProjectImpl(name);
    projects.put(name, retval);

    MySQLDB.addProject(name);
    retval.setId(MySQLDB.latestProjectId());
    return retval;
  }

  /**
   * <code>connectToDataBase</code> - establish database connection
   * @exception IOException if the database fails to start
   */

  private static void connectToDataBase() throws IOException {
    System.err.println("Starting MySQL...");
    long startTime = System.currentTimeMillis();
    MySQLDB.startDatabase();
    startTime = System.currentTimeMillis() - startTime;
    System.err.println("   ... elapsed time: " + startTime + "ms.");
    System.err.println("Connecting to MySQL...");
    long connectTime = System.currentTimeMillis();
    MySQLDB.registerDatabase();
    connectTime = System.currentTimeMillis() - connectTime;
    System.err.println("   ... elapsed time: " + connectTime + "ms.");
  } // end connectToExistDataBase

  /**
   * <code>getProject</code> - get a project object by its name.
   *
   * @param name - <code>String</code> - the name of the project
   * @return - <code>PwProject</code> - the project datastructure
   * @exception - <code>ResourceNotFoundException</code> if no project by that name exists
   */
  public static PwProject getProject( String name) throws ResourceNotFoundException {
    if(!projects.containsKey(name)) {
      throw new ResourceNotFoundException("Project " + name + " not found.");
    }
    return (PwProject) projects.get(name);
  } // end getProject

  /**
   * <code>listProjects</code>
   *
   * @return - <code>List</code> - of String (names)
   */
  public static List listProjects() {
    return new ArrayList(projects.keySet());
  }

  private String name;
  private Integer id;
  private List planningSequences; // element PwPlanningSequence

  /**
   * <code>PwProjectImpl</code> - constructor 
   *                  create a new project from an url
   *                  called from PwProject.createProject
   *
   * @param name - <code>String</code> - 
   * @exception DuplicateNameException if an error occurs
   */
  public PwProjectImpl( String name)  throws DuplicateNameException {
    this.name = name;
    id = new Integer(-1);
    planningSequences = new ArrayList();
  } // end  constructor PwProjectImpl.createProject


  /**
   * <code>PwProjectImpl</code> - constructor
   *                  construct project from information in database.
   *
   * @param name - <code>String</code> - project name
   * @param isInDb - <code>boolean</code> - boolean used to differentiate between constructors
   * @exception ResourceNotFoundException if an error occurs
   */
  public PwProjectImpl( String name, boolean isInDb) 
    throws ResourceNotFoundException {
    this.name = name;
    
    id = MySQLDB.getProjectIdByName(name);
    if(id == null) {
      throw new ResourceNotFoundException("Project " + name + " not found in database.");
    }
    planningSequences = new ArrayList();
    Map sequences = MySQLDB.getSequences(id);
    Iterator seqIdIterator = sequences.keySet().iterator();
    while(seqIdIterator.hasNext()) {
      Long sequenceId = (Long) seqIdIterator.next();
      String seqUrl = (String) sequences.get(sequenceId);
      System.err.println(sequenceId + " : " + seqUrl);
      planningSequences.add( new PwPlanningSequenceImpl( seqUrl, sequenceId));
    }
  } // end  constructor PwProjectImpl.openProject

  /**
   * <code>getId</code> - get the project's Id.
   *
   * @return - <code>Integer</code> - the Id.
   */

  public Integer getId() {
    return id;
  }
  
  protected void setId(Integer id) {
    this.id = id;
  }

  /**
   * <code>getName</code> - project name 
   *
   * @return - <code>String</code> - 
   */
  public String getName() {
    return name;
  } // end getName

  /**
   * <code>listPlanningSequences</code>
   *
   * @return - <code>List</code> -  List of Strings (urls of sequences)
   *                                each sequence is set of partial plans
   */
  public List listPlanningSequences() {
    ArrayList retval = new ArrayList();
    ListIterator sequenceIterator = planningSequences.listIterator();
    while(sequenceIterator.hasNext()) {
      retval.add(((PwPlanningSequenceImpl)sequenceIterator.next()).getUrl());
    }
    return retval;
  } // end listPlanningSequences

  /**
   * <code>getPlanningSequence</code>
   *
   * @param url - <code>String</code> - 
   * @return - <code>PwPlanningSequence</code> - 
   */
  public PwPlanningSequence getPlanningSequence( String url)
    throws ResourceNotFoundException {
    Iterator planningSeqIterator = planningSequences.iterator();
    while (planningSeqIterator.hasNext()) {
      PwPlanningSequence pwPlanningSequence =
        (PwPlanningSequence) planningSeqIterator.next();
      if (pwPlanningSequence.getUrl().equals( url)) {
        return pwPlanningSequence;
      }
    }
    throw new ResourceNotFoundException( "getPlanningSequence could not find " + url);
  } // end getPlanningSequence

  public PwPlanningSequence getPlanningSequence(Long seqId) throws ResourceNotFoundException {
    Iterator planningSeqIterator = planningSequences.iterator();
    while(planningSeqIterator.hasNext()) {
      PwPlanningSequence pwPlanningSequence = (PwPlanningSequence) planningSeqIterator.next();
      if(pwPlanningSequence.getId().equals(seqId)) {
        return pwPlanningSequence;
      }
    }
    throw new ResourceNotFoundException("getPlanning sequence could not find " + id);
  }

  public PwPlanningSequence addPlanningSequence(String url) 
    throws DuplicateNameException, ResourceNotFoundException {
    PwPlanningSequenceImpl retval = null;
    if(MySQLDB.sequenceExists(url)) {
      throw new DuplicateNameException("Sequence at " + url + " already in database.");
    }
    planningSequences.add(retval = new PwPlanningSequenceImpl( url, this));
    return retval;
  }

  public void deletePlanningSequence(String seqName) throws ResourceNotFoundException {
    ListIterator seqIterator = planningSequences.listIterator();
    while(seqIterator.hasNext()) {
      PwPlanningSequence planSeq = (PwPlanningSequence) seqIterator.next();
      if(planSeq.getUrl().equals(seqName)) {
        seqIterator.remove();
        return;
      }
    }
    throw new ResourceNotFoundException("Sequence " + seqName + " not in projet.");
  }

  public void deletePlanningSequence(Long seqId) throws ResourceNotFoundException {
    ListIterator seqIterator = planningSequences.listIterator();
    while(seqIterator.hasNext()) {
      PwPlanningSequence planSeq = (PwPlanningSequence) seqIterator.next();
      if(planSeq.getId().equals(seqId)) {
        seqIterator.remove();
        return;
      }
    }
    throw new ResourceNotFoundException("Sequence " + seqId + " not in project.");
  }
  /**
   * <code>delete</code> - remove this project from list of projects and database
   *
   * @exception Exception if an error occurs
   */
  public void delete() throws Exception, ResourceNotFoundException {
    long t1 = System.currentTimeMillis();
    projects.remove(name);
    MySQLDB.deleteProject(id);
    System.err.println("Deleting project took " + (System.currentTimeMillis() - t1) + "ms");
  } // end delete


} // end class PwProjectImpl
