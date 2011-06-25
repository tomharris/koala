package org.koala.model;
/*
 * Created on Feb 11, 2006
 *
 */

/**
 * @author tom
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;

import org.koala.Money;

public class CustomerReport extends Report {
  private ArrayList<Transaction> transactions;
  private Customer customer;

  public CustomerReport(Customer customer) {
    super();
    this.customer = customer;
    loadResources();
  }

  protected void finalize() {
    super.finalize();
    transactions.clear();
    transactions = null;
    customer = null;
  }

  public boolean isPrintable() {
    return true;
  }

  protected final void loadResources() {
    transactions = Transaction.getAll(customer);
  }

  public final void doReport() {
    Money totalSpent = Money.ZERO;
    Money totalAdded = Money.ZERO;
    Money totalRefunded = Money.ZERO;
    SimpleDateFormat todayFormat = new SimpleDateFormat("EEE MMM dd yyyy 'at' h:mm a");

    report.append("Purchase history for customer: ");
    report.append(customer.getFirstName());
    report.append(" ");
    report.append(customer.getLastName());
    report.append("\n\n");

    for(Transaction transaction : transactions) {
      //print day header
      report.append(todayFormat.format(transaction.getTransactionTime()));
      report.append(":\n");

      for(TransactionItem item : transaction.getAllItems()) {
        report.append("\t");

        if(item.getSku().equals(TransactionItem.CORRECTION_SKU) &&
          (transaction.getAcctCode().equals(Transaction.CODE_CREDITACCOUNT) || transaction.getAcctCode().equals(Transaction.CODE_CREATEACCOUNT) ||
            transaction.getAcctCode().equals(Transaction.CODE_CREDITCOMPACCOUNT) || transaction.getAcctCode().equals(Transaction.CODE_CREATECOMPACCOUNT))) {

          report.append("Added Money");
        }
        else if(item.getSku().equals(TransactionItem.CORRECTION_SKU) &&
          (transaction.getAcctCode().equals(Transaction.CODE_CLOSEACCOUNT) || transaction.getAcctCode().equals(Transaction.CODE_CLOSECOMPACCOUNT))) {

          report.append("Closed Account");
        }
        else {
          report.append(item.getName());
        }
        report.append("\t");
        report.append(item.getPrice().abs().formattedString());
        report.append("\n");
      }

      if(transaction.getAcctCode().equals(Transaction.CODE_CREDITACCOUNT) ||
        transaction.getAcctCode().equals(Transaction.CODE_CREATEACCOUNT) ||
        transaction.getAcctCode().equals(Transaction.CODE_CREDITCOMPACCOUNT) ||
        transaction.getAcctCode().equals(Transaction.CODE_CREATECOMPACCOUNT)) {

        totalAdded = totalAdded.plus(transaction.getTotal());
        report.append("Total spent at this visit: $0.00\n\n");
      }
      else if(transaction.getAcctCode().equals(Transaction.CODE_CLOSEACCOUNT) || transaction.getAcctCode().equals(Transaction.CODE_CLOSECOMPACCOUNT)) {
        totalRefunded = totalRefunded.plus(transaction.getTotal());
        report.append("Total spent at this visit: $0.00\n\n");
      }
      else {
        totalSpent = totalSpent.plus(transaction.getTotal());
        report.append("Total spent at this visit: ");
        report.append(transaction.getTotal().formattedString());
        report.append("\n\n");
      }
    }

    //make sure numbers are rounded and formated properly
    report.append("\nTotal visits made: ");
    report.append(transactions.size());
    report.append("\nTotal added to account: ");
    report.append(totalAdded.formattedString());
    report.append("\nTotal spent: ");
    report.append(totalSpent.formattedString());
    report.append("\nTotal refunded: ");
    report.append(totalRefunded.formattedString());
    report.append("\nBalance Remaining: ");
    report.append(customer.getBalance().formattedString());
  }
}
