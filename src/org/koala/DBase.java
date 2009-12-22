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
import org.koala.exception.EntryAlreadyExistsException;

public class DBase {
  private static Logger logger = Logger.getLogger(DBase.class);

  public DBase() {
  }

  protected void finalize() {
    DatabaseConnection.getInstance().disconnect();
  }

  public User getUser(int uid) {
      PreparedStatement stmt;
    ResultSet rs;
    String query =
      "select level, username, firstname, lastname from users where userid=?";

    User user = null;

    try {
        stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
        stmt.setInt(1, uid);
      rs = stmt.executeQuery();

      if(rs.next())
        user =  new User(uid, rs.getInt("level"), rs.getString("username"),
            rs.getString("firstname"), rs.getString("lastname"));

      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error retrieving user info for user(" + uid + ")", e);
    }

    return user;
  }

  public ArrayList<User> searchUsers(int Access) {
    PreparedStatement stmt;
    ResultSet rs;
      String query =
        "select userid, username, firstname, lastname from users where level=? order by username";

      ArrayList<User> userVec = new ArrayList<User>();
      try {
        stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
        stmt.setInt(1, Access);
      rs = stmt.executeQuery();

      while(rs.next()) {
          userVec.add(new User(rs.getInt("userid"), Access,
                  rs.getString("username"), rs.getString("firstname"),
                  rs.getString("lastname")));
      }
      rs.close();
      stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error searching users", e);
      }

      return userVec;
  }

  public Customer getCustomer(int customerid) {
    if(customerid == Customer.CashCustomer.getId())
      return null;

      StringBuilder query = new StringBuilder();
      query.append("select firstname, lastname, comp, ");
      query.append("renewamount, note from customers left join notes on ");
      query.append("customers.customerid = notes.customerid where customers.customerid=?");

      Customer customer = null;
      try {
        PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
        stmt.setInt(1, customerid);
      ResultSet rs = stmt.executeQuery();

      if(rs.next()) {
        customer = new Customer(customerid,
            getCustomerBalanceFromTransactions(customerid),
                  rs.getString("lastname"), rs.getString("firstname"),
                  rs.getInt("comp") == 1, rs.getBigDecimal("renewamount"),
                  rs.getString("note"));
      }
      rs.close();
      stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error searching customer", e);
      }

      return customer;
  }

  public ArrayList<Customer> getAllCustomers() {
      StringBuilder query = new StringBuilder();
      query.append("select customers.customerid, ");
      query.append("customers.firstname, customers.lastname, customers.comp, ");
      query.append("customers.renewamount, notes.note from customers ");
      query.append("left join notes on customers.customerid = notes.customerid ");
      query.append("order by customers.lastname, customers.firstname");

      ArrayList<Customer> custVec = new ArrayList<Customer>();
      try {
        Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
      ResultSet rs = stmt.executeQuery(query.toString());

      while(rs.next()) {
          custVec.add(new Customer(rs.getInt("customerid"),
              getCustomerBalanceFromTransactions(rs.getInt("customerid")),
                  rs.getString("lastname"), rs.getString("firstname"),
                  rs.getInt("comp") == 1, rs.getBigDecimal("renewamount"),
                  rs.getString("note")));
      }
      rs.close();
      stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error searching customers", e);
      }

      return custVec;
  }


  public ArrayList<Person> getCustomerList() {
    String query =
      "select customerid, firstname, lastname from customers order by lastname, firstname";

    ArrayList<Person> custVec = new ArrayList<Person>();
        try {
          Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while(rs.next()) {
            custVec.add(new Person(rs.getInt("customerid"),
                    rs.getString("lastname"), rs.getString("firstname")));
        }
        rs.close();
        stmt.close();
        }
        catch (SQLException e) {
        logger.error("SQL error generating customer list", e);
        }

        return custVec;
  }

  public BigDecimal getCustomerBalanceFromTransactions(int id) {
    if(id == Customer.CashCustomer.getId())
      return null;

      String creditQuery = "select sum(subtotal+tax) as credits from transactions t where t.code='b' and t.customerid=?";
      String debitQuery = "select sum(subtotal+tax) as debits from transactions t where t.code='c' and t.customerid=?";

      BigDecimal balance = null;
      try {
        PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(creditQuery);
        stmt.setInt(1, id);
      ResultSet rs = stmt.executeQuery();

      BigDecimal credits = null;
      if(rs.next()) {
        credits = rs.getBigDecimal("credits");
      }
      rs.close();
      stmt.close();

      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(debitQuery);
        stmt.setInt(1, id);
      rs = stmt.executeQuery();

      if(credits != null && rs.next()) {
        balance = credits.subtract(rs.getBigDecimal("debits"));
      }
      rs.close();
      stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error loading customer balance", e);
      }

      return balance;
  }

  public ArrayList<Transaction> getTransactions(Customer customer, boolean getItems) {
    StringBuilder query = new StringBuilder();
    int initialCapacity = 10; //initial array capacity; 10 is the default for arraylist

      query.append("select transaction_id, transaction_time, cashierid, subtotal, tax, code from transactions ");
      //here are some good guesses for a default array size; on average, I think this is close
    if(customer == null) { //we are getting all transaction for everyone
      initialCapacity = 750;
    }
    else { //just the one customer
      query.append("where customerid=? ");
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
            transaction = new Transaction(rs.getInt("transaction_id"),
                  this.getUser(rs.getInt("cashierid")), customer,
                  rs.getBigDecimal("subtotal"),
                    rs.getBigDecimal("tax"),
                    rs.getString("code"), rs.getDate("transaction_time"));

            if(getItems)
              transaction.lookupTransactionItems(this);

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

  public ArrayList<Item> getTransactionItems(int transNumber) {
    StringBuilder query = new StringBuilder();
      query.append("select transaction_items.sku, inventory.name, transaction_items.quantity, ");
      query.append("transaction_items.price, inventory.tax, inventory.unlimited ");
      query.append("from transaction_items ");
      query.append("left outer join inventory on inventory.sku=transaction_items.sku ");
      query.append("where transaction_id=?");
      ArrayList<Item> transItems = new ArrayList<Item>();

      try {
        PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
        stmt.setInt(1, transNumber);
        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
          String name = rs.getString("name");
          if(name == null)
            name = rs.getString("sku"); //reasonable default I should think

          BigDecimal price = rs.getBigDecimal("price");
          if(price == null)
            price = BigDecimal.ZERO;

          BigDecimal tax = rs.getBigDecimal("tax");
          if(tax == null)
            tax = BigDecimal.ZERO;

          transItems.add(new ForSale(rs.getString("sku"), name,
              rs.getInt("quantity"), price, tax,
              rs.getInt("unlimited") == 1));
          }
          rs.close();
          stmt.close();
      }
      catch (SQLException e) {
          logger.error("SQL error loading customer items", e);
      }

      return transItems;
  }

  public User loginUser(String username, String password) {
      PreparedStatement stmt;
    ResultSet rs;
      User loginUser = null;
      StringBuilder query = new StringBuilder();
    query.append("select userid from users where username=? and password=");
    query.append(DatabaseConnection.getInstance().getProfile().getPasswordCmd());

    try {
        stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
        stmt.setString(1, username);
        stmt.setString(2, password);
      rs = stmt.executeQuery();

      if(rs.next()) {
          int uid = rs.getInt("userid");

        loginUser = getUser(uid);
        loginUser.setDBHandle(this);
      }

      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error logging in user", e);
    }

    return loginUser;
  }

  //argument: sku of the item in inventory
  //returns:  Item object of the inventory item
  public Item getItem(String sku) {
      PreparedStatement stmt;
    ResultSet rs;
    String query =
      "select rentable, name, quantity, price, tax, unlimited from inventory where sku=?";

    String specialName = Item.getSpecialItemName(sku);
    if(specialName != null)
      return new Item(sku, specialName, 0, BigDecimal.ZERO, BigDecimal.ZERO, false);

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

    if(rentable)
      return new ForRent(sku, name, price);

    return new ForSale(sku, name, quantity, price, taxRate, unlimited);
  }

  public ArrayList<Item> getAllItem() {
      PreparedStatement stmt;
    ResultSet rs;
    String query =
      "select sku, rentable, name, quantity, price, tax, unlimited from inventory";

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
  public void addInv(Item newItem) {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into inventory (name, quantity, price, tax, unlimited, rentable, sku) ");
      query.append("VALUES(?, ?, ?, ?, ?, ?, ?)");

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
          stmt.setString(1, newItem.getName());
          stmt.setInt(2, newItem.getQuantity());
          stmt.setBigDecimal(3, newItem.getPrice());
          stmt.setBigDecimal(4, newItem.getTaxRate());
          stmt.setInt(5, (newItem.getUnlimited() ? 1 : 0));
          stmt.setInt(6, (newItem instanceof ForRent ? 1 : 0));
          stmt.setString(7, newItem.getSku());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
          logger.error("SQL error adding inventory item", e);
      }
  }

  //Change the details of this item in inventory
  public void updateInv(Item invItem) {
    PreparedStatement stmt;
      StringBuilder query = new StringBuilder();
      query.append("update inventory set name=?, quantity=?, price=?, ");
      query.append("tax=?, unlimited=?, rentable=? where sku=?");

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
          stmt.setString(1, invItem.getName());
          stmt.setInt(2, invItem.getQuantity());
          stmt.setBigDecimal(3, invItem.getPrice());
          stmt.setBigDecimal(4, invItem.getTaxRate());
          stmt.setInt(5, (invItem.getUnlimited() ? 1 : 0));
          stmt.setInt(6, (invItem instanceof ForRent ? 1 : 0));
          stmt.setString(7, invItem.getSku());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
          System.err.println ("SQL error modifying inventory item");
          System.err.println ("Details : " + e.getMessage());
          e.printStackTrace();
          logger.error("Illegal Driver Access", e);
      }
  }

  //removes the item with this sku from inventory
  public void removeInv(String Sku) {
    PreparedStatement stmt;
    String query = "delete from inventory where sku=?";

    try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
          stmt.setString(1, Sku);
          stmt.executeUpdate();
          stmt.close();
    }
    catch (SQLException e) {
          logger.error("SQL error removing inventory item", e);
      }
  }

  //add a user (operator) to the system
  public void addUser(User newUser, String password) throws EntryAlreadyExistsException {
    PreparedStatement stmt;
      StringBuilder query = new StringBuilder();
      query.append("insert into users (username, password, level, firstname, lastname) ");
      query.append("VALUES(?, ");
      query.append(DatabaseConnection.getInstance().getProfile().getPasswordCmd());
      query.append(", ?, ?, ?)");

      if(userExists(newUser.getUserName()))
          throw new EntryAlreadyExistsException(newUser.getUserName()); //user already exists

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
          stmt.setString(1, newUser.getUserName());
          stmt.setString(2, password);
          stmt.setInt(3, newUser.getLevel());
          stmt.setString(4, newUser.getFirstName());
          stmt.setString(5, newUser.getLastName());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
          logger.error("SQL error adding new user", e);
      }
  }

  //remove user (operator) from system
  public void removeUser(User remUser) {
    PreparedStatement stmt;
      String query = "delete from users where userid=?";

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
          stmt.setInt(1, remUser.getId());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error removing user", e);
      }
  }

  //change user details
  //  doesnt change password if that field is null
  public void updateUser(User moddifiedUser, String password) {
      PreparedStatement stmt;
      StringBuilder query = new StringBuilder();
      query.append("update users set username=?, firstname=?, lastname=?, ");
      if(password != null && !password.trim().equals("")) {
        query.append("password=");
        query.append(DatabaseConnection.getInstance().getProfile().getPasswordCmd());
      }
      query.append(" where userid=?");

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
          int paramCount = 1;
          stmt.setString(paramCount++, moddifiedUser.getUserName());
          stmt.setString(paramCount++, moddifiedUser.getFirstName());
          stmt.setString(paramCount++, moddifiedUser.getLastName());
          if(password != null && !password.trim().equals(""))
              stmt.setString(paramCount++, password);
          stmt.setInt(paramCount++, moddifiedUser.getId());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
          logger.error("SQL error moddifying user", e);
      }
  }

  //we just check to see if that user is in our database
  private boolean userExists(String username) {
      PreparedStatement stmt;
    ResultSet rs;
    boolean status = false;
      String query = "select userid from users where username=?";

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
          stmt.setString(1, username);
          rs = stmt.executeQuery();

          if(rs.next())
              status =  true;

          stmt.close();
          rs.close();
      }
      catch (SQLException e) {
          logger.error("SQL error checking if user exists", e);
      }

      return status;
  }

  //  we just check to see if that customer is in our database
  private boolean customerExists(String lastname, String firstname) {
      PreparedStatement stmt;
    ResultSet rs;
    boolean status = false;
      String query = "select customerid from customers where lastname=? and firstname=?";

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
          stmt.setString(1, lastname);
          stmt.setString(2, firstname);
          rs = stmt.executeQuery();

          if(rs.next())
              status = true;

          stmt.close();
          rs.close();
      }
      catch (SQLException e) {
          logger.error("SQL error checking if customer exists", e);
      }

      return status;
  }

  //add a new customer to the database and return customer with correct userid
  public Customer addCustomer(Customer newCustomer) throws EntryAlreadyExistsException {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into customers (firstname, lastname, balance, comp, renewamount)");
    query.append("VALUES(?, ?, ?, ?, ?)");

      if(customerExists(newCustomer.getLastName(), newCustomer.getFirstName()))
          throw new EntryAlreadyExistsException(newCustomer.getFirstName() + " " + newCustomer.getLastName());

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
          stmt.setString(1, newCustomer.getFirstName());
          stmt.setString(2, newCustomer.getLastName());
          stmt.setBigDecimal(3, newCustomer.getBalance());
          stmt.setInt(4, newCustomer.isComplementary() ? 1 : 0);
          stmt.setBigDecimal(5, newCustomer.getRenewAmount());
          stmt.executeUpdate();
          stmt.close();

          //get the auto inc field of the row just inserted
          // and assign it to the customer
          newCustomer.setId(getAutoIncKey("customers", "customerid"));
      }
      catch (SQLException e) {
          logger.error("SQL error adding new customer", e);
      }

      //save account note
      updateCustomerNote(newCustomer);

      return newCustomer;
  }

  public void addCustomers(ArrayList<Customer> customers) {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into customers (firstname, lastname, balance, comp, renewamount)");
    query.append("VALUES(?, ?, ?, ?, ?)");

    try {
        stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
        DatabaseConnection.getInstance().getConnection().setAutoCommit(false); //enables sql transaction

        for(Customer newCustomer : customers) {
          if(customerExists(newCustomer.getLastName(), newCustomer.getFirstName()))
            updateCustomer(newCustomer); //overwrite any dups

              stmt.setString(1, newCustomer.getFirstName());
              stmt.setString(2, newCustomer.getLastName());
              stmt.setBigDecimal(3, newCustomer.getBalance());
              stmt.setInt(4, newCustomer.isComplementary() ? 1 : 0);
              stmt.setBigDecimal(5, newCustomer.getRenewAmount());
              stmt.executeUpdate();

              //get the auto inc field of the row just inserted
              newCustomer.setId(getAutoIncKey("customers", "customerid"));

              //save account note
            updateCustomerNote(newCustomer);
        }

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
        logger.error("SQL error adding new customers: Transaction is being rolled back", e);
      }
  }

  //change customer details
  public void updateCustomer(Customer customer) {
      PreparedStatement stmt;
      StringBuilder query = new StringBuilder();
    query.append("update customers set balance=?, firstname=?, ");
    query.append("lastname=?, comp=?, renewamount=? where customerid=?");

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
          stmt.setBigDecimal(1, customer.getBalance());
          stmt.setString(2, customer.getFirstName());
          stmt.setString(3, customer.getLastName());
          stmt.setInt(4, customer.isComplementary() ? 1 : 0);
          stmt.setBigDecimal(5, customer.getRenewAmount());
          stmt.setInt(6, customer.getId());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
          logger.error("SQL error moddifying customer", e);
      }

      //save any changes to note
      updateCustomerNote(customer);
  }

  //reamove customer from system
  public void removeCustomer(Customer customer) {
    PreparedStatement stmt;
      String query = "delete from customers where customerid=?";

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
          stmt.setInt(1, customer.getId());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error removing customer", e);
      }
  }

  //update note on a customer account
  // updating a note to null will delete the note
  private void updateCustomerNote(Customer customer) {
    if(customer.getId() != Customer.CashCustomer.getId()) {
      //first delete the old note
      removeCustomerNote(customer);

      //add note if exists
      addCustomerNote(customer);
    }
  }

  //add a new note to customer account
  private void addCustomerNote(Customer customer) {
    PreparedStatement stmt;
      String query = "insert into notes (customerid, note) VALUES(?, ?)";

      if(customer.getNote() == null || customer.getNote().equals(""))
        return;

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
          stmt.setInt(1, customer.getId());
          stmt.setString(2, customer.getNote());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
          logger.error("SQL error adding new customer note", e);
      }
  }

  //remove this customer's note
  private void removeCustomerNote(Customer customer) {
    PreparedStatement stmt;
    String query = "delete from notes where customerid=?";

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
          stmt.setInt(1, customer.getId());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error removing customer note", e);
      }
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
    query.append("insert into transactions (transaction_time, code, cashierid, customerid, subtotal, tax) ");
    query.append("VALUES(");
    query.append(DatabaseConnection.getInstance().getProfile().getCurrentTimeCmd());
    query.append(", ?, ?, ?, ?, ?)");

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());

          stmt.setString(1, TransAction.getAcctCode());
          stmt.setInt(2, TransAction.getCashier().getId());
          if(TransAction.getCustomer() == null ||
              TransAction.getCustomer().getId() == Customer.CashCustomer.getId())
            stmt.setNull(3, java.sql.Types.INTEGER);
          else
            stmt.setInt(3, TransAction.getCustomer().getId());
          stmt.setBigDecimal(4, TransAction.getSubTotal());
          stmt.setBigDecimal(5, TransAction.getTax());
          stmt.executeUpdate();

          //get the auto inc field of the row just inserted
          autoIncKey = getAutoIncKey("transactions", "transaction_id");

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
              stmt.setBigDecimal(4, item.getPrice());
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
      if(transaction.getCustomer() == null ||
          transaction.getCustomer().getId() == Customer.CashCustomer.getId())
        return;

      //check to see if the transaction is special;
      // its not that allowing these is 'bad', but rather redundent
      //
      //We use first item because the last may be a correction due
      // to an over spending (which would be special). We would
      // still need to debit the account in that case.
      if(Item.isSpecial(transaction.getFirstItem().getSku()))
        return;

    PreparedStatement stmt;
    String query = "update customers set balance=? where customerid=?";

      try {
          stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
          stmt.setBigDecimal(1, transaction.getCustomer().getBalance().subtract(transaction.getTotal()));
          stmt.setInt(2, transaction.getCustomer().getId());
          stmt.executeUpdate();
          stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error debiting cutomer account", e);
      throw e;
      }
  }

  public ArrayList<Customer> getRenewCustomers() {
    StringBuilder query = new StringBuilder();
    query.append("select customers.customerid, customers.firstname, ");
    query.append("customers.lastname, customers.renewamount, notes.note from customers ");
    query.append("left join notes on customers.customerid = notes.customerid ");
    query.append("where customers.renewamount > 0 order by customers.lastname");

    ArrayList<Customer> customers = new ArrayList<Customer>();
      try {
        Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement();
      ResultSet rs = stmt.executeQuery(query.toString());

      while(rs.next()) {
        customers.add(new Customer(rs.getInt("customerid"),
                  rs.getBigDecimal("renewamount"),
                  rs.getString("lastname"), rs.getString("firstname"),
                  true, rs.getBigDecimal("renewamount"),
                  rs.getString("note")));
      }
      rs.close();
      stmt.close();
      }
      catch (SQLException e) {
      logger.error("SQL error saving renew customers", e);
      //System.exit(0);
      }

      customers.trimToSize();
      return customers;
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
