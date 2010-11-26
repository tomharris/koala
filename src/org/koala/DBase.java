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

  /*needs to acomplish several things:
   *  1: decrement all the items from total inventory
   *  2: store transaction totals
   *  3: store the transaction items
   *  4: debit customer account (if applicable)
   */
  public void doTransaction(Transaction TransAction) {
    try {
      DatabaseConnection.getInstance().getConnection().setAutoCommit(false); //enables sql transaction

      decrementInventory(TransAction);
      int transactionNumber = storeTransactionTotals(TransAction);
      storeTransaction(transactionNumber, TransAction);
      debitAccount(TransAction);

      DatabaseConnection.getInstance().getConnection().commit();
      DatabaseConnection.getInstance().getConnection().setAutoCommit(true);
    }
    catch (Exception e) { //roll back for any reason
      try {
        DatabaseConnection.getInstance().getConnection().rollback();
      }
      catch(SQLException ex) {
        logger.error("Rollback failed.", ex);
      }
      logger.error("Exception recording transaction: Transaction is being rolled back", e);
    }
  }

  //decrement the inventory if applicable
  private void decrementInventory(Transaction transaction) throws SQLException {
    //check to see if the transaction is special
    if(Item.isSpecial(transaction.getFirstItem().getSku()))
      return;

    ArrayList<Item> items = transaction.getAllItems();
      PreparedStatement stmt;
      String decQuery = "update inventory set quantity=quantity-? where sku=?";

      try {
        stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(decQuery);

        for(Item item : items) {
            if(item.getUnlimited() || Item.isSpecial(item.getSku()) )
                continue; //skip if its unlimited or special

            stmt.setInt(1, item.getQuantity());
            stmt.setString(2, item.getSku());
            stmt.executeUpdate();
        }

        stmt.close();
      }
      catch (SQLException e) {
        logger.error("SQL error decrementing item count in inventory", e);
        throw e;
      }
  }

  //store the totals for the transaction and not the individual item ammounts
  // the individual ammounts are stored in another table however
  private int storeTransactionTotals(Transaction TransAction) throws SQLException {
    PreparedStatement stmt;
    int autoIncKey = -1;
    StringBuilder query = new StringBuilder();
    query.append("insert into transactions (transaction_time, code, user_id, customer_id, subtotal, tax) ");
    query.append("VALUES(");
    query.append(DatabaseConnection.getInstance().getProfile().getCurrentTimeCmd());
    query.append(", ?, ?, ?, ?, ?)");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());

      stmt.setString(1, TransAction.getAcctCode());
      stmt.setInt(2, TransAction.getCashier().getId());
      if(TransAction.getCustomer() == null || TransAction.getCustomer() instanceof CashCustomer) {
        stmt.setNull(3, java.sql.Types.INTEGER);
      }
      else {
        stmt.setInt(3, TransAction.getCustomer().getId());
      }
      stmt.setBigDecimal(4, TransAction.getSubTotal().getAmount());
      stmt.setBigDecimal(5, TransAction.getTax().getAmount());
      stmt.executeUpdate();

      //get the auto inc field of the row just inserted
      autoIncKey = getAutoIncKey("transactions", "id");

      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error recording transaction totals", e);
      throw e;
    }

    return autoIncKey;
  }

  /*
   * Each recept is inserted into the transaction_items table.
   * Every item will be a row in the table. Rows will be
   * of the form [transaction_id, sku, quantity, price].
   */
  private void storeTransaction(int transID, Transaction TransAction) throws SQLException {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into transaction_items (transaction_id, sku, quantity, price) ");
    query.append("VALUES(?, ?, ?, ?)");

    ArrayList<Item> items = TransAction.getAllItems();

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      for(Item item : items) {
            stmt.setInt(1, transID);
            stmt.setString(2, item.getSku());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getPrice().getAmount());
            stmt.executeUpdate();
        }

        stmt.close();
      }
      catch (SQLException e) {
        logger.error("SQL error recording transaction items", e);
        throw e;
      }
  }

  //debit the transaction total from a customer's account
  private void debitAccount(Transaction transaction) throws SQLException {
    //check for cash transaction
    if(transaction.getCustomer() == null || transaction.getCustomer() instanceof CashCustomer) {
      return;
    }

    //check to see if the transaction is special;
    // its not that allowing these is 'bad', but rather redundent
    //
    //We use first item because the last may be a correction due
    // to an over spending (which would be special). We would
    // still need to debit the account in that case.
    if(Item.isSpecial(transaction.getFirstItem().getSku()))
      return;

    PreparedStatement stmt;
    String query = "update customers set balance=? where id=?";

    try {
        stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
        stmt.setBigDecimal(1, transaction.getCustomer().getBalance().minus(transaction.getTotal()).getAmount());
        stmt.setInt(2, transaction.getCustomer().getId());
        stmt.executeUpdate();
        stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error debiting cutomer account", e);
      throw e;
    }
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

  private int getAutoIncKey(String tableName, String autoIncField) {
    Statement stmt;
    ResultSet rs;
    int autoIncKey = -1;

    try {
      stmt = DatabaseConnection.getInstance().getConnection().createStatement();
      rs = stmt.executeQuery(DatabaseConnection.getInstance().getProfile().getAutoIncCmd(tableName, autoIncField));
      if (rs.next())
        autoIncKey = rs.getInt(1);
      else
        throw new SQLException("Getting auto inc key failed");

      rs.close();
      stmt.close();

      if(autoIncKey == -1)
        throw new SQLException("Auto increment key is -1!");
    }
    catch (SQLException e) {
      logger.error("SQL getting auto increment key", e);
      //System.exit(0);
    }

    return autoIncKey;
  }
}
