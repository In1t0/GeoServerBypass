package me.gv7.woodpecker.plugin;

//import me.gv7.woodpecker.plugin.payload.*;

import me.gv7.woodpecker.plugin.XMLPayloadBypass;

import java.util.ArrayList;
import java.util.List;

public class VulPluginInfo implements IHelperPlugin {
    public static IHelperPluginCallbacks callbacks;
    public static IPluginHelper pluginHelper;


    @Override
    public void HelperPluginMain(IHelperPluginCallbacks iHelperPluginCallbacks) {
        callbacks = iHelperPluginCallbacks;
        pluginHelper = callbacks.getPluginHelper();
        callbacks.setHelperPluginName("GeoServerBypass");
        callbacks.setHelperPluginVersion("0.1.0");
        callbacks.setHelperPluginAutor("iN1t0");
        callbacks.setHelperPluginDescription("GeoServer Bypass WAF");
        List<IHelper> helperList = new ArrayList<IHelper>();
        helperList.add(new XMLPayloadBypass());
        callbacks.registerHelper(helperList);
    }
}