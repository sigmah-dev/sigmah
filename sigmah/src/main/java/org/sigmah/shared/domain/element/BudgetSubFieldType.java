package org.sigmah.shared.domain.element;

import org.sigmah.client.i18n.I18N;

public enum BudgetSubFieldType {
	
	PLANNED,
	
	SPENT,
	
	RECEIVED;
	
	
	public static String getName(BudgetSubFieldType e){
		String name = null;
		switch(e){
		case PLANNED:
			name = I18N.CONSTANTS.projectPlannedBudget();
			break;
		case RECEIVED:
			name = I18N.CONSTANTS.projectReceivedBudget();
			break;
		case SPENT:
			name = I18N.CONSTANTS.projectSpendBudget();
			break;
		default:
			break;
		
		}
		return name;
	}

}
