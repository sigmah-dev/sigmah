/**
 * 
 */
package org.sigmah.client.page.project.dashboard;

import java.util.ArrayList;
import java.util.Date;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;


import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;

/**
 * A pop-up form window for the edit of reminders and monitored points.
 * It is almost the same as {@link org.sigmah.client.page.project.logframe.FormWindow}
 * But it adds a deletion link.
 * 
 * @author HUZHE
 *
 */
public class EditFormWindow {
	
	
	/**
	 * Listen the form completion.
	 * 
	 * @author tmi
	 * 
	 */
	public static interface FormSubmitListener {

		/**
		 * Method called when the form is correctly filled (values can be
		 * <code>null</code> if the null input is allowed for some fields). The
		 * values are returned in the same order in which fields have been
		 * added.
		 * 
		 * @param values
		 *            The input values.
		 */
		public void formSubmitted(Object... values);
		
		
		/**
		 * Method called when the uses just click the delete button to remove
		 * the model object.
		 */
		public void deleteModelObject();

	}

	/**
	 * Manages a field displayed by this window.
	 * 
	 * @author tmi
	 * 
	 */
	private static final class FieldWrapper {

		/**
		 * The form field.
		 */
		private final Field<?> field;

		/**
		 * If the field allows blank value.
		 */
		private final boolean allowBlank;

		public FieldWrapper(Field<?> field, boolean allowBlank) {
			this.field = field;
			this.allowBlank = allowBlank;
		}
	}

	/**
	 * Combobox preferred height.
	 */
	private static final int FIELD_HEIGHT = 32;

	/**
	 * Listeners.
	 */
	private final ArrayList<FormSubmitListener> listeners;

	/**
	 * The pop-up window.
	 */
	private Window window;

	/**
	 * The form title.
	 */
	private Label titleLabel;

	/**
	 * The vertical panel to display fields.
	 */
	private ContentPanel fieldsPanel;

	/**
	 * List of all fields.
	 */
	private final ArrayList<FieldWrapper> fields;
		

	/**
	 * Initialize the window.
	 */
	public EditFormWindow() {
		listeners = new ArrayList<FormSubmitListener>();
		fields = new ArrayList<FieldWrapper>();
		
	}

	/**
	 * Adds a listener.
	 * 
	 * @param l
	 *            The new listener.
	 */
	public void addFormSubmitListener(FormSubmitListener l) {

		if (l == null) {
			return;
		}

		listeners.add(l);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param l
	 *            The old listener.
	 */
	public void removeFormSubmitListener(FormSubmitListener l) {

		if (l == null) {
			return;
		}

		listeners.remove(l);
	}

	/**
	 * Informs the listeners that the form has been filled.
	 * 
	 * @param values
	 *            The input values.
	 */
	protected void fireFormSubmitted(Object... values) {
		for (final FormSubmitListener l : listeners) {
			l.formSubmitted(values);
		}
	}
	
	/**
	 * Informs the listeners that users has pressed the delete button.
	 * 
	 */
	protected void fireDeletion() {
		for (final FormSubmitListener l : listeners) {
			l.deleteModelObject();
		}
	}
	

	/**
	 * Builds the pop-up window.
	 */
	private void init() {

		// Build the form label.
		titleLabel = new Label();

		// Builds the submit button.
		final Button selectButton = new Button(
				I18N.CONSTANTS.formWindowSubmitAction(),IconImageBundle.ICONS.save());
		selectButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				final ArrayList<Object> values = new ArrayList<Object>();

				// Retrieves each value.
				for (final FieldWrapper field : fields) {

					final Object value = field.field.getValue();

					// Checks if null value is allowed for this field.
					if (!field.allowBlank && value == null) {
						MessageBox.alert(I18N.CONSTANTS
								.formWindowFieldsUnfilled(), I18N.CONSTANTS
								.formWindowFieldsUnfilledDetails(), null);
						return;
					}

					values.add(value);
				}

				fireFormSubmitted(values.toArray(new Object[values.size()]));

				// Closes the window.
				window.hide();
			}
		});
		
		final Button deleteButton = new Button(I18N.CONSTANTS.formWindowDeleteAction(),IconImageBundle.ICONS.remove());
		
		deleteButton.addListener(Events.OnClick, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				
				fireDeletion();
				
				//Closes the window
				window.hide();
				
			}
			
		});

		// Builds the fields panel.
		fieldsPanel = new ContentPanel();
		final VBoxLayout fieldsPanelLayout = new VBoxLayout();
		fieldsPanelLayout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		fieldsPanel.setHeaderVisible(false);
		fieldsPanel.setLayout(fieldsPanelLayout);
		fieldsPanel.setBorders(false);
		fieldsPanel.setWidth("100%");

		// Builds the main panel.
		final ContentPanel mainPanel = new ContentPanel();
		final VBoxLayout layout = new VBoxLayout();
		layout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		mainPanel.setHeaderVisible(false);
		mainPanel.setLayout(layout);
		mainPanel.setBorders(true);
		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");

		mainPanel.setTopComponent(null);
		mainPanel.add(titleLabel, new VBoxLayoutData(new Margins(4, 8, 0, 8)));
		mainPanel.add(fieldsPanel, new VBoxLayoutData(new Margins(4, 8, 0, 8)));
		
		//Add the buttons
		final HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setWidth("80%");
		buttonsPanel.setTableWidth("100%");
		buttonsPanel.setBorders(false);
		buttonsPanel.add(deleteButton,new TableData(Style.HorizontalAlignment.CENTER,Style.VerticalAlignment.MIDDLE));
		buttonsPanel.add(selectButton, new TableData(Style.HorizontalAlignment.CENTER,Style.VerticalAlignment.MIDDLE));
		
		mainPanel.add(buttonsPanel,  new VBoxLayoutData(new Margins(8, 8, 0, 8)));


		// Builds window.
		window = new Window();
		window.setWidth(445);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());

		window.add(mainPanel);
	}

	/**
	 * Initialize the window and open it.
	 * 
	 * @param title
	 *            The window title.
	 * @param header
	 *            The heading label of the selection form.
	 */
	public void show(String title, String header) {

		// Lazy building.
		if (window == null) {
			init();
		}

		titleLabel.setText(header);

		// Open the window.
		window.setHeading(title);
		window.setHeight(100 + (FIELD_HEIGHT * (fields.size())));
		window.show();
	}

	/**
	 * Removes all the fields of this window.<br/>
	 * This method removes also the listeners.
	 */
	public void clear() {

		if (window != null) {
			fieldsPanel.removeAll();
			fields.clear();
			listeners.clear();
			window = null;
		}
	}

	/**
	 * Cleans all fields.
	 */
	public void clean() {
		for (final FieldWrapper field : fields) {
			field.field.reset();
		}
	}


	/**
	 * Adds a text field with a default value in the window .
	 * 
	 * @param fieldLabelString
	 *            The label of the text field. Can be <code>null</code>.
	 * @param allowBlank
	 *            If the field is required.
	 * @param defaultValue
	 *            The default value.
	 * 
	 * @return The field.
	 */
	public TextField<String> addTextField(String fieldLabelString,
			String defaultValue, boolean allowBlank) {

		// Lazy building.
		if (window == null) {
			init();
		}

		// Builds the text field.
		final TextField<String> field = new TextField<String>();
		field.setAllowBlank(allowBlank);
		field.setFieldLabel(fieldLabelString);
		field.setValue(defaultValue);

		fields.add(new FieldWrapper(field, allowBlank));

		addField(field, fieldLabelString);

		return field;
	}


	/**
	 * Adds a date field with a default value in the window 
	 * 
	 * @param fieldLabelString
	 *            The label of the date field. Can be <code>null</code>.
	 * @param defaultValue
	 *            The default value
	 * @param allowBlank
	 *            If the field is required.
	 * @return The field.
	 */
	public DateField addDateField(String fieldLabelString, Date defaultValue, boolean allowBlank) {

        // Lazy building.
        if (window == null) {
            init();
        }

        // Builds the text field.
        final DateField field = new DateField();
        field.setAllowBlank(allowBlank);
        field.setFieldLabel(fieldLabelString);
        field.setValue(defaultValue);

        fields.add(new FieldWrapper(field, allowBlank));

        addField(field, fieldLabelString);

        return field;
    }

	  /**
     * Adds a label field in the window.
     * 
     * @param fieldLabelString
     *            The label of the date field. Can be <code>null</code>.
     * @return The field.
     */
    public LabelField addLabelField(String fieldLabelString) {

        // Lazy building.
        if (window == null) {
            init();
        }

        // Builds the text field.
        final LabelField field = new LabelField();
        field.setFieldLabel(fieldLabelString);

        fields.add(new FieldWrapper(field, true));

        addField(field, fieldLabelString);

        return field;
    }

    /**
     * Adds a number field in the window.
     * 
     * @param fieldLabelString
     *            The label of the number field. Can be <code>null</code>.
     * @param allowBlank
     *            If the field is required.
     * @return The field.
     */
    public NumberField addNumberField(String fieldLabelString,Number defaultValue, boolean allowBlank) {

        // Lazy building.
        if (window == null) {
            init();
        }

        // Builds the text field.
        final NumberField field = new NumberField();
        field.setAllowBlank(allowBlank);
        field.setFieldLabel(fieldLabelString);
        field.setValue(defaultValue);

        fields.add(new FieldWrapper(field, allowBlank));

        addField(field, fieldLabelString);

        return field;
    }

	/**
	 * Adds a field in the form.
	 * 
	 * @param field
	 *            The field.
	 * @param fieldLabelString
	 *            The label of the field. Can be <code>null</code>.
	 */
	private void addField(Field<?> field, String fieldLabelString) {

		// Builds the field label.
		final Label fieldLabel = new Label(fieldLabelString);
		fieldLabel.setWidth("165px");
		fieldLabel.addStyleName("flexibility-element-label");

		// Builds the field panel.
		final ContentPanel fieldPanel = new ContentPanel();
		fieldPanel.setBodyBorder(false);
		fieldPanel.setHeaderVisible(false);
		fieldPanel.setLayout(new HBoxLayout());

		fieldPanel.add(fieldLabel, new HBoxLayoutData(new Margins(4, 5, 0, 0)));
		final HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
		flex.setFlex(1);
		fieldPanel.add(field, flex);

		// Adds the field in the panel.
		fieldsPanel.setHeight(FIELD_HEIGHT * fields.size());
		fieldsPanel
				.add(fieldPanel, new VBoxLayoutData(new Margins(4, 0, 0, 0)));
		fieldsPanel.layout();
	}
	
	
	
	/**
	 * Hide the EditFormWindow
	 */
	public void hide()
	{
		window.hide();
	}


}
