package com.github.lulewiczg.contentserver.web.filters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.web.filters.EncodingFilter;

/**
 * Tests EncodingFilter.
 * 
 * @author lulewiczg
 */
public class EncodingFilterTest extends ServletTestTemplate {

    private EncodingFilter filter;

    /**
     * Sets up tested class.
     * 
     * @see com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    protected void additionalBefore() throws ServletException {
        filter = initFilter(() -> new EncodingFilter());
    }

    @Test
    @DisplayName("Setting UTF-8")
    public void testRead() throws IOException, ServletException {
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);

        verify(request).setCharacterEncoding(Constants.Setting.UTF8);
        verify(response).setCharacterEncoding(Constants.Setting.UTF8);
    }
}
