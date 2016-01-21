package org.sigmah.server.domain.report;

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

import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.quality.QualityCriterion;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Key Question domain entity.
 * </p>
 * <p>
 * Represents a key question associated with a report section.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.KEY_QUESTION_TABLE)
public class KeyQuestion extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3607090451212723216L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.KEY_QUESTION_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.KEY_QUESTION_COLUMN_LABEL)
	@Size(max = EntityConstants.KEY_QUESTION_LABEL_MAX_LENGTH)
	private String label;

	@Column(name = EntityConstants.KEY_QUESTION_COLUMN_SORT_ORDER)
	private Integer index;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	// FIXME this should be a foreign key.
	// Must specify if there is any reason for being only an integer, or change type to ProjectReportModelSection
	@Column(name = EntityConstants.KEY_QUESTION_COLUMN_SECTION_ID)
	private Integer sectionId;

	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.KEY_QUESTION_COLUMN_QUALITY_CRITERION_ID)
	private QualityCriterion qualityCriterion;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("label", label);
		builder.append("index", index);
	}

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param sectionId
	 *          the parent section identifier.
	 * @param modelesReset
	 *          the map of the reseted objects (original object, transformed object).
	 * @param modelesImport
	 *          the list of object that have been transformed or are being transformed.
	 */
	public void resetImport(Integer sectionId, HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
		this.id = null;
		this.sectionId = sectionId;
		if (this.qualityCriterion != null) {
			this.qualityCriterion.resetImport(modelesReset, modelesImport);
		}
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public QualityCriterion getQualityCriterion() {
		return qualityCriterion;
	}

	public void setQualityCriterion(QualityCriterion qualityCriterion) {
		this.qualityCriterion = qualityCriterion;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
