package org.sigmah.server.domain;

import javax.persistence.*;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.EntityConstants;

@Entity
@Table(name = EntityConstants.FRAMEWORK_ELEMENT_IMPLEMENTATION_TABLE)
public class FrameworkElementImplementation extends AbstractEntityId<Integer> {
	private static final long serialVersionUID = -5739408225983154095L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.FRAMEWORK_ELEMENT_IMPLEMENTATION_COLUMN_ID)
	private Integer id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.FRAMEWORK_ELEMENT_COLUMN_ID, nullable = false)
	private FrameworkElement frameworkElement;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false)
	private FlexibleElement flexibleElement;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = EntityConstants.FRAMEWORK_FULFILLMENT_COLUMN_ID, nullable = false)
	private FrameworkFulfillment frameworkFulfillment;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public FrameworkElement getFrameworkElement() {
		return frameworkElement;
	}

	public void setFrameworkElement(FrameworkElement frameworkElement) {
		this.frameworkElement = frameworkElement;
	}

	public FlexibleElement getFlexibleElement() {
		return flexibleElement;
	}

	public void setFlexibleElement(FlexibleElement flexibleElement) {
		this.flexibleElement = flexibleElement;
	}

	public FrameworkFulfillment getFrameworkFulfillment() {
		return frameworkFulfillment;
	}

	public void setFrameworkFulfillment(FrameworkFulfillment frameworkFulfillment) {
		this.frameworkFulfillment = frameworkFulfillment;
	}
}
