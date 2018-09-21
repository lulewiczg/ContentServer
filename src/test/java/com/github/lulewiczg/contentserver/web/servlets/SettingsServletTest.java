package com.github.lulewiczg.contentserver.web.servlets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate;
import com.github.lulewiczg.contentserver.utils.json.JSONModel;
import com.github.lulewiczg.contentserver.utils.models.Setting;
import com.github.lulewiczg.contentserver.web.servlets.SettingsServlet;

/**
 * Tests SettingsServlet.
 * 
 * @author lulewiczg
 */
public class SettingsServletTest extends ServletTestTemplate {

    private SettingsServlet servlet = spy(SettingsServlet.class);
    private List<Setting> settings = Arrays.asList(new Setting(TEST, TEST), new Setting("test.test", TEST),
            new Setting("test.1", "123"), new Setting("test.test.\\test;'1\"23", "!@#$%^&*()_;'[]}{./?><|"));

    /**
     * Sets up tested object.
     * 
     * @see com.github.lulewiczg.contentserver.test.utils.ServletTestTemplate#additionalBefore()
     */
    @Override
    protected void additionalBefore() throws Exception {
        setupServlet(servlet);
    }

    @Test
    @DisplayName("Gets empty settings")
    public void testGetEmptySettings() throws IOException, ServletException {
        Properties props = new Properties();
        when(helper.getSettingsProperties()).thenReturn(props);

        servlet.doGet(request, response);

        verifyOk(JSONModel.EMPTY_ARR);
    }

    @Test
    @DisplayName("Gets settings")
    public void testSettings() throws IOException, ServletException {
        Collections.sort(settings);
        Properties props = new Properties();
        settings.forEach(i -> props.setProperty(i.getName(), i.getValue().toString()));
        when(helper.getSettingsProperties()).thenReturn(props);

        servlet.doGet(request, response);

        String json = JSONModel.toJSONArray(settings);
        verifyOkJSON(json);
    }

    @Test
    @DisplayName("Update empty settings")
    public void testUpdateEmptySettings() throws IOException, ServletException {
        Map<String, String[]> params = settings.stream()
                .collect(Collectors.toMap(Setting::getName, i -> new String[] { i.getValue().toString() }));
        when(request.getParameterMap()).thenReturn(params);
        Properties props = mock(Properties.class);
        when(helper.getSettingsProperties()).thenReturn(props);

        servlet.doPost(request, response);

        params.forEach((i, j) -> verify(props).setProperty(i, j[0]));

        verify(helper).saveSettings();
        verifyOkEmptyResponse();
    }
}
