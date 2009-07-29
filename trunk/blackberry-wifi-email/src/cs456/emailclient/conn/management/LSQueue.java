package cs456.emailclient.conn.management;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import cs456.emailclient.Main;
import cs456.emailclient.models.Log;
import cs456.emailclient.util.IOUtilities;
import net.rim.device.api.util.MathUtilities;

/**
 * Link list implementation queue
 * @author Joseph
 */
public class LSQueue 
{
	public static final String	OBSERVATIONS_FILE	= "observations.txt";
	public static final int		MAX_SIZE			= 50;
	public static final double	MAX_DISTANCE		= 0.1;		//in kilometers
	public static final double	EARTH_RADIUS		= 6371.0;	//in kilometers
	private LSLink	head;
	private LSLink	tail;
	private int		size;
	
	public LSQueue()
	{
		size = 0;
		head = null;
		tail = null;
		readList();
	}
	
	/**
	 * The formula is beyond the scope of the course so we referenced 
	 * a website for calculating the distance between two longtitude, 
	 * latitude points.
	 * 
	 * Movable Type Ltd
	 * Website:		http://www.movable-type.co.uk/scripts/latlong.html
	 * Accessed:	Tuesday July 28th 2009
	 * Updated:		April 2007
	 */
	private static double calculateDist(Point p1, Point p2)
	{
		double p1lat = Math.toRadians((double)p1.Latitude);
		double p2lat = Math.toRadians((double)p2.Latitude);
		double p1long = Math.toRadians((double)p1.Longtitude);
		double p2long = Math.toRadians((double)p2.Longtitude);
	 	double latitudeDifference	= p2lat-p1lat;
	 	double longtitudeDifference = p2long-p1long;
		
		double a = Math.sin(latitudeDifference/2) * Math.sin(latitudeDifference/2) +
		        Math.cos(p1lat) * Math.cos(p2lat) * 
		        Math.sin(longtitudeDifference/2) * Math.sin(longtitudeDifference/2); 
		double c = 2 * MathUtilities.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = EARTH_RADIUS * c;
		return Math.abs(d);
	}
	
	
	public int sumNearestPoints(Point lastScan)
	{
		int accumulator = 0;
		
		LSLink current = head;
		while(current != null)
		{
			if( calculateDist(lastScan, current.locationSuccessfulness.location) < MAX_DISTANCE )
			{
				if(current.locationSuccessfulness.success)
				{
					accumulator++;
				}
				else
				{
					accumulator--;
				}
			}
			current = current.getNext();
		}
		
		return accumulator;
	}
	
	public void writeList()
	{
		FileConnection file = null;
		DataOutputStream out = null;
		
		try
		{
			file = (FileConnection)Connector.open(Main.SETTINGS_FOLDER + OBSERVATIONS_FILE);
			
			if(!file.exists())
				file.create();
			
			out = file.openDataOutputStream();
			
			LSLink current = head;
			while(current != null)
			{
				out.writeFloat(current.locationSuccessfulness.location.Latitude);
				out.writeFloat(current.locationSuccessfulness.location.Longtitude);
				out.writeBoolean(current.locationSuccessfulness.success);
				
				current = current.next;
			}
			
			out.writeFloat(Float.NaN);
			
			file.close();
		}
		catch (IOException e) 
		{
			Log.error(e.toString());
			IOUtilities.closeConnection(file);
			IOUtilities.closeOutputStream(out);
		}
		
	}
	
	private void readList()
	{
		FileConnection file = null;
		DataInputStream in = null;
		try 
		{
			file = (FileConnection)Connector.open(Main.SETTINGS_FOLDER + OBSERVATIONS_FILE);
			if(file.exists())
			{
				in = file.openDataInputStream();
				
				float latitude;
				float longtitude;
				boolean success;
				
				latitude = in.readFloat();
				while(latitude != Float.NaN)
				{
					longtitude = in.readFloat();
					success = in.readBoolean();
					
					this.add(new LocationSuccessfulness(new Point(latitude, longtitude), success));
					
					latitude = in.readFloat();
				}
				
				in.close();
			}
			file.close();
		}
		catch (IOException e)
		{
			Log.error(e.getMessage());
			IOUtilities.closeConnection(file);
			IOUtilities.closeInputStream(in);
		}
	}
	
	public void add(LocationSuccessfulness l)
	{
		synchronized(this)
		{
			if(tail == null)
			{
				size++;
				//the list is empty
				tail = new LSLink(null, l);
				head = tail;
			}
			else
			{
				size++;
				//the list is not empty
				tail.setNext(new LSLink(null, l));
				tail = tail.getNext();
			}
			
			if(size > MAX_SIZE)
				this.remove();
		}
	}
	
	public LocationSuccessfulness remove()
	{
		synchronized(this)
		{
			if(this.head == null)
			{
				//there's nothing in the list so do nothing
				return null;
			}
			else if(this.head == this.tail)
			{
				size--;
				//there is only one item left in the list
				//so we should set the head and tail to null
				LSLink temp = head;
				this.head = null;
				this.tail = null;
				return temp.getLocationSuccessfulness();
			}
			else
			{
				size--;
				//we have more than one item in the list
				//so we need to restructure the link list
				LSLink temp = head;
				head = head.getNext();
				return temp.getLocationSuccessfulness();
			}
		}
	}
	
	public boolean isEmpty()
	{
		synchronized(this)
		{
			return head == null;
		}
	}
	
	private static class LSLink
	{
		LocationSuccessfulness locationSuccessfulness;
		LSLink next;
		//next == NULL means ends of list
		
		LSLink(LSLink next, LocationSuccessfulness l)
		{
			this.next = next;
			this.locationSuccessfulness = l;
		}
		
		public LocationSuccessfulness getLocationSuccessfulness()
		{
			return locationSuccessfulness;
		}
		
		public LSLink getNext()
		{
			return next;
		}

		public void setNext(LSLink next)
		{
			this.next = next;
		}	
	}
}


