package cs456.emailclient.popclient;

import cs456.emailclient.models.AddedObserver;

public abstract class PopActivityListener implements AddedObserver {

	/**
	 * DO NOT USE THIS ONE
	 */
	public void Added(Object addedObject) 
	{
		
	}
	
	public abstract void activitySuccess(boolean success);
}
