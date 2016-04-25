package org.sigmah.server.domain;

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

import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

@Entity
@Table(name = EntityConstants.FRAMEWORK_TABLE)
public class Framework extends AbstractEntityId<Integer> {
	private static final long serialVersionUID = -3264965526006270675L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.FRAMEWORK_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.FRAMEWORK_COLUMN_LABEL, nullable = false)
	@NotNull
	private String label;

	@Column(name = EntityConstants.FRAMEWORK_COLUMN_AVAILABILITY_STATUS, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private AvailabilityStatus availabilityStatus;

	@Column(name = EntityConstants.FRAMEWORK_COLUMN_IMPLEMENTATION_STATUS, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ImplementationStatus implementationStatus;

	@ManyToOne
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

	@OneToMany(mappedBy = "framework", fetch = FetchType.LAZY)
	@OrderBy("level ASC, label ASC")
	private List<FrameworkHierarchy> frameworkHierarchies;

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

	public AvailabilityStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}

	public ImplementationStatus getImplementationStatus() {
		return implementationStatus;
	}

	public void setImplementationStatus(ImplementationStatus implementationStatus) {
		this.implementationStatus = implementationStatus;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public List<FrameworkHierarchy> getFrameworkHierarchies() {
		return frameworkHierarchies;
	}

	public void setFrameworkHierarchies(List<FrameworkHierarchy> frameworkHierarchies) {
		this.frameworkHierarchies = frameworkHierarchies;
	}

	public enum AvailabilityStatus {
		DRAFT, AVAILABLE, UNAVAILABLE
	}

	public enum ImplementationStatus {
		WAITING_FOR_IMPLEMENTATION, IMPLEMENTED
	}
}
