package cs456.emailclient.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import cs456.emailclient.Main;
import cs456.emailclient.util.IOUtilities;

/**
 * 1) handles the storage and retrieval of message from the hard disk.
 * 2) lets the user interface know when a new message is ready
 * 3) keeps track of the unique id
 */
public class MessageBox
{
	/**
	 * should be incremented only through incrementMessageIdCounter()
	 */
	public static long messageIdCounter = 0L;
	/**
	 * list of CachedMessages
	 */
	public static MessageList messages = new MessageList();
	
	public static Vector addedObservers = new Vector();
	
	private static void incrementMessageIdCounter()
	{
		messageIdCounter++;
		Main.PushSettingsToFileSystem();
	}
	
	private static void writeHelper(String s, DataOutputStream out) throws IOException
	{
		if(s != null)
			out.writeUTF(s);
		else
			out.writeUTF("NULL");
	}
	
	private static boolean writeHeader(Message message)
	{
		boolean success = false;
		String fileName = Main.HEADER_FOLDER + messageIdCounter + ".txt";
		FileConnection file = null;
		DataOutputStream out = null;
		try
		{
			file = (FileConnection)Connector.open(fileName);
			
			if(file.exists())
			{
				Log.error(fileName + " already existed. Trying a different one.");
				file.close();
				messageIdCounter++;
				return writeHeader(message);
			}
			file.create();
			out = file.openDataOutputStream();
			
			out.writeLong(messageIdCounter);
			writeHelper(message.subject, out);
			writeHelper(message.date, out);
			writeHelper(message.from, out);
			writeHelper(message.to, out);
			
			out.close();
			file.close();
			success = true;
		}
		catch(IOException e)
		{
			Log.error(e.getMessage());
			IOUtilities.closeConnection(file);
			IOUtilities.closeOutputStream(out);
		}
		return success;
	}
	
	private static boolean writeBody(Message message)
	{
		boolean success = false;
		String fileName = Main.BODY_FOLDER + messageIdCounter + ".txt";
		FileConnection file = null;
		DataOutputStream out = null;
		try
		{
			file = (FileConnection)Connector.open(fileName);
			if(file.exists())
			{
				Log.error(fileName + " already existed");
				return false;
			}
			file.create();
			out = file.openDataOutputStream();
			
			writeHelper(message.getBody(), out);
			
			out.close();
			file.close();
			success = true;
		}
		catch(IOException e)
		{
			Log.error(e.getMessage());
			IOUtilities.closeConnection(file);
			IOUtilities.closeOutputStream(out);
		}
		return success;
	}
	
	public static void AddMessage(Message message)
	{
		if(writeHeader(message))
		{
			if(writeBody(message))
			{
		
				CachedMessage cachedMessage = new CachedMessage();
				cachedMessage.subject = message.subject;
				cachedMessage.date = message.date;
				cachedMessage.from = message.from;
				cachedMessage.to = message.to;
				cachedMessage.messageId = messageIdCounter;
				
				messages.addMessage(cachedMessage);
				
				incrementMessageIdCounter();
				
				updateAddedObservers(cachedMessage);
			}
		}
	}
	
	private static void grabHeader(String fileName)
	{
		FileConnection file = null;
		DataInputStream in = null;
		try
		{
			file = (FileConnection)Connector.open(fileName);
			in = file.openDataInputStream();
			
			CachedMessage message = new CachedMessage();
			message.messageId	= in.readLong();
			message.subject		= in.readUTF();
			message.date		= in.readUTF();
			message.from		= in.readUTF();
			message.to			= in.readUTF();
			
			in.close();
			file.close();
			
			messages.addMessage(message);
			updateAddedObservers(message);
		}
		catch(IOException e)
		{
			Log.error(e.toString());
			IOUtilities.closeInputStream(in);
			IOUtilities.closeConnection(file);
		}
	}
	
	public static void loadHeaders()
	{
		FileConnection headerFolder = null;
		String fileName = null;
		try
		{
			headerFolder = (FileConnection)Connector.open(Main.HEADER_FOLDER);
			
			Enumeration files = headerFolder.list();
			while(files.hasMoreElements())
			{
				fileName = (String)files.nextElement();
				grabHeader(Main.HEADER_FOLDER + fileName);
			}
			headerFolder.close();
		}
		catch(IOException e)
		{
			Log.error(e.toString());
			IOUtilities.closeConnection(headerFolder);
		}
	}
	
	private static void updateAddedObservers(CachedMessage message)
	{
		for(int x = 0; x < addedObservers.size(); x++)
		{
			AddedObserver observer = (AddedObserver)addedObservers.elementAt(x);
			observer.Added(message);
		}	
	}
	public static void AddAddedObserver(AddedObserver observer)
	{
		addedObservers.addElement(observer);
	}
}
