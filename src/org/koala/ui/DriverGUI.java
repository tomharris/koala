package org.koala.ui;
/*
 * selectMenu.java
 *
 * Created on April 16, 2005, 12:34 AM
 */

/**
 *
 * @author fergus
 */

import java.io.*;
import java.util.Stack;
import java.util.Properties;
import org.apache.log4j.*;
import org.koala.model.Config;
import org.koala.model.Customer;
import org.koala.model.User;

public class DriverGUI extends javax.swing.JFrame {
  private static Stack<String> previousGui = null;
  private static Logger logger = Logger.getLogger(DriverGUI.class);

  private static DriverGUI currentGui = null;
  private static DriverGUI modelGui = null;

  protected static final String APP_VERSION = "v1.2.0.rc1";
  protected static final String APP_NAME = "Koala PoS! " + APP_VERSION;

  protected static final java.awt.Font LABEL_HEADER_FONT = new java.awt.Font("Dialog", 1, 30);
  protected static final java.awt.Font LABEL_TEXT_FONT = new java.awt.Font("Dialog", 1, 18);
  protected static final java.awt.Font BUTTON_TEXT_FONT = new java.awt.Font("Dialog", 1, 14);
  protected static final java.awt.Dimension TEXTAREA_SIZE = new java.awt.Dimension(100, 25);

  protected static User currentUser;
  protected static Customer currentCustomer;

  public static final long serialVersionUID = 20110609;

  public static void main(String[] args) {

    //load config file
    DriverGUI.initConfig();

    //load logging
    if(args.length > 1 && args[0].equals("-l")) {
      DriverGUI.initLogging(args[1].replaceAll("\\.+/", "")); //not bullet-proof but it's good enough
    }
    else {
      DriverGUI.initLogging(null);
    }
    logger.info("Application Started.");

    //load gui
    DriverGUI.nextGui(new LoginScreenGUI());
  }

  public DriverGUI() {
    super();
    try {
      javax.swing.UIManager.setLookAndFeel(
        javax.swing.UIManager.getSystemLookAndFeelClassName()
      );
    }
    catch (javax.swing.UnsupportedLookAndFeelException e) {
      DriverGUI.printError(e);
    }
    catch (Exception e) {
      DriverGUI.printError(e);
    }
  }

  public void cleanup() {
    currentUser = null;
    currentCustomer = null;
  }

  public final void exitPoS() {
    this.cleanup();
    logger.info("Application Ended.");
    this.dispose();
    System.exit(0);
  }

  public static void printError(Throwable t) {
    logger.error("Generic error message", t);
  }

  private static void initLogging(String filename) {
    Properties logProperties = new Properties();

    try
    {
      File logConfig = new File("log4j.properties");
      if(logConfig.exists()) {
        logProperties.load(new FileInputStream("log4j.properties"));
      }
      else {
        logProperties.load(DriverGUI.class.getClassLoader().getResourceAsStream("log4j.properties"));
      }

      logConfig = null;

      //log to specific file, if name is provided
      if(filename != null) {
        logProperties.setProperty("log4j.appender.logfile.File", filename);
      }

      PropertyConfigurator.configure(logProperties);
    }
    catch(IOException e)
    {
      throw new RuntimeException("Unable to load logging properties");
    }
  }

  private static void initConfig() {
    try {
      File configFile = new File("pos.conf");
      if(configFile.exists()) {
        Config.loadConfig(new FileInputStream("pos.conf"));
      }
      else {
        Config.loadConfig(DriverGUI.class.getClassLoader().getResourceAsStream("pos.conf"));
      }
    }
    catch (FileNotFoundException e) {
      DriverGUI.printError(e);
      System.exit(1);
    }
  }

/**********************************************************
 * generic show/exit model gui function
 **********************************************************/

  public static final void showModelGui(DriverGUI targetGui) {
    if(modelGui != null) {
      return;
    }

    targetGui.setLocationRelativeTo(null);
    targetGui.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    targetGui.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent we) {
        if(DriverGUI.modelGui != null) {
          DriverGUI.modelGui.requestFocus();
        }
      }
    });

    targetGui.addWindowFocusListener(new java.awt.event.WindowFocusListener() {
      public void windowLostFocus(java.awt.event.WindowEvent evt) {
        if(DriverGUI.modelGui != null) {
          DriverGUI.modelGui.requestFocus();
        }
      }
      public void windowGainedFocus(java.awt.event.WindowEvent evt) {
          return;
      }
    });
    modelGui = targetGui;
    targetGui = null;
    modelGui.setVisible(true);
  }

  public static final void closeModelGui() {
    if(modelGui != null) {
      modelGui.dispose();
      modelGui = null;
    }

    currentGui.requestFocus();
  }

/**********************************************************
 * generic show/exit nonmodel gui function
 **********************************************************/
  //let the window opening this keep track of it, as they can have as many as they want
  public static final DriverGUI showNonModelGui(DriverGUI targetGui) {
    targetGui.setLocationRelativeTo(null);
    targetGui.setVisible(true);
    targetGui.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    return targetGui;
  }

  //user needs to remember to focus the next window they want
  public static final void closeNonModelGui(DriverGUI targetGui) {
    if(targetGui != null) {
      targetGui.dispose();
      targetGui = null;
    }
  }

/**********************************************************
 * generic stack based switch gui functions
 **********************************************************/
  public static final void nextGui(DriverGUI newGui) {
    if(previousGui == null) {
      previousGui = new Stack<String>();
    }

    if(currentGui != null) { //push the name of the class
      previousGui.push(currentGui.getClass().getName());
      currentGui.dispose();
      currentGui = null;
    }

    newGui.setLocationRelativeTo(null);
    newGui.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    currentGui = newGui;
    newGui = null;
    currentGui.setVisible(true);
  }

  public static final void backGui() {
    if(previousGui == null || previousGui.size() == 0 || currentGui == null) {
      return;
    }

    currentGui.dispose();
    currentGui = null;
    try {
      currentGui = (DriverGUI)Class.forName(previousGui.pop()).newInstance();
    }
    catch(IllegalAccessException e) {
      DriverGUI.printError(e);
    }
    catch (ClassNotFoundException e) {
      DriverGUI.printError(e);
    }
    catch (InstantiationException e) {
      DriverGUI.printError(e);
    }

    currentGui.setLocationRelativeTo(null);
    currentGui.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    currentGui.setVisible(true);
  }
}