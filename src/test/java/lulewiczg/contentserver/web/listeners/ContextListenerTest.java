package lulewiczg.contentserver.web.listeners;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.utils.Constants;

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
        ServletContext context = mock(ServletContext.class);
        when(event.getServletContext()).thenReturn(context);
        when(context.getRealPath(Constants.SEP)).thenReturn(new File(".").getCanonicalPath() + "/target/classes/");
        when(context.getServerInfo()).thenReturn("tomcat");

        listener.contextInitialized(event);
    }

}
