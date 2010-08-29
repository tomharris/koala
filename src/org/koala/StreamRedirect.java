package org.koala;
/**
 * @author tom
 *
 * Here we handle all i/o streams for exec in separate threads. We
 *  also flush and close those streams. This is because we dont want
 *  to close the stream in the main thread before the child can finish
 *  writing to it.
 */

import java.io.*;

import org.koala.ui.DriverGUI;

public class StreamRedirect extends Thread {
  InputStream is;
  OutputStream os;

  StreamRedirect(InputStream is) {
    this(is, null);
  }

  StreamRedirect(InputStream input, OutputStream output) {
    this.is = input;
    this.os = output;
  }

  public void run() {
    try {
      for(int data = is.read(); data > 0; data = is.read()) {
        if(os != null)
          os.write(data);
      }
      if(os != null) {
        os.flush();
        os.close();
      }
      is.close();
    }
    catch (IOException e) {
      DriverGUI.printError(e);
    }
  }
}
