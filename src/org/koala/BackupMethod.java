package org.koala;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/*
 * Created on May 12, 2007
 *
 */

/**
 * @author tom
 *
 */
abstract public class BackupMethod {

	protected DBase dbHandle;

	private static Logger logger = Logger.getLogger(BackupMethod.class);

	public BackupMethod(DBase dbHandle) {
		this.dbHandle = dbHandle;
	}

	abstract public ArrayList<String> getBackupPackageNames();
	abstract protected boolean loadPackage(String packageName);
	abstract public String createPackage(String tableName);

	public String createPackage() {
		return this.createPackage("");
	}

	public boolean restorePackage(String packageName) throws Exception {
		this.dbHandle.clearDB();
		this.dbHandle.finalize();
		try {
			this.dbHandle = new DBase();
		}
		catch (Exception e) {
			logger.error("Error connecting to Database", e);
		    throw new Exception("Error connecting to Database");
		}
		return this.loadPackage(packageName);
	}
}
