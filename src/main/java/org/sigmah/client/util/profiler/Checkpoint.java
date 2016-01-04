package org.sigmah.client.util.profiler;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.offline.js.Values;

/**
 * Checkpoint of an execution.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Checkpoint extends JavaScriptObject {
	
	/**
	 * Creates a new checkpoint with the given values.
	 * 
	 * @param name Name of the checkpoint.
	 * @param time Time of the checkpoint.
	 * @return A new checkpoint.
	 */
	public static Checkpoint createCheckpoint(String name, double time) {
		final Checkpoint checkpoint = Values.createJavaScriptObject(Checkpoint.class);
		checkpoint.setName(name);
		checkpoint.setTime(time);
		return checkpoint;
	}
	
	protected Checkpoint() {
		// Protected constructor (required for JavaScript objects).
	}
	
	/**
	 * Retrieves the name of the checkpoint.
	 * 
	 * @return Name of the checkpoint.
	 */
	public native String getName() /*-{
		return this.name;
	}-*/;

	/**
	 * Defines the name of the checkpoint.
	 * 
	 * @param name Name of the checkpoint.
	 */
	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	/**
	 * Retrieves the time when this checkpoint was reached.
	 * 
	 * @return Time of the checkpoint.
	 */
	public native double getTime() /*-{
		return this.time;
	}-*/;
	
	/**
	 * Defines the time when this checkpoint was reached.
	 * 
	 * @param time Time of the checkpoint.
	 */
	public native void setTime(double time) /*-{
		this.time = time;
	}-*/;

	/**
	 * Retrieves the time spent for this checkpoint.
	 * 
	 * @return Duration of the checkpoint.
	 */
	public native double getDuration() /*-{
		return this.duration;
	}-*/;
	
	/**
	 * Defines the time spent for this checkpoint.
	 * 
	 * @param duration Duration of the checkpoint.
	 */
	public native void setDuration(double duration) /*-{
		this.duration = duration;
	}-*/;
	
}
