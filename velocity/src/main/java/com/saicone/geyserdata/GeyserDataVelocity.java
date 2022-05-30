package com.saicone.geyserdata;

import com.google.inject.Inject;
import com.saicone.geyserdata.connection.VelocityConnection;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "GeyserData", name = "Geyser Data Channel", version = "${version}", authors = "Rubenicos", dependencies = {@Dependency(id = "Geyser-Velocity")})
public class GeyserDataVelocity {

    public static final ChannelIdentifier CHANNEL = new LegacyChannelIdentifier("geyserdata:main");

    private static GeyserDataVelocity instance;

    public static GeyserDataVelocity get() {
        return instance;
    }

    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public GeyserDataVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.proxy = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        getProxy().getChannelRegistrar().register(CHANNEL);
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        getProxy().getChannelRegistrar().unregister(CHANNEL);
    }

    @Subscribe
    public void onMessage(PluginMessageEvent event) {
        if (!CHANNEL.getId().equals(event.getIdentifier().getId())) {
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        if (event.getSource() instanceof Player) {
            return;
        }

        VelocityConnection connection = new VelocityConnection((Player) event.getTarget(), event.getData());
        getProxy().getScheduler().buildTask(this, connection::process).schedule();
    }
}
