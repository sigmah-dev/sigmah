package org.sigmah.shared.dto.report;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.EntityDTO;

/**
 * Editable html field of a project report.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class RichTextElementDTO implements EntityDTO<Integer>, ProjectReportContent {

	private Integer id;
	private String text;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "report.RichTextElement";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("id", getId());
		builder.append("text", getText());

		return builder.toString();
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
