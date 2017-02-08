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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import org.sigmah.shared.command.UpdateLayoutGroupIterations;
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;

public final class UpdateLayoutGroupIterationsJS extends CommandJS {

  protected UpdateLayoutGroupIterationsJS() {
  }

  public static UpdateLayoutGroupIterationsJS toJavaScript(UpdateLayoutGroupIterations updateLayoutGroupIterations) {
    final UpdateLayoutGroupIterationsJS updateLayoutGroupIterationsJS = Values.createJavaScriptObject(UpdateLayoutGroupIterationsJS.class);

    updateLayoutGroupIterationsJS.setIterationChanges(updateLayoutGroupIterations.getIterationChanges());
    updateLayoutGroupIterationsJS.setContainerId(updateLayoutGroupIterations.getContainerId());

    return updateLayoutGroupIterationsJS;
  }

  public UpdateLayoutGroupIterations toUpdateLayoutGroupIterations() {
    final UpdateLayoutGroupIterations updateLayoutGroupIterations = new UpdateLayoutGroupIterations();

    updateLayoutGroupIterations.setIterationChanges(getIterationChanges());
    updateLayoutGroupIterations.setContainerId(getContainerId());

    return updateLayoutGroupIterations;
  }

  public native int getContainerId() /*-{
		return this.containerId;
	}-*/;

  public native void setContainerId(int containerId) /*-{
		this.containerId = containerId;
	}-*/;

  public List<IterationChange> getIterationChanges() {
    final JsArray<IterationChangeJS> iterationChanges = getIterationChangesJS();

    if (iterationChanges == null) {
      return null;
    }

    final ArrayList<IterationChange> list = new ArrayList<IterationChange>();
    for (int index = 0; index < iterationChanges.length(); index++) {
      list.add(iterationChanges.get(index).toIterationChange());
    }

    return list;
  }

  public native JsArray<IterationChangeJS> getIterationChangesJS() /*-{
		return this.iterationChanges;
	}-*/;

  public void setIterationChanges(List<IterationChange> iterationChanges) {
    if (iterationChanges == null) {
      return;
    }

    final JsArray<IterationChangeJS> array = (JsArray<IterationChangeJS>) JavaScriptObject.createArray();

    for (final IterationChange iterationChange : iterationChanges) {
      array.push(IterationChangeJS.toJavaScript(iterationChange));
    }

    setIterationChangesJS(array);
  }

  public native void setIterationChangesJS(JsArray<IterationChangeJS> iterationChangesJS) /*-{
    this.iterationChanges = iterationChangesJS;
  }-*/;
}
