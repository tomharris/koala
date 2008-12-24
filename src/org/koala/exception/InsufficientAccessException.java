package org.koala.exception;
import org.koala.ui.DriverGUI;

/*
 * Created on Apr 20, 2005
 *
 */

/**
 * @author tom
 *
 */
public class InsufficientAccessException extends Exception {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

	public InsufficientAccessException(String message) {
	    super(message);
	}
}
