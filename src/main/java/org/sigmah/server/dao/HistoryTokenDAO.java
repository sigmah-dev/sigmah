package org.sigmah.server.dao;
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

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.HistoryToken;

public interface HistoryTokenDAO extends DAO<HistoryToken, Integer> {
  List<HistoryToken> findByContainerIdAndFlexibleElementId(Integer containerId, List<Integer> flexibleElementIds, boolean lastOnly);
  /**
   * Find history tokens with a value equaled to or containing the given id.
   * <p>
   *   The value inside the history token can be a multivalued value.
   * </p>
   */
  List<HistoryToken> findByIdInSerializedValueAndElementType(Integer containerId, String elementTypeTableName, boolean lastOnly);
}
