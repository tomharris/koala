package org.koala;
/*
 * Created on Apr 14, 2005
 */

/**
 * @author tom
 *
 * Handles all the heavy lifting for the interaction of the system and the dbase
 *
 */

import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.koala.model.*;
import org.koala.exception.EntryAlreadyExistsException;

public class DBase {
  private static Logger logger = Logger.getLogger(DBase.class);

  public DBase() {
  }

  public void finalize() {
    DatabaseConnection.getInstance().disconnect();
  }
}
