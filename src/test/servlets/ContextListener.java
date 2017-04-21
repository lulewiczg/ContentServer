package test.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import test.permissions.PermissionsResolver;
import test.utils.Log;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		Log.getLog().log("Loading config...");
		PermissionsResolver.getInstance(event.getServletContext());
		Log.getLog().log("Settings OK");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

}
