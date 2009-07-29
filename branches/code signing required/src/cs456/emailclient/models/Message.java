package cs456.emailclient.models;

/**
 * Structure for representing the message
 */
public class Message 
{
	public String to		= null;
	public String from		= null;
	public String date		= null;
	public String subject	= null;
	private String body		= null;
	
	public boolean isMultPart;
	public String contentType;
	//public String contentEncoding;
	public String boundary;
	
	
	public void setBody(String newBody)
	{
		this.body = newBody;
	}
	
	public String getBody()
	{
		return this.body;
	}
}
