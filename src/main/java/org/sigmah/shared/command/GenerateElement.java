package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.Content;
import org.sigmah.shared.dto.pivot.model.ReportElement;

/**
 * <p>
 * Generates and returns to the client the content of an element.
 * </p>
 * <p>
 * Returns: {@link org.sigmah.shared.command.result.Content}.
 * </p>
 *
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <T> Type of content to generate.
 */
public class GenerateElement<T extends Content> extends AbstractCommand<T> {

	private ReportElement element;

	protected GenerateElement() {
		// Serialization.
	}

	public GenerateElement(ReportElement element) {
		this.element = element;
	}

	public ReportElement getElement() {
		return element;
	}

	public void setElement(ReportElement element) {
		this.element = element;
	}
}
