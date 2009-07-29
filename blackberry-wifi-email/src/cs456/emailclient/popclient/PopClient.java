package cs456.emailclient.popclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.SecureConnection;

import cs456.emailclient.models.Log;

public class PopClient
{
	static SecureConnection connectionToPopServer;
	static DataInputStream	fromPopServer;
	static DataOutputStream	toPopServer;
	static String 			email;
	static String 			password;
	static String 			popServer;
	static String 			popPort;
	static Vector			messageOrdinals;
	/**
	 * unused
	 */
	static Vector			uids;
	static Vector			messageSizes;
	
	/**
	 * completely closes all connections and data streams
	 */
	public static void closeEverything()
	{
		try {
			if(connectionToPopServer != null)
				connectionToPopServer.close();
		} catch (IOException e1) {}
		try {
			if(fromPopServer != null)
				fromPopServer.close();
		} catch (IOException e1) {}
		try {
			if(toPopServer != null)
				toPopServer.close();
		} catch (IOException e1) {}
	}
	
	/**
	 * starts up the pop connection and reads the +OK from the server
	 */
	static void startPop() throws PopException
	{
		try
		{
			String connectionString = "tls://" + PopClient.popServer + ":" + PopClient.popPort +";interface=wifi";
			Log.info("Connection Stirng: " + connectionString);
			connectionToPopServer = (SecureConnection)Connector.open(connectionString);
			fromPopServer = connectionToPopServer.openDataInputStream();
			toPopServer = connectionToPopServer.openDataOutputStream();
			
			String str = readLine(fromPopServer);
			Log.info(str);
		}
		catch(IOException e)
		{
			throw new PopException.PopCommunicationException(e.getMessage());
		}
	}
	
	/**
	 * logs the user into the maildrop
	 * @throws IOException
	 */
	static void login() throws PopException
	{
		try
		{
			//USERNAME
			String output = "USER " + PopClient.email + "\r\n";
			Log.info("writing: " + output + " to pop server");
			toPopServer.write(output.getBytes(), 0, output.getBytes().length);
			toPopServer.flush();
			
			String str = readLine(fromPopServer);
			Log.info(str);
			if(str.startsWith("-"))
				throw new PopException.BadCredentialException("Pop server returned: " + str);
			
			
			
			//PASSWORD
			output = "PASS " + PopClient.password + "\r\n";
			Log.info("writing: " + output + " to pop server");
			toPopServer.write(output.getBytes(), 0, output.getBytes().length);
			toPopServer.flush();
			
			str = readLine(fromPopServer);
			Log.info(str);
			if(str.startsWith("-"))
				throw new PopException.BadCredentialException("Pop server returned: " + str);
		}
		catch(IOException e)
		{
			throw new PopException.PopCommunicationException(e.getMessage());
		}
	}
	
	/**
	 * gets the list of messages from the mail drop
	 * ordinal and size
	 * @throws IOException
	 */
	static void list() throws PopException
	{
		try
		{
			messageOrdinals = new Vector();
			messageSizes = new Vector();
			//list all the messages
			String output = "LIST\r\n";
			Log.info("writing: " + output + " to pop server");
			toPopServer.write(output.getBytes(), 0, output.getBytes().length);
			toPopServer.flush();
			
			String str = readLine(fromPopServer);
			if(str.startsWith("+OK"))
			{
				str = readLine(fromPopServer);
				while(!str.equals("."))
				{
					String id = parseNthToken(str, 0);
					String size = parseNthToken(str, 1);
					messageOrdinals.addElement(id);
					messageSizes.addElement(size);
					Log.info("ID: " + id + " SIZE: " + size);
					str = readLine(fromPopServer);
				}
			}
			else
			{
				throw new PopException.PopCommunicationException("Pop server returned: " + str);
			}
		}
		catch(IOException e)
		{
			throw new PopException.PopCommunicationException(e.getMessage());
		}
	}
	
	/**
	 * 0 indicates the first token
	 * 1 indicates the second token
	 * etc
	 * @param line
	 * @param n
	 * @return
	 */
	private static String parseNthToken(String line, int n)
	{
		String token = "";
		int counter = 0;
		
		for(int x = 0; x < n; x++)
		{
			//finds the first space
			while(counter < line.length() && line.charAt(counter) != ' ')
			{
				counter++;
			}
			counter++;
		}
		//this is the nth token
		while(counter < line.length() && line.charAt(counter) != ' ')
		{
			token = token + line.charAt(counter);
			counter++;
		}	
		return token;
	}
	
	/**
	 * 1) gets the message from the server
	 * 2) passes it to the parser
	 * 3) parser passes it to the MessageBox (acts as storage and model for views)
	 * @param id
	 * @param strSize
	 * @throws IOException
	 */
	static void getMessage(String id, String strSize) throws PopException
	{
		try
		{
			Vector lines = new Vector();
			//int size = Integer.parseInt(strSize);
			//retrieve the message from the pop server
			String output = "RETR " + id + "\r\n";
			Log.info("writing: " + output + " to pop server");
			toPopServer.write(output.getBytes(), 0, output.getBytes().length);
			toPopServer.flush();
			
			String str = readLine(fromPopServer);
			if(str.startsWith("+OK"))
			{
				str = readLine(fromPopServer);
				while(!str.equals("."))
				{
					lines.addElement(str);
					str = readLine(fromPopServer);
				}
			}
			else
			{
				throw new PopException.PopCommunicationException("server returned: " + str);
			}
		
			IMFParser.ParseIMF(lines);
		}
		catch(IOException e)
		{
			throw new PopException.PopCommunicationException(e.getMessage());
		}
	}
	
	/**
	 * Sends the QUIT command to the pop server and
	 * closes all the data streams and the connection.
	 * @throws IOException
	 */
	static void endPop() throws PopException
	{
		try
		{
			String output = "QUIT\r\n";
			Log.info("writing: " + output + " to pop server");
			toPopServer.write(output.getBytes(), 0, output.getBytes().length);
			toPopServer.flush();
			
			String str = readLine(fromPopServer);
			Log.info(str);
			
			Log.info("closing connection");
			closeEverything();
		}
		catch(IOException e)
		{
			throw new PopException.PopCommunicationException(e.getMessage());
		}
	}
	
	/**
	 * helper method to read line by line (delimited by CRLF)
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static String readLine(DataInputStream input) throws IOException
	{
		String returnString = "";
		
		int currentByte = input.read();
		while(currentByte != (int)'\n')
		{
			if(currentByte == (int)'\r'){}
			else
				returnString += (char)currentByte;
			currentByte = input.read();
		}
		return returnString;
	}
	
//	/**
//	 * Other Unused Stuff
//	 */
//	
//	/**
//	 * When ever there is a problem with the connection, this guy
//	 * is called. It closes connections and sets connected to false;
//	 * @param e
//	 */
//	private static void connectionProblem(IOException e)
//	{
//		Log.error(e.toString());
//		Log.error("could not connect to the pop sever.");
//		closeEverything();
//	}
//	
//	/***********************************************
//	 * UNUSED POP COMMANDS
//	 ***********************************************/
//	
//	/**
//	 * gets the amount of messages on the server
//	 * @throws IOException
//	 */
//	private static void stat() throws IOException
//	{
//		//STAT
//		String output = "STAT\r\n";
//		Log.info("writing: " + output + " to pop server");
//		toPopServer.write(output.getBytes(), 0, output.getBytes().length);
//		toPopServer.flush();
//		
//		String str = readLine(fromPopServer);
//		Log.info(str);
//	}
//	
//	/**
//	 * Method used to get the unique ids of the messages on the maildrop.
//	 * 
//	 * This method is no longer used. We were expecting to use it for multiple
//	 * access to the maildrop; however, we learned that the maildrops (gmail)
//	 * only keep the messages that have not been retrieved on the maildrop.
//	 * @throws IOException
//	 */
//	private static void uidl() throws IOException
//	{
//		messageOrdinals = new Vector();
//		uids = new Vector();
//		//list all the messages
//		String output = "UIDL\r\n";
//		Log.info("writing: " + output + " to pop server");
//		toPopServer.write(output.getBytes(), 0, output.getBytes().length);
//		toPopServer.flush();
//		
//		String str = readLine(fromPopServer);
//		if(str.startsWith("+OK"))
//		{
//			str = readLine(fromPopServer);
//			while(!str.equals("."))
//			{
//				String ordinal = parseNthToken(str, 0);
//				String uid = parseNthToken(str, 1);
//				messageOrdinals.addElement(ordinal);
//				uids.addElement(uid);
//				Log.info("UID: " + uid);
//				str = readLine(fromPopServer);
//			}
//		}
//		else
//		{
//			Log.error("UIDL not supported");
//		}
//		
//	}
//	
//	/**
//	 * Communicates with the pop server to retrieve the size
//	 * of the provided message.
//	 * @param ordinal
//	 * @return the size of the given message
//	 * @throws IOException
//	 */
//	private static int getSize(String ordinal) throws IOException
//	{
//		String output = "LIST " + ordinal + "\r\n";
//		Log.info("writing: " + output + " to pop server");
//		toPopServer.write(output.getBytes(), 0, output.getBytes().length);
//		toPopServer.flush();
//		
//		String str = readLine(fromPopServer);
//		if(str.startsWith("+OK"))
//		{
//			String strSize = parseNthToken(str, 2);
//			Log.info("Got size: " + strSize);
//			return Integer.parseInt(strSize);
//		}
//		else
//		{
//			Log.error("Could not retrieve the size of message: " + ordinal);
//			return -1;
//		}
//	}
//	
//	/**
//	 * Resets deleted messages. We thought this would lets us access the same message
//	 * over and over again on multiple connections; however, we learned that GMAIL
//	 * only sends messages that have not been retrieved yet.
//	 * 
//	 * This is no long used.
//	 * @throws IOException
//	 */
//	private static void reset() throws IOException
//	{
//		String output = "RSET\r\n";
//		Log.info("writing: " + output + " to pop server");
//		toPopServer.write(output.getBytes(), 0, output.getBytes().length);
//		toPopServer.flush();
//		
//		String str = readLine(fromPopServer);
//		Log.info(str);
//	}
	
}
