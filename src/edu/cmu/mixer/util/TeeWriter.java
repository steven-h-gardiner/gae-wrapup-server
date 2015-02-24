package edu.cmu.mixer.util;

public class TeeWriter extends java.io.Writer {
  private java.io.Writer main = null;
  private java.io.Writer[] tees = null;

  public TeeWriter(java.io.Writer main, java.io.Writer ... tees) {
    this.main = main;
    this.tees = tees;
  }

  public void close() throws java.io.IOException {
    if (main != null) {
      main.close();
    }
    for (java.io.Writer tee : tees) {
      tee.close();
    }
  }
  public void flush() throws java.io.IOException  {
    if (main != null) {
      main.flush();
    }
    for (java.io.Writer tee : tees) {
      tee.flush();
    }
  }
  public void write(char[] cbuf, int off, int len) throws java.io.IOException {
    if (main != null) {
      main.write(cbuf,off,len);
    }
    for (java.io.Writer tee : tees) {
      tee.write(cbuf,off,len);
    }
    
  }
}
