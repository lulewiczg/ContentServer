package com.github.lulewiczg.contentserver.web.listeners;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.web.listeners.ContextListener;

/**
 * Servlet context listener for loading settings on startup.
 *
 * @author lulewiczg
 */
public class ContextListenerTest extends ServletTestTemplate {

    private ContextListener listener = new ContextListener();

    @Test
    @DisplayName("Initialization is passing")
    public void testInit() throws IOException, ServletException {
        ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);
        String path = new File(".").getCanonicalPath() + "/target/classes/";
        when(context.getRealPath(Constants.SEP)).thenReturn(path);
        when(context.getServerInfo()).thenReturn("tomcat");

        listener.contextInitialized(event);
    }

}
