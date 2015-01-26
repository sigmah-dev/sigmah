package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.BooleanResult;
import org.sigmah.shared.dto.IsModel.ModelType;

/**
 * Command to check if a project model or orgunit model has been ever used.
 * 
 * @author HUZHE (zhe.hu32@gmail.com) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class CheckModelUsage extends AbstractCommand<BooleanResult> {

	private ModelType modelType;
	private Integer modelId;

	protected CheckModelUsage() {
		// Serialization.
	}

	public CheckModelUsage(final ModelType modelType, final Integer modelId) {
		this.modelType = modelType;
		this.modelId = modelId;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public Integer getModelId() {
		return modelId;
	}

}
