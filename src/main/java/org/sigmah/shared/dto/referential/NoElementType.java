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
 * Nil implmentation of <code>LogicalElementType</code>.
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class NoElementType implements LogicalElementType {

  /**
   * Shared instance.
   */
  public static final NoElementType INSTANCE = new NoElementType();

  /**
   * Private constructor.
   */
  private NoElementType() {
    // No initialization.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ElementTypeEnum toElementTypeEnum() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TextAreaType toTextAreaType() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DefaultFlexibleElementType toDefaultFlexibleElementType() {
    return null;
  }

  @Override
  public DefaultContactFlexibleElementType toDefaultContactFlexibleElementType() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String name() {
    return null;
  }

}
