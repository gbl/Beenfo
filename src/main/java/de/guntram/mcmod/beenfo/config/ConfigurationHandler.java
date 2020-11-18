package de.guntram.mcmod.beenfo.config;

import de.guntram.mcmod.beenfo.Beenfo;
import de.guntram.mcmod.fabrictools.ConfigChangedEvent;
import de.guntram.mcmod.fabrictools.Configuration;
import de.guntram.mcmod.fabrictools.ModConfigurationHandler;
import java.io.File;

public class ConfigurationHandler implements ModConfigurationHandler
{
    private static ConfigurationHandler instance;

    private Configuration config;
    private String configFileName;
    
    private boolean showPopup;
    private int popupXPercent;
    private int popupYPercent;

    public static ConfigurationHandler getInstance() {
        if (instance==null)
            instance=new ConfigurationHandler();
        return instance;
    }
    
    public void load(final File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            configFileName=configFile.getPath();
            loadConfig();
        }
    }
    
    public static String getConfigFileName() {
        return getInstance().configFileName;
    }

    @Override
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Beenfo.MODNAME)) {
            loadConfig();
        }
    }
    
    @Override
    public void onConfigChanging(ConfigChangedEvent.OnConfigChangingEvent event) {
        if (event.getModID().equals(Beenfo.MODNAME)) {
            switch (event.getItem()) {
                case "beenfo.config.showpopup": showPopup=(boolean)(Boolean)(event.getNewValue()); break;
                case "beenfo.config.popupxpercent": popupXPercent=(int)(Integer)(event.getNewValue()); break;
                case "beenfo.config.popupypercent": popupYPercent=(int)(Integer)(event.getNewValue()); break;
            }
        }
    }

    private void loadConfig() {
        showPopup     = config.getBoolean("beenfo.config.showpopup", Configuration.CATEGORY_CLIENT, false, "beenfo.config.tt.showpopup");
        popupXPercent = config.getInt("beenfo.config.popupxpercent", Configuration.CATEGORY_CLIENT, 50, 0, 100, "beenfo.config.tt.popupxpercent");
        popupYPercent = config.getInt("beenfo.config.popupypercent", Configuration.CATEGORY_CLIENT, 50, 0, 100, "beenfo.config.tt.popupypercent");
        if (config.hasChanged())
            config.save();
    }

    @Override
    public Configuration getConfig() {
        return getInstance().config;
    }

    public static boolean getShowPopup() {
        return getInstance().showPopup;
    }
    
    public static int getXPercent() {
        return getInstance().popupXPercent;
    }
    
    public static int getYPercent() {
        return getInstance().popupYPercent;
    }
}
