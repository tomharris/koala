package org.koala.ui;
/*
 * Created on Jun 25, 2005
 *
 */

/**
 * @author tom
 *
 */


import java.awt.GridLayout;
import java.math.BigDecimal;

import javax.swing.*;

import org.koala.exception.ItemNotFoundException;

public class AddToAccountGUI extends DriverGUI {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

	private JPanel jContentPane = null;
	private JPanel amountPanel = null;
	private JLabel amountLabel = null;
	private JTextField amountTextField = null;
	private JPanel titlePanel = null;
	private JLabel titleLabel = null;
	private JPanel buttonPanel = null;
	private JButton addButton = null;
	private JButton cancelButton = null;
	/**
	 * This is the default constructor
	 */
	public AddToAccountGUI() {
		super();
		initialize();
		this.setTitle(APP_NAME);
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300,150);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel(new GridLayout(1,0));
			buttonPanel.add(getAddButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add");
			addButton.setFont(BUTTON_TEXT_FONT);
			addButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                addButtonActionPerformed(evt);
	            }
	        });
		}
		return addButton;
	}

	private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
	    if(amountTextField.getText().equals("")) {
	    	amountTextField.requestFocus();
	        return;
	    }

	    BigDecimal addAmount = null;
		try {
			addAmount = new BigDecimal(amountTextField.getText().trim());
	    }
	    catch(NumberFormatException e) {
	    	//log later?
	    	amountTextField.setText("");
	    	return;
	    }

	    if(addAmount.compareTo(BigDecimal.ZERO) < 0) {
	    	amountTextField.setText("");
	    	amountTextField.requestFocus();
	    	return;
	    }

	    currentCustomer.setBalance(currentCustomer.getBalance().add(addAmount));

	    try {
	        currentUser.updateCustomer(currentCustomer);
	        currentCustomer = currentUser.getCustomer(currentCustomer.getId()); //refresh
	    }
	    catch (ItemNotFoundException e) {
	        DriverGUI.printError(e);
	    }

	    DriverGUI.backGui();
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.setFont(BUTTON_TEXT_FONT);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            		DriverGUI.backGui();
	            }
	        });
		}
		return cancelButton;
	}

   	public static void main(String[] args) {
  		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddToAccountGUI().setVisible(true);
            }
        });
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getAmountPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getTitlePanel(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getAmountPanel() {
		if (amountPanel == null) {
			amountLabel = new JLabel();
			amountPanel = new JPanel();
			amountLabel.setText("Amount to add: ");
			amountPanel.add(amountLabel, null);
			amountPanel.add(getAmountTextField(), null);
		}
		return amountPanel;
	}
	/**
	 * This method initializes amountTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getAmountTextField() {
		if (amountTextField == null) {
			amountTextField = new JTextField();
			amountTextField.setPreferredSize(TEXTAREA_SIZE);
		}
		return amountTextField;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titleLabel = new JLabel();
			titlePanel = new JPanel();
			titleLabel.setText("Add to Customer Account");
			titleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			titlePanel.add(titleLabel, null);
		}
		return titlePanel;
	}
   }
