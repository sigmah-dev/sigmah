package org.sigmah.server.handler;

import org.sigmah.server.dao.BaseMapDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetBaseMaps;
import org.sigmah.shared.command.result.BaseMapResult;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;

/**
 * Handler for the {@link GetBaseMaps} command
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetBaseMapsHandler extends AbstractCommandHandler<GetBaseMaps, BaseMapResult> {

	private final BaseMapDAO baseMapDAO;

	@Inject
	public GetBaseMapsHandler(BaseMapDAO baseMapDAO) {
		this.baseMapDAO = baseMapDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BaseMapResult execute(GetBaseMaps cmd, final UserExecutionContext context) throws CommandException {
		return new BaseMapResult(baseMapDAO.getBaseMaps());
	}
}
