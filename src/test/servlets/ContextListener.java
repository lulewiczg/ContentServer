package test.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import test.permissions.ResourceHelper;
import test.utils.Log;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		ResourceHelper.getInstance(event.getServletContext());
		Log.getLog().log("Config loaded!");
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

}
