package org.koala.ui;
/*
 * loginFrame.java
 *
 * Created on April 14, 2005, 6:08 PM
 */

/**
 *
 * @author  fergus
 */


import javax.swing.*;

import org.koala.model.User;

public class LoginScreenGUI extends DriverGUI {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;

  /** Creates new form loginFrame */
  public LoginScreenGUI() {
    initComponents();
    this.setSize(600, 550);
    this.setLocationRelativeTo(null);
    this.setTitle(APP_NAME);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   */
  private void initComponents() {
    imageLabel1 = new JLabel();
    imageLabel2 = new JLabel();
    imageLabel3 = new JLabel();
    imageLabel4 = new JLabel();

    //ImageIcon myImage1 = new ImageIcon(new String
        //("posImages/lookUp.jpg"));
    //ImageIcon myImage1 = new ImageIcon(
        //getClass().getResource("images/lookUp.jpg"));
    ImageIcon myImage1 = new ImageIcon(
        getClass().getClassLoader().getResource("lookUp.jpg"));
    ImageIcon myImage2 = new ImageIcon(
        getClass().getClassLoader().getResource("lookGood.jpg"));
    ImageIcon myImage3 = new ImageIcon(
        getClass().getClassLoader().getResource("lookRight.jpg"));
    ImageIcon myImage4 = new ImageIcon(
        getClass().getClassLoader().getResource("comboTitleSmall2.jpg"));
    imageLabel1.setIcon(myImage1);
    imageLabel2.setIcon(myImage2);
    imageLabel3.setIcon(myImage3);
    imageLabel4.setIcon(myImage4);
    loginLabelPanel = new JPanel();
    systemLoginLabel = new JLabel();
    usernamePanel = new JPanel();
    usernameLabel = new JLabel();
    usernameTextField = new JTextField();
    passwordPanel = new JPanel();
    passwordLabel = new JLabel();
    passwordField = new JPasswordField();
    loginButtonPanel = new JPanel(new java.awt.GridLayout(2,2));
    loginButton = new JButton();
    exitButton = new JButton();

    getContentPane().setLayout(new java.awt.GridLayout(4, 0));

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//        loginLabelPanel.setLayout(new java.awt.BorderLayout());

    systemLoginLabel.setFont(new java.awt.Font("Dialog", 1, 36));
    systemLoginLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    systemLoginLabel.setText("System Login");
    systemLoginLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    //loginLabelPanel.add(systemLoginLabel, java.awt.BorderLayout.CENTER);

    getContentPane().add(loginLabelPanel);
    loginLabelPanel.add(imageLabel4, java.awt.BorderLayout.CENTER);

    usernameLabel.setFont(LABEL_TEXT_FONT);
    usernameLabel.setText("Username:");
    usernamePanel.add(usernameLabel);

    usernameTextField.setMinimumSize(TEXTAREA_SIZE);
    usernameTextField.setPreferredSize(TEXTAREA_SIZE);
    usernamePanel.add(usernameTextField, java.awt.BorderLayout.EAST);

    getContentPane().add(usernamePanel);

    usernamePanel.add(imageLabel1, java.awt.BorderLayout.WEST);

    passwordLabel.setFont(LABEL_TEXT_FONT);
    passwordLabel.setText("Password:");
    passwordPanel.add(passwordLabel);

    passwordField.setMinimumSize(TEXTAREA_SIZE);
    passwordField.setPreferredSize(TEXTAREA_SIZE);
    passwordPanel.add(passwordField);

    passwordPanel.add(imageLabel2);

    getContentPane().add(passwordPanel);

    loginButtonPanel.add(new JPanel());
    loginButtonPanel.add(new JPanel());

    loginButton.setFont(BUTTON_TEXT_FONT);
    loginButton.setText("Login");
    loginButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        loginButtonActionPerformed(evt);
      }
    });

    //loginButtonPanel.add(imageLabel3);
    loginButtonPanel.add(loginButton);

    exitButton.setFont(BUTTON_TEXT_FONT);
    exitButton.setText("Exit");
    exitButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exitPoS();
      }
    });

    loginButtonPanel.add(exitButton);

    getContentPane().add(loginButtonPanel);

    pack();
  }

  private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if(usernameTextField.getText().length() == 0 ||
      passwordField.getPassword().length == 0) {
      emptyUsernamePassword();
      return;
    }

    try {
      currentUser = User.login(usernameTextField.getText(), new String(passwordField.getPassword()));
    }
    catch (Exception e) {
      //maybe post a poppup or something
      DriverGUI.printError(e);
    }

    if(currentUser == null) {
      javax.swing.JOptionPane.showMessageDialog(
        this,
        "Your username and/or password is incorrect!",
        "Invalid username/password combo",
        javax.swing.JOptionPane.PLAIN_MESSAGE
      );
      this.usernameTextField.setText("");
      this.passwordField.setText("");
      this.usernameTextField.requestFocus();

      return;
    }

    switch(currentUser.getLevel()) {
      case User.ADMIN:
        DriverGUI.nextGui(new AdminMenuGUI());
        break;
      case User.MANAGER:
        DriverGUI.nextGui(new ManagerMenuGUI());
        break;
      case User.CASHIER:
        DriverGUI.nextGui(new CustomerLookupGUI());
        break;
      default:
        break;
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new LoginScreenGUI().setVisible(true);
      }
    });
  }

  public void emptyUsernamePassword() {
    javax.swing.JOptionPane.showMessageDialog(
      this,
      "You must enter a username and password!",
      "Invalid Input",
      javax.swing.JOptionPane.PLAIN_MESSAGE
    );
    this.usernameTextField.setText("");
    this.passwordField.setText("");
    usernameTextField.requestFocus();
  }

  // Variables declaration
  private JLabel imageLabel1;
  private JLabel imageLabel2;
  private JLabel imageLabel3;
  private JLabel imageLabel4;
  private JButton exitButton;
  private JButton loginButton;
  private JPanel loginButtonPanel;
  private JPanel loginLabelPanel;
  private JPasswordField passwordField;
  private JLabel passwordLabel;
  private JPanel passwordPanel;
  private JLabel systemLoginLabel;
  private JLabel usernameLabel;
  private JPanel usernamePanel;
  private JTextField usernameTextField;
}
