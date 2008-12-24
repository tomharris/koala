package org.koala;
import java.io.*;

import org.koala.ui.DriverGUI;

public class SystemCommand {
	public static final int NONE = 0;
	public static final int INPUT = 1;
	public static final int OUTPUT = 2;
	private String command;
	private String filename;
	private InputStream inputStream;
	private OutputStream outputStream;
	private int type;

	SystemCommand(String command) {
		this(command, (String)null, NONE);
	}

	SystemCommand(String command, String filename, int type) {
		this.command = command;
		this.filename = filename;
		this.inputStream = null;
		this.outputStream = null;
		this.type = type;
	}

	SystemCommand(String command, InputStream fileStream, int type) {
		this.command = command;
		this.filename = null;
		this.inputStream = fileStream;
		this.outputStream = null;
		this.type = type;
	}

	SystemCommand(String command, OutputStream fileStream, int type) {
		this.command = command;
		this.filename = null;
		this.inputStream = null;
		this.outputStream = fileStream;
		this.type = type;
	}

	public boolean exec() {
		boolean result = false;

        try {
        	//prep input/output file if exists
        	// StreamRedirect will flush/close the streams
        	InputStream inStream = null;
        	OutputStream outStream = null;
        	if(type != NONE) {
        		if(filename != null) {
        			if(type == INPUT)
        				inStream = new FileInputStream(filename);
        			else if(type == OUTPUT)
        				outStream = openOutputFile(filename); //creates if doesnt exist
        		}
        		else if(inputStream != null) {
        			if(type == INPUT)
        				inStream = inputStream;
        		}
        		else if(outputStream != null) {
        			if(type == OUTPUT)
        				outStream = outputStream;
        		}
        	}

        	//call exec()
        	Process p;
        	p = Runtime.getRuntime().exec(command);

        	//dev/null the stderr
        	StreamRedirect stderr = new StreamRedirect(p.getErrorStream());

        	//send stdout to the file (if there is no outfile, we just lose the output)
        	StreamRedirect stdout = new StreamRedirect(p.getInputStream(), outStream);

        	//send the stdin the output of a file
        	StreamRedirect stdin = null;
        	if(inStream != null)
        	    stdin = new StreamRedirect(inStream, p.getOutputStream());

        	//start the redirection
        	stderr.start();
        	stdout.start();
        	if(stdin != null)
        	    stdin.start();

        	//wait for end of exec
        	p.waitFor();

        	result = true;
        }
        catch (IOException e) {
        	DriverGUI.printError(e);
        }
        catch (InterruptedException e) {
        	DriverGUI.printError(e);
        }

        return result;
    }

	private FileOutputStream openOutputFile(String filename) throws IOException {
	    File file = new File(filename);
	    if(file.exists())
	        return new FileOutputStream(filename);
	    else if(file.createNewFile())
            return new FileOutputStream(filename);
        else
            throw new IOException("SystemCommand output file can not be created.");
	}
}
