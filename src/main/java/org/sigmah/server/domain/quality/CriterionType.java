package org.sigmah.server.domain.quality;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Criterion type domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.CRITERION_TYPE_TABLE)
public class CriterionType extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6055173450423740216L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.CRITERION_TYPE_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.CRITERION_TYPE_COLUMN_LABEL, nullable = false, length = EntityConstants.CRITERION_TYPE_LABEL_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.CRITERION_TYPE_LABEL_MAX_LENGTH)
	private String label;

	@Column(name = EntityConstants.CRITERION_TYPE_COLUMN_LEVEL, nullable = false)
	@NotNull
	private Integer level;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.QUALITY_FRAMEWORK_COLUMN_ID, nullable = false)
	@NotNull
	private QualityFramework qualityFramework;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param qualityFramework
	 *          the quality framework
	 */
	public void resetImport(QualityFramework qualityFramework) {
		this.id = null;
		this.qualityFramework = qualityFramework;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("label", label);
		builder.append("level", level);
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public QualityFramework getQualityFramework() {
		return qualityFramework;
	}

	public void setQualityFramework(QualityFramework qualityFramework) {
		this.qualityFramework = qualityFramework;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

}
