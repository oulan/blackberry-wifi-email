package cs456.emailclient.popclient;

public abstract class PopException extends Exception 
{
	String additionalInfo;
	public PopException(String additionalInfo)
	{
		this.additionalInfo = additionalInfo;
	}
	
	public String getPopErrorMessage()
	{
		if(additionalInfo != null && !additionalInfo.equals(""))
		{
			return additionalInfo + "\n";
		}
		else
		{
			return "";
		}
	}
	
	public static class BadCredentialException extends PopException
	{
		public BadCredentialException(String additionalInfo) {super(additionalInfo);}
		public String getPopErrorMessage() {
			return super.getPopErrorMessage() + "The username or password is invalid";
		}
	}
	
	public static class PopCommunicationException extends PopException
	{
		public PopCommunicationException(String additionalInfo) {super(additionalInfo);}
		public String getPopErrorMessage() {
			return super.getPopErrorMessage() + "We could not contact the pop server.";
		}
	}
}