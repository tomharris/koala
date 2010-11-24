package org.koala.model;
/*
 * Created on Apr 18, 2005
 *
 * The transaction will be a vector of all Items.
 */

/**
 * @author tom
 *  SUMARY: A transaction can be started when the customerid is given. We need
 *    another gui screen to be presented after the cashier logs in but
 *    before the CashierScreenGUI (to get the customer's name). This screen
 *    will collect the customer's name and convert that to a ID. The
 *    CashierScreenGUI will then be launched to add items to the transaction.
 */

import java.util.ArrayList;
import org.koala.Money;
import java.sql.Date;

import org.koala.DBase;

public class Transaction extends Base {
  private User cashier;
  private Customer customer;
  private Money subTotal;
  private Money tax;
  private ArrayList<Item> transactions = null;
  private String code;
  private Date transactionTime;
  private static final int initSize = 10; //initial array size; 10 sounds good

  //transaction codes
  public static final String CODE_CASH = "a";
  public static final String CODE_CREDITACCOUNT = "b";
  public static final String CODE_DEBITACCOUNT = "c";
  public static final String CODE_CLOSEACCOUNT = "d";
  public static final String CODE_CREDITCOMPACCOUNT = "e";
  public static final String CODE_DEBITCOMPACCOUNT = "f";
  public static final String CODE_CLOSECOMPACCOUNT = "g";
  public static final String CODE_INVENTORYADD = "h";
  public static final String CODE_INVENTORYCORRECTION = "i";

  /*
   * The idea is that you shouldnt need to create a transaction unless you already
   * have a item to put in it.
   */
  // public Transaction(User cashier, Customer customer, Item newItem) {
  //  this.id = -1;
  //  this.subTotal = BigDecimal.ZERO;
  //     this.subTotal.setScale(2, BigDecimal.ROUND_CEILING);
  //     this.tax = BigDecimal.ZERO;
  //     this.tax.setScale(2, BigDecimal.ROUND_CEILING);
  //
  //     this.cashier = cashier;
  //     this.customer = customer;
  //     if(newItem == null) {
  //      this.transactions = null;
  //     }
  //     else {
  //      this.transactions = new ArrayList<Item>(initSize);
  //      addItem(newItem);
  //      if(newItem.getSku().equals(Item.NEW_ACCOUNT_SKU))
  //        this.acctCode = Transaction.CODE_CREDITACCOUNT;
  //      else if(newItem.getSku().equals(Item.COMP_ACCOUNT_SKU))
  //        this.acctCode = Transaction.CODE_CREDITCOMPACCOUNT;
  //      else if(newItem.getSku().equals(Item.CASHOUT_SKU))
  //        this.acctCode = Transaction.CODE_CLOSEACCOUNT;
  //      else if(newItem.getSku().equals(Item.INVENTORYADD_SKU))
  //        this.acctCode = Transaction.CODE_INVENTORYADD;
  //      else if(newItem.getSku().equals(Item.INVENTORYCORRECTION_SKU))
  //        this.acctCode = Transaction.CODE_INVENTORYCORRECTION;
  //      else if(customer != null && customer.isComplementary())
  //        this.acctCode = Transaction.CODE_DEBITCOMPACCOUNT;
  //      else if(customer != null && customer.getId() != 0)
  //        this.acctCode = Transaction.CODE_DEBITACCOUNT;
  //      else
  //        this.acctCode = Transaction.CODE_CASH;
  //     }
  // }

  public Transaction() {
    super();
  }

  protected void finalize() {
    this.transactions.clear();
    this.transactions = null;

    this.cashier = null;
    this.customer = null;
    this.subTotal = null;
    this.tax = null;
    this.code = null;
    this.transactionTime = null;
  }

  public void lookupTransactionItems(DBase dbHandle) {
    this.transactions = dbHandle.getTransactionItems(this.id);
    this.transactions.trimToSize();
  }

  public void addItem(Item newItem) {
    this.transactions.add(newItem);
    this.subTotal = this.subTotal.plus(newItem.getTotal());
    this.tax = this.tax.plus(newItem.getPrice().times(newItem.getTaxRate()));
  }

  public void removeItem(String sku) {
    for(Item item : this.transactions) {
      if(item.getSku().equals(sku)) {
        this.transactions.remove(item);
        break;
      }
    }

    this.subTotal = Money.ZERO; 
    this.tax = Money.ZERO;
    for(Item item : this.transactions) {
      this.subTotal = this.subTotal.plus(item.getTotal());
      this.tax = this.tax.plus(item.getPrice()).times(item.getTaxRate());
    }
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getTransactionTime() {
    return this.transactionTime;
  }

  public void setTransactionTime(Date transactionTime) {
    this.transactionTime = transactionTime;
  }

  public Money getSubTotal() {
    return this.subTotal;
  }

  public void setSubTotal(Money subTotal) {
    this.subTotal = subTotal;
  }

  public Money getTax() {
    return this.tax;
  }

  public void setTax(Money tax) {
    this.tax = tax;
  }

  //total rounded to the nearest penny
  public Money getTotal() {
    return this.subTotal.plus(this.tax);
  }

  public User getCashier() {
    return this.cashier;
  }

  public void setCashier(User cashier) {
    this.cashier = cashier;
  }

  public Customer getCustomer() {
    return this.customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Item getLastItem() {
    return this.transactions.get(this.transactions.size() - 1);
  }

  public Item getFirstItem() {
    return this.transactions.get(0);
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getAcctCode() {
    return this.getCode();
  }

  //please avoid as this is VERY expensive
  public ArrayList<Item> getAllItems() {
    return new ArrayList<Item>(this.transactions);
  }
}
