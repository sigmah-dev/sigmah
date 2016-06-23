package org.sigmah.offline.indexeddb;

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

/**
 * Types of DOMError used by IndexedDB.
 *
 * Comments taken from {@code http://www.w3.org/TR/IndexedDB/#exceptions}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum DOMErrorType {
    /**
     * The operation failed for reasons unrelated to the database itself and not covered by any other errors.
     */
    UnknownError,
    /**
     * A mutation operation in the transaction failed because a constraint was not satisfied.
     * For example, an object such as an object store or index already exists and a request attempted to create a new one.
     */
    ConstraintError,
    /**
     * Data provided to an operation does not meet requirements.
     */
    DataError,
    /**
     * A request was placed against a transaction which is currently not active, or which is finished.
     */
    TransactionInactiveError,
    /**
     * The mutating operation was attempted in a "readonly" transaction.
     */
    ReadOnlyError,
    /**
     * An attempt was made to open a database using a lower version than the existing version.
     */
    VersionError,
    /**
     * The operation failed because the requested database object could not be found. 
     * For example, an object store did not exist but was being opened.
     */
	NotFoundError,
    /**
     * An operation was called on an object on which it is not allowed or at a time when it is not allowed. 
     * Also occurs if a request is made on a source object that has been deleted or removed. 
     * Use TransactionInactiveError or ReadOnlyError when possible, as they are more specific variations of InvalidStateError.
     */
    InvalidStateError,
    /**
     * An invalid operation was performed on an object.
     * For example transaction creation attempt was made, but an empty scope was provided.
     */
    InvalidAccessError,
    /**
     * A request was aborted, for example through a call to IDBTransaction.abort.
     */
    AbortError,
    /**
     * A lock for the transaction could not be obtained in a reasonable time.
     */
    TimeoutError,
    /**
     * The operation failed because there was not enough remaining storage space, or the storage quota was reached and the user declined to give more space to the database.
     */
    QuotaExceededError,
    /**
     * The keypath argument contains an invalid key path.
     */
    SyntaxError,
    /**
     * The data being stored could not be cloned by the internal structured cloning algorithm.
     */
	DataCloneError;
}
