package org.sigmah.shared.dto.referential;

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

/**
 * Regroups <code>ElementTypeEnum</code> and <code>TextAreaType</code>.
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public interface LogicalElementType {

  /**
   * Returns the <code>ElementTypeEnum</code> value matching this type.
   *
   * @return the <code>ElementTypeEnum</code> value.
   */
  ElementTypeEnum toElementTypeEnum();

  /**
   * Returns the <code>TextAreaType</code> value matching this type or
   * <code>null</code> if it is unknown.
   *
   * @return the <code>TextAreaType</code> value or <code>null</code>.
   */
  TextAreaType toTextAreaType();

  /**
   * Returns the <code>DefaultFlexibleElementType</code> value matching this type or
   * <code>null</code> if it is unknown.
   *
   * @return the <code>DefaultFlexibleElementType</code> value or <code>null</code>.
   */
  DefaultFlexibleElementType toDefaultFlexibleElementType();

  /**
   * Returns the <code>DefaultContactFlexibleElementType</code> value matching this type or
   * <code>null</code> if it is unknown.
   *
   * @return the <code>DefaultContactFlexibleElementType</code> value or <code>null</code>.
   */
  DefaultContactFlexibleElementType toDefaultContactFlexibleElementType();

  /**
   * Returns a <code>String</code> representation of this type.
   *
   * @return a <code>String</code> representation.
   */
  String getDescription();

  /**
   * Returns the technical name of this type.
   *
   * @return the technical name of this type
   */
  String name();
}
