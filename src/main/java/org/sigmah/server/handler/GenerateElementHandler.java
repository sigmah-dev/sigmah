package org.sigmah.server.handler;

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

import com.google.inject.Inject;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.report.model.generator.ReportGenerator;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.command.result.Content;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.util.DateRange;

/**
 * @author Alex Bertram
 * @param <C>
 */
public class GenerateElementHandler<C extends Content> extends AbstractCommandHandler<GenerateElement<C>, C> {

    private final ReportGenerator generator;

    @Inject
    public GenerateElementHandler(ReportGenerator generator) {
        this.generator = generator;
    }

	@Override
	protected Content execute(GenerateElement command, UserDispatch.UserExecutionContext context) throws CommandException {
		return generator.generateElement(context.getUser(), command.getElement(), null, new DateRange());
	}
}
