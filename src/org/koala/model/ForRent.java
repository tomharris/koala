package org.koala.model;
/*
 * Created on Apr 17, 2005
 */

/**
 * @author tom
 */

import java.util.Date;

import org.koala.Money;

public class ForRent extends InventoryItem {
  Date checkedOut, dueDate;

  public ForRent() {
    super();
  }

  public ForRent(String sku, String name, Money price, Money taxRate) {
    super();
    this.sku = sku;
    this.name = name;
    this.price = price;
    this.taxRate = taxRate;
  }

  public Date getDueDate() {
    return this.dueDate;
  }

  public void setDueDate(Date dueDate) {
    //maybe add a check for 'Due > today'
    this.dueDate = dueDate;
  }

  public Date getCheckedOutDate() {
    return this.checkedOut;
  }

  public void setCheckedOutDate(Date checkedOutDate) {
    //maybe add a check for 'CheckedOutDate > today'
    this.checkedOut = checkedOutDate;
  }
}
