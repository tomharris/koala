package org.koala;
/*
 * Created on Apr 14, 2005
 */

/**
 * @author tom
 *
 * TODO: fold UpdateInventoryGUI transaction stuff into add/update inventory
 * functions here.
 */

import java.util.ArrayList;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.koala.exception.EntryAlreadyExistsException;
import org.koala.exception.ItemNotFoundException;

public class User extends Person {
  private DBase dbHandle;
  private String userName;
  private Transaction currentTransaction;
  private int accessLevel;

  //the various access levels
  public static final int NONE = 0;
  public static final int CASHIER = 1;
  public static final int MANAGER = 2;
  public static final int ADMIN = 3;

  private static Logger logger = Logger.getLogger(User.class);

  public User(User user) {
    super(user.getId(), user.getLastName(), user.getFirstName());
    accessLevel = user.getLevel();
    userName = user.getUserName();
    currentTransaction = null;

    dbHandle = null;
  }

  public User(int uid, int level, String username, String firstname, String lastname) {
    super(uid, lastname, firstname);
    accessLevel = level;
    userName = username;
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

  public int getLevel() {
    return accessLevel;
  }

  public String getUserName() {
    return userName;
  }

  public static User login(String username, String password) throws Exception {
    User userInfo = null;

    try {
      DBase dbHandle = new DBase();
        userInfo = dbHandle.loginUser(username, password);
    }
    catch (Exception e) {
      logger.error("Error connecting to Database", e);
        throw new Exception("Error connecting to Database");
    }

      return userInfo;
  }

  public void logout() {
    this.dbHandle.finalize();
    this.dbHandle = null;
  }

  public void setDBHandle(DBase dbHandle) {
    this.dbHandle = dbHandle;
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

  public void addUser(User newUser, String password) throws EntryAlreadyExistsException {
        dbHandle.addUser(newUser, password);
  }

  public void removeUser(User remUser) {
        dbHandle.removeUser(remUser);
  }

  //a password of null means dont change it
  public void updateUser(User moddifiedUser, String password) {
        dbHandle.updateUser(moddifiedUser, password);
  }

  public Customer getCustomer(int customerid) {
        return dbHandle.getCustomer(customerid);
  }

  public ArrayList<Customer> getAllCustomers() {
        return dbHandle.getAllCustomers();
  }

  public ArrayList<Person> getCustomerList() {
        return dbHandle.getCustomerList();
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

      currentTransaction = new Transaction(this, Customer.CashCustomer, cashHalf);
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

  /* notes:
   * 1. respect access_level ie: manager cant search admins
   */
  public ArrayList<User> searchUsers(int userLevel) {
    if(userLevel > accessLevel)
      return null;

      if(accessLevel < MANAGER)
          return null;
      else if(accessLevel == MANAGER)
          return dbHandle.searchUsers(CASHIER);
      else if(accessLevel == ADMIN)
          return dbHandle.searchUsers(MANAGER);
      else
          return null;
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
}
