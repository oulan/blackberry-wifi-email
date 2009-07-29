package cs456.emailclient.conn.management;

import javax.microedition.location.*;

import cs456.emailclient.models.Log;


public class GPSScanner 
{
	public static final int GPS_TIMEOUT=30;
	
	public static Point getGPSLocation()
	{
		Point point = null;
		LocationProvider l; 
		Location loc;
		
		try
		{
			l = LocationProvider.getInstance(null);
			
			try
			{
			    loc = l.getLocation(GPS_TIMEOUT);
			    
			    QualifiedCoordinates cords = loc.getQualifiedCoordinates();
			    
			    Float latitude = new Float(cords.getLatitude());
			    Float longitude = new Float(cords.getLongitude());
			    
			    point = new Point(latitude.floatValue(), longitude.floatValue());
			}
			catch(LocationException e)          {Log.error(e.toString());}
			catch(InterruptedException e)       {Log.error(e.toString());}
			catch(SecurityException e)          {Log.error(e.toString());}
			catch(IllegalArgumentException e)   {Log.error(e.toString());}
		}
		catch(LocationException e)              {Log.error(e.toString());}
		
		return point;      
	}

}
