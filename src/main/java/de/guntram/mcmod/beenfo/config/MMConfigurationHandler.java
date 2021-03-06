package de.guntram.mcmod.beenfo.config;

import de.guntram.mcmod.beenfo.Beenfo;
import de.guntram.mcmod.fabrictools.ConfigurationProvider;
import de.guntram.mcmod.fabrictools.GuiModOptions;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class MMConfigurationHandler implements ModMenuApi
{
    @Override
    public ConfigScreenFactory getModConfigScreenFactory() {
        return screen -> new GuiModOptions(screen, Beenfo.MODNAME, ConfigurationProvider.getHandler(Beenfo.MODNAME));
    }
}
