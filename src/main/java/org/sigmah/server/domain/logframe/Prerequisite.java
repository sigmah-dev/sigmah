package org.sigmah.server.domain.logframe;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * <p>
 * Prerequisite domain entity.
 * </p>
 * <p>
 * Represents an item of the prerequisites list of a log frame.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOGFRAME_PREREQUISITE_TABLE)
public class Prerequisite extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3093621922617967414L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LOGFRAME_PREREQUISITE_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LOGFRAME_PREREQUISITE_COLUMN_CODE, nullable = false)
	@NotNull
	private Integer code;

	@Column(name = EntityConstants.LOGFRAME_PREREQUISITE_COLUMN_CONTENT, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String content;

	@Column(name = EntityConstants.LOGFRAME_PREREQUISITE_COLUMN_POSITION)
	private Integer position;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_log_frame", nullable = true)
	private LogFrame parentLogFrame;

	@ManyToOne(optional = true)
	@JoinColumn(name = "id_group", nullable = true)
	private LogFrameGroup group;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Duplicates this prerequisite (omits the ID).
	 * 
	 * @param parentLogFrame
	 *          Log frame that will contains this copy.
	 * @param context
	 *          Map of copied groups.
	 * @return A copy of this prerequisite.
	 */
	public Prerequisite copy(final LogFrame parentLogFrame, final LogFrameCopyContext context) {

		final Prerequisite copy = new Prerequisite();

		copy.code = this.code;
		copy.content = this.content;
		copy.parentLogFrame = parentLogFrame;
		copy.group = context.getGroupCopy(this.group);
		copy.position = this.position;

		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("code", code);
		builder.append("position", position);
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

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LogFrame getParentLogFrame() {
		return parentLogFrame;
	}

	public void setParentLogFrame(LogFrame parentLogFrame) {
		this.parentLogFrame = parentLogFrame;
	}

	public LogFrameGroup getGroup() {
		return group;
	}

	public void setGroup(LogFrameGroup group) {
		this.group = group;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

}
