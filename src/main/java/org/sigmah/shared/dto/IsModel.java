package org.sigmah.shared.dto;

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
		OrgUnitModel;

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
	public Date getDateMaintenance();
	
}
