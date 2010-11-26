package org.koala.model;
/*
 * Created on Apr 14, 2005
 */

/**
 * @author tom
 *
 * TODO: fold UpdateInventoryGUI transaction stuff into add/update inventory
 * functions here.
 */

import java.sql.*;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.koala.*;
import org.koala.model.Base;
import org.koala.exception.EntryAlreadyExistsException;
import org.koala.exception.ItemNotFoundException;

public class User extends Base {
  private int id; //non-neg if valid
  private String userName;
  private String password;
  private String firstName, lastName;
  private Transaction currentTransaction;
  private int accessLevel;

  //the various access levels
  public static final int NONE = 0;
  public static final int CASHIER = 1;
  public static final int MANAGER = 2;
  public static final int ADMIN = 3;

  private static Logger logger = Logger.getLogger(User.class);

  public User() {
    super();
  }

  protected void finalize() {
    this.userName = null;
    this.currentTransaction = null;
  }

  public String toString() {
    return this.userName;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public int getLevel() {
    return this.accessLevel;
  }

  public void setLevel(int level) {
    this.accessLevel = level;
  }

  public String getUserName() {
    return this.userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public static User login(String username, String password) {
    PreparedStatement stmt;
    ResultSet rs;
    int user_id = 0;
    StringBuilder query = new StringBuilder();
    query.append("select id from users where username=? and password_hash=");
    query.append(DatabaseConnection.getInstance().getProfile().getPasswordCmd());

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setString(1, username);
      stmt.setString(2, password);
      rs = stmt.executeQuery();

      if(rs.next()) {
        user_id = rs.getInt("id");
      }

      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error logging in user", e);
    }

    return User.find(user_id);
  }

  public static User find(int id) {
    PreparedStatement stmt;
    ResultSet rs;
    String query = "select level, username, firstname, lastname from users where id=?";

    User user = null;

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setInt(1, id);
      rs = stmt.executeQuery();

      if(rs.next()) {
        user = new User();
        user.setId(id);
        user.setLevel(rs.getInt("level"));
        user.setUserName(rs.getString("username"));
        user.setFirstName(rs.getString("firstname"));
        user.setLastName(rs.getString("lastname"));
      }

      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error retrieving user info for user(" + id + ")", e);
    }

    return user;
  }

  public static ArrayList<User> findAllByAccess(int access) {
    PreparedStatement stmt;
    ResultSet rs;
    String query = "select id, username, firstname, lastname from users where level=? order by username";

    ArrayList<User> userVec = new ArrayList<User>();
    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setInt(1, access);
      rs = stmt.executeQuery();

      while(rs.next()) {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setLevel(access);
        user.setUserName(rs.getString("username"));
        user.setFirstName(rs.getString("firstname"));
        user.setLastName(rs.getString("lastname"));

        userVec.add(user);
      }
      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error searching users", e);
    }

    return userVec;
  }

  //remove user (operator) from system
  public void destroy() {
    PreparedStatement stmt;
    String query = "delete from users where id=?";

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setInt(1, this.getId());
      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error removing user", e);
    }
  }

  //add a user (operator) to the system
  protected void create() throws EntryAlreadyExistsException {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("insert into users (username, password_hash, level, firstname, lastname) ");
    query.append("VALUES(?, ");
    query.append(DatabaseConnection.getInstance().getProfile().getPasswordCmd());
    query.append(", ?, ?, ?)");

    if(User.userExists(this.getUserName())) {
      throw new EntryAlreadyExistsException(this.getUserName()); //user already exists
    }

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      stmt.setString(1, this.getUserName());
      stmt.setString(2, this.getPassword());
      stmt.setInt(3, this.getLevel());
      stmt.setString(4, this.getFirstName());
      stmt.setString(5, this.getLastName());
      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error adding new user", e);
    }
  }

  //change user details
  //  doesnt change password if that field is null
  protected void update() {
    PreparedStatement stmt;
    StringBuilder query = new StringBuilder();
    query.append("update users set username=?, firstname=?, lastname=?, ");
    if(this.getPassword() != null && !this.getPassword().trim().equals("")) {
      query.append("password_hash=");
      query.append(DatabaseConnection.getInstance().getProfile().getPasswordCmd());
    }
    query.append(" where id=?");

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query.toString());
      int paramCount = 1;
      stmt.setString(paramCount++, this.getUserName());
      stmt.setString(paramCount++, this.getFirstName());
      stmt.setString(paramCount++, this.getLastName());
      if(this.getPassword() != null && !this.getPassword().trim().equals("")) {
        stmt.setString(paramCount++, this.getPassword());
      }
      stmt.setInt(paramCount++, this.getId());
      stmt.executeUpdate();
      stmt.close();
    }
    catch (SQLException e) {
      logger.error("SQL error moddifying user", e);
    }
  }

  public void doTransaction() {
    currentTransaction.commit();

    //print receipt here
    currentTransaction = null; //byebye; gc will clean up sometime
  }

  public void addItem(String sku, int quantity, Customer customer) throws ItemNotFoundException {
    Item currentItem = Item.createSpecialItem(sku, customer, currentTransaction); //check if this is special and create item
    if(currentItem == null) {
      currentItem = Item.findBySku(sku);
      if(currentItem == null)
        throw new ItemNotFoundException("add Item");

      currentItem.setQuantity(quantity);
    }

    if(currentTransaction == null) {
      currentTransaction = new Transaction();
      currentTransaction.setCashier(this);
      currentTransaction.setCustomer(customer);
      currentTransaction.addItem(currentItem);
    }
    else {
      currentTransaction.addItem(currentItem);
    }
  }

  //we just dont do a inventory lookup
  public void addSpecialItem(Item item, Customer customer) throws ItemNotFoundException {
    if(item == null) {
      throw new ItemNotFoundException("add Special item");
    }

    if(currentTransaction == null) {
      currentTransaction = new Transaction();
      currentTransaction.setCashier(this);
      currentTransaction.setCustomer(customer);
      currentTransaction.addItem(item);
    }
    else {
      currentTransaction.addItem(item);
    }
  }

  public void doPartialCashTransaction(Customer customer) {
    //cash half needs to be first, otherwise we lose the transaction total
    Item cashHalf = Item.createSpecialItem(Item.PARTIALCASH_CASHHALF, customer, currentTransaction);
    currentTransaction.addItem(Item.createSpecialItem(Item.PARTIALCASH_CREDITHALF, customer, currentTransaction));
    currentTransaction.commit();

    currentTransaction = new Transaction();
    currentTransaction.setCashier(this);
    currentTransaction.setCustomer(new CashCustomer());
    currentTransaction.addItem(cashHalf);
    currentTransaction.commit();
  }

  public void removeItem(String sku) {
    currentTransaction.removeItem(sku);
  }

  public void removeAllItems() {
    currentTransaction = null; //gc will take care of it
  }

  public boolean isTransactionStarted() {
    return currentTransaction != null;
  }

  public Money getTransactionSubTotal() {
    if(currentTransaction == null)
      return null;

    return currentTransaction.getSubTotal();
  }

  public Money getTransactionTotal() {
    if(currentTransaction == null)
      return null;

    return currentTransaction.getTotal();
  }

  public Money getTransactionTax() {
    if(currentTransaction == null)
      return null;

    return currentTransaction.getTax();
  }

  public Item getLastItem() {
    if(currentTransaction == null)
      return null;

    return currentTransaction.getLastItem();
  }

  //expensive OMG
  public ArrayList<Item> getCurrentTransactionItems() {
    return currentTransaction.getItems();
  }

  public Report customerReport(Customer customer) {
    return new CustomerReport(customer);
  }

  //manager level
  public void endOfDayReports() {
    //are we even doing this?
  }

  public Report financialReport() {
    return new FinancialReport();
  }

  public Report outstandingAccountsReport() {
    return new OutstandingAccountsReport();
  }

  /* stuff to think about
   * 1. check to see if any cashiers are logged in
   * 2. tell manager of any still logged in; best to log them out; optionaly force
   * 3. prevent ONLY cashiers from logging in
   */
  public void lockTerminals() {
      //TODO
  }

  public String resetDatabase() {
    //dump the entire pos db
    String packageName = this.getCurrentBackupMethod().createPackage();

    //save customers that have auto-renew accounts
    ArrayList<Customer> customers = Customer.findAllRenewable();

    //reset the DB
    Base.resetDB();

    //reload the renew accounts
    for(Customer customer : customers) {
      try {
        customer.setId(0); //basically mark the record as new
        customer.save();
      }
      catch(EntryAlreadyExistsException e) {
        logger.error("Error reloading renewable customers", e);
      }
    }

    return packageName;
  }

  public BackupMethod getCurrentBackupMethod() {
    BackupMethod method = null;
    String methodType = Config.getConfig().getValue("db_backup_method");

    if(methodType.equals("dump")) {
      method = new DBDumpBackup();
    }
    else if(methodType.equals("internal")) { //TODO: this is just a working name

    }
    else {
      logger.warn("Config value db_backup_method is not set to a valid type. Defaulting to 'dump'");
      method = new DBDumpBackup();
    }

    return method;
  }

  //we just check to see if that user is in our database
  private static boolean userExists(String username) {
    PreparedStatement stmt;
    ResultSet rs;
    boolean status = false;
    String query = "select id from users where username=?";

    try {
      stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(query);
      stmt.setString(1, username);
      rs = stmt.executeQuery();

      if(rs.next()) {
        status = true;
      }

      stmt.close();
      rs.close();
    }
    catch (SQLException e) {
      logger.error("SQL error checking if user exists", e);
    }

    return status;
  }
}
