package org.sigmah.shared.util;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for <code>Collection</code> types.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Collections {
    
    /**
     * Private constructor.
     */
    private Collections() {
        // Nothing.
    }
    
    /**
     * Find if the given <code>haystack</code> of elements contains one the given <code>needles</code>.
     * 
     * @param <T>
     *          Type of the elements contained in the collection.
     * @param haystack
     *          Set of elements to search in.
     * @param needles
     *          Collection of elements to find.
     * @return <code>true</code> if one of the <code>needles</code> is found in <code>haystack</code>,
     * <code>false</code> if none are found.
     */
    public static <T> boolean containsOneOf(final Collection<T> haystack, final Collection<T> needles) {
        
        for (final T needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Join the elements of the collection with the given separator.
     * 
     * @param <T>
     *          Type of the elements contained in the collection.
     * @param collection
     *          Collection of elements.
     * @param separator
     *          Separator to insert between each element.
     * @return A new string joining every elements in the collection with the given separator.
     */
    public static <T> String join(final Collection<T> collection, final String separator) {
		
        return join(collection, new Mapper<T, String>() {
            
            @Override
            public String forEntry(T s) {
                return String.valueOf(s);
            }
        }, separator);
    }
    
    /**
     * Join the elements of the collection with the given separator.
     * 
     * @param <T>
     *          Type of the elements contained in the collection.
     * @param collection
     *          Collection of elements.
     * @param mapper
     *          Transform the content of the collection to <code>String</code>.
     * @param separator
     *          Separator to insert between each element.
     * @return A new string joining every elements in the collection with the given separator.
     */
    public static <T> String join(final Collection<T> collection, final Mapper<T, String> mapper, final String separator) {
		
        final StringBuilder builder = new StringBuilder();
        
		for (final T entry : collection) {
			builder.append(mapper.forEntry(entry)).append(separator);
		}

		removeLastSeparator(builder, separator);
        return builder.toString();
    }

    /**
     * Join the elements of the collection with the given separator.
     * 
     * @param <T>
     *          Type of the elements contained in the collection.
     * @param collection
     *          Collection of elements.
     * @param mapper
     *          Transform the content of the collection to <code>String</code>.
     * @param separator
     *          Separator to insert between each element.
     * @return A new string joining every elements in the collection with the given separator.
     */
    public static <T> String join(final Collection<T> collection, final OptionnalMapper<T, String> mapper, final String separator) {
		
        final StringBuilder builder = new StringBuilder();
        
		for (final T entry : collection) {
			if (!mapper.skipEntry(entry)) {
				builder.append(mapper.forEntry(entry)).append(separator);
			}
		}

		removeLastSeparator(builder, separator);
        return builder.toString();
    }
    
    /**
     * Creates a list by mapping the elements from the given collection.
     * 
     * @param <S>
     *          Source type.
     * @param <D>
     *          Destination type.
     * @param collection
     *          Collection to map.
     * @param mapper
     *          Mapper from S to D.
     * @return A new list.
     */
    public static <S, D> List<D> map(final Collection<S> collection, final Mapper<S, D> mapper) {
		
        final ArrayList<D> list = new ArrayList<D>();
        
        for (final S entry : collection) {
            list.add(mapper.forEntry(entry));
        }
        
        return list;
    }
    
    /**
     * Creates a list by mapping the elements from the given collection.
	 * <p>
	 * The elements identified as skippable from the given mapper will not be
	 * included in the returned list.
     * 
     * @param <S>
     *          Source type.
     * @param <D>
     *          Destination type.
     * @param collection
     *          Collection to map.
     * @param mapper
     *          Mapper from S to D.
     * @return A new list.
     */
    public static <S, D> List<D> map(final Collection<S> collection, final OptionnalMapper<S, D> mapper) {
		
        final ArrayList<D> list = new ArrayList<D>();
        
        for (final S entry : collection) {
			if (!mapper.skipEntry(entry)) {
				list.add(mapper.forEntry(entry));
			}
        }
        
        return list;
    }
	
	/**
	 * Removes the last separator in the given <code>StringBuilder</code>.
	 * 
	 * @param builder
	 *          StringBuilder to modify.
	 * @param separator 
	 *          Separator to remove.
	 */
	private static void removeLastSeparator(final StringBuilder builder, final String separator) {
		
		final int builderLength = builder.length();
		if (builderLength > 0) {
			builder.setLength(builderLength - separator.length());
		}
	}
    
    /**
     * Simple mapper interface.
     * 
     * @param <S>
     *          Source type.
     * @param <D>
     *          Destination type.
     */
    public interface Mapper<S, D> {
        
        /**
         * Map the given element to the required type.
         * 
         * @param entry
         *          Element to map.
         * @return The element converted.
         */
        D forEntry(S entry);
		
    }
	
	/**
	 * Mapper interface for collections with optional elements.
	 * 
	 * @param <S>
     *          Source type.
     * @param <D>
     *          Destination type.
	 */
	public interface OptionnalMapper<S, D> extends Mapper<S, D> {
		
		/**
		 * Decide if the given entry should be mapped or not.
		 * 
		 * @param entry
		 *          Entry to verify.
		 * @return <code>true</code> to skip the given entry, 
		 * <code>false</code> otherwise.
		 */
		boolean skipEntry(S entry);
		
	}
    
}
