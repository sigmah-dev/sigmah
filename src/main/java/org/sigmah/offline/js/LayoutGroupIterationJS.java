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
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;

public final class LayoutGroupIterationJS extends JavaScriptObject {
  protected LayoutGroupIterationJS() {
  }

  public static LayoutGroupIterationJS toJavaScript(LayoutGroupIterationDTO layoutGroupIterationDTO) {
    LayoutGroupIterationJS layoutGroupIterationJS = Values.createJavaScriptObject(LayoutGroupIterationJS.class);
    layoutGroupIterationJS.setId(layoutGroupIterationDTO.getId());
    layoutGroupIterationJS.setContainerId(layoutGroupIterationDTO.getContainerId());
    layoutGroupIterationJS.setLayoutGroup(layoutGroupIterationDTO.getLayoutGroup());
    layoutGroupIterationJS.setName(layoutGroupIterationDTO.getName());

    return layoutGroupIterationJS;
  }

  public LayoutGroupIterationDTO toDTO() {
    LayoutGroupIterationDTO layoutGroupIterationDTO = new LayoutGroupIterationDTO();
    layoutGroupIterationDTO.setId(getId());
    layoutGroupIterationDTO.setContainerId(getContainerId());
    layoutGroupIterationDTO.setLayoutGroup(getLayoutGroupDTO());
    layoutGroupIterationDTO.setName(getName());

    return layoutGroupIterationDTO;
  }

  public native int getId() /*-{
		return this.id;
	}-*/;

  public native void setId(int id) /*-{
		this.id = id;
	}-*/;

  public native int getContainerId() /*-{
    return this.containerId;
  }-*/;

  public native void setContainerId(int containerId) /*-{
    this.containerId = containerId;
  }-*/;

  public LayoutGroupDTO getLayoutGroupDTO() {
    return getLayoutGroup().toDTO();
  }

  public native LayoutGroupJS getLayoutGroup() /*-{
    return this.layoutGroup;
  }-*/;

  public void setLayoutGroup(LayoutGroupDTO layoutGroup) {
    setLayoutGroup(LayoutGroupJS.toJavaScript(layoutGroup));
  }

  public native void setLayoutGroup(LayoutGroupJS layoutGroup) /*-{
    this.layoutGroup = layoutGroup;
  }-*/;

  public native String getName() /*-{
    return this.name;
  }-*/;

  public native void setName(String name) /*-{
    this.name = name;
  }-*/;
}
