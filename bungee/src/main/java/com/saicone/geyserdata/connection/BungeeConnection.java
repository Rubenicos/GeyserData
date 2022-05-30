package com.saicone.geyserdata.connection;

import com.saicone.geyserdata.GeyserDataBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BungeeConnection extends DownstreamConnection<ProxiedPlayer> {

    private final Server server;

    public BungeeConnection(Server server, ProxiedPlayer player, byte[] data) {
        super(player, data);
        this.server = server;
    }

    @Override
    protected String getPlayerName() {
        return player.getName();
    }

    @Override
    protected List<String> getPlayerList(String server) {
        if (server.equals("ALL")) {
            List<String> players = new ArrayList<>();
            for (Map.Entry<String, ServerInfo> entry : GeyserDataBungee.get().getProxy().getServers().entrySet()) {
                players.addAll(getPlayersNames(entry.getValue()));
            }
            return players;
        } else {
            return getPlayersNames(GeyserDataBungee.get().getProxy().getServerInfo(server));
        }
    }

    private List<String> getPlayersNames(ServerInfo server) {
        List<String> players = new ArrayList<>();
        if (server != null) {
            for (ProxiedPlayer player : server.getPlayers()) {
                players.add(player.getName());
            }
        }
        return players;
    }

    @Override
    protected void sendResponse(byte[] data) {
        if (data.length < 1 || server == null) {
            return;
        }
        server.sendData(GeyserDataBungee.CHANNEL, data);
    }
}
