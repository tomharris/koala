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
import java.math.BigDecimal;

import org.koala.DBase;

public class CustomerReport extends Report {
    private ArrayList<Transaction> transactions;
    private Customer customer;

    public CustomerReport(DBase dbHandle, Customer customer) {
        super(dbHandle);
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
        // transactions = dbHandle.getTransactions(customer, true);
    }

    public final void doReport() {
    		ArrayList<Item> transItems = null;
    		Date currentDay = null;
    		BigDecimal totalSpent = BigDecimal.ZERO;
    		BigDecimal totalAdded = BigDecimal.ZERO;
    		BigDecimal totalRefunded = BigDecimal.ZERO;
    		SimpleDateFormat todayFormat = new SimpleDateFormat("EEE MMM dd yyyy 'at' h:mm a");

    		report.append("Purchase history for customer: ");
    		report.append(customer.getFirstName());
    		report.append(" ");
    		report.append(customer.getLastName());
    		report.append("\n\n");

    		for(Transaction transaction : transactions) {
    			transItems = transaction.getAllItems();
    			currentDay = transaction.getTransactionTime();
    			
    			//print day header from currentDay
    			report.append(todayFormat.format(currentDay));
    			report.append(":\n");

    			for(Item item : transItems) {
    				report.append("\t");
    				report.append(item.getName());
    				report.append("\t");
    				report.append(Report.currencyFormat.format(item.getPrice().abs()));
    				report.append("\n");
    			}

    			if(transaction.getAcctCode().equals(Transaction.CODE_CREDITACCOUNT) ||
    					transaction.getAcctCode().equals(Transaction.CODE_CREDITCOMPACCOUNT)) {
    				totalAdded = totalAdded.add(transaction.getTotal());
    				report.append("Total spent at this visit: $0.00\n\n");
    			}
    			else if(transaction.getAcctCode().equals(Transaction.CODE_CLOSEACCOUNT) ||
    					transaction.getAcctCode().equals(Transaction.CODE_CLOSECOMPACCOUNT)) {
    				totalRefunded = totalRefunded.add(transaction.getTotal());
    				report.append("Total spent at this visit: $0.00\n\n");
    			}
    			else {
    				totalSpent = totalSpent.add(transaction.getTotal());
    				report.append("Total spent at this visit: ");
    				report.append(Report.currencyFormat.format(transaction.getTotal()));
    				report.append("\n\n");
    			}
    		}

    		//make sure numbers are rounded and formated properly
    		report.append("\nTotal visits made: ");
    		report.append(transactions.size());
    		report.append("\nTotal added to accout: ");
    		report.append(Report.currencyFormat.format(totalAdded));
    		report.append("\nTotal spent: ");
    		report.append(Report.currencyFormat.format(totalSpent));
    		report.append("\nTotal refunded: ");
    		report.append(Report.currencyFormat.format(totalRefunded));
    		report.append("\nBalance Remaining: ");
    		report.append(Report.currencyFormat.format(customer.getBalance()));
    }
}
