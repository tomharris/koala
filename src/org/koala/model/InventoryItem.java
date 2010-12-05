package org.koala.model;
/*
 * Created on Nov 28, 2010
 */

/**
 * @author tom
 */

import java.sql.*;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.koala.DatabaseConnection;
import org.koala.Money;

public class InventoryItem extends Base {
  protected String sku;
  protected String name;
  protected Money price;
  protected Money taxRate;
  protected int quantity;
  protected boolean unlimited;

  private static Logger logger = Logger.getLogger(InventoryItem.class);

  public InventoryItem() {
    super();
    this.sku = "";
    this.name = "";
    this.price = Money.ZERO;
    this.taxRate = Money.ZERO;
    this.quantity = 0;
    this.unlimited = false;
  }

  public String toString() {
    return name;
  }

  public String getSku() {
    return sku;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Money getPrice() {
    return price;
  }

  public void setPrice(Money price) {
    this.price = price;
  }

  public Money getTotal() {
    return price.times(quantity);
  }

  public Money getTaxRate() {
    return taxRate;
  }

  public void setTaxRate(Money taxRate) {
    this.taxRate = taxRate;
  }

  public boolean getUnlimited() {
    return unlimited;
  }

  public void setUnlimited(boolean unlimited) {
    this.unlimited = unlimited;
  }

  public static InventoryItem findBySku(String sku) {
    PreparedStatement stmt;
    ResultSet rs;
    String query = "select rentable, name, quantity, price, tax, unlimited from inventory where sku=?";

    boolean rentable = false, unlimited = false;
    String name = null;
    int quantity = 0;
    Money price = null;
    Money taxRate = null;

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setString(1, sku);
      rs = stmt.executeQuery();

      if(rs.next()) {
        rentable = (rs.getInt("rentable") == 1);
        name = rs.getString("name");
        quantity = rs.getInt("quantity");
        price = new Money(rs.getBigDecimal("price"));
        taxRate = new Money(rs.getBigDecimal("tax_rate"));
        unlimited = (rs.getInt("unlimited") == 1);
      }
      else {
        return null;
      }

      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error looking up item", e);
    }

    if(rentable) {
      return new ForRent(sku, name, price, taxRate);
    }

    return new ForSale(sku, name, quantity, price, taxRate, unlimited);
  }

  public static ArrayList<InventoryItem> findAll() {
    PreparedStatement stmt;
    ResultSet rs;
    String query = "select sku, rentable, name, quantity, price, tax_rate, unlimited from inventory";

    ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      rs = stmt.executeQuery();

      while(rs.next()) {
        String sku = rs.getString("sku");
        boolean rentable = (rs.getInt("rentable") == 1);
        String name = rs.getString("name");
        int quantity = rs.getInt("quantity");
        Money price = new Money(rs.getBigDecimal("price"));
        Money taxRate = new Money(rs.getBigDecimal("tax_rate"));
        boolean unlimited = (rs.getInt("unlimited") == 1);

        if(rentable) {
          items.add(new ForRent(sku, name, price, taxRate));
        }
        else {
          items.add(new ForSale(sku, name, quantity, price, taxRate, unlimited));
        }
      }

      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error looking up item", e);
    }

    return items;
  }

  //added the Item object to the inventory
  public void create() {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into inventory (name, quantity, price, tax_rate, unlimited, rentable, sku) ");
    query.append("VALUES(?, ?, ?, ?, ?, ?, ?)");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setString(1, this.getName());
      stmt.setInt(2, this.getQuantity());
      stmt.setBigDecimal(3, this.getPrice().getAmount());
      stmt.setBigDecimal(4, this.getTaxRate().getAmount());
      stmt.setInt(5, (this.getUnlimited() ? 1 : 0));
      stmt.setInt(6, (this instanceof ForRent ? 1 : 0));
      stmt.setString(7, this.getSku());
      stmt.executeUpdate();
      stmt.close();

      this.setId(getAutoIncKey(this));
    }
    catch (SQLException e) {
      logger.error("SQL error adding inventory item", e);
    }
  }

  //Change the details of this item in inventory
  public void update() {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("update inventory set name=?, quantity=?, price=?, ");
    query.append("tax_rate=?, unlimited=?, rentable=? where id=?");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setString(1, this.getName());
      stmt.setInt(2, this.getQuantity());
      stmt.setBigDecimal(3, this.getPrice().getAmount());
      stmt.setBigDecimal(4, this.getTaxRate().getAmount());
      stmt.setInt(5, (this.getUnlimited() ? 1 : 0));
      stmt.setInt(6, (this instanceof ForRent ? 1 : 0));
      stmt.setInt(7, this.getId());
      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error modifying inventory item", e);
    }
  }

  //removes the item with this sku from inventory
  public void destroy() {
    PreparedStatement stmt;
    String query = "delete from inventory where id=?";

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setInt(1, this.getId());
      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error removing inventory item", e);
    }
  }

  //decrement the inventory if applicable
  public static void decrementInventory(Transaction transaction) throws SQLException {
    ArrayList<TransactionItem> items = transaction.getAllItems();
    PreparedStatement stmt;
    String decQuery = "update inventory set quantity=quantity-? where unlimited=0 and sku=?";

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(decQuery);

      for(TransactionItem item : items) {
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
}