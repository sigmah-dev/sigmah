package org.sigmah.shared.dto.report;

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
