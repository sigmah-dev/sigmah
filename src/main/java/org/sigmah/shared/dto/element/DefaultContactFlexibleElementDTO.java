package org.sigmah.shared.dto.element;
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

import com.extjs.gxt.ui.client.widget.Component;

import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;

public class DefaultContactFlexibleElementDTO extends FlexibleElementDTO {
  private static final long serialVersionUID = -1251850749619288873L;

  private static final String ENTITY_NAME = "element.DefaultContactFlexibleElement";

  @Override
  protected Component getComponent(ValueResult valueResult, boolean enabled) {
    // TODO: Create the component
    return null;
  }

  @Override
  public boolean isCorrectRequiredValue(ValueResult result) {
    return false;
  }

  public DefaultContactFlexibleElementType getType() {
    return get("type");
  }

  public void setType(DefaultContactFlexibleElementType type) {
    set("type", type);
  }

  @Override
  public String getEntityName() {
    return ENTITY_NAME;
  }

  @Override
  public String getFormattedLabel() {
    return getLabel() != null ? getLabel() : DefaultContactFlexibleElementType.getName(getType());
  }
}
