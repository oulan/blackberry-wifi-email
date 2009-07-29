package cs456.emailclient.userinterface;

import cs456.emailclient.Main;
import cs456.emailclient.conn.management.ConnectionPredictor;
import cs456.emailclient.conn.management.WiFiStatus;
import cs456.emailclient.models.AddedObserver;
import cs456.emailclient.models.CachedMessage;
import cs456.emailclient.models.Log;
import cs456.emailclient.models.MessageBox;
import cs456.emailclient.popclient.PopWorker;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.component.PasswordEditField;


public class LoginScreen extends MainScreen
{
	
	public static String APPLICATION_NAME = "Email Application v1.0";
	
    public static final Border DEFAULT_BORDER = BorderFactory.createRoundedBorder(new XYEdges(5,5,5,5));
    
    PopWorker				popWorker;
    ConnectionPredictor		connectionPredictor;
    LogScreen				scrnLog;
	ButtonField				btnShowLog;
    
	MessageScreen			scrnMessage;
	
    VerticalFieldManager    vfmLogin;
	BasicEditField 			txtEmail;
	PasswordEditField 		txtPassword;
	BasicEditField 			txtPopServer;
	BasicEditField 			txtPopPort;
	CheckboxField			chkSecure;
	HorizontalFieldManager	hfmButtons;
	ButtonField				btnStart;
	ButtonField				btnStop;
	ButtonField				btnStartGmail;
	ButtonField				btnStartHotmail;
	
	public LoginScreen()
	{
        this.initializeScreen();
        this.addLog();
        this.scrnMessage = new MessageScreen();
        this.addLoginPanel();
        
        if(Main.MakeSureFoldersAreThere())
        {
        	if(Main.InitializeSettings())
        	{
        		//do nothing - everything is fine
        	}
        	else
        	{
            	UiApplication.getApplication().invokeLater(new Runnable(){
            		public void run(){
            			Dialog.alert("There is a problem with the settings file. The program cannot run.");
            		}
        		});
        	}
        }
        else
        {
        	UiApplication.getApplication().invokeLater(new Runnable(){
        		public void run(){
        			Dialog.alert("There is a problem with the file system. The program cannot run. (folders could not be created)");
        		}
    		});
        }
        
        MessageBox.AddAddedObserver(new AddedObserver(){
			public void Added(Object addedObject) {
				UiApplication.getUiApplication().invokeLater(new MessageAdder((CachedMessage)addedObject));
			}
        });
        MessageBox.loadHeaders();
        
        

		popWorker = new PopWorker();
		popWorker.start();
		popWorker.addAddedObserver(new PopWorkerObserver());
		connectionPredictor = new ConnectionPredictor(popWorker);
		connectionPredictor.start();
	}
	
	private class PopWorkerObserver implements AddedObserver
	{
		public void Added(Object addedObject) 
		{
			Integer status = (Integer) addedObject;
			if(status == PopWorker.STATUS_STARTED)
			{
				btnStop.setLabel("Stop");
				btnStop.setEditable(true);
				btnStart.setLabel("");
				btnStart.setEditable(false);
				btnStartGmail.setLabel("");
				btnStartGmail.setEditable(false);
				btnStartHotmail.setLabel("");
				btnStartHotmail.setEditable(false);
			}
			else
			{
				btnStop.setLabel("");
				btnStop.setEditable(false);
				btnStart.setLabel("Start Retriever");
				btnStart.setEditable(true);
				btnStartGmail.setLabel("Gmail");
				btnStartGmail.setEditable(true);
				btnStartHotmail.setLabel("Hotmail");
				btnStartHotmail.setEditable(true);
			}
				
		}
	}
	
	private class MessageAdder implements Runnable
	{
		CachedMessage message;
		public MessageAdder(CachedMessage message)
		{
			this.message = message;
		}
		public void run()
		{
			VerticalFieldManager	vfmNewMessageButton; 
			LabelField				lblNewMessageButton;
			ButtonField 			newMessageButton; 
			
			
			vfmNewMessageButton	= new VerticalFieldManager();
			vfmNewMessageButton	.setBorder(DEFAULT_BORDER);newMessageButton = new ButtonField(ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
			
			lblNewMessageButton = new LabelField(
					"Subject: " + message.subject	+ "\n" +
					"Date:    " + message.date	+ "\n" +
					"From:    " + message.from	+ "\n" +
					"To:      " + message.to
					);
			
			newMessageButton.setLabel("Read");
			
			newMessageButton.setChangeListener(new FieldChangeListener(){
				public void fieldChanged(Field field, int context) {
					LoginScreen.this.scrnMessage.setMessage(message);
					Main.GetApplication().pushScreen(scrnMessage);
				}
			});
			
			vfmNewMessageButton.add(lblNewMessageButton);
			vfmNewMessageButton.add(newMessageButton);
			LoginScreen.this.add(vfmNewMessageButton);
		}
	}
	
    private void initializeScreen()
    {
        this.setTitle(APPLICATION_NAME);
    }
    
    private void addLog()
    {
    	scrnLog 	= new LogScreen();
    	btnShowLog	= new ButtonField("Show Log", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
    	btnShowLog.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				Main.GetApplication().pushScreen(scrnLog);
			}
    	});
    	this.add(btnShowLog);
    }
    
    private void addLoginPanel()
    {
    	vfmLogin 		= new VerticalFieldManager();
    	txtEmail 		= new BasicEditField("Email\t\t:",	Main.GMAIL_ADDRESS);
    	txtPassword = new PasswordEditField("Password\t:", Main.GMAIL_PASSWORD);

    	
    	txtPopServer 	= new BasicEditField("Pop Server:", Main.GMAIL_POP_SERVER);
    	txtPopPort 		= new BasicEditField("Pop Port\t\t:", Main.GMAIL_SECURE_POP_PORT);
    	chkSecure		= new CheckboxField("Secure", true);
    	hfmButtons		= new HorizontalFieldManager();
    	btnStart		= new ButtonField("Start Retriever", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
    	btnStart		.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) 
			{
				connectionPredictor.startConnectionPredictor();
				if(WiFiStatus.HandleConncetivityIssues())
				{
					popWorker.setCredentials(
							txtEmail.getText(),
							txtPassword.getText(),
							txtPopServer.getText(),
							txtPopPort.getText(),
							chkSecure.getChecked());
					popWorker.StartPopRetriever();
				}
			}
    	});
    	btnStartGmail		= new ButtonField("Gmail", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
    	btnStartGmail		.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) 
			{
				connectionPredictor.startConnectionPredictor();
				if(WiFiStatus.HandleConncetivityIssues())
				{
					popWorker.setCredentials(
							Main.GMAIL_ADDRESS,
							Main.GMAIL_PASSWORD,
							Main.GMAIL_POP_SERVER,
							Main.GMAIL_SECURE_POP_PORT,
							Main.GMAIL_IS_SECURE_PORT);
					popWorker.StartPopRetriever();
				}
			}
    	});
    	btnStartHotmail		= new ButtonField("Hotmail", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
    	btnStartHotmail		.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) 
			{
				connectionPredictor.startConnectionPredictor();
				if(WiFiStatus.HandleConncetivityIssues())
				{
					popWorker.setCredentials(
							Main.HOTMAIL_ADDRESS,
							Main.HOTMAIL_PASSWORD,
							Main.HOTMAIL_POP_SERVER,
							Main.HOTMAIL_SECURE_POP_PORT,
							Main.HOTMAIL_IS_SECURE_PORT);
					popWorker.StartPopRetriever();
				}
			}
    	});
    	btnStop = new ButtonField("", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
    	btnStop.setEditable(false);
    	btnStop		.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) 
			{
				popWorker.stopWorker();
			}
    	});
    	vfmLogin		.setBorder(DEFAULT_BORDER);
    	vfmLogin   		.add(txtEmail);
    	vfmLogin   		.add(txtPassword);
    	vfmLogin   		.add(txtPopServer);
    	vfmLogin   		.add(txtPopPort);
    	vfmLogin   		.add(chkSecure);
    	hfmButtons 		.add(btnStart);
    	hfmButtons 		.add(btnStartGmail);
    	hfmButtons 		.add(btnStartHotmail);
    	hfmButtons 		.add(btnStop);
    	vfmLogin		.add(hfmButtons);
        this			.add(vfmLogin);     
    }	
}
