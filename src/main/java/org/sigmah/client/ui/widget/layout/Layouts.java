package org.sigmah.client.ui.widget.layout;

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

import org.sigmah.client.util.ClientUtils;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;

/**
 * Layout utility class providing utility methods for layouts.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class Layouts {

	/**
	 * Layouts left column width (in pixels).
	 */
	public static final float LEFT_COLUMN_WIDTH = 250f;

	/**
	 * Layouts vertical menu width (in pixels).
	 */
	public static final float VERTICAL_MENU_WIDTH = 160f;

	/**
	 * Layouts south panel height (in pixels).
	 */
	public static final float SOUTH_PANEL_HEIGHT = 140f;

	/**
	 * Layouts banner panel height (in pixels).
	 */
	public static final float BANNER_PANEL_HEIGHT = 130f;

	/**
	 * White background CSS style.
	 */
	public static final String STYLE_WHITE_BACKGROUND = "white-background";

	// --------------------------------------------------------------------------------
	//
	// CONTAINERS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Builds a new container configured with {@link VBoxLayout} (default align and default options).
	 * 
	 * @return The {@link VBoxLayout} container instance.
	 * @see VBoxLayout
	 */
	public static LayoutContainer vBox() {

		return new LayoutContainer(vBoxLayout());
	}

	/**
	 * Builds a new container configured with {@link VBoxLayout} (default options).
	 * 
	 * @param align
	 *          The vertical alignment. If {@code null}, default {@link VBoxLayoutAlign#STRETCH} is set.
	 * @return The {@link VBoxLayout} container instance.
	 * @see VBoxLayout
	 */
	public static LayoutContainer vBox(final VBoxLayoutAlign align) {

		return new LayoutContainer(vBoxLayout(align));
	}

	/**
	 * Builds a new container configured with {@link VBoxLayout} (default align).
	 * 
	 * @param options
	 *          The layout options.
	 * @return The {@link VBoxLayout} container instance.
	 * @see VBoxLayout
	 */
	public static LayoutContainer vBox(final LayoutOptions options) {

		return new LayoutContainer(vBoxLayout(options));
	}

	/**
	 * Builds a new container configured with {@link VBoxLayout}.
	 * 
	 * @param align
	 *          The vertical alignment. If {@code null}, default {@link VBoxLayoutAlign#STRETCH} is set.
	 * @param options
	 *          The layout options.
	 * @return The {@link VBoxLayout} container instance.
	 * @see VBoxLayout
	 */
	public static LayoutContainer vBox(final VBoxLayoutAlign align, final LayoutOptions options) {

		return new LayoutContainer(vBoxLayout(align, options));
	}

	/**
	 * Builds a new container configured with {@link HBoxLayout} (default align and default options).
	 * 
	 * @return The {@link HBoxLayout} container instance.
	 * @see HBoxLayout
	 */
	public static LayoutContainer hBox() {

		return new LayoutContainer(hBoxLayout());
	}

	/**
	 * Builds a new container configured with {@link HBoxLayout} (default options).
	 * 
	 * @param align
	 *          The horizontal alignment. If {@code null}, default {@link HBoxLayoutAlign#STRETCH} is set.
	 * @return The {@link HBoxLayout} container instance.
	 * @see HBoxLayout
	 */
	public static LayoutContainer hBox(final HBoxLayoutAlign align) {

		return new LayoutContainer(hBoxLayout(align));
	}

	/**
	 * Builds a new container configured with {@link HBoxLayout} (default align).
	 * 
	 * @param options
	 *          The layout options.
	 * @return The {@link HBoxLayout} container instance.
	 * @see HBoxLayout
	 */
	public static LayoutContainer hBox(final LayoutOptions options) {

		return new LayoutContainer(hBoxLayout(options));
	}

	/**
	 * Builds a new container configured with {@link HBoxLayout}.
	 * 
	 * @param align
	 *          The horizontal alignment. If {@code null}, default {@link HBoxLayoutAlign#STRETCH} is set.
	 * @param options
	 *          The layout options.
	 * @return The {@link HBoxLayout} container instance.
	 * @see HBoxLayout
	 */
	public static LayoutContainer hBox(final HBoxLayoutAlign align, final LayoutOptions options) {

		return new LayoutContainer(hBoxLayout(align, options));
	}

	/**
	 * Builds a new container configured with {@link BorderLayout} and default transparent background.
	 * 
	 * @param stylenames
	 *          (optional) Style names added to the layout inner {@code target}.
	 * @return The {@link BorderLayout} container instance.
	 * @see BorderLayout
	 */
	public static LayoutContainer border(final String... stylenames) {

		return new LayoutContainer(borderLayout(stylenames));
	}

	/**
	 * Builds a new container configured with {@link BorderLayout}.
	 * 
	 * @param transparent
	 *          {@code true} to set a transparent background, {@code false} to set the default background.
	 * @param stylenames
	 *          (optional) Style names added to the layout inner {@code target}.
	 * @return The {@link BorderLayout} container instance.
	 * @see BorderLayout
	 */
	public static LayoutContainer border(final boolean transparent, final String... stylenames) {

		return new LayoutContainer(borderLayout(transparent, stylenames));
	}

	/**
	 * Builds a new container configured with {@link FitLayout} and default transparent background.
	 * 
	 * @param stylenames
	 *          (optional) Style names added to the layout inner {@code target}.
	 * @return The {@link FitLayout} container instance.
	 * @see FitLayout
	 */
	public static LayoutContainer fit(final String... stylenames) {

		return new LayoutContainer(fitLayout(stylenames));
	}

	/**
	 * Builds a new container configured with {@link FitLayout}.
	 * 
	 * @param transparent
	 *          {@code true} to set a transparent background, {@code false} to set the default background.
	 * @param stylenames
	 *          (optional) Style names added to the layout inner {@code target}.
	 * @return The {@link FitLayout} container instance.
	 * @see FitLayout
	 */
	public static LayoutContainer fit(final boolean transparent, final String... stylenames) {

		return new LayoutContainer(fitLayout(transparent, stylenames));
	}

	// --------------------------------------------------------------------------------
	//
	// LAYOUTS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Layout with transparent background corresponding style name.
	 */
	private static final String LAYOUT_TRANSPARENT_STYLE_NAME = "layout-transparent";

	// --
	// VBOX LAYOUT.
	// --

	/**
	 * Builds a new {@link VBoxLayout} with default {@link VBoxLayoutAlign#STRETCH} and default options.
	 * 
	 * @return The {@link VBoxLayout} instance.
	 * @see VBoxLayout
	 */
	public static VBoxLayout vBoxLayout() {

		return vBoxLayout(null, null);

	}

	/**
	 * Builds a new {@link VBoxLayout} with default options.
	 * 
	 * @param align
	 *          The vertical alignment. If {@code null}, default {@link VBoxLayoutAlign#STRETCH} is set.
	 * @return The {@link VBoxLayout} instance.
	 * @see VBoxLayout
	 */
	public static VBoxLayout vBoxLayout(final VBoxLayoutAlign align) {

		return vBoxLayout(align, null);

	}

	/**
	 * Builds a new {@link VBoxLayout} with default {@link VBoxLayoutAlign#STRETCH}.
	 * 
	 * @param options
	 *          The layout options.
	 * @return The {@link VBoxLayout} instance.
	 * @see VBoxLayout
	 */
	public static VBoxLayout vBoxLayout(final LayoutOptions options) {

		return vBoxLayout(null, options);

	}

	/**
	 * Builds a new {@link VBoxLayout}.
	 * 
	 * @param align
	 *          The vertical alignment. If {@code null}, default {@link VBoxLayoutAlign#STRETCH} is set.
	 * @param options
	 *          The layout options.
	 * @return The {@link VBoxLayout} instance.
	 * @see VBoxLayout
	 */
	public static VBoxLayout vBoxLayout(final VBoxLayoutAlign align, final LayoutOptions options) {

		final VBoxLayout layout = new VBoxLayout(align != null ? align : VBoxLayoutAlign.STRETCH) {

			@Override
			protected void initTarget() {

				super.initTarget();

				if (options == null || options.transparent) {
					target.addStyleName(LAYOUT_TRANSPARENT_STYLE_NAME);
				}

				if (options != null && ClientUtils.isNotEmpty(options.stylenames)) {
					target.addStyleName(options.stylenames);
				}
			}

			@Override
			protected void onLayout(final Container<?> container, final El target) {

				super.onLayout(container, target);

				if (options != null && ClientUtils.isNotEmpty(options.scrollStylenames)) {
					// Scroll style(s) have to be set on 'innerCt' component.
					innerCt.addStyleName(options.scrollStylenames);
				}
			}
		};

		if (options != null && options.padding != null) {
			layout.setPadding(options.padding);
		}

		return layout;
	}

	// --
	// HBOX LAYOUT.
	// --

	/**
	 * Builds a new {@link HBoxLayout} with default {@link HBoxLayoutAlign#STRETCH} and default options.
	 * 
	 * @return The {@link HBoxLayout} instance.
	 * @see HBoxLayout
	 */
	public static HBoxLayout hBoxLayout() {

		return hBoxLayout(null, null);

	}

	/**
	 * Builds a new {@link HBoxLayout} with default options.
	 * 
	 * @param align
	 *          The horizontal alignment. If {@code null}, default {@link HBoxLayoutAlign#STRETCH} is set.
	 * @return The {@link HBoxLayout} instance.
	 * @see HBoxLayout
	 */
	public static HBoxLayout hBoxLayout(final HBoxLayoutAlign align) {

		return hBoxLayout(align, null);

	}

	/**
	 * Builds a new {@link HBoxLayout} with default {@link HBoxLayoutAlign#STRETCH}.
	 * 
	 * @param options
	 *          The layout options.
	 * @return The {@link HBoxLayout} instance.
	 * @see HBoxLayout
	 */
	public static HBoxLayout hBoxLayout(final LayoutOptions options) {

		return hBoxLayout(null, options);

	}

	/**
	 * Builds a new {@link HBoxLayout}.
	 * 
	 * @param align
	 *          The horizontal alignment. If {@code null}, default {@link HBoxLayoutAlign#STRETCH} is set.
	 * @param options
	 *          The layout options.
	 * @see HBoxLayout
	 */
	public static HBoxLayout hBoxLayout(final HBoxLayoutAlign align, final LayoutOptions options) {

		final HBoxLayout layout = new HBoxLayout() {

			@Override
			protected void initTarget() {

				super.initTarget();

				if (options == null || options.transparent) {
					target.addStyleName(LAYOUT_TRANSPARENT_STYLE_NAME);
				}

				if (options != null && ClientUtils.isNotEmpty(options.stylenames)) {
					innerCt.addStyleName(options.stylenames);
				}
			}

			@Override
			protected void onLayout(final Container<?> container, final El target) {

				super.onLayout(container, target);

				if (options != null && ClientUtils.isNotEmpty(options.scrollStylenames)) {
					// Scroll style(s) have to be set on 'innerCt' component.
					innerCt.addStyleName(options.scrollStylenames);
				}
			}
		};

		layout.setHBoxLayoutAlign(align != null ? align : HBoxLayoutAlign.STRETCH);

		if (options != null && options.padding != null) {
			layout.setPadding(options.padding);
		}

		return layout;
	}

	// --
	// BORDER LAYOUT.
	// --

	/**
	 * Builds a new {@link BorderLayout} with default transparent background.
	 * 
	 * @param stylenames
	 *          (optional) Style names added to the layout inner {@code target}.
	 * @return The {@link BorderLayout} instance.
	 * @see BorderLayout
	 */
	public static BorderLayout borderLayout(final String... stylenames) {

		return borderLayout(true, stylenames);
	}

	/**
	 * Builds a new {@link BorderLayout}.
	 * 
	 * @param transparent
	 *          {@code true} to set a transparent background, {@code false} to set the default background.
	 * @param stylenames
	 *          (optional) Style names added to the layout inner {@code target}.
	 * @return The {@link BorderLayout} instance.
	 * @see BorderLayout
	 */
	public static BorderLayout borderLayout(final boolean transparent, final String... stylenames) {

		return new BorderLayout() {

			@Override
			protected void initTarget() {

				super.initTarget();

				if (transparent) {
					target.addStyleName(LAYOUT_TRANSPARENT_STYLE_NAME);
				}

				if (ClientUtils.isNotEmpty(stylenames)) {
					target.addStyleName(stylenames);
				}
			}
		};
	}

	// --
	// FIT LAYOUT.
	// --

	/**
	 * Builds a new {@link FitLayout} with default transparent background.
	 * 
	 * @param stylenames
	 *          (optional) Style names added to the layout inner {@code target}.
	 * @return The {@link FitLayout} instance.
	 * @see FitLayout
	 */
	public static FitLayout fitLayout(final String... stylenames) {

		return fitLayout(true, stylenames);
	}

	/**
	 * Builds a new {@link FitLayout}.
	 * 
	 * @param transparent
	 *          {@code true} to set a transparent background, {@code false} to set the default background.
	 * @param stylenames
	 *          (optional) Style names added to the layout inner {@code target}.
	 * @return The {@link FitLayout} instance.
	 * @see FitLayout
	 */
	public static FitLayout fitLayout(final boolean transparent, final String... stylenames) {

		return new FitLayout() {

			@Override
			protected void initTarget() {

				super.initTarget();

				if (transparent) {
					target.addStyleName(LAYOUT_TRANSPARENT_STYLE_NAME);
				}

				if (ClientUtils.isNotEmpty(stylenames)) {
					target.addStyleName(stylenames);
				}
			}
		};
	}

	// --------------------------------------------------------------------------------
	//
	// LAYOUT DATA.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Default (H/V)BoxLayoutData default flex.
	 */
	private static final double DEFAULT_FLEX = 1.0;

	// --
	// FitData.
	// --

	/**
	 * Builds a new {@link FitData} with no margins.
	 * 
	 * @param margins
	 *          The optional margin(s).
	 * @return The {@link FitData} instance.
	 */
	public static FitData fitData(final Margin... margins) {

		final FitData data = new FitData();

		data.setMargins(Margin.toMargins(margins));

		return data;
	}

	// --
	// VBoxLayoutData.
	// --

	/**
	 * Builds a new {@link VBoxLayoutData} with default {@code 1.0} flex value and no margins.
	 * 
	 * @return The {@link VBoxLayoutData} instance.
	 */
	public static VBoxLayoutData vBoxData() {
		return vBoxData(null, (Margin[]) null);
	}

	/**
	 * Builds a new {@link VBoxLayoutData}.
	 * 
	 * @param flex
	 *          The flex value. If {@code null}, a default {@code 1.0} flex is set.
	 * @return The {@link VBoxLayoutData} instance.
	 */
	public static VBoxLayoutData vBoxData(final Double flex) {
		return vBoxData(flex, (Margin[]) null);
	}

	/**
	 * Builds a new {@link VBoxLayoutData} with default {@code 1.0} flex value.
	 * 
	 * @param margins
	 *          The margin(s).
	 * @return The {@link VBoxLayoutData} instance.
	 */
	public static VBoxLayoutData vBoxData(final Margin... margins) {
		return vBoxData(null, margins);
	}

	/**
	 * Builds a new {@link VBoxLayoutData}.
	 * 
	 * @param flex
	 *          The flex value. If {@code null}, a default {@code 1.0} flex is set.
	 * @param margins
	 *          The margin(s).
	 * @return The {@link VBoxLayoutData} instance.
	 */
	public static VBoxLayoutData vBoxData(final Double flex, final Margin... margins) {

		final VBoxLayoutData data = new VBoxLayoutData();

		data.setFlex(flex != null ? flex : DEFAULT_FLEX);
		data.setMargins(Margin.toMargins(margins));

		return data;
	}

	// --
	// HBoxLayoutData.
	// --

	/**
	 * Builds a new {@link HBoxLayoutData} with default {@code 1.0} flex value and no margins.
	 * 
	 * @return The {@link HBoxLayoutData} instance.
	 */
	public static HBoxLayoutData hBoxData() {
		return hBoxData(null, (Margin[]) null);
	}

	/**
	 * Builds a new {@link HBoxLayoutData}.
	 * 
	 * @param flex
	 *          The flex value. If {@code null}, a default {@code 1.0} flex is set.
	 * @return The {@link HBoxLayoutData} instance.
	 */
	public static HBoxLayoutData hBoxData(final Double flex) {
		return hBoxData(flex, (Margin[]) null);
	}

	/**
	 * Builds a new {@link HBoxLayoutData} with default {@code 1.0} flex.
	 * 
	 * @param margins
	 *          The margin(s).
	 * @return The {@link HBoxLayoutData} instance.
	 */
	public static HBoxLayoutData hBoxData(final Margin... margins) {
		return hBoxData(null, margins);
	}

	/**
	 * Builds a new {@link HBoxLayoutData}.
	 * 
	 * @param flex
	 *          The flex value. If {@code null}, a default {@code 1.0} flex is set.
	 * @param margins
	 *          The margin(s).
	 * @return The {@link HBoxLayoutData} instance.
	 */
	public static HBoxLayoutData hBoxData(final Double flex, final Margin... margins) {

		final HBoxLayoutData data = new HBoxLayoutData();

		data.setFlex(flex != null ? flex : DEFAULT_FLEX);
		data.setMargins(Margin.toMargins(margins));

		return data;
	}

	// --
	// BorderLayoutData.
	// --

	/**
	 * Builds a new {@link BorderLayoutData}.
	 * 
	 * @param region
	 *          The border layout region. If {@code null}, default <em>center</em> region is set.
	 * @return The {@link BorderLayoutData} instance.
	 */
	public static BorderLayoutData borderLayoutData(final LayoutRegion region) {
		return borderLayoutData(region, null, (Margin[]) null);
	}

	/**
	 * Builds a new {@link BorderLayoutData}.
	 * 
	 * @param region
	 *          The border layout region. If {@code null}, default <em>center</em> region is set.
	 * @param size
	 *          The data size in pixels (width or height, depending on the region).
	 *          Values of 1 or less are treated as percentages (defaults to 200).
	 * @return The {@link BorderLayoutData} instance.
	 */
	public static BorderLayoutData borderLayoutData(final LayoutRegion region, final Float size) {
		return borderLayoutData(region, size, (Margin[]) null);
	}

	/**
	 * Builds a new {@link BorderLayoutData}.
	 * 
	 * @param region
	 *          The border layout region. If {@code null}, default <em>center</em> region is set.
	 * @param margins
	 *          The margin(s).
	 * @return The {@link BorderLayoutData} instance.
	 */
	public static BorderLayoutData borderLayoutData(final LayoutRegion region, final Margin... margins) {
		return borderLayoutData(region, null, margins);
	}

	/**
	 * Builds a new {@link BorderLayoutData}.
	 * 
	 * @param region
	 *          The border layout region. If {@code null}, default <em>center</em> region is set.
	 * @param size
	 *          The data size in pixels (width or height, depending on the region).
	 *          Values of 1 or less are treated as percentages (defaults to 200).
	 * @param margins
	 *          The margin(s).
	 * @return The {@link BorderLayoutData} instance.
	 */
	public static BorderLayoutData borderLayoutData(final LayoutRegion region, final Float size, final Margin... margins) {
		return borderLayoutData(region, size, false, margins);
	}

	/**
	 * Builds a new {@link BorderLayoutData}.
	 * 
	 * @param region
	 *          The border layout region. If {@code null}, default <em>center</em> region is set.
	 * @param size
	 *          The data size in pixels (width or height, depending on the region).
	 *          Values of 1 or less are treated as percentages (defaults to 200).
	 * @param collapsible
	 *          If the layout data is collapsible.
	 * @param margins
	 *          The margin(s).
	 * @return The {@link BorderLayoutData} instance.
	 */
	public static BorderLayoutData borderLayoutData(final LayoutRegion region, final Float size, final boolean collapsible, final Margin... margins) {

		final BorderLayoutData data = new BorderLayoutData(region != null ? region : LayoutRegion.CENTER);

		if (size != null) {
			data.setSize(size);
		}
		data.setMargins(Margin.toMargins(margins));
		data.setCollapsible(collapsible);

		return data;
	}

	// --------------------------------------------------------------------------------
	//
	// MARGINS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Default margin separating two components in a view (in pixels).
	 */
	private static final int DEFAULT_MARGIN = 8;

	/**
	 * Margins values allowing layouts to specify which margin(s) should be enabled.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum Margin {

		/**
		 * Adds a top margin.
		 */
		TOP,

		/**
		 * Adds a right margin.
		 */
		RIGHT,

		/**
		 * Adds a bottom margin.
		 */
		BOTTOM,

		/**
		 * Adds a left margin.
		 */
		LEFT,

		/**
		 * Adds a <b>half</b> top margin.
		 */
		HALF_TOP,

		/**
		 * Adds a <b>half</b> right margin.
		 */
		HALF_RIGHT,

		/**
		 * Adds a <b>half</b> bottom margin.
		 */
		HALF_BOTTOM,

		/**
		 * Adds a <b>half</b> left margin.
		 */
		HALF_LEFT,

		/**
		 * Adds a <b>double</b> top margin.
		 */
		DOUBLE_TOP,

		/**
		 * Adds a <b>double</b> right margin.
		 */
		DOUBLE_RIGHT,

		/**
		 * Adds a <b>double</b> bottom margin.
		 */
		DOUBLE_BOTTOM,

		/**
		 * Adds a <b>double</b> left margin.
		 */
		DOUBLE_LEFT;

		/**
		 * Merges the given {@code margins} instance(s) into a new {@link com.extjs.gxt.ui.client.util.Margins Margins}.
		 * 
		 * @param margins
		 *          The {@link Margin} instance(s) (may be {@code null}).
		 *          {@code null} values are ignored.
		 * @return The merged {@link com.extjs.gxt.ui.client.util.Margins Margins} result, never {@code null}.
		 * @see com.extjs.gxt.ui.client.util.Margins
		 */
		private static Margins toMargins(final Margin... margins) {

			final Margins result = new Margins(0);

			if (margins == null) {
				return result;
			}

			for (final Margin margin : margins) {

				if (margin == null) {
					continue;
				}

				switch (margin) {

					case TOP:
						result.top += DEFAULT_MARGIN;
						break;

					case RIGHT:
						result.right += DEFAULT_MARGIN;
						break;

					case BOTTOM:
						result.bottom += DEFAULT_MARGIN;
						break;

					case LEFT:
						result.left += DEFAULT_MARGIN;
						break;

					case HALF_TOP:
						result.top += DEFAULT_MARGIN / 2;
						break;

					case HALF_RIGHT:
						result.right += DEFAULT_MARGIN / 2;
						break;

					case HALF_BOTTOM:
						result.bottom += DEFAULT_MARGIN / 2;
						break;

					case HALF_LEFT:
						result.left += DEFAULT_MARGIN / 2;
						break;

					case DOUBLE_TOP:
						result.top += DEFAULT_MARGIN * 2;
						break;

					case DOUBLE_RIGHT:
						result.right += DEFAULT_MARGIN * 2;
						break;

					case DOUBLE_BOTTOM:
						result.bottom += DEFAULT_MARGIN * 2;
						break;

					case DOUBLE_LEFT:
						result.left += DEFAULT_MARGIN * 2;
						break;
				}
			}

			return result;
		}
	}

	/**
	 * <p>
	 * Layout options.
	 * </p>
	 * <p>
	 * Default options are:
	 * <ul>
	 * <li>{@code transparent} set to {@code true}.</li>
	 * <li>{@code padding} set to {@code null}.</li>
	 * <li>{@code scroll} set to {@code null}.</li>
	 * <li>{@code stylenames} set to {@code null}.</li>
	 * </ul>
	 * </p>
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static final class LayoutOptions {

		/**
		 * Scroll bar size (in pixels).
		 */
		private static final int SCROLL_BAR_SIZE = 20;

		private static final String STYLE_VERTICAL_SCROLL = "layout-with-v-scroll";
		private static final String STYLE_HORIZONTAL_SCROLL = "layout-with-h-scroll";

		/**
		 * Scroll configuration.
		 * 
		 * @author Denis Colliot (dcolliot@ideia.fr)
		 */
		public static enum Scroll {

			/**
			 * Automatically sets a vertical scroll bar and the necessary styles/paddings.
			 */
			VERTICAL,

			/**
			 * Automatically sets an horizontal scroll bar and the necessary styles/paddings.
			 */
			HORIZONTAL,

			/**
			 * Automatically sets a vertical <b>and</b> an horizontal scroll bar and the necessary styles/paddings.
			 */
			BOTH;
		}

		private final Padding padding;
		private final boolean transparent;
		private final String[] stylenames;
		private final String[] scrollStylenames;

		// --
		// Transparent.
		// --

		/**
		 * Builds a new {@link LayoutOptions}.
		 * 
		 * @param transparent
		 *          {@code true} to make the layout background transparent. If {@code null}, set to {@code true}.
		 * @param stylenames
		 *          (optional) Style names added to the layout inner {@code target}.
		 */
		public LayoutOptions(final Boolean transparent, final String... stylenames) {
			this((Padding) null, transparent, null, stylenames);
		}

		// --
		// Scroll.
		// --

		/**
		 * Builds a new {@link LayoutOptions}.
		 * 
		 * @param scroll
		 *          The scroll configuration. If {@code null}, no scroll configuration is set.
		 * @param stylenames
		 *          (optional) Style names added to the layout inner {@code target}.
		 */
		public LayoutOptions(final Scroll scroll, final String... stylenames) {
			this((Padding) null, null, scroll, stylenames);
		}

		// --
		// Padding.
		// --

		/**
		 * Builds a new {@link LayoutOptions}.
		 * 
		 * @param padding
		 *          The layout padding. If {@code null}, no padding is set.
		 * @param stylenames
		 *          (optional) Style names added to the layout inner {@code target}.
		 */
		public LayoutOptions(final Padding padding, final String... stylenames) {
			this(padding, null, null, stylenames);
		}

		// --
		// Padding + transparent.
		// --

		/**
		 * Builds a new {@link LayoutOptions}.
		 * 
		 * @param padding
		 *          The layout padding. If {@code null}, no padding is set.
		 * @param transparent
		 *          {@code true} to make the layout background transparent. If {@code null}, set to {@code true}.
		 * @param stylenames
		 *          (optional) Style names added to the layout inner {@code target}.
		 */
		public LayoutOptions(final Padding padding, final Boolean transparent, final String... stylenames) {
			this(padding, transparent, null, stylenames);
		}

		// --
		// Padding + scroll.
		// --

		/**
		 * Builds a new {@link LayoutOptions}.
		 * 
		 * @param padding
		 *          The layout padding. If {@code null}, no padding is set.
		 * @param scroll
		 *          The scroll configuration. If {@code null}, no scroll configuration is set.
		 * @param stylenames
		 *          (optional) Style names added to the layout inner {@code target}.
		 */
		public LayoutOptions(final Padding padding, final Scroll scroll, final String... stylenames) {
			this(padding, null, scroll, stylenames);
		}

		// --
		// Full.
		// --

		/**
		 * Builds a new {@link LayoutOptions}.
		 * 
		 * @param padding
		 *          The layout padding. If {@code null}, no padding is set.
		 * @param transparent
		 *          {@code true} to make the layout background transparent. If {@code null}, set to {@code true}.
		 * @param scroll
		 *          The scroll configuration. If {@code null}, no scroll configuration is set.
		 * @param stylenames
		 *          (optional) Style names added to the layout inner {@code target}.
		 */
		public LayoutOptions(Padding padding, final Boolean transparent, final Scroll scroll, final String... stylenames) {
			this.padding = padding;
			this.transparent = transparent != null ? transparent.booleanValue() : true;
			this.stylenames = stylenames;
			this.scrollStylenames = scroll != null ? new String[2] : null;

			if (scroll == null) {
				return;
			}

			switch (scroll) {

				case VERTICAL:
					if (padding != null) {
						padding.right += SCROLL_BAR_SIZE;
					} else {
						padding = new Padding(0, SCROLL_BAR_SIZE, 0, 0);
					}

					this.scrollStylenames[0] = STYLE_VERTICAL_SCROLL;
					break;

				case HORIZONTAL:
					if (padding != null) {
						padding.bottom += SCROLL_BAR_SIZE;
					} else {
						padding = new Padding(0, 0, SCROLL_BAR_SIZE, 0);
					}

					this.scrollStylenames[0] = STYLE_HORIZONTAL_SCROLL;
					break;

				case BOTH:
					if (padding != null) {
						padding.right += SCROLL_BAR_SIZE;
						padding.bottom += SCROLL_BAR_SIZE;
					} else {
						padding = new Padding(0, SCROLL_BAR_SIZE, SCROLL_BAR_SIZE, 0);
					}

					this.scrollStylenames[0] = STYLE_VERTICAL_SCROLL;
					this.scrollStylenames[1] = STYLE_HORIZONTAL_SCROLL;
					break;

				default:
					break;
			}
		}
	}

	/**
	 * Private constructor.
	 */
	private Layouts() {
		// Factory pattern.
	}

}
