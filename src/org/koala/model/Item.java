package org.koala.model;
/*
 * Created on Apr 17, 2005
 */

/**
 * @author tom
 */

import java.math.BigDecimal;
import java.util.HashMap;

public class Item {
	protected String sku;
	protected String invName;
	protected BigDecimal price, taxRate;
	protected int quantity;
	protected boolean unlimited;

	private static HashMap<String, String> specialItemSkus = null;

	//sku codes
	public static final String NEW_ACCOUNT_SKU = "newaccout";
	public static final String COMP_ACCOUNT_SKU = "compaccount";
	public static final String CASHOUT_SKU = "cashout";
	public static final String PARTIALCASH_CREDITHALF = "partialcredithalf";
	public static final String PARTIALCASH_CASHHALF = "partialcashhalf";
	public static final String INVENTORYADD_SKU = "inventoryadd";
	public static final String INVENTORYCORRECTION_SKU = "inventorycorrection";

	public Item(String Sku, String Name, int Quantity, BigDecimal Price, BigDecimal TaxRate, boolean Unlimited) {
		//purhaps some checking on the sku format?
		this.sku = Sku;
		this.invName = Name;

		this.quantity = Quantity;
		this.price = Price;
		this.price.setScale(2, BigDecimal.ROUND_CEILING);
		this.taxRate = TaxRate;
		this.taxRate.setScale(2, BigDecimal.ROUND_CEILING);
		this.unlimited = Unlimited;
	}

	public static final Item createSpecialItem(String sku, Customer customer, Transaction transaction) {
		Item specialItem = null;

		if(sku.equals(Item.CASHOUT_SKU) ||
			sku.equals(Item.NEW_ACCOUNT_SKU) ||
			sku.equals(Item.COMP_ACCOUNT_SKU))
			specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, customer.getBalance(), BigDecimal.ZERO, false);
		else if(sku.equals(Item.PARTIALCASH_CREDITHALF))
		    specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, transaction.getTotal().subtract(customer.getBalance()).negate(), BigDecimal.ZERO, false);
		else if(sku.equals(Item.PARTIALCASH_CASHHALF))
		    specialItem = new Item(sku, Item.getSpecialItemName(sku), 1, transaction.getTotal().subtract(customer.getBalance()), BigDecimal.ZERO, false);

		//let the nullexception float back up
		return specialItem;
	}

	public static final String getSpecialItemName(String sku) {
		if(specialItemSkus == null) {
			specialItemSkus = new HashMap<String, String>(7);
			specialItemSkus.put(Item.CASHOUT_SKU, "Customer Cashout");
			specialItemSkus.put(Item.NEW_ACCOUNT_SKU, "New Customer Account");
			specialItemSkus.put(Item.COMP_ACCOUNT_SKU, "New Comp Customer Account");
			specialItemSkus.put(Item.PARTIALCASH_CREDITHALF, "Credit Adjustment for Partial Cash");
			specialItemSkus.put(Item.PARTIALCASH_CASHHALF, "Cash Adjustment for Partial Cash");
			specialItemSkus.put(Item.INVENTORYADD_SKU, "Add Items to Inventory");
			specialItemSkus.put(Item.INVENTORYCORRECTION_SKU, "Corrected Inventory Count");
		}

		return specialItemSkus.get(sku);
	}

	public static boolean isSpecial(String sku) {
		return Item.getSpecialItemName(sku) != null;
	}

	public boolean isSpecial() {
		return Item.getSpecialItemName(this.sku) != null;
	}

	public String toString() {
		return invName;
	}

	public String getSku() {
		return sku;
	}

	public String getName() {
		return invName;
	}

	public int getQuantity() {
	    return quantity;
	}

	public void setQuantity(int amount) {
		quantity = amount;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getTotal() {
		return price.multiply(new BigDecimal(quantity));
	}

	public BigDecimal getTaxRate() {
	    return taxRate;
	}

	public boolean getUnlimited() {
	    return unlimited;
	}
}
