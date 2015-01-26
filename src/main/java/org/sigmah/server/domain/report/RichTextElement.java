package org.sigmah.server.domain.report;

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
