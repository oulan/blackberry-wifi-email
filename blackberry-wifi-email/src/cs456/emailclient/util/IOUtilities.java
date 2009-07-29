package cs456.emailclient.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import cs456.emailclient.models.Log;

public class IOUtilities 
{
	
	public static class FileExistsResult
	{
		public boolean success = false;
		public boolean existed = false;
	}

	public static FileExistsResult makeSureFileIsThere(String fileName)
	{
		FileExistsResult result = new FileExistsResult();
		FileConnection file = null;
		try
		{
			file = (FileConnection)Connector.open(fileName);
			if(!file.exists())
			{
				result.existed = false;
				file.create();
			}
			else
			{
				result.existed = true;
			}
			
			file.close();
			result.success = true;
		}
		catch(IOException e)
		{
	    	Log.info(e.toString());
			Log.info("Could not create the " + fileName + " file");
			closeConnection(file);
		}
		return result;
	}
	
	public static boolean makeSureFolderIsThere(String folderName)
	{
    	boolean success = false;
		FileConnection folder = null;
		try
		{
			folder = (FileConnection)Connector.open(folderName);
			if(!folder.exists())
				folder.mkdir();
			
			folder.close();
			success = true;
		}
		catch(IOException e)
		{
	    	Log.info(e.toString());
			Log.info("Could not create the " + folderName + " folder");
			IOUtilities.closeConnection(folder);
		}
		return success;
	}
	
	
	
	public static void closeConnection(Connection connection)
	{
		try
		{
			if(connection != null)
			{
				connection.close();
			}
		}
		catch(IOException e) {}
	}
	
	public static void closeOutputStream(OutputStream out)
	{
		try
		{
			if(out != null)
			{
				out.close();
			}
		}
		catch(IOException e2) {}
	}
	
	public static void closeInputStream(InputStream in)
	{
		try
		{
			if(in != null)
			{
				in.close();
			}
		}
		catch(IOException e2) {}
	}



}
