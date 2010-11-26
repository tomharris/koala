package org.koala.model;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.log4j.Logger;

public class Config {
  private HashMap<String, String> config;
  private static Config currentConfig = null;

  private static Logger logger = Logger.getLogger(Config.class);

  private Config(HashMap<String, String> config) {
    this.config = config;
  }

  protected void finalize() {
    this.config.clear();
    this.config = null;
  }

  public String getValue(String key) {
    return config.get(key);
  }

  public static synchronized Config getConfig() {
    //If you dont load a config, all you get is the defaults
    if(Config.currentConfig == null)
      Config.currentConfig = new Config(Config.loadDefaults());

    return currentConfig;
  }

  protected static HashMap<String, String> loadDefaults() {
    HashMap<String, String> defaults = new HashMap<String, String>();

    //database info
    defaults.put("db_type", "postgresql");
    defaults.put("db_host", "localhost");

    //printer
    defaults.put("print_device", "/dev/printer");

    //setup file
    defaults.put("pos_pathto_setup", "pos_setup_$db_type$.sql");

    //backup info
    defaults.put("db_backup_method", "dump");
    defaults.put("db_backup_dir", "./backup");
    defaults.put("db_backup_filename", "backup_$_input$_$currentdatetime$.sql");

    //mysql backup commands
    defaults.put("db_backup_cmd", "mysqldump --quick --host=$db_host$ --user=$db_user$ --password=$db_pass$ $db_name$ $_input$");
    defaults.put("db_restore_cmd", "mysql --host=$db_host$ --user=$db_user$ --password=$db_pass$ $db_name$");

    //postgresql backup commands (no cmdline passwords; use a 'trust' account)
    defaults.put("db_superuser", "postgres");
    defaults.put("db_backup_cmd", "pg_dump --inserts --host=$db_host$ --username=$db_user$ <!--table=$_input$!> $db_name$");
    defaults.put("db_restore_cmd", "psql -X -q -d $db_name$ -h $db_host$ -U $db_superuser$");

    return defaults;
  }

  public String renderString(String string, String input) {
    String sRegex = "\\$(.+?)\\$";
    String iRegex = "<!(.+?)!>";
    String variable = null;
    String backRef = null;
    Pattern pRegex = Pattern.compile(sRegex);
    Pattern ipRegex = Pattern.compile(iRegex);
    Matcher m = pRegex.matcher(string);

    while(m.find()) {
      backRef = m.group(1);
      variable = getValue(backRef);
      if(variable != null)
        string = m.replaceFirst(variable);
      else if(backRef.equals("currentdatetime")) {
        Date now = new Date();
        string = m.replaceFirst(now.toString().replaceAll(" ","_"));
      }
      else if(backRef.equals("_input")) {
        string = m.replaceFirst(input);
      }

      m = pRegex.matcher(string); //match against the new string
    }

    //the <! dfsdf !> section only appears when 'input' is used
    m = ipRegex.matcher(string);
    if(m.find()) {
      backRef = "";
      if(input != null && input != "")
        backRef = m.group(1);

      string = m.replaceFirst(backRef);
    }

    return string;
  }

  public static void loadConfig(InputStream fileStream) {
    HashMap<String, String> configMap = Config.loadDefaults();
    BufferedReader bin;

    String cfgRegex = "^\\$(.+?)\\s+?=\\s+?(.+?)$";
    String whitespaceRegex = "^\\s*$";
    Pattern pRegex = Pattern.compile(cfgRegex);
    Pattern wsRegex = Pattern.compile(whitespaceRegex);
    Matcher m;

    try {
      bin = new BufferedReader(new InputStreamReader(fileStream));

      String line = null;
      while((line = bin.readLine()) != null) {
        //check for comment
        if(line.startsWith("//") || line.startsWith("#") )
          continue;

        //check for blankline
        m = wsRegex.matcher(line);
        if(m.find())
          continue;

        //look for config vars
        m = pRegex.matcher(line);
        m.find();

        //if there is a variable and a value; and the variable is valid
        if(m.groupCount() == 2) {
          if(configMap.put(m.group(1), m.group(2)) != null) {
            logger.debug("Replacing default value for variable '" + m.group(1) + "' with value '" + m.group(2) + "'");
          }
        }
        else {
          logger.error("Config error on line: " + line);
        }
      }

      bin.close();
      fileStream.close();
    }
    catch (IllegalStateException e) {
      logger.error("Error parsing config file. All config variables will use defaults.", e);
    }
    catch (IOException e) {
      logger.error("Error loading config file. All config variables will use defaults.", e);
    }

    Config.currentConfig  = new Config(configMap);
  }
}
