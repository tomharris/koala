package org.koala.ui;
/*
 * Created on Jun 21, 2005
 *
 */

/**
 * @author tom
 *
 */


import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

import org.koala.Money;
import org.koala.model.Customer;
import org.koala.exception.EntryAlreadyExistsException;
import org.koala.ui.widget.NoteTextArea;

public class AccountMaintenanceGUI extends DriverGUI {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;

  private JPanel jContentPane = null;
  private JPanel westPanel = null;
  private JPanel eastPanel = null;
  private JPanel southPanel = null;
  private JButton backButton = null;
  private JPanel northPanel = null;
  private JPanel centerPanel = null;
  private JLabel firstNameLabel = null;
  private JTextField firstNameTextField = null;
  private JLabel lastNameLabel = null;
  private JTextField lastNameTextField = null;
  private JLabel balanceLabel = null;
  private JTextField balanceTextField = null;
  private JLabel compLabel = null;
  private JCheckBox compCheckBox = null;
  private JLabel addTitleLabel = null;
  private JComboBox customerComboBox = null;
  private JPanel customerBoxPanel = null;
  private JButton updateButton = null;
  private JButton deleteButton = null;
  private JPanel compPanel = null;
  private JPanel buttonPanel = null;
  private JPanel firstNamePanel = null;
  private JPanel lastNamePanel = null;
  private JPanel balancePanel = null;
  private JPanel notePanel = null;
  private JLabel noteLabel = null;
  private NoteTextArea noteTextArea = null;

  private JPanel renewPanel = null;

  private JLabel renewLabel = null;

  private JCheckBox renewCheckBox = null;
  /**
   * This is the default constructor
   */
  public AccountMaintenanceGUI() {
    super();
    initialize();
    this.setTitle(APP_NAME + "Edit Acounts");
  }

  private void clearFields() {
    firstNameTextField.setText("");
    lastNameTextField.setText("");
    balanceTextField.setText("");
    noteTextArea.setText("");
    compCheckBox.setSelected(false);
    compCheckBox.setEnabled(true);
    renewCheckBox.setSelected(false);
  }

  /**
   * This method initializes jTextField
   *
   * @return javax.swing.JTextField
   */
  private JTextField getFirstNameTextField() {
    if (firstNameTextField == null) {
      firstNameTextField = new JTextField();
      firstNameTextField.setPreferredSize(TEXTAREA_SIZE);
    }
    return firstNameTextField;
  }
  /**
   * This method initializes jTextField1
   *
   * @return javax.swing.JTextField
   */
  private JTextField getLastNameTextField() {
    if (lastNameTextField == null) {
      lastNameTextField = new JTextField();
      lastNameTextField.setPreferredSize(TEXTAREA_SIZE);
    }
    return lastNameTextField;
  }
  /**
   * This method initializes jTextField2
   *
   * @return javax.swing.JTextField
   */
  private JTextField getBalanceTextField() {
    if (balanceTextField == null) {
      balanceTextField = new JTextField();
      balanceTextField.setPreferredSize(TEXTAREA_SIZE);
    }
    return balanceTextField;
  }

  /**
   * This method initializes jCheckBox
   *
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getCompCheckBox() {
    if (compCheckBox == null) {
      compCheckBox = new JCheckBox();
    }
    return compCheckBox;
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
      else {
        customerComboBox = new JComboBox(Customer.findAll().toArray());
      }

      customerComboBox.insertItemAt("<<NEW CUSTOMER>>", 0);
      customerComboBox.setSelectedIndex(0);
      customerComboBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
          customerComboBoxItemStateChanged(evt);
        }
      });
    }
    return customerComboBox;
  }

  private void customerComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
    if(customerComboBox.getSelectedIndex() == 0) {
      clearFields();
      return;
    }

    Customer selectedCust = (Customer)customerComboBox.getSelectedItem();

    firstNameTextField.setText(selectedCust.getFirstName());
    lastNameTextField.setText(selectedCust.getLastName());
    balanceTextField.setText(selectedCust.getBalance().toString());
    noteTextArea.setText(selectedCust.getNote());
    renewCheckBox.setSelected(selectedCust.getRenewAmount().isPlus());
    compCheckBox.setSelected(selectedCust.isComplementary() || renewCheckBox.isSelected());
    compCheckBox.setEnabled(!renewCheckBox.isSelected());
  }

  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getCustomerBoxPanel() {
    if (customerBoxPanel == null) {
      customerBoxPanel = new JPanel();
      customerBoxPanel.setPreferredSize(new java.awt.Dimension(175,35));
      customerBoxPanel.add(getCustomerComboBox(), null);
    }
    return customerBoxPanel;
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
    //defaults for new user
    int customerid = 0;

    if(customerComboBox.getSelectedIndex() != 0) { //if modify
      customerid = ((Customer)customerComboBox.getSelectedItem()).getId();
    }

    Money renewAmount = null;
    Money balanceAmount = null;
    try {
      balanceAmount = new Money(balanceTextField.getText().trim());
    }
    catch(NumberFormatException e) {
      //log later?
      balanceTextField.setText("");
      return;
    }

    if(renewCheckBox.isSelected()) {
      renewAmount = balanceAmount;
    }
    else {
      renewAmount = Money.ZERO;
    }

    Customer customer = new Customer();
    customer.setId(customerid);
    customer.setBalance(balanceAmount);
    customer.setLastName(lastNameTextField.getText());
    customer.setFirstName(firstNameTextField.getText());
    customer.setComplementary(compCheckBox.isSelected());
    customer.setRenewAmount(renewAmount);
    customer.setNote(noteTextArea.getText());
    customer.setCreator(currentUser);

    try {
      customer.save();
    }
    catch (EntryAlreadyExistsException e) {
      JOptionPane.showMessageDialog(this,
        "This customer already exists!",
        "Customer exists",
        JOptionPane.PLAIN_MESSAGE
      );
    }

    //update combobox
    if(customerComboBox.getSelectedIndex() != 0) { //updating an existing user
      customerComboBox.removeItem(customerComboBox.getSelectedItem());
    }

    customerComboBox.addItem(customer);
    customerComboBox.repaint();
    customerComboBox.requestFocus();
    customerComboBox.setSelectedIndex(0); //first item; new customer
    clearFields();
  }

  /**
   * This method initializes jButton1
   *
   * @return javax.swing.JButton
   */
  private JButton getDeleteButton() {
    if (deleteButton == null) {
      deleteButton = new JButton();
      deleteButton.setText("Delete");
      deleteButton.setFont(BUTTON_TEXT_FONT);
      deleteButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          deleteButtonActionPerformed(evt);
        }
      });
    }
    return deleteButton;
  }

  private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if(customerComboBox.getSelectedIndex() == 0) {
      return;
    }

    Customer customer = ((Customer)customerComboBox.getSelectedItem());
    customer.destroy();
    customerComboBox.removeItemAt(customerComboBox.getSelectedIndex());
  }

  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getCompPanel() {
    if (compPanel == null) {
      compPanel = new JPanel();
      compLabel = new JLabel();
      compLabel.setText("Complementary: ");
      compLabel.setPreferredSize(new java.awt.Dimension(120,15));
      compPanel.add(compLabel, null);
      compPanel.add(getCompCheckBox(), null);
    }
    return compPanel;
  }
  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new JPanel();
      buttonPanel.add(getUpdateButton(), null);
      buttonPanel.add(getDeleteButton(), null);
    }
    return buttonPanel;
  }
  /**
   * This method initializes jPanel1
   *
   * @return javax.swing.JPanel
   */
  private JPanel getFirstNamePanel() {
    if (firstNamePanel == null) {
      firstNamePanel = new JPanel();
      firstNameLabel = new JLabel();
      firstNameLabel.setText("First Name: ");
      firstNameLabel.setPreferredSize(new java.awt.Dimension(80,15));
      firstNamePanel.add(firstNameLabel, null);
      firstNamePanel.add(getFirstNameTextField(), null);
    }
    return firstNamePanel;
  }
  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getLastNamePanel() {
    if (lastNamePanel == null) {
      lastNamePanel = new JPanel();
      lastNameLabel = new JLabel();
      lastNameLabel.setText("Last Name: ");
      lastNameLabel.setPreferredSize(new java.awt.Dimension(80,15));
      lastNamePanel.add(lastNameLabel, null);
      lastNamePanel.add(getLastNameTextField(), null);
    }
    return lastNamePanel;
  }
  /**
   * This method initializes jPanel1
   *
   * @return javax.swing.JPanel
   */
  private JPanel getBalancePanel() {
    if (balancePanel == null) {
      balancePanel = new JPanel();
      balanceLabel = new JLabel();
      balanceLabel.setText("Balance: ");
      balanceLabel.setPreferredSize(new java.awt.Dimension(80,15));
      balancePanel.add(balanceLabel, null);
      balancePanel.add(getBalanceTextField(), null);
    }
    return balancePanel;
  }
  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getNotePanel() {
    if (notePanel == null) {
      noteLabel = new JLabel();
      notePanel = new JPanel();
      notePanel.setLayout(new BorderLayout());
      noteLabel.setText("Note:");
      notePanel.add(noteLabel, java.awt.BorderLayout.NORTH);
      notePanel.add(getNoteTextArea(), java.awt.BorderLayout.SOUTH);
    }
    return notePanel;
  }
  /**
   * This method initializes jTextArea
   *
   * @return javax.swing.JTextArea
   */
  private NoteTextArea getNoteTextArea() {
    if (noteTextArea == null) {
      noteTextArea = new NoteTextArea();
      noteTextArea.setRows(3);
      noteTextArea.setColumns(18);
    }
    return noteTextArea;
  }

  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getRenewPanel() {
    if (renewPanel == null) {
      renewLabel = new JLabel();
      renewLabel.setText("Renew acount after reset?");
      renewPanel = new JPanel();
      renewPanel.add(renewLabel, null);
      renewPanel.add(getRenewCheckBox(), null);
    }
    return renewPanel;
  }

  /**
   * This method initializes jCheckBox
   *
   * @return javax.swing.JCheckBox
   */
  private JCheckBox getRenewCheckBox() {
    if (renewCheckBox == null) {
      renewCheckBox = new JCheckBox();
      renewCheckBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          doRenewCheckBoxActionPerformed(evt);
        }
      });
    }
    return renewCheckBox;
  }

  private void doRenewCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
    compCheckBox.setSelected(renewCheckBox.isSelected());
    compCheckBox.setEnabled(!renewCheckBox.isSelected());
  }

  public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new AccountMaintenanceGUI().setVisible(true);
      }
    });
  }

  /**
   * This method initializes this
   *
   * @return void
   */
  private void initialize() {
    this.setSize(295,460);
    this.setContentPane(getJContentPane());
  }
  /**
   * This method initializes jContentPane
   *
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if(jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new java.awt.BorderLayout());
      jContentPane.add(getWestPanel(), java.awt.BorderLayout.WEST);
      jContentPane.add(getEastPanel(), java.awt.BorderLayout.EAST);
      jContentPane.add(getSouthPanel(), java.awt.BorderLayout.SOUTH);
      jContentPane.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
      jContentPane.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
    }
    return jContentPane;
  }
  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getWestPanel() {
    if (westPanel == null) {
      westPanel = new JPanel();
      westPanel.setPreferredSize(new java.awt.Dimension(25,0));
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
      eastPanel.setPreferredSize(new java.awt.Dimension(25,0));
    }
    return eastPanel;
  }
  /**
   * This method initializes jPanel2
   *
   * @return javax.swing.JPanel
   */
  private JPanel getSouthPanel() {
    if (southPanel == null) {
      southPanel = new JPanel();
      southPanel.add(getBackButton(), null);
    }
    return southPanel;
  }
  /**
   * This method initializes jButton
   *
   * @return javax.swing.JButton
   */
  private JButton getBackButton() {
    if (backButton == null) {
      backButton = new JButton();
      backButton.setText("Back");
      backButton.setFont(BUTTON_TEXT_FONT);
      backButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          DriverGUI.backGui();
        }
      });
    }
    return backButton;
  }

  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getNorthPanel() {
    if (northPanel == null) {
      northPanel = new JPanel();
    }
    return northPanel;
  }
  /**
   * This method initializes jPanel1
   *
   * @return javax.swing.JPanel
   */
  private JPanel getCenterPanel() {
    if (centerPanel == null) {
      centerPanel = new JPanel();
      addTitleLabel = new JLabel();
      addTitleLabel.setText("Add/Change/Delete Customer");
      addTitleLabel.setPreferredSize(new java.awt.Dimension(200,15));
      addTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
      addTitleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
      centerPanel.add(addTitleLabel, null);
      centerPanel.add(getCustomerBoxPanel(), null);
      centerPanel.add(getFirstNamePanel(), null);
      centerPanel.add(getLastNamePanel(), null);
      centerPanel.add(getBalancePanel(), null);
      centerPanel.add(getRenewPanel(), null);
      centerPanel.add(getNotePanel(), null);
      centerPanel.add(getCompPanel(), null);
      centerPanel.add(getButtonPanel(), null);
    }
    return centerPanel;
  }
}
