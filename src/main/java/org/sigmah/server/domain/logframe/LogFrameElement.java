package org.sigmah.server.domain.logframe;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * Abstract logframe element parent domain entity.
 * </p>
 * <p>
 * Base class for all LogFrame elements, such as SpecificObjective, Activity, etc.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOGFRAME_ELEMENT_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LogFrameElement extends AbstractEntityId<Integer> implements Comparable<LogFrameElement> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -5507774046181168381L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LOGFRAME_ELEMENT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LOGFRAME_ELEMENT_COLUMN_CODE, nullable = false)
	@NotNull
	protected Integer code;

	@Column(name = EntityConstants.LOGFRAME_ELEMENT_COLUMN_POSITION)
	protected Integer position;

	@Column(name = EntityConstants.LOGFRAME_ELEMENT_COLUMN_RISKS_ASSUMPTIONS, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	protected String risksAndAssumptions;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.LOGFRAME_GROUP_COLUMN_ID, nullable = true)
	protected LogFrameGroup group;

	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = EntityConstants.LOGFRAME_ELEMENT_INDICATOR_LINK_TABLE)
	@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.LOGFRAME_ELEMENT_HIDE_DELETED_CONDITION)
	protected Set<Indicator> indicators = new HashSet<Indicator>(0);

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final LogFrameElement o) {
		final int c1 = getCode() == null ? 0 : getCode();
		final int c2 = o.getCode() == null ? 0 : o.getCode();

		return c1 - c2;
	}

	protected final Set<Indicator> copyIndicators(final LogFrameCopyContext context) {

		final Set<Indicator> copies = new HashSet<Indicator>();

		for (final Indicator indicator : indicators) {
			if (!indicator.isDeleted()) {

				switch (context.getIndicatorStrategy()) {
					case REFERENCE:
						if (!context.getDestinationProjet().getId().equals(indicator.getDatabase().getId())) {
							throw new IllegalStateException("IndicatorStrategy.REFERENCE can only be used when copying a logframe within the same project");
						}
						copies.add(indicator);
						break;

					case DUPLICATE_AND_LINK:
						copies.add(copyAndLink(indicator, context));
						break;

					case DUPLICATE:
						copies.add(copy(indicator, context));
						break;
				}
			}
		}
		return copies;
	}

	private Indicator copy(final Indicator indicator, final LogFrameCopyContext context) {

		final Indicator copy = indicator.copy(context.getDestinationProjet());
		copy.setActivity(context.getActivityCopy(indicator.getActivity()));
		return copy;
	}

	private Indicator copyAndLink(final Indicator indicator, final LogFrameCopyContext context) {

		final Indicator copy = copy(indicator, context);
		copy.getDataSources().add(indicator);
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

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public LogFrameGroup getGroup() {
		return group;
	}

	public void setGroup(LogFrameGroup group) {
		this.group = group;
	}

	public String getRisksAndAssumptions() {
		return risksAndAssumptions;
	}

	public void setRisksAndAssumptions(String risksAndAssumptions) {
		this.risksAndAssumptions = risksAndAssumptions;
	}

	public Set<Indicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(Set<Indicator> indicators) {
		this.indicators = indicators;
	}

}
