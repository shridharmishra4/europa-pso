package org.ops.ui;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import bsh.Interpreter;
import bsh.util.JConsole;

import org.ops.ui.chart.PSJFreeResourceChart;
import org.ops.ui.chart.PSResourceChart;
import org.ops.ui.chart.PSResourceChartPSEModel;
import org.ops.ui.gantt.PSEGantt;
import org.ops.ui.gantt.PSGantt;
import org.ops.ui.gantt.PSGanttPSEModel;
import org.ops.ui.solver.PSSolverDialog;
import org.ops.ui.util.Util;
import org.ops.ui.mouse.ActionViolationsPanel;
import org.ops.ui.mouse.ActionDetailsPanel;

import org.josql.contrib.JoSQLSwingTableModel;

import psengine.*;

public class PSDesktop
{
	protected JDesktopPane desktop_;
	protected int windowCnt_=0;
	protected PSEngine psEngine_=null;
	
	protected static String bshFile_=null;
	
	public static void main(String[] args) 
	{
		if (args.length > 0)
			bshFile_ = args[0];
		
		PSDesktop desktop = new PSDesktop();
		desktop.runUI();
	}

    public void runUI()
    {
	    SwingUtilities.invokeLater(new UICreator());
    }   
    
    public JInternalFrame makeNewFrame(String name)
    {
        JInternalFrame frame = new JInternalFrame(name);
        frame.getContentPane().setLayout(new BorderLayout());
	    desktop_.add(frame);
	    int offset=windowCnt_*15;
	    windowCnt_++;
	    frame.setLocation(offset,offset);
	    frame.setSize(700,300);
	    frame.setResizable(true);
	    frame.setClosable(true);
	    frame.setMaximizable(true);
	    frame.setIconifiable(true);
        frame.setVisible(true);
        return frame;
    }
    
    private class UICreator
        implements Runnable
    {
	    public void run() 
	    {	
	    	createAndShowGUI(); 
	    }    	
    }

    private void createAndShowGUI() 
    {
    	try {
    		//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    		JFrame.setDefaultLookAndFeelDecorated(true);
    		
    		//Create and set up the window.
    		JFrame frame = new JFrame("Planning & Scheduling UI");
    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		frame.getContentPane().setLayout(new BorderLayout());    		
    		createDesktop();
    		frame.getContentPane().add(desktop_,BorderLayout.CENTER);

    		//Display the window.
    		frame.pack();
    		frame.setSize(1200,600);
    		frame.setVisible(true);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		System.exit(0);
    	}
    }    
    
    private void createDesktop()
        throws Exception
    {
    	desktop_ = new JDesktopPane();
    	
        // BeanShell scripting
        JConsole console = new JConsole();
        JInternalFrame consoleFrame = makeNewFrame("Console");
        consoleFrame.getContentPane().add(console);
        Interpreter interp = new Interpreter(console);
        new Thread(interp).start();   
        
        interp.set("desktop",this);        
        registerPSEngine(interp);
        
        if (bshFile_ != null)
        	interp.eval("source(\""+bshFile_+"\");");
        //consoleFrame.setIcon(true);
    }
    
    protected void registerPSEngine(Interpreter interp)
        throws Exception
    {
        interp.set("psengine",getPSEngine());    	
    }
    
    public void makeTableFrame(String title,List l,String fields[])
    {
    	JInternalFrame frame = this.makeNewFrame(title);
    	JTable table = Util.makeTable(l,fields);
    	JScrollPane scrollpane = new JScrollPane(table);
    	frame.getContentPane().add(scrollpane);
    }

    /*
     * Creates a table on the results of a JoSQL query
     */
    public void makeTableFrame(String title,List l,String josqlQry)
    {
    	try {
    		JInternalFrame frame = this.makeNewFrame(title);
    		
    		// TODO: JoSQLSwingTableModel doesn't preserve column names, it hsould be easy to add
    		JoSQLSwingTableModel model =  new JoSQLSwingTableModel();
    		model.parse(josqlQry);
    		model.execute(l);
    		
    		/*
    		Query qry = new Query();
    		qry.parse(josqlQry);
    		List data = qry.execute(l).getResults();
    		TableModel model = Util.makeTableModel(data, new String[]{"toString"});
    		*/
    		
    		JTable table = new JTable(model);
    		JScrollPane scrollpane = new JScrollPane(table);
    		frame.getContentPane().add(scrollpane);
    		frame.setSize(frame.getSize()); // Force repaint
    	}
    	catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
   
    public PSEngine getPSEngine()
    {
    	if (psEngine_ == null) {
            // TODO: postfix mut be a parameter
            System.loadLibrary("PSEngine_g");
            psEngine_ = new PSEngine();
            psEngine_.start();
    	}
    	
    	return psEngine_;
    }
    	
    
    public void makeSolverDialog(PSSolver solver)
    {
    	try {
    		JInternalFrame frame = makeNewFrame("Solver");
    		frame.getContentPane().setLayout(new BorderLayout());
    		frame.getContentPane().add(new JScrollPane(new PSSolverDialog(this,solver)));
    		frame.setSize(675,375);
    	}
    	catch (Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    }
    
    public void showTokens(PSObject o)
    {
        final String[] columns = {
                "Key",
                "Name",
                "Parameters",
         };
            
         makeTableFrame("Activities for "+o.getName(),Util.SWIGList(o.getTokens()),columns);        	
    }    
    
    public JInternalFrame makeResourceGanttFrame(
    		String objectsType,
	        Calendar start,
	        Calendar end)
    {
        PSGantt gantt = new PSEGantt(new PSGanttPSEModel(getPSEngine(),start,objectsType),start,end);

        JInternalFrame frame = makeNewFrame("Resource Schedule");
        frame.getContentPane().add(gantt);
		frame.setSize(frame.getSize()); // Force repaint
        
        return frame;    
    }    
    
    public PSResourceChart makeResourceChart(PSResource r,Calendar start)
    {
    	return new PSJFreeResourceChart(
    			r.getName(),
    			new PSResourceChartPSEModel(r),
    			start);
    }    
    
    public JInternalFrame makeResourcesFrame(String type,Calendar start)
    {
        JTabbedPane resourceTabs = new JTabbedPane();
        PSResourceList resources = getPSEngine().getResourcesByType(type); 
        for (int i = 0; i < resources.size(); i++) {
        	PSResource r = resources.get(i);
            resourceTabs.add(r.getName(),makeResourceChart(r,start));
        }
        
        JInternalFrame frame = makeNewFrame("Resources");
        frame.getContentPane().add(resourceTabs);
		frame.setSize(frame.getSize()); // Force repaint
        
        return frame;
    }    
    
    public JInternalFrame makeViolationsFrame()
    {
        ActionViolationsPanel vp = new ActionViolationsPanel(getPSEngine());
        JInternalFrame frame = makeNewFrame("Violations");
        frame.getContentPane().add(vp);
        frame.setLocation(500,180);
        frame.setSize(300,300); 
        
        return frame;       
    }

    public JInternalFrame makeDetailsFrame()
    {
        ActionDetailsPanel dp = new ActionDetailsPanel(getPSEngine());
        JInternalFrame frame = makeNewFrame("Details");
        frame.getContentPane().add(dp);
        frame.setLocation(800,180);
        frame.setSize(300,200);        
        
        return frame;
    }    
}

