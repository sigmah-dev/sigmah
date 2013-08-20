package org.sigmah.shared.domain.element;

import java.io.Serializable;

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

@Entity
@Table(name = "budget_sub_field")
public class BudgetSubField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8199770095282982247L;
	private Long id;
	private BudgetElement budgetElement;
	private String label;
	private Integer fieldOrder;
	private BudgetSubFieldType type;

	public BudgetSubField() {
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_budget_sub_field", nullable = false)
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_budget_element", nullable = false)
	public BudgetElement getBudgetElement() {
		return budgetElement;
	}

	public void setBudgetElement(BudgetElement budgetElement) {
		this.budgetElement = budgetElement;
	}

	/**
	 * @return the label
	 */
	@Column(name = "label")
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name = "fieldOrder")
	public Integer getFieldOrder() {
		return fieldOrder;
	}

	public void setFieldOrder(Integer order) {
		this.fieldOrder = order;
	}

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	public BudgetSubFieldType getType() {
		return type;
	}

	public void setType(BudgetSubFieldType type) {
		this.type = type;
	}

}
