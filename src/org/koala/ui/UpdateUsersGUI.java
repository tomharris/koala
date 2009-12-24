package org.koala.ui;
/*
 * Created on Jun 15, 2005
 *
 */

/**
 * @author tom
 *
 */


import java.awt.*;

import javax.swing.*;

import org.koala.model.User;
import org.koala.exception.EntryAlreadyExistsException;

public class UpdateUsersGUI extends DriverGUI {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

	private JPanel jContentPane = null;

	private JPanel usernamePanel = null;
	private JLabel usernameLabel = null;
	private JPanel FirstNamePanel = null;
	private JLabel firstNameLabel = null;
	private JTextField firstNameTextField = null;
	private JPanel lastNamePanel = null;
	private JLabel lastNameLabel = null;
	private JTextField lastNameTextField = null;
	private JButton updateButton = null;
	private JPanel newPasswordPanel = null;
	private JLabel newPasswordLabel = null;
	private JButton cancelButton = null;
	private JPanel buttonPanel = null;
	private JTextField newPasswordTextField = null;
	private JComboBox usernameComboBox = null;
	private JButton deleteButton = null;
	private JPanel newUsernamePanel = null;
	private JLabel newUsernameLabel = null;
	private JTextField newUsernameTextField = null;

	private int userLevel = User.NONE;

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getUsernamePanel() {
		if (usernamePanel == null) {
			usernameLabel = new JLabel();
			usernamePanel = new JPanel();
			usernameLabel.setText("Current Username:");
			usernamePanel.add(usernameLabel, null);
			usernamePanel.add(getUsernameComboBox(), null);
		}
		return usernamePanel;
	}
	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getFirstNamePanel() {
		if (FirstNamePanel == null) {
			firstNameLabel = new JLabel();
			FirstNamePanel = new JPanel();
			firstNameLabel.setText("First Name:");
			firstNameLabel.setPreferredSize(new java.awt.Dimension(95,15));
			FirstNamePanel.add(firstNameLabel, null);
			FirstNamePanel.add(getFirstNameTextField(), null);
		}
		return FirstNamePanel;
	}
	/**
	 * This method initializes firstNameTextField
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
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getLastNamePanel() {
		if (lastNamePanel == null) {
			lastNameLabel = new JLabel();
			lastNamePanel = new JPanel();
			lastNameLabel.setText("Last Name:");
			lastNameLabel.setPreferredSize(new java.awt.Dimension(95,15));
			lastNamePanel.add(lastNameLabel, null);
			lastNamePanel.add(getLastNameTextField(), null);
		}
		return lastNamePanel;
	}
	/**
	 * This method initializes lastNameTextField
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
		int userid = 0;
		if(usernameComboBox.getSelectedIndex() != 0) {
		  userid = ((User)usernameComboBox.getSelectedItem()).getId();
		}

		//create user
		User user = new User();
    user.setId(userid);
    user.setLevel(userLevel);
    user.setUserName(newUsernameTextField.getText());
    user.setFirstName(firstNameTextField.getText());
    user.setLastName(lastNameTextField.getText());

		String password = null;
		if(newPasswordTextField.getText() != null && !newPasswordTextField.getText().trim().equals("")) {
		  user.setPassword(newPasswordTextField.getText());
		}

		//update db
		try {
			user.save();
		}
		catch (EntryAlreadyExistsException e) {
	    DriverGUI.printError(e);
		}

		//update combo box
		if(usernameComboBox.getSelectedIndex() != 0) {
		  usernameComboBox.removeItemAt(usernameComboBox.getSelectedIndex());
		}
		usernameComboBox.addItem(user);
		usernameComboBox.setSelectedIndex(0);
		clearFields();
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getNewPasswordPanel() {
		if (newPasswordPanel == null) {
			newPasswordLabel = new JLabel();
			newPasswordPanel = new JPanel();
			newPasswordLabel.setText("New Password:");
			newPasswordLabel.setPreferredSize(new java.awt.Dimension(95,15));
			newPasswordPanel.add(newPasswordLabel, null);
			newPasswordPanel.add(getNewPasswordTextField(), null);
		}
		return newPasswordPanel;
	}
	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel(new GridLayout(1,0));
			buttonPanel.add(getUpdateButton());
			buttonPanel.add(getDeleteButton());
			buttonPanel.add(new JPanel());
			buttonPanel.add(getCancelButton());
		}
		return buttonPanel;
	}
	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Back");
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
	 * This method initializes usernameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getNewPasswordTextField() {
		if (newPasswordTextField == null) {
			newPasswordTextField = new JTextField();
			newPasswordTextField.setPreferredSize(TEXTAREA_SIZE);
		}
		return newPasswordTextField;
	}
	/**
	 * This method initializes usernameComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getUsernameComboBox() {
		if (usernameComboBox == null) {
	        if(currentUser == null)
	        	usernameComboBox = new JComboBox();
	        else {
	        	usernameComboBox = new JComboBox(User.findAllByAccess(userLevel).toArray());
	        }

		    usernameComboBox.setPreferredSize(new java.awt.Dimension(150,20));
		    usernameComboBox.setMaximumRowCount(5);
		    usernameComboBox.insertItemAt("<<NEW USER>>", 0);
		    usernameComboBox.setSelectedIndex(0);
		    usernameComboBox.addItemListener(new java.awt.event.ItemListener() {
            	public void itemStateChanged(java.awt.event.ItemEvent evt) {
            		usernameComboBoxItemStateChanged(evt);
            	}
            });
		}
		return usernameComboBox;
	}

	private void usernameComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
	    if(usernameComboBox.getSelectedIndex() == 0) {
	        clearFields();
	        return;
	    }

	    User selectedUser = (User)usernameComboBox.getSelectedItem();

	    newUsernameTextField.setText(selectedUser.getUserName());
	    firstNameTextField.setText(selectedUser.getFirstName());
	    lastNameTextField.setText(selectedUser.getLastName());
	}

	/**
	 * This method initializes deleteButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDeleteButton() {
		if (deleteButton == null) {
			deleteButton = new JButton();
			deleteButton.setText("Remove");
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
		if(usernameComboBox.getSelectedIndex() == 0)
			return;

		int result = JOptionPane.showConfirmDialog(this,
	            "Are you sure you want to remove this user?",
	            "User remove",
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.WARNING_MESSAGE);

		if(result != JOptionPane.YES_OPTION)
			return;

		((User)usernameComboBox.getSelectedItem()).destroy();
		usernameComboBox.removeItemAt(usernameComboBox.getSelectedIndex());
	}

	/**
	 * This method initializes newUsernamePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getNewUsernamePanel() {
		if (newUsernamePanel == null) {
			newUsernameLabel = new JLabel();
			newUsernameLabel.setText("New Username:");
			newUsernameLabel.setPreferredSize(new java.awt.Dimension(105,15));
			newUsernamePanel = new JPanel();
			newUsernamePanel.add(newUsernameLabel, null);
			newUsernamePanel.add(getNewUsernameTextField(), null);
		}
		return newUsernamePanel;
	}
	/**
	 * This method initializes newUsernameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getNewUsernameTextField() {
		if (newUsernameTextField == null) {
			newUsernameTextField = new JTextField();
			newUsernameTextField.setPreferredSize(TEXTAREA_SIZE);
		}
		return newUsernameTextField;
	}
	public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UpdateUsersGUI(User.NONE).setVisible(true);
            }
        });
    }

	private void clearFields() {
		newUsernameTextField.setText("");
		firstNameTextField.setText("");
		lastNameTextField.setText("");
		newPasswordTextField.setText("");
	}

	/**
	 * This is the default constructor
	 */
	public UpdateUsersGUI(int userLevel) {
		super();
		this.userLevel = userLevel;
		initialize();
		this.setTitle(APP_NAME + ": Update Users");
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(400,330);
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

			JPanel centerPane = new JPanel();
			centerPane.setLayout(new GridLayout(0,1));
			centerPane.add(getUsernamePanel());
			centerPane.add(getNewUsernamePanel());
			centerPane.add(getFirstNamePanel());
			centerPane.add(getLastNamePanel());
			centerPane.add(new JPanel());
			centerPane.add(getNewPasswordPanel());

			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(centerPane, java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
}
