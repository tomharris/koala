package org.koala.model;
/*
 * Created on Jun 19, 2005
 *
 */

/**
 * @author tom
 *
 */

import java.sql.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

import org.koala.DBase;

public class FinancialReport extends Report {
	private ArrayList<Transaction> transactionsBundle = null;

    public FinancialReport(DBase dbHandle) {
        super(dbHandle);
        loadResources();
    }

	protected void finalize() {
		super.finalize();
		transactionsBundle.clear();
		transactionsBundle = null;
	}

	public boolean isPrintable() {
		return true;
	}

    //giving it a db should be safe as we are being called from a user 
    public final void doReport() {
        //Transaction currentTrans;
		Date currentDay = null, yesterDay = null;
		SimpleDateFormat todayFormat = new SimpleDateFormat("EEE MMM dd yyyy");

        //for each days total
		int accountsNumber = 0;
		BigDecimal cashForAccounts = BigDecimal.ZERO;
		BigDecimal cashSales = BigDecimal.ZERO;
		BigDecimal accountSales = BigDecimal.ZERO;
		BigDecimal accountRefunds =BigDecimal.ZERO;

        //for the weeks total
        int accountsNumberTotal = 0;
        BigDecimal cashForAccountsTotal = BigDecimal.ZERO;
        BigDecimal cashSalesTotal = BigDecimal.ZERO;
        BigDecimal accountSalesTotal = BigDecimal.ZERO;
        BigDecimal accountRefundsTotal = BigDecimal.ZERO;

        report.append("Financial Report for CDL SnackShop\n\n");
        report.append("Daily Sales:\n");

        for(Transaction currentTrans : transactionsBundle) {
        		yesterDay = currentDay;
        		currentDay = currentTrans.getTransactionTime(); //sql date

        		if(yesterDay == null) {
        			//print day header for the first day
        			report.append("\n");
        			report.append(todayFormat.format(currentDay));
        			report.append(":\n");
        		}

        		//print footer
        		if(yesterDay != null && currentDay.compareTo(yesterDay) > 0) {
    				//print totals from yesterDay
        			report.append("\tNumber of accounts created: ");
        			report.append(accountsNumber);
        			report.append("\n\tCash collected for account creation: ");
        			report.append(Report.currencyFormat.format(cashForAccounts));
        			report.append("\n\tIncome from cash sales: ");
        			report.append(Report.currencyFormat.format(cashSales));
        			report.append("\n\tIncome from account sales: ");
        			report.append(Report.currencyFormat.format(accountSales));
        			report.append("\n\tCash Refunded from accounts: ");
        			report.append(Report.currencyFormat.format(accountRefunds));
        			report.append("\n");

    				//zero out totals
    			    accountsNumber = 0;
    			    cashForAccounts = BigDecimal.ZERO;
    			    cashSales = BigDecimal.ZERO;
    			    accountSales = BigDecimal.ZERO;
    			    accountRefunds = BigDecimal.ZERO;
    			    
    			    //print day header from currentDay
    			    report.append("\n");
    			    report.append(todayFormat.format(currentDay));
    			    report.append(":\n");
        		}

        		//add transactiontotals to today
        		// catagorys: cash for accounts, cash sales, account sales,
        		//	account refunds, ...
        		if(currentTrans.getAcctCode().equals(Transaction.CODE_CASH)) {
        		    cashSales = cashSales.add(currentTrans.getTotal());
        		    cashSalesTotal = cashSalesTotal.add(currentTrans.getTotal());
        		}
        		else if(currentTrans.getAcctCode().equals(Transaction.CODE_DEBITACCOUNT)) {
        		    accountSales = accountSales.add(currentTrans.getTotal());
        		    accountSalesTotal = accountSalesTotal.add(currentTrans.getTotal());
        		}
        		else if(currentTrans.getAcctCode().equals(Transaction.CODE_CLOSEACCOUNT)) {
        		    accountRefunds = accountRefunds.add(currentTrans.getTotal());
        		    accountRefundsTotal = accountRefundsTotal.add(currentTrans.getTotal());
        		}
        		else if(currentTrans.getAcctCode().equals(Transaction.CODE_CREDITACCOUNT)) {
        			//this works because a new account will be the only item in a transaction
        			if(dbHandle.getTransactionItems(currentTrans.getTransactionID()).get(0).getSku().equals(Item.NEW_ACCOUNT_SKU)) {
        				accountsNumber++;
        				accountsNumberTotal++;
        			}

        		    cashForAccounts = cashForAccounts.add(currentTrans.getTotal());
        		    cashForAccountsTotal = cashForAccountsTotal.add(currentTrans.getTotal());
        		}
        }

        //print totals for the last day
        report.append("\tNumber of accounts created: ");
        report.append(accountsNumber);
        report.append("\n\tCash collected for account creation: ");
        report.append(Report.currencyFormat.format(cashForAccounts));
        report.append("\n\tIncome from cash sales: ");
        report.append(Report.currencyFormat.format(cashSales));
        report.append("\n\tIncome from account sales: ");
        report.append(Report.currencyFormat.format(accountSales));
        report.append("\n\tCash Refunded from accounts: ");
        report.append(Report.currencyFormat.format(accountRefunds));
        report.append("\n");

        //print end of period totals
        report.append("\n\nEnd of period totals:\n");
        report.append("\tNumber of accounts created: ");
        report.append(accountsNumberTotal);
        report.append("\n\tCash collected for account creation: ");
        report.append(Report.currencyFormat.format(cashForAccountsTotal));
        report.append("\n\tIncome from cash sales: ");
        report.append(Report.currencyFormat.format(cashSalesTotal));
        report.append("\n\tIncome from account sales: ");
        report.append(Report.currencyFormat.format(accountSalesTotal));
        report.append("\n\tCash Refunded from accounts: ");
        report.append(Report.currencyFormat.format(accountRefundsTotal));
        report.append("\n");
    }

    protected final void loadResources() {
        //load stuff from dbase:
        // all transactions

        transactionsBundle = dbHandle.getTransactions(null, false);
    }
}
