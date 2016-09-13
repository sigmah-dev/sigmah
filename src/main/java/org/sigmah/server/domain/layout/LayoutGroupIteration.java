package org.sigmah.server.domain.layout;

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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.value.Value;

@Entity
@Table(name = EntityConstants.LAYOUT_GROUP_ITERATION_TABLE)
public class LayoutGroupIteration extends AbstractEntityId<Integer> {

  private static final long serialVersionUID = -1375256465284801367L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = EntityConstants.LAYOUT_GROUP_ITERATION_COLUMN_ID)
  private Integer id;

  @Column(name = EntityConstants.LAYOUT_GROUP_ITERATION_COLUMN_ID_CONTAINER, nullable = false)
  @NotNull
  private Integer containerId;

  @Column(name = EntityConstants.LAYOUT_GROUP_ITERATION_COLUMN_NAME, nullable = false)
  @NotNull
  private String name;

  // --------------------------------------------------------------------------------
  //
  // FOREIGN KEYS.
  //
  // --------------------------------------------------------------------------------

  @ManyToOne(optional = false)
  @JoinColumn(name = EntityConstants.LAYOUT_GROUP_COLUMN_ID, nullable = false)
  private LayoutGroup layoutGroup;

  @OneToMany(mappedBy = "layoutGroupIteration", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Value> values = new ArrayList<Value>();

  // --------------------------------------------------------------------------------
  //
  // METHODS.
  //
  // --------------------------------------------------------------------------------
  @Override
  protected void appendToString(ToStringBuilder builder) {
    builder.append("containerId", containerId);
    builder.append("name", name);
    builder.append("layoutGroup", layoutGroup);
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

  public Integer getContainerId() {
    return containerId;
  }

  public void setContainerId(Integer containerId) {
    this.containerId = containerId;
  }

  public LayoutGroup getLayoutGroup() {
    return layoutGroup;
  }

  public void setLayoutGroup(LayoutGroup layoutGroup) {
    this.layoutGroup = layoutGroup;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
