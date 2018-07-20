package lulewiczg.contentserver.web.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import lulewiczg.contentserver.permissions.ResourceHelper;
import lulewiczg.contentserver.utils.Log;

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
        Log.init(event.getServletContext().getRealPath("/"));
        ResourceHelper.init(event.getServletContext());
        Log.getLog().logInfo("Config loaded!");
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Log.getLog().logInfo("Context destroyed!");
    }

}
