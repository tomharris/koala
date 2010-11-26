package org.koala;
/*
 * Created on Apr 14, 2005
 */

/**
 * @author tom
 *
 * Handles all the heavy lifting for the interaction of the system and the dbase
 *
 */

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.koala.model.*;
import org.koala.exception.EntryAlreadyExistsException;

public class DBase {
  private static Logger logger = Logger.getLogger(DBase.class);

  public DBase() {
  }

  public void finalize() {
    DatabaseConnection.getInstance().disconnect();
  }

  public void clearDB() {
    ArrayList<String> tables = new ArrayList<String>(6);
    tables.add("transaction_items");
    tables.add("transactions");
    tables.add("notes");
    tables.add("customers");
    tables.add("users");
    tables.add("inventory");

    this.dropTables(tables);
  }

  public void resetDB() {
    ArrayList<String> tables = new ArrayList<String>(4);
    tables.add("transaction_items");
    tables.add("transactions");
    tables.add("notes");
    tables.add("customers");

    this.dropTables(tables);
  }

  private void dropTables(ArrayList<String> tables) {
    Statement stmt;

    try {
      DatabaseConnection.getInstance().getConnection().setAutoCommit(false);

      stmt = DatabaseConnection.getInstance().getConnection().createStatement();
      for(String table : tables)
        stmt.executeUpdate("delete from " + table);
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
      logger.error("SQL error dropping tables: Transaction is being rolled back", e);
    }
  }
}
