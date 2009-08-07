package net.i2p.router.web;

import net.i2p.I2PAppContext;
import net.i2p.crypto.TrustedUpdate;
import net.i2p.data.DataHelper;
import net.i2p.router.RouterContext;

public class ConfigUpdateHelper extends HelperBase {
    public ConfigUpdateHelper() {}
    
    public boolean updateAvailable() {
        return true;
    }
    
    public String getNewsURL() {
        return getNewsURL(_context);
    }

    /** hack to replace the old news location with the new one, even if they have saved
        the update page at some point */
    public static String getNewsURL(I2PAppContext ctx) {
        String url = ctx.getProperty(ConfigUpdateHandler.PROP_NEWS_URL);
        if (url != null && !url.equals(ConfigUpdateHandler.OLD_DEFAULT_NEWS_URL))
            return url;
        else
            return ConfigUpdateHandler.DEFAULT_NEWS_URL;
    }
    public String getUpdateURL() {
        String url = _context.getProperty(ConfigUpdateHandler.PROP_UPDATE_URL);
        if (url != null)
            return url.replaceAll(",", "\n");
        else
            return ConfigUpdateHandler.DEFAULT_UPDATE_URL;
    }
    public String getProxyHost() {
        return _context.getProperty(ConfigUpdateHandler.PROP_PROXY_HOST, ConfigUpdateHandler.DEFAULT_PROXY_HOST);
    }
    public String getProxyPort() {
        return _context.getProperty(ConfigUpdateHandler.PROP_PROXY_PORT, ConfigUpdateHandler.DEFAULT_PROXY_PORT);
    }
    
    public String getUpdateThroughProxy() {
        String proxy = _context.getProperty(ConfigUpdateHandler.PROP_SHOULD_PROXY, ConfigUpdateHandler.DEFAULT_SHOULD_PROXY);
        if (Boolean.valueOf(proxy).booleanValue()) 
            return "<input type=\"checkbox\" class=\"optbox\" value=\"true\" name=\"updateThroughProxy\" checked=\"true\" >";
        else
            
            return "<input type=\"checkbox\" class=\"optbox\" value=\"true\" name=\"updateThroughProxy\" >";
    }
    
    private static final long PERIODS[] = new long[] { 12*60*60*1000l, 24*60*60*1000l, 48*60*60*1000l, -1l };
    
    public String getRefreshFrequencySelectBox() {
        String freq = _context.getProperty(ConfigUpdateHandler.PROP_REFRESH_FREQUENCY);
        if (freq == null) freq = ConfigUpdateHandler.DEFAULT_REFRESH_FREQUENCY;
        long ms = -1;
        try { 
            ms = Long.parseLong(freq);
        } catch (NumberFormatException nfe) {}

        StringBuilder buf = new StringBuilder(256);
        buf.append("<select name=\"refreshFrequency\">");
        for (int i = 0; i < PERIODS.length; i++) {
            buf.append("<option value=\"").append(PERIODS[i]);
            if (PERIODS[i] == ms)
                buf.append("\" selected=\"true\"");
            
            if (PERIODS[i] == -1)
                buf.append("\">Never</option>\n");
            else
                buf.append("\">Every ").append(DataHelper.formatDuration(PERIODS[i])).append("</option>\n");
        }
        buf.append("</select>\n");
        return buf.toString();
    }
    
    public String getUpdatePolicySelectBox() {
        String policy = _context.getProperty(ConfigUpdateHandler.PROP_UPDATE_POLICY, ConfigUpdateHandler.DEFAULT_UPDATE_POLICY);
        
        StringBuilder buf = new StringBuilder(256);
        buf.append("<select name=\"updatePolicy\">");
        
        if ("notify".equals(policy))
            buf.append("<option value=\"notify\" selected=\"true\">Notify only</option>");
        else
            buf.append("<option value=\"notify\">Notify only</option>");

        if ("download".equals(policy))
            buf.append("<option value=\"download\" selected=\"true\">Download and verify only</option>");
        else
            buf.append("<option value=\"download\">Download and verify only</option>");
        
        if (System.getProperty("wrapper.version") != null) {
            if ("install".equals(policy))
                buf.append("<option value=\"install\" selected=\"true\">Download, verify, and restart</option>");
            else
                buf.append("<option value=\"install\">Download, verify, and restart</option>");
        }
        
        buf.append("</select>\n");
        return buf.toString();
    }
    
    public String getTrustedKeys() {
        return new TrustedUpdate(_context).getTrustedKeysString();
    }

    public String getNewsStatus() { 
        return NewsFetcher.getInstance(_context).status();
    }

    public String getUpdateVersion() { 
        return NewsFetcher.getInstance(_context).updateVersion();
    }
}
