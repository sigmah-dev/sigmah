package org.sigmah.server.domain.logframe;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.LogFrameGroupType;

/**
 * <p>
 * Logframe group domain entity.
 * </p>
 * <p>
 * Represents a group of log frame elements displayed together.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOGFRAME_GROUP_TABLE)
public class LogFrameGroup extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5457361875504176525L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LOGFRAME_GROUP_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LOGFRAME_GROUP_COLUMN_TYPE)
	@Enumerated(EnumType.STRING)
	private LogFrameGroupType type;

	@Column(name = EntityConstants.LOGFRAME_GROUP_COLUMN_LABEL, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String label;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.LOGFRAME_COLUMN_ID, nullable = false)
	@NotNull
	private LogFrame parentLogFrame;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Duplicates this group.
	 * 
	 * @param parentLogFrame
	 *          Log frame that will contains this group.
	 * @return A copy of this group.
	 */
	public LogFrameGroup copy(final LogFrame parentLogFrame) {

		final LogFrameGroup copy = new LogFrameGroup();

		copy.type = type;
		copy.label = label;
		copy.parentLogFrame = parentLogFrame;

		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("type", type);
		builder.append("label", label);
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

	public LogFrameGroupType getType() {
		return type;
	}

	public void setType(LogFrameGroupType type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public LogFrame getParentLogFrame() {
		return parentLogFrame;
	}

	public void setParentLogFrame(LogFrame parentLogFrame) {
		this.parentLogFrame = parentLogFrame;
	}

}
