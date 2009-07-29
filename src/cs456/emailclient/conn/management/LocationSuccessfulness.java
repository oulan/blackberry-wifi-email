package cs456.emailclient.conn.management;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


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
