package com.dnebinger.liferay.vaadin.sample;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinPortletSession;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.portlet.PortletRequest;

/**
 * class UsersUI: Simple UI class to demonstrate Vaadin in the portal.  Highly commented for Liferay developers.
 *
 * The UI extension class is the main entry point for your Vaadin portlet.  It is here that you will create your initial UI.
 *
 * @version 1.0
 * @author dnebinger
 */

// Use the standard Liferay theme for the widgets.
@Theme("liferay")
public class UsersUI extends UI {

	private VerticalLayout mainLayout;

	private LiferayUsersComponent usersComponent;

	/**
	 * init: The extension point, define the initial UI for the interface.
	 * @param vaadinRequest The initial request which is used to initialize the display.
	 */
	@Override
	protected void init(final VaadinRequest vaadinRequest) {
		// create the main vertical layout
		mainLayout = new VerticalLayout();

		// give it a margin and space the internal components.
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		// set the layout as the content area.
		setContent(mainLayout);

		// In the portal, the main widget cannot have an indeterminate height or it just won't render.  There's a number
		// of things going on between the Liferay JS, the Liferay CSS, and the GWT and Vaadin.  There is a bug open on
		// Vaadin, but until it is resolved it's best to come up with a reasonable height and set it.  Note this is just
		// an initial height to get it to render, there are corresponding things that can be done to update the height
		// later on...
		setHeight("600px");
		setWidth("100%");

		// create the users component, the component that has the (hidden) tabs.
		usersComponent = new LiferayUsersComponent(this);

		// add to the page.
		mainLayout.addComponent(usersComponent);

		// switch to the list view for the initial view.
		switchToListView();
	}

	/**
	 * switchToListView: Utility method to switch to the list view.
	 */
	public void switchToListView() {
		usersComponent.switchToListView();
	}

	/**
	 * switchToDetailView: Utility method to switch to the detail view.
	 * @param userId The user id to switch to.
	 */
	public void switchToDetailView(final long userId) {
		usersComponent.switchToDetailView(userId);
	}

	/**
	 * getThemeDisplay: Returns the current ThemeDisplay instance.
	 * @return ThemeDisplay The found instance.
	 */
	public ThemeDisplay getThemeDisplay() {

		ThemeDisplay td = null;

		// okay, the theme display is actually a request attribute, so we just need to get there...

		// start by getting the current portlet request
		PortletRequest portletRequest = VaadinPortletService.getCurrentPortletRequest();

		// get the ThemeDisplay instance from the request attributes
		td = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		// return the instance.
		return td;
	}
}
