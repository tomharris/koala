package org.koala.model;
/**
 *  Shows customers who have not cashed out their accounts.
 */

/**
 * @author tom
 *
 */

import java.util.ArrayList;

import org.koala.Money;
import org.koala.model.Customer;

public class OutstandingAccountsReport extends Report {
  private ArrayList<Customer> customers;

  public OutstandingAccountsReport() {
    super();
    loadResources();
  }

  protected void finalize() {
    super.finalize();
    customers.clear();
    customers = null;
  }

  protected final void loadResources() {
    customers = Customer.findAll();
  }

  public final void doReport() {
    int numberCustomers = 0;
    StringBuilder sbCustomers = new StringBuilder();
    Money balanceTotal = Money.ZERO;

    for(Customer customer : customers) {
      if(customer.getBalance().isPlus() && !customer.isComplementary()) {
        numberCustomers++;
        balanceTotal = balanceTotal.plus(customer.getBalance());

        sbCustomers.append(customer.toString());
        sbCustomers.append(" (");
        sbCustomers.append(customer.getBalance().formattedString());
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
