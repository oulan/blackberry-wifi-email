package cs456.emailclient.conn.management;

import net.rim.device.api.system.RadioInfo; 

public class WiFiStatus {

	
	//Check if the WiFi  is turned on
	public static boolean WiFiIsOn()
	{
		if ( ( RadioInfo.getActiveWAFs() & RadioInfo.WAF_WLAN ) != 0 ) 
			{return true; }
		else
		{return false;}
	}
	
}
