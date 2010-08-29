package org.koala.ui;

import javax.swing.JPanel;/*
 * Created on Jun 15, 2006
 *
 */
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * @author tom
 *
 */

public class ItemExchangeGUI extends DriverGUI {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;
  private JPanel contentPanel = null;
  private JPanel centerPanel = null;
  private JPanel buttonPanel = null;
  private JButton okButton = null;
  private JButton cancelButton = null;
  private JPanel returnedItemPanel = null;
  private JLabel returnedItemLabel = null;
  private JTextField returnedItemTextField = null;
  private JPanel newItemPanel = null;
  private JLabel newItemLabel = null;
  private JTextField newItemTextField = null;

  /**
   * This method initializes
   *
   */
  public ItemExchangeGUI() {
    super();
    initialize();
    this.setTitle(APP_NAME + "Item Exchange");
  }

  /**
   * This method initializes contentPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getContentPanel() {
    if (contentPanel == null) {
      contentPanel = new JPanel();
      contentPanel.setLayout(new BorderLayout());
      contentPanel.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
      contentPanel.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
    }
    return contentPanel;
  }

  /**
   * This method initializes centerPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getCenterPanel() {
    if (centerPanel == null) {
      centerPanel = new JPanel();
      centerPanel.add(getReturnedItemPanel(), null);
      centerPanel.add(getNewItemPanel(), null);
    }
    return centerPanel;
  }

  /**
   * This method initializes buttonPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new JPanel();
      buttonPanel.add(getOkButton(), null);
      buttonPanel.add(getCancelButton(), null);
    }
    return buttonPanel;
  }

  /**
   * This method initializes OkButton
   *
   * @return javax.swing.JButton
   */
  private JButton getOkButton() {
    if (okButton == null) {
      okButton = new JButton();
      okButton.setText("OK");
    }
    return okButton;
  }

  /**
   * This method initializes cancelButton
   *
   * @return javax.swing.JButton
   */
  private JButton getCancelButton() {
    if (cancelButton == null) {
      cancelButton = new JButton();
      cancelButton.setText("Cancel");
    }
    return cancelButton;
  }

  /**
   * This method initializes returnedItemPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getReturnedItemPanel() {
    if (returnedItemPanel == null) {
      returnedItemLabel = new JLabel();
      returnedItemLabel.setText("Returned Item:");
      returnedItemLabel.setPreferredSize(new java.awt.Dimension(95,15));
      returnedItemPanel = new JPanel();
      returnedItemPanel.add(returnedItemLabel, null);
      returnedItemPanel.add(getReturnedItemTextField(), null);
    }
    return returnedItemPanel;
  }

  /**
   * This method initializes returnedItemTextField
   *
   * @return javax.swing.JTextField
   */
  private JTextField getReturnedItemTextField() {
    if (returnedItemTextField == null) {
      returnedItemTextField = new JTextField();
      returnedItemTextField.setPreferredSize(new java.awt.Dimension(75,25));
    }
    return returnedItemTextField;
  }

  /**
   * This method initializes newItemPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getNewItemPanel() {
    if (newItemPanel == null) {
      newItemLabel = new JLabel();
      newItemLabel.setText("New Item:");
      newItemLabel.setPreferredSize(new java.awt.Dimension(95,15));
      newItemPanel = new JPanel();
      newItemPanel.add(newItemLabel, null);
      newItemPanel.add(getNewItemTextField(), null);
    }
    return newItemPanel;
  }

  /**
   * This method initializes jTextField
   *
   * @return javax.swing.JTextField
   */
  private JTextField getNewItemTextField() {
    if (newItemTextField == null) {
      newItemTextField = new JTextField();
      newItemTextField.setPreferredSize(new java.awt.Dimension(75,25));
    }
    return newItemTextField;
  }

  public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new ItemExchangeGUI().setVisible(true);
      }
    });
  }

  /**
   * This method initializes this
   *
   */
  private void initialize() {
    this.setSize(new java.awt.Dimension(204,198));
    this.setContentPane(getContentPanel());
  }

}  //  @jve:decl-index=0:visual-constraint="10,10"
