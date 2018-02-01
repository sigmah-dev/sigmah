package org.sigmah.offline.js;
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
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;

public final class IterationChangeJS extends JavaScriptObject {
  protected IterationChangeJS() {
  }

  public static IterationChangeJS toJavaScript(IterationChange iterationChange) {
    IterationChangeJS iterationChangeJS = Values.createJavaScriptObject(IterationChangeJS.class);
    iterationChangeJS.setIterationId(iterationChange.getIterationId());
    iterationChangeJS.setNewIterationId(iterationChange.getNewIterationId());
    iterationChangeJS.setName(iterationChange.getName());
    iterationChangeJS.setLayoutGroupId(iterationChange.getLayoutGroupId());
    iterationChangeJS.setDeleted(iterationChange.isDeleted());

    return iterationChangeJS;
  }

  public IterationChange toIterationChange() {
    IterationChange iterationChange = new IterationChange();
    iterationChange.setIterationId(getIterationId());
    iterationChange.setNewIterationId(getNewIterationId());
    iterationChange.setName(getName());
    iterationChange.setLayoutGroupId(getLayoutGroupId());
    iterationChange.setDeleted(isDeleted());

    return iterationChange;
  }

  public native int getIterationId() /*-{
		return this.iterationId;
	}-*/;

  public native void setIterationId(int iterationId) /*-{
		this.iterationId = iterationId;
	}-*/;

  public native int getNewIterationId() /*-{
    return this.newIterationId;
  }-*/;

  public native void setNewIterationId(int newIterationId) /*-{
    this.newIterationId = newIterationId;
  }-*/;

  public native int getLayoutGroupId() /*-{
    return this.layoutGroupId;
  }-*/;

  public native void setLayoutGroupId(int layoutGroupId) /*-{
    this.layoutGroupId = layoutGroupId;
  }-*/;

  public native String getName() /*-{
    return this.name;
  }-*/;

  public native void setName(String name) /*-{
    this.name = name;
  }-*/;

  public native boolean isDeleted() /*-{
    return !!this.deleted;
  }-*/;

  public native void setDeleted(boolean deleted) /*-{
    this.deleted = deleted;
  }-*/;
}
