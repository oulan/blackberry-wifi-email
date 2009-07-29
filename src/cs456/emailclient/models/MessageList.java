package cs456.emailclient.models;

import java.util.Vector;

public class MessageList 
{
	private Vector messages;
	
	public MessageList()
	{
		messages = new Vector();
	}
	
	public void addMessage(CachedMessage message)
	{
		this.messages.addElement(message);
	}
	
	public CachedMessage getMessage(int index)
	{
		return (CachedMessage)this.messages.elementAt(index);
	}
}
