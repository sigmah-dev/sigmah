package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.IsModel.ModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * GetAvailableStatusForModel command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetAvailableStatusForModel extends AbstractCommand<ListResult<ProjectModelStatus>> {

	/**
	 * The model type.
	 */
	private ModelType modelType;

	/**
	 * The model id.
	 */
	private Integer modelId;

	/**
	 * The current model status.
	 */
	private ProjectModelStatus status;

	protected GetAvailableStatusForModel() {
		// Serialization.
	}

	/**
	 * Retrieves the available {@link ProjectModelStatus} for the given arguments.
	 * 
	 * @param modelType
	 *          The model type.
	 * @param modelId
	 *          The model id.
	 * @param status
	 *          The model current status.
	 * @see ModelType
	 */
	public GetAvailableStatusForModel(ModelType modelType, Integer modelId, ProjectModelStatus status) {
		this.modelType = modelType;
		this.modelId = modelId;
		this.status = status;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public Integer getModelId() {
		return modelId;
	}

	public ProjectModelStatus getStatus() {
		return status;
	}

}
