package cs456.emailclient.conn.management;

import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo; 
import net.rim.device.api.ui.component.Dialog;

public class WiFiStatus {

	
	//Check if the WiFi  is turned on
	public static boolean WiFiIsOn()
	{
		if ( ( RadioInfo.getActiveWAFs() & RadioInfo.WAF_WLAN ) != 0 ) 
			{return true; }
		else
		{return false;}
	}
	
	
	//This Turns on the WiFi
	public static boolean WiFiTurnOn()
	{
		return Radio.activateWAFs(RadioInfo.WAF_WLAN); 
	}
	
	//This Turns off the WiFi
	public static void WiFiTurnOff()
	{
		Radio.deactivateWAFs(RadioInfo.WAF_WLAN);
	}
	
	
	//This Handles connectivity
	public static boolean HandleConncetivityIssues()
	{
		
		if (!(WiFiIsOn())) 
		{
			int myChoice;
			myChoice=Dialog.ask(Dialog.D_YES_NO, "WiFi is turned Off, cannot login! Do you want to turn WiFi on?");
			if (myChoice == Dialog.YES)
			{
				boolean result = WiFiTurnOn();
				try
				{
					java.lang.Thread.sleep(10000);
				}
				catch(Exception e) {return false;}
				return result;
			}
			else
			{
				Dialog.alert ("Cannot start due to lack of network connectivity!");
			}
			return false;
		}
		return true;
	}
	
	
}
