package org.sigmah.server.domain.element;
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;

@Entity
@Table(name = EntityConstants.DEFAULT_CONTACT_FLEXIBLE_ELEMENT_TABLE)
public class DefaultContactFlexibleElement extends FlexibleElement {
  private static final long serialVersionUID = 2545492387257612242L;

  @Column(name = EntityConstants.DEFAULT_CONTACT_FLEXIBLE_ELEMENT_COLUMN_TYPE)
  @Enumerated(EnumType.STRING)
  private DefaultContactFlexibleElementType type;

  public DefaultContactFlexibleElementType getType() {
    return type;
  }

  public void setType(DefaultContactFlexibleElementType type) {
    this.type = type;
  }
}
