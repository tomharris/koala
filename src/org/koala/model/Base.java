package org.koala.model;
/*
 * Created on Dec 22, 2009
 */

/**
 * @author tom
 *
 */

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
}
