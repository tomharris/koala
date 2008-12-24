package org.koala;
/**
 * @author tom
 *
 */

public abstract class DatabaseProfile {
	protected String dbClass;
	protected String passwordCmd;
	protected String timeCmd;

	public DatabaseProfile() {
	}

	public String getDBClass() {
		return dbClass;
	}

	public abstract String getAutoIncCmd(String tableName, String fieldName);

	public String getPasswordCmd() {
		return passwordCmd;
	}
	
	public String getCurrentTimeCmd() {
		return timeCmd;
	}
}
