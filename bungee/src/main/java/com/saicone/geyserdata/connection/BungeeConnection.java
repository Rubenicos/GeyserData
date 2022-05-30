package com.saicone.geyserdata.connection;

import com.saicone.geyserdata.GeyserDataBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BungeeConnection extends DownstreamConnection<ProxiedPlayer> {

    public BungeeConnection(ProxiedPlayer player, byte[] data) {
        super(player, data);
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
        player.getServer().sendData(GeyserDataBungee.CHANNEL, data);
    }
}
