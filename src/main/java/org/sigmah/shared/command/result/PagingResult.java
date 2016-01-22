package org.sigmah.shared.command.result;

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
