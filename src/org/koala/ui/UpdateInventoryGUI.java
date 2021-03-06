package org.koala.ui;
/*
 * Created on Jun 11, 2005
 */

/**
 * @author tom
 *
 */


import javax.swing.*;

import org.koala.Money;
import org.koala.model.ForRent;
import org.koala.model.ForSale;
import org.koala.model.InventoryItem;
import org.koala.exception.EntryAlreadyExistsException;

import java.awt.*;

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
  private JTextField quantityTextField = null;
  private JPanel namePanel = null;
  private JLabel nameLabel = null;
  private JTextField nameTextField = null;
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
  private JPanel quantityLabelPanel = null;
  private JLabel quantityLabel = null;
  private JLabel quantityAmountLabel = null;

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
      centerPanel.add(getNamePanel(), null);
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

    InventoryItem invItem = null;
    invItem = InventoryItem.findBySku(skuTextField.getText());

    //fill in the textboxes
    if(invItem != null && !invItem.getSku().equals("")) {
      quantityTextField.setText(new Integer(invItem.getQuantity()).toString());
      quantityLabel.setText(quantityDefaultLabel + "(current: " + new Integer(invItem.getQuantity()).toString() + ")");
      nameTextField.setText(invItem.getName());
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
      quantityPanel.add(getQuantityAmountPanel(), null);
    }
    return quantityPanel;
  }
  /**
   * This method initializes quantityTextField
   *
   * @return javax.swing.JTextField
   */
  private JTextField getQuantityTextField() {
    if (quantityTextField == null) {
      quantityTextField = new JTextField();
      quantityTextField.setPreferredSize(TEXTAREA_SIZE);
    }
    return quantityTextField;
  }
  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getNamePanel() {
    if (namePanel == null) {
      nameLabel = new JLabel();
      namePanel = new JPanel();
      nameLabel.setText("Name: ");
      nameLabel.setPreferredSize(new java.awt.Dimension(85,15));
      namePanel.add(nameLabel, null);
      namePanel.add(getNameTextField(), null);
    }
    return namePanel;
  }
  /**
   * This method initializes nameTextField
   *
   * @return javax.swing.JTextField
   */
  private JTextField getNameTextField() {
    if (nameTextField == null) {
      nameTextField = new JTextField();
      nameTextField.setPreferredSize(TEXTAREA_SIZE);
    }
    return nameTextField;
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
      updateButton.setText("Save");
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
    if(skuTextField.getText().equals("")) {
      return;
    }

    InventoryItem item = InventoryItem.findBySku(skuTextField.getText().trim());

    if(item == null) {
      item = new ForSale();
    }

    item.setSku(skuTextField.getText());
    item.setName(nameTextField.getText());

    if(unlimitedCheckBox.isSelected()) {
      item.setUnlimited(unlimitedCheckBox.isSelected());
      item.setQuantity(0);
    }
    else {
      item.setQuantity(Integer.parseInt(quantityTextField.getText()));
    }

    try {
      item.setPrice(new Money(priceTextField.getText().trim()));
    }
    catch(NumberFormatException e) {
      //log later?
      priceTextField.setText("");
      return;
    }

    try {
      item.setTaxRate(new Money(taxTextField.getText().trim()));
    }
    catch(NumberFormatException e) {
      item.setTaxRate(Money.ZERO);
    }

    try {
      item.save();
    }
    catch (EntryAlreadyExistsException e) {
      DriverGUI.printError(e);
    }

    clearFields();

    skuTextField.requestFocus();
  }

  private void clearFields() {
    skuTextField.setText("");
    quantityTextField.setText("");
    quantityTextField.setEnabled(true);
    nameTextField.setText("");
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
      unlimitedLabel.setText("Unlimited: ");
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
    quantityTextField.setEnabled(!unlimitedCheckBox.isSelected());
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

    InventoryItem item = InventoryItem.findBySku(skuTextField.getText());
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
  private JPanel getQuantityAmountPanel() {
    if (quantityUpdatePanel == null) {
      quantityAmountLabel = new JLabel();
      quantityAmountLabel.setText("Amount: ");
      quantityUpdatePanel = new JPanel();
      quantityUpdatePanel.add(quantityAmountLabel, null);
      quantityUpdatePanel.add(getQuantityTextField(), null);
    }
    return quantityUpdatePanel;
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
