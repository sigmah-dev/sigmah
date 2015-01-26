package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.base.AbstractModelDataDTO;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * Abstract base class for <code>CommandResult</code>s that are compatible with the GXT loading framework.
 *
 * @param <D>
 *          The type of model contained in the list
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see com.extjs.gxt.ui.client.data.PagingLoadResult
 */
public abstract class PagingResult<D extends AbstractModelDataDTO> extends ListResult<D> implements Result, PagingLoadResult<D> {

	private int offset;

	protected PagingResult() {
		// Serialization.
	}

	public PagingResult(List<D> data) {
		this(data, 0, data.size());
	}

	public PagingResult(List<D> data, int offset, int totalCount) {
		super(data, totalCount);
		setOffset(offset);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOffset() {
		return offset;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTotalLength() {
		return getSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTotalLength(int totalLength) {
		setSize(totalLength);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<D> getData() {
		return getList();
	}

}
