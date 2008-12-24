package org.koala.exception;
import org.koala.ui.DriverGUI;

/*
 * Created on Apr 23, 2005
 *
 */

/**
 * @author tom
 *
 */
public class EntryAlreadyExistsException extends Exception {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

    public EntryAlreadyExistsException(String msg) {
        super(msg);
    }
}
