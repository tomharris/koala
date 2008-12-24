package org.koala.exception;
import org.koala.ui.DriverGUI;

/*
 * Created on Mar 31, 2007
 *
 */

/**
 * @author tom
 *
 */
public class OperationUnrecoverableException extends Exception {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

	public OperationUnrecoverableException(String message) {
	    super(message);
	}
}
