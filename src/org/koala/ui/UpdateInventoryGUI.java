package org.koala.ui;
/*
 * Created on Jun 11, 2005
 */

/**
 * @author tom
 *
 */


import javax.swing.*;

import org.koala.model.ForRent;
import org.koala.model.ForSale;
import org.koala.model.Item;
import org.koala.exception.EntryAlreadyExistsException;
import org.koala.exception.ItemNotFoundException;

import java.awt.*;
import java.math.BigDecimal;

public class UpdateInventoryGUI extends DriverGUI {
	private static final String quantityDefaultLabel = "Quantity: ";
	
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

	private JPanel jContentPane = null;

	private JPanel westPanel = null;
	private JPanel eastPanel = null;
	private JPanel buttonPanel = null;
	private JPanel centerPanel = null;
	private JPanel skuPanel = null;
	private JLabel skuLabel = null;
	private JTextField skuTextField = null;
	private JPanel quantityPanel = null;
	private JTextField quantityUpdateTextField = null;
	private JPanel descPanel = null;
	private JLabel descLabel = null;
	private JTextField descTextField = null;
	private JPanel pricePanel = null;
	private JPanel taxPanel = null;
	private JLabel priceLabel = null;
	private JLabel taxLabel = null;
	private JTextField priceTextField = null;
	private JTextField taxTextField = null;
	private JButton updateButton = null;
	private JPanel rentablePanel = null;
	private JLabel rentableLabel = null;
	private JCheckBox rentableCheckBox = null;
	private JPanel unlimitedPanel = null;
	private JLabel unlimitedLabel = null;
	private JCheckBox unlimitedCheckBox = null;
	private JButton cancelButton = null;
	private JButton removeButton = null;
	private JPanel quantityUpdatePanel = null;
	private JPanel quantityAddPanel = null;
	private JTextField quantityAddTextField = null;
	private JPanel quantityLabelPanel = null;
	private JLabel quantityLabel = null;
	private JRadioButton invAddRadio = null;
	private JRadioButton invUpdateRadio = null;
	private ButtonGroup invRadioGroup;
	private JLabel quantityAddLabel = null;
	private JLabel quantityUpdateLabel = null;

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getWestPanel() {
		if (westPanel == null) {
			westPanel = new JPanel();
			westPanel.setPreferredSize(new java.awt.Dimension(30,10));
		}
		return westPanel;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getEastPanel() {
		if (eastPanel == null) {
			eastPanel = new JPanel();
			eastPanel.setPreferredSize(new java.awt.Dimension(30,10));
		}
		return eastPanel;
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel(new GridLayout(1,0));
			buttonPanel.add(getUpdateButton(), null);
			buttonPanel.add(getRemoveButton(), null);
			buttonPanel.add(new JPanel(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}
	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			centerPanel.add(getSkuPanel(), null);
			centerPanel.add(getQuantityLabelPanel(), null);
			centerPanel.add(getUnlimitedPanel(), null);
			centerPanel.add(getQuantityPanel(), null);
			centerPanel.add(getDescPanel(), null);
			centerPanel.add(getPricePanel(), null);
			centerPanel.add(getTaxPanel(), null);
			centerPanel.add(getRentablePanel(), null);
		}
		return centerPanel;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getSkuPanel() {
		if (skuPanel == null) {
			skuLabel = new JLabel();
			skuPanel = new JPanel();
			skuLabel.setText("Sku: ");
			skuLabel.setPreferredSize(new java.awt.Dimension(80,15));
			skuPanel.add(skuLabel, null);
			skuPanel.add(getSkuTextField(), null);
		}
		return skuPanel;
	}
	/**
	 * This method initializes quantityTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getSkuTextField() {
		if (skuTextField == null) {
			skuTextField = new JTextField();
			skuTextField.setPreferredSize(TEXTAREA_SIZE);
			skuTextField.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				    skuTextFieldActionPerformed(evt);
				}
			});
		}
		return skuTextField;
	}

	private void skuTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
	    if(skuTextField.getText().equals("")) {
	    		quantityLabel.setText(quantityDefaultLabel);
	    		clearFields();
	    		return;
	    }

	    Item invItem = null;
        invItem = Item.findBySku(skuTextField.getText());

	    //fill in the textboxes
	    if(invItem != null && !invItem.getSku().equals("")) {
	    	quantityAddTextField.setText("");
	        quantityUpdateTextField.setText(new Integer(invItem.getQuantity()).toString());
	        quantityLabel.setText(quantityDefaultLabel + "(current: " +
	        		new Integer(invItem.getQuantity()).toString() + ")");
	        descTextField.setText(invItem.getName());
	        priceTextField.setText(invItem.getPrice().toString());
	        taxTextField.setText(invItem.getTaxRate().toString());
	        unlimitedCheckBox.setSelected(invItem.getUnlimited());
	        rentableCheckBox.setSelected(invItem instanceof ForRent);

	        unlimitedCheckBoxActionPerformed(null);
	    }
	    else {
	    	quantityLabel.setText(quantityDefaultLabel);	
	        String newSku = skuTextField.getText();
	        clearFields();
	        skuTextField.setText(newSku);
	    }
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getQuantityPanel() {
		if (quantityPanel == null) {
			FlowLayout flowLayout1 = new FlowLayout(FlowLayout.LEFT);
			flowLayout1.setVgap(0);
			quantityPanel = new JPanel();
			quantityPanel.setPreferredSize(new java.awt.Dimension(275,75));
			quantityPanel.setLayout(flowLayout1);
			quantityPanel.add(getQuantityAddPanel(), null);
			quantityPanel.add(getQuantityUpdatePanel(), null);
			invRadioGroup = new ButtonGroup();
			invRadioGroup.add(invAddRadio);
			invRadioGroup.add(invUpdateRadio);
		}
		return quantityPanel;
	}
	/**
	 * This method initializes quantityTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getQuantityTextField() {
		if (quantityUpdateTextField == null) {
			quantityUpdateTextField = new JTextField();
			quantityUpdateTextField.setEnabled(false);
			quantityUpdateTextField.setPreferredSize(TEXTAREA_SIZE);
		}
		return quantityUpdateTextField;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getDescPanel() {
		if (descPanel == null) {
			descLabel = new JLabel();
			descPanel = new JPanel();
			descLabel.setText("Description: ");
			descLabel.setPreferredSize(new java.awt.Dimension(85,15));
			descPanel.add(descLabel, null);
			descPanel.add(getDescTextField(), null);
		}
		return descPanel;
	}
	/**
	 * This method initializes descTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getDescTextField() {
		if (descTextField == null) {
			descTextField = new JTextField();
			descTextField.setPreferredSize(TEXTAREA_SIZE);
		}
		return descTextField;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPricePanel() {
		if (pricePanel == null) {
			priceLabel = new JLabel();
			pricePanel = new JPanel();
			priceLabel.setText("Price: ");
			priceLabel.setPreferredSize(new java.awt.Dimension(80,15));
			pricePanel.add(priceLabel, null);
			pricePanel.add(getPriceTextField(), null);
		}
		return pricePanel;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getTaxPanel() {
		if (taxPanel == null) {
			taxLabel = new JLabel();
			taxPanel = new JPanel();
			taxLabel.setText("Tax Rate: ");
			taxLabel.setPreferredSize(new java.awt.Dimension(80,15));
			taxPanel.add(taxLabel, null);
			taxPanel.add(getTaxTextField(), null);
		}
		return taxPanel;
	}
	/**
	 * This method initializes priceTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getPriceTextField() {
		if (priceTextField == null) {
			priceTextField = new JTextField();
			priceTextField.setPreferredSize(TEXTAREA_SIZE);
		}
		return priceTextField;
	}
	/**
	 * This method initializes taxTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getTaxTextField() {
		if (taxTextField == null) {
			taxTextField = new JTextField();
			taxTextField.setPreferredSize(TEXTAREA_SIZE);
		}
		return taxTextField;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getUpdateButton() {
		if (updateButton == null) {
			updateButton = new JButton();
			updateButton.setText("Update");
			updateButton.setFont(BUTTON_TEXT_FONT);
			updateButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				    updateButtonActionPerformed(evt);
				}
			});
		}
		return updateButton;
	}

	private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if(skuTextField.getText().equals(""))
			return;

	    //update db
	    Item newItem = null;

	    try {
	    		Item currentItem = Item.findBySku(skuTextField.getText().trim());
	    	
	    		if(currentItem == null) {
	    			//check that quantity isnt empty
	    			invAddRadio.setSelected(true);
	    			int quant = Integer.parseInt(quantityAddTextField.getText());

	    			BigDecimal price = null;
	    			BigDecimal tax = null;
	    			try {
	    				price = new BigDecimal(priceTextField.getText().trim());
	    				tax = new BigDecimal(taxTextField.getText().trim());
	    		    }
	    		    catch(NumberFormatException e) {
	    		    	//log later?
	    		    	priceTextField.setText("");
	    		    	taxTextField.setText("");
	    		    	return;
	    		    }

	    			//make inv item
	    			newItem = new ForSale(skuTextField.getText(), descTextField.getText(),
	    	                quant, price, tax, unlimitedCheckBox.isSelected());

	    			if(!quantityAddTextField.getText().equals("") && !(quant <= 0)) {
		    			//build transaction item
		    			currentUser.addSpecialItem(
		    					new Item(Item.INVENTORYADD_SKU, newItem.getSku(), quant, BigDecimal.ZERO,
		    							BigDecimal.ZERO, false), null);
		    			currentUser.doTransaction(); //somehow this triggers two transactions FIX
	
		    			//add to inv
		    			try {
		    			  newItem.save();
		    			}
		    			catch (EntryAlreadyExistsException e) {
        	        DriverGUI.printError(e);
        	    }
	    			}
	    		}
	    		else {
	    			if(invAddRadio.isSelected()) {
	    				int quant = 0;
	    				if(quantityAddTextField.getText().equals(""))
	    					quant = currentItem.getQuantity();
	    				else
	    					quant = Integer.parseInt(quantityAddTextField.getText());

	    				BigDecimal price = null;
		    			BigDecimal tax = null;
		    			try {
		    				price = new BigDecimal(priceTextField.getText().trim());
		    				tax = new BigDecimal(taxTextField.getText().trim());
		    		    }
		    		    catch(NumberFormatException e) {
		    		    	//log later?
		    		    	priceTextField.setText("");
		    		    	taxTextField.setText("");
		    		    	return;
		    		    }

	    				//make inv item
		    			newItem = new ForSale(skuTextField.getText(), descTextField.getText(),
		    	                quant + currentItem.getQuantity(), price, tax,
		    	                unlimitedCheckBox.isSelected());

		    			if(!quantityAddTextField.getText().equals("") && !(quant <= 0)) {
			    			//build transaction item
			    			currentUser.addSpecialItem(
			    					new Item(Item.INVENTORYADD_SKU, newItem.getSku(), quant, BigDecimal.ZERO,
			    							BigDecimal.ZERO, false), null);
			    			currentUser.doTransaction();
		    			}
	    			}
	    			else {
	    				int quant = 0;
	    				if(!quantityUpdateTextField.isEnabled() || quantityUpdateTextField.getText().equals(""))
	    					quant = currentItem.getQuantity();
	    				else
	    					quant = Integer.parseInt(quantityUpdateTextField.getText());

	    				BigDecimal price = null;
		    			BigDecimal tax = null;
		    			try {
		    				price = new BigDecimal(priceTextField.getText().trim());
		    				tax = new BigDecimal(taxTextField.getText().trim());
		    		    }
		    		    catch(NumberFormatException e) {
		    		    	//log later?
		    		    	priceTextField.setText("");
		    		    	taxTextField.setText("");
		    		    	return;
		    		    }

	    				//make inv item
		    			newItem = new ForSale(skuTextField.getText(), descTextField.getText(),
		    	                quant, price, tax, unlimitedCheckBox.isSelected());

		    			if(!quantityUpdateTextField.getText().equals("") && !(quant <= 0)) {
			    			//build transaction item
			    			currentUser.addSpecialItem(
			    					new Item(Item.INVENTORYCORRECTION_SKU, newItem.getSku(),
			    							quant - currentItem.getQuantity(), BigDecimal.ZERO, BigDecimal.ZERO,
			    							false), null);
			    			currentUser.doTransaction();
		    			}
	    			}

            try {
              newItem.save();
            }
            catch (EntryAlreadyExistsException e) {
      	        DriverGUI.printError(e);
      	    }
	    		}
	    }
	    catch (ItemNotFoundException e) {
	    		DriverGUI.printError(e);
	    }

	    clearFields();

	    skuTextField.requestFocus();
	}

	private void clearFields() {
	    skuTextField.setText("");
	    quantityUpdateTextField.setText("");
	    quantityAddTextField.setText("");
	    descTextField.setText("");
	    priceTextField.setText("");
	    taxTextField.setText("");
	    unlimitedCheckBox.setSelected(false);
	    rentableCheckBox.setSelected(false);
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getRentablePanel() {
		if (rentablePanel == null) {
			rentableLabel = new JLabel();
			rentablePanel = new JPanel();
			rentableLabel.setText("Rentable: ");
			rentableLabel.setEnabled(false);
			rentableLabel.setVisible(false);
			rentablePanel.add(rentableLabel, null);
			rentablePanel.add(getRentableCheckBox(), null);
		}
		return rentablePanel;
	}
	/**
	 * This method initializes rentableCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getRentableCheckBox() {
		if (rentableCheckBox == null) {
			rentableCheckBox = new JCheckBox();
			rentableCheckBox.setEnabled(false);
			rentableCheckBox.setVisible(false);
		}
		return rentableCheckBox;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getUnlimitedPanel() {
		if (unlimitedPanel == null) {
			unlimitedLabel = new JLabel();
			unlimitedPanel = new JPanel();
			unlimitedLabel.setText("   Unlimited: ");
			unlimitedPanel.add(unlimitedLabel, null);
			unlimitedPanel.add(getUnlimitedCheckBox(), null);
		}
		return unlimitedPanel;
	}
	/**
	 * This method initializes unlimitedCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getUnlimitedCheckBox() {
		if (unlimitedCheckBox == null) {
			unlimitedCheckBox = new JCheckBox();
			unlimitedCheckBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					unlimitedCheckBoxActionPerformed(evt);
				}
			});
		}
		return unlimitedCheckBox;
	}

	private void unlimitedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		invAddRadio.setEnabled(!unlimitedCheckBox.isSelected());
		invUpdateRadio.setEnabled(!unlimitedCheckBox.isSelected());
		quantityAddTextField.setEnabled(!unlimitedCheckBox.isSelected() &&
				invAddRadio.isSelected());
		quantityUpdateTextField.setEnabled(!unlimitedCheckBox.isSelected() &&
				invUpdateRadio.isSelected());
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Done");
			cancelButton.setFont(BUTTON_TEXT_FONT);
			cancelButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent evt) { 
					DriverGUI.backGui();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove");
			removeButton.setFont(BUTTON_TEXT_FONT);
			removeButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					removeButtonActionPerformed(evt);
				}
			});
		}
		return removeButton;
	}

	private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if(skuTextField.getText().equals("")) {
		  return;
		}
		
		Item item = Item.findBySku(skuTextField.getText());
    if(item != null) {
      item.destroy();
    }

		clearFields();
		skuTextField.requestFocus();
	}

     /**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getQuantityUpdatePanel() {
		if (quantityUpdatePanel == null) {
			quantityUpdateLabel = new JLabel();
			quantityUpdateLabel.setText("Update Inventory: ");
			quantityUpdatePanel = new JPanel();
			quantityUpdatePanel.add(getInvUpdateRadio(), null);
			quantityUpdatePanel.add(quantityUpdateLabel, null);
			quantityUpdatePanel.add(getQuantityTextField(), null);
		}
		return quantityUpdatePanel;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getQuantityAddPanel() {
		if (quantityAddPanel == null) {
			quantityAddLabel = new JLabel();
			quantityAddLabel.setText("Add to Inventory: ");
			quantityAddPanel = new JPanel();
			quantityAddPanel.add(getInvAddRadio(), null);
			quantityAddPanel.add(quantityAddLabel, null);
			quantityAddPanel.add(getQuantityAddTextField(), null);
		}
		return quantityAddPanel;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getQuantityAddTextField() {
		if (quantityAddTextField == null) {
			quantityAddTextField = new JTextField();
			quantityAddTextField.setPreferredSize(TEXTAREA_SIZE);
		}
		return quantityAddTextField;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getQuantityLabelPanel() {
		if (quantityLabelPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setVgap(0);
			quantityLabel = new JLabel();
			quantityLabel.setPreferredSize(new Dimension(250, 15));
			quantityLabel.setText("Quantity: ");
			quantityLabelPanel = new JPanel();
			quantityLabelPanel.setLayout(flowLayout);
			quantityLabelPanel.add(quantityLabel, null);
		}
		return quantityLabelPanel;
	}
	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getInvAddRadio() {
		if (invAddRadio == null) {
			invAddRadio = new JRadioButton();
			invAddRadio.setSelected(true);
			invAddRadio.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					quantityAddTextField.setEnabled(invAddRadio.isSelected());
				}
			});
		}
		return invAddRadio;
	}
	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getInvUpdateRadio() {
		if (invUpdateRadio == null) {
			invUpdateRadio = new JRadioButton();
			invUpdateRadio.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					quantityUpdateTextField.setEnabled(invUpdateRadio.isSelected());
				}
			});
		}
		return invUpdateRadio;
	}
	public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UpdateInventoryGUI().setVisible(true);
            }
        });
    }
	/**
	 * This is the default constructor
	 */
	public UpdateInventoryGUI() {
		super();
		initialize();
		this.setTitle(APP_NAME + ": Update Inventory");
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(400,375);
		this.setContentPane(getJContentPane());
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getWestPanel(), java.awt.BorderLayout.WEST);
			jContentPane.add(getEastPanel(), java.awt.BorderLayout.EAST);
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
}
