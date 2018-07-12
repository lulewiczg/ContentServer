package lulewiczg.web.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import lulewiczg.utils.Log;
import lulewiczg.web.permissions.ResourceHelper;

/**
 * Servlet context listener for loading settings on startup.
 * 
 * @author lulewiczg
 */
public class ContextListener implements ServletContextListener {

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		ResourceHelper.getInstance(event.getServletContext());
		Log.getLog().log("Config loaded!");
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		Log.getLog().log("Context destroyed!");
	}

}
