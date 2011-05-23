package org.sigmah.shared.domain.logframe;

import org.sigmah.shared.domain.Deleteable;
import org.sigmah.shared.domain.Indicator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Base class for all LogFrame elements, such as SpecificObjective, Activity, etc
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@org.hibernate.annotations.FilterDefs({ @org.hibernate.annotations.FilterDef(name = "hideDeleted") })
@org.hibernate.annotations.Filters({ @org.hibernate.annotations.Filter(name = "hideDeleted", condition = "DateDeleted is null") })
public abstract class LogFrameElement implements Serializable, Deleteable {
  private Integer id;
  protected Date dateDeleted;
  protected Integer code;
  protected Integer position;
  protected LogFrameGroup group;
  protected String risks;
  protected String assumptions;
  private Set<Indicator> indicators;


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id_objective")
  public Integer getId() {
      return id;
  }

  public void setId(Integer id) {
      this.id = id;
  }

  @Column
  @Temporal(value = TemporalType.TIMESTAMP)
  public Date getDateDeleted() {
      return this.dateDeleted;
  }

  public void setDateDeleted(Date date) {
      this.dateDeleted = date;
  }

  @Override
  public void delete() {
      setDateDeleted(new Date());
  }

  @Override
  @Transient
  public boolean isDeleted() {
      return getDateDeleted() != null;
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

  @ManyToMany
  @JoinTable(name = "LogFrameElementIndicators")
  public Set<Indicator> getIndicators() {
    return indicators;
  }

  public void setIndicators(Set<Indicator> indicators) {
    this.indicators = indicators;
  }
}
