package com.github.lulewiczg.contentserver.web.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.github.lulewiczg.contentserver.utils.CommonUtil;
import com.github.lulewiczg.contentserver.utils.Log;
import com.github.lulewiczg.contentserver.utils.ResourceUtil;
import com.github.lulewiczg.contentserver.utils.SettingsUtil;

/**
 * Servlet context listener for loading settings on startup.
 *
 * @author lulewiczg
 */
public class ContextListener implements ServletContextListener {

    private static final String TEST_PATH = "WEB-INF/classes/data";

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        ServletContext context = event.getServletContext();
        String path = CommonUtil.getContextPath(context);

        Log.init(path);
        SettingsUtil.init(context);
        ResourceUtil.init(context, path + TEST_PATH);
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
