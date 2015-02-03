package org.sigmah.shared.dto.element;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;

/**
 * Field that encapsulates an other field and adds an history button next to it.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class HistoryWrapper extends AdapterField {
	
	/**
	 * Wrapped field.
	 */
	private final Field<?> field;
	
	/**
	 * History button.
	 */
	private final Button historyButton;

	/**
	 * Wrap the given field.
	 * 
	 * @param field Field to wrap.
	 */
	public HistoryWrapper(Field<?> field) {
		super(new FlowPanel());
		this.field = field;

		final Grid grid = new Grid(1, 2);
		((FlowPanel)getWidget()).add(grid);
		
		historyButton = Forms.button();
		historyButton.setIcon(IconImageBundle.ICONS.history16());
		
		grid.setWidget(0, 0, field);
		grid.setWidget(0, 1, historyButton);
		
		grid.getCellFormatter().setStyleName(0, 1, "flexibility-action-iconable");
	}

	/**
	 * Retrieve the history button.
	 * 
	 * @return the history button.
	 */
	public Button getHistoryButton() {
		return historyButton;
	}
	
	// --
	// Wiring fields method to the wrapped element.
	// --

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public String getFieldLabel() {
		return field.getFieldLabel();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public String getLabelSeparator() {
		return field.getLabelSeparator();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public Object getValue() {
		return field.getValue();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public String getRawValue() {
		return field.getRawValue();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public boolean isDirty() {
		return field.isDirty();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void setName(String name) {
		field.setName(name);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void clear() {
		field.clear();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void clearInvalid() {
		field.clearInvalid();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void clearState() {
		field.clearState();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void focus() {
		field.focus();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void forceInvalid(String msg) {
		field.forceInvalid(msg);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public El mask(String message, String messageStyleName) {
		return field.mask(message, messageStyleName);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void unmask() {
		field.unmask();
	}
}
