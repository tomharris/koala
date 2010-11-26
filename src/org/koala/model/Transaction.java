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

import org.apache.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.koala.DatabaseConnection;
import org.koala.Money;

public class Transaction extends Base {
  private static Logger logger = Logger.getLogger(Transaction.class);

  private User cashier;
  private Customer customer;
  private Money subTotal;
  private Money tax;
  private ArrayList<Item> items = null;
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
  //      this.items = null;
  //     }
  //     else {
  //      this.items = new ArrayList<Item>(initSize);
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
    this.items.clear();
    this.items = null;

    this.cashier = null;
    this.customer = null;
    this.subTotal = null;
    this.tax = null;
    this.code = null;
    this.transactionTime = null;
  }

  public void addItem(Item newItem) {
    this.items.add(newItem);
    this.subTotal = this.subTotal.plus(newItem.getTotal());
    this.tax = this.tax.plus(newItem.getPrice().times(newItem.getTaxRate()));
  }

  public void removeItem(String sku) {
    for(Item item : this.items) {
      if(item.getSku().equals(sku)) {
        this.items.remove(item);
        break;
      }
    }

    this.subTotal = Money.ZERO;
    this.tax = Money.ZERO;
    for(Item item : this.items) {
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
    return this.items.get(this.items.size() - 1);
  }

  public Item getFirstItem() {
    return this.items.get(0);
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

  public ArrayList<Item> getItems() {
    return new ArrayList<Item>(this.items);
  }

  public static ArrayList<Transaction> getAll(Customer customer) {
    StringBuilder query = new StringBuilder();
    int initialCapacity = 10; //initial array capacity; 10 is the default for arraylist

    query.append("select id, transaction_time, user_id, subtotal, tax, code from transactions ");
    //here are some good guesses for a default array size; on average, I think this is close
    if(customer == null) { //we are getting all transaction for everyone
      initialCapacity = 750;
    }
    else { //just the one customer
      query.append("where customer_id=? ");
      initialCapacity = 15;
    }
    query.append("order by transaction_time");
    ArrayList<Transaction> transactions = new ArrayList<Transaction>(initialCapacity);

    try {
      PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      if(customer != null) {
        stmt.setInt(1, customer.getId());
      }
      ResultSet rs = stmt.executeQuery();

      Transaction transaction = null;
      while(rs.next()) {
        transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setCashier(User.find(rs.getInt("user_id")));
        transaction.setCustomer(customer);
        transaction.setSubTotal(new Money(rs.getBigDecimal("subtotal")));
        transaction.setTax(new Money(rs.getBigDecimal("tax")));
        transaction.setCode(rs.getString("code"));
        transaction.setTransactionTime(rs.getDate("transaction_time"));

        transactions.add(transaction);
      }
      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error loading transactions for customer history", e);
    }

    transactions.trimToSize();
    return transactions;
  }

  public ArrayList<Item> getAllItems() {
    StringBuilder query = new StringBuilder();
    query.append("select transaction_items.sku, inventory.name, transaction_items.quantity, ");
    query.append("transaction_items.price, inventory.tax, inventory.unlimited ");
    query.append("from transaction_items ");
    query.append("left outer join inventory on inventory.sku=transaction_items.sku ");
    query.append("where transaction_id=?");
    ArrayList<Item> transItems = new ArrayList<Item>();

    try {
      PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setInt(1, this.id);
      ResultSet rs = stmt.executeQuery();

      while(rs.next()) {
        String name = rs.getString("name");
        if(name == null)
          name = rs.getString("sku"); //reasonable default I should think

        BigDecimal price = rs.getBigDecimal("price");
        if(price == null) {
          price = BigDecimal.ZERO;
        }

        BigDecimal tax = rs.getBigDecimal("tax");
        if(tax == null) {
          tax = BigDecimal.ZERO;
        }

        transItems.add(new ForSale(rs.getString("sku"), name,
          rs.getInt("quantity"),
          new Money(price),
          new Money(tax),
          rs.getInt("unlimited") == 1));
      }
      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error loading customer items", e);
    }

    transItems.trimToSize();
    return transItems;
  }
  
  //store the totals for the transaction and not the individual item ammounts
  // the individual ammounts are stored in another table however
  public void create(Transaction TransAction) throws SQLException {
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
      this.setId(Base.getAutoIncKey(this));

      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error recording transaction totals", e);
      throw e;
    }
  }
  
  /*needs to acomplish several things:
   *  1: decrement all the items from total inventory
   *  2: store transaction totals
   *  3: store the transaction items
   *  4: debit customer account (if applicable)
   */
  public void commit() {
    try {
      DatabaseConnection.getInstance().getConnection().setAutoCommit(false); //enables sql transaction

      Item.decrementInventory(this);
      this.save();
      commitItems();
      
      // debit the customer's account (reload account to be sure data is fresh)
      Customer customer = Customer.find(this.getCustomer().getId());
      customer.setBalance(customer.getBalance().minus(this.getTotal()));
      customer.update();

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
  
  /*
   * Each recept is inserted into the transaction_items table.
   * Every item will be a row in the table. Rows will be
   * of the form [transaction_id, sku, quantity, price].
   */
  private void commitItems() throws SQLException {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into transaction_items (transaction_id, sku, quantity, price) ");
    query.append("VALUES(?, ?, ?, ?)");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      for(Item item : this.items) {
        stmt.setInt(1, this.id);
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
}
