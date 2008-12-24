package org.koala;
/**
 * @author tom
 *
 */

public class MySQLProfile extends DatabaseProfile {
	protected String autoIncCmd;

	public MySQLProfile() {
		super();
		dbClass = "com.mysql.jdbc.Driver";
		passwordCmd = "MD5(?)";
		timeCmd = "NOW()";

		autoIncCmd = "select LAST_INSERT_ID()";
	}

	public String getAutoIncCmd(String tableName, String fieldName) {
		return autoIncCmd;
	}
}
