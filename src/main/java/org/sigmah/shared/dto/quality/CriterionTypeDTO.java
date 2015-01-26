package org.sigmah.shared.dto.quality;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity quality.CriterionType.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CriterionTypeDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 9171979198420463751L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "quality.CriterionType";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", getLabel());
		builder.append("level", getLevel());
	}

	// Type id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Type label.
	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	// Type framework.
	public QualityFrameworkDTO getQualityFramework() {
		return get("qualityFramework");
	}

	public void setQualityFramework(QualityFrameworkDTO qualityFramework) {
		set("qualityFramework", qualityFramework);
	}

	// Type level.
	public Integer getLevel() {
		return (Integer) get("level");
	}

	public void setLevel(Integer level) {
		set("level", level);
	}

}
