package org.koala.model;
/*
 * Created on Jun 18, 2005
 *
 */

/**
 * @author tom
 *
 */

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;

import org.koala.ui.DriverGUI;

abstract public class Report {
  protected StringBuilder report;
  private final String PRINTDEVICE = Config.getConfig().getValue("print_device");

  protected static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

  protected static final String SUNDAY = "Sun";
  protected static final String MONDAY = "Mon";
  protected static final String TUESDAY = "Tue";
  protected static final String WEDNESDAY = "Wed";
  protected static final String THURSDAY = "Thu";
  protected static final String FRIDAY = "Fri";
  protected static final String SATURDAY = "Sat";

  public Report() {
    //put report template in string
    this.report = new StringBuilder();
  }

  protected void finalize() {
  	this.report = null;
  }

  abstract protected void loadResources();

  abstract public void doReport();

  public boolean isPrintable() {
  	return false;
  }

  //report should be done by now
  public final String toString() {
    return this.report.toString();
  }

  public final void print() {
    try
    {
        //open printer as if it were a file
        FileOutputStream os = new FileOutputStream(PRINTDEVICE);
        //wrap stream in "friendly" PrintStream
        PrintStream ps = new PrintStream(os);

        //print report here
        ps.println(this.report.toString());
        //print a form feed
        ps.print("\f");

        //flush buffer and close
        ps.close();
    }
    catch (Exception e)
    {
    	DriverGUI.printError(e);
    }
  }
}
