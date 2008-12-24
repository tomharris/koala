package org.koala;
/*
 * Created on Apr 17, 2005
 */

/**
 * @author tom
 */

import java.util.Date;
import java.math.BigDecimal;

public class ForRent extends Item {
	Date checkedOut, due;

	public ForRent(String Sku, String Name, BigDecimal Value) {
		super(Sku, Name, 0, Value, new BigDecimal(0), false);
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

	public void setCheckedOutDate(Date CheckedOutDate) {
		//maybe add a check for 'CheckedOutDate > today'
		checkedOut = CheckedOutDate;
	}
}
