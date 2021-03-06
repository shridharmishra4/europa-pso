package org.ops.ui.nddl;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;

import nddl.ModelAccessor;
import nddl.NddlParser;
import nddl.NddlParserState;
import nddl.NddlTreeParser;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

import org.ops.ui.PSDesktop;
import org.ops.ui.ash.AshConsole;
import org.ops.ui.ash.AshInterpreter;
import org.ops.ui.ash.DocumentOutputStream;

import psengine.PSException;

public class NddlInterpreter extends AshInterpreter {
  NddlParserState persistantState = null;
	PrintStream consoleErr = null;

  public NddlInterpreter() {
    super("Nddl");
    ModelAccessor.init();
    persistantState = new NddlParserState(null);
  }

  public void setConsole(AshConsole console) {
    consoleErr= new PrintStream(new BufferedOutputStream(new DocumentOutputStream(console.getDocument(), "!!")), true);
    persistantState.setErrStream(consoleErr);
  }

  private boolean execute(NddlParser parser) {
    IXMLElement xml = new XMLElement("nddl");

    if(parser.getState().getErrorCount() == 0) {
      try {
        NddlTreeParser treeParser = new NddlTreeParser(parser.getState());
        treeParser.nddl(parser.getAST(),xml);
      }
      catch(Exception ex) {
        return false;
      }
    }
    else
      return false;

    StringWriter string = new StringWriter();
    try {
      new XMLWriter(new BufferedWriter(string)).write(xml);
    }
    catch(IOException ex) {
      return false;
    }

		try {
    	PSDesktop.desktop.getPSEngine().executeTxns(string.toString(), false, true);
    	persistantState = parser.getState();
		}
		catch(PSException ex) {
			consoleErr.print(ex.getFile());
			consoleErr.print(":");
			consoleErr.print(ex.getLine());
			consoleErr.print(": error: ");
			consoleErr.println(ex.getMessage());
			return false;
		}
    return true;
  }

  public void source(String filename) {
    try {
      File modelFile =  ModelAccessor.generateIncludeFileName("",filename);
      NddlParser parser = NddlParser.parse(persistantState, modelFile, null);
      execute(parser);
    }
    catch(Exception ex) {
			consoleErr.print("error: ");
			consoleErr.println(ex.getMessage());
    }
  }

  // returns true if parse didn't encounter a premature end of string.
  public boolean eval(String toEval) {
    try {
      NddlParser parser = NddlParser.eval(persistantState, new StringReader(toEval));
      execute(parser);
    }
    catch(EOFException ex) {
      return false;
    }
    catch(Exception ex) {
			consoleErr.print("error: ");
			ex.printStackTrace(consoleErr);
    }
    return true;
  }
}
