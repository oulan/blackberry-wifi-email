package cs456.emailclient.models;

public interface AddedObserver 
{
	/***
	 * Tell the observer that an object was added.
	 * @param addedObject The object that was added to the thing being observerd.
	 */
	public void Added(Object addedObject);
}
