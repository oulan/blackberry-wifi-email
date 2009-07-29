package cs456.emailclient.models;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import cs456.emailclient.Main;

public class Log 
{

    private static String LOG_FILE_NAME = Main.PROGRAM_FOLDER + "log.txt";
	
	/**
	 * Contains only AddedObservers
	 */
	static Vector addedObservers = new Vector();
	
	public static void println(String line)
	{
		System.out.println(line);
		Log.fileOutput(line + "\n");
		Log.uiOutput(line);
	}
	
	public static void uiOutput(String line)
	{
		for(int x = 0; x < addedObservers.size(); x++)
		{
			AddedObserver observer = (AddedObserver)addedObservers.elementAt(x);
			observer.Added(line);
		}
	}
	
	public static void info(String line)
	{
		println(line);
	}
	
	public static void warn(String line)
	{
		println("W " + line);
	}
	
	public static void error(String line)
	{
		println("* " + line);
	}
	
	public static void AddAddedObserver(AddedObserver observer)
	{
		addedObservers.addElement(observer);
	}
	
    private static void fileOutput(String s)
    {
        synchronized(addedObservers)
        {
            try
            {
                FileConnection logFile = (FileConnection)Connector.open(LOG_FILE_NAME);
                
                
                //create the trace file if it does not exist
                if(!logFile.exists())
                	logFile.create();
                
                long size = logFile.fileSize();
                DataOutputStream out = new DataOutputStream(logFile.openOutputStream(size));
                
                out.write(s.getBytes());
                
                out.flush();
                out.close();
                logFile.close();
            }
            catch(IOException e)
            {
            	Log.uiOutput(e.toString());
            	Log.uiOutput("could not write the log entry to the file");
            }
        }       
    }	
}
