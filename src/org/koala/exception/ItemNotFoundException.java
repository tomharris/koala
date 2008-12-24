package org.koala.exception;
import org.koala.ui.DriverGUI;

/*
 * Created on Jun 23, 2005
 *
 */

/**
 * @author tom
 *
 */
public class ItemNotFoundException extends Exception {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

    public ItemNotFoundException(String msg) {
        super(msg);
    }
}
