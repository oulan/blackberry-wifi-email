package cs456.emailclient.userinterface;

import cs456.emailclient.Main;
import cs456.emailclient.models.CachedMessage;
import cs456.emailclient.models.Log;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class MessageScreen extends FullScreen
{

	VerticalFieldManager	vfmLayout;
	
	VerticalFieldManager	vfmToolTip;
	LabelField				lblToolTip;
	
	VerticalFieldManager	vfmHeader;
	BasicEditField			txtHeader;	
	
	VerticalFieldManager	vfmBody;
	BasicEditField			txtBody;
	
	ButtonField				btnClose;
	
	public MessageScreen()
	{
		vfmLayout = new VerticalFieldManager(VERTICAL_SCROLL);
		this.add(vfmLayout);
		this.makeToolTip();
		this.makeHeader();
		this.makeBody();
		this.makeClose();
	}
	
	public void setMessage(CachedMessage message)
	{
		txtHeader.setText(
				message.subject + "\n" +
				message.date	+ "\n" +
				message.from	+ "\n" +
				message.to		+ "\n"
				);
		txtBody.setText(message.getBody());
	}
	
	/**
	 * Overriding keyChar so that the user can hit q to exit.
	 */
	public boolean keyChar(char key, int status, int time) 
	{
		if(key == 'q')
		{
			Main.GetApplication().popScreen(MessageScreen.this);
		}
		return true;
	}
	
	private void makeToolTip()
	{
		vfmToolTip	= new VerticalFieldManager();
		vfmToolTip	.setBorder(LoginScreen.DEFAULT_BORDER);
		
		lblToolTip = new LabelField("Press 'q' to quit.");
		
		vfmToolTip.add(lblToolTip);
		vfmLayout.add(vfmToolTip);
	}
	
	private void makeHeader()
	{
		vfmHeader	= new VerticalFieldManager();
		vfmHeader	.setBorder(LoginScreen.DEFAULT_BORDER);
		
		txtHeader = new BasicEditField();
		txtHeader.setEditable(false);
		
		vfmHeader.add(txtHeader);
		vfmLayout.add(vfmHeader);
	}
	
	private void makeBody()
	{
		vfmBody	= new VerticalFieldManager();
		vfmBody	.setBorder(LoginScreen.DEFAULT_BORDER);
		
		txtBody = new BasicEditField();
		txtBody.setEditable(false);
		
		vfmBody.add(txtBody);
		vfmLayout.add(vfmBody);
	}
	
	private void makeClose()
	{
		btnClose = new ButtonField("Close", ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
		btnClose.setChangeListener(new FieldChangeListener(){
			public void fieldChanged(Field field, int context) {
				Main.GetApplication().popScreen(MessageScreen.this);
			}
		});
		vfmLayout.add(btnClose);
	}	
}
