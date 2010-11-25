package org.koala.model;
/*
 * Created on Apr 17, 2005
 */

/**
 * @author tom
 */

import org.koala.Money;

public class ForSale extends Item {
  public ForSale() {
    super();
  }

	public ForSale(String sku, String name, int quantity, Money price, Money taxRate, boolean unlimited) {
		super(sku, name, quantity, price, taxRate, unlimited);
	}
}
