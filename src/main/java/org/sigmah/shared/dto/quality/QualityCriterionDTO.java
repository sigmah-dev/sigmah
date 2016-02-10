package org.sigmah.shared.dto.quality;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity quality.QualityCriterion.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class QualityCriterionDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -49281834964182785L;

	private transient CriterionTypeDTO type;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "quality.QualityCriterion";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("code", getCode());
		builder.append("label", getLabel());
		builder.append("children", getSubCriteria());
	}

	/**
	 * Retrieves this criterion type.
	 * 
	 * @return The criterion type.
	 */
	public CriterionTypeDTO getCriterionType() {

		if (type == null) {

			int level = 0;

			// Computes the position of this criterion in the hierarchy of the
			// quality framework.
			QualityCriterionDTO parent = getParentCriterion();
			QualityFrameworkDTO framework = null;
			while (parent != null) {
				level++;
				framework = parent.getQualityFramework();
				parent = parent.getParentCriterion();
			}

			assert framework != null;

			type = framework.getType(level);
		}

		return type;
	}

	/**
	 * Returns the info of this criterion as a string.
	 * 
	 * @return The info string.
	 */
	public String getInfo() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getCode());
		sb.append(" - ");
		sb.append(getLabel());
		sb.append(" (");
		final CriterionTypeDTO type = getCriterionType();
		if (type != null) {
			sb.append(getCriterionType().getLabel());
		} else {
			sb.append("...");
		}
		sb.append(")");
		return sb.toString();
	}

	// Criterion id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Criterion code.
	public String getCode() {
		return get("code");
	}

	public void setCode(String code) {
		set("code", code);
	}

	// Criterion label.
	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	// Quality framework.
	public QualityFrameworkDTO getQualityFramework() {
		return get("qualityFramework");
	}

	public void setQualityFramework(QualityFrameworkDTO qualityFramework) {
		set("qualityFramework", qualityFramework);
	}

	// Criterion parent.
	public QualityCriterionDTO getParentCriterion() {
		return get("parentCriterion");
	}

	public void setParentCriterion(QualityCriterionDTO parentCriterion) {
		set("parentCriterion", parentCriterion);
	}

	// Criterion sub criteria.
	public List<QualityCriterionDTO> getSubCriteria() {
		return get("subCriteria");
	}

	public void setSubCriteria(List<QualityCriterionDTO> subCriteria) {
		set("subCriteria", subCriteria);
	}

}
