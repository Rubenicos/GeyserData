package com.saicone.geyserdata.connection;

import com.saicone.geyserdata.GeyserDataVelocity;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VelocityConnection extends DownstreamConnection<Player> {

    public VelocityConnection(Player player, byte[] data) {
        super(player, data);
    }

    @Override
    protected String getPlayerName() {
        return player.getUsername();
    }

    @Override
    protected List<String> getPlayerList(String server) {
        if (server.equals("ALL")) {
            List<String> players = new ArrayList<>();
            for (Player player : GeyserDataVelocity.get().getProxy().getAllPlayers()) {
                players.add(player.getUsername());
            }
            return players;
        } else {
            return GeyserDataVelocity.get().getProxy().getServer(server).map(this::getPlayersNames).orElse(Collections.emptyList());
        }
    }

    private List<String> getPlayersNames(RegisteredServer server) {
        List<String> players = new ArrayList<>();
        for (Player player : server.getPlayersConnected()) {
            players.add(player.getUsername());
        }
        return players;
    }

    @Override
    protected void sendResponse(byte[] data) {
        player.getCurrentServer().ifPresent(con -> con.getServer().sendPluginMessage(GeyserDataVelocity.CHANNEL, data));
    }
}
