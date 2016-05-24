package org.sigmah.server.domain;

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
