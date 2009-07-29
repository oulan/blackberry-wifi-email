package cs456.emailclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import cs456.emailclient.models.Log;
import cs456.emailclient.models.MessageBox;
import cs456.emailclient.util.IOUtilities;
import cs456.emailclient.userinterface.LoginScreen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;


public class Main extends UiApplication
{
	public static String PROGRAM_FOLDER			= "file:///SDCard/emailclient/";
	public static String SETTINGS_FOLDER		= PROGRAM_FOLDER + "settings/";
	public static String HEADER_FOLDER			= PROGRAM_FOLDER + "headers/";
	public static String BODY_FOLDER			= PROGRAM_FOLDER + "bodies/";
	public static String SETTINGS_FILE			= SETTINGS_FOLDER + "settings.txt";
	
	
	public static String GMAIL_ADDRESS			= "<testing_gmail_email>@gmail.com";
	public static String GMAIL_PASSWORD			= "<testing_gmail_pass>";
	public static String GMAIL_POP_SERVER		= "pop.gmail.com";
	public static String GMAIL_SECURE_POP_PORT	= "995";
	public static boolean GMAIL_IS_SECURE_PORT	= true;
	
	public static String HOTMAIL_ADDRESS			= "<testing_hotmail_email>";
	public static String HOTMAIL_PASSWORD			= "testing_hotmail_pass";
	public static String HOTMAIL_POP_SERVER			= "pop3.live.com";
	public static String HOTMAIL_SECURE_POP_PORT	= "995";
	public static boolean HOTMAIL_IS_SECURE_PORT	= true;	
	
	private static UiApplication instance = null;
	
	Main() 
    {    
        MainScreen screen = new LoginScreen();
        this.pushScreen(screen);
    }
    
	public static UiApplication GetApplication()
	{
		return instance;
	}
	
    public static void main(String args[])
    {
    	
    	instance = new Main();
    	instance.enterEventDispatcher();
    }
    
    public static boolean PushSettingsToFileSystem()
    {
    	boolean success = false;
    	FileConnection settingsFile = null;
    	DataOutputStream out = null;
    	
    	try
    	{
        	settingsFile = (FileConnection)Connector.open(SETTINGS_FILE);
        	out = settingsFile.openDataOutputStream();
        	
        	//messageCount
        	out.writeLong(MessageBox.messageIdCounter);
        	
        	out.close();
        	settingsFile.close();
        	success = true;
    	}
        catch(Exception e)
        {
	    	Log.info(e.toString());
			IOUtilities.closeConnection(settingsFile);
			IOUtilities.closeOutputStream(out);
        }
    	
    	
    	return success;
    }
    
    public static boolean ExtractSettingsFromFileSystem()
    {
    	boolean success = false;
    	FileConnection settingsFile = null;
        DataInputStream in = null;
        
        try
        {
        	settingsFile = (FileConnection)Connector.open(SETTINGS_FILE);
            in = settingsFile.openDataInputStream();
            
            //messageIdCount
            MessageBox.messageIdCounter = in.readLong();
            
            in.close();
            settingsFile.close();
            success = true;
        }
        catch(Exception e)
        {
	    	Log.info(e.toString());
			IOUtilities.closeConnection(settingsFile);
			IOUtilities.closeInputStream(in);
        }
        
        return success;
    }
    
    public static boolean InitializeSettings()
    {
    	boolean success = false;
    	
    	IOUtilities.FileExistsResult fqr = IOUtilities.makeSureFileIsThere(SETTINGS_FILE);
    	
    	if(!fqr.success)
    		return false;
    	
    	if(fqr.existed)
    	{
    		//the settings file existed so extract it
    		success = ExtractSettingsFromFileSystem();
    	}
    	else
    	{
    		//the settings file did not exist so we need to populate it
    		success = PushSettingsToFileSystem();
    	}
    	
    	return success;
    }
    
    public static boolean MakeSureFoldersAreThere()
    {
    	boolean success = false;
    	if(IOUtilities.makeSureFolderIsThere(PROGRAM_FOLDER))
    		if(IOUtilities.makeSureFolderIsThere(SETTINGS_FOLDER))
    			if(IOUtilities.makeSureFolderIsThere(HEADER_FOLDER))
    				if(IOUtilities.makeSureFolderIsThere(BODY_FOLDER))
    					success = true;
    	return success;
    					
    }
}
