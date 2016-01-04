package org.sigmah.shared.command.base;

import org.sigmah.shared.command.result.Result;

import java.io.Serializable;

/**
 * <p>
 * Command interface.
 * </p>
 * <p>
 * All command implementations should implements this interface and declare an empty constructor.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <R>
 *          The command result type.
 */
public interface Command<R extends Result> extends Serializable {

}
