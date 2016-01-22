package org.sigmah.shared.dto.report;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.EntityDTO;

/**
 * Represents a key question associated with a project report section.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class KeyQuestionDTO implements EntityDTO<Integer>, ProjectReportContent {

	private Integer id;
	private String label;
	private RichTextElementDTO richTextElementDTO;
	private int number;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "report.KeyQuestion";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("id", getId());
		builder.append("label", getLabel());
		builder.append("number", getNumber());

		return builder.toString();
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public RichTextElementDTO getRichTextElementDTO() {
		return richTextElementDTO;
	}

	public void setRichTextElementDTO(RichTextElementDTO richTextElementDTO) {
		this.richTextElementDTO = richTextElementDTO;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
