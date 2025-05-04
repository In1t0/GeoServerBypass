package me.gv7.woodpecker.plugin;
import me.gv7.woodpecker.plugin.XMLPayloadBypass;

public class WoodpeckerPluginManager implements IPluginManager {
    public void registerPluginManagerCallbacks(IPluginManagerCallbacks iPluginManagerCallbacks) {
        VulPluginInfo vulPluginInfo = new VulPluginInfo();
        iPluginManagerCallbacks.registerHelperPlugin(vulPluginInfo);
    }
}