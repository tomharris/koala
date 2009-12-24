package org.koala.ui;
/*
 * Created on May 28, 2005
 *
 */

/**
 * @author tom
 *
 * TODO
 */

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.*;

import javax.swing.JButton;

import org.koala.model.Customer;
import org.koala.model.CashCustomer;
import org.koala.model.User;

import java.awt.event.ActionEvent;

public class CustomerLookupGUI extends DriverGUI {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

	private javax.swing.JPanel jContentPane = null;

	private JPanel northPanel = null;
	private JPanel centerPanel = null;
	private JLabel customerLabel = null;
	private JComboBox customerComboBox = null;
	private JPanel buttonPanel = null;
	private JButton selectCustomerButton = null;
	private JButton logOutButton = null;
	private JButton cashSaleButton = null;
	private JButton newAccountButton = null;
	private JLabel cashierLabel = null;
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getNorthPanel() {
		if (northPanel == null) {
			cashierLabel = new JLabel();
			northPanel = new JPanel();
			northPanel.setPreferredSize(new java.awt.Dimension(10,60));
			cashierLabel.setText("Cashier: " + currentUser.getUserName());
			cashierLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
			northPanel.add(cashierLabel, null);
		}
		return northPanel;
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			customerLabel = new JLabel();
			centerPanel = new JPanel();
			customerLabel.setText("Customer Name");
			customerLabel.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			centerPanel.add(customerLabel, null);
			centerPanel.add(getCustomerComboBox(), null);
		}
		return centerPanel;
	}
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getCustomerComboBox() {
		if (customerComboBox == null) {
	        if(currentUser == null)
	            customerComboBox = new JComboBox();
	        else
	            customerComboBox = new JComboBox(Customer.findAll().toArray());
		}
		customerComboBox.setPreferredSize(new java.awt.Dimension(200,20));
		return customerComboBox;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel(new GridLayout(1,0));
			buttonPanel.add(getSelectCustomerButton(), null);
			buttonPanel.add(getCashSaleButton(), null);
			buttonPanel.add(getNewAccountButton(), null);
			buttonPanel.add(getLogOutButton(), null);
		}
		return buttonPanel;
	}
	/**
	 * This method initializes selectCustomer	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getSelectCustomerButton() {
		if (selectCustomerButton == null) {
			selectCustomerButton = new JButton();
			selectCustomerButton.setFont(BUTTON_TEXT_FONT);
			selectCustomerButton.setText("Select");
			selectCustomerButton.addActionListener(new java.awt.event.ActionListener() {
			    public void actionPerformed(ActionEvent evt) {
                selectCustomerButtonActionPerformed(evt);
			    }
			});
		}
		return selectCustomerButton;
	}

	private void selectCustomerButtonActionPerformed(ActionEvent evt) {
	    //get the customer again to be sure it is in a current state
        currentCustomer = Customer.find(((Customer)customerComboBox.getSelectedItem()).getId());

	    DriverGUI.nextGui(new CashierGUI());
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getLogOutButton() {
		if (logOutButton == null) {
			logOutButton = new JButton();
			logOutButton.setFont(BUTTON_TEXT_FONT);
			if(currentUser.getLevel() > User.CASHIER)
				logOutButton.setText("Back");
			else
				logOutButton.setText("Log Out");
			logOutButton.addActionListener(new java.awt.event.ActionListener() {
			    public void actionPerformed(ActionEvent evt) {
			    		DriverGUI.backGui();
			    }
			});
		}
		return logOutButton;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCashSaleButton() {
		if (cashSaleButton == null) {
			cashSaleButton = new JButton();
			cashSaleButton.setFont(BUTTON_TEXT_FONT);
			cashSaleButton.setText("Cash");
			cashSaleButton.addActionListener(new java.awt.event.ActionListener() {
			    public void actionPerformed(ActionEvent evt) {
			        cashSaleButtonActionPerformed(evt);
				    }
				});
		}
		return cashSaleButton;
	}

	private void cashSaleButtonActionPerformed(ActionEvent evt) {
	    currentCustomer = new CashCustomer(); //this means cash
	    DriverGUI.nextGui(new CashierGUI());
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getNewAccountButton() {
		if (newAccountButton == null) {
			newAccountButton = new JButton();
			newAccountButton.setFont(BUTTON_TEXT_FONT);
			newAccountButton.setText("New Account");
			newAccountButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                DriverGUI.nextGui(new AddAccountGUI());
	            }
	        });
		}
		return newAccountButton;
	}

   	public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
           public void run() {
                new CustomerLookupGUI().setVisible(true);
            }
        });
    }
	/**
	 * This is the default constructor
	 */
	public CustomerLookupGUI() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(535, 190);
		this.setContentPane(getJContentPane());
		this.setTitle(APP_NAME);
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}

		return jContentPane;
	}
}