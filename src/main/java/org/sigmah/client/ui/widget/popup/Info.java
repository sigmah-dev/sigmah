package org.sigmah.client.ui.widget.popup;

import org.sigmah.client.util.MessageType;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.InfoConfig;

/**
 * Custom implementation of {@link com.extjs.gxt.ui.client.widget.Info} panel.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class Info extends com.extjs.gxt.ui.client.widget.Info {

	private static final int DEFAULT_WIDTH = 350;
	private static final int DEFAULT_DELAY = 2500;

	private final InfoConfig config;

	public Info(String title, String content) {
		this(title, content, DEFAULT_DELAY);
	}

	public Info(String title, String content, int delay) {
		config = new InfoConfig(title, content);
		config.width = DEFAULT_WIDTH;
		config.display = delay > 0 ? delay : DEFAULT_DELAY;
	}

	/**
	 * Displays this info panel.
	 * 
	 * @param type
	 *          The type of the panel.
	 */
	public void show(MessageType type) {
		MessageType.applyStyleName(this, type);
		super.show(config);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point position() {
		final Size s = XDOM.getViewportSize();
		int left = s.width - config.width - 10 + XDOM.getBodyScrollLeft();
		int top = s.height - getHeight() - 10 - (level * (getHeight() + 10)) + XDOM.getBodyScrollTop();
		return new Point(left, top);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, getHeight());
	}

}
