package org.koala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class DatabaseConnection {

  private static Logger logger = Logger.getLogger(DatabaseConnection.class);

  private Connection con;
  private Config currentConfig;
  private DatabaseProfile dbProfile;

  private static class DatabaseConnectionHolder {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
  }

  public static DatabaseConnection getInstance() {
    return DatabaseConnectionHolder.INSTANCE;
  }

  private DatabaseConnection() {
    //load config
    currentConfig = Config.getConfig();
    String dbClass = null;
    String url = null;

    try {
      //load database driver; more databases to come i hope
      if(currentConfig.getValue("db_type").equals("mysql")) {
        dbProfile = new MySQLProfile(); //mysql
      }
      else if(currentConfig.getValue("db_type").equals("postgresql")) {
        dbProfile = new PostgreSQLProfile(); //postgresql
      }
      else {
        throw new ClassNotFoundException("Database Type: " + currentConfig.getValue("db_type") + " is not supported");
      }

      dbClass = dbProfile.getDBClass();
      Class.forName(dbClass).newInstance();
    }
    catch (Exception e) {
      logger.error("SQL error finding profile class for DB of type: " + currentConfig.getValue("db_type"), e);
    }

    try {
      url = "jdbc:" +
      currentConfig.getValue("db_type") + "://" +
      currentConfig.getValue("db_host") + "/" +
      currentConfig.getValue("db_name");

      //open a connection to the db
      con = DriverManager.getConnection(url, currentConfig.getValue("db_user"), currentConfig.getValue("db_pass"));
    }
    catch (SQLException e) {
      logger.error("SQL error connecting to DB: URL: " + url, e);
    }
  }

  public Connection getConnection() {
    return this.con;
  }

  public DatabaseProfile getProfile() {
    return this.dbProfile;
  }

  protected void finalize() {
    this.disconnect();
    this.currentConfig = null;
    this.dbProfile = null;
  }

  public void disconnect() {
    try {
      if(this.con != null) {
        this.con.close();
        this.con = null;
      }
    }
    catch (SQLException e) {
      this.con = null;
    }
  }
}