package cs456.emailclient.conn.management;

public class LocationSuccessfulness
{
	public Point location;
	public boolean success;
	public LocationSuccessfulness(Point p, boolean success)
	{
		this.location = p;
		this.success = success;
	}
}
