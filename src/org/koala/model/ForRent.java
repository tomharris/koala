package org.koala.model;
/*
 * Created on Apr 17, 2005
 */

/**
 * @author tom
 */

import java.util.Date;

import org.koala.Money;

public class ForRent extends Item {
	Date checkedOut, due;

	public ForRent(String sku, String name, Money value) {
		super(sku, name, 0, value, Money.ZERO, false);
	}

	public Date getDueDate() {
		return due;
	}

	public void setDueDate(Date Due) {
		//maybe add a check for 'Due > today'
		due = Due;
	}

	public Date getCheckedOutDate() {
		return checkedOut;
	}

	public void setCheckedOutDate(Date checkedOutDate) {
		//maybe add a check for 'CheckedOutDate > today'
		checkedOut = checkedOutDate;
	}
}
