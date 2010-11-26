package org.koala.ui;
/*
 * ManagerMenuGUI.java
 *
 * Created on April 15, 2005, 10:56 PM
 */

/**
 *
 * @author  fergus
 * TODO: add button for inventory gui
 *  add button for addaccount
 */


import javax.swing.*;

import org.koala.model.User;
import org.koala.model.FinancialReport;
import org.koala.model.OutstandingAccountsReport;

public class ManagerMenuGUI extends DriverGUI {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;

  /** Creates new form ManagerMenuGUI */
  public ManagerMenuGUI() {
    initComponents();
    this.setSize(375,475);
    this.setTitle(APP_NAME);
  }

  /** This method is called from within the constructor to
   * initialize the form.
    */
  private void initComponents() {
    accountMaintenanceButton = new JButton();
    updateInventoryButton = new JButton();
    managerOptionsPanel = new JPanel();
    managerOptionsLabel = new JLabel();
    cashierScreenButton = new JButton();
    cashierMaintenanceButton = new JButton();
    backupManagerButton = new JButton();
    financialReportButton = new JButton();
    accountsReportButton = new JButton();
    logoutButton = new JButton();

    getContentPane().setLayout(new java.awt.GridLayout(9, 0));

    managerOptionsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));

    managerOptionsLabel.setFont(new java.awt.Font("Dialog", 1, 30));
    managerOptionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    managerOptionsLabel.setText("Manager Options:");
    managerOptionsPanel.add(managerOptionsLabel);

    getContentPane().add(managerOptionsPanel);

    cashierScreenButton.setFont(BUTTON_TEXT_FONT);
    cashierScreenButton.setText("Cashier Screen");
    cashierScreenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        DriverGUI.nextGui(new CustomerLookupGUI());
      }
    });

    getContentPane().add(cashierScreenButton);

    cashierMaintenanceButton.setFont(BUTTON_TEXT_FONT);
    cashierMaintenanceButton.setText("Cashier Management");
    cashierMaintenanceButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        DriverGUI.nextGui(new UpdateUsersGUI(User.CASHIER));
      }
    });

    getContentPane().add(cashierMaintenanceButton);

    updateInventoryButton.setFont(BUTTON_TEXT_FONT);
    updateInventoryButton.setText("Inventory Management");
    updateInventoryButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        DriverGUI.nextGui(new UpdateInventoryGUI());
      }
    });

    getContentPane().add(updateInventoryButton);

    accountMaintenanceButton.setFont(BUTTON_TEXT_FONT);
    accountMaintenanceButton.setText("Customers Management");
    accountMaintenanceButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        DriverGUI.nextGui(new AccountMaintenanceGUI());
      }
    });

    getContentPane().add(accountMaintenanceButton);

    backupManagerButton.setFont(BUTTON_TEXT_FONT);
    backupManagerButton.setText("Backup Management");
    backupManagerButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        DriverGUI.nextGui(new BackupManagerGUI());
      }
    });

    getContentPane().add(backupManagerButton);

    financialReportButton.setFont(BUTTON_TEXT_FONT);
    financialReportButton.setText("Financial Report");
    financialReportButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showModelGui(new ReportGUI(new FinancialReport()));
      }
    });

    getContentPane().add(financialReportButton);

    accountsReportButton.setFont(BUTTON_TEXT_FONT);
    accountsReportButton.setText("Outstanding Accts Report");
    accountsReportButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        showModelGui(new ReportGUI(new OutstandingAccountsReport()));
      }
    });

    getContentPane().add(accountsReportButton);

    logoutButton.setFont(BUTTON_TEXT_FONT);
    if(currentUser.getLevel() > User.MANAGER)
      logoutButton.setText("Back");
    else
      logoutButton.setText("Log Out");
      logoutButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          DriverGUI.backGui();
        }
      });

      getContentPane().add(logoutButton);

      pack();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          new ManagerMenuGUI().setVisible(true);
        }
      });
    }

    // Variables declaration
    private JButton accountMaintenanceButton;
    private JButton updateInventoryButton;
    private JButton cashierScreenButton;
    private JButton cashierMaintenanceButton;
    private JButton backupManagerButton;
    private JPanel managerOptionsPanel;
    private JButton logoutButton;
    private JLabel managerOptionsLabel;
    private JButton financialReportButton;
    private JButton accountsReportButton;
}
