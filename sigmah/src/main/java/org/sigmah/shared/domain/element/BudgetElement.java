package org.sigmah.shared.domain.element;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
/**
 * 
 * @author Guerline Jean-Baptiste, gjbaptiste@ideia.fr
 *
 */
@Entity
@Table(name = "budget_element")
@Inheritance(strategy = InheritanceType.JOINED)
public class BudgetElement extends DefaultFlexibleElement {

	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private List<BudgetSubField> budgetSubFields = new ArrayList<BudgetSubField>();
	private BudgetSubField ratioDividend;
    private BudgetSubField ratioDivisor;

    @OneToMany(mappedBy = "budgetElement", cascade = CascadeType.ALL)
	public List<BudgetSubField> getBudgetSubFields() {
	    return budgetSubFields;
    }

	public void setBudgetSubFields(List<BudgetSubField> budgetSubFields) {
	    this.budgetSubFields = budgetSubFields;
    }
    
	/**
	 * @return the ratioDividand
	 */
	@OneToOne
	@JoinColumn(name = "id_ratio_dividend")
	public BudgetSubField getRatioDividend() {
		return ratioDividend;
	}

	/**
	 * @param ratioDividand the ratioDividand to set
	 */
	public void setRatioDividend(BudgetSubField ratioDividend) {
		this.ratioDividend = ratioDividend;
	}

	/**
	 * @return the ratioDivisor
	 */
	@OneToOne
	@JoinColumn(name = "id_ratio_divisor")
	public BudgetSubField getRatioDivisor() {
		return ratioDivisor;
	}

	/**
	 * @param ratioDivisor the ratioDivisor to set
	 */
	public void setRatioDivisor(BudgetSubField ratioDivisor) {
		this.ratioDivisor = ratioDivisor;
	}

}
