package org.sigmah.shared.dto.layout;

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

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.ui.Widget;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * LayoutGroupIterationDTO.
 */
public class LayoutGroupIterationDTO extends AbstractModelDataEntityDTO<Integer> {

  /**
   * DTO corresponding entity name.
   */
  public static final String ENTITY_NAME = "layout.LayoutGroupIteration";

  // DTO attributes keys.
  public static final String NAME = "name";
  public static final String CONTAINER_ID = "containerId";
  public static final String LAYOUT_GROUP = "layoutGroup";

  /**
   * {@inheritDoc}
   */
  @Override
  public String getEntityName() {
    return ENTITY_NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void appendToString(final ToStringBuilder builder) {
    builder.append(NAME, getName());
    builder.append(CONTAINER_ID, getContainerId());
    builder.append(LAYOUT_GROUP, getLayoutGroup());
  }

  public String getName() {
    return (String) get(NAME);
  }

  public void setName(String name) {
    set(NAME, name);
  }

  public Integer getContainerId() {
    return (Integer) get(CONTAINER_ID);
  }

  public void setContainerId(Integer containerId) {
    set(CONTAINER_ID, containerId);
  }

  public LayoutGroupDTO getLayoutGroup() {
    return (LayoutGroupDTO) get(LAYOUT_GROUP);
  }

  public void setLayoutGroup(LayoutGroupDTO layoutGroup) {
    set(LAYOUT_GROUP, layoutGroup);
  }

  public Widget getWidget() {
    final FieldSet fieldSet = new FieldSet();
    fieldSet.setHeadingHtml(getName());
    fieldSet.setCollapsible(true);

    final FormLayout formLayout = new FormLayout();
    formLayout.setLabelWidth(250);

    fieldSet.setLayout(formLayout);

    return fieldSet;
  }

}
