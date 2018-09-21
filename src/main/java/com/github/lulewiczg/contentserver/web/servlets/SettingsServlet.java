package com.github.lulewiczg.contentserver.web.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.Log;
import com.github.lulewiczg.contentserver.utils.SettingsUtil;
import com.github.lulewiczg.contentserver.utils.models.Setting;

/**
 * Servlet for settings.
 *
 * @author lulewiczg
 */
public class SettingsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ServletContext context;

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        context = getServletContext();
    }

    /**
     * Returns current application settings
     *
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(Constants.Setting.PLAIN_TEXT);
        Properties props = SettingsUtil.get(context).getSettingsProperties();
        List<Setting> settings = new ArrayList<>();
        for (Entry<Object, Object> e : props.entrySet()) {
            settings.add(new Setting(e.getKey().toString(), e.getValue()));
        }
        resp.setContentType(Constants.Setting.APPLICATION_JSON);
        Collections.sort(settings);
        resp.getWriter().write(Setting.toJSONArray(settings));
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Setting> settings = Setting.load(req.getParameterMap());
        Properties props = SettingsUtil.get(context).getSettingsProperties();
        for (Setting s : settings) {
            props.setProperty(s.getName(), s.getValue().toString());
            Log.getLog().logInfo(String.format("Changed %s to %s", s.getName(), s.getValue()));
        }
        SettingsUtil.get(context).saveSettings();
    }
}
