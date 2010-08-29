package org.koala.ui.widget;
/*
 * Created on Aug 3, 2005
 *
 */

/**
 * @author tom
 *
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import org.koala.ui.DriverGUI;

public class NoteTextArea extends javax.swing.JTextArea {
  public static final long serialVersionUID = DriverGUI.serialVersionUID;

  public NoteTextArea() {
    // bind our new forward focus traversal keys
    Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(1);
    newForwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB,0));
    this.setFocusTraversalKeys(
      KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
      Collections.unmodifiableSet(newForwardKeys)
    );

    // bind our new backward focus traversal keys
    Set<AWTKeyStroke> newBackwardKeys = new HashSet<AWTKeyStroke>(1);
    newBackwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB,KeyEvent.SHIFT_MASK+KeyEvent.SHIFT_DOWN_MASK));
    this.setFocusTraversalKeys(
      KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
      Collections.unmodifiableSet(newBackwardKeys)
    );
  }
}
