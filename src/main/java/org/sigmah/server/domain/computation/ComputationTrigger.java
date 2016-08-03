package org.sigmah.server.domain.computation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.ComputationElement;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
@Entity
//@Table(name = EntityConstants.PHASE_TABLE)
public class ComputationTrigger extends AbstractEntityId<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
//	@Column(name = EntityConstants.PHASE_COLUMN_ID)
	private Integer id;
	
	private ComputationElement linkedElement;
	
	private ComputationTriggerSourceType sourceType;
	
	private Integer sourceId;
	
	@Override
	public Integer getId() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setId(Integer id) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
