package org.koala;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import org.koala.model.Base;
import org.koala.model.Config;

/*
 * Created on May 12, 2007
 *
 */

/**
 * @author tom
 *
 */
abstract public class BackupMethod {

	private static Logger logger = Logger.getLogger(BackupMethod.class);

	public BackupMethod() {
	}

	abstract public ArrayList<String> getBackupPackageNames();
	abstract protected boolean loadPackage(String packageName);
	abstract public String createPackage(String tableName);

	public String createPackage() {
		return this.createPackage("");
	}

	public boolean restorePackage(String packageName) throws Exception {
		Base.clearDB();

		return this.loadPackage(packageName);
	}
}
