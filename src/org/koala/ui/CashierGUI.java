package org.koala.ui;

import javax.swing.*;

import java.awt.*;

import javax.swing.table.DefaultTableModel;

import org.koala.Money;
import org.koala.model.CashCustomer;
import org.koala.model.Customer;
import org.koala.model.CustomerReport;
import org.koala.model.InventoryItem;
import org.koala.model.Transaction;
import org.koala.model.TransactionItem;
import org.koala.exception.EntryAlreadyExistsException;
import org.koala.ui.widget.ItemTable;

/**
 * Created on April 1, 2007, 2:34 PM
 */

/**
 * @author tom
 *
 */
public class CashierGUI extends DriverGUI {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;

  private Transaction currentTransaction;

  JPanel contentPane = null;
  JPanel northPanel = null;
  JPanel southPanel = null;
  JPanel eastPanel = null;
  JPanel westPanel = null;

  JPanel customerInfoPanel = null;
  JPanel notePanel = null;
  JPanel skuPanel = null;
  JPanel totalPanel = null;
  JPanel buttonPanel = null;

  JTextArea noteTextArea = null;
  ItemTable itemTable = null;
  JTextField skuTextField = null;
  JButton addItemButton = null;
  JLabel totalLabel = null;
  JLabel customerNameLabel = null;
  JLabel balanceLabel = null;
  JPanel morePanel = null;
  JButton moreButton = null;

  JButton cashOutButton = null;
  JButton addToAccountButton = null;
  JButton customerHistoryButton = null;

  /**
   * This is the default constructor
   */
  public CashierGUI() {
    super();
    initialize();
    populateFields();
  }
  /**
   * This method initializes this
   *
   * @return void
   */
  private void initialize() {
    this.setSize(750,550);
    this.setContentPane(getJContentPane());
    this.setTitle(APP_NAME + ": Cashier");
  }

  /**
   * This method initializes CustomerInfoPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getCustomerInfoPanel() {
    if(this.customerInfoPanel == null) {
      this.customerInfoPanel = new JPanel();

      this.balanceLabel = new JLabel();
      this.balanceLabel.setFont(LABEL_TEXT_FONT);
      this.customerNameLabel = new JLabel();
      this.customerNameLabel.setFont(LABEL_TEXT_FONT);

      this.customerInfoPanel = new JPanel();
      this.customerInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      this.customerInfoPanel.add(this.customerNameLabel);
      this.customerInfoPanel.add(this.balanceLabel);
    }

    return this.customerInfoPanel;
  }
  /**
   * This method initializes notePanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getNotePanel() {
    if(this.notePanel == null) {
      this.notePanel = new JPanel();
      JLabel noteLabel = new JLabel();
      noteLabel.setFont(LABEL_TEXT_FONT);
      noteLabel.setText("Note: ");

      this.noteTextArea = new JTextArea();
      this.noteTextArea.setEditable(false);
      this.noteTextArea.setMinimumSize(new Dimension(600, 30));
      this.noteTextArea.setPreferredSize(new Dimension(600, 30));
      this.noteTextArea.setFocusable(false);
      this.noteTextArea.setBackground(Color.BLACK);
      this.noteTextArea.setForeground(Color.WHITE);

      this.notePanel.add(noteLabel);
      this.notePanel.add(this.noteTextArea);
    }

    return this.notePanel;
  }

  /**
   * This method initializes morePanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getMorePanel(boolean isComp) {
    if(this.morePanel == null) {
      this.morePanel = new JPanel(new GridLayout(0,1));

      if(!isComp) {
        this.cashOutButton = new JButton();
        this.cashOutButton.setFont(BUTTON_TEXT_FONT);
        this.cashOutButton.setText("Cash Out");
        this.cashOutButton.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            cashOutButtonActionPerformed(evt);
          }
        });

        this.addToAccountButton = new JButton();
        this.addToAccountButton.setFont(BUTTON_TEXT_FONT);
        this.addToAccountButton.setText("Add Money");
        this.addToAccountButton.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            DriverGUI.nextGui(new AddToAccountGUI());
          }
        });
      }

      this.customerHistoryButton = new JButton();
      this.customerHistoryButton.setFont(BUTTON_TEXT_FONT);
      this.customerHistoryButton.setText("History");
      this.customerHistoryButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          DriverGUI.showModelGui(new ReportGUI(new CustomerReport(currentCustomer)));
        }
      });

      if(!isComp) {
        this.morePanel.add(this.cashOutButton);
        this.morePanel.add(this.addToAccountButton);
      }
      this.morePanel.add(this.customerHistoryButton);
    }
    return this.morePanel;
  }

  /**
   * This method initializes itemTable
   *
   * @return ItemTable.ItemTable
   */
  private ItemTable getItemTable() {
    if(this.itemTable == null) {
      this.itemTable = new ItemTable();

      this.itemTable.setModel(new DefaultTableModel(
        new String [] {
          "SKU", "Item Description", "Price"
        },
        0 //no rows in the table to start with
      ));

      this.itemTable.getColumnModel().getColumn(0).setPreferredWidth(10);
      this.itemTable.getColumnModel().getColumn(0).setMinWidth(10);
      this.itemTable.getColumnModel().getColumn(1).setPreferredWidth(300);
      this.itemTable.getColumnModel().getColumn(1).setMinWidth(300);
      this.itemTable.getColumnModel().getColumn(2).setPreferredWidth(10);
      this.itemTable.getColumnModel().getColumn(2).setMinWidth(10);
    }

    return this.itemTable;
  }
  /**
   * This method initializes itemScrollPane
   *
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getItemScrollPane() {
    JScrollPane itemScrollPane = new JScrollPane();
    itemScrollPane.setViewportView(getItemTable());

    return itemScrollPane;
  }

  public static void main(String[] args) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new CashierGUI().setVisible(true);
      }
    });
  }

  /**
   * This method initializes contentPane
   *
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if(this.contentPane == null) {
      this.contentPane = new javax.swing.JPanel();
      this.contentPane.setLayout(new java.awt.BorderLayout());
      this.contentPane.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
      this.contentPane.add(getItemScrollPane(), java.awt.BorderLayout.CENTER);
      this.contentPane.add(getSouthPanel(), java.awt.BorderLayout.SOUTH);
      this.contentPane.add(getEastPanel(), java.awt.BorderLayout.EAST);
      this.contentPane.add(getWestPanel(), java.awt.BorderLayout.WEST);
    }

    return this.contentPane;
  }

  /**
   * This method initializes northPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getNorthPanel() {
    if(this.northPanel == null) {
      this.northPanel = new JPanel();
      this.northPanel.setLayout(new GridLayout(1,1));
      this.northPanel.add(getCustomerInfoPanel());
    }

    return this.northPanel;
  }

  /**
   * This method initializes southPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getSouthPanel() {
    if(this.southPanel == null) {
      this.southPanel = new JPanel();
      this.southPanel.setLayout(new GridLayout(2,1));

      JPanel orderPanel = new JPanel();
      orderPanel.setLayout(new GridLayout(1,2));
      orderPanel.add(getSkuPanel());
      orderPanel.add(getTotalPanel());

      this.southPanel.add(orderPanel);
      this.southPanel.add(getButtonPanel());
    }

    return this.southPanel;
  }

  private JPanel getSkuPanel() {
    if(this.skuPanel == null) {
      this.skuPanel = new JPanel();
      JLabel skuLabel = new JLabel();
      skuLabel.setFont(LABEL_TEXT_FONT);
      skuLabel.setText("Item: ");
      this.skuPanel.add(skuLabel);

      this.skuTextField = new JTextField();
      this.skuTextField.setMinimumSize(TEXTAREA_SIZE);
      this.skuTextField.setPreferredSize(TEXTAREA_SIZE);
      this.skuTextField.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          skuEnterButtonActionPerformed(evt);
        }
      });

      this.addItemButton = new JButton();
      this.addItemButton.setText("Add");
      this.addItemButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          skuEnterButtonActionPerformed(evt);
        }
      });
      this.skuPanel.add(this.skuTextField);
      this.skuPanel.add(this.addItemButton);
    }

    return this.skuPanel;
  }

  private JPanel getTotalPanel() {
    if(this.totalPanel == null) {
      this.totalPanel = new JPanel();
      JLabel totalDescriptionLabel = new JLabel();

      totalDescriptionLabel.setFont(LABEL_TEXT_FONT);
      totalDescriptionLabel.setText("Total: ");
      this.totalPanel.add(totalDescriptionLabel);

      this.totalLabel = new JLabel();
      this.totalLabel.setFont(LABEL_TEXT_FONT);
      this.totalLabel.setText("$0.00");
      this.totalPanel.add(this.totalLabel);
    }

    return this.totalPanel;
  }

  private JPanel getButtonPanel() {
    if(this.buttonPanel == null) {
      this.buttonPanel = new JPanel();
      this.buttonPanel.setLayout(new GridLayout(1,0));

      JButton saleButton = new JButton();
      saleButton.setFont(BUTTON_TEXT_FONT);
      saleButton.setText("Sale");
      saleButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          if(saleButtonActionPerformed(evt)) { //if successful sale
            DriverGUI.backGui();
          }
        }
      });

      JButton cancelButton = new JButton();
      cancelButton.setFont(BUTTON_TEXT_FONT);
      cancelButton.setText("Cancel Sale");
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          cancelButtonActionPerformed(evt);
        }
      });

      JButton voidItemButton = new JButton();
      voidItemButton.setFont(BUTTON_TEXT_FONT);
      voidItemButton.setText("Void Item");
      voidItemButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          voidItemButtonActionPerformed(evt);
        }
      });

      this.moreButton = new JButton();
      this.moreButton.setFont(BUTTON_TEXT_FONT);
      this.moreButton.setText("More Options");
      this.moreButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          moreButtonActionPerformed(evt);
        }
      });

      this.buttonPanel.add(saleButton);
      this.buttonPanel.add(voidItemButton);
      this.buttonPanel.add(cancelButton);
      this.buttonPanel.add(this.moreButton);
    }

    return this.buttonPanel;
  }

  /**
   * This method initializes eastPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getEastPanel() {
    if(this.eastPanel == null) {
      this.eastPanel = new JPanel();
      //this.eastPanel.add(getMorePanel());
    }

    return this.eastPanel;
  }
  /**
   * This method initializes westPanel
   *
   * @return javax.swing.JPanel
   */
  private JPanel getWestPanel() {
    if(this.westPanel == null) {
      this.westPanel = new JPanel();
    }

    return this.westPanel;
  }

  /**
   * Event Actions
   */
  private boolean saleButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if(currentTransaction == null) {
      return false;
    }

    if(!(currentCustomer instanceof CashCustomer) &&
      currentTransaction.getTotal().compareTo(currentCustomer.getBalance()) > 0) { //TransactionTotal > Balance

      if(additionalFundsRequiredPopup(currentTransaction.getTotal().minus(currentCustomer.getBalance()))) {
        //cash half needs to be first, otherwise we lose the transaction total
        TransactionItem cashHalf = TransactionItem.createSpecialItem(TransactionItem.PARTIALCASH_CASHHALF_SKU, currentTransaction.getTotal().minus(currentCustomer.getBalance()));

        currentTransaction.addItem(TransactionItem.createSpecialItem(TransactionItem.PARTIALCASH_CREDITHALF_SKU, currentTransaction.getTotal().minus(currentCustomer.getBalance()).negate()));
        currentTransaction.commit();
        Customer customer = currentTransaction.getCustomer();
        customer.setBalance(Money.ZERO);
        customer.setDoTransaction(false);
        customer.update();
        customer.setDoTransaction(true);

        currentTransaction = new Transaction();
        currentTransaction.setCode(Transaction.CODE_DEBITACCOUNT);
        currentTransaction.setCashier(currentUser);
        currentTransaction.setCustomer(new CashCustomer());
        currentTransaction.addItem(cashHalf);
        currentTransaction.commit();

        return true;
      }
    }
    else if(currentCustomer instanceof CashCustomer) {
      if(additionalFundsRequiredPopup(currentTransaction.getTotal())) {
        currentTransaction.commit();
        return true;
      }
    }
    else {
      currentTransaction.commit(); //they must have enough money in their account
      Customer customer = currentTransaction.getCustomer();
      customer.setBalance(customer.getBalance().minus(currentTransaction.getTotal()));
      customer.setDoTransaction(false);
      customer.update();
      customer.setDoTransaction(true);

      return true;
    }

    currentTransaction = null;

    return false;
  }

  private void skuEnterButtonActionPerformed(java.awt.event.ActionEvent evt) {
    //quantity is one for now because we dont yet have a field for that
    TransactionItem currentItem = new TransactionItem(InventoryItem.findBySku(skuTextField.getText()), 1);

    if(currentTransaction == null) {
      currentTransaction = new Transaction();
      currentTransaction.setCode(Transaction.CODE_DEBITACCOUNT);
      currentTransaction.setCashier(currentUser);
      currentTransaction.setCustomer(currentCustomer);
    }
    currentTransaction.addItem(currentItem);

    Object[] data = {currentItem.getSku(), currentItem.getName(), currentItem.getPrice()};
    ((DefaultTableModel) this.itemTable.getModel()).addRow(data);

    //update tax and total fields
    refreshTotals();

    //scroll to bottom of table
    Rectangle visible = this.itemTable.getVisibleRect();
    visible.y = this.itemTable.getBounds().height;
    this.itemTable.scrollRectToVisible(visible);

    skuTextField.setText("");
    this.skuTextField.requestFocus();
  }

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
    //we will assume that a cancel means that they arent buying anything;
    // thus we can go to the next person in line
    currentTransaction = null;
    DriverGUI.backGui();
  }

  private void voidItemButtonActionPerformed(java.awt.event.ActionEvent evt) {
    DefaultTableModel model = (DefaultTableModel) this.itemTable.getModel();
    for(int i=0; i < model.getRowCount(); i++) {
      if(model.getValueAt(i, 0).equals(skuTextField.getText())) {
        currentTransaction.removeItem(skuTextField.getText());
        model.removeRow(i);
        //update fields
        refreshTotals();
        break;
      }
    }

    skuTextField.requestFocus();
  }

  private void cashOutButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if(currentCustomer instanceof CashCustomer) { //cant cash out a cash customer
      return;
    }

    try {
      if(currentTransaction != null) {
        saleButtonActionPerformed(evt);
        //after we make the transaction, we need to refresh the info
        currentCustomer = Customer.find(currentCustomer.getId());
      }

      if(currentCustomer.getBalance().isZero()) {
        return;
      }

      int result = JOptionPane.showConfirmDialog(this,
        "Refund Due: " + currentCustomer.getBalance(),
        "Customer Cash Out",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.INFORMATION_MESSAGE
      );

      switch(result) {
        case JOptionPane.OK_OPTION:
          currentTransaction = new Transaction();
          currentTransaction.setCode(Transaction.CODE_CLOSEACCOUNT);
          currentTransaction.setCashier(currentUser);
          currentTransaction.setCustomer(currentCustomer);
          currentTransaction.addItem(TransactionItem.createSpecialItem(TransactionItem.CORRECTION_SKU, currentCustomer.getBalance()));
          currentTransaction.commit();

          currentCustomer.setBalance(Money.ZERO);
          currentCustomer.setDoTransaction(false);
          currentCustomer.save();
          currentCustomer.setDoTransaction(true);
          DriverGUI.backGui();
        case JOptionPane.CANCEL_OPTION:
          break;
      }
    }
    catch (EntryAlreadyExistsException e) {
      DriverGUI.printError(e);
    }
  }

  private void moreButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if(this.eastPanel.getComponentCount() == 0) {
      this.eastPanel.add(getMorePanel(currentCustomer.isComplementary()));
      this.moreButton.setText("Hide Options");
    }
    else {
      this.eastPanel.removeAll();
      this.moreButton.setText("More Options");
    }

    this.contentPane.validate();
  }

  /**
   * Private Helper Functions
   */

  private void populateFields() {
    if(currentCustomer != null && currentCustomer.getId() > 0) {
      this.customerNameLabel.setText(currentCustomer.toString());
      this.balanceLabel.setText("$" + currentCustomer.getBalance().toString());

      /*
      if(currentCustomer.isComplementary()) {
              cashOutButton.setVisible(false);
              addToAccountButton.setVisible(false);
          }
      */

      if(currentCustomer.getNote() != null && !currentCustomer.getNote().equals("")) {
        this.northPanel.setLayout(new GridLayout(2,1));
        GridLayout noteLayout = (GridLayout) this.northPanel.getLayout();
        noteLayout.setRows(noteLayout.getRows() + 1);
        this.northPanel.add(getNotePanel());
        this.noteTextArea.setText(currentCustomer.getNote());
      }
    }
    else { //hi, im a cash customer, or we're testing
      this.customerNameLabel.setText("Cash");

      //hide unneeded fields
      this.balanceLabel.setVisible(false);
      this.moreButton.setVisible(false);
    }
  }

  private void refreshTotals() {
    try {
      this.totalLabel.setText("$" + currentTransaction.getTotal().toString());
      if(currentCustomer.getId() != 0) {
        this.balanceLabel.setText("$" + currentCustomer.getBalance().minus(currentTransaction.getTotal()).toString());
      }
    }
    catch (NullPointerException e) {
      return;
    }
  }

  /**
   * This method requests additional funds to complete the transaction.
   *
   * @return boolean: true if approved
   */
    private boolean additionalFundsRequiredPopup(Money val) {
      int result = JOptionPane.showConfirmDialog(this,
        "Cash Payment Due: $" + val.toString(),
        "Cash Due",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.INFORMATION_MESSAGE
      );

      return result == JOptionPane.OK_OPTION;
    }
}
