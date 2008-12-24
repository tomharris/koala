package org.koala;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * 
 */

/**
 * @author tom
 *
 */
public class DBDumpBackup extends BackupMethod {

	private static Logger logger = Logger.getLogger(DBDumpBackup.class);

	public DBDumpBackup(DBase dbHandle) {
		super(dbHandle);
	}

	public ArrayList<String> getBackupPackageNames() {
		String backupDir = Config.getConfig().getValue("db_backup_dir");
		if(backupDir == null) {
			logger.error("Config value db_backup_dir is required when backup method is set to database dump");
			return null;
		}
		
	    File dir = new File(backupDir);
	    
	    if (dir.list() == null) {
	        logger.error("Backup directory not found: " + backupDir);
	        return null;
	    }
	    
	    FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	        		if(name.startsWith("."))
	        			return false;
	        		else if(name.startsWith("backup_") && name.endsWith(".sql"))
	        			return true;
	        		else
	        			return false;
	        }
	    };

	    return new ArrayList<String>(Arrays.asList((dir.list(filter))));
	}

	public String createPackage(String tableName) {
	    Config config = Config.getConfig();
	    String cmd = config.renderString(config.getValue("db_backup_cmd"), tableName);
	    String filename = config.renderString(config.getValue("db_backup_filename"), tableName);
	    String backupDir = config.renderString(config.getValue("db_backup_dir"), "");

	    SystemCommand sysCmd = new SystemCommand(cmd, backupDir + '/' + filename, SystemCommand.OUTPUT);
	    if(!sysCmd.exec())
	    	filename = null;

	    return filename;
	}

	protected boolean loadPackage(String packageName) {
	    Config config = Config.getConfig();

	    String backupDir = config.renderString(config.getValue("db_backup_dir"), "");
	    String cmd = config.renderString(config.getValue("db_restore_cmd"), null);

	    SystemCommand sysCmd = new SystemCommand(cmd, backupDir + '/' + packageName, SystemCommand.INPUT);
	    sysCmd.exec();

	    return sysCmd.exec();
	}
}
