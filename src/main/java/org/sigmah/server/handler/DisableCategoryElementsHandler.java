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
import com.google.inject.persist.Transactional;
import java.util.List;
import org.sigmah.shared.command.DisableCategoryElements;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DisableFlexibleElements;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for the command {@link DisableCategoryElements}.
 *
 * @author Nikita Jibhkate (niksj1996@@gmail.com)
 */
public class DisableCategoryElementsHandler extends AbstractCommandHandler<DisableCategoryElements, VoidResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisableCategoryElementsHandler.class);

    @Override
    protected VoidResult execute(DisableCategoryElements command, UserDispatch.UserExecutionContext context) throws CommandException {
        if (command.getCategoryElements() != null) {
            setDisabled(command.getCategoryElements(), command.isDisable());
        }
        return null;
    }

    @Transactional
    protected void setDisabled(List<CategoryElementDTO> elements, boolean disabled) {
        for (CategoryElementDTO categoryElementDTO : elements) {
            final CategoryElement categoryElement = em().find(CategoryElement.class, categoryElementDTO.getId());
            //flexibleElement.setDisabledDate(disabled ? new Date() : null);
            categoryElement.setisDisabled(disabled);
            em().merge(categoryElement);

            LOGGER.debug("DisableFlexibleElementsHandler flexibleElement {} name {}.", categoryElementDTO.getId(), categoryElementDTO.getLabel());
        }
    }
}
