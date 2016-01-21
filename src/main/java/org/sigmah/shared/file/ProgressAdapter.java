package org.sigmah.shared.file;

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
 * An abstract adapter class for creating progress listeners.
 * The methods in this class are empty. This class exists as convenience for creating listener objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class ProgressAdapter implements ProgressListener {

    @Override
    public void onProgress(double progress, double speed) {
    }

    @Override
    public void onFailure(Cause cause) {
    }

    @Override
    public void onLoad(String result) {
    }
    
}
