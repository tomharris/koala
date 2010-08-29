package org.koala.model;
/*
 * Created on Apr 17, 2005
 */

/**
 * @author tom
 */

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.koala.DatabaseConnection;

public class Item extends Base {
  protected String sku;
  protected String invName;
  protected BigDecimal price, taxRate;
  protected int quantity;
  protected boolean unlimited;

  private static HashMap<String, String> specialItemSkus = null;

  private static Logger logger = Logger.getLogger(User.class);

  //sku codes
  public static final String NEW_ACCOUNT_SKU = "newaccout";
  public static final String COMP_ACCOUNT_SKU = "compaccount";
  public static final String CASHOUT_SKU = "cashout";
  public static final String PARTIALCASH_CREDITHALF = "partialcredithalf";
  public static final String PARTIALCASH_CASHHALF = "partialcashhalf";
  public static final String INVENTORYADD_SKU = "inventoryadd";
  public static final String INVENTORYCORRECTION_SKU = "inventorycorrection";

  public Item(String Sku, String Name, int Quantity, BigDecimal Price, BigDecimal TaxRate, boolean Unlimited) {
    //purhaps some checking on the sku format?
    this.sku = Sku;
    this.invName = Name;

    this.quantity = Quantity;
    this.price = Price;
    this.price.setScale(2, BigDecimal.ROUND_CEILING);
    this.taxRate = TaxRate;
    this.taxRate.setScale(2, BigDecimal.ROUND_CEILING);
    this.unlimited = Unlimited;
  }

  public static final Item createSpecialItem(String sku, Customer customer, Transaction transaction) {
    Item specialItem = null;

    if(sku.equals(Item.CASHOUT_SKU) ||
      sku.equals(Item.NEW_ACCOUNT_SKU) ||
      sku.equals(Item.COMP_ACCOUNT_SKU))
      specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, customer.getBalance(), BigDecimal.ZERO, false);
    else if(sku.equals(Item.PARTIALCASH_CREDITHALF))
      specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, transaction.getTotal().subtract(customer.getBalance()).negate(), BigDecimal.ZERO, false);
    else if(sku.equals(Item.PARTIALCASH_CASHHALF))
      specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, transaction.getTotal().subtract(customer.getBalance()), BigDecimal.ZERO, false);

    //let the nullexception float back up
    return specialItem;
  }

  public static final String getSpecialItemName(String sku) {
    if(specialItemSkus == null) {
      specialItemSkus = new HashMap<String, String>(7);
      specialItemSkus.put(Item.CASHOUT_SKU, "Customer Cashout");
      specialItemSkus.put(Item.NEW_ACCOUNT_SKU, "New Customer Account");
      specialItemSkus.put(Item.COMP_ACCOUNT_SKU, "New Comp Customer Account");
      specialItemSkus.put(Item.PARTIALCASH_CREDITHALF, "Credit Adjustment for Partial Cash");
      specialItemSkus.put(Item.PARTIALCASH_CASHHALF, "Cash Adjustment for Partial Cash");
      specialItemSkus.put(Item.INVENTORYADD_SKU, "Add Items to Inventory");
      specialItemSkus.put(Item.INVENTORYCORRECTION_SKU, "Corrected Inventory Count");
    }

    return specialItemSkus.get(sku);
  }

  public static boolean isSpecial(String sku) {
    return Item.getSpecialItemName(sku) != null;
  }

  public boolean isSpecial() {
    return Item.getSpecialItemName(this.sku) != null;
  }

  public String toString() {
    return invName;
  }

  public String getSku() {
    return sku;
  }

  public String getName() {
    return invName;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int amount) {
    quantity = amount;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public BigDecimal getTotal() {
    return price.multiply(new BigDecimal(quantity));
  }

  public BigDecimal getTaxRate() {
    return taxRate;
  }

  public boolean getUnlimited() {
    return unlimited;
  }

  public static Item findBySku(String sku) {
    PreparedStatement stmt;
    ResultSet rs;
    String query = "select rentable, name, quantity, price, tax, unlimited from inventory where sku=?";

    String specialName = Item.getSpecialItemName(sku);
    if(specialName != null) {
      return new Item(sku, specialName, 0, BigDecimal.ZERO, BigDecimal.ZERO, false);
    }

    boolean rentable = false, unlimited = false;
    String name = null;
    int quantity = 0;
    BigDecimal price = null;
    BigDecimal taxRate = null;

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setString(1, sku);
      rs = stmt.executeQuery();

      if(rs.next()) {
        rentable = (rs.getInt("rentable") == 1);
        name = rs.getString("name");
        quantity = rs.getInt("quantity");
        price = rs.getBigDecimal("price");
        taxRate = rs.getBigDecimal("tax");
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
      return new ForRent(sku, name, price);
    }

    return new ForSale(sku, name, quantity, price, taxRate, unlimited);
  }

  public static ArrayList<Item> findAll() {
    PreparedStatement stmt;
    ResultSet rs;
    String query = "select sku, rentable, name, quantity, price, tax, unlimited from inventory";

    ArrayList<Item> items = new ArrayList<Item>();

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      rs = stmt.executeQuery();

      while(rs.next()) {
        String sku = rs.getString("sku");
        boolean rentable = (rs.getInt("rentable") == 1);
        String name = rs.getString("name");
        int quantity = rs.getInt("quantity");
        BigDecimal price = rs.getBigDecimal("price");
        BigDecimal taxRate = rs.getBigDecimal("tax");
        boolean unlimited = (rs.getInt("unlimited") == 1);

        if(rentable) {
          items.add(new ForRent(sku, name, price));
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
    query.append("insert into inventory (name, quantity, price, tax, unlimited, rentable, sku) ");
    query.append("VALUES(?, ?, ?, ?, ?, ?, ?)");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setString(1, this.getName());
      stmt.setInt(2, this.getQuantity());
      stmt.setBigDecimal(3, this.getPrice());
      stmt.setBigDecimal(4, this.getTaxRate());
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
    query.append("tax=?, unlimited=?, rentable=? where id=?");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setString(1, this.getName());
      stmt.setInt(2, this.getQuantity());
      stmt.setBigDecimal(3, this.getPrice());
      stmt.setBigDecimal(4, this.getTaxRate());
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
}
