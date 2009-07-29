package cs456.emailclient.userinterface;

import cs456.emailclient.Main;
import cs456.emailclient.models.AddedObserver;
import cs456.emailclient.models.Log;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class LogScreen extends FullScreen implements AddedObserver
{
	
	ButtonField				btnClose;
	ButtonField				btnClear;
	VerticalFieldManager	vfmLayout;
    VerticalFieldManager    vfmLog;	
    BasicEditField 			txtLog;
	
	public LogScreen()
	{
		Log.AddAddedObserver(this);
		vfmLayout = new VerticalFieldManager(VERTICAL_SCROLL);
		this.add(vfmLayout);
		this.addLog();
		this.addClose();
		this.addClear();
	}
	
	/**
	 * Overriding keyChar so that the user can hit q to exit.
	 */
	public boolean keyChar(char key, int status, int time) 
	{
		if(key == 'q')
		{
			Main.GetApplication().popScreen(LogScreen.this);
		}
		return true;
	}
	
	private String addedString;
	/**
	 * Called when something is added to the log.
	 */
	public void Added(Object addedObject)
	{
		synchronized(this)
		{
			addedString = (String)addedObject;
			
			UiApplication.getUiApplication().invokeAndWait(new Runnable(){
				public void run() {
					txtLog.setText(txtLog.getText() + "\n" + LogScreen.this.addedString);
				}
			});
		}
	}
	
	private void addClose()
	{
		btnClose = new ButtonField("Close", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
		btnClose.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				Main.GetApplication().popScreen(LogScreen.this);
			}
		});
		vfmLayout.add(btnClose);
	}

	private void addClear()
	{
		btnClear = new ButtonField("Clear", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
		btnClear.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				LogScreen.this.txtLog.setText("");
			}
		});
		vfmLayout.add(btnClear);
	}	
	
	private void addLog()
	{
    	vfmLog	= new VerticalFieldManager();
    	vfmLog	.setBorder(LoginScreen.DEFAULT_BORDER);
    	txtLog	= new BasicEditField();
    	txtLog	.setEditable(false);
    	vfmLog	.add(txtLog);
    	vfmLayout	.add(vfmLog);
	}
}
