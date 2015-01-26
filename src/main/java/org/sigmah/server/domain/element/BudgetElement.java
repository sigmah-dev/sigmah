package org.sigmah.server.domain.element;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Budget element domain entity.
 * </p>
 * 
 * @author Guerline Jean-Baptiste, gjbaptiste@ideia.fr
 */
@Entity
@Table(name = EntityConstants.BUDGET_ELEMENT_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public class BudgetElement extends DefaultFlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7471247661332358221L;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "budgetElement", cascade = CascadeType.ALL)
	@OrderBy("fieldOrder")
	private List<BudgetSubField> budgetSubFields = new ArrayList<BudgetSubField>();

	@OneToOne
	@JoinColumn(name = EntityConstants.BUDGET_ELEMENT_COLUMN_ID_RATIO_DIVIDEND)
	private BudgetSubField ratioDividend;

	@OneToOne
	@JoinColumn(name = EntityConstants.BUDGET_ELEMENT_COLUMN_ID_RATIO_DIVISOR)
	private BudgetSubField ratioDivisor;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	// Add methods here.

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public List<BudgetSubField> getBudgetSubFields() {
		return budgetSubFields;
	}

	public void setBudgetSubFields(List<BudgetSubField> budgetSubFields) {
		this.budgetSubFields = budgetSubFields;
	}

	public BudgetSubField getRatioDividend() {
		return ratioDividend;
	}

	public void setRatioDividend(BudgetSubField ratioDividend) {
		this.ratioDividend = ratioDividend;
	}

	public BudgetSubField getRatioDivisor() {
		return ratioDivisor;
	}

	public void setRatioDivisor(BudgetSubField ratioDivisor) {
		this.ratioDivisor = ratioDivisor;
	}

}
