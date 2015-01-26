/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import com.google.inject.Inject;
import javax.persistence.EntityManager;
import org.dozer.Mapper;
import org.sigmah.server.dao.Transactional;
import org.sigmah.shared.command.CopyLogFrame;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.logframe.IndicatorCopyStrategy;
import org.sigmah.shared.domain.logframe.LogFrame;
import org.sigmah.shared.domain.logframe.LogFrameCopyContext;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.exception.CommandException;

/**
 * Handler for the CopyLogFrame command.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CopyLogFrameHandler implements CommandHandler<CopyLogFrame> {

    private EntityManager em;
    private Mapper mapper;

    @Inject
    public CopyLogFrameHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(CopyLogFrame cmd, User user) throws CommandException {
        final Project project = em.find(Project.class, cmd.getDestinationId());
        final LogFrame logFrame = em.find(LogFrame.class, cmd.getSourceId());

        final LogFrame copy = replaceLogFrame(project, logFrame, cmd);
        return mapper.map(copy, LogFrameDTO.class);
    }

    @Transactional
    private LogFrame replaceLogFrame(Project project, LogFrame source, CopyLogFrame cmd) {
        final LogFrame previousLogFrame = project.getLogFrame();
        if(previousLogFrame != null) {
        	em.remove(previousLogFrame);
        }

        final LogFrame copy = source.copy(LogFrameCopyContext
        		.toProject(project)
        		.withStrategy(cmd.getIndicatorCopyStrategy()));
        copy.setParentProject(project);
        project.setLogFrame(copy);

        em.merge(project);

        return project.getLogFrame();
    }
}
