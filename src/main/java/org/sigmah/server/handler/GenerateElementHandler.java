package org.sigmah.server.handler;

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
