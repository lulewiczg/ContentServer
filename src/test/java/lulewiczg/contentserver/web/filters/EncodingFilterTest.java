package lulewiczg.contentserver.web.filters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.test.utils.ServletTestTemplate;
import lulewiczg.contentserver.utils.Constants;

/**
 * Tests EncodingFilter.
 * 
 * @author lulewiczg
 */
public class EncodingFilterTest extends ServletTestTemplate {

    private EncodingFilter filter = new EncodingFilter();

    @Test
    @DisplayName("Setting UTF-8")
    public void testRead() throws IOException, ServletException {
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);

        verify(request).setCharacterEncoding(Constants.Setting.UTF8);
        verify(response).setCharacterEncoding(Constants.Setting.UTF8);
    }
}
