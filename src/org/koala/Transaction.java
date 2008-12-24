package org.koala;
/*
 * Created on Apr 18, 2005
 *
 * The transaction will be a vector of all Items.
 */

/**
 * @author tom
 *	SUMARY: A transaction can be started when the customerid is given. We need
 *		another gui screen to be presented after the cashier logs in but
 *		before the CashierScreenGUI (to get the customer's name). This screen
 *		will collect the customer's name and convert that to a ID. The
 *		CashierScreenGUI will then be launched to add items to the transaction. 
 */

import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Date;

public class Transaction {
	private int id;
	private User cashier;
	private Customer customer;
	private BigDecimal subTotal;
	private BigDecimal tax;
	private ArrayList<Item> transactions = null;
	private String acctCode;
	private Date transTime;
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
	public Transaction(User cashier, Customer customer, Item newItem) {
		this.id = -1;
		this.subTotal = BigDecimal.ZERO;
	    this.subTotal.setScale(2, BigDecimal.ROUND_CEILING);
	    this.tax = BigDecimal.ZERO;
	    this.tax.setScale(2, BigDecimal.ROUND_CEILING);

	    this.cashier = cashier;
	    this.customer = customer;
	    if(newItem == null)
	    	this.transactions = null;
	    else {
	    	this.transactions = new ArrayList<Item>(initSize);
    		addItem(newItem);
    		if(newItem.getSku().equals(Item.NEW_ACCOUNT_SKU))
    			this.acctCode = Transaction.CODE_CREDITACCOUNT;
    		else if(newItem.getSku().equals(Item.COMP_ACCOUNT_SKU))
    			this.acctCode = Transaction.CODE_CREDITCOMPACCOUNT;
    		else if(newItem.getSku().equals(Item.CASHOUT_SKU))
    			this.acctCode = Transaction.CODE_CLOSEACCOUNT;
    		else if(newItem.getSku().equals(Item.INVENTORYADD_SKU))
    			this.acctCode = Transaction.CODE_INVENTORYADD;
    		else if(newItem.getSku().equals(Item.INVENTORYCORRECTION_SKU))
    			this.acctCode = Transaction.CODE_INVENTORYCORRECTION;
    		else if(customer != null && customer.isComplementary())
    			this.acctCode = Transaction.CODE_DEBITCOMPACCOUNT;
    		else if(customer != null && customer.getId() != 0)
    			this.acctCode = Transaction.CODE_DEBITACCOUNT;
    		else
    			this.acctCode = Transaction.CODE_CASH;
	    }
	}

	//mostly for reports; would take alot of work to craft a item to set all this info
	public Transaction(int id, User cashier, Customer customer, BigDecimal subTotal,
			BigDecimal tax, String accountCode, Date transTime) {
	    this.id = id;
		this.cashier = cashier;
	    this.customer = customer;
	    this.subTotal = subTotal;
	    this.subTotal.setScale(2, BigDecimal.ROUND_CEILING);
	    this.tax = tax;
	    this.tax.setScale(2, BigDecimal.ROUND_CEILING);
	    this.acctCode = accountCode;
	    this.transTime = transTime;
	}

	protected void finalize() {
		this.transactions.clear();
		this.transactions = null;

		this.cashier = null;
		this.customer = null;
		this.subTotal = null;
		this.tax = null;
		this.acctCode = null;
		this.transTime = null;
	}

	public void lookupTransactionItems(DBase dbHandle) {
		this.transactions = dbHandle.getTransactionItems(this.id);
		this.transactions.trimToSize();
	}

	public void addItem(Item newItem) {
		this.transactions.add(newItem);
		this.subTotal = this.subTotal.add(newItem.getTotal());
	    this.subTotal.setScale(2, BigDecimal.ROUND_CEILING);
	    this.tax = this.tax.add(newItem.getPrice().multiply(newItem.getTaxRate()));
	    this.tax.setScale(2, BigDecimal.ROUND_CEILING);
	}

	public void removeItem(String sku) {
		for(Item item : this.transactions) {
	        if(item.getSku().equals(sku)) {
	        	this.transactions.remove(item);
	            break;
	        }
		}
		
		this.subTotal = new BigDecimal(0);
		this.tax = new BigDecimal(0);
		for(Item item : this.transactions) {
			this.subTotal = this.subTotal.add(item.getTotal());
			this.tax = this.tax.add(item.getPrice()).multiply(item.getTaxRate());
	    }
	}

	public int getTransactionID() {
		return this.id;
	}

	public Date getTransactionTime() {
	    return this.transTime;
	}
	public BigDecimal getSubTotal() {
	    return this.subTotal;
	}

	public BigDecimal getTax() {
	    return this.tax;
	}

	//total rounded to the nearest penny
	public BigDecimal getTotal() {
	    return this.subTotal.add(this.tax);
	}

	public User getCashier() {
	    return new User(this.cashier);
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public Item getLastItem() {
	    return this.transactions.get(this.transactions.size() - 1);
	}

	public Item getFirstItem() {
	    return this.transactions.get(0);
	}

	public String getAcctCode() {
		return this.acctCode;
	}

	//please avoid as this is VERY expensive
	public ArrayList<Item> getAllItems() {
	    return new ArrayList<Item>(this.transactions);
	}
}
