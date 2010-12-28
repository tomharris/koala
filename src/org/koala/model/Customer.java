package org.koala.model;
/*
 * Created on May 29, 2005
 */

/**
 * @author tom
 *
 */

import java.sql.*;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.koala.DatabaseConnection;
import org.koala.Money;
import org.koala.exception.EntryAlreadyExistsException;

public class Customer extends Base {
  private String firstName, lastName;
  private Money balance;
  private Money renewAmount;
  private boolean comp;
  private String note; //some textual info that we need to keep

  private static Logger logger = Logger.getLogger(User.class);

  public Customer() {
    super();
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Money getBalance() {
    return balance;
  }

  public void setBalance(Money amount) {
    this.balance = amount;
  }

  public boolean isComplementary() {
    return comp;
  }

  public void setComplementary(boolean value) {
    this.comp = value;
  }

  public Money getRenewAmount() {
    return renewAmount;
  }

  public void setRenewAmount(Money amount) {
    this.renewAmount = amount;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String toString() {
    return new String(getLastName() + ", " + getFirstName());
  }

  public static Customer find(int id) {
    StringBuilder query = new StringBuilder();
    query.append("select firstname, lastname, comp, balance, ");
    query.append("renewamount, note from customers ");
    query.append("where id=?");

    Customer customer = null;
    try {
      PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();

      if(rs.next()) {
        customer = new Customer();
        customer.setId(id);
        customer.setFirstName(rs.getString("firstname"));
        customer.setLastName(rs.getString("lastname"));
        customer.setBalance(new Money(rs.getBigDecimal("balance")));
        customer.setComplementary(rs.getInt("comp") == 1);
        customer.setRenewAmount(new Money(rs.getBigDecimal("renewamount")));
        customer.setNote(rs.getString("note"));
      }
      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error finding customer", e);
    }

    return customer;
  }

  public static ArrayList<Customer> findAll() {
    StringBuilder query = new StringBuilder();
    query.append("select id, balance, firstname, lastname, comp, ");
    query.append("renewamount, note from customers ");
    query.append("order by lastname, firstname");

    ArrayList<Customer> customers = new ArrayList<Customer>();
    try {
      Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
      ResultSet rs = stmt.executeQuery(query.toString());

      while(rs.next()) {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setFirstName(rs.getString("firstname"));
        customer.setLastName(rs.getString("lastname"));
        customer.setBalance(new Money(rs.getBigDecimal("balance")));
        customer.setComplementary(rs.getInt("comp") == 1);
        customer.setRenewAmount(new Money(rs.getBigDecimal("renewamount")));
        customer.setNote(rs.getString("note"));

        customers.add(customer);
      }
      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error searching customers", e);
    }

    return customers;
  }

  public static ArrayList<Customer> findAllRenewable() {
    StringBuilder query = new StringBuilder();
    query.append("select id, firstname, lastname, renewamount, note from customers ");
    query.append("where renewamount > 0 order by lastname");

    ArrayList<Customer> customers = new ArrayList<Customer>();
    try {
      Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
      ResultSet rs = stmt.executeQuery(query.toString());

      while(rs.next()) {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setFirstName(rs.getString("firstname"));
        customer.setLastName(rs.getString("lastname"));
        customer.setBalance(new Money(rs.getBigDecimal("renewamount"))); // set the balance as the renewed amount
        customer.setComplementary(true); //always true for renewable accounts
        customer.setRenewAmount(new Money(rs.getBigDecimal("renewamount")));
        customer.setNote(rs.getString("note"));

        customers.add(customer);
      }
      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error saving renew customers", e);
    }

    customers.trimToSize();
    return customers;
  }

  //  we just check to see if that customer is in our database
  private static boolean doesExist(String lastname, String firstname) {
    PreparedStatement stmt;
    ResultSet rs;
    boolean status = false;
    String query = "select id from customers where lastname=? and firstname=?";

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setString(1, lastname);
      stmt.setString(2, firstname);
      rs = stmt.executeQuery();

      if(rs.next()) {
        status = true;
      }

      stmt.close();
      rs.close();
    }
    catch (SQLException e) {
      logger.error("SQL error checking if customer exists", e);
    }

    return status;
  }

  protected void create() throws EntryAlreadyExistsException {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into customers (firstname, lastname, balance, comp, renewamount, note) ");
    query.append("VALUES(?, ?, ?, ?, ?, ?)");

    if(Customer.doesExist(this.getLastName(), this.getFirstName())) {
      throw new EntryAlreadyExistsException(this.getFirstName() + " " + this.getLastName());
    }

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setString(1, this.getFirstName());
      stmt.setString(2, this.getLastName());
      stmt.setBigDecimal(3, this.getBalance().getAmount());
      stmt.setInt(4, this.isComplementary() ? 1 : 0);
      stmt.setBigDecimal(5, this.getRenewAmount().getAmount());
      stmt.setString(6, this.getNote());
      stmt.executeUpdate();
      stmt.close();

      //get the auto inc field of the row just inserted
      // and assign it to the customer
      this.setId(getAutoIncKey(this));
    }
    catch (SQLException e) {
      logger.error("SQL error adding new customer", e);
    }

    // Add to transaction log
    Transaction customerTransaction = new Transaction();
    if(this.isComplementary()) {
      customerTransaction.addItem(TransactionItem.createSpecialItem(Transaction.COMP_ACCOUNT_SKU, this, customerTransaction));
    }
    else {
      customerTransaction.addItem(TransactionItem.createSpecialItem(Transaction.NEW_ACCOUNT_SKU, this, customerTransaction));
    }
    customerTransaction.commit();
  }

  //change customer details
  public void update() {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("update customers set balance=?, firstname=?, ");
    query.append("lastname=?, comp=?, renewamount=?, note=? where id=?");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setBigDecimal(1, this.getBalance().getAmount());
      stmt.setString(2, this.getFirstName());
      stmt.setString(3, this.getLastName());
      stmt.setInt(4, this.isComplementary() ? 1 : 0);
      stmt.setBigDecimal(5, this.getRenewAmount().getAmount());
      stmt.setString(6, this.getNote());
      stmt.setInt(7, this.getId());
      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error moddifying customer", e);
    }
  }

  //remove customer from system
  public void destroy() {
    PreparedStatement stmt;
    String query = "delete from customers where id=?";

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setInt(1, this.getId());
      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error removing customer", e);
    }
  }
}
