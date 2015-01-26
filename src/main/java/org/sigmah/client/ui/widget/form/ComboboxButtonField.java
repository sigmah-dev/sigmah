package org.sigmah.client.ui.widget.form;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.MultiField;

/**
 * <p>
 * Custom field implementation composed of one (or multiple) {@link ComboBox} and a {@link Button} (horizontally
 * aligned).
 * </p>
 * <p>
 * This field is not parameterized with generic argument to allow multiple fields types.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ComboboxButtonField extends MultiField<ModelData> implements Loadable {

	private final List<ComboBox<ModelData>> comboBoxes;
	private final Button button;
	private final AdapterField buttonAdapter;

	/**
	 * Initializes a new {@code ComboboxButtonField} field instance with <b>one</b> comboBox.<br>
	 * To initialize multiple comboBoxes, see {@link #ComboboxButtonField(String, Pair...)} constructor.
	 * 
	 * @param fieldLabel
	 *          The field label.
	 * @param valueField
	 *          The comboBox value field.
	 * @param displayField
	 *          The comboBox display field.
	 */
	public ComboboxButtonField(String fieldLabel, String valueField, String displayField) {
		this(fieldLabel, new Pair<String, String>(valueField, displayField));
	}

	/**
	 * Initializes a new {@code ComboboxButtonField} field instance with multiple comboBoxes.
	 * 
	 * @param fieldLabel
	 *          The field label.
	 * @param comboBoxFields
	 *          The comboBox(es) { value ; display } fields.
	 */
	@SafeVarargs
	public ComboboxButtonField(String fieldLabel, Pair<String, String>... comboBoxFields) {

		assert ClientUtils.isNotEmpty(comboBoxFields) : "Provide at least one comboBox configuration.";

		comboBoxes = new ArrayList<ComboBox<ModelData>>(comboBoxFields.length);
		button = Forms.button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add()); // Default configuration.
		buttonAdapter = Forms.adapter(fieldLabel, button);

		if (ClientUtils.isNotBlank(fieldLabel)) {
			setFieldLabel(fieldLabel);
		} else {
			setLabelSeparator("");
		}

		setSpacing(7);

		for (final Pair<String, String> comboBoxField : comboBoxFields) {
			if (comboBoxField == null) {
				continue;
			}
			final ComboBox<ModelData> comboBox = Forms.combobox(null, false, comboBoxField.left, comboBoxField.right);
			comboBoxes.add(comboBox);
			add(comboBox);
		}

		add(buttonAdapter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResize(int width, int height) {

		super.onResize(width, height);

		int buttonWidth = buttonAdapter.el().getParent().getWidth();
		if (buttonWidth <= 0) {
			// TODO Try to 'always' determine dynamic button width (see 'EditFlexibleElementAdminView' problem).
			buttonWidth = 70; // Arbitrary width considering all i18n translations widths.
			button.setWidth(buttonWidth);
		}

		final int comboBoxesNumber = comboBoxes.size();
		final int comboBoxesTotalWidth = width - buttonWidth - (comboBoxesNumber * spacing);
		final int comboBoxWidth = comboBoxesTotalWidth / comboBoxesNumber;

		for (final ComboBox<ModelData> comboBox : comboBoxes) {
			comboBox.setSize(comboBoxWidth, height);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoading(boolean loading) {
		button.setLoading(loading);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLoading() {
		return button.isLoading();
	}

	/**
	 * Returns the given {@code index} corresponding inner {@link ComboBox} field.<br>
	 * The returned field type is automatically cast to the referenced type.
	 * 
	 * @param index
	 *          The comboBox index.
	 * @return The inner {@link ComboBox} field.
	 * @throws IndexOutOfBoundsException
	 *           If the given {@code index} is out of bounds.
	 */
	@SuppressWarnings("unchecked")
	public <M extends ModelData> ComboBox<M> getComboBox(final int index) {
		return (ComboBox<M>) comboBoxes.get(index);
	}

	/**
	 * Returns the given {@code index} corresponding inner {@code ComboBox}'s {@link ListStore}.<br>
	 * The returned store type is automatically cast to the referenced type.
	 * 
	 * @param index
	 *          The comboBox index.
	 * @return The given {@code index} corresponding inner {@code ComboBox}'s {@link ListStore}.
	 * @throws IndexOutOfBoundsException
	 *           If the given {@code index} is out of bounds.
	 */
	@SuppressWarnings("unchecked")
	public <M extends ModelData> ListStore<M> getStore(final int index) {
		return (ListStore<M>) getComboBox(index).getStore();
	}

	/**
	 * Returns the inner {@link Button}.
	 * 
	 * @return The inner {@link Button} (never {@code null}).
	 */
	public Button getButton() {
		return button;
	}

	/**
	 * Clears any text/value currently set in the comboBox(es) field(s).
	 */
	public void clearSelections() {
		for (final ComboBox<ModelData> comboBox : comboBoxes) {
			comboBox.clearSelections();
		}
	}

}
