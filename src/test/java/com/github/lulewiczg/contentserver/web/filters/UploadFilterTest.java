package com.github.lulewiczg.contentserver.web.filters;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;

/**
 * Tests UploadFilter.
 *
 * @author lulewiczg
 */
public class UploadFilterTest extends ServletTestTemplate {

    private UploadFilter filter;

    private FilterChain chain = mock(FilterChain.class);

    /**
     * Sets up tested class.
     *
     * @see com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    protected void additionalBefore() throws ServletException {
        filter = initFilter(() -> new UploadFilter());
    }

    @Test
    @DisplayName("todo")
    public void todo() throws IOException, ServletException {
        fail();
    }
}
