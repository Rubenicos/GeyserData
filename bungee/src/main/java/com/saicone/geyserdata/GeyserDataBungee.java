package com.saicone.geyserdata;

import com.saicone.geyserdata.connection.BungeeConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class GeyserDataBungee extends Plugin implements Listener {

    public static final String CHANNEL = "geyserdata:main";

    private static GeyserDataBungee instance;

    public static GeyserDataBungee get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getProxy().registerChannel(CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public void onDisable() {
        getProxy().unregisterChannel(CHANNEL);
    }

    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        if (event.isCancelled() || !CHANNEL.equals(event.getTag())) {
            return;
        }
        event.setCancelled(true);

        if (event.getSender() instanceof ProxiedPlayer) {
            return;
        }

        BungeeConnection connection = new BungeeConnection((ProxiedPlayer) event.getReceiver(), event.getData());
        getProxy().getScheduler().runAsync(this, connection::process);
    }
}
