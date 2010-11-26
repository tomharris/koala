package org.koala.model;
/*
 * Created on Apr 17, 2005
 */

/**
 * @author tom
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.koala.DatabaseConnection;
import org.koala.Money;

public class Item extends Base {
  protected String sku;
  protected String invName;
  protected Money price;
  protected Money taxRate;
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

  public Item() {
  }

  public Item(String sku, String name, int quantity, Money price, Money taxRate, boolean unlimited) {
    //purhaps some checking on the sku format?
    this.sku = sku;
    this.invName = name;

    this.quantity = quantity;
    this.price = price;
    this.taxRate = taxRate;
    this.unlimited = unlimited;
  }

  public static final Item createSpecialItem(String sku, Customer customer, Transaction transaction) {
    Item specialItem = null;

    if(sku.equals(Item.CASHOUT_SKU) ||
      sku.equals(Item.NEW_ACCOUNT_SKU) ||
      sku.equals(Item.COMP_ACCOUNT_SKU))
      specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, customer.getBalance(), Money.ZERO, false);
    else if(sku.equals(Item.PARTIALCASH_CREDITHALF))
      specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, transaction.getTotal().minus(customer.getBalance()).negate(), Money.ZERO, false);
    else if(sku.equals(Item.PARTIALCASH_CASHHALF))
      specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, transaction.getTotal().minus(customer.getBalance()), Money.ZERO, false);

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

  public static Item findBySku(String sku) {
    PreparedStatement stmt;
    ResultSet rs;
    String query = "select rentable, name, quantity, price, tax, unlimited from inventory where sku=?";

    String specialName = Item.getSpecialItemName(sku);
    if(specialName != null) {
      return new Item(sku, specialName, 0, Money.ZERO, Money.ZERO, false);
    }

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
        taxRate = new Money(rs.getBigDecimal("tax"));
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
        Money price = new Money(rs.getBigDecimal("price"));
        Money taxRate = new Money(rs.getBigDecimal("tax"));
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
    query.append("tax=?, unlimited=?, rentable=? where id=?");

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
}
