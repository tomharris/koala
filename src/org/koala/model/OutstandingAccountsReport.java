package org.koala.model;
/**
 *  Shows customers who have not cashed out their accounts.
 */

/**
 * @author tom
 *
 */

import java.util.ArrayList;
import java.math.BigDecimal;

import org.koala.DBase;

public class OutstandingAccountsReport extends Report {
	private ArrayList<Customer> customers;

	public OutstandingAccountsReport(DBase dbHandle) {
		super(dbHandle);
		loadResources();
	}

	protected void finalize() {
		super.finalize();
		customers.clear();
		customers = null;
	}

    protected final void loadResources() {
		customers = dbHandle.getAllCustomers();
    }

	public final void doReport() {
		int numberCustomers = 0;
		StringBuilder sbCustomers = new StringBuilder();
		BigDecimal balanceTotal = BigDecimal.ZERO;

		for(Customer customer : customers) {
			if(customer.getBalance().floatValue() > 0 
					&& !customer.isComplementary()) {
				numberCustomers++;
				balanceTotal = balanceTotal.add(customer.getBalance());

				sbCustomers.append(customer.toString());
				sbCustomers.append(" (");
				sbCustomers.append(Report.currencyFormat.format(customer.getBalance()));
				sbCustomers.append(")\n");
			}
		}

		if(numberCustomers > 0) {
			report.append("Customers remaining with a balance: ");
			report.append(numberCustomers);
			report.append("\nOutstanding accounts total: ");
			report.append(balanceTotal);
			report.append("\n\nThe following customers have an account balance:\n");
			report.append(sbCustomers.toString());
		}
		else {
			report.append("There are no customers with outstanding account balances.");
		}
	}
}