package lulewiczg.web.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lulewiczg.permissions.ResourceHelper;
import lulewiczg.utils.Constants;
import lulewiczg.utils.Log;
import lulewiczg.utils.models.Setting;

/**
 * Servlet for settings.
 *
 * @author lulewiczg
 */
public class SettingsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Returns current application settings
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(Constants.Setting.UTF8);
        Properties props = ResourceHelper.getInstance().getSettingsProperties();
        List<Setting> settings = new ArrayList<>();
        for (Entry<Object, Object> e : props.entrySet()) {
            settings.add(new Setting(e.getKey().toString(), e.getValue()));
        }
        resp.setContentType(Constants.Setting.APPLICATION_JSON);
        Collections.sort(settings);
        resp.getWriter().write(Setting.toJSON(settings));
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        Properties props = ResourceHelper.getInstance().getSettingsProperties();
        for (Entry<String, String[]> e : parameterMap.entrySet()) {
            props.setProperty(e.getKey(), e.getValue()[0]);
            Log.getLog().log(String.format("Changed %s to %s", e.getKey(), e.getValue()[0]));
        }
        ResourceHelper.getInstance().saveSettings();
    }
}
