package org.koala.model;
/*
 * Created on Dec 22, 2005
 */

/**
 * @author tom
 *
 */

import org.koala.Money;

public class CashCustomer extends Customer {

  public CashCustomer() {
    super();
    this.setBalance(Money.ZERO);
    this.setFirstName("Cash");
    this.setLastName("Cash");
    this.setComplementary(false);
    this.setRenewAmount(Money.ZERO);
  }

  public void save() {
    // no-op. We never want to persist this object.
  }
}
