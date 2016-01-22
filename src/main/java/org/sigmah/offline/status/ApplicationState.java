package org.sigmah.offline.status;

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
 * Possibles connection states.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum ApplicationState {
    /**
     * Sigmah is checking the current state of the application.
     */
    UNKNOWN,
    /**
     * No connection has been found. User is most likely offline.
     */
    OFFLINE,
    /**
     * User is online but has uncommited changes. A synchronization is required
     * to return back online.
     */
    READY_TO_SYNCHRONIZE,
    /**
     * User if online.
     */
    ONLINE;
}
