package org.sigmah.client.util.profiler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
