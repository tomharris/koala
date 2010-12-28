package org.koala.model;
/*
 * Created on Dec 22, 2009
 */

/**
 * @author tom
 *
 */

import java.sql.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import org.koala.DatabaseConnection;
import org.koala.exception.EntryAlreadyExistsException;

abstract public class Base {
  protected int id; //non-neg if valid
  private boolean doTransaction = true;

  private static Logger logger = Logger.getLogger(Base.class);

  public Base() {
    this.id = 0;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean shouldDoTransaction() {
    return this.doTransaction;
  }

  public void setDoTransaction(boolean doTransaction) {
    this.doTransaction = doTransaction;
  }

  public String toString() {
    return this.getClass().getSimpleName() + "(#" + this.id + ")";
  }

  public boolean isNewRecord() {
    return this.getId() <= 0;
  }

  protected String getTableName() {
    String class_name = this.getClass().getSimpleName();
    class_name = underscore(class_name);
    class_name = pluralize(class_name);
    return class_name;
  }

  protected void create() throws EntryAlreadyExistsException {
  }

  protected void update() {
  }

  public void save() throws EntryAlreadyExistsException {
    if(this.isNewRecord()) {
      this.create();
    }
    else {
      this.update();
    }
  }

  public static void clearDB() {
    ArrayList<String> tables = new ArrayList<String>(6);
    tables.add("transaction_items");
    tables.add("transactions");
    tables.add("customers");
    tables.add("users");
    tables.add("inventory");

    clearTables(tables);
  }

  public static void resetDB() {
    ArrayList<String> tables = new ArrayList<String>(4);
    tables.add("transaction_items");
    tables.add("transactions");
    tables.add("customers");

    clearTables(tables);
  }

  private static void clearTables(ArrayList<String> tables) {
    Statement stmt;

    try {
      DatabaseConnection.getInstance().getConnection().setAutoCommit(false);

      stmt = DatabaseConnection.getInstance().getConnection().createStatement();
      for(String table : tables) {
        stmt.executeUpdate("truncate table " + table);
      }
      stmt.close();

      DatabaseConnection.getInstance().getConnection().commit();
      DatabaseConnection.getInstance().getConnection().setAutoCommit(true);
    }
    catch (SQLException e) {
      try {
        DatabaseConnection.getInstance().getConnection().rollback();
      }
      catch(SQLException ex) {
        logger.error("Rollback failed.", ex);
      }
      logger.error("SQL error clearing tables: Transaction is being rolled back", e);
    }
  }

  protected static int getAutoIncKey(Base model) {
    Statement stmt;
    ResultSet rs;
    int autoIncKey = -1;

    try {
      stmt = DatabaseConnection.getInstance().getConnection().createStatement();
      rs = stmt.executeQuery(DatabaseConnection.getInstance().getProfile().getAutoIncCmd(model.getTableName(), "id"));
      if (rs.next()) {
        autoIncKey = rs.getInt(1);
      }
      else {
        throw new SQLException("Getting auto inc key failed");
      }

      rs.close();
      stmt.close();

      if(autoIncKey == -1) {
        throw new SQLException("Auto increment key is -1!");
      }
    }
    catch (SQLException e) {
      logger.error("SQL getting auto increment key", e);
    }

    return autoIncKey;
  }

  protected static String underscore(String text) {
    return text.replaceAll("([A-Z])", "_$1").replaceFirst("^_", "").toLowerCase(); //tablize the name
  }

  protected static String pluralize(String text) {
    if (text.matches(".*y$")) {
      text = text.replaceFirst("y$", "ies");
    }
    else if (text.matches(".*ox$")) {
      text = text.concat("es");
    }
    else {
      text = text.concat("s");
    }
    return text;
  }
}
