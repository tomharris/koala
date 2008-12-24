package org.koala;
/**
 * @author tom
 *
 */

public class PostgreSQLProfile extends DatabaseProfile {
	protected String autoIncCmd;

	public PostgreSQLProfile() {
		super();
		dbClass = "org.postgresql.Driver";
		passwordCmd = "MD5(?)";
		timeCmd = "NOW()";

		autoIncCmd = "select currval(\'?_?_seq\')";
	}

	public String getAutoIncCmd(String tableName, String fieldName) {
		return autoIncCmd.replaceFirst("\\?", tableName).replaceFirst("\\?", fieldName);
	}
}
