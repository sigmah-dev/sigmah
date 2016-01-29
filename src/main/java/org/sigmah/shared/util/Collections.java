package org.sigmah.shared.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    public static <T> boolean containsOneOf(final Set<T> haystack, final Collection<T> needles) {
        
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
        
        final int builderLength = builder.length();
        if (builderLength > 0) {
            builder.setLength(builderLength - separator.length());
        }
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
     * Simple filter interface.
     * 
     * @param <S> Source type.
     * @param <D> Destination type.
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
    
}
