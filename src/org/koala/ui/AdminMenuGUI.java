package org.koala.ui;
/*
 * AdminMenuGUI.java
 *
 * Created on April 15, 2005, 10:55 PM
 */

/**
 *
 * @author  fergus
 */


import javax.swing.*;

import org.koala.User;

public class AdminMenuGUI extends DriverGUI {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;
    
    /** Creates new form AdminMenuGUI */
    public AdminMenuGUI() {
        initComponents();
        this.setSize(375,400);
        this.setTitle(APP_NAME);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        adminLabelPanel = new JPanel();
        adminOptionsLabel = new JLabel();
        managerMaintenanceButton = new JButton();
        backupManagerButton = new JButton();
        managerMenuButton = new JButton();
        logoutButton = new JButton();

        getContentPane().setLayout(new java.awt.GridLayout(5, 0));

        adminLabelPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));

        adminOptionsLabel.setFont(new java.awt.Font("Dialog", 1, 30));
        adminOptionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adminOptionsLabel.setText("Administrator Options");
        adminLabelPanel.add(adminOptionsLabel);

        getContentPane().add(adminLabelPanel);

        managerMenuButton.setFont(BUTTON_TEXT_FONT);
        managerMenuButton.setText("Manager Menu");
        managerMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            		DriverGUI.nextGui(new ManagerMenuGUI());
            }
        });

        getContentPane().add(managerMenuButton);

        managerMaintenanceButton.setFont(BUTTON_TEXT_FONT);
        managerMaintenanceButton.setText("Manager Management");
        managerMaintenanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            		DriverGUI.nextGui(new UpdateUsersGUI(User.MANAGER));
            }
        });

        getContentPane().add(managerMaintenanceButton);

        backupManagerButton.setFont(BUTTON_TEXT_FONT);
        backupManagerButton.setText("Backup Management");
        backupManagerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            		DriverGUI.nextGui(new BackupManagerGUI());
            }
        });

        getContentPane().add(backupManagerButton);

        logoutButton.setFont(BUTTON_TEXT_FONT);
        logoutButton.setText("Logout");
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
                new AdminMenuGUI().setVisible(true);
            }
        });
    }

    // Variables declaration
    private JLabel adminOptionsLabel;
    private JPanel adminLabelPanel;
    private JButton backupManagerButton;
    private JButton managerMenuButton;
    private JButton logoutButton;
    private JButton managerMaintenanceButton;
}
