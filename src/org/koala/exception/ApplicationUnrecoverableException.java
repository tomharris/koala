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
public class ApplicationUnrecoverableException extends Exception {
	public static final long serialVersionUID = DriverGUI.serialVersionUID;

	public ApplicationUnrecoverableException(String message) {
	    super(message);
	}
}
