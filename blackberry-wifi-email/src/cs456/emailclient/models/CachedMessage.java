package cs456.emailclient.models;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import cs456.emailclient.Main;
import cs456.emailclient.util.IOUtilities;

public class CachedMessage extends Message 
{
	public long messageId = -1L;
	
	public String getBody()
	{
		String returnString;
		FileConnection bodyFile = null;
		DataInputStream in = null;
		try
		{
			bodyFile = (FileConnection)Connector.open( Main.BODY_FOLDER + messageId + ".txt" );
			in = bodyFile.openDataInputStream();
			returnString = in.readUTF();
			
			in.close();
			bodyFile.close();
		}
		catch(IOException e)
		{
			returnString = e.toString();
			IOUtilities.closeInputStream(in);
			IOUtilities.closeConnection(bodyFile);
		}
		return returnString;
	}
}
