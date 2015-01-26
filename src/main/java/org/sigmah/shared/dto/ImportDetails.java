package org.sigmah.shared.dto;

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
