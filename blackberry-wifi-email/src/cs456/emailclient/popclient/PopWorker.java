package cs456.emailclient.popclient;

import java.util.Vector;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import cs456.emailclient.models.AddedObserver;
import cs456.emailclient.models.Log;

public class PopWorker extends Thread 
{
	public static final int DEFAULT_INTERVAL	= 15;
	public static final int MAX_TRIES			= 3;
	
	public static final Integer STATUS_STOPPED = new Integer(0);
	public static final Integer STATUS_STARTED = new Integer(1);
	
	private volatile boolean running = false;
	private int failedTries;
	
	/**
	 * the interval in seconds
	 */
	private int interval;
	private int secondsPast;
	
	public PopWorker()
	{
		running = false;
		interval = DEFAULT_INTERVAL;
		secondsPast = 0;
		failedTries = 0;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	public void setCredentials(
			String	email,
			String	password,
			String	popServer,
			String	popPort,
			boolean secure
			)
	{
		PopClient.email		= email;
		PopClient.password	= password;
		PopClient.popServer	= popServer;
		PopClient.popPort	= popPort;
	}
	
	public void StartPopRetriever()
	{
		secondsPast = interval;
		failedTries = 0;
		startWorker();
	}
	
	private void startWorker()
	{
		if(!this.isRunning())
		{
			running = true;
			UiApplication.getUiApplication().invokeLater(new Runnable(){
				public void run(){
					updateAddedObservers(STATUS_STARTED);
				}
			});
		}
	}
	
	public void stopWorker()
	{
		PopClient.closeEverything();
		running = false;
		secondsPast = 0;
		UiApplication.getUiApplication().invokeLater(new Runnable(){
			public void run(){
				updateAddedObservers(STATUS_STOPPED);
			}
		});
	}
	
	public void setInterval(int seconds)
	{
		interval = seconds;
	}
	
	private class PopErrorAlerter implements Runnable
	{
		PopException exception;
		public PopErrorAlerter(PopException e)
		{
			this.exception = e;
		}
		
		public void run()
		{
			Dialog.alert("PopWorker stopped!\n" + exception.getPopErrorMessage());
		}
	}
	
	
	
	public void run()
	{
		while(true)
		{
			if(running)
			{
				if(secondsPast >= interval)
				{
					try
					{
						PopClient.startPop();
						PopClient.login();
						PopClient.list();
								
						for(int x = 0; x < PopClient.messageOrdinals.size(); x++)
						{
							PopClient.getMessage(
								(String)PopClient.messageOrdinals.elementAt(x),
								(String)PopClient.messageSizes.elementAt(x));
						}
								
						PopClient.endPop();
						
						failedTries = 0;
						updatePopActivityListeners(true);
					}
					catch(PopException e)
					{
						updatePopActivityListeners(false);
						Log.error(e.getPopErrorMessage());
						if( failedTries + 1 < MAX_TRIES )
						{
							failedTries++;
						}
						else
						{
							UiApplication.getUiApplication().invokeLater(new PopErrorAlerter(e));
							stopWorker();
						}
					}
					secondsPast = 0;
				}
				secondsPast++;
			}
			try
			{
				sleep(1000);
			}
			catch(InterruptedException e)
			{
				Log.error(e.toString());
			}
		}
	}
	
	public Vector popActivityListeners = new Vector();
	private void updatePopActivityListeners(boolean success)
	{
		for(int x = 0; x < popActivityListeners.size(); x++)
		{
			PopActivityListener listener = (PopActivityListener)popActivityListeners.elementAt(x);
			listener.activitySuccess(success);
		}	
	}
	public void addPopActivityListener(PopActivityListener listener)
	{
		popActivityListeners.addElement(listener);
	}
	
	public Vector addedObservers = new Vector();
	private void updateAddedObservers(Integer status)
	{
		for(int x = 0; x < addedObservers.size(); x++)
		{
			AddedObserver observer = (AddedObserver)addedObservers.elementAt(x);
			observer.Added(status);
		}	
	}
	public void addAddedObserver(AddedObserver observer)
	{
		addedObservers.addElement(observer);
	}	
	
}
