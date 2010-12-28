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

public class TransactionItem extends Base {
  protected String sku;
  protected String name;
  protected Money price;
  protected Money taxRate;
  protected int quantity;

  private static HashMap<String, String> specialItemSkus = null;
  private static Logger logger = Logger.getLogger(TransactionItem.class);

  //sku codes
  public static final String NEW_ACCOUNT_SKU = "newaccout";
  public static final String CASHOUT_SKU = "cashout";
  public static final String PARTIALCASH_CREDITHALF_SKU = "partialcredithalf";
  public static final String PARTIALCASH_CASHHALF_SKU = "partialcashhalf";

  public TransactionItem() {
    super();
    this.sku = "";
    this.name = "";
    this.price = Money.ZERO;
    this.taxRate = Money.ZERO;
    this.quantity = 1;
  }

  public TransactionItem(InventoryItem item, int quantity) {
    super();
    this.sku = item.getSku();
    this.name = item.getName();
    this.price = item.getPrice();
    this.taxRate = item.getTaxRate();
    this.quantity = quantity;
  }

  public static final TransactionItem createSpecialItem(String sku, Customer customer, Transaction transaction) {
    TransactionItem specialItem = null;

    if(sku.equals(TransactionItem.CASHOUT_SKU) || sku.equals(TransactionItem.NEW_ACCOUNT_SKU)) {
      specialItem = new TransactionItem();
      specialItem.setSku(sku);
      specialItem.setName(TransactionItem.getSpecialItemName(sku));
      specialItem.setPrice(customer.getBalance());
    }
    else if(sku.equals(TransactionItem.PARTIALCASH_CREDITHALF_SKU)) {
      specialItem = new TransactionItem();
      specialItem.setSku(sku);
      specialItem.setName(TransactionItem.getSpecialItemName(sku));
      specialItem.setPrice(transaction.getTotal().minus(customer.getBalance()).negate());
    }
    else if(sku.equals(TransactionItem.PARTIALCASH_CASHHALF_SKU)) {
      specialItem = new TransactionItem();
      specialItem.setSku(sku);
      specialItem.setName(TransactionItem.getSpecialItemName(sku));
      specialItem.setPrice(transaction.getTotal().minus(customer.getBalance()));
    }

    return specialItem;
  }

  public static final String getSpecialItemName(String sku) {
    if(specialItemSkus == null) {
      specialItemSkus = new HashMap<String, String>(5);
      specialItemSkus.put(TransactionItem.CASHOUT_SKU, "Customer Cashout");
      specialItemSkus.put(TransactionItem.NEW_ACCOUNT_SKU, "New Customer Account");
      specialItemSkus.put(TransactionItem.PARTIALCASH_CREDITHALF_SKU, "Credit Adjustment for Partial Cash");
      specialItemSkus.put(TransactionItem.PARTIALCASH_CASHHALF_SKU, "Cash Adjustment for Partial Cash");
    }

    return specialItemSkus.get(sku);
  }

  public static boolean isSpecial(String sku) {
    return TransactionItem.getSpecialItemName(sku) != null;
  }

  public String toString() {
    return name;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}
