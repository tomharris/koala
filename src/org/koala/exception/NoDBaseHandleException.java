package org.koala.exception;
import org.koala.ui.DriverGUI;

/*
 * Created on Apr 21, 2005
 *
 */

/**
 * @author tom
 *
 */
public class NoDBaseHandleException extends Exception {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

    public NoDBaseHandleException(String message) {
        super(message);
    }
}
