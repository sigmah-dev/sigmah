package org.sigmah.shared.dto.element;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
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
 * @param <V> Type of the value of the encapsulated field.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class HistoryWrapper<V> extends AdapterField {

	private static final int BUTTON_WIDTH = 26;
	
	/**
	 * Wrapped field.
	 */
	private final Field<V> field;
	
	/**
	 * History button.
	 */
	private final Button historyButton;
	
	/**
	 * Wrap the given field.
	 * 
	 * @param field Field to wrap.
	 */
	public HistoryWrapper(Field<V> field) {
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

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		field.setSize(width - BUTTON_WIDTH, height);
	}
	
	// --
	// Wiring fields method to the wrapped element.
	// --

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void addListener(EventType eventType, Listener<? extends BaseEvent> listener) {
		field.addListener(eventType, listener);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void removeAllListeners() {
		field.removeAllListeners();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void removeListener(EventType eventType, Listener<? extends BaseEvent> listener) {
		field.removeListener(eventType, listener);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void setEnabled(boolean enabled) {
		field.setEnabled(enabled);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void enable() {
		field.enable();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void disable() {
		field.disable();
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public String getFieldLabel() {
		return field.getFieldLabel();
	}

	@Override
	public void setFieldLabel(String fieldLabelHtml) {
		field.setFieldLabel(fieldLabelHtml);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public String getLabelSeparator() {
		return field.getLabelSeparator();
	}

	@Override
	public void setLabelSeparator(String labelSeparator) {
		field.setLabelSeparator(labelSeparator);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public V getValue() {
		return field.getValue();
	}

	@Override
	public void setValue(Object value) {
		field.setValue((V)value);
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
