package org.sigmah.client.ui.widget.form;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.sigmah.client.ui.widget.Loadable;

/**
 * Similar to <code>LabelField</code> but able to validate its content.
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class StringField extends Field<String> implements Loadable {

    /**
     * Validates the value.
     */
    private Validator validator;

    /**
     * Indicates if this field is loading.
     * <p>
     * <code>true</code> if the value of this field is pending,
     * <code>false</code> otherwise.
     */
    private boolean loading;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRender(Element parent, int index) {
        setElement(DOM.createDiv(), parent, index);

        if (value != null) {
            setRawValue(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRawValue() {
        return getElement().getInnerText();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setRawValue(String value) {
        if (rendered) {
            getElement().setInnerText(value);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean validateValue(String value) {
        if (!super.validateValue(value)) {
            return false;
        }
        if (validator != null) {
            final String message = validator.validate(this, this.value);
            if (message != null) {
                markInvalid(message);
                return false;
            }
        }
        return true;
    }

    // --
    // GETTERS & SETTERS
    // --
    /**
     * Returns the field's validator instance.
     *
     * @return the validator
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * Sets the validator instance to be called during field validation.
     *
     * @param validator the validator
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoading() {
        return loading;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

}
