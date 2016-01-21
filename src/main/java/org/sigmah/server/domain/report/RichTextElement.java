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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Rich Text Element domain entity.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.RICH_TEXT_ELEMENT_TABLE)
public class RichTextElement extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5948142357960490953L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.RICH_TEXT_ELEMENT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.RICH_TEXT_ELEMENT_COLUMN_SORT_ORDER)
	private Integer index;

	@Column(name = EntityConstants.RICH_TEXT_ELEMENT_COLUMN_TEXT, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String text;

	@Column(name = EntityConstants.RICH_TEXT_ELEMENT_COLUMN_SECTION_ID)
	private Integer sectionId;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = EntityConstants.RICH_TEXT_ELEMENT_COLUMN_VERSION_ID)
	private ProjectReportVersion version;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("index", index);
		builder.append("text", text);
	}

	/**
	 * Creates a new RichTextElement and fill some of its values.<br>
	 * <br>
	 * Not similar to the clone method since it doesn't copy every fields.
	 * 
	 * @return A new RichTextElement object.
	 */
	public RichTextElement duplicate() {
		final RichTextElement duplicate = new RichTextElement();
		duplicate.sectionId = this.sectionId;
		duplicate.index = this.index;
		duplicate.text = this.text;

		return duplicate;
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

	public ProjectReportVersion getVersion() {
		return version;
	}

	public void setVersion(ProjectReportVersion version) {
		this.version = version;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
