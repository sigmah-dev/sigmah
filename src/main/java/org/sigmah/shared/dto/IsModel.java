package org.sigmah.shared.dto;

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

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.extjs.gxt.ui.client.data.ModelData;
import java.util.Date;

/**
 * Interface implemented by model entities.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public interface IsModel extends EntityDTO<Integer>, ModelData {

	/**
	 * Model type.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
	 */
	public static enum ModelType {

		/**
		 * Project model type.
		 */
		ProjectModel,

		/**
		 * Organization unit model type.
		 */
		OrgUnitModel,

		/**
		 * Contact model type
		 */
		ContactModel;

	}

	/**
	 * Returns the current model name.
	 * 
	 * @return The current model name.
	 */
	String getName();

	/**
	 * Returns the current model corresponding {@link ModelType}.
	 * 
	 * @return The current model corresponding {@link ModelType}.
	 */
	ModelType getModelType();

	/**
	 * Returns the current model status.
	 * 
	 * @return The current model status.
	 */
	ProjectModelStatus getStatus();

	/**
	 * Returns the current model corresponding <b>all</b> flexible elements.
	 * 
	 * @return The current model corresponding <b>all</b> flexible elements.
	 */
	List<FlexibleElementDTO> getAllElements();

	/**
	 * Returns the model elements that handle a layout.<br>
	 * {@code null} elements will be ignored.
	 * 
	 * @return The model elements that handle a layout (may contain {@code null} values).
	 */
	List<AbstractModelDataEntityDTO<?>> getHasLayoutElements();

	/**
	 * Returns <code>true</code> if the model is currently under maintenance.
	 * 
	 * @return <code>true</code> if this model is under maintenance, <code>false</code> otherwise.
	 */
	boolean isUnderMaintenance();
	
	/**
	 * Returns the start date of the current maintenance.
	 * 
	 * @return The start date of the maintenance.
	 */
	Date getDateMaintenance();
	
	/**
	 * Returns <code>true</code> if the model is in an editable state (<code>DRAFT</code> or <code>UNDER_MAINTENANCE</code> status).
	 * 
	 * @return {@code true} if the current project model is editable, {@code false} otherwise.
	 */
	boolean isEditable();
}
