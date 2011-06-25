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
  private Money taxTotal;
  private ArrayList<TransactionItem> items = null;
  private String code;
  private Date transactionTime;

  //transaction codes
  public static final String CODE_CASH = "a";
  public static final String CODE_CREDITACCOUNT = "b";
  public static final String CODE_DEBITACCOUNT = "c";
  public static final String CODE_CREATEACCOUNT = "d";
  public static final String CODE_CLOSEACCOUNT = "e";
  public static final String CODE_CREDITCOMPACCOUNT = "f";
  public static final String CODE_DEBITCOMPACCOUNT = "g";
  public static final String CODE_CREATECOMPACCOUNT = "h";
  public static final String CODE_CLOSECOMPACCOUNT = "i";

  public Transaction() {
    super();
    this.subTotal = Money.ZERO;
    this.taxTotal = Money.ZERO;
    this.items = new ArrayList<TransactionItem>();
  }

  protected void finalize() {
    this.items.clear();
    this.items = null;

    this.cashier = null;
    this.customer = null;
    this.subTotal = null;
    this.taxTotal = null;
    this.code = null;
    this.transactionTime = null;
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

  public Money getTaxTotal() {
    return this.taxTotal;
  }

  public void setTaxTotal(Money taxTotal) {
    this.taxTotal = taxTotal;
  }

  //total rounded to the nearest penny
  public Money getTotal() {
    return this.subTotal.plus(this.taxTotal);
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

  public TransactionItem getLastItem() {
    return this.items.get(this.items.size() - 1);
  }

  public TransactionItem getFirstItem() {
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

  public ArrayList<TransactionItem> getItems() {
    return new ArrayList<TransactionItem>(this.items);
  }

  public void addItem(TransactionItem newItem) {
    this.items.add(newItem);
    recalculateTotals();
  }

  public void removeItem(String sku) {
    for(TransactionItem item : this.items) {
      if(item.getSku().equals(sku)) {
        this.items.remove(item);
        break;
      }
    }

    recalculateTotals();
  }

  public static ArrayList<Transaction> getAll(Customer customer) {
    StringBuilder query = new StringBuilder();

    query.append("select id, transaction_time, user_id, subtotal, taxtotal, code from transactions ");
    if(customer != null) {
      query.append("where customer_id=? ");
    }

    query.append("order by transaction_time");
    ArrayList<Transaction> transactions = new ArrayList<Transaction>();

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
        transaction.setTaxTotal(new Money(rs.getBigDecimal("taxtotal")));
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

  public ArrayList<TransactionItem> getAllItems() {
    StringBuilder query = new StringBuilder();
    query.append("select sku, name, quantity, price, tax_rate ");
    query.append("from transaction_items ");
    query.append("where transaction_id=?");
    ArrayList<TransactionItem> items = new ArrayList<TransactionItem>();

    try {
      PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setInt(1, this.id);
      ResultSet rs = stmt.executeQuery();

      while(rs.next()) {
        BigDecimal price = rs.getBigDecimal("price");
        if(price == null) {
          price = BigDecimal.ZERO;
        }

        BigDecimal taxRate = rs.getBigDecimal("tax_rate");
        if(taxRate == null) {
          taxRate = BigDecimal.ZERO;
        }

        TransactionItem item = new TransactionItem();
        item.setSku(rs.getString("sku"));
        item.setName(rs.getString("name"));
        item.setPrice(new Money(price));
        item.setTaxRate(new Money(taxRate));
        items.add(item);
      }
      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error loading customer items", e);
    }

    items.trimToSize();
    return items;
  }

  //store the totals for the transaction and not the individual item ammounts
  // the individual ammounts are stored in another table however
  public void create() {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into transactions (transaction_time, code, user_id, customer_id, subtotal, taxtotal) ");
    query.append("VALUES(");
    query.append(DatabaseConnection.getInstance().getProfile().getCurrentTimeCmd());
    query.append(", ?, ?, ?, ?, ?)");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());

      stmt.setString(1, this.getAcctCode());
      if(this.getCashier() == null) {
        stmt.setNull(2, java.sql.Types.INTEGER);
      }
      else {
        stmt.setInt(2, this.getCashier().getId());
      }
      if(this.getCustomer() == null || this.getCustomer() instanceof CashCustomer) {
        stmt.setNull(3, java.sql.Types.INTEGER);
      }
      else {
        stmt.setInt(3, this.getCustomer().getId());
      }
      stmt.setBigDecimal(4, this.getSubTotal().getAmount());
      stmt.setBigDecimal(5, this.getTaxTotal().getAmount());
      stmt.executeUpdate();

      //get the auto inc field of the row just inserted
      this.setId(Base.getAutoIncKey(this));

      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error recording transaction totals with query: " + query.toString(), e);
    }
  }

  /*needs to acomplish several things:
   *  1: decrement all the items from total inventory
   *  2: store transaction totals
   *  3: store the transaction items
   */
  public void commit() {
    try {
      DatabaseConnection.getInstance().getConnection().setAutoCommit(false); //enables sql transaction

      // Adjust the transaction type for comp accounts
      if(this.getCustomer().isComplementary()) {
        if(this.getCode().equals(CODE_DEBITACCOUNT)) {
          this.setCode(CODE_DEBITCOMPACCOUNT);
        }
        else if(this.getCode().equals(CODE_CREDITACCOUNT)) {
          this.setCode(CODE_CREDITCOMPACCOUNT);
        }
        else if(this.getCode().equals(CODE_CLOSEACCOUNT)) {
          this.setCode(CODE_CREDITCOMPACCOUNT);
        }
      }

      InventoryItem.decrementInventory(this);
      this.save();
      commitItems();

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
   * of the form [transaction_id, sku, name, quantity, price, tax_rate].
   */
  private void commitItems() throws SQLException {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into transaction_items (transaction_id, sku, name, quantity, price, tax_rate) ");
    query.append("VALUES(?, ?, ?, ?, ?, ?)");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      for(TransactionItem item : this.items) {
        stmt.setInt(1, this.id);
        stmt.setString(2, item.getSku());
        stmt.setString(3, item.getName());
        stmt.setInt(4, item.getQuantity());
        stmt.setBigDecimal(5, item.getPrice().getAmount());
        stmt.setBigDecimal(6, item.getTaxRate().getAmount());
        stmt.executeUpdate();
      }

      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error recording transaction items with query: " + query.toString(), e);
      throw e;
    }
  }

  private void recalculateTotals() {
    this.subTotal = Money.ZERO;
    this.taxTotal = Money.ZERO;

    for(TransactionItem item : this.items) {
      this.subTotal = this.subTotal.plus(item.getTotal());
      this.taxTotal = this.taxTotal.plus(item.getPrice()).times(item.getTaxRate());
    }
  }
}
