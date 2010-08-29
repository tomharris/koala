package org.koala.ui;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.koala.BackupMethod;
import org.koala.model.User;

import java.awt.FlowLayout;
import java.awt.GridLayout;

/*
 * Created on Feb 21, 2006
 *
 */

/**
 * @author tom
 *
 */
public class BackupManagerGUI extends DriverGUI {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;

  private javax.swing.JPanel jContentPane = null;

  private JPanel buttonPanel = null;
  private JButton backButton = null;
  private JList backupsList = null;
  private JButton restoreButton = null;
  private JPanel backupPanel = null;
  private JButton backupButton = null;
  private JScrollPane backupScrollPane = null;
  private JPanel messagePanel = null;
  private JLabel messageLabel = null;
  private JButton resetButton = null;

  private static Logger logger = Logger.getLogger(User.class);

  /**
   * This method initializes jPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new JPanel(new GridLayout(1,0));
      buttonPanel.add(getBackupButton(), null);
      buttonPanel.add(getRestoreButton(), null);
      buttonPanel.add(getResetButton(), null);
      buttonPanel.add(getBackButton(), null);
    }
    return buttonPanel;
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
   * This method initializes jList
   *
   * @return javax.swing.JList
   */
  private JList getBackupsList() {
    if (backupsList == null) {
      backupsList = new JList(currentUser.getCurrentBackupMethod().getBackupPackageNames().toArray());
    }

    backupsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    backupsList.setLayoutOrientation(JList.VERTICAL);
    backupsList.setVisibleRowCount(10);

    return backupsList;
  }

     /**
   * This method initializes jButton
   *
   * @return javax.swing.JButton
   */
  private JButton getRestoreButton() {
    if (restoreButton == null) {
      restoreButton = new JButton();
      restoreButton.setText("Restore");
      restoreButton.setFont(BUTTON_TEXT_FONT);
      restoreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          doRestore(evt);
        }
      });
    }
    return restoreButton;
  }

  void doRestore(java.awt.event.ActionEvent evt) {
    boolean result = false;
    try {
      result = currentUser.getCurrentBackupMethod().restorePackage((String)backupsList.getSelectedValue());
    }
    catch (Exception e) {
      logger.error("Error connecting to Database", e);
    }

    if(result) {
      messageLabel.setText("Database Restore Completed!");
      messageLabel.setVisible(true);
    }
    else {
      messageLabel.setText("Unable to restore database!");
      messageLabel.setVisible(true);
    }
  }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getBackupPanel() {
      if (backupPanel == null) {
        backupPanel = new JPanel();
        backupPanel.setLayout(new FlowLayout());
        backupPanel.add(getBackupScrollPane(), null);
      }
      return backupPanel;
    }

    void doBackup(java.awt.event.ActionEvent evt) {
      String packageName = null;
      BackupMethod backupMethod = null;

      backupMethod = currentUser.getCurrentBackupMethod();
      packageName = backupMethod.createPackage();

      if(packageName != null && backupMethod != null) {
        backupsList.setListData(backupMethod.getBackupPackageNames().toArray());
        messageLabel.setText("Database Backup Successful!");
        messageLabel.setVisible(true);
      }
      else {
        messageLabel.setText("Database Backup Failed!");
        messageLabel.setVisible(true);
      }
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton getBackupButton() {
      if (backupButton == null) {
        backupButton = new JButton();
        backupButton.setText("Backup");
        backupButton.setFont(BUTTON_TEXT_FONT);
        backupButton.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            doBackup(evt);
          }
        });
      }
      return backupButton;
    }
    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getBackupScrollPane() {
      if (backupScrollPane == null) {
        backupScrollPane = new JScrollPane();
        backupScrollPane.setViewportView(getBackupsList());
      }
      return backupScrollPane;
    }
    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
      if (messagePanel == null) {
        messageLabel = new JLabel();
        messageLabel.setText("");
        messageLabel.setVisible(false);
        messagePanel = new JPanel();
        messagePanel.add(messageLabel, null);
      }
      return messagePanel;
    }
    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton getResetButton() {
      if (resetButton == null) {
        resetButton = new JButton();
        resetButton.setText("Reset");
        resetButton.setFont(BUTTON_TEXT_FONT);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            doReset(evt);
          }
        });
      }
      return resetButton;
    }

    private void doReset(java.awt.event.ActionEvent evt) {
      String packageName = null;

      packageName = currentUser.resetDatabase();
      backupsList.setListData(currentUser.getCurrentBackupMethod().getBackupPackageNames().toArray());

      if(packageName != null) {
        messageLabel.setText("Database Reset Successful!");
        messageLabel.setVisible(true);
      }
      else {
        messageLabel.setText("Database Reset Failed!");
        messageLabel.setVisible(true);
      }
    }

    public static void main(String[] args) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          new BackupManagerGUI().setVisible(true);
        }
      });
    }
  /**
   * This is the default constructor
   */
  public BackupManagerGUI() {
    super();
    initialize();
  }
  /**
   * This method initializes this
   *
   * @return void
   */
  private void initialize() {
    this.setSize(400,350);
    this.setContentPane(getJContentPane());
    this.setTitle(APP_NAME + ": Backup Manager");
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
      jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
      jContentPane.add(getBackupPanel(), java.awt.BorderLayout.CENTER);
      jContentPane.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
    }
    return jContentPane;
  }
}
