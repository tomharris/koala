package org.koala.ui.widget;
/*
 * Created on June 20, 2005
 *  the main purpose here it to make our purchased items in the cashier table
 *  non-editable and non-focusable.
 */

/**
 * @author tom
 *
 */

import org.koala.ui.DriverGUI;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ItemTable extends JTable {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;

  public ItemTable() {
    super();
  }

  public ItemTable(TableModel arg0) {
    super(arg0);
  }

  public ItemTable(TableModel arg0, TableColumnModel arg1) {
    super(arg0, arg1);
  }

  public ItemTable(TableModel arg0, TableColumnModel arg1, ListSelectionModel arg2) {
    super(arg0, arg1, arg2);
  }

  public ItemTable(int arg0, int arg1) {
    super(arg0, arg1);
  }

  public ItemTable(Vector arg0, Vector arg1) {
    super(arg0, arg1);
  }

  public ItemTable(Object[][] arg0, Object[] arg1) {
    super(arg0, arg1);
  }

  public boolean isCellEditable(int x, int y) {
    return false;
  }

  public boolean isFocusable() {
    return false;
  }
}
