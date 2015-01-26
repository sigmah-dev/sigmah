package org.sigmah.shared.dto;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;

/**
 * 
 * Fixes {@link BaseTreeModel} so that it can be used with deRPC.
 * DeRPC does not call the class constructor, so subsequent calls
 * to BaseTreeModel methods that assume {@code changeEventSupport}
 * has been initialized will fail.
 *
 * 
 * @author alexander
 *
 */
public class DeRpcSafeBaseTreeModel extends BaseTreeModel {

	protected ChangeEventSupport getChangeEventSupport() {
		if(changeEventSupport == null) {
			changeEventSupport = new ChangeEventSupport();
		}
		return changeEventSupport;
	}

	@Override
	public void addChangeListener(ChangeListener... listener) {

		getChangeEventSupport().addChangeListener(listener);
	}

	@Override
	public void addChangeListener(List<ChangeListener> listeners) {
		for (ChangeListener listener : listeners) {
			getChangeEventSupport().addChangeListener(listener);
		}
	}

	@Override
	public boolean isSilent() {
		return getChangeEventSupport().isSilent();
	}

	@Override
	public void notify(ChangeEvent evt) {
		getChangeEventSupport().notify(evt);
	}

	@Override
	public void removeChangeListener(ChangeListener... listener) {
		getChangeEventSupport().removeChangeListener(listener);
	}

	public void removeChangeListeners() {
		getChangeEventSupport().removeChangeListeners();
	}


	public void setSilent(boolean silent) {
		getChangeEventSupport().setSilent(silent);
	}
}
