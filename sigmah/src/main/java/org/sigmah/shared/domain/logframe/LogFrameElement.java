package org.sigmah.shared.domain.logframe;

import java.io.Serializable;
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

import org.sigmah.shared.domain.Indicator;

/**
 * Base class for all LogFrame elements, such as SpecificObjective, Activity, etc
 */
@Entity
@Table(name="log_frame_element")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LogFrameElement implements Serializable, Comparable<LogFrameElement> {
	private Integer id;
	protected Integer code;
	protected Integer position;
	protected LogFrameGroup group;
	protected String risks;
	protected String assumptions;
	protected Set<Indicator> indicators = new HashSet<Indicator>(0);
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_element")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name = "code", nullable = false)
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Column(name = "position")
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "id_group", nullable = true) 
	public LogFrameGroup getGroup() {
		return group;
	}

	public void setGroup(LogFrameGroup group) {
		this.group = group;
	}

	@Column(name = "risks", columnDefinition = "TEXT")
	public String getRisks() {
		return risks;
	}

	public void setRisks(String risks) {
		this.risks = risks;
	}

	@Column(name = "assumptions", columnDefinition = "TEXT")
	public String getAssumptions() {
		return assumptions;
	}

	public void setAssumptions(String assumptions) {
		this.assumptions = assumptions;
	}

	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(name = "log_frame_indicators")
    @org.hibernate.annotations.Filter(
            name = "hideDeleted",
            condition = "DateDeleted is null")
	public Set<Indicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(Set<Indicator> indicators) {
		this.indicators = indicators;
	}

	@Override
	public int compareTo(LogFrameElement o) {
		int c1 = getCode() == null ? 0 : getCode();
		int c2 = o.getCode() == null ? 0 : o.getCode();
		
		return c1 - c2;
	}
	
	protected final Set<Indicator> copyIndicators(LogFrameCopyContext context) {
		Set<Indicator> copies = new HashSet<Indicator>();
		for(Indicator indicator : indicators) {
			if(!indicator.isDeleted()) {
				switch(context.getIndicatorStrategy()) {
				case REFERENCE:
					if(context.getDestinationProjet().getId() != indicator.getDatabase().getId()) {
						throw new IllegalStateException("IndicatorStrategy.REFERENCE can only be used when copying a logframe within the same project");
					}
					copies.add(indicator);
					break;
					
				case DUPLICATE_AND_LINK:
					copies.add(copyAndLink(indicator, context));
					break;
					
				case DUPLICATE:
					copies.add(indicator.copy(context.getDestinationProjet()));
					break;
				}
			}
		}
		return copies;
	}

	private Indicator copyAndLink(Indicator indicator,
			LogFrameCopyContext context) {
		
		Indicator copy = indicator.copy(context.getDestinationProjet());
		copy.getDataSources().add(indicator);
		return copy;
	}
}
