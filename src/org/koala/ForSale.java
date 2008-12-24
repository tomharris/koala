package org.koala;
/*
 * Created on Apr 17, 2005
 */

/**
 * @author tom
 */

import java.math.BigDecimal;

public class ForSale extends Item {
	public ForSale(String Sku, String Name, int Quantity, BigDecimal Price, BigDecimal TaxRate, boolean Unlimited) {
		super(Sku, Name, Quantity, Price, TaxRate, Unlimited);
	}
}
