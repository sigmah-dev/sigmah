package org.sigmah.server.domain.element;

import javax.persistence.CascadeType;
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
import org.sigmah.shared.dto.referential.BudgetSubFieldType;

/**
 * <p>
 * Budget sub field domain entity.
 * </p>
 * 
 * @author Guerline Jean-Baptiste, gjbaptiste@ideia.fr
 */
@Entity
@Table(name = EntityConstants.BUDGET_SUB_FIELD_TABLE)
public class BudgetSubField extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8199770095282982247L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.BUDGET_SUB_FIELD_COLUMN_ID, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.BUDGET_SUB_FIELD_COLMUN_LABEL)
	private String label;

	@Column(name = EntityConstants.BUDGET_SUB_FIELD_COLMUN_FIELD_ORDER)
	private Integer fieldOrder;

	@Column(name = EntityConstants.BUDGET_SUB_FIELD_COLMUN_TYPE)
	@Enumerated(EnumType.STRING)
	private BudgetSubFieldType type;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = EntityConstants.BUDGET_SUB_FIELD_COLMUN_ID_BUDGET_ELEMENT, nullable = false)
	@NotNull
	private BudgetElement budgetElement;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("id", id);
		builder.append("label", label);
		builder.append("fieldOrder", fieldOrder);
		builder.append("type", type);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS AND SETTERS.
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

	public BudgetElement getBudgetElement() {
		return budgetElement;
	}

	public void setBudgetElement(BudgetElement budgetElement) {
		this.budgetElement = budgetElement;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getFieldOrder() {
		return fieldOrder;
	}

	public void setFieldOrder(Integer order) {
		this.fieldOrder = order;
	}

	public BudgetSubFieldType getType() {
		return type;
	}

	public void setType(BudgetSubFieldType type) {
		this.type = type;
	}

}
