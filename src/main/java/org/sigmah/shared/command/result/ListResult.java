package org.sigmah.shared.command.result;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.ClientUtils;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import java.io.Serializable;

/**
 * An action result which returns a list or a size.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <E>
 *          the type of the entities.
 */
public class ListResult<E extends Serializable> implements Result, ListLoadResult<E> {

	/**
	 * The list.
	 */
	private List<E> list;

	/**
	 * The size.
	 */
	private int size;

	public ListResult() {
		// Serialization.
	}

	public ListResult(final List<E> entities) {
		this(entities, entities != null ? entities.size() : 0);
	}

	public ListResult(final int size) {
		this(null, size);
	}

	public ListResult(final List<E> entities, final int size) {
		this.list = entities;
		this.size = size;
	}

	public ListResult(ListResult<E> result) {
		this.list = result != null && result.getList() != null ? new ArrayList<E>(result.getList()) : null;
		this.size = result != null ? result.getSize() : 0;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(final List<E> list) {
		this.list = list;
		this.size = list != null ? list.size() : 0;
	}

	public int getSize() {
		return size;
	}

	public void setSize(final int size) {
		this.size = size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> getData() {
		return getList();
	}

	// --------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Returns if the inner list is {@code null} or empty.
	 * 
	 * @return {@code true} if the inner list is {@code null} or empty, {@code false} otherwise.
	 */
	public boolean isEmpty() {
		return ClientUtils.isEmpty(list);
	}

	/**
	 * Returns if the inner list is <b>not</b> {@code null} or empty.
	 * 
	 * @return {@code true} if the inner list is <b>not</b> {@code null} or empty, {@code false} otherwise.
	 */
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	/**
	 * Returns the given {@code result} inner list data.
	 * 
	 * @param result
	 *          The {@code ListResult} instance (can be {@code null}).
	 * @return the given {@code result} inner list data, or {@code null} if {@code result} is {@code null}.
	 */
	public static <E extends Serializable> List<E> asList(final ListResult<E> result) {
		return result == null ? null : (List<E>) result.getList();
	}

	/**
	 * Returns the given {@code result} inner list data as given {@code clazz} typed list (using an unchecked cast, see
	 * following warning).<br/>
	 * <b>Warning : throws a {@code ClassCastException} if given {@code clazz} type does not correspond to given
	 * {@code result} inner list type.</b>
	 * 
	 * @param result
	 *          The {@code ListResult} instance (can be {@code null}).
	 * @param clazz
	 *          The returned list type.
	 * @return the given {@code result} inner list data as given {@code clazz} typed list (using an unchecked cast), or
	 *         {@code null} if {@code result} is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable, E extends Serializable> List<E> asList(final ListResult<T> result, final Class<E> clazz) {
		return result == null ? null : (List<E>) result.getList();
	}

}
