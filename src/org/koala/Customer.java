package org.koala;
/*
 * Created on May 29, 2005
 */

/**
 * @author tom
 *
 */

import java.math.BigDecimal;

public class Customer extends Person {
	private BigDecimal balance;
	private BigDecimal renewAmount;
	private boolean comp;
	private String note; //some textual info that we need to keep

	public static final Customer CashCustomer =
		new Customer(0, BigDecimal.ZERO, "Cash", "Cash", false, BigDecimal.ZERO, null);

	public Customer(int customerId, BigDecimal balance, String lastName, String firstName, boolean comp, BigDecimal renewAmount, String note) {
	    super(customerId, lastName, firstName);
	    this.balance = balance;
	    this.balance.setScale(2, BigDecimal.ROUND_CEILING);
	    this.comp = comp;
	    this.renewAmount = renewAmount;
	    this.renewAmount.setScale(2, BigDecimal.ROUND_CEILING);
	    this.note = note;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal amount) {
			balance = amount;
	}

	public boolean isComplementary() {
	    return comp;
	}

	public BigDecimal getRenewAmount() {
		return renewAmount;
	}

	public String getNote() {
		return note;
	}
}
