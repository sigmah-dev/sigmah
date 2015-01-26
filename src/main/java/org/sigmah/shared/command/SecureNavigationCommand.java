package org.sigmah.shared.command;

import org.sigmah.client.page.Page;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.SecureNavigationResult;

/**
 * <p>
 * Secures the navigation to a given page.
 * </p>
 * <p>
 * This command returns two information:
 * <ul>
 * <li>A boolean flag that grants or refuses the access to the page.</li>
 * <li>An updated instance of {@link org.sigmah.shared.command.result.Authentication Authentication}.</li>
 * </ul>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SecureNavigationCommand extends AbstractCommand<SecureNavigationResult> {

	/**
	 * Page user is trying to access.
	 */
	private Page page;

	/**
	 * Empty constructor necessary for RPC serialization.
	 */
	public SecureNavigationCommand() {
		// Serialization.
	}

	/**
	 * Initializes a new command securing page navigation.
	 * 
	 * @param page
	 *          The page the user is trying to access.
	 */
	public SecureNavigationCommand(final Page page) {
		this.page = page;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("page", page);
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
