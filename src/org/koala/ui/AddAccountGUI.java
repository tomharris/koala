package org.koala.ui;
/*
 * Created on Jun 8, 2005
 */

/**
 * @author tom
 *
 * TODO hide compcheckbox from non-managers
 */


import javax.swing.*;

import org.koala.model.Customer;
import org.koala.exception.EntryAlreadyExistsException;
import org.koala.exception.ItemNotFoundException;
import org.koala.ui.widget.NoteTextArea;

import java.awt.*;
import java.math.BigDecimal;

public class AddAccountGUI extends DriverGUI {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;

  private JPanel jContentPane = null;

  private JPanel centerPanel = null;
  private JPanel southPanel = null;
  private JLabel firstNameLabel = null;
  private JPanel eastPanel = null;
  private JPanel westPanel = null;
  private JTextField firstNameTextField = null;
  private JLabel lastNameLabel = null;
  private JTextField lastNameTextField = null;
  private JLabel amountLabel = null;
  private JTextField amountTextField = null;
  private JPanel firstNamePanel = null;
  private JPanel lastNamePanel = null;
  private JPanel amountPanel = null;
  private JButton createButton = null;
  private JButton cancelButton = null;
  private JPanel compPanel = null;
  private JLabel compLabel = null;
  private JCheckBox compCheckBox = null;
  private JPanel notePanel = null;
  private NoteTextArea noteTextArea = null;
  private JLabel noteLabel = null;
  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getCenterPanel() {
    if (centerPanel == null) {
      amountLabel = new JLabel();
      lastNameLabel = new JLabel();
      firstNameLabel = new JLabel();
      centerPanel = new JPanel();
      centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
      firstNameLabel.setText("First Name: ");
      firstNameLabel.setPreferredSize(new java.awt.Dimension(80,15));
      lastNameLabel.setText("Last Name: ");
      lastNameLabel.setPreferredSize(new java.awt.Dimension(80,15));
      amountLabel.setText("Amount: ");
      amountLabel.setPreferredSize(new java.awt.Dimension(80,15));
      centerPanel.add(getFirstNamePanel(), null);
      centerPanel.add(getLastNamePanel(), null);
      centerPanel.add(getAmountPanel(), null);
      centerPanel.add(getNotePanel(), null);
      centerPanel.add(getCompPanel(), null);
    }
    return centerPanel;
  }
  /**
   * This method initializes jPanel1
   *
   * @return javax.swing.JPanel
   */
  private JPanel getSouthPanel() {
    if (southPanel == null) {
      southPanel = new JPanel(new GridLayout(1,0));
      southPanel.add(getCreateButton(), null);
      southPanel.add(getCancelButton(), null);
    }
    return southPanel;
  }
  /**
   * This method initializes jPanel2
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
   * This method initializes jPanel3
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
  private JTextField getAmountTextField() {
    if (amountTextField == null) {
      amountTextField = new JTextField();
      amountTextField.setPreferredSize(TEXTAREA_SIZE);
    }
    return amountTextField;
  }
  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getFirstNamePanel() {
    if (firstNamePanel == null) {
      firstNamePanel = new JPanel();
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
  private JPanel getAmountPanel() {
    if (amountPanel == null) {
      amountPanel = new JPanel();
      amountPanel.add(amountLabel, null);
      amountPanel.add(getAmountTextField(), null);
    }
    return amountPanel;
  }
  /**
   * This method initializes jButton
   *
   * @return javax.swing.JButton
   */
  private JButton getCreateButton() {
    if (createButton == null) {
      createButton = new JButton();
      createButton.setText("Create");
      createButton.setFont(BUTTON_TEXT_FONT);
      createButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          createButtonActionPerformed(evt);
        }
      });
    }
    return createButton;
  }

  private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
    BigDecimal balanceAmount = null;
    try {
      balanceAmount = new BigDecimal(amountTextField.getText().trim());
    }
    catch(NumberFormatException e) {
      //log later?
      amountTextField.setText("");
      return;
    }

    try {
      Customer customer = new Customer();
      customer.setBalance(balanceAmount);
      customer.setLastName(lastNameTextField.getText());
      customer.setFirstName(firstNameTextField.getText());
      customer.setComplementary(compCheckBox.isSelected());
      customer.setRenewAmount(BigDecimal.ZERO);
      customer.setNote(noteTextArea.getText());
      customer.save();
    }
    catch (EntryAlreadyExistsException e) {
      JOptionPane.showMessageDialog(this,
        "This customer already exists!",
        "Customer exists",
        JOptionPane.PLAIN_MESSAGE
      );
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

  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getCompPanel() {
    if (compPanel == null) {
      compLabel = new JLabel();
      compPanel = new JPanel();
      compLabel.setText("Complementary: ");
      compPanel.add(compLabel, null);
      compPanel.add(getCompCheckBox(), null);
    }
    return compPanel;
  }
  /**
   * This method initializes compCheckBox
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
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getNotePanel() {
    if (notePanel == null) {
      noteLabel = new JLabel();
      notePanel = new JPanel();
      notePanel.setLayout(new BorderLayout());
      noteLabel.setText("Note: ");
      noteLabel.setPreferredSize(new java.awt.Dimension(80,15));
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

  public static void main(String[] args) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new AddAccountGUI().setVisible(true);
      }
    });
  }
  /**
   * This is the default constructor
   */
  public AddAccountGUI() {
    super();
    initialize();
  }
  /**
   * This method initializes this
   *
   * @return void
   */
  private void initialize() {
    this.setSize(300,325);
    this.setContentPane(getJContentPane());
    this.setTitle(APP_NAME + "Add Account");
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
      jContentPane.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
      jContentPane.add(getSouthPanel(), java.awt.BorderLayout.SOUTH);
      jContentPane.add(getEastPanel(), java.awt.BorderLayout.EAST);
      jContentPane.add(getWestPanel(), java.awt.BorderLayout.WEST);
    }
    return jContentPane;
  }
}
