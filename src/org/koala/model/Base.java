package org.koala.model;
/*
 * Created on Dec 22, 2009
 */

/**
 * @author tom
 *
 */

import org.koala.exception.EntryAlreadyExistsException;

abstract public class Base {
  protected int id; //non-neg if valid

  public Base() {
    this.id = 0;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String toString() {
    return this.getClass().getSimpleName() + "(#" + this.id + ")";
  }

  public boolean isNewRecord() {
    return this.getId() <= 0;
  }

  protected void create() throws EntryAlreadyExistsException {
  }

  protected void update() {
  }

  public void save() throws EntryAlreadyExistsException {
    if(this.isNewRecord()) {
      this.create();
    }
    else {
      this.update();
    }
  }
}
