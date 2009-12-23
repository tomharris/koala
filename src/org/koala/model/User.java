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
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.koala.*;
import org.koala.exception.EntryAlreadyExistsException;
import org.koala.exception.ItemNotFoundException;

public class User extends Base {
  private DBase dbHandle;
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

  public User(User user) {
    this.id = user.getId();
    this.lastName = user.getLastName();
    this.firstName = user.getFirstName();
    this.accessLevel = user.getLevel();
    this.userName = user.getUserName();
    currentTransaction = null;

    dbHandle = null;
  }

  public User(int id, int level, String username, String firstname, String lastname) {
    this.id = id;
		this.lastName = lastname;
		this.firstName = firstname;
    this.accessLevel = level;
    this.userName = username;
    currentTransaction = null;

    //null is useful because this could have been a search result
    dbHandle = null;
  }

  protected void finalize() {
    this.dbHandle.finalize();
    this.dbHandle = null;
    this.userName = null;
    this.currentTransaction = null;
  }

  public String toString() {
      return userName;
  }

  public int getId() {
		return id;
	}

	public void setId(int id) {
	  this.id = id;
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

  public int getLevel() {
    return accessLevel;
  }

  public String getUserName() {
    return userName;
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

  public void logout() {
    this.dbHandle.finalize();
    this.dbHandle = null;
  }

  public void setDBHandle(DBase dbHandle) {
    this.dbHandle = dbHandle;
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
        user = new User(id, rs.getInt("level"), rs.getString("username"), rs.getString("firstname"), rs.getString("lastname"));
        user.setDBHandle(new DBase());
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
        userVec.add(new User(rs.getInt("id"), access,
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

  public void addCustomer(Customer customer) throws EntryAlreadyExistsException, ItemNotFoundException {
      customer = dbHandle.addCustomer(customer);
    if(customer.isComplementary())
      this.addItem(Item.COMP_ACCOUNT_SKU, -1, customer);
    else
      this.addItem(Item.NEW_ACCOUNT_SKU, -1, customer);
    this.doTransaction();
  }

  public void updateCustomer(Customer customer) throws ItemNotFoundException {
    Customer oldCustomer = dbHandle.getCustomer(customer.getId());
    BigDecimal balanceAdded = customer.getBalance().subtract(oldCustomer.getBalance());

    if(balanceAdded.compareTo(BigDecimal.ZERO) > 0) { //amount > 0
      BigDecimal balance = customer.getBalance();
      customer.setBalance(balanceAdded); //the transaction log only needs to show the amount added

      //remove items in the transaction prior to adding money
      this.removeAllItems();
      this.addItem(Item.NEW_ACCOUNT_SKU, -1, customer);
      this.doTransaction();
            customer.setBalance(balance);
        }
        else if(balanceAdded.negate().equals(oldCustomer.getBalance())) { //cashout
          customer.setBalance(oldCustomer.getBalance()); //show in the transaction log that all money was removed
          this.addItem(Item.CASHOUT_SKU, -1, customer);
          this.doTransaction();
          this.removeAllItems();
          customer.setBalance(BigDecimal.ZERO);
        }
        dbHandle.updateCustomer(customer);
  }

  public void removeCustomer(Customer remCustomer) {
        dbHandle.removeCustomer(remCustomer);
  }

  public Customer getCustomer(int customerid) {
        return dbHandle.getCustomer(customerid);
  }

  public ArrayList<Customer> getAllCustomers() {
        return dbHandle.getAllCustomers();
  }

  //cashier level
  public void doTransaction() {
        dbHandle.doTransaction(currentTransaction);

      //print receipt here
      currentTransaction = null; //byebye; gc will clean up sometime
  }

  public void addItem(String sku, int quantity, Customer customer) throws ItemNotFoundException {
      Item currentItem = Item.createSpecialItem(sku, customer, currentTransaction); //check if this is special and create item
      if(currentItem == null) {
          currentItem = dbHandle.getItem(sku);
          if(currentItem == null)
              throw new ItemNotFoundException("add Item");

          currentItem.setQuantity(quantity);
      }

      if(currentTransaction == null)
          currentTransaction = new Transaction(this, customer, currentItem);
      else
          currentTransaction.addItem(currentItem);
  }

  //we just dont do a inventory lookup
  public void addSpecialItem(Item item, Customer customer) throws ItemNotFoundException {
      if(item == null) {
        throw new ItemNotFoundException("add Special item");
      }

      if(currentTransaction == null)
          currentTransaction = new Transaction(this, customer, item);
      else
          currentTransaction.addItem(item);
  }

  public void doPartialCashTransaction(Customer customer) {
      //cash half needs to be first, otherwise we lose the transaction total
      Item cashHalf = Item.createSpecialItem(Item.PARTIALCASH_CASHHALF, customer, currentTransaction);
      currentTransaction.addItem(Item.createSpecialItem(Item.PARTIALCASH_CREDITHALF, customer, currentTransaction));
      doTransaction();

      currentTransaction = new Transaction(this, new CashCustomer(), cashHalf);
      doTransaction();
  }

  public Item getItem(String sku) {
        return dbHandle.getItem(sku);
  }

  public ArrayList<Item> getAllItems() {
        return dbHandle.getAllItem();
  }

  public void addInventory(Item invItem) {
    //currentTransaction = new Transaction(this, Customer.getInternalCustomer(), invItem);
    //doTransaction();
        dbHandle.addInv(invItem);
  }

  public void updateInventory(Item invItem) {
        dbHandle.updateInv(invItem);
  }

  public void removeInventory(String Sku) {
        dbHandle.removeInv(Sku);
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

  public BigDecimal getTransactionSubTotal() {
      if(currentTransaction == null)
        return null;

      return currentTransaction.getSubTotal().setScale(2, BigDecimal.ROUND_CEILING);
  }

  public BigDecimal getTransactionTotal() {
      if(currentTransaction == null)
        return null;

      return currentTransaction.getTotal().setScale(2, BigDecimal.ROUND_CEILING);
  }

  public BigDecimal getTransactionTax() {
      if(currentTransaction == null)
      return null;

      return currentTransaction.getTax().setScale(2, BigDecimal.ROUND_CEILING);
  }

  public Item getLastItem() {
      if(currentTransaction == null)
      return null;

      return currentTransaction.getLastItem();
  }

  //expensive OMG
  public ArrayList<Item> getCurrentTransactionItems() {
      return currentTransaction.getAllItems();
  }

  public Report customerReport(Customer customer) {
      return new CustomerReport(dbHandle, customer);
  }

  //manager level
  public void endOfDayReports() {
      //are we even doing this?
  }

  public Report financialReport() {
      return new FinancialReport(dbHandle);
  }

  public Report outstandingAccountsReport() {
      return new OutstandingAccountsReport(dbHandle);
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
      ArrayList<Customer> renewCustomers = dbHandle.getRenewCustomers();

      //reset the DB
      dbHandle.resetDB();

      //reload the renew accounts
      dbHandle.addCustomers(renewCustomers);

      return packageName;
  }

  public BackupMethod getCurrentBackupMethod() {
    BackupMethod method = null;
    String methodType = Config.getConfig().getValue("db_backup_method");

    if(methodType.equals("dump")) {
      method = new DBDumpBackup(this.dbHandle);
    }
    else if(methodType.equals("internal")) { //TODO: this is just a working name

    }
    else {
      logger.warn("Config value db_backup_method is not set to a valid type. Defaulting to 'dump'");
      method = new DBDumpBackup(this.dbHandle);
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
