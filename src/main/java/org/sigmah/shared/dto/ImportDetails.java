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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.referential.ImportStatusCode;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.extjs.gxt.ui.client.data.BaseModel;
import org.sigmah.shared.dto.base.DTO;

/**
 * Entity representing the details found for an id key of an {@link ImportationSchemeModel}
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ImportDetails extends BaseModel implements Serializable, DTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3285824305875549594L;

	private String modelName;
	private ProjectModelStatus modelStatus;
	private String keyIdentification;
	private ImportStatusCode entityStatus;
	private Map<EntityDTO<?>, List<ElementExtractedValue>> entitiesToImport = new HashMap<EntityDTO<?>, List<ElementExtractedValue>>();

	public ImportDetails() {
		// Serialization.
	}

	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * @param modelName
	 *          the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * @return the keyIdentification
	 */
	public String getKeyIdentification() {
		return keyIdentification;
	}

	/**
	 * @param keyIdentification
	 *          the keyIdentification to set
	 */
	public void setKeyIdentification(String keyIdentification) {
		this.keyIdentification = keyIdentification;
	}

	/**
	 * @return the entityStatus
	 */
	public ImportStatusCode getEntityStatus() {
		return entityStatus;
	}

	/**
	 * @param entityStatus
	 *          the entityStatus to set
	 */
	public void setEntityStatus(ImportStatusCode entityStatus) {
		this.entityStatus = entityStatus;
	}

	/**
	 * @return the entitiesToImport
	 */
	public Map<EntityDTO<?>, List<ElementExtractedValue>> getEntitiesToImport() {
		return entitiesToImport;
	}

	/**
	 * @param entitiesToImport
	 *          the entitiesToImport to set
	 */
	public void setEntitiesToImport(Map<EntityDTO<?>, List<ElementExtractedValue>> entitiesToImport) {
		this.entitiesToImport = entitiesToImport;
	}

	/**
	 * @return the modelStatus
	 */
	public ProjectModelStatus getModelStatus() {
		return modelStatus;
	}

	/**
	 * @param modelStatus
	 *          the modelStatus to set
	 */
	public void setModelStatus(ProjectModelStatus modelStatus) {
		this.modelStatus = modelStatus;
	}

}
