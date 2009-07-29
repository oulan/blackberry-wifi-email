package cs456.emailclient.conn.management;

import cs456.emailclient.models.Log;
import cs456.emailclient.popclient.PopActivityListener;
import cs456.emailclient.popclient.PopWorker;

public class ConnectionPredictor extends Thread 
{
	public static int PREDICT_TURN_ON_SIZE	= 4;
	public static int PREDICT_TURN_OFF_SIZE	= -6;
	public static int PREDICTOR_INTERVAL	= 30;
	public static int ADDS_UNTIL_PUSH		= 25;
	
	private PopWorker 			worker;
	private LSQueue				lsQueue;
	private volatile Point		lastLocation;
	private volatile boolean	running;
	private volatile int 		addedSinceLastPush;
	
	
	public ConnectionPredictor(PopWorker worker)
	{
		running = false;
		this.worker = worker;
		this.worker.addPopActivityListener(new CPPopActivityListener());
		lastLocation = null;
		addedSinceLastPush = 0;
		lsQueue = new LSQueue();
	}
	

	public void startConnectionPredictor()
	{
		running = true;
	}
	
	private class CPPopActivityListener extends PopActivityListener
	{
		public void activitySuccess(boolean success) 
		{
			if(lastLocation != null)
			{
				lsQueue.add(new LocationSuccessfulness(lastLocation, success));
				addedSinceLastPush++;
				if(addedSinceLastPush > ADDS_UNTIL_PUSH)
					lsQueue.writeList();
			}
		}
	}
	
	public void run()
	{
		while(true)
		{
			try
			{

				if(running)
				{
					Point tempLocation = GPSScanner.getGPSLocation();
					
					if(tempLocation != null)
					{
						lastLocation = tempLocation;
					}
					if(lastLocation != null)
					{
						if(!worker.isRunning())
						{
							if(WiFiStatus.WiFiIsOn() )
								worker.StartPopRetriever();
						}
					}
				}
				sleep(PREDICTOR_INTERVAL * 1000);
			}
			catch(InterruptedException e)
			{
				Log.error(e.getMessage());
			}
		}
	}
}
